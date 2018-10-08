package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.responseobjects.Commit;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CommitService {

    ResponseEntity<Void> saveCommits(List<Commit> commits);

}
