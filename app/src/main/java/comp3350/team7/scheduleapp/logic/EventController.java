package comp3350.team7.scheduleapp.logic;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import comp3350.team7.scheduleapp.application.DbClient;
import comp3350.team7.scheduleapp.logic.comparators.AbstractComparator;
import comp3350.team7.scheduleapp.logic.comparators.EventStartAscendingComparator;
import comp3350.team7.scheduleapp.logic.exceptions.DbErrorException;
import comp3350.team7.scheduleapp.logic.exceptions.EventControllerException;
import comp3350.team7.scheduleapp.logic.exceptions.InvalidEventException;
import comp3350.team7.scheduleapp.objects.Event;
import comp3350.team7.scheduleapp.persistence.EventPersistenceInterface;

/*
 * Created By Thai Tran on 22 February,2021
 *
 */
public class EventController {
    private static final String TAG = "EventController";
    EventPersistenceInterface eventPersistence;
    AbstractComparator sortingStrategy;

    public EventController() {
        eventPersistence = DbClient
                .getInstance()
                .getEventPersistence();
        // default way of sorting
        sortingStrategy = new EventStartAscendingComparator();
    }

    /* dependency inject */
    public EventController(EventPersistenceInterface eventPersistence) {
        this.eventPersistence = eventPersistence;
        sortingStrategy = new EventStartAscendingComparator();
        // default way of sorting
        sortingStrategy = new EventStartAscendingComparator();
    }


    // part of strategy pattern, inject AbstractComparator
    public void setSortStrategy(AbstractComparator newSortStrategy) {
        this.sortingStrategy = newSortStrategy;
    }

    public Event buildEvent(String userName, String eventName, String description, Calendar calStart) throws EventControllerException {
        Event newEvent = new Event(userName, eventName, description, calStart);
        try {
            EventValidator.validate(newEvent);
            return newEvent;
        } catch (InvalidEventException err) {
            Log.d(TAG, err.getMessage() + "Cause by" + err.getCause());
            throw new EventControllerException("Something went wrong, contact admin if needed\n" + err.getMessage());
        }
    }



    public Event buildEvent(String userName, String eventName, String description, Calendar calStart, Calendar calEnd) throws EventControllerException {
        Event newEvent = new Event(userName, eventName, description, calStart, calEnd);
        try {
            EventValidator.validate(newEvent);
            return newEvent;
        } catch (InvalidEventException err) {
            Log.d(TAG, err.getMessage() + "Cause by" + err.getCause());
            throw new EventControllerException("Something went wrong, contact admin if needed\n" + err.getMessage());
        }
    }

    public Event buildEvent(String userName, String eventName, String description, Calendar calStart, Calendar end, Calendar alarm) throws EventControllerException {
        Event newEvent = new Event(userName, eventName, description, calStart, end, alarm);
        try {
            EventValidator.validate(newEvent);
            return newEvent;
        } catch (InvalidEventException err) {
            Log.d(TAG, err.getMessage() + "Cause by" + err.getCause());
            throw new EventControllerException("Something went wrong, contact admin if needed\n" + err.getMessage());
        }
    }

  public Event getEvent(String userId, int eventId) throws EventControllerException {

      Event event = null ;
      try {
          event = eventPersistence.getEvent(userId,eventId);
      } catch (DbErrorException e) {
          Log.d(TAG, e.getMessage() + "\n Cause by " + e.getCause());
          throw new EventControllerException("Something went wrong, contact admin if needed\n" + e.getMessage());
      }

      return event;
  }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Event> getEventList(String userid) throws EventControllerException {
        List<Event> eventList = new ArrayList<>();
        try {
            eventList = eventPersistence.getEventList(userid);
            eventList.sort(sortingStrategy);
        } catch (DbErrorException e) {
            Log.d(TAG, e.getMessage() + "\n Cause by " + e.getCause());
            throw new EventControllerException("Something went wrong, contact admin if needed\n" + e.getMessage());
        }

        return eventList;
    }

    public int getEventListLength(String userId) throws EventControllerException {
        int res = -1;
        try {
            res = eventPersistence.getEventListLength(userId);
        } catch (DbErrorException e) {
            e.printStackTrace();
        }
        return res;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Event> getScheduleForUserOnDate(String username, Calendar date) throws EventControllerException {

        List<Event> eventList = null;
        try {
            eventList = eventPersistence.getScheduleForUserOnDate(username, date);
            if (eventList != null)
                eventList.sort(sortingStrategy);
        } catch (DbErrorException e) {
            Log.d(TAG, e.getMessage() + "\n Cause by " + e.getCause());
            throw new EventControllerException("Something went wrong, contact admin if needed\n" + e.getMessage());
        }

        return eventList;
    }

    // Someone use our api to create an invalid event, let them catch it
    public void addEvent(Event e) throws EventControllerException {
        try {
            EventValidator.validate(e);
            eventPersistence.addEvent(e);
        } catch (InvalidEventException error) {
            Log.d(TAG, error.getMessage() + " ,Cause by : " + error.getCause());
            throw new EventControllerException("Something went wrong, contact admin if needed\n" + error.getMessage());
        } catch (DbErrorException dbError) {
            Log.d(TAG, dbError.getMessage() + " ,Cause by : " + dbError.getCause());
            throw new EventControllerException("Something went wrong, contact admin if needed\n" + dbError.getMessage() + dbError.getCause());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeEvent(Event e) throws EventControllerException {
        try {
            //EventValidator.validate(e);
            eventPersistence.removeEvent(e);
//        } catch (InvalidEventException err) {
//            Log.d(TAG, err.getMessage() + " ,Cause by : " + err.getCause());
//            throw new EventControllerException("Something went wrong, contact admin if needed\n" + err.getMessage());
        } catch (DbErrorException dbError) {
            Log.d(TAG, dbError.getMessage() + " ,Cause by : " + dbError.getCause());
            throw new EventControllerException("Something went wrong, contact admin if needed\n" + dbError.getMessage());
        }

    }

    public void removeEvent(String username, int eventId) throws EventControllerException {
        try {
            eventPersistence.removeEvent(username, eventId);
        } catch (DbErrorException dbError) {
            Log.d(TAG, dbError.getMessage() + " ,Cause by : " + dbError.getCause());
            throw new EventControllerException("Something went wrong, contact admin if needed\n" + dbError.getMessage());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public Event updateEvent(Event old, Event fresh) throws EventControllerException {
        try {
            fresh.setID(old.getID());
            fresh.setUserName(old.getUserName());
            EventValidator.validate(fresh);
            eventPersistence.updateEvent(fresh);
        } catch (InvalidEventException err) {
            Log.d(TAG, err.getMessage() + " ,Cause by : " + err.getCause());
            throw new EventControllerException("Something went wrong, contact admin if needed\n" + err.getMessage());
        } catch (DbErrorException dbError) {
            Log.d(TAG, dbError.getMessage() + " ,Cause by : " + dbError.getCause());
            throw new EventControllerException("Something went wrong, contact admin if needed\n" + dbError.getMessage());
        }
        return old;
    }

}
