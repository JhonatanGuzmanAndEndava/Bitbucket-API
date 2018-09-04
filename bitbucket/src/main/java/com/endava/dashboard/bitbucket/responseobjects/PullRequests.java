package com.endava.dashboard.bitbucket.responseobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class PullRequests {

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
