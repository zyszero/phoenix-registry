package io.github.zyszero.phoenix.registry.health;

/**
 * Interface for health checker.
 * @Author: zyszero
 * @Date: 2024/5/5 15:13
 */
public interface HealthChecker {

    void start();

    void stop();
}
