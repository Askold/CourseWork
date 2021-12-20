package ru.sfedu.crswork;

public class Constants {
    public static final String C1 = "STRING1";
    public static final String CONFIG_PATH ="./src/main/resources/environment.properties";
    //-----------------Data source-----------------
    public static final String DEFAULT_CSV_PATH = "csv_path";
    public static final String CSV_EXTENSION = ".csv";
    public static final String DEFAULT_XML_PATH = "xml_path";
    public static final String XML_EXTENSION = ".xml";
    public static final String DEFAULT_ACTOR = "system";
    public static final String CSV_CREATED ="Created new CSV file: ";
    public static final String CSV_PATH_IS = "CSV PATH is :";
    public static final String XML_CREATED ="Created new XML file: ";
    public static final String XML_PATH_IS = "XML PATH is :";
    //-----------------CRUD notifications-----------------
    public static final String RECORDS_ADDED = "All records were added";
    public static final String RECORDS_SELECTED = "Records were selected";
    public static final String FOUND = " found";
    public static final String NOT_FOUND = " not found";
    public static final String NOT_UPDATED =" wasn't updated";
    public static final String UPDATED =" updated";
    public static final String DELETED =" deleted";
    public static final String NOT_DELETED =" not deleted";
    public static final String ADDED =" was added";
    public static final String NOT_ADDED =" not added";
    //-----------------Use-case-----------------
    public static final String SOME_COMMENTS = "Some comments";
    public static final String SOME_DESCRIPTION = "Some description";
    public static final String WORKOUT_ESTIMATE = "Workout for client was: ";
    public static final String COMMENTS = "His comments: ";
    public static final String CLIENT_WORKOUT = "Client don't have a workout yet";
    public static final String ID_IS = " ID is ";
    public static final String EXERCISES_WORKOUT =  "Exercises for this workout wasn't found";
    public static final String IMPOSSIBLE_TO_VIEW = "Impossible to view workout";
    //-----------------History content-----------------
    public static final String HISTORY_ADDED = "New History record added";
    public static final String LOCALHOST=" localhost";
    public static final String MONGODB_NAME = "myMongoDb";
    public static final String CONNECTED_TO_MONGO = "Connected to MongoDB";
    public static final String CONNECTION_NAME ="HistoryContent";
    public static final String COLLECTION_CREATED ="MongoDB collection created";
    public static final String COLLECTION_RECIEVED = "MongoDB collection received";

