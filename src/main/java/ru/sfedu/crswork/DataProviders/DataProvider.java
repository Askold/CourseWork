package ru.sfedu.crswork.DataProviders;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import ru.sfedu.crswork.App;
import ru.sfedu.crswork.Models.*;

import java.io.File;
import java.util.List;
import java.util.Optional;

public abstract class DataProvider {
    private static final Logger logger = LogManager.getLogger(App.class);

    // ---------- General methods ----------

    //abstract <T> List<T> selectRecords(Class<?> type);

    // ---------- Trainer CRUD ----------

    abstract boolean insertTrainer(Trainer trainer);

    abstract boolean deleteTrainerById(long id);

    abstract Optional<Trainer> getTrainerById(long id);

    abstract boolean updateTrainer(Trainer trainer);

    // ---------- Client CRUD ----------

    abstract boolean insertClient(Client client);

    abstract Optional<Client> getClientById(long id);

    abstract boolean updateClient(Client client);

    abstract boolean deleteClientById(long id);

    // ---------- Exercise CRUD ----------

    abstract boolean insertExercise(Exercise exercise);

    abstract Optional<Exercise> getExerciseById(long id);

    // ---------- Workout CRUD ----------

    abstract boolean insertWorkout(Workout workout);

    abstract Optional<Workout> getWorkoutById(long id);

    abstract boolean updateWorkout(Workout workout);

    abstract boolean deleteWorkoutById(long id);

    // ---------- Feedback CRUD ----------

    abstract boolean insertFeedback(Feedback feedback);

    abstract Optional<Feedback> getFeedbackById(long id);

    // ---------- Use cases implementation ----------
    //---------Trainer role
    //-------checkClient use case
    abstract boolean checkClient(long id);

    abstract boolean viewFeedback(long client);

    // method getClientById() is in Client CRUD section

    // other cases
    abstract boolean createWorkout(String type, long client, long trainer);

    abstract boolean createExercise(String name, int weight, int repetitions, int rounds, long workout);

    //---------Client role

    abstract boolean executeWorkout(long workoutID, String isCompleted);

    abstract boolean viewWorkout(long workoutID);

    abstract long composeFeedback(String isCompleted);

    // ----------- MongoDB history --------------

    public void addHistoryRecord(HistoryContent record){
        String recordJson = new Gson().toJson(record);
        logger.info(recordJson);
        MongoDatabase database = connectToDB();
        MongoCollection collection = receiveCollection(database);
        collection.insertOne(Document.parse(recordJson));
        logger.debug("New History record added");
    }

    private static MongoDatabase connectToDB(){
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase("myMongoDb");
        logger.debug("Connected to MongoDB");
        return database;
    }

    private static MongoCollection receiveCollection(MongoDatabase database){
        try {
            database.createCollection("HistoryContent");
            logger.debug("MongoDB collection created");
        }catch (MongoCommandException e){
            logger.error(e.getClass().getName() + e.getMessage()); ;
        }
        MongoCollection<Document> collection = database.getCollection("HistoryContent");
        logger.debug("MongoDB collection received");
        return collection;
    }


}
