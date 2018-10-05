package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.responseobjects.Commit;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CommitServiceImpl implements CommitService {

    private InfluxDB influxDB;

    public CommitServiceImpl() {
        influxDB = InfluxDBFactory.connect(URL +":"+ PORT, USERNAME, PASSWORD);
    }

    @Override
    public ResponseEntity<Void> saveCommits(List<Commit> commits) {

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
        for (Commit commit: commits) {
            p = Point.measurement("commit")
                    .time(commit.getCommitterTimestamp(), TimeUnit.MILLISECONDS)
                    .addField("id", commit.getId())
                    .addField("displayId", commit.getDisplayId())
                    .addField("projectId", commit.getProjectId())
                    .addField("repositoryId", commit.getRepositoryId())
                    .addField("author", commit.getAuthor())
                    .addField("authorEmail", commit.getAuthorEmail())
                    .addField("message", commit.getMessage())
                    .build();

            batchPoints.point(p);
        }
        influxDB.write(batchPoints);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
