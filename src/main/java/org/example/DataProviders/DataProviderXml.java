package org.example.DataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Constants;
import org.example.Models.*;
import org.example.Utils.ConfigurationUtil;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DataProviderXml extends DataProvider{
    private static final Logger logger = LogManager.getLogger(DataProviderXml.class);

    @Override
    File initDataSource(Class<?> type) {
        String filePath;
        File file = null;
        try {
            filePath = ConfigurationUtil.getConfigurationEntry(Constants.DEFAULT_XML_PATH)
                    +'/'+ type.getSimpleName() + Constants.XML_EXTENSION;
            file = new File(filePath);
            if (!file.exists()){
                file.createNewFile();
                logger.info(Constants.XML_CREATED + type.getSimpleName());
            }
            logger.info(Constants.XML_PATH_IS + filePath);
        } catch (IOException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
        return file;
    }

    @Override
    <T> boolean saveRecords(List<T> beans) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        try {
            Serializer serializer = new Persister();
            File file = initDataSource(beans.get(0).getClass());
            Writer writer = new FileWriter(file);
            EntityWrapper<T> beansXml = new EntityWrapper<>(beans);
            serializer.write(beansXml, writer);
        } catch (Exception e) {
            logger.error(e.getClass().getName() + e.getMessage());
            e.printStackTrace();
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        addHistoryRecord(historyRecord);
        return true;
    }

    @Override
    <T> List<T> selectRecords(Class<?> type) {
        Serializer serializer = new Persister();
        File file;
        EntityWrapper beansList = null;
        try {
            file = initDataSource(type);
            beansList = serializer.read(EntityWrapper.class, file);
        } catch (Exception e) {
            logger.debug(e.getClass().getName() + e.getMessage());
        }
        return beansList.getData();
    }

    @Override
    boolean insertTrainer(Trainer bean) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        List<Trainer> data = selectRecords(Trainer.class);
        data.add(bean);
        if(!saveRecords(data)){
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            logger.debug(bean.getClass().getSimpleName()+Constants.NOT_ADDED);
            return false;
        }
        addHistoryRecord(historyRecord);
        return true;
    }

    @Override
    boolean deleteTrainerById(long id) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Trainer beanToRemove = new Trainer();
        if(getTrainerById(id).isEmpty()){
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            logger.debug(beanToRemove.getClass().getSimpleName() + Constants.NOT_FOUND);
            return false;
        }
        beanToRemove = getTrainerById(id).get();
        List<Trainer> listOfCars = selectRecords(Trainer.class);
        Trainer finalBeanToRemove = beanToRemove;
        listOfCars.removeIf(bean -> bean.equals(finalBeanToRemove));
        return saveRecords(listOfCars);
    }

    @Override
    Optional<Trainer> getTrainerById(long id) {
        List<Trainer> listOfBeans = selectRecords(Trainer.class);
        Stream<Trainer> streamFromList = listOfBeans.stream();
        Trainer result = new Trainer();
        result = streamFromList.filter((bean -> bean.getId() == id)).findFirst().orElseThrow();
        return Optional.of(result);
    }

    @Override
    boolean updateTrainer(Trainer bean) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Trainer beanToUpdate = new Trainer();
        if(getTrainerById(bean.getId()).isEmpty()){
            logger.debug(bean.getClass().getSimpleName() + Constants.NOT_UPDATED);
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        beanToUpdate = getTrainerById(bean.getId()).orElseThrow();

        if (!beanToUpdate.equals(bean)) {
            if (!deleteTrainerById(bean.getId())){
                historyRecord.setStatus(HistoryContent.Status.FAULT);
                addHistoryRecord(historyRecord);
                logger.error(bean.getClass().getSimpleName() + Constants.NOT_UPDATED);
                return false;
            }
            insertTrainer(bean);
        }
        addHistoryRecord(historyRecord);
        logger.debug(bean.getClass().getSimpleName() + Constants.UPDATED);
        return true;
    }

    @Override
    boolean insertClient(Client client) {
        return false;
    }

    @Override
    Optional<Client> getClientById(long id) {
        return Optional.empty();
    }

    @Override
    boolean updateClient(Client client) {
        return false;
    }

    @Override
    boolean insertExercise(Exercise exercise) {
        return false;
    }

    @Override
    Optional<Exercise> getExerciseById(long id) {
        return Optional.empty();
    }

    @Override
    boolean insertWorkout(Workout workout) {
        return false;
    }

    @Override
    Optional<Workout> getWorkoutById(long id) {
        return Optional.empty();
    }

    @Override
    boolean updateWorkout(Workout workout) {
        return false;
    }

    @Override
    boolean insertFeedback(Feedback feedback) {
        return false;
    }

    @Override
    Optional<Feedback> getFeedbackById(long id) {
        return Optional.empty();
    }

    @Override
    boolean createNewProgram(Program program) {
        return false;
    }

    @Override
    Optional<Program> getProgramById(long id) {
        return Optional.empty();
    }



    @Override
    Optional<Exercise> addNewExercise(long id) {
        return Optional.empty();
    }

    @Override
    Optional<Feedback> viewFeedback(Client client) {
        return Optional.empty();
    }


    @Override
    long composeFeedback(String isCompleted) {
        return 0;
    }
}
