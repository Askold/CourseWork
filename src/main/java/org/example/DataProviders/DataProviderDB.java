package org.example.DataProviders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.Constants;
import org.example.Models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataProviderDB{
    private static final Logger logger = LogManager.getLogger(DataProviderDB.class);

    private final Connection connection = getNewConnection();

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS TRAINER (" +
            "id BIGINT PRIMARY KEY, " +
            "name VARCHAR, " +
            "surname VARCHAR)";
    public static final String INSERT ="INSERT INTO Trainer VALUES (?, ?, ?)";
    public static final String SELECT_ALL = "SELECT * FROM ";
    public static final String GET_ONE = "SELECT * FROM TRAINER WHERE id = ?";
    public static final String UPDATE = "UPDATE Trainer SET name = ?, surname = ? WHERE id = ?";
    public static final String DELETE = "DELETE FROM Trainer WHERE id = ?";
    public static final String CLEAR = "TRUNCATE TABLE Trainer";

    public Optional<ResultSet> readFromDB(Class<?> type){
        createTable();
        PreparedStatement statement;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SELECT_ALL + type.getSimpleName());
            statement.execute();
            resultSet = statement.getResultSet();

        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
        return Optional.ofNullable(resultSet);
    }

    public List<Trainer> selectTrainers(){
        ResultSet resultSet = readFromDB(Trainer.class).orElseThrow();
        List<Trainer> list= new ArrayList<>();
        try {
            while (resultSet.next()){
                Trainer bean = new Trainer(resultSet.getLong(1), resultSet.getString(2),
                        resultSet.getString(3));
                list.add(bean);
            }
        } catch (SQLException e){
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }

        return list;
    }

    public boolean insertTrainer(Trainer trainer){
        if(!createTable()) return false;
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(INSERT);
            statement.setString(1, trainer.getClass().getSimpleName());
            statement.setString(1, String.valueOf(trainer.getId()));
            statement.setString(2, trainer.getName());
            statement.setString(3, trainer.getSurname());
            statement.execute();
            logger.debug(trainer.getClass().getSimpleName() + Constants.ADDED);

        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            return false;
        }
        return true;
    }

    public Optional<Trainer>  getTrainerByID(long id){
        Trainer trainer = new Trainer();
        createTable();
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(GET_ONE);
            statement.setString(1, String.valueOf(id));
            statement.execute();
            ResultSet resultSet = statement.getResultSet();
            if (resultSet.next()) {
                trainer.setId(resultSet.getLong(1));
                trainer.setName(resultSet.getString(2));
                trainer.setSurname(resultSet.getString(3));
            }
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
        }
        return Optional.of(trainer);
    }

    public boolean updateTrainer(Trainer trainer){
        createTable();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(UPDATE);
            preparedStatement.setString(1, trainer.getName());
            preparedStatement.setString(2, trainer.getSurname());
            preparedStatement.setString(3, String .valueOf(trainer.getId()));
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteTrainerById(long id){
        createTable();
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(DELETE);
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            return false;
        }
        return true;
    }

    public boolean clearTable(){
        createTable();
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(CLEAR);
            statement.execute();
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            return false;
        }
        return true;
    }

    public boolean createTable(){
        Statement stmt;
        try {
            stmt = connection.createStatement();
            stmt.execute(CREATE_TABLE);
        } catch (SQLException e) {
            logger.error(e.getClass().getName() +"    "+ e.getMessage());
            return false;
        }
        return true;
    }

    public Connection getNewConnection() {
        Connection con = null;
        try {
            Class.forName("org.h2.Driver");
            con = DriverManager.getConnection(Constants.DATABASE_URL,
                    Constants.DATABASE_USER, Constants.DATABASE_PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            logger.error(e.getClass().getName() + "    " + e.getMessage());
        }
        logger.debug("Connected to DB");

        return con;

    }
}
