package com.endava.dashboard.bitbucket.responseobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Repository {

    private Long id;
    //private Long projectId;
    private String slug;
    private String name;
    private String state;
    private boolean isPublic;
    private String link;

}
