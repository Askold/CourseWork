package org.example.DataProviders;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.example.App;
import org.example.Models.*;

import java.io.File;
import java.util.List;
import java.util.Optional;

public abstract class DataProvider {
    private static final Logger logger = LogManager.getLogger(App.class);

    // ---------- Trainer CRUD ----------

    abstract File initDataSource(Class<?> type);

    abstract <T> boolean saveRecords(List<T> beans);

    abstract <T> List<T> selectRecords(Class<?> type);

    // ---------- Trainer CRUD ----------

    abstract boolean insertTrainer(Trainer trainer);

    abstract boolean deleteTrainerById(long id);

    abstract Optional<Trainer> getTrainerById(long id);

    abstract boolean updateTrainer(Trainer trainer);

    // ---------- Client CRUD ----------

    abstract boolean insertClient(Client client);

    abstract Optional<Client> getClientById(long id);

    abstract boolean updateClient(Client client);

    // ---------- Exercise CRUD ----------

    abstract boolean insertExercise(Exercise exercise);

    abstract Optional<Exercise> getExerciseById(long id);

    // ---------- Workout CRUD ----------

    abstract boolean insertWorkout(Workout workout);

    abstract Optional<Workout> getWorkoutById(long id);

    abstract boolean updateWorkout(Workout workout);

    // ---------- Feedback CRUD ----------

    abstract boolean insertFeedback(Feedback feedback);

    abstract Optional<Feedback> getFeedbackById(long id);

    // ---------- Program CRUD ----------

    abstract boolean createNewProgram(Program program);

    abstract Optional<Program> getProgramById(long id);

    // ---------- Use-case implementation ----------
    //Trainer role

    //abstract boolean composeProgram(int workoutsAmount, int durationInWeeks, int workoutsPerWeek,
    //                                       String description, Program.ProgramType type, long clientId);
//
    //abstract long composeWorkout(List<Long> exercisesIds, List<Integer> weights, List<Integer> rounds,
    //                                List<Integer> repetitions);

    abstract Optional<Exercise> addNewExercise(long id);

    abstract Optional<Feedback> viewFeedback(Client client);

    //Client role
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
