package edu.northeastern.cs5500.delivery.repository;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import edu.northeastern.cs5500.delivery.model.Model;
import edu.northeastern.cs5500.delivery.service.MongoDBService;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

@Slf4j
public class MongoDBRepository<T extends Model> implements GenericRepository<T> {

    MongoCollection<T> collection;

    @Inject
    public MongoDBRepository(Class<T> clazz, MongoDBService mongoDBService) {
        MongoDatabase mongoDatabase = mongoDBService.getMongoDatabase();
        collection = mongoDatabase.getCollection(clazz.getName(), clazz);
    }

    @Nullable
    public T get(ObjectId id) {
        return collection.find(eq("_id", id)).first();
    }

    @Override
    public T add(T item) {
        if (item.getId() == null) {
            item.setId(new ObjectId());
        }
        collection.insertOne(item);
        return item;
    }

    @Override
    public T update(T item) {
        return collection.findOneAndReplace(eq("_id", item.getId()), item);
    }

    @Override
    public void delete(ObjectId id) {
        collection.deleteOne(eq("_id", id));
    }

    @Override
    public Collection<T> getAll() {
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public long count() {
        return collection.countDocuments();
    }

    @Override
    public T findWithField(String field, String val) {
        Bson bsonFilter = Filters.eq(field, val);
        FindIterable<T> findIt = collection.find(bsonFilter);
        if (findIt == null) {
            return null;
        }
        return findIt.first();
    }
}
