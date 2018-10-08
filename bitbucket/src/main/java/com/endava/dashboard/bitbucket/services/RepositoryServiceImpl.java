package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.repositories.RepositoryRepository;
import com.endava.dashboard.bitbucket.responseobjects.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class RepositoryServiceImpl implements RepositoryService {

    private RepositoryRepository repositoryRepository;

    @Autowired
    public RepositoryServiceImpl(RepositoryRepository repositoryRepository) {
        this.repositoryRepository = repositoryRepository;
    }

    @Override
    public ResponseEntity<Void> addRepository(Repository theRepository) {
        repositoryRepository.save(theRepository);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public Optional<Repository> getRepositoryBySlug(String repositorySlug) {
        return repositoryRepository.findRepositoryBySlug(repositorySlug);
    }

    @Override
    public Iterable<Repository> getAllRepositoriesByProjectId(Long projectId) {
        return repositoryRepository.findAllByProjectId(projectId);
    }
}
