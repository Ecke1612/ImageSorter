package main;

import com.ed.filehandler.PlainHandler;
import persistentData.debugData.LogFile;
import persistentData.file_handling.InitData;
import persistentData.file_handling.ReadWriteAppData;
import presentation.gui.controller.MainController;
import presentation.gui.dialog.Dialogs;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.AccountManager;
import logic.DataManager;

public class Main extends Application {

    public static final int version = 131;
    private static final String appName = "ImageSorter";
    public static final String parentPath = "bin/apps/" + appName + "/";

    public static Stage primaryStage;
    public static InitData initData;
    private DataManager dataManager = new DataManager();
    private ReadWriteAppData readWriteAppData;
    //private FileHandlerFACADE fileHandler = new FileHandlerFACADE();
    private PlainHandler plainHandler = new PlainHandler();


    @Override
    public void start(Stage primaryStage) throws Exception{
        readWriteAppData = new ReadWriteAppData(dataManager);

        Main.primaryStage = primaryStage;
        initFolderStrcture();

        initData();
        loadingData();
        LogFile.initLogFile();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        MainController mainController = new MainController(dataManager, readWriteAppData);
        fxmlLoader.setController(mainController);
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("ED Image Sorter - " + (float)version / 1000);
        Scene scene = new Scene(root, initData.getWidth(), initData.getHeight());

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.DELETE)) {
                    mainController.deleteFile();
                }
            }
        });

        primaryStage.setScene(scene);
        initKeyCombination(scene, mainController);

        primaryStage.show();

        mainController.initAccountLabel(AccountManager.getActiveAccount().getName());

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                readWriteAppData.saveAllData(false);
            }
        });
    }

    private void initKeyCombination(Scene scene, MainController mainController) {
        KeyCombination selectAll = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);
        Runnable rn = ()-> mainController.selectAll();
        scene.getAccelerators().put(selectAll, rn);

        KeyCombination invertSelection = new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN);
        Runnable ins = ()-> mainController.invertselection();
        scene.getAccelerators().put(invertSelection, ins);

        KeyCombination selectNone = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        Runnable sn = ()-> mainController.selectNone();
        scene.getAccelerators().put(selectNone, sn);

        KeyCombination selectTags = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN);
        Runnable st = ()-> mainController.tagSelection();
        scene.getAccelerators().put(selectTags, st);
    }

    private void initFolderStrcture() {
        if(!plainHandler.fileExist("bin")) plainHandler.createDir("bin");
        if(!plainHandler.fileExist("bin/apps")) plainHandler.createDir("bin/apps");
        if(!plainHandler.fileExist("bin/apps/" + appName)) plainHandler.createDir("bin/apps/" + appName);
    }

    private void initData() {
        if(!plainHandler.fileExist(parentPath + "init.dat")) {
            initData = new InitData();
            Dialogs dialogs = new Dialogs();
            if(dialogs.showNewAccountWindow()) {
                readWriteAppData.writeAccountData();

                String accountPatch = AccountManager.getActiveAccount().getPath() + "\\" + AccountManager.getActiveAccount().getName() + "'s Bilder";
                plainHandler.createDir("bin/apps/" + appName + "\\" + AccountManager.getActiveAccount().getName());

                if (!plainHandler.fileExist(accountPatch)) plainHandler.createDir(accountPatch);
            }
        } else {
            initData = readWriteAppData.loadInitData();
        }
    }

    private void loadingData() {
        if(plainHandler.fileExist(parentPath + "acc.dat")) {
            readWriteAppData.loadAccountData();
        }
        if(plainHandler.fileExist(parentPath + AccountManager.getActiveAccount().getName() + "\\tagdata.dat")) {
            readWriteAppData.loadTagData();
        }
        dataManager.setRootPath(AccountManager.getActiveAccount().getPath() + "\\" + AccountManager.getActiveAccount().getName() + "'s Bilder");
        dataManager.import_all_image_data();
        readWriteAppData.loadImageData("imgdata.dat", false);
        readWriteAppData.loadImageData("tempimgdata.dat", true);
        readWriteAppData.loadStats();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
