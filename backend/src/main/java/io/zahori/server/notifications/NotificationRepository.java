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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {

    List<Notification> findByAccountId(long accountId);

    @Query("select n from Notification n, NotificationEvent ne, NotificationMedia nm, Account a, Client cli, Process p where n.account.id = a.id and a.client.clientId = cli.clientId and cli.clientId = p.client.clientId and p.processId = :processId and n.event.notificationEventId = ne.notificationEventId and n.media.notificationMediaId = nm.notificationMediaId and n.active = true and LOWER(ne.name) = LOWER(:eventName)")
    List<Notification> findActiveNotificationes(@Param("eventName") String eventName, @Param("processId") long processId);

}
