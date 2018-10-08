package com.endava.dashboard.bitbucket.controller;

import com.endava.dashboard.bitbucket.exception.BitbucketCollectorException;
import com.endava.dashboard.bitbucket.parse.ParsePojo;
import com.endava.dashboard.bitbucket.responseobjects.Commit;
import com.endava.dashboard.bitbucket.responseobjects.Project;
import com.endava.dashboard.bitbucket.responseobjects.PullRequest;
import com.endava.dashboard.bitbucket.responseobjects.Repository;
import com.endava.dashboard.bitbucket.services.CommitService;
import com.endava.dashboard.bitbucket.services.ProjectService;
import com.endava.dashboard.bitbucket.services.PullRequestService;
import com.endava.dashboard.bitbucket.services.RepositoryService;
import com.endava.dashboard.bitbucket.settings.BitbucketConf;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@EnableScheduling
@RestController
@RequestMapping(path = "/v2")
public class BitbucketController {

    private ProjectService projectService;
    private RepositoryService repositoryService;
    private CommitService commitService;
    private PullRequestService pullRequestService;

    @Autowired
    public BitbucketController(ProjectService projectService,
                               RepositoryService repositoryService,
                               CommitService commitService,
                               PullRequestService pullRequestService) {
        this.projectService = projectService;
        this.repositoryService = repositoryService;
        this.commitService = commitService;
        this.pullRequestService = pullRequestService;
    }

    private HttpEntity basicCredentials() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic "+ BitbucketConf.BASE_64_CREDENTIALS);
        return new HttpEntity<>(headers);
    }

    private ResponseEntity<String> getJsonBodyFromRequest(URI uri) {
        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> response;
        try {
            response = rest.exchange(uri, HttpMethod.GET, basicCredentials(), String.class);
        }catch(HttpClientErrorException e) {
            if(e.getRawStatusCode() == 401) {
                System.err.println("Unauthenticated user: " + e.getMessage());
                throw new BitbucketCollectorException("Unauthenticated user");
            }
            else {
                System.err.println("Something is wrong!" + e.getMessage());
                throw new BitbucketCollectorException("Something is wrong: Try again later");
            }
        }
        return response;
    }

    private List<Commit> getCommits(String project, String repo, Long projectId, Long repositoryId, String numberCommits) {

        URI uri = URI.create(BitbucketConf.API_URL + "/projects/"+project+"/repos/"+repo+"/commits?limit="+numberCommits+"&permission=REPO_WRITE");
        List<Commit> commitList = new ArrayList<>();;

        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(getJsonBodyFromRequest(uri).getBody());
        } catch (ParseException e) {
            System.err.println("Error reading JSON String");
            throw new BitbucketCollectorException("Error reading JSON String");
        }

        JSONArray ja = (JSONArray) jsonObject.get("values");

        for (Object item : ja) {
            JSONObject jo = (JSONObject) item;
            commitList.add(ParsePojo.getCommitFromJsonObject(projectId, repositoryId, jo));
        }
        return commitList;
    }

    private List<PullRequest> getPullRequests(String project, String repo, Long projectId, Long repositoryId, String numberPullRequest) {

        URI uri = URI.create(BitbucketConf.API_URL + "/projects/"+project+"/repos/"+repo+"/pull-requests?limit="+numberPullRequest+"&state=ALL&role=AUTHOR");
        List<PullRequest> pullRequestList = new ArrayList<>();

        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) new JSONParser().parse(getJsonBodyFromRequest(uri).getBody());
        } catch (ParseException e) {
            System.err.println("Error reading JSON String");
            throw new BitbucketCollectorException("Error reading JSON String");
        }

        JSONArray ja = (JSONArray) jsonObject.get("values");

        for (Object item : ja) {
            JSONObject jo = (JSONObject) item;
            pullRequestList.add(ParsePojo.getPullRequestFromJsonObject(projectId, repositoryId, jo));
        }
        return pullRequestList;
    }

    @PostMapping(path = "/{projectSlug}/repos/{repositorySlug}")
    @ResponseBody
    public ResponseEntity<Void> addRepository(@PathVariable("projectSlug")
                                                          String projectSlug,
                                              @PathVariable("repositorySlug")
                                                          String repositorySlug,
                                              @RequestParam(value = "numberCommits", required = false, defaultValue = "100")
                                                          String numberCommits,
                                              @RequestParam(value = "numberPullRequest", required = false, defaultValue = "10")
                                                          String numberPullRequest) {

        URI uriProject = URI.create(BitbucketConf.API_URL + "/projects/"+projectSlug);
        URI uriRepository = URI.create(BitbucketConf.API_URL + "/projects/"+projectSlug+"/repos/"+repositorySlug);

        RestTemplate rest = new RestTemplate();
        ResponseEntity<String> projectResponse;
        ResponseEntity<String> repositoryResponse;

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
        }catch (Exception e) {
            System.err.println("Something is wrong!" + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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

        Project project = ParsePojo.getProjectFromJsonObject(jsonProjectObject);
        Repository repository = ParsePojo.getRepositoryFromJsonObject(jsonRepositoryObject);

        //Check if the repository is already in influx
        Optional<Repository> rep = repositoryService.getRepositoryBySlug(repositorySlug);
        if(!rep.isPresent()) {

            //Check if the project is currently saved in database
            Optional<Project> project1 = projectService.getProjectByKey(projectSlug);
            if(!project1.isPresent()) {
                projectService.addProject(project);
            }

            ResponseEntity<Void> responseEntityRepository = repositoryService.addRepository(repository);

            if (responseEntityRepository.getStatusCode().value() == 201) {
                commitService.saveCommits(getCommits(projectSlug, repositorySlug, project.getId(), repository.getId(), numberCommits));
                pullRequestService.savePullRequestsList(getPullRequests(projectSlug, repositorySlug, project.getId(), repository.getId(), numberPullRequest));
                return new ResponseEntity<>(HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "/update")
    @ResponseBody
    @Scheduled(cron = "0 15 18 * * *") //6:15pm
    public void updateCommitsAndPullRequest() {
        System.out.println("Running cron...");
        commitService.deleteAllCommits();
        pullRequestService.deleteAllPullRequests();

        Iterable<Project> projects = projectService.getAllProjects();
        for (Project project : projects) {
            Iterable<Repository> repositories = repositoryService.getAllRepositoriesByProjectId(project.getId());
            for (Repository repository : repositories) {
                commitService.saveCommits(getCommits(project.getKeyProject(), repository.getSlug(), project.getId(), repository.getId(), "100"));
                pullRequestService.savePullRequestsList(getPullRequests(project.getKeyProject(), repository.getSlug(), project.getId(), repository.getId(), "10"));
            }
        }
    }

    @GetMapping(path = "/grettings")
    @ResponseBody
    //@Scheduled(cron = "0/5 * * ? * *") //5 seconds
    //@Scheduled(cron = "0 15 18 * * *") //6:15pm
    public void grettings() {
        System.out.println("Me estoy ejecutando :v x"+value.getAndIncrement());
    }

    private AtomicInteger value = new AtomicInteger();

}
