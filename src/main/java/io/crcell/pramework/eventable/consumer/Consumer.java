package io.crcell.pramework.eventable.consumer;

public interface Consumer<T, ID> {
  T handleSave(T entity);

  Boolean handleDelete(ID id);

}
