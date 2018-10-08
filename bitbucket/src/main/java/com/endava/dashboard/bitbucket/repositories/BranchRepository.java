package com.endava.dashboard.bitbucket.repositories;

import com.endava.dashboard.bitbucket.responseobjects.Branch;
import org.springframework.data.repository.CrudRepository;

public interface BranchRepository extends CrudRepository<Branch, String> {

}
