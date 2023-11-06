package io.zahori.server.notifications;

/*-
 * #%L
 * zahori-server
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2021 PANEL SISTEMAS INFORMATICOS,S.L
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
import io.zahori.server.exception.BadRequestException;
import io.zahori.server.model.Execution;
import io.zahori.server.model.Process;
import io.zahori.server.security.Account;
import io.zahori.server.security.AccountRepository;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationsService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationsService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationEventRepository notificationEventRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private NotificationSenderFactory notificationSenderFactory;

    public List<Notification> findByAccountId() {
        Account account = getAccount();
        return notificationRepository.findByAccountId(account.getId());
    }

    @Transactional
    public Iterable<Notification> saveAll(List<Notification> notifications) {
        Account account = getAccount();
        List<Notification> notificationsToDelete = new ArrayList<>();

        Iterator<Notification> iterator = notifications.iterator();
        while (iterator.hasNext()) {
            Notification notification = iterator.next();
            notification.setAccount(account);

            if (!notification.isActive()) {
                notificationsToDelete.add(notification);
                iterator.remove();
            }
        }

        Iterable<Notification> activeNotifications = notificationRepository.saveAll(notifications);
        notificationRepository.deleteAll(notificationsToDelete);

        return activeNotifications;
    }

    @Async
    public void sendExecutionNotification(Process process, Execution execution) {
        String event = "execution." + execution.getTrigger() + "." + execution.getStatus();
        LOG.info("Send notifications for event {}", event);

        List<Notification> notifications = notificationRepository.findActiveNotificationes(event, process.getProcessId());
        for (Notification notification : notifications) {
            try {
                NotificationSender sender = notificationSenderFactory.getSender(notification.getMedia());
                sender.sendNotification(notification, execution);
            } catch (Exception e) {
                LOG.error("Error sending notification: {}", e.getMessage());
            }
        }
    }

    private Account getAccount() {
        try {
            UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            return accountRepository.findByUsername(authentication.getPrincipal().toString());
        } catch (Exception e) {
            throw new BadRequestException("Invalid session");
        }
    }
}
