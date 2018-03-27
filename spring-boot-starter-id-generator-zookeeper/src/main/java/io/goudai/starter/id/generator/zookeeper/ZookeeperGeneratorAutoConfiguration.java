package io.goudai.starter.id.generator.zookeeper;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by freeman on 17/2/15.
 */
@Configuration
@EnableConfigurationProperties(ZookeeperGeneratorAutoConfiguration.IdGeneratorZookeeperProperties.class)
@Slf4j
public class ZookeeperGeneratorAutoConfiguration {


    @Bean
    public IdGeneratorFactoryBean idGeneratorFactoryBean(IdGeneratorZookeeperProperties properties) {
        return IdGeneratorFactoryBean.builder()
                .zkAddress(properties.zookeeperServers)
                .connectTimeout(properties.connectTimeout)
                .sessionTimeout(properties.sessionTimeout)
                .root(properties.root)
                .build();
    }


    @Setter
    @Getter
    @ConfigurationProperties(prefix = "goudai.id.generator.zookeeper")
    public static class IdGeneratorZookeeperProperties {

        private String zookeeperServers;

        private String root = "/idGenerator";

        /**
         * 默认开始时间为（2018-03-01）
         */
        private long twepoch = 1519833600000L;

        private int sessionTimeout = 3000;

        private long connectTimeout = 3000;
    }


}
