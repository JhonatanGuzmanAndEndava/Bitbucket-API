package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.repositories.CommitRepository;
import com.endava.dashboard.bitbucket.responseobjects.Commit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommitServiceImpl implements CommitService {

    private CommitRepository commitRepository;

    @Autowired
    public CommitServiceImpl(CommitRepository commitRepository) {
        this.commitRepository = commitRepository;
    }

    @Override
    public ResponseEntity<Void> saveCommits(List<Commit> commits) {
        commitRepository.saveAll(commits);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
