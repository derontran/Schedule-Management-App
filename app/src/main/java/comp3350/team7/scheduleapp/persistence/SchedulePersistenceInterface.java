package comp3350.team7.scheduleapp.persistence;

/*
 * Created By Thai Tran on 17 March,2021
 *
 */

import java.util.Calendar;
import java.util.List;

import comp3350.team7.scheduleapp.objects.Event;

public interface SchedulePersistenceInterface {
    List<Event> getScheduleForUserOnDate(String username, Calendar date);
}