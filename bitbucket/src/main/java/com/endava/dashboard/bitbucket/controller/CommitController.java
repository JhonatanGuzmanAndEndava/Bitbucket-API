package com.endava.dashboard.bitbucket.controller;

import com.endava.dashboard.bitbucket.responseobjects.*;
import com.endava.dashboard.bitbucket.services.PullRequestService;
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
@RequestMapping(path = "/")
public class CommitController {

    private PullRequestService pullRequestService;

    @Autowired
    public CommitController(PullRequestService pullRequestService) {
        this.pullRequestService = pullRequestService;
    }

    private HttpEntity basicCredentials() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic "+BitbucketConf.base64Credentials);
        return new HttpEntity<>(headers);
    }

    /**
     * It returns a list of projects for which the authenticated user has
     * the PROJECT_VIEW permissions
     */
    @GetMapping(path = "/projects")
    @ResponseBody
    public List<Project> projects() {

        URI uri = URI.create(BitbucketConf.URL + "/projects?limit=100");

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> s;
        List<Project> projectList;

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

        Long size = (Long) jsonObject.get("size");
        boolean isLastPage = (boolean) jsonObject.get("isLastPage");

        projectList = new ArrayList<>();

        ja = (JSONArray) jsonObject.get("values");

        for (Object item : ja) {

            JSONObject jo = (JSONObject) item;

            Long id = (Long) jo.get("id");
            String key = (String) jo.get("key");
            String name = (String) jo.get("name");
            String description = (String) jo.get("description");
            boolean isPublic = (boolean) jo.get("public");
            String type = (String) jo.get("type");
            String link = (String)((JSONObject)((JSONArray)((JSONObject) jo.get("links")).get("self")).get(0)).get("href");

            Project project = new Project(id,key,name,description,isPublic,type,link);

            projectList.add(project);

            System.out.println(project);
        }

        //Limit should be 100 at this point by default
        if(size >= (Long) jsonObject.get("limit") && !isLastPage) {
            System.err.println("Only displaying first 100 newest results");
        }
        return projectList;
    }

    /**
     * Into query params there are options for pull request approval, merged, closed and no approval
     * By default just getting first 100 results and author provided by credentials
     */
    @GetMapping(path = "/pullrequests")
    @ResponseBody
    public List<PullRequest> pullRequests() {

        URI uri = URI.create(BitbucketConf.URL + "/dashboard/pull-requests?limit=100&role=AUTHOR");

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

        Long size = (Long) jsonObject.get("size");
        boolean isLastPage = (boolean) jsonObject.get("isLastPage");

        pullRequestList = new ArrayList<>();

        ja = (JSONArray) jsonObject.get("values");

        for (Object item : ja) {

            JSONObject jo = (JSONObject) item;

            Long id = (Long) jo.get("id");
            String title = (String) jo.get("title");
            String description = (String) jo.get("title");
            String state = (String) jo.get("state");
            Long createdDateTimestamp = (Long) jo.get("createdDate");
            Long updatedDateTimestamp = (Long) jo.get("updatedDate");
            String fromBranch = (String)((JSONObject) jo.get("fromRef")).get("id");
            String toBranch = (String)((JSONObject) jo.get("toRef")).get("id");
            String repository = (String)((JSONObject)((JSONObject) jo.get("toRef")).get("repository")).get("slug");
            String author = (String)((JSONObject)((JSONObject) jo.get("author")).get("user")).get("name");
            String authorEmail = (String)((JSONObject)((JSONObject) jo.get("author")).get("user")).get("emailAddress");
            String link = (String)((JSONObject)((JSONArray)((JSONObject) jo.get("links")).get("self")).get(0)).get("href");

            PullRequest pullRequest = new PullRequest(id,title,description,state,createdDateTimestamp,updatedDateTimestamp,
                    fromBranch,toBranch,repository,author,authorEmail,link);

            pullRequestList.add(pullRequest);

            //TODO: Save to influx
            pullRequestService.savePullRequest(pullRequest);

            System.out.println(pullRequest);
        }

        //Limit should be 100 at this point by default
        if(size >= (Long) jsonObject.get("limit") && !isLastPage) {
            System.err.println("Only displaying first 100 newest results");
        }

        return pullRequestList;
    }

    /**
     * Return all repos from authenticated user if user has REPO_WRITE permissions
     */
    @GetMapping(path = "/repos")
    @ResponseBody
    public List<Repository> repos() {

        URI uri = URI.create(BitbucketConf.URL + "/repos?limit=100&permission=REPO_WRITE");

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> s;

        List<Repository> repositoryList;

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

        Long size = (Long) jsonObject.get("size");
        boolean isLastPage = (boolean) jsonObject.get("isLastPage");

        repositoryList = new ArrayList<>();

        ja = (JSONArray) jsonObject.get("values");

        for (Object item : ja) {

            JSONObject jo = (JSONObject) item;

            Long id = (Long) jo.get("id");
            String slug = (String) jo.get("slug");
            String name = (String) jo.get("name");
            String state = (String) jo.get("state");
            boolean isPublic = (boolean) jo.get("public");
            String link = (String)((JSONObject)((JSONArray)((JSONObject) jo.get("links")).get("self")).get(0)).get("href");

            Repository repository = new Repository(id,slug,name,state,isPublic,link);

            repositoryList.add(repository);
            System.out.println(repository);
        }

        //Limit should be 100 at this point by default
        if(size >= (Long) jsonObject.get("limit") && !isLastPage) {
            System.err.println("Only displaying first 100 newest results");
        }

        return repositoryList;
    }

    /**
     * Return repos from specific project
     */
    @GetMapping(path = "/{project}/repos")
    @ResponseBody
    public List<Repository> repositories(@PathVariable("project") String project) {

        URI uri = URI.create(BitbucketConf.URL + "/projects/"+project+"/repos?limit=100&permission=REPO_WRITE");

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> s;

        List<Repository> repositoryList;

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

        Long size = (Long) jsonObject.get("size");
        boolean isLastPage = (boolean) jsonObject.get("isLastPage");

        repositoryList = new ArrayList<>();

        ja = (JSONArray) jsonObject.get("values");

        for (Object item : ja) {

            JSONObject jo = (JSONObject) item;

            Long id = (Long) jo.get("id");
            String slug = (String) jo.get("slug");
            String name = (String) jo.get("name");
            String state = (String) jo.get("state");
            boolean isPublic = (boolean) jo.get("public");
            String link = (String)((JSONObject)((JSONArray)((JSONObject) jo.get("links")).get("self")).get(0)).get("href");

            Repository repository = new Repository(id,slug,name,state,isPublic,link);

            repositoryList.add(repository);
            System.out.println(repository);
        }

        //Limit should be 100 at this point by default
        if(size >= (Long) jsonObject.get("limit") && !isLastPage) {
            System.err.println("Only displaying first 100 newest results");
        }

        return repositoryList;
    }

    /**
     * Return info from specific project and repo according user
     */
    @GetMapping(path = "/{project}/repos/{repositorySlug}")
    @ResponseBody
    public Repository repositoryInfo(@PathVariable("project") String project,
                                   @PathVariable("repositorySlug") String repo) {

        URI uri = URI.create(BitbucketConf.URL + "/projects/"+project+"/repos/"+repo+"?permission=REPO_WRITE");

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> s;

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

        JSONObject jo = jsonObject;

        Long id = (Long) jo.get("id");
        String slug = (String) jo.get("slug");
        String name = (String) jo.get("name");
        String state = (String) jo.get("state");
        boolean isPublic = (boolean) jo.get("public");
        String link = (String)((JSONObject)((JSONArray)((JSONObject) jo.get("links")).get("self")).get(0)).get("href");

        Repository repository = new Repository(id,slug,name,state,isPublic,link);

        System.out.println(repository);

        return repository;
    }

    /**
     * Return commits from specific project and repo
     * TODO: Filter commits by user credentials, maybe slug
     */
    @GetMapping(path = "/{project}/repos/{repositorySlug}/commits")
    @ResponseBody
    public List<Commit> repositoryCommits(@PathVariable("project") String project,
                                          @PathVariable("repositorySlug") String repo) {

        URI uri = URI.create(BitbucketConf.URL + "/projects/"+project+"/repos/"+repo+"/commits?limit=100&permission=REPO_WRITE");

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

        Long size = (Long) jsonObject.get("size");
        boolean isLastPage = (boolean) jsonObject.get("isLastPage");

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

            Commit commit = new Commit(id,displayId,author,authorEmail,committerTimestamp,message);

            commitList.add(commit);
            System.out.println(commit);
        }

        //Limit should be 100 at this point by default
        if(size >= (Long) jsonObject.get("limit") && !isLastPage) {
            System.err.println("Only displaying first 100 newest results");
        }

        return commitList;
    }

    /**
     * Return branches from specific project and repo
     */
    @GetMapping(path = "/{project}/repos/{repositorySlug}/branches")
    @ResponseBody
    public List<Branch> repositoryBranches(@PathVariable("project") String project,
                                           @PathVariable("repositorySlug") String repo) {

        URI uri = URI.create(BitbucketConf.URL + "/projects/"+project+"/repos/"+repo+"/branches?limit=100&permission=REPO_WRITE");

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> s;

        List<Branch> branchList;

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

        Long size = (Long) jsonObject.get("size");
        boolean isLastPage = (boolean) jsonObject.get("isLastPage");

        branchList = new ArrayList<>();

        ja = (JSONArray) jsonObject.get("values");

        for (Object item : ja) {

            JSONObject jo = (JSONObject) item;

            String id = (String) jo.get("id");
            String displayId = (String) jo.get("displayId");
            String type = (String) jo.get("type");
            String latestCommit = (String) jo.get("latestCommit");
            String latestChangeset = (String) jo.get("latestChangeset");
            boolean isDefault = (boolean) jo.get("isDefault");

            Branch branch = new Branch(id,displayId,type,latestCommit,latestChangeset,isDefault);

            branchList.add(branch);
            System.out.println(branch);
        }

        //Limit should be 100 at this point by default
        if(size >= (Long) jsonObject.get("limit") && !isLastPage) {
            System.err.println("Only displaying first 100 newest results");
        }

        return branchList;
    }

}
