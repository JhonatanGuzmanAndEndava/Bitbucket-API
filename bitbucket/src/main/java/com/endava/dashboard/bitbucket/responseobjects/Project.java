package com.endava.dashboard.bitbucket.responseobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Project {

    private Long id;
    private String key;
    private String name;
    private String description;
    private boolean isPublic;
    private String type;
    private String link;

}
