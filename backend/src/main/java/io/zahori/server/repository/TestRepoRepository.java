package io.zahori.server.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import io.zahori.server.model.TestRepository;

@RepositoryRestResource(path = "testRepositories")
public interface TestRepoRepository extends CrudRepository<TestRepository, Long>{

}