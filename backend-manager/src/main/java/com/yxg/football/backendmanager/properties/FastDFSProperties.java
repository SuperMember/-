package com.yxg.football.backendmanager.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "fdfs")
public class FastDFSProperties {
    private String soTimeout;
    private String connectTimeout;
    private List<String> trackerList;
    private String host;

    public String getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(String connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public List<String> getTrackerList() {
        return trackerList;
    }

    public void setTrackerList(List<String> trackerList) {
        this.trackerList = trackerList;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSoTimeout() {

        return soTimeout;
    }

    public void setSoTimeout(String soTimeout) {
        this.soTimeout = soTimeout;
    }
}
