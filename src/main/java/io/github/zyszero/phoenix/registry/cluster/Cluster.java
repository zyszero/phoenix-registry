package io.github.zyszero.phoenix.registry.cluster;

import io.github.zyszero.phoenix.registry.PhoenixRegistryConfigProperties;
import io.github.zyszero.phoenix.registry.service.PhoenixRegistryService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.util.ArrayList;
import java.util.List;


/**
 * Registry cluster.
 * 探活 + 选主
 *
 * @Author: zyszero
 * @Date: 2024/5/5 16:42
 */
@Data
@Slf4j
public class Cluster {

    @Value("${server.port}")
    String port;

    String host;

    Server MYSELF;


    private final PhoenixRegistryConfigProperties registryProperties;

    public Cluster(PhoenixRegistryConfigProperties registryProperties) {
        this.registryProperties = registryProperties;
    }


    private List<Server> servers;

    public void init() {
        try {
            host = new InetUtils(new InetUtilsProperties()).findFirstNonLoopbackAddress().getHostAddress();
            log.info(" ===> findFirstNonLoopbackAddress: {}", host);
        } catch (Exception e) {
            host = "127.0.0.1";
        }

        MYSELF = new Server("http://" + host + ":" + port, true, false, -1L);
        log.info(" ===> myself: {}", MYSELF);

        initServers();

        ServerHealth serverHealth = new ServerHealth(this);
        serverHealth.checkServerHealth();

    }

    private void initServers() {
        List<Server> servers = new ArrayList<>();
        // init servers
        for (String url : registryProperties.getServices()) {
            Server server = new Server();
            if (url.contains("localhost")) {
                url = url.replace("localhost", host);
            } else if (url.contains("127.0.0.1")) {
                url = url.replace("127.0.0.1", host);
            }

            if (url.equals(MYSELF.getUrl())) {
                servers.add(MYSELF);
            } else {
                server.setUrl(url);
                server.setStatus(true);
                server.setLeader(false);
                server.setVersion(-1L);
                servers.add(server);
            }
        }
        this.servers = servers;
    }


    public Server self() {
        MYSELF.setVersion(PhoenixRegistryService.VERSION.get());
        return MYSELF;
    }

    public Server leader() {
        return servers.stream()
                .filter(Server::isStatus)
                .filter(Server::isLeader)
                .findFirst()
                .orElse(null);
    }
}
