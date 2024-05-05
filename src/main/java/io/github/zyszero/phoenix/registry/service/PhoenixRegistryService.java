package io.github.zyszero.phoenix.registry.service;

import io.github.zyszero.phoenix.registry.model.InstanceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default implementation of RegistryService.
 *
 * @Author: zyszero
 * @Date: 2024/4/30 6:33
 */
@Slf4j
public class PhoenixRegistryService implements RegistryService {

    private final static MultiValueMap<String, InstanceMeta> REGISTRY = new LinkedMultiValueMap<>();


    private final static Map<String, Long> VERSIONS = new ConcurrentHashMap<>();

    public final static Map<String, Long> TIMESTAMP = new ConcurrentHashMap<>();

    private final static AtomicLong VERSION = new AtomicLong(0);


    @Override
    public InstanceMeta register(String service, InstanceMeta instance) {
        if (instance == null) {
            return null;
        }
        List<InstanceMeta> metas = REGISTRY.get(service);
        if (metas != null && !metas.isEmpty()) {
            if (metas.contains(instance)) {
                instance.setStatus(true);
                log.info(" ====> instance already registered: {}", instance.toUrl());
                return instance;
            }
        }
        log.info(" ====> register instance: {}", instance.toUrl());
        instance.setStatus(true);
        REGISTRY.add(service, instance);
        renew(instance, service);
        VERSIONS.put(service, VERSION.incrementAndGet());
        return instance;
    }

    @Override
    public InstanceMeta unregister(String service, InstanceMeta instance) {
        if (instance == null) {
            return null;
        }
        List<InstanceMeta> metas = REGISTRY.get(service);
        if (metas == null || metas.isEmpty()) {
            log.info(" ====> instance not found: {}", instance.toUrl());
            return null;
        }

        log.info(" ====> unregister instance: {}", instance.toUrl());
        metas.removeIf(meta -> meta.equals(instance));
        instance.setStatus(false);
        VERSIONS.put(service, VERSION.incrementAndGet());
        return instance;
    }

    @Override
    public List<InstanceMeta> getAllInstances(String service) {
        return REGISTRY.get(service);
    }


    @Override
    public Long renew(InstanceMeta instance, String... services) {
        if (instance == null) {
            return null;
        }
        log.info(" ====> renew instance: {}", instance.toUrl());
        long now = System.currentTimeMillis();
        for (String service : services) {
            TIMESTAMP.put(service + "@" + instance.toUrl(), now);
        }
        return now;
    }


    @Override
    public Long version(String service) {
        return VERSIONS.get(service);
    }


    @Override
    public Map<String, Long> versions(String... services) {
        Map<String, Long> versions = new HashMap<>();
        for (String service : services) {
            versions.put(service, VERSIONS.get(service));
        }
        return versions;
    }

}
