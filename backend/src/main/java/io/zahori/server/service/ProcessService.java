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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.zahori.server.model.Client;
import io.zahori.server.model.ClientTeam;
import io.zahori.server.model.ClientTeamPK;
import io.zahori.server.model.Process;
import io.zahori.server.model.ProcessRegistration;
import io.zahori.server.model.ProcessType;
import io.zahori.server.repository.ProcessesRepository;

/**
 * The type Process service.
 */
@Service
public class ProcessService {

    private ProcessesRepository processRepository;

    /**
     * Instantiates a new Process service.
     *
     * @param processRepository the process repository
     */
    @Autowired
    public ProcessService(ProcessesRepository processRepository) {
        this.processRepository = processRepository;
    }

    /**
     * Find all iterable.
     *
     * @return the iterable
     */
    public Iterable<Process> findAll() {
        return processRepository.findAll();
    }

    /**
     * Find process registered process.
     *
     * @param processRegistration the process registration
     * @return the process
     */
    public Process findProcessRegistered(ProcessRegistration processRegistration) {
        return processRepository.findByNameAndClientIdAndTeamId(processRegistration.getName(), processRegistration.getClientId(),
                processRegistration.getTeamId());
    }

    /**
     * Register process.
     *
     * @param processRegistration the process registration
     * @return the process
     */
    public Process register(ProcessRegistration processRegistration) {
        Process process = new Process();
        process.setName(processRegistration.getName());

        Client client = new Client();
        client.setClientId(processRegistration.getClientId());
        process.setClient(client);

        ClientTeamPK teamPK = new ClientTeamPK();
        teamPK.setClientId(processRegistration.getClientId());
        teamPK.setTeamId(processRegistration.getTeamId());

        ClientTeam clientTeam = new ClientTeam();
        clientTeam.setId(teamPK);
        process.setClientTeam(clientTeam);

        ProcessType processType = new ProcessType();
        processType.setProcTypeId(processRegistration.getProcTypeId());
        process.setProcessType(processType);

        return processRepository.save(process);
    }
}
