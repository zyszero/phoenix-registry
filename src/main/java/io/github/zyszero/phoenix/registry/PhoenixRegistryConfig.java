package io.github.zyszero.phoenix.registry;

import io.github.zyszero.phoenix.registry.health.HealthChecker;
import io.github.zyszero.phoenix.registry.health.PhoenixHealthChecker;
import io.github.zyszero.phoenix.registry.service.PhoenixRegistryService;
import io.github.zyszero.phoenix.registry.service.RegistryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * configuration for all beans.
 *
 * @Author: zyszero
 * @Date: 2024/4/30 6:56
 */
@Configuration
public class PhoenixRegistryConfig {

    @Bean
    public RegistryService registryService() {
        return new PhoenixRegistryService();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public HealthChecker healthChecker(RegistryService registryService) {
        return new PhoenixHealthChecker(registryService);
    }
}
