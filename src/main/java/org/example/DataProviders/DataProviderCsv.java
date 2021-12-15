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
import java.util.stream.Stream;

public class DataProviderCsv extends DataProvider {
    private String FilePath;
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
            logger.error(e.getClass().getName() + e.getMessage());
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
    boolean createNewTrainer(Trainer trainer) {
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
            createNewTrainer(bean);
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
    boolean createNewClient(Client client) {
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
            createNewClient(bean);
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
        if (getClientById(id).isPresent()) beanToRemove = getClientById(id).get();
        List<Client> listOfBeans = selectRecords(Client.class);
        Client finalBeanToRemove = beanToRemove;
        listOfBeans.removeIf(bean -> bean.equals(finalBeanToRemove));
        addHistoryRecord(historyRecord);
        logger.debug(beanToRemove.getClass().getSimpleName() + Constants.DELETED);
        return saveRecords(listOfBeans);
    }

    // -------------------------Exercises class CRUD--------------------------------------
    @Override
    boolean createNewExercise(Exercise exercise) {
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

    @Override
    boolean updateExercise(Exercise bean) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Exercise beanToUpdate = new Exercise();
        if (getExerciseById(bean.getId()).isPresent()) beanToUpdate = getExerciseById(bean.getId()).get();
        if (!beanToUpdate.equals(bean)) {
            if (!deleteTrainerById(bean.getId())){
                historyRecord.setStatus(HistoryContent.Status.FAULT);
                addHistoryRecord(historyRecord);
                logger.error(bean.getClass().getSimpleName() + Constants.NOT_UPDATED);
                return false;
            }
            createNewExercise(bean);
        }
        addHistoryRecord(historyRecord);
        logger.debug(bean.getClass().getSimpleName() + Constants.UPDATED);
        return true;
    }

    @Override
    boolean deleteExerciseById(long id) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Exercise beanToRemove = new Exercise();
        if (getExerciseById(id).isPresent()) beanToRemove = getExerciseById(id).get();
        List<Exercise> listOfBeans = selectRecords(Exercise.class);
        Exercise finalBeanToRemove = beanToRemove;
        listOfBeans.removeIf(bean -> bean.equals(finalBeanToRemove));
        addHistoryRecord(historyRecord);
        logger.debug(beanToRemove.getClass().getSimpleName() + Constants.DELETED);
        return saveRecords(listOfBeans);
    }

    // -------------------------Workout class CRUD--------------------------------------
    @Override
    boolean createNewWorkout(Workout workout) {
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
            createNewWorkout(bean);
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
        if (getWorkoutById(id).isPresent()) beanToRemove = getWorkoutById(id).get();
        List<Workout> listOfBeans = selectRecords(Workout.class);
        Workout finalBeanToRemove = beanToRemove;
        listOfBeans.removeIf(bean -> bean.equals(finalBeanToRemove));
        addHistoryRecord(historyRecord);
        logger.debug(beanToRemove.getClass().getSimpleName() + Constants.DELETED);
        return saveRecords(listOfBeans);
    }

    // -------------------------Feedback class CRUD--------------------------------------
    @Override
    boolean createNewFeedback(Feedback feedback) {
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

    @Override
    boolean updateFeedback(Feedback bean) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Feedback beanToUpdate = new Feedback();
        if (getFeedbackById(bean.getId()).isPresent()) beanToUpdate = getFeedbackById(bean.getId()).get();
        if (!beanToUpdate.equals(bean)) {
            if (!deleteTrainerById(bean.getId())){
                historyRecord.setStatus(HistoryContent.Status.FAULT);
                addHistoryRecord(historyRecord);
                logger.error(bean.getClass().getSimpleName() + Constants.NOT_UPDATED);
                return false;
            }
            createNewFeedback(bean);
        }
        addHistoryRecord(historyRecord);
        logger.debug(bean.getClass().getSimpleName() + Constants.UPDATED);
        return true;
    }

    @Override
    boolean deleteFeedbackById(long id) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Feedback beanToRemove = new Feedback();
        if (getFeedbackById(id).isPresent()) beanToRemove = getFeedbackById(id).get();
        List<Feedback> listOfBeans = selectRecords(Feedback.class);
        Feedback finalBeanToRemove = beanToRemove;
        listOfBeans.removeIf(bean -> bean.equals(finalBeanToRemove));
        addHistoryRecord(historyRecord);
        logger.debug(beanToRemove.getClass().getSimpleName() + Constants.DELETED);
        return saveRecords(listOfBeans);
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

