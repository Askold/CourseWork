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
import ru.sfedu.crswork.Constants;
import ru.sfedu.crswork.Models.*;
import ru.sfedu.crswork.Utils.ConfigurationUtil;

import java.io.IOException;
import java.util.Optional;
/**
 * Abstract DataProvider class with required methods
 * Includes:
 * - CRUD for all beans
 * - methods implemented from use case diagram
 * CRUD explained only for Trainer class, all other beans have the same structure and principles
 * @author Kirill Silonov
 */
public abstract class DataProvider {
    private static final Logger logger = LogManager.getLogger(App.class);

    // ---------- Trainer CRUD ----------

    /**
     * inserting Trainer class object into the data source
     * @param trainer Trainer class object
     * @return result of inserting
     */
    abstract boolean insertTrainer(Trainer trainer);

    /**
     * Looking for record from data source with specific ID
     * @param id ID of the sought object
     * @return an object of Trainer class wrapped in Optional
     */
    abstract Optional<Trainer> getTrainerById(long id);

    /**
     * Updating an existing record in data source
     * @param trainer updated object
     * @return result of updating
     */
    abstract boolean updateTrainer(Trainer trainer);

    /**
     * Delete record from data source by ID
     * @param id ID of record intended to be deleted
     * @return result of deleting
     */
    abstract boolean deleteTrainerById(long id);

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

    /**
     * checks if the client has completed the workout and displays the feedback if he has
     * @param id Client id
     * @return result of checking
     */
    public abstract boolean checkClient(long id);

    /**
     * displays the feedback
     * @param client object of Client class
     * @return result of displaying
     */
    abstract boolean viewFeedback(long client);

    // method getClientById() is in Client CRUD section

    /**
     * Creates new workout and inserts it to data source
     * @param type type of workout (enum)
     * @param client id of Client class object
     * @param trainer id of Trainer class object
     * @return result of creating
     */
    public abstract boolean createWorkout(Workout.WorkoutType type, long client, long trainer);

    /**
     * creates new exercise and inserts it to data source
     * @param name name of the new exercise
     * @param weight weight of attribute that will be used in the exercise
     * @param repetitions amount of repetitions client used to do
     * @param rounds amount of repetitions client used to do
     * @param workout id of Workout class object
     * @return result of creating
     */
    public abstract boolean createExercise(String name, int weight, int repetitions, int rounds, long workout);

    //---------Client role

    /**
     * Used by client to view workout information and mark it as completed  if it true
     * @param workoutID id of Workout class object
     * @param isCompleted false flag if is empty, or contains feedback data
     * @return result of viewing and making up a feedback
     */
    public abstract boolean executeWorkout(long workoutID, String isCompleted);

    /**
     * Displays workout
     * @param workoutID id od Workout object
     * @return result of displaying
     */
    abstract boolean viewWorkout(long workoutID);

    /**
     * creates feedback by client and inserts it to data source
     * @param isCompleted conserve client's estimate enum represented in string
     * @return result of creating
     */
    abstract long composeFeedback(String isCompleted);

    // ----------- MongoDB history --------------

    /**
     *
     * @param record
     */
    public void addHistoryRecord(HistoryContent record){
        String recordJson = new Gson().toJson(record);
        logger.info(recordJson);
        MongoDatabase database = connectToDB().orElseThrow();
        MongoCollection collection = receiveCollection(database).orElseThrow();
        collection.insertOne(Document.parse(recordJson));
        logger.debug(Constants.HISTORY_ADDED);
    }

    /**
     * connects to MongoDB
     * @return object of MongoDatabase class
     */
    private static Optional<MongoDatabase> connectToDB(){
        String localhost = "";
        int port = 0;
        try {
            localhost = ConfigurationUtil.getConfigurationEntry(Constants.MONGO_LOCALHOST);
            port = Integer.parseInt(ConfigurationUtil.getConfigurationEntry(Constants.MONGO_PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }

        MongoClient mongoClient = new MongoClient(localhost, port);
        MongoDatabase database = mongoClient.getDatabase(Constants.MONGODB_NAME);
        logger.debug(Constants.CONNECTED_TO_MONGO);
        return Optional.of(database);
    }

    /**
     * receives collection from MongoDB
     * @param database object of MongoDatabase class
     * @return object of MongoCollection class
     */
    private static Optional<MongoCollection> receiveCollection(MongoDatabase database){
        try {
            database.createCollection(Constants.COLLECTION_NAME);
            logger.debug(Constants.COLLECTION_CREATED);
        }catch (MongoCommandException e){
            logger.info(e.getClass().getName() + e.getMessage());
        }
        MongoCollection<Document> collection = database.getCollection(Constants.COLLECTION_NAME);
        logger.debug(Constants.COLLECTION_RECIEVED);
        return Optional.of(collection);
    }


}
