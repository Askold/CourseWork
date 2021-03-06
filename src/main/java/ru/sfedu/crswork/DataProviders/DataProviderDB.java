package ru.sfedu.crswork.DataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.crswork.Constants;
import ru.sfedu.crswork.Models.*;
import ru.sfedu.crswork.Utils.ConfigurationUtil;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DataProviderDB extends DataProvider{

    private static final Logger logger = LogManager.getLogger(DataProviderDB.class);
    //--------------------!!!Only methods different from DataProviderCsv have comments!!!----------------

    // -----------------General methods-------------------------------
    /**
     * clears table
     * @param type class of objects which table should be cleared
     * @return result of clearing
     */
    public boolean clearTable(Class<?> type){
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        PreparedStatement statement;
        // executing the statement
        try (Connection connection = getNewConnection()) {
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

    /**
     * creating new table if it doesn't exist
     * @param query creating query
     * @param connection object of Connection class
     * @return result of creating
     */
    public boolean createTable(String query, Connection connection){
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

    /**
     * receives new connection to database
     * @return result of connecting
     */
    public Connection getNewConnection() {
        Connection con = null;
        try {
            String url = ConfigurationUtil.getConfigurationEntry(Constants.DATABASE_URL);
            logger.info(url);
            String user = ConfigurationUtil.getConfigurationEntry(Constants.DATABASE_USER);
            logger.info(user);
            String driver = ConfigurationUtil.getConfigurationEntry(Constants.JDBC_DRIVER);
            logger.info(driver);
            Class.forName(driver);
            con = DriverManager.getConnection(url, user, Constants.DATABASE_PASSWORD);
        } catch (SQLException | ClassNotFoundException | IOException e) {
            logger.error(e.getClass().getName() + "    " + e.getMessage());
        }
        logger.debug(Constants.CONNECTED_TO_DB);
        return con;

    }

    /**
     * stage of selecting records of special type
     * @param type class which objects should be selected
     * @param connection Connection class object
     * @return ResultSet class object
     */
    Optional<ResultSet> readFromDB(Class<?> type, Connection connection){
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
    // only Trainer class has comments because other CRUD's are the same
    /**
     * secelting all records of Trainers class from database
     * @return List of elements
     */
    public List<Trainer> selectTrainers(){
        List<Trainer> list= new ArrayList<>();
        try (Connection connection = getNewConnection()){
            createTable(Constants.CREATE_TABLE_TRAINER, connection); // Creating table if it doesn't exist
            ResultSet resultSet = readFromDB(Trainer.class, connection).orElseThrow(); // Getting resultSet
            while (resultSet.next()){ // resultSet to bean
                Trainer bean = new Trainer(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getInt(4), resultSet.getInt(5));
                list.add(bean); // Updating list of beans
            }
        } catch (SQLException e){
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * compose statement with Trainer object parameters
     * @param statement PreparedStatement class object
     * @param trainer Trainer class object
     */
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
    public boolean insertTrainer(Trainer trainer){
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        PreparedStatement statement;
        try (Connection connection = getNewConnection()) {
            if(!createTable(Constants.CREATE_TABLE_TRAINER, connection)) return false;
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

    /**
     * transforms ResultSet object to Trainer object
     * @param resultSet ResultSet class object
     * @return Trainer class object
     */
    Optional<Trainer> resultSetToTrainer(ResultSet resultSet){
        Trainer trainer = new Trainer();
        try {
            trainer.setId(resultSet.getLong(1));
            trainer.setName(resultSet.getString(2));
            trainer.setSurname(resultSet.getString(3));
            trainer.setWorkExperience(resultSet.getInt(4));
            trainer.setRating(resultSet.getInt(5));
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    Optional<Trainer> getTrainerById(long id){
        Trainer trainer = new Trainer();
        PreparedStatement statement;
        try (Connection connection = getNewConnection()) {
            createTable(Constants.CREATE_TABLE_TRAINER, connection);
            statement = connection.prepareStatement(Constants.GET_TRAINER);
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                trainer = resultSetToTrainer(resultSet).orElseThrow();
            } else {
                logger.error(trainer.getClass().getSimpleName() + Constants.NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            return Optional.empty();
        }
        return Optional.of(trainer);
    }

    @Override
    public boolean updateTrainer(Trainer trainer){
        PreparedStatement preparedStatement;
        try(Connection connection = getNewConnection()) {
            createTable(Constants.CREATE_TABLE_TRAINER, connection);
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
        PreparedStatement preparedStatement;
        try (Connection connection = getNewConnection()){
            createTable(Constants.CREATE_TABLE_TRAINER, connection);
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
        List<Client> list= new ArrayList<>();
        try (Connection connection = getNewConnection()){
            ResultSet resultSet = readFromDB(Client.class, connection).orElseThrow();
            while (resultSet.next()){
                createTable(Constants.CREATE_TABLE_CLIENT, connection);
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
            statement.setBoolean(7, client.isAwaiting());
            statement.execute();
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
    }

    @Override
    public boolean insertClient(Client client) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        PreparedStatement statement;
        try (Connection connection = getNewConnection()){
            if(!createTable(Constants.CREATE_TABLE_CLIENT, connection)) return false;
            statement = connection.prepareStatement(Constants.INSERT_CLIENT);
            executeClientStatement(statement, client);
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

    Optional<Client> resultSetToClient(ResultSet resultSet){
        Client client = new Client();
        try {
            client.setId(resultSet.getLong(1));
            client.setName(resultSet.getString(2));
            client.setSurname(resultSet.getString(3));
            client.setAge(resultSet.getInt(4));
            client.setWeight(resultSet.getInt(5));
            client.setHeight(resultSet.getInt(6));
            client.setAwaiting(resultSet.getBoolean(7));
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            return Optional.empty();
        }
        return Optional.of(client);
    }

    @Override
    Optional<Client> getClientById(long id) {
        Client client = new Client();
        PreparedStatement statement;
        try (Connection connection = getNewConnection()){
            createTable(Constants.CREATE_TABLE_CLIENT, connection);
            statement = connection.prepareStatement(Constants.GET_CLIENT);
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                client = resultSetToClient(resultSet).orElseThrow();
            }else {
                logger.error(client.getClass().getSimpleName() + Constants.NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            return Optional.empty();
        }
        return Optional.of(client);
    }

    @Override
    boolean updateClient(Client client) {
        PreparedStatement preparedStatement;
        try(Connection connection = getNewConnection()) {
            createTable(Constants.CREATE_TABLE_CLIENT, connection);
            preparedStatement = connection.prepareStatement(Constants.UPDATE_CLIENT);
            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getSurname());
            preparedStatement.setInt(3, client.getAge());
            preparedStatement.setInt(4, client.getWeight());
            preparedStatement.setInt(5, client.getHeight());
            preparedStatement.setBoolean(6, client.isAwaiting());
            preparedStatement.setLong(7, client.getId());
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
        PreparedStatement preparedStatement;
        try (Connection connection = getNewConnection()){
            createTable(Constants.CREATE_TABLE_CLIENT, connection);
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

    List<Exercise> selectExercises(){
        List<Exercise> list= new ArrayList<>();
        try (Connection connection = getNewConnection()){
            createTable(Constants.CREATE_TABLE_EXERCISE, connection);
            ResultSet resultSet = readFromDB(Exercise.class, connection).orElseThrow();
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
    public boolean insertExercise(Exercise exercise) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        PreparedStatement statement;
        try (Connection connection = getNewConnection()){
            if(!createTable(Constants.CREATE_TABLE_EXERCISE, connection)) return false;
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

    Optional<Exercise> resultSetToExercise(ResultSet resultSet){
        Exercise exercise = new Exercise();
        try {
            exercise.setId(resultSet.getLong(1));
            exercise.setName(resultSet.getString(2));
            exercise.setDescription(resultSet.getString(3));
            exercise.setWeight(resultSet.getInt(4));
            exercise.setRepetitions(resultSet.getInt(5));
            exercise.setRounds(resultSet.getInt(6));
            exercise.setWorkout(resultSet.getLong(7));
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
        return Optional.of(exercise);
    }

    @Override
    Optional<Exercise> getExerciseById(long id) {
        Exercise exercise = new Exercise();
        PreparedStatement statement;
        try (Connection connection = getNewConnection()){
            createTable(Constants.CREATE_TABLE_EXERCISE, connection);
            statement = connection.prepareStatement(Constants.GET_EXERCISE);
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                exercise = resultSetToExercise(resultSet).orElseThrow();
            } else {
                logger.error(exercise.getClass().getSimpleName() + Constants.NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            return Optional.empty();
        }
        return Optional.of(exercise);
    }

    // -------------------------Workout class CRUD--------------------------------------

    List<Workout> selectWorkouts(){
        List<Workout> list= new ArrayList<>();
        try (Connection connection = getNewConnection()){
            ResultSet resultSet = readFromDB(Workout.class, connection).orElseThrow();
            while (resultSet.next()){
                createTable(Constants.CREATE_TABLE_WORKOUT, connection);
                Workout bean = new Workout(resultSet.getLong(1), resultSet.getLong(2),
                        Workout.WorkoutType.valueOf(resultSet.getString(3)), resultSet.getLong(4),
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
    public boolean insertWorkout(Workout workout) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        PreparedStatement statement;
        try (Connection connection = getNewConnection()){
            if(!createTable(Constants.CREATE_TABLE_WORKOUT, connection)) return false;
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

    Optional<Workout> resultSetToWorkout(ResultSet resultSet){
        Workout workout = new Workout();
        try {
        workout.setId(resultSet.getLong(1));
        workout.setFeedback(resultSet.getLong(2));
        workout.setType(Workout.WorkoutType.valueOf(resultSet.getString(3)));
        workout.setClient(resultSet.getLong(4));
        workout.setTrainer(resultSet.getLong(5));
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            return Optional.empty();
        }
        return Optional.of(workout);
    }

    @Override
    Optional<Workout> getWorkoutById(long id) {
        Workout workout= new Workout();
        PreparedStatement statement;
        try (Connection connection = getNewConnection()){
            createTable(Constants.CREATE_TABLE_WORKOUT, connection);
            statement = connection.prepareStatement(Constants.GET_WORKOUT);
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                workout = resultSetToWorkout(resultSet).orElseThrow();
            } else {
                logger.error(workout.getClass().getSimpleName()+Constants.NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            return Optional.empty();
        }
        logger.debug(workout);
        return Optional.of(workout);
    }

    @Override
    boolean updateWorkout(Workout workout) {
        PreparedStatement preparedStatement;
        try(Connection connection = getNewConnection()) {
            createTable(Constants.CREATE_TABLE_WORKOUT, connection);
            preparedStatement = connection.prepareStatement(Constants.UPDATE_WORKOUT);
            executeWorkoutStatement(preparedStatement, workout);
            preparedStatement.setLong(1, workout.getFeedback());
            preparedStatement.setString(2, String.valueOf(workout.getType()));
            preparedStatement.setLong(3, workout.getClient());
            preparedStatement.setLong(4, workout.getTrainer());
            preparedStatement.setLong(5, workout.getId());
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
        PreparedStatement preparedStatement;
        try (Connection connection = getNewConnection()){
            createTable(Constants.CREATE_TABLE_WORKOUT, connection);
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
        List<Feedback> list= new ArrayList<>();
        try (Connection connection = getNewConnection()){
            createTable(Constants.CREATE_TABLE_FEEDBACK, connection);
            ResultSet resultSet = readFromDB(Feedback.class, connection).orElseThrow();
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
    public boolean insertFeedback(Feedback feedback) {
        HistoryContent historyRecord = new HistoryContent(getClass().toString(),
                Thread.currentThread().getStackTrace()[1].getMethodName());
        PreparedStatement statement;
        try (Connection connection = getNewConnection()){
            if(!createTable(Constants.CREATE_TABLE_FEEDBACK, connection)) return false;
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

    Optional<Feedback> resultSetToFeedback(ResultSet resultSet){
        Feedback feedback = new Feedback();
        try {
            feedback.setId(resultSet.getLong(1));
            feedback.setDate(resultSet.getString(2));
            feedback.setComment(resultSet.getString(3));
            feedback.setEstimate(Feedback.Estimate.valueOf(resultSet.getString(4)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(feedback);
    }

    @Override
    Optional<Feedback> getFeedbackById(long id) {
        Feedback feedback = new Feedback();
        PreparedStatement statement;
        try (Connection connection = getNewConnection()){
            createTable(Constants.CREATE_TABLE_FEEDBACK, connection);
            statement = connection.prepareStatement(Constants.GET_FEEDBACK);
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                feedback = resultSetToFeedback(resultSet).orElseThrow();
            } else {
                logger.error(feedback.getClass().getSimpleName() + Constants.NOT_FOUND);
                return Optional.empty();
            }
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            return Optional.empty();
        }
        return Optional.of(feedback);
    }

    // -------------------------Use case implementation--------------------------------------
    // Trainer role
    @Override
    public boolean checkClient(long id) {
        Client client = getClientById(id).orElseThrow();
        if (client.isAwaiting()){
            List<Workout> workouts = selectWorkouts();
            Optional<Workout> result = workouts.stream().filter(bean -> bean.getClient() == id).findFirst();
            if(result.isEmpty()){
                logger.error(Constants.CLIENT_WORKOUT);
                return false;
            }
            viewFeedback(result.get().getFeedback());
        }else{
            logger.info(Constants.NOT_FINISHED);
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
    public boolean createWorkout(Workout.WorkoutType typeWorkout, long client, long trainer) {
        Workout workout = new Workout(typeWorkout, client, trainer);
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
        if (getClientById(id).isEmpty()){
            return false;
        }
        Client client = getClientById(id).orElseThrow();
        client.setAwaiting(false);
        return updateClient(client);
    }

    @Override
    public boolean createExercise(String name, int weight, int repetitions, int rounds, long workout) {
        if(getWorkoutById(workout).isEmpty()){
            return false;
        }
        Exercise exercise = new Exercise(name, weight, repetitions, rounds, workout);
        logger.info(Constants.EXERCISE_SUCCESS);
        return insertExercise(exercise);
    }

    // Client role

    @Override
    public boolean executeWorkout(long workoutID, String isCompleted) {
        if (getWorkoutById(workoutID).isEmpty()){
            logger.error(Workout.class.getSimpleName()+Constants.NOT_FOUND);
            return false;
        }
        if(!viewWorkout(workoutID)){
            logger.error(Constants.IMPOSSIBLE_TO_VIEW);
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
        logger.info(Constants.WORKOUT_EXECUTED);
        return true;
    }

    @Override
    boolean viewWorkout(long workoutID) {
        List<Exercise> exercises = selectExercises();
        List<Exercise> result = exercises.stream().filter(bean -> bean.getWorkout() == workoutID).collect(Collectors.toList());
        if(result.isEmpty()) {
            logger.error(Constants.EXERCISES_WORKOUT);
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
        logger.info(Constants.FEEDBACK_SUCCESS);
        return feedback.getId();
    }

}
