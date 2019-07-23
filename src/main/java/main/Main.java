package main;

import debugData.LogFile;
import file_handling.FileHandler;
import file_handling.InitData;
import file_handling.StoreData;
import gui.controller.MainController;
import gui.dialog.Dialogs;
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

    public static final int version = 122;
    private static final String appName = "ImageSorter";
    public static final String parentPath = "bin/apps/" + appName + "/";

    public static Stage primaryStage;
    public static InitData initData;
    private DataManager dataManager = new DataManager();
    private StoreData storeData;


    @Override
    public void start(Stage primaryStage) throws Exception{
        storeData = new StoreData(dataManager);

        Main.primaryStage = primaryStage;
        initFolderStrcture();

        initData();
        loadingData();
        LogFile.initLogFile();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        MainController mainController = new MainController(dataManager, storeData);
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
                storeData.saveAllData(true);
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
    }

    private void initFolderStrcture() {
        if(!FileHandler.fileExist("bin")) FileHandler.createDir("bin");
        if(!FileHandler.fileExist("bin/apps")) FileHandler.createDir("bin/apps");
        if(!FileHandler.fileExist("bin/apps/" + appName)) FileHandler.createDir("bin/apps/" + appName);
    }

    private void initData() {
        if(!FileHandler.fileExist(parentPath + "init.dat")) {
            initData = new InitData();
            Dialogs dialogs = new Dialogs();
            if(dialogs.showNewAccountWindow()) {
                storeData.writeAccountData();

                String accountPatch = AccountManager.getActiveAccount().getPath() + "\\" + AccountManager.getActiveAccount().getName() + "'s Bilder";
                FileHandler.createDir("bin/apps/" + appName + "\\" + AccountManager.getActiveAccount().getName());

                if (!FileHandler.fileExist(accountPatch)) FileHandler.createDir(accountPatch);
            }
        } else {
            initData = storeData.loadInitData();
        }
    }

    private void loadingData() {
        if(FileHandler.fileExist(parentPath + "acc.dat")) {
            storeData.loadAccountData();
        }
        if(FileHandler.fileExist(parentPath + AccountManager.getActiveAccount().getName() + "\\tagdata.dat")) {
            storeData.loadTagData();
        }
        dataManager.setRootPath(AccountManager.getActiveAccount().getPath() + "\\" + AccountManager.getActiveAccount().getName() + "'s Bilder");
        dataManager.import_all_image_data();
        storeData.loadImageData("imgdata.dat", false);
        storeData.loadImageData("tempimgdata.dat", true);
        storeData.loadStats();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
