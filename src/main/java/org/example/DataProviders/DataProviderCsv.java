package org.example.DataProviders;

import com.google.gson.Gson;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Constants;
import org.example.Models.*;
import org.example.Utils.ConfigurationUtil;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataProviderCsv extends DataProvider {
    private static final Logger logger = LogManager.getLogger(DataProviderCsv.class);

    @Override
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

    // -----------------general select/save records-------------------------------
    @Override
    public <T> boolean saveRecords(List<T> beans) {
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

    @Override
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
            if (!deleteTrainerById(bean.getId())){
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
            if (!deleteTrainerById(bean.getId())){
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
        }
        return Optional.of(result);
    }

    // -------------------------Program class CRUD--------------------------------------
    @Override
    boolean createNewProgram(Program program) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        historyRecord.setBean(new Gson().toJson(program));
        Writer wr;
        try {
            wr = new FileWriter(initDataSource(Program.class), true);
            CSVWriter writer = new CSVWriter(wr);
            StatefulBeanToCsv<Program> beanToCsv = new StatefulBeanToCsvBuilder<Program>(writer)
                    .withLineEnd(CSVWriter.DEFAULT_LINE_END)
                    .build();
            beanToCsv.write(program);
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            logger.error(e.getClass().getName() + e.getMessage());
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        logger.info(program.getClass().getSimpleName() + Constants.ADDED);
        addHistoryRecord(historyRecord);
        return true;
    }

    @Override
    Optional<Program> getProgramById(long id) {
        List<Program> listOfBeans = selectRecords(Program.class);
        Stream<Program> streamFromList = listOfBeans.stream();
        Program result = new Program();
        try{
            result = streamFromList.filter((bean -> bean.getId() == id)).findFirst().orElseThrow();
        }
        catch (NoSuchElementException e){
            logger.error(e.getClass().getName() + e.getMessage());
            logger.error(result.getClass().getSimpleName() + Constants.NOT_FOUND);
        }
        return Optional.of(result);
    }

    // ------------------------- Use-case implementation--------------------------------------
    //Trainer role


    @Override
    Optional<Exercise> addNewExercise(long id){
        Exercise exercise = new Exercise();
        insertExercise(exercise);
        return Optional.of(exercise);
    }

    boolean addExercises(List<Long> ids) {
        try {
            ids.forEach(i -> getExerciseById(i).orElse(addNewExercise(i).orElseThrow()));
        }catch (NoSuchElementException e){
            logger.error(e.getClass().getName() + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    Optional<Feedback> viewFeedback(Client client) {

        return Optional.empty();
    }


    //Client role

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
            //Client client = getClientById();
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
        Feedback.Estimate estimate = null;
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
