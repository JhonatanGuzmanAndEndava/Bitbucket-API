package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.parse.ParsePojo;
import com.endava.dashboard.bitbucket.responseobjects.Project;
import com.endava.dashboard.bitbucket.responseobjects.Repository;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ProjectServiceImpl implements ProjectService {

    private InfluxDB influxDB;

    public ProjectServiceImpl() {
        influxDB = InfluxDBFactory.connect(URL +":"+ PORT, USERNAME, PASSWORD);
    }

    @Override
    public ResponseEntity<Void> addProject(Project theProject) {

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

        batchPoints.point(point1);
        influxDB.write(batchPoints);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Project> getProjectByKey(String projectKey) {

        try {
            Pong pong = influxDB.ping();
            if(!pong.isGood())
                throw new Exception();
        } catch (Exception e) {
            System.err.println("Cannot connect with influxDB");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Query query = BoundParameterQuery.QueryBuilder.newQuery("SELECT * FROM project WHERE key = $key")
                .forDatabase(DATABASE)
                .bind("key", projectKey)
                .create();

        QueryResult queryResult = influxDB.query(query);

        //System.out.println(queryResult);
        for (QueryResult.Result result : queryResult.getResults()) {
            for (QueryResult.Series serie: result.getSeries()) {
                for(List<Object> obj : serie.getValues()) {
                    //Just need one row
                    return new ResponseEntity<>(ParsePojo.getProjectFromInfluxObject(obj), HttpStatus.OK);
                }
            }
        }

        //0 rows found
        return new ResponseEntity<>(new Project(), HttpStatus.OK);
    }
}
