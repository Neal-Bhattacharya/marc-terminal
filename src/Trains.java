import processor.Processor;
import ui.CommandLineUI;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Trains {

    static Path path = Paths.get("");

    public static void main(String[] args) throws IOException {
        processArgs(args);

    }

    private static void processArgs(String[] args) throws IOException {
        Set<String> argsSet = new HashSet<>(List.of(args));
        if (argsSet.contains("live")) {
            argsSet.remove("live");
            Processor p;
            if (argsSet.isEmpty()) {
                p = new Processor(path.toAbsolutePath().toString());
            }
            else{
                p = new Processor(path.toAbsolutePath().toString(), argsSet.iterator().next());

            }
            CommandLineUI.setProcessor(p);
            CommandLineUI.launchURLs();
            return;
        }
        if (argsSet.contains("--noicons")){
            CommandLineUI.disableIcons();
            argsSet.remove("--noicons");
        }
        if (argsSet.size() == 1){
            Processor p = new Processor(path.toAbsolutePath().toString(), argsSet.iterator().next());
            CommandLineUI.setProcessor(p);
            CommandLineUI.printDefault();
            return;
        }
        runDefault();


    }

    private static void runDefault() {
        try {
            Processor p = new Processor(path.toAbsolutePath().toString());
            CommandLineUI.setProcessor(p);
            CommandLineUI.printDefault();
        }
        catch(IOException e) {
            System.err.println(e);
        }
    }
}
