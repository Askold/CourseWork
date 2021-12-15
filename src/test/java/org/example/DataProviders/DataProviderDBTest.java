package org.example.DataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.App;
import org.example.Models.Trainer;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class DataProviderDBTest {
    private static final Logger logger = LogManager.getLogger(App.class);
    DataProviderDB dp = new DataProviderDB();

    @Test
    public void testGetConnection() throws SQLException {
        try(Connection connection = dp.getNewConnection()){
            assertTrue(connection.isValid(1));
            Assert.assertFalse(connection.isClosed());
        }
    }

    @Test
    public void testInsertTrainer() {
        Trainer test = new Trainer("Hairy", "Schlong");
        logger.info(test.toString());
        assertTrue(dp.insertTrainer(test));
        List<Trainer> list = dp.selectTrainers();
        assertFalse(list.isEmpty());
        testSelectRecords();
    }

    @Test
    public void testSelectRecords() {
        List<Trainer> list = dp.selectTrainers();
        list.forEach(System.out::println);
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    public void testPositiveGetTrainerByID() {
        Trainer test = new Trainer("Andrew", "Lipstick");
        assertTrue(dp.insertTrainer(test));
        Trainer test2 = dp.getTrainerByID(test.getId()).orElseThrow();
        assertEquals(test2, test);
        testSelectRecords();
    }

    @Test
    public void testNegativeGetTrainerByID(){
        Trainer test = new Trainer("Evil Andrew", "Lipstick");
        assertTrue(dp.insertTrainer(test));
        Trainer test2 = dp.getTrainerByID(test.getId()).orElseThrow();
        test.setId(0);
        assertNotEquals(test2, test);
        testSelectRecords();
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
    public void testNegativeUpdateTrainer(){
        Trainer test = new Trainer("Evil Dany", "Mclovin");
        assertTrue(dp.updateTrainer(test));
        assertNotEquals(test, dp.getTrainerByID(test.getId()).orElseThrow());
        test.setSurname("Cracker");
    }

    @Test
    public void testPositiveDeleteTrainerById() {
        Trainer test = new Trainer("Levi", "Ackerman");
        assertTrue(new DataProviderDB().insertTrainer(test));
        testSelectRecords();
        assertTrue(new DataProviderDB().deleteTrainerById(test.getId()));
        assertFalse(new DataProviderDB().getTrainerByID(test.getId()).isEmpty());
        testSelectRecords();
    }

    @Test
    public void testNegativeDeleteTrainerById(){

    }

    @Test
    public void testClearTable() {
        assertTrue(new DataProviderDB().clearTable());
        List<Trainer> list = new DataProviderDB().selectTrainers();
        assertTrue(list.isEmpty());
    }
}