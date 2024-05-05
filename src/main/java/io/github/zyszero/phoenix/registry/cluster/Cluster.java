package io.github.zyszero.phoenix.registry.cluster;

import io.github.zyszero.phoenix.registry.PhoenixRegistryConfigProperties;
import io.github.zyszero.phoenix.registry.http.HttpInvoker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


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

    String host = new InetUtils(new InetUtilsProperties()).findFirstNonLoopbackAddress().getHostAddress();

    Server MYSELF;


    private final PhoenixRegistryConfigProperties registryProperties;

    public Cluster(PhoenixRegistryConfigProperties registryProperties) {
        this.registryProperties = registryProperties;
    }


    final ScheduledExecutorService executor =
            Executors.newScheduledThreadPool(1);

    long timeout = 5_000;

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
        // todo ...
        this.servers = servers;


        executor.scheduleWithFixedDelay(() -> {
            // check health
            updateServers();
            electLeader();
        }, 0, timeout, TimeUnit.MILLISECONDS);
    }

    private void electLeader() {
        List<Server> masters = servers.stream().filter(Server::isStatus)
                .filter(Server::isLeader)
                .toList();
        if (masters.isEmpty()) {
            log.info(" ===>>> elect for no leaders: {}", servers);
            elect();
        } else if (masters.size() > 1) {
            log.info(" ===>>> elect for more than one master: {}", servers);
            elect();
        } else {
            log.info(" ===>>> no need election for leader: {}", masters.get(0));
        }
    }

    private void elect() {
        // 1.各种节点自己选，算法保证大家选的是同一个
        // 2.外部有一个分布式锁，谁拿到锁，谁是主
        // 3.分布式一致性算法，比如paxos,raft，，很复杂
        Server candidate = null;
        for (Server server : servers) {
            server.setLeader(false);
            if (server.isStatus()) {
                if (candidate == null) {
                    candidate = server;
                } else {
                    if (candidate.hashCode() > server.hashCode()) {
                        candidate = server;
                    }
                }
            }
        }

        if (candidate != null) {
            candidate.setLeader(true);
            log.info(" ===>>> elected leader: {}", candidate);
        } else {
            log.info(" ===>>> elected failed for no leaders: {}", servers);
        }
    }

    private void updateServers() {
        servers.forEach(server -> {
            try {
                Server serverInfo = HttpInvoker.httpGet(server.getUrl() + "/info", Server.class);
                log.info(" ===>>> health check success for {}", serverInfo);
                if (serverInfo != null) {
                    server.setStatus(true);
                    server.setLeader(serverInfo.isLeader());
                    server.setVersion(serverInfo.getVersion());
                }
            } catch (Exception ex) {
                log.info(" ===>>> health check failed for {}", server);
                server.setStatus(false);
                server.setLeader(false);
            }
        });

    }

    public Server self() {
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
