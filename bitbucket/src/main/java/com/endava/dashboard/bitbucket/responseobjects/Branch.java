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
@Table(name = "branches")
public class Branch {

    @Id
    private String id;
    private String displayId;
    private String type;
    private String latestCommit;
    private String latestChangeset;
    private boolean isDefault;

}
