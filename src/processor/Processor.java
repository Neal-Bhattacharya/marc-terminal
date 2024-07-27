package processor;

import reader.GTFSFileReader;
import ui.CommandLineUI;
import util.*;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Comparator.comparing;

public class Processor {
    private final GTFSFileReader gtReader;
    private Map<String, Set<DayOfWeek>> weeks;
    private Map<String, Set<Holiday>> holidays;
    private List<Route> routes;
    private Map<String, List<StopTime>> stopTimes;
    private Map<String, String> stopNames;
    private static final Calendar calendar = Calendar.getInstance();
    private LocalDateTime now = LocalDateTime.now();
    private LocalDate today = now.toLocalDate();
    private LocalTime timeNow = now.toLocalTime();

    private final Map<String, Trip> tripIdMap = new HashMap<>();
    private List<Trip> trips;

    private boolean todayIsHoliday = false;

    String primaryNorthStopNbId = "12002";
    String primaryNorthStopSbId = "11980";

    String primarySouthStopNbId = "11958";
    String primarySouthStopSbId = "11958";

    String northHeader = "BALTIMORE PENN STATION";
    String southHeader = "DC UNION STATION";

    String northEmoji = "\uD83E\uDD80";
    String southEmoji = "\uD83C\uDFDB";

    String line = "marc-penn";



    public Processor(String dataPath) throws IOException {
        this.gtReader = new GTFSFileReader(dataPath);
        checkEndDate();
        loadAll();
        loadOptions();

    }

    public Processor(String dataPath, String dateStr) throws IOException {
        loadDate(dateStr);
        this.gtReader = new GTFSFileReader(dataPath);
        checkEndDate();
        loadAll();
        loadOptions();
    }

    private void loadOptions() throws IOException {
        Map<String, String> map = gtReader.getOptions();
        if (map.isEmpty()) return;
        if (map.containsKey("emojis")) {
            if (!map.get("emojis").equals("on")) CommandLineUI.disableIcons();
            else {
                if (map.containsKey("northEmoji")) {
                    northEmoji = map.get("northEmoji");
                }
                if (map.containsKey("southEmoji")) {
                    southEmoji = map.get("southEmoji");
                }
            }
        }
        if (map.containsKey("northStopNbId") && map.containsKey("northStopSbId")) {
            primaryNorthStopNbId = map.get("northStopNbId");
            primaryNorthStopSbId = map.get("northStopSbId");

        }
        if (map.containsKey("southStopNbId") && map.containsKey("southStopSbId")) {
            primarySouthStopNbId = map.get("southStopNbId");
            primarySouthStopSbId = map.get("southStopSbId");
        }
        if (map.containsKey("northHeader")) {
            northHeader = map.get("northHeader");
        }

        else{
            northHeader = stopNames.get(primaryNorthStopNbId);
        }
        if (map.containsKey("southHeader")) {
            southHeader = map.get("southHeader");
        }
        else{
            southHeader = stopNames.get(primarySouthStopSbId);
        }

        if (map.containsKey("line")){
            line = map.get("line");
        }
    }

    private void loadDate(String dateStr) {
        dateStr = dateStr.strip().toLowerCase();
        if (dateStr.matches("[a-z]+")){
            loadWeekDay(dateStr);
        }
        else loadCalendarDate(dateStr);
    }

