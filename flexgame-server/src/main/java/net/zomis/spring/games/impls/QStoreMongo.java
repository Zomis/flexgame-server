package net.zomis.spring.games.impls;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import net.zomis.spring.games.impls.qlearn.QStore;
import org.bson.Document;

public class QStoreMongo<S> implements QStore<S> {

    private static final String KEY = "key";
    private static final String VALUE = "value";

    private final MongoCollection<Document> collection;
    private final UpdateOptions updateOptions = new UpdateOptions().upsert(true);

    public QStoreMongo(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    @Override
    public double getOrDefault(S key, double defaultValue) {
        FindIterable<Document> match = collection.find(Filters.eq(KEY, key));
        Document first = match.first();
        if (first != null) {
            return (double) first.get(VALUE);
        } else {
/*            Document document = new Document()
                .append(KEY, key)
                .append(VALUE, defaultValue);
            collection.insertOne(document);*/
            return defaultValue;
        }
    }

    @Override
    public void put(S key, double value) {
        // System.out.println("Found value " + value + " for key " + key);
        collection.updateOne(Filters.eq(KEY, key), Updates.set(VALUE, value), updateOptions);
    }

    @Override
    public long size() {
        return collection.count();
    }

}
