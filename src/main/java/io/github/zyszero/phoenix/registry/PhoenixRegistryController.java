package io.github.zyszero.phoenix.registry;

import com.alibaba.fastjson.JSON;
import io.github.zyszero.phoenix.registry.model.InstanceMeta;
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


    @RequestMapping("/register")
    public InstanceMeta register(@RequestParam("service") String service, @RequestBody InstanceMeta instance) {
        log.info(" ====> register service: {}, instance: {}", service, instance);
        return registryService.register(service, instance);
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
        log.info(" ====> get version for service: {}", service);
        return registryService.version(service);
    }

    @RequestMapping("/versions")
    public Map<String, Long> versions(@RequestParam("services") String services) {
        log.info(" ====> get versions for services: {}", services);
        return registryService.versions(services.split(","));
    }


    public static void main(String[] args) {
        InstanceMeta instanceMeta = InstanceMeta.http("127.0.0.1", 8484)
                .addParams(Map.of("env", "dev", "tag", "RED"));
        System.out.println(JSON.toJSONString(instanceMeta));
    }

}
