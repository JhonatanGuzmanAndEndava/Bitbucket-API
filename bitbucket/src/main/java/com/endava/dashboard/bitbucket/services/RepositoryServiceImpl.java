package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.responseobjects.Project;
import com.endava.dashboard.bitbucket.responseobjects.Repository;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RepositoryServiceImpl implements RepositoryService {

    private InfluxDB influxDB;

    public RepositoryServiceImpl() {
        influxDB = InfluxDBFactory.connect(URL +":"+ PORT, USERNAME, PASSWORD);
    }

    @Override
    public ResponseEntity<Void> addRepository(Project theProject, Repository theRepository) {

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

        Point point1 = Point.measurement("project")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("id", theProject.getId())
                .addField("key", theProject.getKey())
                .addField("name", theProject.getName())
                .addField("description", theProject.getDescription())
                .addField("isPublic", theProject.isPublic())
                .addField("type", theProject.getType())
                .addField("link", theProject.getLink())
                .build();

        Point point2 = Point.measurement("repository")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("id", theRepository.getId())
                .addField("projectId", theRepository.getProjectId())
                .addField("slug", theRepository.getSlug())
                .addField("name", theRepository.getName())
                .addField("state", theRepository.getState())
                .addField("isPublic", theRepository.isPublic())
                .addField("link", theRepository.getLink())
                .build();

        batchPoints.point(point1);
        batchPoints.point(point2);
        influxDB.write(batchPoints);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
