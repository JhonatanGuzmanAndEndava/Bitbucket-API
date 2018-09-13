package com.endava.dashboard.bitbucket.services;

public interface RepositoryService extends InfluxDBConnection {

    void addRepository(String projectSlug, String repositorySlug);

}
