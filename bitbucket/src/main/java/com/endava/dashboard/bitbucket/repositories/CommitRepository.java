package com.endava.dashboard.bitbucket.repositories;

import com.endava.dashboard.bitbucket.responseobjects.Commit;
import org.springframework.data.repository.CrudRepository;

public interface CommitRepository extends CrudRepository<Commit, String> {

}
