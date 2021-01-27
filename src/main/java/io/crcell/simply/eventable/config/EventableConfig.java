package io.crcell.simply.eventable.config;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter
@RequiredArgsConstructor
@Configuration
public class EventableConfig {

    @Value("${simply.eventable.entity-base-package}")
    private String basePackage;

    @Value("${simply.eventable.topic-property.number-of-partitions:1}")
    private Integer numPartitions;

    @Value("${simply.eventable.topic-property.number-of-replicas:1}")
    private Short numReplicas;

}
