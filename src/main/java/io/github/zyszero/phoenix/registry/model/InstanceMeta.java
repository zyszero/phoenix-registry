package io.github.zyszero.phoenix.registry.model;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * instance meta model.
 * @Author: zyszero
 * @Date: 2024/4/30 6:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"scheme", "host", "port", "context"})
public class InstanceMeta {

    private String scheme;

    private String host;

    private Integer port;

    private String context; // dubbo url?k1=v1

    private boolean status; // online or offline

    public InstanceMeta(String scheme, String host, Integer port, String context) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    /**
     * 打标记
     * 比如：机房、单元等
     * idc A B C
     */
    private Map<String, String> parameters = new HashMap<>();

    public String toPath() {
        return String.format("%s_%d", host, port);
    }


    public static InstanceMeta http(String host, Integer port) {
        return new InstanceMeta("http", host, port, "phoenix-rpc");
    }

    public static InstanceMeta from(String url) {
        URI uri = URI.create(url);
        return new InstanceMeta(uri.getScheme(),
                                uri.getHost(),
                                uri.getPort(),
                                uri.getPath().substring(1));
    }


    public String toUrl() {
        return String.format("%s://%s:%d/%s", scheme, host, port, context);
    }

    public String toMetas() {
        return JSON.toJSONString(this.parameters);

    }

    public InstanceMeta addParams(Map<String, String> params) {
        this.getParameters().putAll(params);
        return this;
    }
}