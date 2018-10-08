package com.endava.dashboard.bitbucket.repositories;

import com.endava.dashboard.bitbucket.responseobjects.Project;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProjectRepository extends CrudRepository<Project, Long> {

    Optional<Project> findProjectByKeyProject(String key);

}
