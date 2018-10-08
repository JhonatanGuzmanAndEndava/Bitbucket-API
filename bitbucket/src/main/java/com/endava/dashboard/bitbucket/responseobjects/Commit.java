package com.endava.dashboard.bitbucket.responseobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "commits")
public class Commit {

    @Id
    private String id;
    private String displayId;
    private Long projectId;
    private Long repositoryId;
    private String author;
    private String authorEmail;
    private LocalDateTime committerTimestamp;
    @Column(columnDefinition="TEXT")
    private String message;

}
