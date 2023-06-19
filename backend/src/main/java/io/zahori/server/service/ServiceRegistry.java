package io.zahori.server.service;

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
import io.zahori.server.model.Process;
import io.zahori.server.utils.StringHelper;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServiceRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistry.class);
    private static final String SERVICE_ID_SEPARATOR = "-";

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    public String getServiceId(Process process) {
        return formatToConsulServiceId(
                process.getName()
                + SERVICE_ID_SEPARATOR + process.getClient().getClientId()
                + SERVICE_ID_SEPARATOR + process.getClientTeam().getId().getTeamId());
    }

    public boolean isServiceRegistered(String serviceId) {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceId);
        if (serviceInstances == null) {
            return false;
        }

        LOG.info(serviceInstances.size() + " instances registered for process '" + serviceId + "'");
        return !serviceInstances.isEmpty();
    }

    public boolean isProcessRunning(Process process) {
        return isProcessRunning(getProcessUrl(process));
    }

    private boolean isProcessRunning(String url) {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    private String getProcessUrl(Process process) {
        String serviceId = formatToConsulServiceId(
                process.getName()
                + SERVICE_ID_SEPARATOR + process.getClient().getClientId()
                + SERVICE_ID_SEPARATOR + process.getClientTeam().getId().getTeamId());

        Optional<URI> serviceUri = discoveryClient.getInstances(serviceId)
                .stream().findFirst().map(si -> si.getUri());

        String processUrl = "";

        if (serviceUri.isPresent()) {
            processUrl = serviceUri.get().toString();
        }

        LOG.info("getUrl for process: " + serviceId + " -> '" + processUrl + "'");
        return processUrl;
    }

    /*
        Consul service ids:
        - must not be empty
        - must start with a letter,
        - must end with a letter or digit
        - and have as interior characters only letters, digits and hyphen
     */
    private String formatToConsulServiceId(String processName) {
        String processNameWithoutBlanks = StringHelper.replaceBlanksWithHyphens(processName);
        return StringHelper.removeDuplicatedHyphens(processNameWithoutBlanks);
    }

}
