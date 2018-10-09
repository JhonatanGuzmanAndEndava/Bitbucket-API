package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.responseobjects.Branch;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface BranchService {

    ResponseEntity<Void> addTheBranch(Branch branch);

    Optional<Branch> getCurrentBranchForProjectAndRepository(Long projectId, Long repositoryId);

}
