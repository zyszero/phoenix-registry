package io.github.zyszero.phoenix.registry;

import io.github.zyszero.phoenix.registry.cluster.Cluster;
import io.github.zyszero.phoenix.registry.cluster.Server;
import io.github.zyszero.phoenix.registry.cluster.Snapshot;
import io.github.zyszero.phoenix.registry.model.InstanceMeta;
import io.github.zyszero.phoenix.registry.service.PhoenixRegistryService;
import io.github.zyszero.phoenix.registry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Rest controller for registry service.
 *
 * @Author: zyszero
 * @Date: 2024/4/30 6:55
 */
@RestController
@Slf4j
public class PhoenixRegistryController {

    @Autowired
    private RegistryService registryService;


    @Autowired
    private Cluster cluster;


    @RequestMapping("/register")
    public InstanceMeta register(@RequestParam("service") String service, @RequestBody InstanceMeta instance) {
        checkLeader();
        log.info(" ====> register service: {}, instance: {}", service, instance);
        return registryService.register(service, instance);
    }

    private void checkLeader() {
        if (!cluster.self().isLeader()) {
            throw new RuntimeException("current server is not a leader, the leader is " + cluster.leader());
        }
    }

    @RequestMapping("/unregister")
    public InstanceMeta unregister(@RequestParam("service") String service, @RequestBody InstanceMeta instance) {
        log.info(" ====> unregister service: {}, instance: {}", service, instance);
        return registryService.unregister(service, instance);
    }


    @RequestMapping("/findAll")
    public List<InstanceMeta> getAllInstances(@RequestParam("service") String service) {
        log.info(" ====> get all instances for service: {}", service);
        return registryService.getAllInstances(service);
    }


    @RequestMapping("/renew")
    public Long renew(@RequestParam("service") String service, @RequestBody InstanceMeta instance) {
        log.info(" ====> renew service: {}, instance: {}", instance, service);
        return registryService.renew(instance, service);
    }

    @RequestMapping("/renews")
    public Long renews(@RequestParam("services") String services, @RequestBody InstanceMeta instance) {
        log.info(" ====> renew services: {}", services);
        return registryService.renew(instance, services.split(","));
    }

    @RequestMapping("/version")
    public Long version(@RequestParam("service") String service) {
        log.info(" ====> version: {}", service);
        return registryService.version(service);
    }

    @RequestMapping("/versions")
    public Map<String, Long> versions(@RequestParam("services") String services) {
        log.info(" ====> versions : {}", services);
        return registryService.versions(services.split(","));
    }


    @RequestMapping("/info")
    public Server info() {
        Server server = cluster.self();
        log.info(" ====> server info: {}", server);
        return server;
    }


    @RequestMapping("/cluster")
    public List<Server> cluster() {
        List<Server> servers = cluster.getServers();
        log.info(" ====> cluster info: {}", servers);
        return servers;
    }

    @RequestMapping("/leader")
    public Server leader() {
        Server server = cluster.leader();
        log.info(" ====> leader: {}", server);
        return server;
    }

    @RequestMapping("/sl")
    public Server sl() {
        Server server = cluster.self();
        server.setLeader(true);
        log.info(" ====> leader: {}", server);
        return server;
    }

    @RequestMapping("/snapshot")
    public Snapshot snapshot() {
        return PhoenixRegistryService.snapshot();
    }
}
