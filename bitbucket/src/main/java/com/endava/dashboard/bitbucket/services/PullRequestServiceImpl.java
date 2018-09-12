package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.responseobjects.PullRequest;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class PullRequestServiceImpl implements PullRequestService {

    private InfluxDB influxDB;

    public PullRequestServiceImpl() {
        influxDB = InfluxDBFactory.connect(url+":"+port, username, password);
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

        //QueryResult queryResult = influxDB.query(new Query("SHOW DATABASES",database));
        //System.out.println(queryResult.toString());

        BatchPoints batchPoints = BatchPoints
                .database(database)
                .tag("async", "true")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        Point point1 = Point.measurement("pull_request")
                .time(pullRequest.getCreatedDateTimestamp(), TimeUnit.MILLISECONDS)
                .addField("id", pullRequest.getId())
                .addField("title", pullRequest.getTitle())
                .addField("description", pullRequest.getDescription())
                .addField("state", pullRequest.getState())
                .addField("createdDateTimestamp", pullRequest.getCreatedDateTimestamp())
                .addField("updatedDateTimestamp", pullRequest.getUpdatedDateTimestamp())
                .addField("fromBranch", pullRequest.getFromBranch())
                .addField("toBranch", pullRequest.getToBranch())
                .addField("repository", pullRequest.getRepository())
                .addField("author", pullRequest.getAuthor())
                .addField("authorEmail", pullRequest.getAuthorEmail())
                .addField("link", pullRequest.getLink())
                .build();

        batchPoints.point(point1);
        influxDB.write(batchPoints);

    }
}
