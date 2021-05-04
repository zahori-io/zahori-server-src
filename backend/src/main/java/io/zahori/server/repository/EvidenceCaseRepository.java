package io.zahori.server.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import io.zahori.server.model.EvidenceCase;


@RepositoryRestResource(path = "evidenceCases")
public interface EvidenceCaseRepository extends CrudRepository<EvidenceCase, Long> {

}

