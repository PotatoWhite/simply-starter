package io.easywalk.simply.eventable.producer;

import javax.persistence.MappedSuperclass;


@MappedSuperclass
public interface Eventable<ID> {
    ID getId();
}
