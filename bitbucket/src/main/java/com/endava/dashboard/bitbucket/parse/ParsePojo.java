package com.endava.dashboard.bitbucket.parse;

import com.endava.dashboard.bitbucket.responseobjects.Commit;
import com.endava.dashboard.bitbucket.responseobjects.Project;
import com.endava.dashboard.bitbucket.responseobjects.PullRequest;
import com.endava.dashboard.bitbucket.responseobjects.Repository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

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

    public static Project getProjectFromInfluxObject(List<Object> influxObject) {

        String description = (String) influxObject.get(2);
        Long id = ((Double) influxObject.get(3)).longValue();
        boolean isPublic = (boolean) influxObject.get(4);
        String key = (String) influxObject.get(5);
        String link = (String) influxObject.get(6);
        String name = (String) influxObject.get(7);
        String type = (String) influxObject.get(8);

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

    public static Repository getRepositoryFromInfluxObject(List<Object> influxObject) {

        Long idRepository = ((Double) influxObject.get(2)).longValue();
        boolean isPublicRepository = (boolean) influxObject.get(3);
        String linkRepository = (String) influxObject.get(4);
        String nameRepository = (String) influxObject.get(5);
        Long projectId = ((Double) influxObject.get(6)).longValue();
        String slug = (String) influxObject.get(7);
        String state = (String) influxObject.get(8);

        return new Repository(idRepository,projectId,slug,nameRepository,state,isPublicRepository,linkRepository);
    }

    public static Commit getCommitFromJsonObject(Long projectId, Long repositoryId, JSONObject jsonCommitObject) {
        String id = (String) jsonCommitObject.get("id");
        String displayId = (String) jsonCommitObject.get("displayId");
        String author = (String)((JSONObject) jsonCommitObject.get("author")).get("name");
        String authorEmail = (String)((JSONObject) jsonCommitObject.get("author")).get("emailAddress");
        Long committerTimestamp = (Long) jsonCommitObject.get("committerTimestamp");
        String message = (String) jsonCommitObject.get("message");

        return new Commit(id,displayId,projectId,repositoryId,author,authorEmail,committerTimestamp,message);
    }

    public static PullRequest getPullRequestFromJsonObject(Long projectId, Long repositoryId, JSONObject jsonPullRequestObject) {

        Long id = (Long) jsonPullRequestObject.get("id");
        String title = (String) jsonPullRequestObject.get("title");
        String state = (String) jsonPullRequestObject.get("state");
        Long createdDateTimestamp = (Long) jsonPullRequestObject.get("createdDate");
        Long updatedDateTimestamp = (Long) jsonPullRequestObject.get("updatedDate");
        String fromBranch = (String)((JSONObject) jsonPullRequestObject.get("fromRef")).get("id");
        String toBranch = (String)((JSONObject) jsonPullRequestObject.get("toRef")).get("id");
        String repository = (String)((JSONObject)((JSONObject) jsonPullRequestObject.get("toRef")).get("repository")).get("slug");
        String author = (String)((JSONObject)((JSONObject) jsonPullRequestObject.get("author")).get("user")).get("name");
        String link = (String)((JSONObject)((JSONArray)((JSONObject) jsonPullRequestObject.get("links")).get("self")).get(0)).get("href");

        return new PullRequest(id,projectId,repositoryId,title,state,createdDateTimestamp,updatedDateTimestamp,
                fromBranch,toBranch,repository,author,link);
    }

}
