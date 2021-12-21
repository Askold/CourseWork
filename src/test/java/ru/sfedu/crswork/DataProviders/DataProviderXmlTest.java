package ru.sfedu.crswork.DataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import ru.sfedu.crswork.Constants;
import ru.sfedu.crswork.Models.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class DataProviderXmlTest {
    private static final Logger logger = LogManager.getLogger(DataProviderCsv.class);
    DataProviderXml dp = new DataProviderXml();
    List<Trainer> testTrainer = List.of(
            new Trainer("Alex", "Powerlifter", 5, 10),
            new Trainer("Drake", "Natan", 3, 6),
            new Trainer("William", "Defoe", 1, 9),
            new Trainer("Mathew", "Mcconaughey", 8, 10)
    );
    List<Client> clientList = List.of(
            new Client("Hairy", "Schlong", 24, 78, 182),
            new Client("Davy", "Farter", 20, 80, 190),
            new Client("Macky", "Mclovin", 12, 60, 160),
            new Client("Wendy", "Tour", 40, 80, 185)
    );
    List<Exercise> exercises = List.of(
            new Exercise("Exercise1", 15, 15, 3, 1),
            new Exercise("Exercise2", 20, 10, 4, 1),
            new Exercise("Exercise3", 100, 12, 5, 2),
            new Exercise("Exercise4", 58, 10, 4, 2)
    );

    List<Feedback> feedbacks = List.of(
            new Feedback(Feedback.Estimate.COMMON)
    );

    List<Workout> workouts = List.of(
            new Workout(1, Workout.WorkoutType.AEROBIC, clientList.get(0).getId(), testTrainer.get(0).getId(),
                    feedbacks.get(0).getId()),
            new Workout(2, Workout.WorkoutType.STRENGTH, clientList.get(1).getId(), testTrainer.get(1).getId())
    );

    @Before
    public void initiateRecords() {

        dp.saveRecords(testTrainer);
        dp.saveRecords(clientList);
        dp.saveRecords(exercises);
        dp.saveRecords(workouts);
        dp.saveRecords(feedbacks);
    }

    @Test
    public void testSelectRecords() {
        List<Client> test = new DataProviderCsv().selectRecords(Client.class);
        test.forEach(System.out::println);
    }

    //----------CRUD tests (bases on entity Trainer)-------

    @Test
    public void testInsertNewTrainer() {
        Trainer test = new Trainer("name", "surname");
        assertTrue(new DataProviderCsv().insertTrainer(test));
    }

    @Test
    public void testGetTrainerById() {

    }

    @Test
    public void testUpdateTrainerById() {
        List<Trainer> testList = new DataProviderCsv().selectRecords(Trainer.class);
        Trainer test = testList.get(0);
        test.setName("Ahalab");
        new DataProviderCsv().updateTrainer(test);
        Assert.assertEquals(test, new DataProviderCsv().getTrainerById(test.getId()).orElseThrow());
        Trainer test1 = new Trainer();
        new DataProviderCsv().updateTrainer(test1);
    }

    @Test
    public void testDeleteTrainerById() {
        List<Trainer> testList = new DataProviderCsv().selectRecords(Trainer.class);
        assertTrue(new DataProviderCsv().deleteTrainerById(testList.get(0).getId()));
        List<Trainer> test = new DataProviderCsv().selectRecords(Trainer.class);
        test.remove(0);
        Assert.assertEquals(test, test);
    }

    //-----------------Trainer role tests------------------

    //checkClient use case

    @Test
    public void testPositiveCheckClient() {
        List<Workout> workouts = dp.selectRecords(Workout.class);
        long clientId = workouts.get(0).getClient();
        assertTrue(dp.checkClient(clientId));
    }

    @Test
    public void testNegativeCheckClient() {
        List<Client> clients = dp.selectRecords(Client.class);
        assertFalse(dp.checkClient(clients.get(3).getId()));
    }

    @Test
    public void testPositiveGetClientById() {
        List<Client> clients = dp.selectRecords(Client.class);
        Client testClient = clients.get(2);
        assertEquals(testClient, dp.getClientById(testClient.getId()).orElseThrow());
    }

    @Test
    public void testNegativeGetClientById() {
        assertTrue(dp.getClientById(-1).isEmpty());
    }

    @Test
    public void testPositiveViewFeedback() {
        List<Feedback> feedbacks = dp.selectRecords(Feedback.class);
        assertTrue(dp.viewFeedback(feedbacks.get(0).getId()));
    }

    @Test
    public void testNegativeViewFeedback() {
        assertFalse(dp.viewFeedback(-1));
    }

    //createWorkout() use case
    @Test
    public void testPositiveCreateWorkout() {
        List<Client> clients = dp.selectRecords(Client.class);
        List<Trainer> trainers = dp.selectRecords(Trainer.class);
        assertTrue(dp.createWorkout(Workout.WorkoutType.AEROBIC, clients.get(0).getId(), trainers.get(0).getId()));
    }

    @Test
    public void testNegativeCreateWorkout() {
        List<Client> clients = dp.selectRecords(Client.class);
        List<Trainer> trainers = dp.selectRecords(Trainer.class);
        assertFalse(dp.createWorkout(Workout.WorkoutType.FLEXIBILITY, -1, trainers.get(0).getId()));
    }

    //createExercise() use case
    @Test
    public void testPositiveCreateExercise() {
        List<Workout> workout = dp.selectRecords(Workout.class);
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
        assertFalse(dp.executeWorkout(-2, "something"));
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