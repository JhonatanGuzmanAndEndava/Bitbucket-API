package com.endava.dashboard.bitbucket.repositories;

import com.endava.dashboard.bitbucket.responseobjects.Commit;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface CommitRepository extends CrudRepository<Commit, String> {

    void deleteByProjectIdAndRepositoryId(Long projectId, Long repositoryId);

}
