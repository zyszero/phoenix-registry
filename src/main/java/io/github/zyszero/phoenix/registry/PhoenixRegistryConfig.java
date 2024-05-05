package io.github.zyszero.phoenix.registry;

import io.github.zyszero.phoenix.registry.cluster.Cluster;
import io.github.zyszero.phoenix.registry.health.HealthChecker;
import io.github.zyszero.phoenix.registry.health.PhoenixHealthChecker;
import io.github.zyszero.phoenix.registry.service.PhoenixRegistryService;
import io.github.zyszero.phoenix.registry.service.RegistryService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * configuration for all beans.
 *
 * @Author: zyszero
 * @Date: 2024/4/30 6:56
 */
@Configuration
@EnableConfigurationProperties(PhoenixRegistryConfigProperties.class)
public class PhoenixRegistryConfig {

    @Bean
    public RegistryService registryService() {
        return new PhoenixRegistryService();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public HealthChecker healthChecker(RegistryService registryService) {
        return new PhoenixHealthChecker(registryService);
    }

    @Bean(initMethod = "init")
    public Cluster cluster(PhoenixRegistryConfigProperties registryProperties) {
        return new Cluster(registryProperties);
    }
}
