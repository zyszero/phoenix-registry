package io.github.zyszero.phoenix.registry.cluster;

import io.github.zyszero.phoenix.registry.model.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Map;

/**
 *
 * @Author: zyszero
 * @Date: 2024/5/10 0:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Snapshot {

    private LinkedMultiValueMap<String, InstanceMeta> registry;

    private Map<String, Long> versions;

    public Map<String, Long> timestamps;

    private long version;
}
