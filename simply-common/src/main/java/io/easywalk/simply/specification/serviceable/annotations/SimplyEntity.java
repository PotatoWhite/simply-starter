package io.easywalk.simply.specification.serviceable.annotations;

import javax.persistence.MappedSuperclass;


@MappedSuperclass
public interface SimplyEntity<ID> {
    ID getId();
}