    private void loadCalendarDate(String dateStr) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("M/d/yyyy");
        try{
            LocalDate newNow = LocalDate.parse(dateStr + '/' + LocalDateTime.now().getYear(), df);

            this.now = newNow.atStartOfDay();
            this.today = this.now.toLocalDate();
            this.timeNow = this.now.toLocalTime();
        } catch (DateTimeParseException d) {
            System.err.println("Couldn't parse date. Enter tmr, weekday or mm/dd");
            System.exit(0);
        }

    }

    private void loadWeekDay(String date) {
        DayOfWeek day = stringToDayofWeek(date);
        if (day == null) {
            System.err.println("Couldn't parse date. Enter tmr, weekday or mm/dd");
            System.exit(0);
        }
        int diff = day.compareTo(today.getDayOfWeek());
        this.now = now.plusDays(diff).toLocalDate().atStartOfDay();
        this.today = now.toLocalDate();
        this.timeNow = now.toLocalTime();
    }

    private DayOfWeek stringToDayofWeek(String day){
        return switch (day){
            case "tmr", "tomorrow" -> DayOfWeek.of(today.getDayOfWeek().getValue()+1);
            case "monday" -> DayOfWeek.MONDAY;
            case "tuesday" -> DayOfWeek.TUESDAY;
            case "wednesday" -> DayOfWeek.WEDNESDAY;
            case "thursday" -> DayOfWeek.THURSDAY;
            case "friday" -> DayOfWeek.FRIDAY;
            case "saturday" -> DayOfWeek.SATURDAY;
            case "sunday" -> DayOfWeek.SUNDAY;

            default -> null;
        };
    }

    private void checkEndDate() throws IOException {
        String endDate = gtReader.getEndDate();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate end = LocalDate.parse(endDate, df);
        if  (now.isAfter(end.atStartOfDay())){
            System.out.println("""
                    DATA IS EXPIRED.
                    Download new marc train data from www.mta.maryland.gov/developer-resources
                    And unzip into src/data folder""");
            System.exit(0);
        }
    }

    private void loadAll() throws IOException {
        this.weeks = gtReader.getWeeks();
        this.holidays = gtReader.getHolidays();
        this.routes = gtReader.getRoutes();
        this.stopTimes = gtReader.getStopTimes();
        this.stopNames = gtReader.getStopNames();
        List<Trip> rawTrips = gtReader.getTrips();

        // Populate tripIdMap
        rawTrips.forEach(t->tripIdMap.put(t.getTripId(), t));

        populateTrips();
    }

    private void populateTrips(){

        this.weeks.forEach((key, value) ->
                tripIdMap.values().stream().filter(t -> t.getServiceId().equals(key))
                .forEach(t -> t.setActiveDays(value)));

        this.routes.forEach(r->
            tripIdMap.values().stream()
                    .filter(x-> x.getRouteId().equals(r.routeId()))
                    .forEach(x->x.setRoute(r)));

        populateStopTimes();

        this.tripIdMap.entrySet()
                .stream()
                .filter(x->stopTimes.containsKey(x.getKey()))
                .map(Map.Entry::getValue)
                .forEach(x->x.setStopTimes(stopTimes.get(x.getTripId())));

        this.holidays.forEach((serviceID, h) ->
                tripIdMap.values().stream().filter(t->t.getServiceId().equals(serviceID))
                        .forEach(t->t.setHolidays(h)));

        this.trips = tripIdMap.values().stream().toList();
        resetCalendar();

        for (Trip t : trips){
            if (t.determineHolidayStatus(today)) todayIsHoliday = true;
        }
    }

    private void resetCalendar(){
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private void populateStopTimes(){
        this.stopNames.forEach((stopId, stopName)->stopTimes.values().stream()
                .flatMap(
                        x->x.stream()
                                .filter(y->y.getStopId().equals(stopId)))
                .forEach(y->y.setStopName(stopName)));
    }

    /*
    public List<String> getTrainTimes(boolean direction){
        Predicate<Trip> northSouth;
        String originId;
        String destinationId;
        String routeId;

        if (direction) {
            northSouth = Trip::isNorthBound;
            originId = primarySouthStopNbId;
            destinationId = primaryNorthStopNbId;
        }
        else {
            northSouth = Trip::isSouthBound;
            originId = primaryNorthStopSbId;
            destinationId = primarySouthStopSbId;
        }

        return this.getTrips().stream()
                .filter(northSouth)
                .filter(this::trainRunsToday)
                .map(Trip::getStopTimes)
                .filter(st-> containsRequiredStops(st, originId, destinationId))
                .flatMap(Collection::stream)
                .filter(st -> st.getStopId().equals(originId))
                .filter(this::timeAfterNow)
                .sorted(comparing(StopTime::getDepartureTime))
                .map(StopTime::getDepartureStr).toList();
    } */

    public boolean timeBeforeNow(StopTime st){
        Calendar locCalendar = Calendar.getInstance();
        locCalendar.setTime(st.getDepartureTime());
        double now = timeNow.getHour() + ((double) timeNow.getMinute() / 60);
        return !(locCalendar.get(Calendar.HOUR_OF_DAY) + ((double) locCalendar.get(Calendar.MINUTE) / 60) > now);
    }

    public boolean trainRunsToday(Trip t){
        if (t.getHolidayStatus() < 0) return false;
        if (t.getHolidayStatus() > 1) return true;
        return t.getActiveDays().contains(now.getDayOfWeek());
    }

    public boolean containsRequiredStops(List<StopTime> lst, String originId, String destinationId){
        if (lst == null) return false;
        boolean origin = false, destination = false;
        for (StopTime st : lst){
            if (st.getStopId().equals(originId)) origin = true;
            else if (st.getStopId().equals(destinationId)) destination = true;
        }
        return origin && destination;
    }

    public boolean isTodayHoliday(){
        return todayIsHoliday;
    }

    public TreeMap<StopTime, StopTime> getOriginDestMaps(boolean direction){

        Predicate<Trip> northSouth;
        String originId;
        String destinationId;

        if (direction) {
            northSouth = Trip::isNorthBound;
            originId = primarySouthStopNbId;
            destinationId = primaryNorthStopNbId;
        }
        else {
            northSouth = Trip::isSouthBound;
            originId = primaryNorthStopSbId;
            destinationId = primarySouthStopSbId;
        }

        TreeMap<StopTime, StopTime> treeMap = new TreeMap<>(comparing(StopTime::getDepartureTime));

        this.trips.stream()
                .filter(northSouth)
                .filter(t -> t.getActiveDays().contains(now.getDayOfWeek()))
                .filter(t-> containsRequiredStops(t.getStopTimes(), originId, destinationId))
                .filter(this::trainRunsToday)
                .forEach(t->treeMap.put(t.getStopTimeById(originId), t.getStopTimeById(destinationId)));

        return treeMap;
    }

    public static String generateHourDiffString(StopTime origin, StopTime dest){
        Date originDate = origin.getDepartureTime();
        Date destDate = dest.getArrivalTime();
        calendar.setTime(originDate);
        int originTimeInMin = (calendar.get(Calendar.HOUR_OF_DAY)*60) + calendar.get(Calendar.MINUTE);
        calendar.setTime(destDate);
        int destTimeInMin = (calendar.get(Calendar.HOUR_OF_DAY)*60) + calendar.get(Calendar.MINUTE);

        int duration = destTimeInMin - originTimeInMin;
        if (duration <= 0) throw new IllegalArgumentException();
        LocalTime time = LocalTime.MIN.plus(Duration.ofMinutes(duration));
        StringBuilder resBldr = new StringBuilder();
        int hrs = time.getHour(), mins = time.getMinute();
        if (hrs > 0){
            resBldr.append(hrs).append('h');
        }
        if (mins > 0){
            resBldr.append(mins).append('m');
        }

        return new String(resBldr);
    }

    public String getTimesUrl(boolean direction) {
        //model https://www.mta.maryland.gov/schedule/marc-penn?schedule_date=07252024&direction=1&origin=11958&destination=12002
        StringBuilder urlBldr = new StringBuilder("https://www.mta.maryland.gov/schedule/").append(line).append("?schedule_date=");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyMMdd");
        //TEST_HOLIDAY
        //urlBldr.append("20241128").append('&');
        urlBldr.append(df.format(now)).append('&');
        urlBldr.append("direction=").append(direction ? '1' : '0').append('&');
        urlBldr.append("origin=").append(direction ? primarySouthStopNbId : primaryNorthStopSbId).append('&');
        urlBldr.append("destination=").append(direction ? primaryNorthStopNbId : primarySouthStopSbId);
        urlBldr.append("&show_all=yes");

        return new String(urlBldr);
    }

    public String getSouthEmoji() {
        return southEmoji;
    }

    public String getNorthEmoji() {
        return northEmoji;
    }

    public String getNorthHeader() {
        return northHeader;
    }

    public String getSouthHeader() {
        return southHeader;
    }

}
