package io.github.zyszero.phoenix.registry.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Register server instance.
 *
 * @Author: zyszero
 * @Date: 2024/5/5 16:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"url"})
public class Server {
    private String url;

    private boolean status;

    private boolean leader;

    private long version;
}
