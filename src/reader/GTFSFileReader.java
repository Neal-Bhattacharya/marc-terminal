package reader;

import util.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.util.*;

public class GTFSFileReader {

    String path;
    String dataPath;
    String fileSeparator = FileSystems.getDefault().getSeparator();

    public GTFSFileReader(String pathStr){
        path =  pathStr + fileSeparator + "files" + fileSeparator;
        dataPath = path + fileSeparator + Paths.get("data") + fileSeparator;
    }


    public Map<String, String> getOptions() throws IOException {
        Map<String, String> map = new HashMap<>();
        try {
            CSVReader reader = new CSVReader(path + "options.txt");
            String[] optionsArr = reader.readOptions();
            while (optionsArr != null) {
                if (optionsArr.length > 1) {
                    map.put(optionsArr[0], optionsArr[1]);
                }
                optionsArr = reader.readOptions();
            }
        }
        catch(Exception ignored){System.out.println("Ignored");}
        return map;

    }

    /**
     * Maps service_ids to days on which they're active
     */
    public Map<String, Set<DayOfWeek>> getWeeks() throws IOException {
        CSVReader reader = new CSVReader(dataPath + "calendar.txt");
        Map<String, Set<DayOfWeek>> map = new HashMap<>();
        DayOfWeek[] week =
                {null, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
                        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};

        // Ignore headers
        // could dynamically assign headers to support .txt structure changes
        // fine for now
        reader.readRow();
        String[] row;
        row = reader.readRow();
        while (row != null){
            String serviceId = row[0];
            HashSet<DayOfWeek> days = new HashSet<>();
            for (int i = 1; i < 8; i++){
                if (row[i].equals("1")) {
                    days.add(week[i]);
                }
            }

            map.put(serviceId, days);
            row = reader.readRow();
        }
        return map;
    }

    /**
     * Get end date
     */
    public String getEndDate() throws IOException {
        CSVReader reader = new CSVReader(dataPath + "calendar.txt");
        reader.readRow();
        String[] row = reader.readRow();
        return row[9];
    }

    /**
     * Returns a map linking each serviceId to a set of Holidays
     */
    public Map<String, Set<Holiday>> getHolidays() throws IOException {
        CSVReader reader = new CSVReader(dataPath + "calendar_dates.txt");
        Map<String, Set<Holiday>> map = new HashMap<>();

        reader.readRow();
        String[] row;
        row = reader.readRow();
        while (row != null){
            String serviceId = row[0];
            if (!map.containsKey(serviceId)) {
                map.put(serviceId, new HashSet<>());
            }
            map.get(serviceId).add(
                    new Holiday(row[1], Integer.parseInt(row[2])));
            row = reader.readRow();
        }
        return map;
    }

    /**
     * Returns a list of routes
     */
    public List<Route> getRoutes() throws IOException {
        CSVReader reader = new CSVReader(dataPath + "routes.txt");
        List<Route> list = new LinkedList<>();

        reader.readRow();
        String[] row;
        row = reader.readRow();
        while (row != null){
            list.add(new Route(row[0], row[2] + " "+  row[3]));
            row = reader.readRow();
        }
        return list;
    }

    /**
     * Returns a map of tripId(String)->List<StopSchedule>
     */
    public Map<String, List<StopTime>> getStopTimes() throws IOException {
        CSVReader reader = new CSVReader(dataPath + "stop_times.txt");
        Map<String, List<StopTime>> map = new HashMap<>();

        reader.readRow();
        String[] row;
        row = reader.readRow();
        while (row != null){
            String tripId = row[0];
            if (!map.containsKey(tripId)){
                map.put(tripId, new LinkedList<>());
            }
            map.get(tripId).add(new StopTime(row[3], row[1], row[2], Integer.parseInt(row[4])));
            row = reader.readRow();
        }
        return map;
    }

    /**
     * Returns a map of stopID->stopName
     */
    public Map<String, String> getStopNames() throws IOException {
        CSVReader reader = new CSVReader(dataPath + "stops.txt");
        Map<String, String> map = new HashMap<>();
        reader.readRow();
        String[] row;
        row = reader.readRow();
        while (row != null){
            map.put(row[0],row[2]);
            row = reader.readRow();
        }
        return map;
    }

    public List<Trip> getTrips() throws IOException{
        CSVReader reader = new CSVReader(dataPath + "trips.txt");
        List<Trip> list = new LinkedList<>();

        reader.readRow();
        String[] row;
        row = reader.readRow();
        while (row != null){
            boolean direction = row[5].charAt(0) == 49;
            list.add(new Trip(row[0], row[1], row[2], row[3], direction));
            row = reader.readRow();
        }
        return list;

    }
}
