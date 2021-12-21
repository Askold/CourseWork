package ru.sfedu.crswork;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.sfedu.crswork.DataProviders.DataProvider;
import ru.sfedu.crswork.DataProviders.DataProviderCsv;
import ru.sfedu.crswork.DataProviders.DataProviderDB;
import ru.sfedu.crswork.DataProviders.DataProviderXml;
import ru.sfedu.crswork.Models.*;

import java.util.List;
import java.util.Optional;
public class App {

    private static final Logger logger = LogManager.getLogger(App.class);

    /**
     * Main function
     * @param args received parameters
     */
    public static void main( String[] args ) {
        // checks argument are not absent
        if (args.length == 0){
            logger.error(Constants.ARGS_ERROR);
            return;
        }
        // selecting DataProvider
        DataProvider dataProvider = selectDataProvider(args[0]).orElseThrow();
        String method;
        try{
            method = args[1];
        } catch (IndexOutOfBoundsException e){
            logger.error(e.getClass().getSimpleName() + "    "+ e.getMessage());
            logger.error(Constants.ARGS_ERROR);
            return;
        }
        // selecting method
        switch (method){
            case Constants.CREATE_EXERCISE:
                String name;
                int weight;
                int repetitions;
                int rounds;
                long workout;
                try{
                    name = args[2];
                    weight = Integer.parseInt(args[3]);
                    repetitions = Integer.parseInt(args[4]);
                    rounds = Integer.parseInt(args[5]);
                    workout = Long.parseLong(args[6]);
                }catch (IndexOutOfBoundsException | NumberFormatException e){
                    logger.error(e.getClass().getSimpleName() + "    "+ e.getMessage());
                    logger.error(Constants.ARGS_ERROR);
                    return;
                }
                dataProvider.createExercise(name, weight, repetitions, rounds, workout);
                break;
            case Constants.CREATE_WORKOUT:
                Workout.WorkoutType type;
                long client;
                long trainer;
                try{
                    if(argToWorkoutType(args[2]).isEmpty()){
                        logger.error(Constants.TYPE_NOT_FOUND);
                        return;
                    }
                    type = argToWorkoutType(args[2]).get();
                    client = Long.parseLong(args[3]);
                    trainer = Long.parseLong(args[4]);
                }catch (IndexOutOfBoundsException | NumberFormatException e){
                    logger.error(e.getClass().getSimpleName() + "    "+ e.getMessage());
                    logger.error(Constants.ARGS_ERROR);
                    return;
                }
                dataProvider.createWorkout(type, client, trainer);
                break;
            case Constants.CHECK_CLIENT:
                long id;
                try{
                    id = Long.parseLong(args[2]);
                }catch (IndexOutOfBoundsException | NumberFormatException e){
                    logger.error(e.getClass().getSimpleName() + "    "+ e.getMessage());
                    logger.error(Constants.ARGS_ERROR);
                    return;
                }
                dataProvider.checkClient(id);
                break;
            case Constants.EXECUTE_WORKOUT:
                long workoutId;
                String isCompleted;
                try{
                    workoutId = Long.parseLong(args[2]);
                    isCompleted = args[3];
                }catch (IndexOutOfBoundsException | NumberFormatException e){
                    logger.error(e.getClass().getSimpleName() + "    "+ e.getMessage());
                    logger.error(Constants.ARGS_ERROR);
                    return;
                }
                dataProvider.executeWorkout(workoutId, isCompleted);
                break;
            default:
                logger.error(args[1] + Constants.METHOD_NOT_FOUND);
        }
    }

    /**
     * Select WorkoutType and process the error
     * @param arg supposed WorkoutType
     * @return selected WorkoutType or Optional.empty()
     */
    private static Optional<Workout.WorkoutType> argToWorkoutType(String arg){
        Optional<Workout.WorkoutType> result = Optional.empty() ;
        switch (arg){
            case Constants.TYPE_AEROBIC:
                result =  Optional.of(Workout.WorkoutType.AEROBIC);
                break;
            case Constants.TYPE_FLEXIBILITY:
                result =  Optional.of(Workout.WorkoutType.FLEXIBILITY);
                break;
            case Constants.TYPE_STRENGTH:
                result =  Optional.of(Workout.WorkoutType.STRENGTH);
                break;
            case Constants.TYPE_BALANCE:
                result =  Optional.of(Workout.WorkoutType.BALANCE);
                break;
            default:
        }
        return result;
    }

