package com.endava.dashboard.bitbucket.responseobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Commit {

    private String id;
    private String displayId;
    private Long projectId;
    private Long repositoryId;
    private String author;
    private String authorEmail;
    private Long committerTimestamp;
    private String message;

}
