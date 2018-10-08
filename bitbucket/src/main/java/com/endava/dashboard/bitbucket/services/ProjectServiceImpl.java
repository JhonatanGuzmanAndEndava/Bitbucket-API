package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.repositories.ProjectRepository;
import com.endava.dashboard.bitbucket.responseobjects.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

    private ProjectRepository projectRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public ResponseEntity<Void> addProject(Project theProject) {
        projectRepository.save(theProject);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public Optional<Project> getProjectByKey(String projectKey) {
        return projectRepository.findProjectByKeyProject(projectKey);
    }
}
