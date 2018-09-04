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
    private String title;
    private String description;
    private String state;
    private Long createdDateTimestamp;
    private Long updatedDateTimestamp;
    private String fromBranch;
    private String toBranch;
    private String repository;
    private String author;
    private String authorEmail;
    private String link;


}
