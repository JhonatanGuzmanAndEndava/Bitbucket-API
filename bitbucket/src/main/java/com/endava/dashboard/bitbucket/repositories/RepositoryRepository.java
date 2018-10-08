package com.endava.dashboard.bitbucket.repositories;

import com.endava.dashboard.bitbucket.responseobjects.Repository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RepositoryRepository extends CrudRepository<Repository, Long> {

    Optional<Repository> findRepositoryBySlug(String slug);

}
