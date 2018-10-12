package com.endava.dashboard.bitbucket.repositories;

import com.endava.dashboard.bitbucket.responseobjects.PullRequest;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface PullRequestRepository extends CrudRepository<PullRequest, Long> {

    void deleteByProjectIdAndRepositoryId(Long projectId, Long repositoryId);

}
