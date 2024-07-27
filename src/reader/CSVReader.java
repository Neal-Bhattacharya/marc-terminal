package reader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CSVReader {
    static BufferedReader reader;
    public CSVReader(String filename) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(filename));
    }

    public String[] readRow() throws IOException {
        String line = reader.readLine();
        if (line == null) return null;
        return line.split(",");
    }

    public String[] readOptions() throws IOException {
        String line = reader.readLine();
        if (line == null) return null;
        return line.split(":");
    }
}
