package io.github.zyszero.phoenix.registry.cluster;

import io.github.zyszero.phoenix.registry.http.HttpInvoker;
import io.github.zyszero.phoenix.registry.service.PhoenixRegistryService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * check health of server.
 * @Author: zyszero
 * @Date: 2024/5/11 4:29
 */
@Slf4j
public class ServerHealth {

    final Cluster cluster;

    final ScheduledExecutorService executor =
            Executors.newScheduledThreadPool(1);

    long interval = 5_000;

    public ServerHealth(Cluster cluster) {
        this.cluster = cluster;
    }

    public void checkServerHealth() {
        executor.scheduleWithFixedDelay(() -> {
            try {
                updateServers(); // 1. 更新服务期状态
                doElect();   // 2. 选主
                syncSnapshotFromLeader(); // 3. 同步快照
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 0, interval, TimeUnit.MILLISECONDS);
    }

    private void doElect() {
        Election.electLeader(cluster.getServers());
    }


    private void updateServers() {
        Server self = cluster.self();
        cluster.getServers().parallelStream().forEach(server -> {
            try {
                if (server.equals(self)) {
                    return;
                }
                Server serverInfo = HttpInvoker.httpGet(server.getUrl() + "/info", Server.class);
                log.debug(" ===>>> health check success for {}", serverInfo);
                if (serverInfo != null) {
                    server.setStatus(true);
                    server.setLeader(serverInfo.isLeader());
                    server.setVersion(serverInfo.getVersion());
                }
            } catch (Exception ex) {
                log.debug(" ===>>> health check failed for {}", server);
                server.setStatus(false);
                server.setLeader(false);
            }
        });

    }

    private void syncSnapshotFromLeader() {
        Server self = cluster.self();
        Server leader = cluster.leader();

        log.debug(" ===>>> leader version: {}, my version: {}",leader.getVersion(), self.getVersion());
        if (!self.isLeader() && self.getVersion() < leader.getVersion()) {
            log.debug(" ===>>> sync snapshot from leader: {}", leader);
            Snapshot snapshot = HttpInvoker.httpGet(leader.getUrl() + "/snapshot", Snapshot.class);
            log.debug(" ===>>> sync and restore snapshot {}", snapshot);
            PhoenixRegistryService.restore(snapshot);
        }
    }


}
