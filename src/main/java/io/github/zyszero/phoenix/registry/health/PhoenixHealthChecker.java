package io.github.zyszero.phoenix.registry.health;

import io.github.zyszero.phoenix.registry.model.InstanceMeta;
import io.github.zyszero.phoenix.registry.service.PhoenixRegistryService;
import io.github.zyszero.phoenix.registry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation of HealthChecker.
 *
 * @Author: zyszero
 * @Date: 2024/5/5 15:14
 */
@Slf4j
public class PhoenixHealthChecker implements HealthChecker {

    final ScheduledExecutorService executor =
            Executors.newScheduledThreadPool(1);

    long timeout = 20_000;


    private final RegistryService registryService;

    public PhoenixHealthChecker(RegistryService registryService) {
        this.registryService = registryService;
    }

    @Override
    public void start() {
        executor.scheduleWithFixedDelay(() -> {
            log.info(" ====> Health checker is running...");
            // check health
            long now = System.currentTimeMillis();
            PhoenixRegistryService.TIMESTAMP.keySet().forEach(serviceAndInstance -> {
                long timestamp = PhoenixRegistryService.TIMESTAMP.get(serviceAndInstance);
                if (now - timestamp > timeout) {
                    log.info("  ====> Health checker: {} is down", serviceAndInstance);
                    int index = serviceAndInstance.indexOf("@");
                    String service = serviceAndInstance.substring(0, index);
                    String url = serviceAndInstance.substring(index + 1);
                    InstanceMeta instance = InstanceMeta.from(url);
                    registryService.unregister(service, instance);
                    PhoenixRegistryService.TIMESTAMP.remove(serviceAndInstance);
                }
            });
        }, 10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {

    }
}
