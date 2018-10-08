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
@Table(name = "projects")
public class Project {

    @Id
    private Long id;
    private String keyProject;
    private String name;
    private String description;
    private boolean isPublic;
    private String type;
    private String link;

}
