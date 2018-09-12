package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.responseobjects.PullRequest;

public interface PullRequestService extends InfluxDBConnection {

    void savePullRequest(PullRequest pullRequest);


}