    /**
     * Select DataProvider and process the error
     * @param arg supposed Data Provider specific class
     * @return selected DataProvider class or Optional.empty()
     */
    private static Optional<DataProvider> selectDataProvider(String arg){
        Optional<DataProvider> result = Optional.empty();
        DataProvider dataProvider;
        switch (arg){
            case Constants.DP_CSV:
                dataProvider = new DataProviderCsv();
                insertDataToCsv();
                result = Optional.of(dataProvider);
                break;
            case Constants.DP_XML:
                dataProvider = new DataProviderXml();
                insertDataToXml();
                result = Optional.of(dataProvider);
                break;
            case Constants.DP_DB:
                dataProvider = new DataProviderDB();
                insertDataToDb();
                result = Optional.of(dataProvider);
                break;
        }
        return result;
    }


    /**
     * ID's provided here are simple because it simplifies interaction with objects through the CLI
     */

    private static final List<Trainer> testTrainer = List.of(
            new Trainer(1, "Alex", "Powerlifter", 5, 10),
            new Trainer(2, "Drake", "Natan", 3, 6),
            new Trainer(3, "William", "Defoe", 1, 9),
            new Trainer(4, "Mathew", "Mcconaughey", 8, 10)
    );
    private static final List<Client> clientList = List.of(
            new Client(1, "Hairy", "Schlong", 24, 78, 182, true),
            new Client(2, "Davy", "Farter", 20, 80, 190, false),
            new Client(3, "Macky", "Mclovin", 12, 60, 160),
            new Client(4, "Wendy", "Tour", 40, 80, 185)
    );

    private static final List<Feedback> feedbacks = List.of(
            new Feedback(1, Feedback.Estimate.COMMON)
    );

    private static final List<Workout> workouts = List.of(
            new Workout(1, Workout.WorkoutType.AEROBIC, clientList.get(0).getId(), testTrainer.get(0).getId(),
                    feedbacks.get(0).getId()),
            new Workout( 2, Workout.WorkoutType.STRENGTH, clientList.get(1).getId(), testTrainer.get(1).getId())
    );

    private static final List<Exercise> exercises = List.of(
            new Exercise("Exercise1", 15, 15, 3, workouts.get(0).getId()),
            new Exercise("Exercise2", 20, 10, 4, workouts.get(0).getId()),
            new Exercise("Exercise3", 100, 12, 5, workouts.get(1).getId()),
            new Exercise("Exercise4", 58, 10, 4, workouts.get(1).getId())
    );

    private static void insertDataToCsv(){
        DataProviderCsv dp = new DataProviderCsv();
        dp.saveRecords(testTrainer);
        dp.saveRecords(clientList);
        dp.saveRecords(exercises);
        dp.saveRecords(workouts);
        dp.saveRecords(feedbacks);
        logger.info(Constants.SPLIT);
    }

    private static void insertDataToXml(){
        DataProviderXml dp = new DataProviderXml();
        dp.saveRecords(testTrainer);
        dp.saveRecords(clientList);
        dp.saveRecords(exercises);
        dp.saveRecords(workouts);
        dp.saveRecords(feedbacks);
        logger.info(Constants.SPLIT);
    }

    private static void insertDataToDb(){
        DataProviderDB dp = new DataProviderDB();
        testTrainer.forEach(dp::insertTrainer);
        clientList.forEach(dp::insertClient);
        exercises.forEach(dp::insertExercise);
        workouts.forEach(dp::insertWorkout);
        feedbacks.forEach(dp::insertFeedback);
        logger.info(Constants.SPLIT);
    }

}
