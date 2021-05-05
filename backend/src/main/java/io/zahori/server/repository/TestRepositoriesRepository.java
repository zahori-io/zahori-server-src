package io.zahori.server.repository;

import io.zahori.server.model.TestRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * The interface Configuration repository.
 */
//@Repository
@RepositoryRestResource(path = "test_repo")
public interface TestRepositoriesRepository extends CrudRepository<TestRepository, Long> {
}
