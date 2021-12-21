package ru.sfedu.crswork.DataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import ru.sfedu.crswork.App;
import ru.sfedu.crswork.Constants;
import ru.sfedu.crswork.Models.*;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class DataProviderDBTest {
    private static final Logger logger = LogManager.getLogger(DataProviderDBTest.class);
    DataProviderDB dp = new DataProviderDB();

    List<Trainer> trainerList = List.of(
            new Trainer("Alex", "Powerlifter", 5, 10),
            new Trainer("Drake", "Natan", 3, 6),
            new Trainer("William", "Defoe", 1, 9),
            new Trainer("Mathew", "Mcconaughey", 8, 10));

    List<Client> clientList = List.of(
            new Client("Hairy", "Schlong", 24, 78, 182),
            new Client("Davy", "Farter", 20, 80, 190),
            new Client("Macky", "Mclovin", 12, 60, 160),
            new Client("Wendy", "Tour", 40, 80, 185)
    );
    List<Exercise> exerciseList = List.of(
            new Exercise("Exercise1", 15, 15, 3, 1),
            new Exercise("Exercise2", 20, 10, 4, 1),
            new Exercise("Exercise3", 100, 12, 5, 2),
            new Exercise("Exercise4", 58, 10, 4, 2)
    );
    List<Feedback> feedbackList = List.of(
            new Feedback(Feedback.Estimate.COMMON)
    );
    List<Workout> workoutList = List.of(
            new Workout(1, Workout.WorkoutType.AEROBIC, clientList.get(0).getId(), trainerList.get(0).getId(),
                    feedbackList.get(0).getId()),
            new Workout(2, Workout.WorkoutType.STRENGTH, clientList.get(1).getId(), trainerList.get(1).getId())
    );


    @Before
    public void insertRecords() {
        testInsertTrainer();
        testInsertClient();
        testInsertWorkout();
        testInsertExercise();
        testInsertFeedback();
    }

    @Test
    public void clearTables(){
        assertTrue(dp.clearTable(Trainer.class));
        assertTrue(dp.clearTable(Client.class));
        assertTrue(dp.clearTable(Exercise.class));
        assertTrue(dp.clearTable(Workout.class));
        assertTrue(dp.clearTable(Feedback.class));
    }

    @Test
    public void testInsertTrainer() {
        trainerList.forEach(x -> dp.insertTrainer(x));
        List<Trainer> trainers = dp.selectTrainers();
        assertFalse(trainers.isEmpty());
    }

    @Test
    public void testInsertClient() {
        clientList.forEach(x -> dp.insertClient(x));
        List<Client> clients = dp.selectClients();
        assertFalse(clients.isEmpty());
    }

    @Test
    public void testInsertExercise() {
        exerciseList.forEach(x -> dp.insertExercise(x));
        List<Exercise> exercises = dp.selectExercises();
        assertFalse(exercises.isEmpty());
    }

    @Test
    public void testInsertWorkout() {
        workoutList.forEach(x -> dp.insertWorkout(x));
        List<Workout> workouts = dp.selectWorkouts();
        assertFalse(workouts.isEmpty());
    }

    @Test
    public void testInsertFeedback() {
        feedbackList.forEach(x -> dp.insertFeedback(x));
        List<Feedback> feedbacks = dp.selectFeedbacks();
        assertFalse(feedbacks.isEmpty());
    }

    @Test
    public void testGetConnection() throws SQLException {

        try(Connection connection = dp.getNewConnection()){
            assertTrue(connection.isValid(1));
            Assert.assertFalse(connection.isClosed());
        }
    }

    @Test
    public void testSelectRecords() {
        List<Trainer> list = dp.selectTrainers();
        logger.info(list);
        logger.info(list.get(0));
        list.forEach(System.out::println);
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    public void testSelectClients() {
        List<Client> list = dp.selectClients();
        logger.info(list);
        logger.info(list.get(0));
        list.forEach(System.out::println);
        Assert.assertFalse(list.isEmpty());
    }


    @Test
    public void testPositiveUpdateTrainer() {
        Trainer test = new Trainer("Dany", "Mclovin");
        assertTrue(dp.insertTrainer(test));
        test.setSurname("Cracker");
        testSelectRecords();
        assertTrue(dp.updateTrainer(test));
        testSelectRecords();
    }


    @Test
    public void testPositiveDeleteTrainerById() {
        Trainer test = new Trainer("Levi", "Ackerman");
        assertTrue(new DataProviderDB().insertTrainer(test));
        testSelectRecords();
        assertTrue(new DataProviderDB().deleteTrainerById(test.getId()));
        assertTrue(new DataProviderDB().getTrainerById(test.getId()).isEmpty());
        testSelectRecords();
    }

    @Test
    public void testNegativeDeleteTrainerById(){

    }

    @Test
    public void testClearTable() {
        assertTrue(new DataProviderDB().clearTable(Trainer.class));
        List<Trainer> list = new DataProviderDB().selectTrainers();
        assertTrue(list.isEmpty());
    }

    //-----------------Trainer role tests------------------

    //checkClient use case

    @Test
    public void testPositiveCheckClient() {
        List<Workout> workouts = dp.selectWorkouts();
        long workoutId = workouts.get(0).getClient();
        assertTrue(dp.checkClient(workoutId));
    }

    @Test
    public void testNegativeCheckClient() {
        List<Client> clients = dp.selectClients();
        assertFalse(dp.checkClient(clients.get(3).getId()));
    }

    @Test
    public void testPositiveGetClientById() {
        List<Client> clients = dp.selectClients();
        Client testClient = clients.get(2);
        assertEquals(testClient, dp.getClientById(testClient.getId()).orElseThrow());
    }

    @Test
    public void testNegativeGetClientById() {
        assertTrue(dp.getClientById(-1).isEmpty());
    }

    @Test
    public void testPositiveViewFeedback() {
        List<Feedback> feedbacks = dp.selectFeedbacks();
        assertTrue(dp.viewFeedback(feedbacks.get(0).getId()));
    }

    @Test
    public void testNegativeViewFeedback() {
        assertFalse(dp.viewFeedback(-1));
    }

    //createWorkout() use case
    @Test
    public void testPositiveCreateWorkout() {
        List<Client> clients = dp.selectClients();
        List<Trainer> trainers = dp.selectTrainers();
        assertTrue(dp.createWorkout(Workout.WorkoutType.AEROBIC, clients.get(0).getId(), trainers.get(0).getId()));
    }

    @Test
    public void testNegativeCreateWorkout() {
        List<Client> clients = dp.selectClients();
        List<Trainer> trainers = dp.selectTrainers();
        assertFalse(dp.createWorkout(Workout.WorkoutType.FLEXIBILITY, -1, trainers.get(0).getId()));
    }

    //createExercise() use case
    @Test
    public void testPositiveCreateExercise() {
        List<Workout> workout = dp.selectWorkouts();
        long workoutId = workout.get(1).getId();
        assertTrue(dp.createExercise("somename", 120, 12, 4, workoutId));
    }

    @Test
    public void testNegativeCreateExercise() {
        assertFalse(dp.createExercise("somename", 120,
                12, 4, -1));
    }

    //-----------------Client role tests--------------------

    @Test
    public void testPositiveExecuteWorkout() {
        assertTrue(dp.executeWorkout(2, ""));
        assertTrue(dp.executeWorkout(2, Feedback.Estimate.VERY_EASY.toString()));
    }

    @Test
    public void testNegativeExecuteWorkout() {
        assertFalse(dp.executeWorkout(-1, "something"));
    }

    @Test
    public void testPositiveViewWorkout() {
        assertTrue(dp.viewWorkout(1));
        assertFalse(dp.viewWorkout(0));
    }

    @Test
    public void testNegativeViewWorkout() {
        assertFalse(dp.viewWorkout(0));
    }

    @Test
    public void testPositiveComposeFeedback() {
        String test = Feedback.Estimate.HARD.toString();
        Feedback feedback = dp.getFeedbackById(dp.composeFeedback(test)).orElseThrow();
        assertEquals(feedback.getEstimate().toString(), test);
    }

    @Test
    public void testNegativeComposeFeedback() {
        String test = Constants.NOT_FOUND;
        assertEquals(dp.composeFeedback(test), -1);
    }

}