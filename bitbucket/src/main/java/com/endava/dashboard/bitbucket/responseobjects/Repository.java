package com.endava.dashboard.bitbucket.responseobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "repositories")
public class Repository {

    @Id
    private Long id;
    private Long projectId;
    private String slug;
    private String name;
    private String state;
    private boolean isPublic;
    private String link;

}