    //public static final String  =;
    //-----------------Database-----------------
    public static final String DATABASE_URL = "jdbc:h2:./test";
    public static final String DATABASE_USER = "sa";
    public static final String DATABASE_PASSWORD = "";
    public static final String JDBC_DRIVER = "org.h2.Driver";
    public static final String CONNECTED_TO_DB = "Connected to DB";
    public static final String CLEARED = "Table was cleared";
    //-----------------SQL queries-----------------
    //---------General
    public static final String SELECT_ALL = "SELECT * FROM ";
    public static final String CLEAR = "TRUNCATE TABLE ";
    //---------Trainer CRUD
    public static final String INSERT_TRAINER ="INSERT INTO Trainer VALUES (?, ?, ?, ?, ?)";
    public static final String GET_TRAINER = "SELECT * FROM Trainer WHERE id = ?";
    public static final String UPDATE_TRAINER = "UPDATE Trainer SET name = ?, surname = ?, experience = ?, rating = ? WHERE id = ?";
    public static final String DELETE_TRAINER = "DELETE FROM Trainer WHERE id = ?";
    //---------Client CRUD
    public static final String INSERT_CLIENT ="INSERT INTO Client VALUES (?, ?, ?, ?, ?, ?, ?)";
    public static final String GET_CLIENT = "SELECT * FROM Client WHERE id = ?";
    public static final String UPDATE_CLIENT = "UPDATE Client SET name = ?, surname = ?, age = ?, weight = ?, height = ?, awaiting = ? WHERE id = ?";
    public static final String DELETE_CLIENT = "DELETE FROM Client WHERE id = ?";
    //---------Workout CRUD
    public static final String INSERT_WORKOUT ="INSERT INTO Workout VALUES (?, ?, ?, ?, ?)";
    public static final String GET_WORKOUT = "SELECT * FROM Workout WHERE id = ?";
    public static final String UPDATE_WORKOUT = "UPDATE Workout SET feedback = ?, type = ?, client = ?, trainer = ? WHERE id = ?";
    public static final String DELETE_WORKOUT = "DELETE FROM Workout WHERE id = ?";
    //---------Exercise CRUD
    public static final String INSERT_EXERCISE ="INSERT INTO Exercise VALUES (?, ?, ?, ?, ?, ?, ?)";
    public static final String GET_EXERCISE = "SELECT * FROM Exercise WHERE id = ?";
    //---------Feedback CRUD
    public static final String INSERT_FEEDBACK ="INSERT INTO Feedback VALUES (?, ?, ?, ?)";
    public static final String GET_FEEDBACK = "SELECT * FROM Feedback WHERE id = ?";
    //---------Creating tables
    public static final String CREATE_TABLE_TRAINER = "CREATE TABLE IF NOT EXISTS TRAINER (" +
            "id BIGINT PRIMARY KEY, " +
            "name VARCHAR, " +
            "surname VARCHAR," +
            "experience INTEGER, " +
            "rating INTEGER" +
            ")";
    public static final String CREATE_TABLE_CLIENT = "CREATE TABLE IF NOT EXISTS CLIENT (" +
            "id BIGINT PRIMARY KEY, " +
            "name VARCHAR, " +
            "surname VARCHAR," +
            "age INTEGER," +
            "weight INTEGER, " +
            "height INTEGER, " +
            "awaiting BOOLEAN" +
            ")";
    public static final String CREATE_TABLE_WORKOUT = "CREATE TABLE IF NOT EXISTS WORKOUT (" +
            "id BIGINT PRIMARY KEY, " +
            "feedback BIGINT, " +
            "type VARCHAR," +
            "client BIGINT, " +
            "trainer BIGINT" +
            ")";
    public static final String CREATE_TABLE_EXERCISE = "CREATE TABLE IF NOT EXISTS EXERCISE (" +
            "id BIGINT PRIMARY KEY, " +
            "name VARCHAR, " +
            "description VARCHAR," +
            "weight INTEGER, " +
            "repetitions INTEGER, " +
            "rounds INTEGER, " +
            "workout BIGINT" +
            ")";
    public static final String CREATE_TABLE_FEEDBACK = "CREATE TABLE IF NOT EXISTS FEEDBACK (" +
            "id BIGINT PRIMARY KEY, " +
            "Date VARCHAR, " +
            "Comment VARCHAR," +
            "Estimate VARCHAR " +
            ")";
    //-----------------CLI-----------------
    //---------Methods
    public static final String CREATE_EXERCISE = "create_exercise";
    public static final String CREATE_WORKOUT = "create_workout";
    public static final String CHECK_CLIENT = "check_client" ;
    public static final String EXECUTE_WORKOUT = "execute_workout" ;
    //---------Data Providers
    public static final String DP_CSV =  "csv";
    public static final String DP_XML =  "xml";
    public static final String DP_DB =  "db";
    //---------Workout types
    public static final String TYPE_AEROBIC = "AEROBIC";
    public static final String TYPE_STRENGTH = "STRENGTH" ;
    public static final String TYPE_FLEXIBILITY = "FLEXIBILITY" ;
    public static final String TYPE_BALANCE = "BALANCE" ;
    //---------Workout estimates
    //---------Errors
    public static final String DP_NOT_FOUND = " : such DataProvider not found" ;
    public static final String ARGS_ERROR = "Incorrect amount of arguments or type is wrong" ;
    public static final String TYPE_NOT_FOUND = "Such Workout Type not found";
    public static final String METHOD_NOT_FOUND =  ": such method not found";
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;
    //public static final String  =  ;

}
