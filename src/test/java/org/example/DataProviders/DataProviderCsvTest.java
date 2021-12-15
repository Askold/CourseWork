package org.example.DataProviders;

import org.example.Models.Client;
import org.example.Models.Exercise;
import org.example.Models.Program;
import org.example.Models.Trainer;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DataProviderCsvTest {

    @Test
    public void testSaveRecords() {
        List<Trainer> test = List.of(
                new Trainer("name1", "surname1"),
                new Trainer("name2", "surname2"),
                new Trainer("name3", "surname3"),
                new Trainer("name4", "surname4")
        );
        List<Trainer> test1 = List.of(

        );
        new DataProviderCsv().saveRecords(test);
    }

    @Test
    public void testSelectRecords() {
        List<Client> test= new DataProviderCsv().selectRecords(Client.class);
        test.forEach(System.out::println);
    }

    @Test
    public void testCreateNewTrainer() {
        Trainer test = new Trainer("name", "surname");
        Assert.assertTrue(new DataProviderCsv().createNewTrainer(test));
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
        Assert.assertTrue(new DataProviderCsv().deleteTrainerById(testList.get(0).getId()));
        List<Trainer> test = new DataProviderCsv().selectRecords(Trainer.class);
        test.remove(0);
        Assert.assertEquals(test, test);
    }

    @Test
    public void testAddNewExercise() {
        Exercise test = new Exercise("exercise1", "some description", "some URL");
        new DataProviderCsv().createNewExercise(test);
    }

    @Test
    public void testComposeProgram() {
        Client client = new Client("Dave", "Mclovin", 12, 12, 12);
        Assert.assertTrue(new DataProviderCsv().createNewClient(client));
        Assert.assertTrue(new DataProviderCsv().composeProgram(3,3,3,
                "", Program.ProgramType.FLEXIBILITY, 1639138136681L));
    }

    @Test
    public void testCreateNewClient() {
        List<Program> longList = new DataProviderCsv().selectRecords(Program.class);
        List<Long> list = new ArrayList<>();
        for (Program i:longList){
            list.add(i.getId());
        }
        Client client = new Client();
        client.setPrograms(list);
        new DataProviderCsv().createNewClient(client);
        System.out.println(client);
    }

}