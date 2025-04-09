package com.aml_service.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
    private String host;
    private String port;
    private String inboundTopic;
    private String outboundTopic;
    private String groupId;
    private String bootstrapServers;
}
