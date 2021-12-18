package ru.sfedu.crswork.DataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.crswork.Constants;
import ru.sfedu.crswork.Utils.ConfigurationUtil;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import ru.sfedu.crswork.Models.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataProviderXml extends DataProvider{
    private static final Logger logger = LogManager.getLogger(DataProviderXml.class);

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

    // -----------------general select/save records-------------------------------
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

    // -------------------------Trainer class CRUD--------------------------------------

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
    Optional<Trainer> getTrainerById(long id) {
        List<Trainer> listOfBeans = selectRecords(Trainer.class);
        Stream<Trainer> streamFromList = listOfBeans.stream();
        Optional<Trainer> result;
        result = streamFromList.filter((bean -> bean.getId() == id)).findFirst();
        if(result.isEmpty()){
            logger.error(listOfBeans.get(0).getClass().getSimpleName() + Constants.NOT_FOUND);
        }
        return result;
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

    // -------------------------Client class CRUD--------------------------------------

    @Override
    boolean insertClient(Client bean) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        List<Client> data = selectRecords(Client.class);
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
    Optional<Client> getClientById(long id) {
        List<Client> listOfBeans = selectRecords(Client.class);
        Stream<Client> streamFromList = listOfBeans.stream();
        Optional<Client> result;
        result = streamFromList.filter((bean -> bean.getId() == id)).findFirst();
        if(result.isEmpty()){
            logger.error(listOfBeans.get(0).getClass().getSimpleName() + Constants.NOT_FOUND);
        }
        return result;
    }

    @Override
    boolean updateClient(Client bean) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Client beanToUpdate;
        if(getClientById(bean.getId()).isEmpty()){
            logger.debug(bean.getClass().getSimpleName() + Constants.NOT_UPDATED);
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        beanToUpdate = getClientById(bean.getId()).orElseThrow();

        if (!beanToUpdate.equals(bean)) {
            if (!deleteClientById(bean.getId())){
                historyRecord.setStatus(HistoryContent.Status.FAULT);
                addHistoryRecord(historyRecord);
                logger.error(bean.getClass().getSimpleName() + Constants.NOT_UPDATED);
                return false;
            }
            insertClient(bean);
        }
        addHistoryRecord(historyRecord);
        logger.debug(bean.getClass().getSimpleName() + Constants.UPDATED);
        return true;
    }

    @Override
    boolean deleteClientById(long id) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Client beanToRemove = new Client();
        if(getClientById(id).isEmpty()){
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            logger.debug(beanToRemove.getClass().getSimpleName() + Constants.NOT_FOUND);
            return false;
        }
        beanToRemove = getClientById(id).get();
        List<Client> listOfCars = selectRecords(Client.class);
        Client finalBeanToRemove = beanToRemove;
        listOfCars.removeIf(bean -> bean.equals(finalBeanToRemove));
        return saveRecords(listOfCars);
    }

    // -------------------------Exercise class CRUD--------------------------------------

    @Override
    boolean insertExercise(Exercise bean) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        List<Exercise> data = selectRecords(Exercise.class);
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
    Optional<Exercise> getExerciseById(long id) {
        List<Exercise> listOfBeans = selectRecords(Exercise.class);
        Stream<Exercise> streamFromList = listOfBeans.stream();
        Optional<Exercise> result;
        result = streamFromList.filter((bean -> bean.getId() == id)).findFirst();
        if(result.isEmpty()){
            logger.error(listOfBeans.get(0).getClass().getSimpleName() + Constants.NOT_FOUND);
        }
        return result;
    }

    // -------------------------Workout class CRUD--------------------------------------

    @Override
    boolean insertWorkout(Workout bean) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        List<Workout> data = selectRecords(Workout.class);
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
    Optional<Workout> getWorkoutById(long id) {
        List<Workout> listOfBeans = selectRecords(Workout.class);
        Stream<Workout> streamFromList = listOfBeans.stream();
        Optional<Workout> result;
        result = streamFromList.filter((bean -> bean.getId() == id)).findFirst();
        if(result.isEmpty()){
            logger.error(listOfBeans.get(0).getClass().getSimpleName() + Constants.NOT_FOUND);
        }
        return result;
    }

    @Override
    boolean updateWorkout(Workout bean) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Workout beanToUpdate;
        if(getWorkoutById(bean.getId()).isEmpty()){
            logger.debug(bean.getClass().getSimpleName() + Constants.NOT_UPDATED);
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        beanToUpdate = getWorkoutById(bean.getId()).orElseThrow();

        if (!beanToUpdate.equals(bean)) {
            if (!deleteWorkoutById(bean.getId())){
                historyRecord.setStatus(HistoryContent.Status.FAULT);
                addHistoryRecord(historyRecord);
                logger.error(bean.getClass().getSimpleName() + Constants.NOT_UPDATED);
                return false;
            }
            insertWorkout(bean);
        }
        addHistoryRecord(historyRecord);
        logger.debug(bean.getClass().getSimpleName() + Constants.UPDATED);
        return true;
    }

    @Override
    boolean deleteWorkoutById(long id) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Workout beanToRemove = new Workout();
        if(getWorkoutById(id).isEmpty()){
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            logger.debug(beanToRemove.getClass().getSimpleName() + Constants.NOT_FOUND);
            return false;
        }
        beanToRemove = getWorkoutById(id).get();
        List<Workout> listOfCars = selectRecords(Workout.class);
        Workout finalBeanToRemove = beanToRemove;
        listOfCars.removeIf(bean -> bean.equals(finalBeanToRemove));
        return saveRecords(listOfCars);
    }

    // -------------------------Feedback class CRUD--------------------------------------

    @Override
    boolean insertFeedback(Feedback bean) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        List<Feedback> data = selectRecords(Feedback.class);
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
    Optional<Feedback> getFeedbackById(long id) {
        List<Feedback> listOfBeans = selectRecords(Feedback.class);
        Stream<Feedback> streamFromList = listOfBeans.stream();
        Optional<Feedback> result;
        result = streamFromList.filter((bean -> bean.getId() == id)).findFirst();
        if(result.isEmpty()){
            logger.error(listOfBeans.get(0).getClass().getSimpleName() + Constants.NOT_FOUND);
        }
        return result;
    }

    // ------------------------- Use case implementation--------------------------------------
    // Trainer role

    @Override
    boolean checkClient(long id) {
        Client client = getClientById(id).orElseThrow();
        if (client.isAwaiting()){
            List<Workout> workouts = selectRecords(Workout.class);
            Optional<Workout> result = workouts.stream().filter(bean -> bean.getClient() == id).findFirst();
            if(result.isEmpty()){
                logger.error(Constants.CLIENT_WORKOUT);
                return false;
            }
            viewFeedback(result.get().getFeedback());
        }
        return true;
    }

    @Override
    boolean viewFeedback(long clientId) {
        if (getFeedbackById(clientId).isEmpty()){
            return false;
        }
        Feedback feedback = getFeedbackById(clientId).get();
        logger.info(Constants.WORKOUT_ESTIMATE + feedback.getEstimate().toString());
        logger.info(Constants.COMMENTS + feedback.getComment());
        return true;
    }

    @Override
    boolean createWorkout(String typeWorkout, long client, long trainer) {
        Workout.WorkoutType type;
        try {
            type = Workout.WorkoutType.valueOf(typeWorkout);
        }
        catch (IllegalArgumentException e){
            logger.error(e.getClass().getName() + e.getMessage());
            return false;
        }
        Workout workout = new Workout(type, client, trainer);
        if (!insertWorkout(workout)){
            logger.error(workout.getClass().getSimpleName() + Constants.NOT_ADDED);
            return false;
        }
        if(!changeClientStatus(client)){
            return false;
        }
        logger.info(workout.getClass().getSimpleName()+ Constants.ADDED);
        logger.info(workout.getClass().getSimpleName() + Constants.ID_IS+ workout.getId());
        return true;
    }

    boolean changeClientStatus(long id){
        Client client = getClientById(id).orElseThrow();
        client.setAwaiting(false);
        return updateClient(client);
    }

    @Override
    boolean createExercise(String name, int weight, int repetitions, int rounds, long workout) {
        if(getWorkoutById(workout).isEmpty()){
            return false;
        }
        Exercise exercise = new Exercise(name, weight, repetitions, rounds, workout);
        return insertExercise(exercise);
    }

    // Client role
    @Override
    boolean executeWorkout(long workoutID, String isCompleted) {
        if (getWorkoutById(workoutID).isEmpty()){
            logger.error(Workout.class.getSimpleName()+Constants.NOT_FOUND);
            return false;
        }
        if(!viewWorkout(workoutID)){
            logger.error("Impossible to view workout");
            return false;
        }
        if(!isCompleted.isEmpty()){
            Workout workout = getWorkoutById(workoutID).get();
            workout.setFeedback(composeFeedback(isCompleted));
            updateWorkout(workout);
            Client client = getClientById(workout.getClient()).orElseThrow();
            client.setAwaiting(true);
            updateClient(client);
        }
        return true;
    }

    @Override
    boolean viewWorkout(long workoutID) {
        List<Exercise> exercises = selectRecords(Exercise.class);
        List<Exercise> result = exercises.stream().filter(bean -> bean.getWorkout() == workoutID).collect(Collectors.toList());
        if(result.isEmpty()) {
            logger.error("Exercises for this workout wasn't found");
            return false;
        }
        result.forEach(logger::info);
        return true;
    }

    @Override
    long composeFeedback(String isCompleted) {
        Feedback.Estimate estimate;
        try {
            estimate = Feedback.Estimate.valueOf(isCompleted);
        }
        catch (IllegalArgumentException e){
            logger.error(e.getClass().getName() + e.getMessage());
            return -1;
        }
        Feedback feedback = new Feedback(estimate);
        if (!insertFeedback(feedback)){
            logger.error(feedback.getClass().getSimpleName()+Constants.NOT_ADDED);
        }
        return feedback.getId();
    }
}
