package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.responseobjects.PullRequest;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PullRequestServiceImpl implements PullRequestService {

    private InfluxDB influxDB;

    public PullRequestServiceImpl() {
        influxDB = InfluxDBFactory.connect(URL +":"+ PORT, USERNAME, PASSWORD);
    }

    @Override
    public void savePullRequest(PullRequest pullRequest) {

        try {
            Pong pong = influxDB.ping();
            if(!pong.isGood())
                throw new Exception();
        } catch (Exception e) {
            System.err.println("Cannot connect with influxDB");
            return;
        }

        //QueryResult queryResult = influxDB.query(new Query("SHOW DATABASES",DATABASE));
        //System.out.println(queryResult.toString());

        BatchPoints batchPoints = BatchPoints
                .database(DATABASE)
                .tag("async", "true")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        Point point1 = Point.measurement("pull_request")
                .time(pullRequest.getUpdatedDateTimestamp(), TimeUnit.MILLISECONDS)
                .addField("id", pullRequest.getId())
                .addField("projectId", pullRequest.getProjectId())
                .addField("repositoryId", pullRequest.getRepositoryId())
                .addField("title", pullRequest.getTitle())
                .addField("state", pullRequest.getState())
                .addField("createdDateTimestamp", pullRequest.getCreatedDateTimestamp())
                .addField("updatedDateTimestamp", pullRequest.getUpdatedDateTimestamp())
                .addField("fromBranch", pullRequest.getFromBranch())
                .addField("toBranch", pullRequest.getToBranch())
                .addField("repository", pullRequest.getRepository())
                .addField("author", pullRequest.getAuthor())
                .addField("link", pullRequest.getLink())
                .build();

        batchPoints.point(point1);
        influxDB.write(batchPoints);

    }

    @Override
    public ResponseEntity<Void> savePullRequestsList(List<PullRequest> pullRequestList) {

        try {
            Pong pong = influxDB.ping();
            if(!pong.isGood())
                throw new Exception();
        } catch (Exception e) {
            System.err.println("Cannot connect with influxDB");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        BatchPoints batchPoints = BatchPoints
                .database(DATABASE)
                .tag("async", "true")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        Point p;
        for (PullRequest pullRequest: pullRequestList) {
            p = Point.measurement("pull_request")
                    .time(pullRequest.getUpdatedDateTimestamp(), TimeUnit.MILLISECONDS)
                    .addField("id", pullRequest.getId())
                    .addField("projectId", pullRequest.getProjectId())
                    .addField("repositoryId", pullRequest.getRepositoryId())
                    .addField("title", pullRequest.getTitle())
                    .addField("state", pullRequest.getState())
                    .addField("createdDateTimestamp", pullRequest.getCreatedDateTimestamp())
                    .addField("updatedDateTimestamp", pullRequest.getUpdatedDateTimestamp())
                    .addField("fromBranch", pullRequest.getFromBranch())
                    .addField("toBranch", pullRequest.getToBranch())
                    .addField("repository", pullRequest.getRepository())
                    .addField("author", pullRequest.getAuthor())
                    .addField("link", pullRequest.getLink())
                    .build();

            batchPoints.point(p);
        }
        influxDB.write(batchPoints);
        return new ResponseEntity<>(HttpStatus.CREATED);

    }
}
