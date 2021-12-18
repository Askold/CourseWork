package ru.sfedu.crswork.DataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.crswork.Constants;
import ru.sfedu.crswork.Models.*;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataProviderDB extends DataProvider{

    private static final Logger logger = LogManager.getLogger(DataProviderDB.class);
    private final Connection connection = getNewConnection();

    // -----------------General methods-------------------------------

    public boolean clearTable(Class<?> type){
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(Constants.CLEAR +type.getSimpleName());
            statement.execute();
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        logger.debug(Constants.CLEARED);
        addHistoryRecord(historyRecord);
        return true;
    }

    public boolean createTable(String query){
        Statement stmt;
        try {
            stmt = connection.createStatement();
            stmt.execute(query);
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            return false;
        }
        return true;
    }

    public Connection getNewConnection() {
        Connection con = null;
        try {
            Class.forName(Constants.JDBC_DRIVER);
            con = DriverManager.getConnection(Constants.DATABASE_URL,
                    Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            logger.error(e.getClass().getName() + "    " + e.getMessage());
        }
        logger.debug(Constants.CONNECTED_TO_DB);
        return con;

    }

    Optional<ResultSet> readFromDB(Class<?> type){
        PreparedStatement statement;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(Constants.SELECT_ALL + type.getSimpleName());
            statement.execute();
            resultSet = statement.getResultSet();

        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
        return Optional.ofNullable(resultSet);
    }

    // -------------------------Trainer class CRUD--------------------------------------

    List<Trainer> selectTrainers(){
        createTable(Constants.CREATE_TABLE_TRAINER);
        ResultSet resultSet = readFromDB(Trainer.class).orElseThrow();
        List<Trainer> list= new ArrayList<>();
        try {
            while (resultSet.next()){
                Trainer bean = new Trainer(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getInt(4), resultSet.getInt(5));
                list.add(bean);
            }
        } catch (SQLException e){
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }

        return list;
    }

    void executeTrainerStatement(PreparedStatement statement, Trainer trainer){
        try {
            statement.setLong(1, trainer.getId());
            statement.setString(2, trainer.getName());
            statement.setString(3, trainer.getSurname());
            statement.setInt(4, trainer.getWorkExperience());
            statement.setInt(5, trainer.getRating());
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
    }

    @Override
    boolean insertTrainer(Trainer trainer){
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        if(!createTable(Constants.CREATE_TABLE_TRAINER)) return false;
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(Constants.INSERT_TRAINER);
            executeTrainerStatement(statement, trainer);
            statement.execute();
            logger.debug(trainer.getClass().getSimpleName() + Constants.ADDED);
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        addHistoryRecord(historyRecord);
        return true;
    }

    @Override
    Optional<Trainer> getTrainerById(long id){
        Trainer trainer = new Trainer();
        createTable(Constants.CREATE_TABLE_TRAINER);
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(Constants.GET_TRAINER);
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                trainer.setId(resultSet.getLong(1));
                trainer.setName(resultSet.getString(2));
                trainer.setSurname(resultSet.getString(3));
                trainer.setWorkExperience(resultSet.getInt(4));
                trainer.setRating(resultSet.getInt(5));
            }
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
        return Optional.of(trainer);
    }

    @Override
    public boolean updateTrainer(Trainer trainer){
        createTable(Constants.CREATE_TABLE_TRAINER);
        PreparedStatement preparedStatement;
        try(Connection connection = getNewConnection()) {
            preparedStatement = connection.prepareStatement(Constants.UPDATE_TRAINER);
            executeTrainerStatement(preparedStatement, trainer);
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteTrainerById(long id){
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        createTable(Constants.CREATE_TABLE_TRAINER);
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(Constants.DELETE_TRAINER);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        addHistoryRecord(historyRecord);
        return true;
    }

    // -------------------------Client class CRUD--------------------------------------

    List<Client> selectClients(){
        createTable(Constants.CREATE_TABLE_CLIENT);
        ResultSet resultSet = readFromDB(Client.class).orElseThrow();
        List<Client> list= new ArrayList<>();
        try {
            while (resultSet.next()){
                Client bean = new Client(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getInt(4),
                        resultSet.getInt(5), resultSet.getInt(6),
                        resultSet.getBoolean(7));
                list.add(bean);
            }
        } catch (SQLException e){
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
        return list;
    }

    void executeClientStatement(PreparedStatement statement, Client client){
        try {
            statement.setLong(1, client.getId());
            statement.setString(2, client.getName());
            statement.setString(3, client.getSurname());
            statement.setInt(4, client.getAge());
            statement.setInt(5, client.getWeight());
            statement.setInt(6, client.getHeight());
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
    }

    @Override
    boolean insertClient(Client client) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        if(!createTable(Constants.CREATE_TABLE_CLIENT)) return false;
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(Constants.INSERT_CLIENT);
            executeClientStatement(statement, client);
            statement.execute();
            logger.debug(client.getClass().getSimpleName() + Constants.ADDED);

        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        addHistoryRecord(historyRecord);
        return true;
    }

    @Override
    Optional<Client> getClientById(long id) {
        Client client = new Client();
        createTable(Constants.CREATE_TABLE_CLIENT);
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(Constants.GET_CLIENT);
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                client.setId(resultSet.getLong(1));
                client.setName(resultSet.getString(2));
                client.setSurname(resultSet.getString(3));
                client.setAge(resultSet.getInt(4));
                client.setWeight(resultSet.getInt(5));
                client.setHeight(resultSet.getInt(6));
                client.setAwaiting(resultSet.getBoolean(7));
            }
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
        return Optional.of(client);
    }

    @Override
    boolean updateClient(Client client) {
        createTable(Constants.CREATE_TABLE_CLIENT);
        PreparedStatement preparedStatement;
        try(Connection connection = getNewConnection()) {
            preparedStatement = connection.prepareStatement(Constants.UPDATE_CLIENT);
            executeClientStatement(preparedStatement, client);
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    boolean deleteClientById(long id) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        createTable(Constants.CREATE_TABLE_CLIENT);
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(Constants.DELETE_CLIENT);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        addHistoryRecord(historyRecord);
        return true;
    }

    // -------------------------Exercise class CRUD--------------------------------------

    List<Exercise> selectExercise(){
        createTable(Constants.CREATE_TABLE_EXERCISE);
        ResultSet resultSet = readFromDB(Exercise.class).orElseThrow();
        List<Exercise> list= new ArrayList<>();
        try {
            while (resultSet.next()){
                Exercise bean = new Exercise(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getInt(4),
                        resultSet.getInt(5), resultSet.getInt(6),
                        resultSet.getLong(7));
                list.add(bean);
            }
        } catch (SQLException e){
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }

        return list;
    }

    @Override
    boolean insertExercise(Exercise exercise) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        if(!createTable(Constants.CREATE_TABLE_EXERCISE)) return false;
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(Constants.INSERT_EXERCISE);
            statement.setLong(1, exercise.getId());
            statement.setString(2, exercise.getName());
            statement.setString(3, exercise.getDescription());
            statement.setInt(4, exercise.getWeight());
            statement.setInt(5, exercise.getRepetitions());
            statement.setInt(6, exercise.getRounds());
            statement.setLong(7, exercise.getWorkout());
            statement.execute();
            logger.debug(exercise.getClass().getSimpleName() + Constants.ADDED);
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        addHistoryRecord(historyRecord);
        return true;
    }

    @Override
    Optional<Exercise> getExerciseById(long id) {
        Exercise exercise = new Exercise();
        createTable(Constants.CREATE_TABLE_EXERCISE);
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(Constants.GET_EXERCISE);
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                exercise.setId(resultSet.getLong(1));
                exercise.setName(resultSet.getString(2));
                exercise.setDescription(resultSet.getString(3));
                exercise.setWeight(resultSet.getInt(4));
                exercise.setRepetitions(resultSet.getInt(5));
                exercise.setRounds(resultSet.getInt(6));
                exercise.setWorkout(resultSet.getLong(7));
            }
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
        return Optional.of(exercise);
    }

    // -------------------------Workout class CRUD--------------------------------------

    List<Workout> selectWorkout(){
        createTable(Constants.CREATE_TABLE_WORKOUT);
        ResultSet resultSet = readFromDB(Workout.class).orElseThrow();
        List<Workout> list= new ArrayList<>();
        try {
            while (resultSet.next()){
                Workout bean = new Workout(resultSet.getLong(1), resultSet.getLong(2),
                        resultSet.getString(3), resultSet.getLong(4),
                        resultSet.getLong(5));
                list.add(bean);
            }
        } catch (SQLException e){
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }

        return list;
    }

    void executeWorkoutStatement(PreparedStatement statement, Workout workout){
        try {
            statement.setLong(1, workout.getId());
            statement.setLong(2, workout.getFeedback());
            statement.setString(3, String.valueOf(workout.getType()));
            statement.setLong(4, workout.getClient());
            statement.setLong(5, workout.getTrainer());
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
    }

    @Override
    boolean insertWorkout(Workout workout) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        if(!createTable(Constants.CREATE_TABLE_WORKOUT)) return false;
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(Constants.INSERT_WORKOUT);
            executeWorkoutStatement(statement, workout);
            statement.execute();
            logger.debug(workout.getClass().getSimpleName() + Constants.ADDED);

        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        addHistoryRecord(historyRecord);
        return true;
    }

    @Override
    Optional<Workout> getWorkoutById(long id) {
        Workout workout= new Workout();
        createTable(Constants.CREATE_TABLE_WORKOUT);
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(Constants.GET_WORKOUT);
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                workout.setId(resultSet.getLong(1));
                workout.setFeedback(resultSet.getLong(2));
                workout.setType(Workout.WorkoutType.valueOf(resultSet.getString(3)));
                workout.setClient(resultSet.getLong(4));
                workout.setTrainer(resultSet.getLong(5));
            }
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
        return Optional.of(workout);
    }

    @Override
    boolean updateWorkout(Workout workout) {
        createTable(Constants.CREATE_TABLE_WORKOUT);
        PreparedStatement preparedStatement;
        try(Connection connection = getNewConnection()) {
            preparedStatement = connection.prepareStatement(Constants.UPDATE_WORKOUT);
            executeWorkoutStatement(preparedStatement, workout);
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    boolean deleteWorkoutById(long id) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        createTable(Constants.CREATE_TABLE_WORKOUT);
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(Constants.DELETE_WORKOUT);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        addHistoryRecord(historyRecord);
        return true;
    }

    // -------------------------Feedback class CRUD--------------------------------------

    List<Feedback> selectFeedbacks(){
        createTable(Constants.CREATE_TABLE_FEEDBACK);
        ResultSet resultSet = readFromDB(Feedback.class).orElseThrow();
        List<Feedback> list= new ArrayList<>();
        try {
            while (resultSet.next()){
                Feedback bean = new Feedback(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4));
                list.add(bean);
            }
        } catch (SQLException e){
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }

        return list;
    }

    @Override
    boolean insertFeedback(Feedback feedback) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        if(!createTable(Constants.CREATE_TABLE_FEEDBACK)) return false;
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(Constants.INSERT_FEEDBACK);
            statement.setLong(1, feedback.getId());
            statement.setString(2, feedback.getDate());
            statement.setString(3, feedback.getComment());
            statement.setString(4, String.valueOf(feedback.getEstimate()));
            statement.execute();
            logger.debug(feedback.getClass().getSimpleName() + Constants.ADDED);

        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            historyRecord.setStatus(HistoryContent.Status.FAULT);
            addHistoryRecord(historyRecord);
            return false;
        }
        addHistoryRecord(historyRecord);
        return true;
    }

    @Override
    Optional<Feedback> getFeedbackById(long id) {
        Feedback feedback = new Feedback();
        createTable(Constants.CREATE_TABLE_FEEDBACK);
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(Constants.GET_FEEDBACK);
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                feedback.setId(resultSet.getLong(1));
                feedback.setDate(resultSet.getString(2));
                feedback.setComment(resultSet.getString(3));
                feedback.setEstimate(Feedback.Estimate.valueOf(resultSet.getString(4)));
            }
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
        return Optional.of(feedback);
    }

    // -------------------------Use case implementation--------------------------------------

    @Override
    boolean checkClient(long id) {
        return false;
    }

    @Override
    boolean viewFeedback(long client) {
        return false;
    }

    @Override
    boolean createWorkout(String type, long client, long trainer) {
        return false;
    }

    @Override
    boolean createExercise(String name, int weight, int repetitions, int rounds, long workout) {
        return false;
    }

    @Override
    boolean executeWorkout(long workoutID, String isCompleted) {
        return false;
    }

    @Override
    boolean viewWorkout(long workoutID) {
        return false;
    }

    @Override
    long composeFeedback(String isCompleted) {
        return 0;
    }


    @Override
    File initDataSource(Class<?> type) {
        return null;
    }

    @Override
    <T> boolean saveRecords(List<T> beans) {
        return false;
    }

    @Override
    <T> List<T> selectRecords(Class<?> type) {
        return null;
    }
}