    @Override
    boolean updateProgram(Program bean) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Program beanToUpdate = new Program();
        if (getProgramById(bean.getId()).isPresent()) beanToUpdate = getProgramById(bean.getId()).get();
        if (!beanToUpdate.equals(bean)) {
            if (!deleteTrainerById(bean.getId())){
                historyRecord.setStatus(HistoryContent.Status.FAULT);
                addHistoryRecord(historyRecord);
                logger.error(bean.getClass().getSimpleName() + Constants.NOT_UPDATED);
                return false;
            }
            createNewProgram(bean);
        }
        addHistoryRecord(historyRecord);
        logger.debug(bean.getClass().getSimpleName() + Constants.UPDATED);
        return true;
    }

    @Override
    boolean deleteProgramById(long id) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        Program beanToRemove = new Program();
        if (getProgramById(id).isPresent()) beanToRemove = getProgramById(id).get();
        List<Program> listOfBeans = selectRecords(Program.class);
        Program finalBeanToRemove = beanToRemove;
        listOfBeans.removeIf(bean -> bean.equals(finalBeanToRemove));
        addHistoryRecord(historyRecord);
        logger.debug(beanToRemove.getClass().getSimpleName() + Constants.DELETED);
        return saveRecords(listOfBeans);
    }

    // ------------------------- Use-case extra implementation--------------------------------------
    //Trainer role
    @Override
    boolean composeProgram( int workoutsAmount, int durationInWeeks, int workoutsPerWeek,
                                  String description, Program.ProgramType type, long clientId) {
        Program program = new Program(durationInWeeks, workoutsPerWeek, description, type);
        Long[] list = new Long[workoutsAmount];
        Arrays.stream(list).forEach(x -> x=composeSomeWorkout());
        program.setWorkouts(Arrays.asList(list));
        return !(!createNewProgram(program) | !updateClientPrograms(clientId, program.getId()));
    }

    boolean updateClientPrograms(long clientId, long programId){
        Client client;
        if (getClientById(clientId).isPresent()) {
            client = getClientById(clientId).get();
        }
        else {
            return false;
        }
        List<Long> clientPrograms = client.getPrograms();
        clientPrograms.add(programId);
        client.setPrograms(clientPrograms);
        updateClient(client);
        return true;
    }

    @Override
    long composeWorkout(List<Long> exercises, List<Integer> weights, List<Integer> rounds,
                           List<Integer> repetitions) {
        Workout workout = new Workout(weights, rounds, repetitions);
        addExercises(exercises);
        workout.setExercises(exercises);
        createNewWorkout(workout);
        return workout.getId();
    }

    long composeSomeWorkout(){
        List<Long> exercises = List.of();
        List<Integer> weights = List.of();
        List<Integer> rounds = List.of();
        List<Integer> repetitions = List.of();
        return composeWorkout(exercises, weights, rounds, repetitions);
    }

    @Override
    Optional<Exercise> addNewExercise(long id){
        Exercise exercise = new Exercise(id);
        createNewExercise(exercise);
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
    Optional<Client> selectClient(long id) {
        Client client;
        Optional<Client> opt = getClientById(id);
        client = opt.orElseGet(() -> addNewClient(id).orElseThrow());
        logger.info(Constants.SELECTED_CLIENT + client);
        viewFeedback(client);
        return Optional.of(client);
    }

    @Override
    Optional<Feedback> viewFeedback(Client client) {
        Workout workout = new Workout();
        try {
             workout = getLastWorkout(getLastProgram(client).orElseThrow()).orElseThrow();
        } catch (NoSuchElementException e){
            logger.error(e.getClass().getName() + e.getMessage());
        }
        List<Long> feedbacks = workout.getFeedbacks();
        Feedback feedback = getFeedbackById(feedbacks.get(feedbacks.size() - 1)).orElseThrow();
        logger.info(Constants.LAST_FEEDBACK + feedback);
        return Optional.of(feedback);
    }

    Optional<Program> getLastProgram(Client client){
        List<Long> programs = client.getPrograms();
        long programId = programs.get(programs.size() - 1);
        Program program = getProgramById(programId).orElseThrow();
        logger.info(Constants.LAST_PROGRAM + program);
        return Optional.of(program);
    }

    Optional<Workout> getLastWorkout(Program program){
        List<Long> workouts = program.getWorkouts();
        long workoutId = workouts.get(workouts.size() - 1);
        Workout workout = getWorkoutById(workoutId).orElseThrow();
        logger.info(Constants.LAST_WORKOUT + program);
        return Optional.of(workout);
    }

    @Override
    Optional<Client> addNewClient(long id) {
        Client client = new Client(id);
        createNewClient(client);
        return Optional.of(client);
    }

    //Client role
    @Override
    Optional<Program> selectProgram(Client client) {
        Program program = getLastProgram(client).orElseThrow();
        return Optional.of(program);
    }

    @Override
    Optional<Workout> selectWorkout(Program program, boolean isCompleted) {
        Workout workout = getLastWorkout(program).orElseThrow();
        if (isCompleted){
            composeFeedback(workout);
        }
        return Optional.of(workout);
    }

    @Override
    boolean composeFeedback(Workout workout) {
        Feedback feedback = new Feedback();
        List<Long> feedbacks = workout.getFeedbacks();
        feedbacks.add(feedback.getId());
        workout.setFeedbacks(feedbacks);
        return !(!createNewFeedback(feedback) | !updateWorkout(workout));
    }


}
