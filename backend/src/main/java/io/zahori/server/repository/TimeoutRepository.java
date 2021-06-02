package io.zahori.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.zahori.server.model.Timeout;

//@RepositoryRestResource(path = "timeouts")
@Repository
public interface TimeoutRepository extends CrudRepository<Timeout, Long> {
}