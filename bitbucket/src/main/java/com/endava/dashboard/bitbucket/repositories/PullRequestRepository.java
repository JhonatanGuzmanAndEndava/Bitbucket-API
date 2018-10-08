package com.endava.dashboard.bitbucket.repositories;

import com.endava.dashboard.bitbucket.responseobjects.PullRequest;
import org.springframework.data.repository.CrudRepository;

public interface PullRequestRepository extends CrudRepository<PullRequest, Long> {

}
