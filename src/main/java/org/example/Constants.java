package org.example;

public class Constants {
    public static final String C1 = "STRING1";
    public static final String CONFIG_PATH ="./src/main/resources/environment.properties";
    //Data source
    public static final String DEFAULT_CSV_PATH = "csv_path";
    public static final String CSV_EXTENSION = ".csv";
    public static final String DEFAULT_XML_PATH = "xml_path";
    public static final String XML_EXTENSION = ".xml";
    public static final String DEFAULT_ACTOR = "system";
    public static final String CSV_CREATED ="Created new CSV file: ";
    public static final String CSV_PATH_IS = "CSV PATH is :";
    public static final String XML_CREATED ="Created new XML file: ";
    public static final String XML_PATH_IS = "XML PATH is :";
    //CRUD notifications
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
    //Use-case
    public static final String SOME_COMMENTS = "Some comments";
    public static final String LAST_WORKOUT = "Last workout is:";
    public static final String LAST_PROGRAM = "Last program is:";
    public static final String SELECTED_CLIENT = "Selected client:";
    //Database
    public static final String DATABASE_URL = "jdbc:h2:./test";
    public static final String DATABASE_USER = "sa";
    public static final String DATABASE_PASSWORD = "";
    public static final String JDBC_DRIVER = "org.h2.Driver";
    public static final String CONNECTED_TO_DB = "Connected to DB";
    //Queries
    public static final String CREATE_TABLE_TRAINER = "CREATE TABLE IF NOT EXISTS TRAINER (" +
                                                            "id BIGINT PRIMARY KEY, " +
                                                            "name VARCHAR, " +
                                                            "surname VARCHAR)";
    public static final String INSERT_TRAINER ="INSERT INTO Trainer VALUES (?, ?, ?)";
    public static final String SELECT_ALL = "SELECT * FROM ";
    public static final String GET_TRAINER = "SELECT * FROM TRAINER WHERE id = ?";
    public static final String UPDATE_TRAINER = "UPDATE Trainer SET name = ?, surname = ? WHERE id = ?";
    public static final String DELETE_TRAINER = "DELETE FROM Trainer WHERE id = ?";
    public static final String CLEAR = "TRUNCATE TABLE ";
   //public static final String  =;
   //public static final String  =;

}
