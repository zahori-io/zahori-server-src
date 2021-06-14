package io.zahori.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.zahori.server.model.Retry;

/**
 * The interface retries repository.
 */
@Repository
public interface RetriesRepository extends CrudRepository<Retry, Integer> {

}