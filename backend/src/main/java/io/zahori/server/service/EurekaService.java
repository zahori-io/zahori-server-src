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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;

import io.zahori.server.model.Process;

/**
 * The type Eureka service.
 */
@Service
public class EurekaService {

    private static final Logger LOG = LoggerFactory.getLogger(EurekaService.class);
    private static final String SERVICE_ID_SEPARATOR = ":";

    @Autowired
    private LoadBalancerClient loadBalancer;

    private EurekaService() {
    }

    /**
     * Gets process url.
     *
     * @param process the process
     * @return the process url
     */
    public String getProcessUrl(Process process) {
        String serviceId = process.getClient().getClientId() + SERVICE_ID_SEPARATOR + process.getClientTeam().getId().getTeamId() + SERVICE_ID_SEPARATOR
                + process.getName();

        String processUrl = "";
        ServiceInstance serviceInstance = loadBalancer.choose(serviceId);
        if (serviceInstance != null) {
            processUrl = serviceInstance.getUri().toString();
        }

        LOG.info("getUrl for process: " + serviceId + " -> '" + processUrl + "'");
        return processUrl;
    }
}
