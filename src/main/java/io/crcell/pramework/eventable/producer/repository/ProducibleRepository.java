package io.crcell.pramework.eventable.producer.repository;

import io.crcell.pramework.eventable.producer.Eventable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ProducibleRepository<T1 extends Eventable, T2> extends JpaRepository<T1, T2> {
}
