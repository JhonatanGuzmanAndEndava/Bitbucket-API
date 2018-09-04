package com.endava.dashboard.bitbucket.controller;

import com.endava.dashboard.bitbucket.responseobjects.Projects;
import com.endava.dashboard.bitbucket.responseobjects.PullRequests;
import com.endava.dashboard.bitbucket.settings.BitbucketConf;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

    public HttpEntity basicCredentials() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic "+BitbucketConf.base64Credentials);
        return new HttpEntity<>(headers);
    }

    /**
     * It returns a list of projects for which the authenticated user has
     * the PROJECT_VIEW permissions
     * @return
     */
    @GetMapping(path = "/projects")
    @ResponseBody
    public List<Projects> projects2() {

        URI uri = URI.create(BitbucketConf.URL + "/projects?limit=100");

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> s = null;
        List<Projects> projectsList = null;

        try {
            s = rest.exchange(uri, HttpMethod.GET, basicCredentials(), String.class);
        }catch(HttpClientErrorException e) {
            if(e.getRawStatusCode() == 401)
                System.err.println("Unauthenticated user: " + e.getMessage());
            else
                System.err.println("Something is wrong!" + e.getMessage());

            return null;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(s.getBody());
        } catch (ParseException e) {
            System.err.println("Error reading JSON String");
            return null;
        }

        JSONArray ja = null;

        Long size = (Long) jsonObject.get("size");
        boolean isLastPage = (boolean) jsonObject.get("isLastPage");

        projectsList = new ArrayList<>();

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

            Projects projects = new Projects(id,key,name,description,isPublic,type,link);

            projectsList.add(projects);

            System.out.println(projects);
        }

        //Limit should be 100 at this point by default
        if(size >= (Long) jsonObject.get("limit") && !isLastPage) {
            System.err.println("Only displaying first 100 newest results");
        }
        return projectsList;
    }

    /**
     * Into query params there are options for pull request approval, merged, closed and no approval
     * By default just getting first 100 results and author provided by credentials
     * @return
     */
    @GetMapping(path = "/pullrequests")
    @ResponseBody
    public List<PullRequests> pullRequests() {

        URI uri = URI.create(BitbucketConf.URL + "/dashboard/pull-requests?limit=100&role=AUTHOR");

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> s;

        List<PullRequests> pullRequestsList = null;

        try {
            s = rest.exchange(uri, HttpMethod.GET, basicCredentials(), String.class);
        }catch(HttpClientErrorException e) {
            if(e.getRawStatusCode() == 401)
                System.err.println("Unauthenticated user: " + e.getMessage());
            else
                System.err.println("Something is wrong!" + e.getMessage());

            return null;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(s.getBody());
        } catch (ParseException e) {
            System.err.println("Error reading JSON String");
            return null;
        }

        JSONArray ja = null;

        Long size = (Long) jsonObject.get("size");
        boolean isLastPage = (boolean) jsonObject.get("isLastPage");

        pullRequestsList = new ArrayList<>();

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

            PullRequests pullRequests = new PullRequests(id,title,description,state,createdDateTimestamp,updatedDateTimestamp,
                    fromBranch,toBranch,repository,author,authorEmail,link);

            pullRequestsList.add(pullRequests);

            System.out.println(pullRequests);
        }

        //Limit should be 100 at this point by default
        if(size >= (Long) jsonObject.get("limit") && !isLastPage) {
            System.err.println("Only displaying first 100 newest results");
        }

        return pullRequestsList;
    }

    /**
     * Return all repos from authenticated user
     * @return
     */
    @GetMapping(path = "/repos")
    @ResponseBody
    public String repos() {

        URI uri = URI.create(BitbucketConf.URL + "/repos");

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> s;
        try {
            s = rest.exchange(uri, HttpMethod.GET, basicCredentials(), String.class);
        }catch(HttpClientErrorException e) {
            System.out.println(e.getMessage());
            return e.getRawStatusCode() == 401 ? "Usuario no autenticado" : "Algo no funciona";
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(s.getBody());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);
        System.out.println(jsonObject.toJSONString());

        return jsonObject.toString();
    }

    /**
     * Return repos from specific project
     * @param project
     * @return
     */
    @GetMapping(path = "/{project}/repos")
    @ResponseBody
    public String repositories(@PathVariable("project") String project) {

        URI uri = URI.create(BitbucketConf.URL + "/projects/"+project+"/repos");

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> s;
        try {
            s = rest.exchange(uri, HttpMethod.GET, basicCredentials(), String.class);
        }catch(HttpClientErrorException e) {
            System.out.println(e.getMessage());
            return e.getRawStatusCode() == 401 ? "Usuario no autenticado" : "Algo no funciona";
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(s.getBody());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);
        System.out.println(jsonObject.toJSONString());

        return jsonObject.toString();
    }

    /**
     * Return info from specific project and repo according user
     * @param project
     * @return
     */
    @GetMapping(path = "/{project}/repos/{repositorySlug}")
    @ResponseBody
    public String repositoryInfo(@PathVariable("project") String project,
                                   @PathVariable("repositorySlug") String repo) {

        URI uri = URI.create(BitbucketConf.URL + "/projects/"+project+"/repos/"+repo);
        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> s;
        try {
            s = rest.exchange(uri, HttpMethod.GET, basicCredentials(), String.class);
        }catch(HttpClientErrorException e) {
            System.out.println(e.getMessage());
            return e.getRawStatusCode() == 401 ? "Usuario no autenticado" : "Algo no funciona";
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(s.getBody());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);
        System.out.println(jsonObject.toJSONString());

        return jsonObject.toString();
    }

    /**
     * Return action from specific project and repo according user
     * @param project
     * @return
     */
    @GetMapping(path = "/{project}/repos/{repositorySlug}/{action}")
    @ResponseBody
    public String actionRepository(@PathVariable("project") String project,
                                   @PathVariable("repositorySlug") String repo,
                                   @PathVariable("action") String action) {

        /**
         * none: repo info
         * branches
         * commits
         * pull-requests
         */

        URI uri = URI.create(BitbucketConf.URL + "/projects/"+project+"/repos/"+repo+"/"+action);
        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> s;
        try {
            s = rest.exchange(uri, HttpMethod.GET, basicCredentials(), String.class);
        }catch(HttpClientErrorException e) {
            System.out.println(e.getMessage());
            return e.getRawStatusCode() == 401 ? "Usuario no autenticado" : "Algo no funciona";
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(s.getBody());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(jsonObject);
        System.out.println(jsonObject.toJSONString());

        return jsonObject.toString();
    }

}
