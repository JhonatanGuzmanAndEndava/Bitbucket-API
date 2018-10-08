package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.responseobjects.Project;
import org.springframework.http.ResponseEntity;

public interface ProjectService extends InfluxDBConnection {

    ResponseEntity<Void> addProject(Project theProject);

    ResponseEntity<Project> getProjectByKey(String projectKey);

}
