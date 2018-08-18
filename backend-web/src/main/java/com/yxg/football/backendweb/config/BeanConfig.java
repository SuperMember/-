package com.yxg.football.backendweb.config;

import com.gargoylesoftware.htmlunit.WebClient;
import com.github.tobato.fastdfs.FdfsClientConfig;
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
import com.google.gson.Gson;
import com.yxg.football.backendweb.properties.FastDFSProperties;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {
    @Autowired
    FastDFSProperties fastDFSProperties;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public Md5PasswordEncoder md5PasswordEncoder() {
        return new Md5PasswordEncoder();
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

    @Bean
    public WebClient webClient() {
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setUseInsecureSSL(false);
        return webClient;
    }

    /*
    * https
    * */
//    @Bean
//    public EmbeddedServletContainerFactory servletContainer() {
//
//        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
//
//            @Override
//            protected void postProcessContext(Context context) {
//
//                SecurityConstraint securityConstraint = new SecurityConstraint();
//                securityConstraint.setUserConstraint("CONFIDENTIAL");
//                SecurityCollection collection = new SecurityCollection();
//                collection.addPattern("/*");
//                securityConstraint.addCollection(collection);
//                context.addConstraint(securityConstraint);
//            }
//        };
//        tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
//        return tomcat;
//    }
//
//    private Connector initiateHttpConnector() {
//
//        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//        connector.setScheme("http");
//        connector.setPort(8080);
//        connector.setSecure(false);
//        connector.setRedirectPort(8443);
//        return connector;
//    }
}
