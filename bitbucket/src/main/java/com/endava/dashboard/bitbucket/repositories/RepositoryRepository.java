package com.endava.dashboard.bitbucket.repositories;

import com.endava.dashboard.bitbucket.responseobjects.Repository;
import org.springframework.data.repository.CrudRepository;

public interface RepositoryRepository extends CrudRepository<Repository, Long> {

}
