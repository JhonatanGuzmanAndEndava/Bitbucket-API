package com.endava.dashboard.bitbucket.controller;

import com.endava.dashboard.bitbucket.responseobjects.Commit;
import com.endava.dashboard.bitbucket.responseobjects.Project;
import com.endava.dashboard.bitbucket.responseobjects.PullRequest;
import com.endava.dashboard.bitbucket.responseobjects.Repository;
import com.endava.dashboard.bitbucket.services.CommitService;
import com.endava.dashboard.bitbucket.services.PullRequestService;
import com.endava.dashboard.bitbucket.services.RepositoryService;
import com.endava.dashboard.bitbucket.settings.BitbucketConf;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/v2")
public class BitbucketController {

    private RepositoryService repositoryService;
    private CommitService commitService;
    private PullRequestService pullRequestService;

    @Autowired
    public BitbucketController(RepositoryService repositoryService, CommitService commitService, PullRequestService pullRequestService) {
        this.repositoryService = repositoryService;
        this.commitService = commitService;
        this.pullRequestService = pullRequestService;
    }

    private HttpEntity basicCredentials() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic "+ BitbucketConf.base64Credentials);
        return new HttpEntity<>(headers);
    }

    public List<Commit> getCommits(String project, String repo, Long projectId, Long repositoryId) {

        URI uri = URI.create(BitbucketConf.URL + "/projects/"+project+"/repos/"+repo+"/commits?limit=10&permission=REPO_WRITE");

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> s;

        List<Commit> commitList;

        try {
            s = rest.exchange(uri, HttpMethod.GET, basicCredentials(), String.class);
        }catch(HttpClientErrorException e) {
            if(e.getRawStatusCode() == 401)
                System.err.println("Unauthenticated user: " + e.getMessage());
            else
                System.err.println("Something is wrong!" + e.getMessage());

            return null;
        }

        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(s.getBody());
        } catch (ParseException e) {
            System.err.println("Error reading JSON String");
            return null;
        }

        JSONArray ja;

        commitList = new ArrayList<>();

        ja = (JSONArray) jsonObject.get("values");

        for (Object item : ja) {

            JSONObject jo = (JSONObject) item;

            String id = (String) jo.get("id");
            String displayId = (String) jo.get("displayId");
            String author = (String)((JSONObject)jo.get("author")).get("name");
            String authorEmail = (String)((JSONObject)jo.get("author")).get("emailAddress");
            Long committerTimestamp = (Long) jo.get("committerTimestamp");
            String message = (String) jo.get("message");

            Commit commit = new Commit(id,displayId,projectId,repositoryId,author,authorEmail,committerTimestamp,message);

            commitList.add(commit);
        }

        return commitList;
    }

    public List<PullRequest> getPullRequests(String project, String repo, Long projectId, Long repositoryId) {

        URI uri = URI.create(BitbucketConf.URL + "/projects/"+project+"/repos/"+repo+"/pull-requests?limit=10&state=ALL&role=AUTHOR");

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> s;

        List<PullRequest> pullRequestList;

        try {
            s = rest.exchange(uri, HttpMethod.GET, basicCredentials(), String.class);
        }catch(HttpClientErrorException e) {
            if(e.getRawStatusCode() == 401)
                System.err.println("Unauthenticated user: " + e.getMessage());
            else
                System.err.println("Something is wrong!" + e.getMessage());

            return null;
        }

        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(s.getBody());
        } catch (ParseException e) {
            System.err.println("Error reading JSON String");
            return null;
        }

        JSONArray ja;
        pullRequestList = new ArrayList<>();

        ja = (JSONArray) jsonObject.get("values");

        for (Object item : ja) {

            JSONObject jo = (JSONObject) item;

            Long id = (Long) jo.get("id");
            String title = (String) jo.get("title");
            String state = (String) jo.get("state");
            Long createdDateTimestamp = (Long) jo.get("createdDate");
            Long updatedDateTimestamp = (Long) jo.get("updatedDate");
            String fromBranch = (String)((JSONObject) jo.get("fromRef")).get("id");
            String toBranch = (String)((JSONObject) jo.get("toRef")).get("id");
            String repository = (String)((JSONObject)((JSONObject) jo.get("toRef")).get("repository")).get("slug");
            String author = (String)((JSONObject)((JSONObject) jo.get("author")).get("user")).get("name");
            String link = (String)((JSONObject)((JSONArray)((JSONObject) jo.get("links")).get("self")).get(0)).get("href");

            PullRequest pullRequest = new PullRequest(id,projectId,repositoryId,title,state,createdDateTimestamp,updatedDateTimestamp,
                    fromBranch,toBranch,repository,author,link);

            pullRequestList.add(pullRequest);

        }

        return pullRequestList;
    }

    @GetMapping(path = "/{projectSlug}/repos/{repositorySlug}")
    @ResponseBody
    public ResponseEntity<Void> addRepository(@PathVariable("projectSlug") String projectSlug,
                                              @PathVariable("repositorySlug") String repositorySlug,
                                              @RequestParam(value = "branch",required = false) String branch) {

        URI uriProject = URI.create(BitbucketConf.URL + "/projects/"+projectSlug);
        URI uriRepository = URI.create(BitbucketConf.URL + "/projects/"+projectSlug+"/repos/"+repositorySlug);

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> projectResponse;
        ResponseEntity<String> repositoryResponse;

        Project project;
        Repository repository;

        try {
            projectResponse = rest.exchange(uriProject, HttpMethod.GET, basicCredentials(), String.class);
            repositoryResponse = rest.exchange(uriRepository, HttpMethod.GET, basicCredentials(), String.class);
        }catch(HttpClientErrorException e) {
            if(e.getRawStatusCode() == 401) {
                System.err.println("Unauthenticated user or unauthorized: " + e.getMessage());
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }else {
                System.err.println("Something is wrong!" + e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }

        JSONObject jsonProjectObject;
        JSONObject jsonRepositoryObject;

        try {
            jsonProjectObject = (JSONObject) new JSONParser().parse(projectResponse.getBody());
            jsonRepositoryObject = (JSONObject) new JSONParser().parse(repositoryResponse.getBody());
        } catch (ParseException e) {
            System.err.println("Error reading JSON String");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //Project
        Long id = (Long) jsonProjectObject.get("id");
        String key = (String) jsonProjectObject.get("key");
        String name = (String) jsonProjectObject.get("name");
        String description = (String) jsonProjectObject.get("description");
        boolean isPublic = (boolean) jsonProjectObject.get("public");
        String type = (String) jsonProjectObject.get("type");
        String link = (String)((JSONObject)((JSONArray)((JSONObject) jsonProjectObject.get("links")).get("self")).get(0)).get("href");

        project = new Project(id,key,name,description,isPublic,type,link);

        //Repository
        Long idRepository = (Long) jsonRepositoryObject.get("id");
        Long projectId = (Long) ((JSONObject)jsonRepositoryObject.get("project")).get("id");
        String slug = (String) jsonRepositoryObject.get("slug");
        String nameRepository = (String) jsonRepositoryObject.get("name");
        String state = (String) jsonRepositoryObject.get("state");
        boolean isPublicRepository = (boolean) jsonRepositoryObject.get("public");
        String linkRepository = (String)((JSONObject)((JSONArray)((JSONObject) jsonRepositoryObject.get("links")).get("self")).get(0)).get("href");

        repository = new Repository(idRepository,projectId,slug,nameRepository,state,isPublicRepository,linkRepository);

        ResponseEntity<Void> responseEntity = repositoryService.addRepository(project,repository);

        if(responseEntity.getStatusCode().value() == 201) {
            commitService.saveCommits(getCommits(projectSlug, repositorySlug, projectId, idRepository));
            pullRequestService.savePullRequestsList(getPullRequests(projectSlug, repositorySlug, projectId, idRepository));
            return new ResponseEntity<>(HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
