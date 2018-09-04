package com.endava.dashboard.bitbucket.responseobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Branch {

    private String id;
    private String displayId;
    private String type;
    private String latestCommit;
    private String latestChangeset;
    private boolean isDefault;

}
