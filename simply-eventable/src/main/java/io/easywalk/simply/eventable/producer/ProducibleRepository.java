package io.easywalk.simply.eventable.producer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ProducibleRepository<T1 extends Eventable, T2> extends JpaRepository<T1, T2> {
}
