package comp3350.team7.scheduleapp.Application;

/*
 * Created By Thai Tran on 10 March,2021
 *
 */

import comp3350.team7.scheduleapp.persistence.UserPersistenceInterface;
import comp3350.team7.scheduleapp.persistence.hsqldb.UserPersistanceHSQLDB;

public class Services {

    private static UserPersistenceInterface userDatabase = null;
    private static EventPersistenceInterface eventDatabase = null;

    public static synchronized UserPersistenceInterface getUserPersistence(){

        if(userDatabase == null){
            userDatabase = new UserPersistanceHSQLDB(Main.getDBPathName());
        }

        return userDatabase;
    }
    /*
    public static synchronized EventPersistenceInterface getUserPersistence(){

        if(eventDatabase == null){
            eventDatabase = new eventPersistanceHSQLDB(Main.getDBPathName());
        }

        return eventDatabase;
    }
    */
}
