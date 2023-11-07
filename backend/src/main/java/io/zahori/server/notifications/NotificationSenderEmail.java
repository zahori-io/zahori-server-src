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
import io.zahori.server.email.Email;
import io.zahori.server.email.EmailService;
import io.zahori.server.model.CaseExecution;
import io.zahori.server.model.Execution;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component("emailNotificationSender")
public class NotificationSenderEmail implements NotificationSender {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationSenderEmail.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void sendNotification(Notification notification, Execution execution) {
        if (!emailService.isServiceEnabled()) {
            return;
        }

        if (notification.getAccount() == null || StringUtils.isBlank(notification.getAccount().getEmail())) {
            return;
        }

        LOG.info("Send email notification");

        Locale locale = new Locale(notification.getAccount().getLanguage().getLanguageCode());
        Context context = new Context(locale);

        context.setVariable("locale", locale);
        context.setVariable("execution", execution);
        context.setVariable("uniqueResolutions", getUniqueResolutions(execution.getCasesExecutions()));
        context.setVariable("uniqueBrowsers", getUniqueBrowsers(execution.getCasesExecutions()));

        List<List<CaseExecution>> caseGroups = createCaseGroups(execution.getCasesExecutions());
        context.setVariable("caseGroups", caseGroups);

        String emailContentHtml = templateEngine.process("execution-report.html", context);

        Email email = new Email();
        email.setSubject("[Zahori] Execution " + execution.getStatus() + ": " + execution.getName());
        email.setRecipient(notification.getAccount().getEmail());
        email.setMsgText(emailContentHtml);
        email.setMsgHtml(emailContentHtml);

        emailService.send(email);
    }

    public List<List<CaseExecution>> createCaseGroups(List<CaseExecution> caseExecutions) {
        Map<String, List<CaseExecution>> caseGroupsMap = new HashMap<>();

        for (CaseExecution caseExecution : caseExecutions) {
            String caseName = caseExecution.getCas().getName();
            if (!caseGroupsMap.containsKey(caseName)) {
                caseGroupsMap.put(caseName, new ArrayList<>());
            }
            caseGroupsMap.get(caseName).add(caseExecution);
        }

        return new ArrayList<>(caseGroupsMap.values());
    }

    public Set<String> getUniqueResolutions(List<CaseExecution> caseExecutions) {
        Set<String> uniqueResolutions = new HashSet<>();
        for (CaseExecution caseExecution : caseExecutions) {
            uniqueResolutions.add(caseExecution.getScreenResolution());
        }
        return uniqueResolutions;
    }

    public Set<String> getUniqueBrowsers(List<CaseExecution> caseExecutions) {
        Set<String> uniqueBrowsers = new HashSet<>();
        for (CaseExecution caseExecution : caseExecutions) {
            uniqueBrowsers.add(caseExecution.getBrowser().getBrowserName());
        }
        return uniqueBrowsers;
    }

}
