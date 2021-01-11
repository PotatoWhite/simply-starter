package io.crcell.pramework.eventable.repository;

import io.crcell.pramework.eventable.Eventable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EventableRepository<T1 extends Eventable, T2> extends JpaRepository<T1, T2> {
}
