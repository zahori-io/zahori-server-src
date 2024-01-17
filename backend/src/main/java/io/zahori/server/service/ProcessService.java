package io.zahori.server.service;

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
