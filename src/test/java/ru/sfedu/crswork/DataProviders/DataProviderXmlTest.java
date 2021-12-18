package ru.sfedu.crswork.DataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.crswork.Models.Trainer;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DataProviderXmlTest {
    private static final Logger logger = LogManager.getLogger(DataProviderCsv.class);
    DataProviderXml dp = new DataProviderXml();

    @Test
    public void testSaveRecords() {
        List<Long> list = List.of(1L, 2L ,3L, 4L);
        List<Trainer> test = List.of(
                new Trainer("name1", "surname1"),
                new Trainer("name2", "surname2"),
                new Trainer("name3", "surname3"),
                new Trainer("name4", "surname4")
        );
        dp.saveRecords(test);
    }

    @Test
    public void testSelectRecords() {

        List<Trainer> test= new DataProviderXml().selectRecords(Trainer.class);
        test.forEach(logger::info);
        Assert.assertNotNull(test);
    }
}