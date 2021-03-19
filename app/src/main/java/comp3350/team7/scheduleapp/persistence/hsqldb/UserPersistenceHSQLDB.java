package comp3350.team7.scheduleapp.persistence.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import comp3350.team7.scheduleapp.objects.User;
import comp3350.team7.scheduleapp.persistence.UserPersistenceInterface;
import comp3350.team7.scheduleapp.logic.exceptions.UserDBException;
public class UserPersistenceHSQLDB implements UserPersistenceInterface {

    private final String dbPath;

    public UserPersistenceHSQLDB(final String dbPath){
        this.dbPath = dbPath;
    }


    public Connection connection() throws  SQLException{
        return DriverManager.getConnection("jdbc:hsqldb:file:" + dbPath + ";shutdown=true", "SA", "");
    }

    private User fromResultSet(final ResultSet rs) throws SQLException {
        final String username = rs.getString("userID");
        final String password = rs.getString("password");
        final String firstName = rs.getString("firstName");
        final String lastName = rs.getString("lastName");
        return new User(firstName,lastName,username,password);
    }


    @Override
    public List<User> getUserDB() throws UserDBException {
        final List<User> users = new ArrayList<>();

        try(final Connection c =  connection()) {
            final Statement msg = c.createStatement();
            final ResultSet rs =  msg.executeQuery("SELECT * FROM USERS");
            while(rs.next()) {
                final User user = fromResultSet(rs);
                users.add(user);
            }
            rs.close();
            msg.close();

            return users;
        }catch (final SQLException e){
            throw new UserDBException("Fail to get users from database", e);
        }
    }

    @Override
    public User getUser(String username) throws UserDBException {
        User userExists =null;

        try(final Connection c = connection()) {
            final PreparedStatement msg = c.prepareStatement("SELECT * FROM USERS WHERE USERID = ?");
            msg.setString(1, username);

            final ResultSet rs = msg.executeQuery();
            if(rs.next())
                userExists = fromResultSet(rs);

            rs.close();
            msg.close();

            return userExists;
        }catch (final SQLException e){
            throw new UserDBException("Fail to get user: " + username + "from database",e);
        }
    }

    @Override
    public User addUser(User newUser) throws UserDBException {
        try(final Connection c = connection()) {
            final PreparedStatement msg = c.prepareStatement("INSERT INTO USERS VALUES(?,?,?,?)");
            msg.setString(1, newUser.getUserId());
            msg.setString(2, newUser.getPassword());
            msg.setString(3, newUser.getFirstName());
            msg.setString(4, newUser.getLastName());

            msg.executeUpdate();

            return newUser;
        }catch (final SQLException e){
            throw new UserDBException("Username is already taken.",e);
        }
    }

    @Override
    public void deleteUser(User user) throws UserDBException {
        try(final Connection c = connection()){
            final PreparedStatement msg = c.prepareStatement("DELETE FROM USER WHERE USERID = ?");
            msg.setString(1, user.getUserId());
            msg.executeUpdate();

        }catch (final SQLException e){
            throw new UserDBException(user.toString() + "doesn't exits in database",e);
        }
    }
}
