package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.responseobjects.Repository;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface RepositoryService {

    ResponseEntity<Void> addRepository(Repository theRepository);

    Optional<Repository> getRepositoryBySlug(String repositorySlug);

    Iterable<Repository> getAllRepositoriesByProjectId(Long projectId);

}
