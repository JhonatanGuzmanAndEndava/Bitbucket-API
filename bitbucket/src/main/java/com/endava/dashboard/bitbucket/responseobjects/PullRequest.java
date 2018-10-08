package com.endava.dashboard.bitbucket.responseobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@ToString
@Entity
@Table(name = "pull_requests")
public class PullRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long pullRequestId;
    private Long projectId;
    private Long repositoryId;
    private String title;
    private String state;
    private LocalDateTime createdDateTimestamp;
    private LocalDateTime updatedDateTimestamp;
    private String fromBranch;
    private String toBranch;
    private String repository;
    private String author;
    private String link;

    public PullRequest(Long pullRequestId,
                       Long projectId,
                       Long repositoryId,
                       String title, String state,
                       LocalDateTime createdDateTimestamp,
                       LocalDateTime updatedDateTimestamp,
                       String fromBranch, String toBranch,
                       String repository, String author, String link) {
        this.pullRequestId = pullRequestId;
        this.projectId = projectId;
        this.repositoryId = repositoryId;
        this.title = title;
        this.state = state;
        this.createdDateTimestamp = createdDateTimestamp;
        this.updatedDateTimestamp = updatedDateTimestamp;
        this.fromBranch = fromBranch;
        this.toBranch = toBranch;
        this.repository = repository;
        this.author = author;
        this.link = link;
    }
}
