package com.endava.dashboard.bitbucket.responseobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PullRequest {

    private Long id;
    private Long projectId;
    private Long repositoryId;
    private String title;
    private String state;
    private Long createdDateTimestamp;
    private Long updatedDateTimestamp;
    private String fromBranch;
    private String toBranch;
    private String repository;
    private String author;
    private String link;


}
