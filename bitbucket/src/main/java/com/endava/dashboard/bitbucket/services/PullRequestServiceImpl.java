package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.repositories.PullRequestRepository;
import com.endava.dashboard.bitbucket.responseobjects.PullRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PullRequestServiceImpl implements PullRequestService {

    private PullRequestRepository pullRequestRepository;

    @Autowired
    public PullRequestServiceImpl(PullRequestRepository pullRequestRepository) {
        this.pullRequestRepository = pullRequestRepository;
    }

    @Override
    public ResponseEntity<Void> savePullRequestsList(List<PullRequest> pullRequestList) {
        pullRequestRepository.saveAll(pullRequestList);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteAllPullRequests() {
        pullRequestRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> deletePullRequestsByProjectIdAndRepositoryId(Long projectId, Long repositoryId) {
        pullRequestRepository.deleteByProjectIdAndRepositoryId(projectId, repositoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
