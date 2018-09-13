package com.endava.dashboard.bitbucket.services;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.springframework.stereotype.Service;

@Service
public class RepositoryServiceImpl implements RepositoryService {

    private InfluxDB influxDB;

    public RepositoryServiceImpl() {
        influxDB = InfluxDBFactory.connect(url+":"+port, username, password);
    }

    @Override
    public void addRepository(String projectSlug, String repositorySlug) {

        try {
            Pong pong = influxDB.ping();
            if(!pong.isGood())
                throw new Exception();
        } catch (Exception e) {
            System.err.println("Cannot connect with influxDB");
            return;
        }

    }
}
