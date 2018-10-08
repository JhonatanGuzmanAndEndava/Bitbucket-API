package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.responseobjects.PullRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PullRequestService {

    void savePullRequest(PullRequest pullRequest);

    ResponseEntity<Void> savePullRequestsList(List<PullRequest> pullRequestList);

}
