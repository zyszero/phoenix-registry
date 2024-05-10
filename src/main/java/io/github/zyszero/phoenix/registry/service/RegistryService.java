package io.github.zyszero.phoenix.registry.service;

import io.github.zyszero.phoenix.registry.cluster.Snapshot;
import io.github.zyszero.phoenix.registry.model.InstanceMeta;

import java.util.List;
import java.util.Map;

/**
 * Interface for registry service.
 * @Author: zyszero
 * @Date: 2024/4/30 6:28
 */
public interface RegistryService {

    // 最基础的3个方法
    InstanceMeta register(String service, InstanceMeta instance);

    InstanceMeta unregister(String service, InstanceMeta instance);

    List<InstanceMeta> getAllInstances(String service);

    // TODO 添加一些高级功能
    Long renew(InstanceMeta instance, String... service);

    Long version(String service);

    Map<String, Long> versions(String... services);




}
