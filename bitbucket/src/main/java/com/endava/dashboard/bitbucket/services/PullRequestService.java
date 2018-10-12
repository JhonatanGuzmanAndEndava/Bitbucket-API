package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.responseobjects.PullRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PullRequestService {

    ResponseEntity<Void> savePullRequestsList(List<PullRequest> pullRequestList);

    ResponseEntity<Void> deleteAllPullRequests();

    ResponseEntity<Void> deletePullRequestsByProjectIdAndRepositoryId(Long projectId, Long repositoryId);
}
