package comp3350.team7.scheduleapp.logic;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Calendar;
import java.util.List;

import comp3350.team7.scheduleapp.application.DbServiceProvider;
import comp3350.team7.scheduleapp.logic.comparators.AbstractComparator;
import comp3350.team7.scheduleapp.logic.comparators.EventStartAscendingComparator;
import comp3350.team7.scheduleapp.logic.exceptions.DbErrorException;
import comp3350.team7.scheduleapp.logic.exceptions.EventControllerException;
import comp3350.team7.scheduleapp.logic.exceptions.InvalidEventException;
import comp3350.team7.scheduleapp.objects.User;
import comp3350.team7.scheduleapp.persistence.EventPersistenceInterface;
import comp3350.team7.scheduleapp.objects.Event;

/*
 * Created By Thai Tran on 22 February,2021
 *
 */
public class EventController {
    private static final String TAG = "EventController";
    EventPersistenceInterface eventPersistence;
    AbstractComparator sortingStrategy;
    String userName;
    List<Event> events;

    public EventController(String userName) {
        eventPersistence = DbServiceProvider
                .getInstance()
                .getEventPersistence();

        this.userName = userName;
        // default way of sorting
        sortingStrategy = new EventStartAscendingComparator();
    }
    public EventController(EventPersistenceInterface eventPersistence, String username){
        this.eventPersistence = eventPersistence;
        sortingStrategy = new EventStartAscendingComparator();
        events= null;
    }


    // part of strategy pattern, inject AbstractComparator
    public void setSortStrategy(AbstractComparator newSortStrategy){
        this.sortingStrategy = newSortStrategy;
    }

    public Event CreateEvent(String userName,int id,  String eventName, String description, Calendar calStart){
        Event newEvent = new Event(userName,id, eventName,description,calStart);
        try {
            eventPersistence.addEvent(newEvent);
        }catch (DbErrorException error) {

        }
        return newEvent;
    }

    public Event CreateEvent(String eventName, String description, Calendar calStart, Calendar calEnd){
        Event newEvent = new Event(eventName,description,calStart,calEnd);
        eventPersistence.addEvent(newEvent);
        return newEvent;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Event> getEventList() throws EventControllerException {
        List<Event> eventList =null;
        try{
            eventList = eventPersistence.getEventList(this.userName);
            eventList.sort(sortingStrategy);
        }catch (DbErrorException e){
            Log.e(TAG,e.getMessage() + "\n Cause by "+ e.getCause());
            throw new EventControllerException("Attention user, Something went wrong");

        }

        return eventList;
    }

    // Someone use our api to create an invalid event, let them catch it
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Event> addEvent(Event e) throws EventControllerException {
        try {
            EventValidator.valid(e);
        }catch (InvalidEventException e) {

        }
        return eventPersistence.addEvent(e);


    }

        // return this for testing
        return getEventList();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Event> removeEvent(Event e) throws InvalidEventException{
        try {
            if(EventValidator.valid(e)){
                eventPersistence.removeEvent(e);
            }

        }catch(DbErrorException err){
            // need do more than just this
            Log.e("DbErrorException", err.getMessage());
            err.printStackTrace();
        }
        // return this for testing
        return getEventList();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Event> removeEvent(int position) throws DbErrorException{
        eventPersistence.removeEvent(position);
        // return this for testing
        return getEventList();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Event> updateEvent(Event old,Event fresh) throws InvalidEventException{
        try {
            if(EventValidator.valid(old)){
                eventPersistence.updateEvent(old,fresh);
            }

        }catch(DbErrorException err){
            // need do more than just this
            Log.e("DbErrorException", err.getMessage());
            err.printStackTrace();
        }
        // return this for testing
        return getEventList();
    }

}
