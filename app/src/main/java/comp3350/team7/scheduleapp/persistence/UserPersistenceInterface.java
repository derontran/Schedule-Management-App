package comp3350.team7.scheduleapp.persistence;

/*
 * Created By Thai Tran on 04 March,2021
 *
 */

import java.util.List;

import comp3350.team7.scheduleapp.objects.User;
import comp3350.team7.scheduleapp.logic.exceptions.DbErrorException;

public interface UserPersistenceInterface {
    
   List<User> getUserDB() throws DbErrorException;

   User addUser(User newUser) throws DbErrorException;

   User getUser(String username) throws DbErrorException;

   void deleteUser(User user) throws DbErrorException;

}
