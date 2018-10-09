package com.endava.dashboard.bitbucket.responseobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@AllArgsConstructor
@ToString
@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long projectId;
    private Long repositoryId;
    private String displayId;

    public Branch(Long projectId, Long repositoryId, String displayId) {
        this.projectId = projectId;
        this.repositoryId = repositoryId;
        this.displayId = displayId;
    }
}
