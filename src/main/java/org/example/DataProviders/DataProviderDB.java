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

    public Optional<ResultSet> readFromDB(Class<?> type){
        createTable();
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
            statement = connection.prepareStatement(Constants.INSERT_TRAINER);
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
            statement = connection.prepareStatement(Constants.GET_TRAINER);
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
        try(Connection connection = getNewConnection()) {
            preparedStatement = connection.prepareStatement(Constants.UPDATE_TRAINER);
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
            preparedStatement = connection.prepareStatement(Constants.DELETE_TRAINER);
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
            statement = connection.prepareStatement(Constants.CLEAR + Trainer.class.getSimpleName());
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
            stmt.execute(Constants.CREATE_TABLE_TRAINER);
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
}
