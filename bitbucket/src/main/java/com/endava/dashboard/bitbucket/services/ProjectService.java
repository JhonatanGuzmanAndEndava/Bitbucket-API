package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.responseobjects.Project;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface ProjectService {

    ResponseEntity<Void> addProject(Project theProject);

    Optional<Project> getProjectByKey(String projectKey);

}
