package com.endava.dashboard.bitbucket.parse;

import com.endava.dashboard.bitbucket.responseobjects.Commit;
import com.endava.dashboard.bitbucket.responseobjects.Project;
import com.endava.dashboard.bitbucket.responseobjects.PullRequest;
import com.endava.dashboard.bitbucket.responseobjects.Repository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class ParsePojo {

    public static Project getProjectFromJsonObject(JSONObject jsonProjectObject){

        Long id = (Long) jsonProjectObject.get("id");
        String key = (String) jsonProjectObject.get("key");
        String name = (String) jsonProjectObject.get("name");
        String description = (String) jsonProjectObject.get("description");
        boolean isPublic = (boolean) jsonProjectObject.get("public");
        String type = (String) jsonProjectObject.get("type");
        String link = (String)((JSONObject)((JSONArray)((JSONObject) jsonProjectObject.get("links")).get("self")).get(0)).get("href");

        return new Project(id,key,name,description,isPublic,type,link);
    }

    public static Repository getRepositoryFromJsonObject(JSONObject jsonRepositoryObject) {

        Long idRepository = (Long) jsonRepositoryObject.get("id");
        Long projectId = (Long) ((JSONObject)jsonRepositoryObject.get("project")).get("id");
        String slug = (String) jsonRepositoryObject.get("slug");
        String nameRepository = (String) jsonRepositoryObject.get("name");
        String state = (String) jsonRepositoryObject.get("state");
        boolean isPublicRepository = (boolean) jsonRepositoryObject.get("public");
        String linkRepository = (String)((JSONObject)((JSONArray)((JSONObject) jsonRepositoryObject.get("links")).get("self")).get(0)).get("href");

        return new Repository(idRepository,projectId,slug,nameRepository,state,isPublicRepository,linkRepository);
    }

    public static Commit getCommitFromJsonObject(Long projectId, Long repositoryId, JSONObject jsonCommitObject) {
        String id = (String) jsonCommitObject.get("id");
        String displayId = (String) jsonCommitObject.get("displayId");
        String author = (String)((JSONObject) jsonCommitObject.get("author")).get("name");
        String authorEmail = (String)((JSONObject) jsonCommitObject.get("author")).get("emailAddress");
        LocalDateTime committerTimestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(((Long) jsonCommitObject.get("committerTimestamp"))), TimeZone.getDefault().toZoneId());
        String message = (String) jsonCommitObject.get("message");

        return new Commit(id,displayId,projectId,repositoryId,author,authorEmail,committerTimestamp,message);
    }

    public static PullRequest getPullRequestFromJsonObject(Long projectId, Long repositoryId, JSONObject jsonPullRequestObject) {

        Long id = (Long) jsonPullRequestObject.get("id");
        String title = (String) jsonPullRequestObject.get("title");
        String state = (String) jsonPullRequestObject.get("state");
        LocalDateTime createdDateTimestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(((Long) jsonPullRequestObject.get("createdDate"))), TimeZone.getDefault().toZoneId());
        LocalDateTime updatedDateTimestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(((Long) jsonPullRequestObject.get("updatedDate"))), TimeZone.getDefault().toZoneId());
        String fromBranch = (String)((JSONObject) jsonPullRequestObject.get("fromRef")).get("id");
        String toBranch = (String)((JSONObject) jsonPullRequestObject.get("toRef")).get("id");
        String repository = (String)((JSONObject)((JSONObject) jsonPullRequestObject.get("toRef")).get("repository")).get("slug");
        String author = (String)((JSONObject)((JSONObject) jsonPullRequestObject.get("author")).get("user")).get("name");
        String link = (String)((JSONObject)((JSONArray)((JSONObject) jsonPullRequestObject.get("links")).get("self")).get(0)).get("href");

        return new PullRequest(id,projectId,repositoryId,title,state,createdDateTimestamp,updatedDateTimestamp,
                fromBranch,toBranch,repository,author,link);
    }

}
