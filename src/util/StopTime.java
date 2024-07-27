package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StopTime {

    private final String stopId;
    private String arrivalStr;
    private String departureStr;

    private String stopName;
    private Date arrivalTime = null;
    private Date departureTime = null;
    private Integer sequenceNum;

    public StopTime(String stopId, String arrival, String departure, Integer sequenceNum) {
        this.stopId = stopId;
        this.arrivalStr = arrival;
        this.departureStr = departure;
        this.sequenceNum = sequenceNum;

        DateFormat parser = new SimpleDateFormat("HH:mm");
        DateFormat formatter = new SimpleDateFormat("hh:mm a");

        try {
            this.arrivalTime = parser.parse(arrivalStr);
            this.departureTime = parser.parse(departureStr);
            this.arrivalStr = formatter.format(arrivalTime);
            this.departureStr = formatter.format(departureTime);

        } catch (ParseException p) {
            System.out.println("Could not parse date for " + stopId);
        }

    }



    public String getStopId() {
        return stopId;
    }

    public String getArrivalStr() {
        return arrivalStr;
    }

    public String getDepartureStr() {
        return departureStr;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    @Override
    public String toString(){
        return this.stopName + " " + this.departureStr;
    }

    public Date getArrivalTime() { return this.arrivalTime;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

}
