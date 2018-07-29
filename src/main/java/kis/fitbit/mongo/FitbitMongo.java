/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kis.fitbit.mongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import kis.fitbit.DailySleep;
import kis.fitbit.DailySleep.Sleep;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

/**
 * mongo access.
 * >docker volume create mongo_volume
 * >docker run -p 27017:27017
 *      --mount source=mongo_volume,target=/data/db --name fitbit-mongo -d mongo
 * @author naoki
 */
public class FitbitMongo implements Closeable{
    private MongoClient client;
    private MongoDatabase db;
    private MongoCollection<MongoSleep> sleepCol;
    public FitbitMongo(String url) {
        var registries = CodecRegistries.fromRegistries(
                com.mongodb.MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        var settings = MongoClientSettings.builder()
                .codecRegistry(registries)
                .applyConnectionString(new ConnectionString(url))
                .build();
        
        client = MongoClients.create(settings);
        db = client.getDatabase("fitbit");
        
        createIndex(getSleepCol(), true, "user", "startTime");
    }
    
    public FitbitMongo() {
        this("mongodb://localhost:27017");
    }
    
    public static void main(String[] args) throws IOException {
        var mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        DailySleep sleep = mapper.readValue(new FileReader("sleep.json"), DailySleep.class);

        try (var con = new FitbitMongo()) {
            con.retrieveSleep("kishida", LocalDate.of(2018, 6, 24)).forEach(System.out::println);
        }
        
        var msleep = new MongoSleep(null, "kishida", sleep.getSleep().get(0));
        // sleepCol.insertOne(msleep);
        
        //var col = db.getCollection("test");
        //col.insertOne(new Document().append("name", "naoki").append("lang", "java"));
        
    }
    
    private MongoCollection<MongoSleep> getSleepCol() {
        if (sleepCol == null) {
            sleepCol = db.getCollection("sleeps", MongoSleep.class);
        }
        return sleepCol;
    }
    
    public void addSleeps(String user, DailySleep sleeps) {
        getSleepCol().insertMany(sleeps.getSleep().stream()
                .map(s -> new MongoSleep(null, user, s))
                .collect(Collectors.toUnmodifiableList()));
    }
    public void addSleeps(String user, DailySleep.RangeSleep sleeps) {
        getSleepCol().insertMany(sleeps.getSleep().stream()
                .map(s -> new MongoSleep(null, user, s))
                .collect(Collectors.toUnmodifiableList()));
    }
    
    public List<MongoSleep> retrieveSleep(String user, LocalDate date) {
        return retrieve(getSleepCol(), Map.of(
                "user", user,
                "sleepData.dateOfSleep", date));
    }
    
    public List<MongoSleep> retrieveSleep(String user, LocalDate start, LocalDate end) {
        return retrieve(getSleepCol(), 
                Map.of("user", user,
                       "sleepData.dateOfSleep",
                             new BasicDBObject(Map.of("$gte", start, "$lte", end))));
    }
    private static <T> List<T> retrieve(MongoCollection<T> col, Map<String, Object> attr) {
        return StreamSupport.stream(
                col.find(new BasicDBObject(attr))
                   .spliterator(), false).collect(Collectors.toUnmodifiableList());
    }
    
    static void createIndex(MongoCollection<?> col, boolean unique, String... fields) {
        var index = Indexes.ascending(fields);
        var opt = new IndexOptions().unique(unique);
        col.createIndex(index, opt);
    }

    @Override
    public void close() {
        client.close();
    }
    
    @Data
    @NoArgsConstructor
    public static class MongoSleep {
        ObjectId id;
        String user;
        LocalDateTime startTime;
        Sleep sleepData;

        public MongoSleep(ObjectId id, String user, Sleep sleepData) {
            this.id = id;
            this.user = user;
            this.sleepData = sleepData;
            startTime = sleepData.getStartTime();
        }
    }
}
