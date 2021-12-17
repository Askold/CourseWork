package org.example.DataProviders;

import com.sun.source.tree.AssertTree;
import org.example.Constants;
import org.example.Models.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DataProviderCsvTest {
    DataProviderCsv dp = new DataProviderCsv();
    @Test
    public void testSaveRecords() {
        List<Trainer> testTrainer = List.of(
                new Trainer("Alex", "Powerlifter", 5, 10),
                new Trainer("Drake", "Natan", 3,6),
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
                new Exercise("Exercise1", "Some description", 15, 15, 3, 1),
                new Exercise("Exercise2", "Some description", 20, 10, 4,1),
                new Exercise("Exercise3", "Some description", 100, 12, 5, 2),
                new Exercise("Exercise4", "Some description", 58, 10, 4, 2)
        );
        List<Workout> workouts = List.of(new Workout(1), new Workout(2)
        );
        List<Program> programs = List.of(new Program(12, "Some description", Program.ProgramType.AEROBIC));

        dp.saveRecords(testTrainer);
        dp.saveRecords(clientList);
        dp.saveRecords(exercises);
        dp.saveRecords(workouts);
        dp.saveRecords(programs);
    }

    @Test
    public void testSelectRecords() {
        List<Client> test= new DataProviderCsv().selectRecords(Client.class);
        test.forEach(System.out::println);
    }

    @Test
    public void testCreateNewTrainer() {
        Trainer test = new Trainer("name", "surname");
        assertTrue(new DataProviderCsv().insertTrainer(test));
    }

    @Test
    public void testGetTrainerById() {

    }

    @Test
    public void testUpdateTrainerById() {
        List<Trainer> testList= new DataProviderCsv().selectRecords(Trainer.class);
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


    @Test
    public void testCreateNewClient() {
        List<Program> longList = new DataProviderCsv().selectRecords(Program.class);
        List<Long> list = new ArrayList<>();
        for (Program i:longList){
            list.add(i.getId());
        }
        Client client = new Client();
        new DataProviderCsv().insertClient(client);
        System.out.println(client);
    }


    @Test
    public void testExecuteWorkout() {
        assertTrue(dp.executeWorkout(2, ""));
        assertTrue(dp.executeWorkout(2, Feedback.Estimate.VERY_EASY.toString()));
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