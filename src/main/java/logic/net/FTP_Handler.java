package logic.net;

import com.ed.filehandler.PlainHandler;
import logic.AccountManager;
import main.Main;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.awt.*;
import java.io.*;


/**
 * Created by Eike on 08.06.2017.
 */
public class FTP_Handler extends Component {

    private FTPClient ftp = null;
    private PlainHandler plainHandler = new PlainHandler();

    public FTP_Handler(String host, String user, String pwd) throws Exception {
        ftp = new FTPClient();

        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;
        ftp.connect(host);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Konnte keine Verbindung zum Server herstellen");
        }
        ftp.login(user, pwd);
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();
    }

    public void downloadFile(String remoteFile, String localFilePath) {
        System.out.println("download: " + remoteFile + "; save to: " + localFilePath);
        try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
            this.ftp.retrieveFile(remoteFile, fos);

            System.out.println(remoteFile + " erfolgreich geladen");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadFiles() {
        String folder = Main.parentPath + AccountManager.getActiveAccount().getName() + "/";
        if(plainHandler.fileExist(folder + "log.txt") && plainHandler.fileExist(folder + "stats.dat")) {
            File logFile = new File(folder + "log.txt");
            File statsFile = new File(folder + "stats.dat");

            String remotePath = "/imagesorter/remotedata/" + System.getProperty("user.name") + "/";

            try {
                ftp.changeWorkingDirectory("imagesorter/remotedata/");
                ftp.makeDirectory(System.getProperty("user.name"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String logPath = remotePath + "log.txt";
            String statsPath = remotePath + "stats.json";

            try {
                InputStream inputStream1 = new FileInputStream(logFile);
                InputStream inputStream2 = new FileInputStream(statsFile);
                System.out.println("uploading File");

                ftp.storeFile(logPath, inputStream1);
                boolean done = ftp.storeFile(statsPath, inputStream2);

                inputStream1.close();
                inputStream2.close();
                if(done) {
                    System.out.println("data uploaded");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("uploadfiles not exist");
        }
    }


    public void disconnect() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
            } catch (IOException e) {
                //Mache gar nichts
                System.out.println("Mache nichts");
            }
        }
    }
}
