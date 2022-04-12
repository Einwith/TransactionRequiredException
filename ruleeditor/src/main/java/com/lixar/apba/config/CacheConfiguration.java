package com.lixar.apba.config;

import com.hazelcast.config.*;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.instance.HazelcastInstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.session.hazelcast.HazelcastSessionRepository;
import org.springframework.session.hazelcast.PrincipalNameExtractor;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

@Configuration
@EnableCaching
@AutoConfigureAfter(value = { MetricsConfiguration.class, DatabaseConfiguration.class })
public class CacheConfiguration {

    private final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    private static HazelcastInstance hazelcastInstance;

    @Inject
    private Environment env;

    private CacheManager cacheManager;

    @PreDestroy
    public void destroy() {
        log.info("Closing Cache Manager");
        Hazelcast.shutdownAll();
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        log.debug("Starting HazelcastCacheManager");
        cacheManager = new com.hazelcast.spring.cache.HazelcastCacheManager(hazelcastInstance);
        return cacheManager;
    }

    @Bean
    public HazelcastInstance hazelcastInstance(JHipsterProperties jHipsterProperties) {
        log.debug("Configuring Hazelcast");
        Config config = new Config();

        // In development, remove multicast auto-configuration
        if (env.acceptsProfiles(Profiles.of(Constants.SPRING_PROFILE_DEVELOPMENT))) {
            System.setProperty("hazelcast.local.localAddress", "127.0.0.1");
            config.getNetworkConfig().setPort(9701);
            config.getNetworkConfig().setPortAutoIncrement(false);
            config.getMapConfigs().put("default", initializeDefaultMapConfig());
            config.getMapConfigs().put("com.lixar.apba.domain.*", initializeDomainMapConfig(jHipsterProperties));
            config.getMapConfigs().put("clustered-http-sessions", initializeClusteredSession(jHipsterProperties));
            config.getNetworkConfig().getJoin().getAwsConfig().setEnabled(false);
            config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
            config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
            config.getGroupConfig().setName("dev");
        } else if (jHipsterProperties.getCache().getHazelcast().getConfig().endsWith("xml")) {
            config = new ClasspathXmlConfig(jHipsterProperties.getCache().getHazelcast().getConfig());
        } else {
            config = new ClasspathYamlConfig(jHipsterProperties.getCache().getHazelcast().getConfig());
        }
        MapAttributeConfig attributeConfig = new MapAttributeConfig()
            .setName(HazelcastSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
            .setExtractor(PrincipalNameExtractor.class.getName());
        config.getMapConfig("spring:session:sessions").addMapAttributeConfig(attributeConfig)
            .addMapIndexConfig(new MapIndexConfig(HazelcastSessionRepository.PRINCIPAL_NAME_ATTRIBUTE, false));

        return HazelcastInstanceFactory.newHazelcastInstance(config);
    }

    private MapConfig initializeDefaultMapConfig() {
        MapConfig mapConfig = new MapConfig();

        /*
            Number of backups. If 1 is set as the backup-count for example,
            then all entries of the map will be copied to another JVM for
            fail-safety. Valid numbers are 0 (no backup), 1, 2, 3.
         */
        mapConfig.setBackupCount(0);

        /*
            Valid values are:
            NONE (no eviction),
            LRU (Least Recently Used),
            LFU (Least Frequently Used).
            NONE is the default.
         */
        mapConfig.setEvictionPolicy(EvictionPolicy.LRU);

        /*
            Maximum size of the map. When max size is reached,
            map is evicted based on the policy defined.
            Any integer between 0 and Integer.MAX_VALUE. 0 means
            Integer.MAX_VALUE. Default is 0.
         */
        mapConfig.setMaxSizeConfig(new MaxSizeConfig(0, MaxSizeConfig.MaxSizePolicy.USED_HEAP_SIZE));

        return mapConfig;
    }

    private MapConfig initializeDomainMapConfig(JHipsterProperties jHipsterProperties) {
        MapConfig mapConfig = new MapConfig();

        mapConfig.setTimeToLiveSeconds(jHipsterProperties.getCache().getTimeToLiveSeconds());
        return mapConfig;
    }

    /**
    * @return the unique instance.
    */
    public static HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    private MapConfig initializeClusteredSession(JHipsterProperties jHipsterProperties) {
        MapConfig mapConfig = new MapConfig();

        mapConfig.setBackupCount(jHipsterProperties.getCache().getHazelcast().getBackupCount());
        mapConfig.setTimeToLiveSeconds(jHipsterProperties.getCache().getTimeToLiveSeconds());
        return mapConfig;
    }

    /**
     * Use by Spring Security, to get events from Hazelcast.
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
