package com.endava.dashboard.bitbucket.services;

import com.endava.dashboard.bitbucket.parse.ParsePojo;
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
public class RepositoryServiceImpl implements RepositoryService {

    private InfluxDB influxDB;

    public RepositoryServiceImpl() {
        influxDB = InfluxDBFactory.connect(URL +":"+ PORT, USERNAME, PASSWORD);
    }

    @Override
    public ResponseEntity<Void> addRepository(Repository theRepository) {

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

        batchPoints.point(point2);
        influxDB.write(batchPoints);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Repository> getRepositoryBySlug(String repositorySlug) {

        try {
            Pong pong = influxDB.ping();
            if(!pong.isGood())
                throw new Exception();
        } catch (Exception e) {
            System.err.println("Cannot connect with influxDB");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Query query = BoundParameterQuery.QueryBuilder.newQuery("SELECT * FROM repository WHERE slug = $slug")
                .forDatabase(DATABASE)
                .bind("slug", repositorySlug)
                .create();

        QueryResult queryResult = influxDB.query(query);

        System.out.println(queryResult);
        /*for (QueryResult.Result result : queryResult.getResults()) {
            for (QueryResult.Series serie: result.getSeries()) {
                for(List<Object> obj : serie.getValues()) {
                    //Just need one row
                    return new ResponseEntity<>(ParsePojo.getRepositoryFromInfluxObject(obj), HttpStatus.OK);
                }
            }
        }*/

        //0 rows found
        return new ResponseEntity<>(new Repository(), HttpStatus.OK);
    }

}
