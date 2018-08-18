package com.yxg.football.backendmanager.config;

import com.github.tobato.fastdfs.conn.ConnectionPoolConfig;
import com.github.tobato.fastdfs.conn.FdfsConnectionPool;
import com.github.tobato.fastdfs.conn.PooledConnectionFactory;
import com.github.tobato.fastdfs.conn.TrackerConnectionManager;
import com.github.tobato.fastdfs.domain.DefaultThumbImageConfig;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.DefaultFastFileStorageClient;
import com.github.tobato.fastdfs.service.DefaultTrackerClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.github.tobato.fastdfs.service.TrackerClient;
import com.yxg.football.backendmanager.properties.FastDFSProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {
    @Autowired
    FastDFSProperties fastDFSProperties;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /*
    * fastdfs start
    * */
    @Bean
    public TrackerConnectionManager trackerConnectionManager() {
        TrackerConnectionManager trackerConnectionManager = new TrackerConnectionManager(fdfsConnectionPool());
        trackerConnectionManager.setTrackerList(fastDFSProperties.getTrackerList());
        return trackerConnectionManager;
    }

    @Bean
    public FastFileStorageClient fastFileStorageClient() {
        return new DefaultFastFileStorageClient();
    }

    @Bean
    public ThumbImageConfig thumbImageConfig() {
        ThumbImageConfig thumbImageConfig = new DefaultThumbImageConfig();
        return thumbImageConfig;
    }

    @Bean
    public TrackerClient trackerClient() {
        TrackerClient trackerClient = new DefaultTrackerClient();
        return trackerClient;
    }

    @Bean
    public FdfsConnectionPool fdfsConnectionPool() {
        FdfsConnectionPool fdfsConnectionPool = new FdfsConnectionPool(pooledConnectionFactory(), connectionPoolConfig());
        return fdfsConnectionPool;
    }

    @Bean
    public ConnectionPoolConfig connectionPoolConfig() {
        ConnectionPoolConfig connectionPoolConfig = new ConnectionPoolConfig();
        connectionPoolConfig.setJmxEnabled(false);
        return connectionPoolConfig;
    }

    @Bean
    public PooledConnectionFactory pooledConnectionFactory() {
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        return pooledConnectionFactory;
    }
    /*
    * fastdfs end
    * */
}
