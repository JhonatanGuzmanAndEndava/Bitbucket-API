package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.repositories.BranchRepository;
import com.endava.dashboard.bitbucket.responseobjects.Branch;
import com.google.common.annotations.Beta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BranchServiceImpl implements BranchService {

    private BranchRepository branchRepository;

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public ResponseEntity<Void> addTheBranch(Branch branch) {
        branchRepository.save(branch);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public Optional<Branch> getCurrentBranchForProjectAndRepository(Long projectId, Long repositoryId) {
        return branchRepository.findBranchByProjectIdAndRepositoryId(projectId, repositoryId);
    }
}
