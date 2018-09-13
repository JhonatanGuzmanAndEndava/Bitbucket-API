package com.endava.dashboard.bitbucket.controller;

import com.endava.dashboard.bitbucket.responseobjects.Project;
import com.endava.dashboard.bitbucket.responseobjects.Repository;
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

    @Autowired
    public BitbucketController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    private HttpEntity basicCredentials() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic "+ BitbucketConf.base64Credentials);
        return new HttpEntity<>(headers);
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
            if(e.getRawStatusCode() == 401)
                System.err.println("Unauthenticated user or unauthorized: " + e.getMessage());
            else
                System.err.println("Something is wrong!" + e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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

        return repositoryService.addRepository(project,repository);

    }



}
