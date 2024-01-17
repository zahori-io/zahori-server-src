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
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification_media")
public class NotificationMedia {

    @Id
    @Column(name = "notification_media_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationMediaId;
    private String name;

    public NotificationMedia() {
    }

    @Override
    public String toString() {
        return "NotificationMedia{" + "notificationMediaId=" + notificationMediaId + ", name=" + name + '}';
    }

    public Long getNotificationMediaId() {
        return notificationMediaId;
    }

    public void setNotificationMediaId(Long notificationMediaId) {
        this.notificationMediaId = notificationMediaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
