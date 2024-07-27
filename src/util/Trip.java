package util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Trip{

    private final String tripId;
    private final String routeId;
    private final String serviceId;
    private final String headSign;
    private final boolean direction;
    private List<StopTime> stopTimes;
    private TreeSet<DayOfWeek> activeDays;

    private Set<Holiday> holidays;

    private Route route;

    /**
     * 0 = Normal service, no holiday
     * -1 = Route deleted by holiday
     * 1 = Route added by holiday
     */
    private int holidayStatus = 0;

    public Trip(String tripId, String routeId, String serviceId, String headSign, Boolean direction) {
            this.tripId = tripId;
            this.routeId = routeId;
            this.serviceId = serviceId;
            this.headSign = headSign;
            this.direction = direction;
    }

    /**
     * Mainly for debugging
     * @return String representation of a Trip
     */
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(tripId).append(": {");
        builder.append(" Route: ").append(this.routeId).append(" ");
        builder.append("DAYS: [");
        for (DayOfWeek day : this.activeDays){
            builder.append(day.name()).append(" ");
        }
        builder.append("] DIRECTION: [");
        if (direction) builder.append("Northbound towards ");
        else builder.append("Southbound towards ");
        builder.append(headSign);
        builder.append("] STOPS: [");

        if (this.stopTimes == null) builder.append("No stops.");
        else{
            for (StopTime s : this.stopTimes){
                builder.append(s.getStopId()).append("-").append(s.getStopName()).append(" ");
            }
        }
        builder.append("]}");
        return new String(builder);
    }

    public String getTripId() {
        return tripId;
    }

    public String getRouteId() {
        return routeId;
    }

    public StopTime getStopTimeById(String id){
        for (StopTime st : this.stopTimes){
            if (st.getStopId().equals(id)) return st;
        }
        return null;
    }


    public String getServiceId() {
        return serviceId;
    }

    public String getHeadSign() {
        return headSign;
    }

    public boolean isNorthBound() {
        return direction;
    }
    public boolean isSouthBound() {
        return !direction;
    }


    public Set<DayOfWeek> getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(Set<DayOfWeek> activeDays) {
        Comparator<DayOfWeek> comparator = Enum::compareTo;
        this.activeDays = new TreeSet<>(comparator);
        this.activeDays.addAll(activeDays);
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public List<StopTime> getStopTimes() {
        return stopTimes;
    }

    public void setStopTimes(List<StopTime> schedules) {
        this.stopTimes = schedules;
    }

    public Set<Holiday> getHolidays() {
        return holidays;
    }

    public void setHolidays(Set<Holiday> holidays) {
        this.holidays = holidays;
    }

    public int getHolidayStatus() {
        return holidayStatus;
    }

    public void setHolidayStatus(int holidayStatus) {
        this.holidayStatus = holidayStatus;
    }

    public boolean determineHolidayStatus(LocalDate today){

        if (this.holidays == null) return false;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");

        String todayStr = df.format(today);
        //TEST_HOLIDAY
        //String today = "20241128";
        for (Holiday h: this.holidays){
            if (todayStr.equals(h.dateStr())){
                if (h.type() == 2) this.holidayStatus = -1;
                if (h.type() == 1) this.holidayStatus = 1;
                return true;
            }
        }
        return false;
    }

}
