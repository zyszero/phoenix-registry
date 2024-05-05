package io.github.zyszero.phoenix.registry;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * registry config properties.
 *
 * @Author: zyszero
 * @Date: 2024/5/5 16:53
 */
@Data
@ConfigurationProperties(prefix = "phoenix.registry")
public class PhoenixRegistryConfigProperties {
    private List<String> services;
}
