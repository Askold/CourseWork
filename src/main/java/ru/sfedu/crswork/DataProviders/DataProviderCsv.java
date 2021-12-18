package ru.sfedu.crswork.DataProviders;

import com.google.gson.Gson;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.crswork.Constants;
import ru.sfedu.crswork.Utils.ConfigurationUtil;
import ru.sfedu.crswork.Models.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataProviderCsv extends DataProvider {
    private static final Logger logger = LogManager.getLogger(DataProviderCsv.class);

    File initDataSource(Class<?> type) {
        String filePath;
        File file = null;
        try {
            filePath = ConfigurationUtil.getConfigurationEntry(Constants.DEFAULT_CSV_PATH)
                    +'/'+ type.getSimpleName() + Constants.CSV_EXTENSION;
            file = new File(filePath);
            if (!file.exists()){
                file.createNewFile();
                logger.info(Constants.CSV_CREATED + type.getSimpleName());
            }
            logger.info(Constants.CSV_PATH_IS + filePath);
        } catch (IOException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
        return file;
    }

    // -----------------general select/save methods-------------------------------

    public<T> boolean saveRecords(List<T> beans) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Writer writer;
        try {
            writer = new FileWriter(initDataSource(beans.get(0).getClass()), false);
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                    .build();
            beanToCsv.write(beans);
            writer.close();
            logger.info(Constants.RECORDS_ADDED);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error(e.getClass().getName() + e.getMessage());
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        addHistoryRecord(historyRecord);
        return true;
    }

    public<T> List<T> selectRecords(Class<?> type)  {
        List<T> beanToCsv = new ArrayList<>();
        try {
            FileReader reader = new FileReader(initDataSource(type));
            beanToCsv = new CsvToBeanBuilder(reader)
                    .withType(type)
                    .build().parse();
            logger.info(Constants.RECORDS_SELECTED);
        } catch (IOException e) {
            logger.error(e.getClass().getName() + e.getMessage());
        }
        return beanToCsv;
    }

    // -------------------------Trainer class CRUD--------------------------------------
    @Override
    boolean insertTrainer(Trainer trainer) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        historyRecord.setBean(new Gson().toJson(trainer));
        Writer wr;
        try {
            wr = new FileWriter(initDataSource(Trainer.class), true);
            CSVWriter writer = new CSVWriter(wr);
            StatefulBeanToCsv<Trainer> beanToCsv = new StatefulBeanToCsvBuilder<Trainer>(writer)
                    .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                    .build();
            beanToCsv.write(trainer);
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error(e.getClass().getName() + e.getMessage());
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        logger.info(trainer.getClass().getSimpleName() + Constants.ADDED);
        addHistoryRecord(historyRecord);
        return true;
    }

    @Override
    Optional<Trainer> getTrainerById(long id) {
        List<Trainer> listOfBeans = selectRecords(Trainer.class);
        Stream<Trainer> streamFromList = listOfBeans.stream();
        Trainer result = new Trainer();
        try{
            result = streamFromList.filter((bean -> bean.getId() == id)).findFirst().orElseThrow();
        }
        catch (NoSuchElementException e){
            logger.error(e.getClass().getName() + e.getMessage());
            logger.error(result.getClass().getSimpleName() + Constants.NOT_FOUND);
            return Optional.empty();
        }
        return Optional.of(result);
    }

    @Override
    boolean updateTrainer(Trainer bean) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Trainer beanToUpdate = new Trainer();
        try{
            beanToUpdate = getTrainerById(bean.getId()).orElseThrow();
        } catch (NoSuchElementException e){
            logger.error(e.getClass().getName() + e.getMessage());
            logger.error(bean.getClass().getSimpleName() + Constants.NOT_UPDATED);
            return false;
        }
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
        try{
            beanToRemove = getTrainerById(id).orElseThrow();
        }catch (NoSuchElementException e){
            logger.error(e.getClass().getName() + e.getMessage());
            logger.error(beanToRemove.getClass().getSimpleName() + Constants.NOT_DELETED);
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            return false;
        }
        List<Trainer> listOfBeans = selectRecords(Trainer.class);
        Trainer finalBeanToRemove = beanToRemove;
        listOfBeans.removeIf(bean -> bean.equals(finalBeanToRemove));
        addHistoryRecord(historyRecord);
        logger.debug(beanToRemove.getClass().getSimpleName() + Constants.DELETED);
        return saveRecords(listOfBeans);
    }

    // -------------------------Client class CRUD--------------------------------------
    @Override
    boolean insertClient(Client client) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        historyRecord.setBean(new Gson().toJson(client));
        Writer wr;
        try {
            wr = new FileWriter(initDataSource(Client.class), true);
            CSVWriter writer = new CSVWriter(wr);
            StatefulBeanToCsv<Client> beanToCsv = new StatefulBeanToCsvBuilder<Client>(writer)
                    .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                    .build();
            beanToCsv.write(client);
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error(e.getClass().getName() + e.getMessage());
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        logger.info(client.getClass().getSimpleName() + Constants.ADDED);
        addHistoryRecord(historyRecord);
        return true;
    }

    @Override
    Optional<Client> getClientById(long id) {
        List<Client> listOfBeans = selectRecords(Client.class);
        Stream<Client> streamFromList = listOfBeans.stream();
        Client result = new Client();
        try{
            result = streamFromList.filter((bean -> bean.getId() == id)).findFirst().orElseThrow();
        }
        catch (NoSuchElementException e){
            logger.error(e.getClass().getName() + e.getMessage());
            logger.error(result.getClass().getSimpleName() + Constants.NOT_FOUND);
            return Optional.empty();
        }
        return Optional.of(result);
    }

    @Override
    boolean updateClient(Client bean) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Client beanToUpdate = new Client();
        if (getClientById(bean.getId()).isPresent()) beanToUpdate = getClientById(bean.getId()).get();
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
        try{
            beanToRemove = getClientById(id).orElseThrow();
        }catch (NoSuchElementException e){
            logger.error(e.getClass().getName() + e.getMessage());
            logger.error(beanToRemove.getClass().getSimpleName() + Constants.NOT_DELETED);
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            return false;
        }
        List<Client> listOfBeans = selectRecords(Client.class);
        Client finalBeanToRemove = beanToRemove;
        listOfBeans.removeIf(bean -> bean.equals(finalBeanToRemove));
        addHistoryRecord(historyRecord);
        logger.debug(beanToRemove.getClass().getSimpleName() + Constants.DELETED);
        return saveRecords(listOfBeans);
    }

    // -------------------------Exercises class CRUD--------------------------------------
    @Override
    boolean insertExercise(Exercise exercise) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        historyRecord.setBean(new Gson().toJson(exercise));
        Writer wr;
        try {
            wr = new FileWriter(initDataSource(Exercise.class), true);
            CSVWriter writer = new CSVWriter(wr);
            StatefulBeanToCsv<Exercise> beanToCsv = new StatefulBeanToCsvBuilder<Exercise>(writer)
                    .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                    .build();
            beanToCsv.write(exercise);
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error(e.getClass().getName() + e.getMessage());
            logger.error(Exercise.class.getSimpleName()+Constants.NOT_ADDED);
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        logger.info(exercise.getClass().getSimpleName() + Constants.ADDED);
        addHistoryRecord(historyRecord);
        return true;
    }

    @Override
    Optional<Exercise> getExerciseById(long id) {
        List<Exercise> listOfBeans = selectRecords(Exercise.class);
        Stream<Exercise> streamFromList = listOfBeans.stream();
        Exercise result = new Exercise();
        try{
            result = streamFromList.filter((bean -> bean.getId() == id)).findFirst().orElseThrow();
        }
        catch (NoSuchElementException e){
            logger.error(e.getClass().getName() + e.getMessage());
            logger.error(result.getClass().getSimpleName() + Constants.NOT_FOUND);
            return Optional.empty();
        }
        return Optional.of(result);
    }


    // -------------------------Workout class CRUD--------------------------------------
    @Override
    boolean insertWorkout(Workout workout) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        historyRecord.setBean(new Gson().toJson(workout));
        Writer wr;
        try {
            wr = new FileWriter(initDataSource(Workout.class), true);
            CSVWriter writer = new CSVWriter(wr);
            StatefulBeanToCsv<Workout> beanToCsv = new StatefulBeanToCsvBuilder<Workout>(writer)
                    .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                    .build();
            beanToCsv.write(workout);
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error(e.getClass().getName() + e.getMessage());
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        logger.info(workout.getClass().getSimpleName() + Constants.ADDED);
        addHistoryRecord(historyRecord);
        return true;
    }

    @Override
    Optional<Workout> getWorkoutById(long id) {
        List<Workout> listOfBeans = selectRecords(Workout.class);
        Stream<Workout> streamFromList = listOfBeans.stream();
        Workout result = new Workout();
        try{
            result = streamFromList.filter((bean -> bean.getId() == id)).findFirst().orElseThrow();
        }
        catch (NoSuchElementException e){
            logger.error(e.getClass().getName() + e.getMessage());
            logger.error(result.getClass().getSimpleName() + Constants.NOT_FOUND);
            return Optional.empty();
        }
        return Optional.of(result);
    }

    @Override
    boolean updateWorkout(Workout bean) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Workout beanToUpdate = new Workout();
        if (getTrainerById(bean.getId()).isPresent()) beanToUpdate = getWorkoutById(bean.getId()).get();
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
        try{
            beanToRemove = getWorkoutById(id).orElseThrow();
        }catch (NoSuchElementException e){
            logger.error(e.getClass().getName() + e.getMessage());
            logger.error(beanToRemove.getClass().getSimpleName() + Constants.NOT_DELETED);
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            return false;
        }
        List<Workout> listOfBeans = selectRecords(Workout.class);
        Workout finalBeanToRemove = beanToRemove;
        listOfBeans.removeIf(bean -> bean.equals(finalBeanToRemove));
        addHistoryRecord(historyRecord);
        logger.debug(beanToRemove.getClass().getSimpleName() + Constants.DELETED);
        return saveRecords(listOfBeans);
    }

    // -------------------------Feedback class CRUD--------------------------------------
    @Override
    boolean insertFeedback(Feedback feedback) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        historyRecord.setBean(new Gson().toJson(feedback));
        Writer wr;
        try {
            wr = new FileWriter(initDataSource(Feedback.class), true);
            CSVWriter writer = new CSVWriter(wr);
            StatefulBeanToCsv<Feedback> beanToCsv = new StatefulBeanToCsvBuilder<Feedback>(writer)
                    .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                    .build();
            beanToCsv.write(feedback);
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error(e.getClass().getName() + e.getMessage());
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        logger.info(feedback.getClass().getSimpleName() + Constants.ADDED);
        addHistoryRecord(historyRecord);
        return true;
    }

    @Override
    Optional<Feedback> getFeedbackById(long id) {
        List<Feedback> listOfBeans = selectRecords(Feedback.class);
        Stream<Feedback> streamFromList = listOfBeans.stream();
        Feedback result = new Feedback();
        try{
            result = streamFromList.filter((bean -> bean.getId() == id)).findFirst().orElseThrow();
        }
        catch (NoSuchElementException e){
            logger.error(e.getClass().getName() + e.getMessage());
            logger.error(result.getClass().getSimpleName() + Constants.NOT_FOUND);
            return Optional.empty();
        }
        return Optional.of(result);
    }


    // ------------------------- Use-case implementation--------------------------------------
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
    boolean viewFeedback(long id) {
        if (getFeedbackById(id).isEmpty()){
            return false;
        }
        Feedback feedback = getFeedbackById(id).get();
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
    boolean executeWorkout(long workoutID, String isCompleted){
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
    boolean viewWorkout(long workoutID){
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
