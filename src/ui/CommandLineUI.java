package ui;

import jdk.jshell.spi.ExecutionControl;
import processor.Processor;
import util.StopTime;

import java.awt.*;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.List;

import static java.util.Comparator.comparing;

public class CommandLineUI {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String SPACES = "";

    private static Processor processor = null;
    private static boolean icons = true;
    private CommandLineUI(){}

    public static void setProcessor(Processor processor) {
        CommandLineUI.processor = processor;
    }

    public static void printDefault(){
        printHeader(true);
        printOriginDestPair(true);
        //printTrainTimes(processor.getTrainTimes(true));
        printHeader(false);
        printOriginDestPair(false);
        //printTrainTimes(processor.getTrainTimes(false));
        if (processor.isTodayHoliday()){
            holidayMenu();
        }
    }

    private static void holidayMenu() {
        printHolidayAlert();
        if(Desktop.isDesktopSupported()){
            System.out.println("Open train times in your browser? [y/n]");
            Scanner scanner = new Scanner(System.in);
            System.out.print(">");
            String input = scanner.nextLine();;
            if (input.trim().equalsIgnoreCase("y")){
                launchURLs();
            }
        }
    }

    public static void launchURLs(){
        Desktop desktop = Desktop.getDesktop();
        String northTimeTable = processor.getTimesUrl(true);
        String southTimeTable = processor.getTimesUrl(false);

        try{
            desktop.browse(new URI(northTimeTable));
            desktop.browse(new URI(southTimeTable));
        } catch (Exception e){
            System.out.println("Could not launch browser. Go to\n" + northTimeTable + "\n" + southTimeTable);

        }
    }

    private static void printHolidayAlert(){
        StringBuilder text =  new StringBuilder();
        text.append(ANSI_YELLOW).append("ALERT:").append(ANSI_RESET).append(" It's a holiday. ")
                .append("Times may be wrong!");
        StringBuilder alert = new StringBuilder("-".repeat(42)).append('\n').append(text);
        System.out.println(new String(alert));
    }

    public static void printHeader(boolean direction){
        String icon;
        if (icons) icon = direction ? processor.getNorthEmoji() : processor.getSouthEmoji();
        else icon = "";
        String header = icon +  " TRAINS TO" + (direction ? pad(processor.getNorthHeader()) : pad(processor.getSouthHeader())) + icon;
        StringBuilder borderBld = new StringBuilder();
        borderBld.append("=".repeat(header.length()));
        String border = new String(borderBld);
        System.out.println(border+'\n'+header+'\n'+border);
    }

    private static String pad(String s) {
        return " " + s + " ";
    }

    /*
    public static void printTrainTimes(List<String> times) {
        if (times == null || times.isEmpty()){
            System.out.println("No more trains today.");
            return;
        };
        System.out.println("[" + ANSI_GREEN + SPACES + times.getFirst() + ANSI_RESET + "]");
        for (int i = 1; i < times.size(); i++) {
            System.out.println("-" + times.get(i));
        }*/

    public static void printOriginDestPair(boolean direction) {
        TreeMap<StopTime, StopTime> times = processor.getOriginDestMaps(direction);
        if (times == null || times.isEmpty()){
            System.out.println(ANSI_CYAN + "No more trains today." + ANSI_RESET);
            return;
        };
        Iterator<Map.Entry<StopTime, StopTime>> it = times.entrySet().iterator();
        Map.Entry<StopTime, StopTime> nextTrain = it.next();

        if (!processor.timeAfterNow(nextTrain.getKey()) && !it.hasNext()){
            System.out.println(ANSI_CYAN + "No more trains today." + ANSI_RESET);
            return;
        }

        while (!processor.timeAfterNow(nextTrain.getKey())){
            nextTrain = it.next();
            if (!processor.timeAfterNow(nextTrain.getKey()) && !it.hasNext()){
                System.out.println("No more trains today.");
                return;
            }
        }


        System.out.println("[" + ANSI_GREEN + generateOriginDestPairStr(nextTrain.getKey(), nextTrain.getValue())
        + ANSI_RESET + "]");

        while (it.hasNext()){
            nextTrain = it.next();
            System.out.println(" " + generateOriginDestPairStr(nextTrain.getKey(), nextTrain.getValue()));
        }
    }

    public static String generateOriginDestPairStr(StopTime origin, StopTime dest){
        StringBuilder timesBuilder = new StringBuilder(origin.getDepartureStr());
        String diffString = Processor.generateHourDiffString(origin, dest);
        timesBuilder.append("----").append(diffString).append("-".repeat(12 - (diffString.length() +4) ))
                .append(">").append(dest.getArrivalStr());
        return new String(timesBuilder);
    }

    public static void disableIcons() {
        icons = false;
    }
}
