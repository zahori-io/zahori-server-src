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
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

    @Autowired
    private NotificationsService notificationsService;

    @Autowired
    private NotificationEventRepository notificationEventRepository;

    @Autowired
    private NotificationMediaRepository notificationMediaRepository;

    @GetMapping
    public ResponseEntity<Object> getNotifications() {
        List<Notification> notifications = notificationsService.findByAccountId();
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> saveNotifications(@RequestBody List<Notification> notifications) {
        Iterable<Notification> notificationsSaved = notificationsService.saveAll(notifications);
        return ResponseEntity.ok(notificationsSaved);
    }

    @GetMapping("/events")
    public ResponseEntity<Object> getNotificationEvents() {
        Iterable<NotificationEvent> notifications = notificationEventRepository.findAll();
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/media")
    public ResponseEntity<Object> getNotificationMedia() {
        Iterable<NotificationMedia> notifications = notificationMediaRepository.findAll();
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

}
