package io.easywalk.simply.eventable.kafka.producer;

import javax.persistence.MappedSuperclass;


@MappedSuperclass
public interface Eventable<ID> {
    ID getId();
}
