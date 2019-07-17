package debugData;

import logic.AccountManager;
import main.Main;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class LogFile {

    public static ArrayList<String> logfiles = new ArrayList<>();

    public static void initLogFile() {
        LocalDateTime date = LocalDateTime.now();
        String header = date.toString() + " - System Name: " + System.getProperty("user.name") + " -  Account Name: " + AccountManager.getActiveAccount().getName() + " -  Version: " + Main.version;
        logfiles.add(header);
        logfiles.add("");
        System.out.println("log:");
        System.out.println(Arrays.toString(logfiles.toArray()));
    }

}
