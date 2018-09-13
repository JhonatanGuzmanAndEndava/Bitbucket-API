package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.responseobjects.Project;
import com.endava.dashboard.bitbucket.responseobjects.Repository;
import org.springframework.http.ResponseEntity;

public interface RepositoryService extends InfluxDBConnection {

    ResponseEntity<Void> addRepository(Project theProject, Repository theRepository);

}
