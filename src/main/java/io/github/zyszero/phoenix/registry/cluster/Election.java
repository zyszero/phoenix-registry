package io.github.zyszero.phoenix.registry.cluster;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * elect leader for servers.
 *
 * @Author: zyszero
 * @Date: 2024/5/11 4:38
 */
@Slf4j
public class Election {

    public static void electLeader(List<Server> servers) {
        List<Server> masters = servers.stream()
                .filter(Server::isStatus)
                .filter(Server::isLeader)
                .toList();
        if (masters.isEmpty()) {
            log.warn(" ===>>> [ELECT] elect for no leaders: {}", servers);
            elect(servers);
        } else if (masters.size() > 1) {
            log.warn(" ===>>> [ELECT] elect for more than one master: {}", servers);
            elect(servers);
        } else {
            log.debug(" ===>>> no need election for leader: {}", masters.get(0));
        }
    }

    private static void elect(List<Server> servers) {
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
}
