package edu.northeastern.cs5500.delivery.repository;

import java.util.Collection;
import javax.annotation.Nonnull;
import org.bson.types.ObjectId;

public interface GenericRepository<T> {
    public T get(@Nonnull ObjectId id);

    public T add(@Nonnull T item);

    public T update(@Nonnull T item);

    public void delete(@Nonnull ObjectId id);

    public Collection<T> getAll();

    public long count();

    public T findWithField(String field, String val);
}
