package io.zahori.server.email;

import org.apache.commons.lang3.StringUtils;

/*-
 * #%L
 * zahori-server
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2021 - 2023 PANEL SISTEMAS INFORMATICOS,S.L
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
public class Email {

    private String recipient;
    private String subject;
    private String msgText;
    private String msgHtml;
    private String attachment;

    public Email() {
    }

    public Email(String recipient, String subject, String msgText) {
        this.recipient = recipient;
        this.subject = subject;
        this.msgText = msgText;
    }

    public Email(String recipient, String subject, String msgText, String msgHtml) {
        this.recipient = recipient;
        this.subject = subject;
        this.msgText = msgText;
        this.msgHtml = msgHtml;
    }

    public Email(String recipient, String subject, String msgText, String msgHtml, String attachment) {
        this.recipient = recipient;
        this.subject = subject;
        this.msgText = msgText;
        this.msgHtml = msgHtml;
        this.attachment = attachment;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public String getMsgHtml() {
        return msgHtml;
    }

    public void setMsgHtml(String msgHtml) {
        this.msgHtml = msgHtml;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public boolean hasMsgText() {
        return StringUtils.isNotBlank(msgText);
    }

    public boolean hasMsgHtml() {
        return StringUtils.isNotBlank(msgHtml);
    }

}
