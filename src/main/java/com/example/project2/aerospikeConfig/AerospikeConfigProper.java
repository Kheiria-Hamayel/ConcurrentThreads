package com.example.project2.aerospikeConfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aerospike")
public class AerospikeConfigProper {
    private String host;
    private int port;
    private  String namespace;

}
