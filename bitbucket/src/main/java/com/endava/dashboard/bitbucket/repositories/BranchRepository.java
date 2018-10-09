package com.endava.dashboard.bitbucket.repositories;

import com.endava.dashboard.bitbucket.responseobjects.Branch;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BranchRepository extends CrudRepository<Branch, String> {

    Optional<Branch> findBranchByProjectIdAndRepositoryId(Long projectId, Long repositoryId);
}
