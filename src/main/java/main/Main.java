package main;

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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import logic.AccountManager;
import logic.DataManager;

import java.io.File;

public class Main extends Application {

    private static final int version = 100;
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

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        MainController mainController = new MainController(dataManager, storeData);
        fxmlLoader.setController(mainController);
        Parent root = fxmlLoader.load();

        primaryStage.setTitle("ED Image Sorter");
        primaryStage.setScene(new Scene(root));

        primaryStage.show();

        mainController.initAccountLabel(AccountManager.getActiveAccount().getName());

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                storeData.saveAllData();
            }
        });
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
            dialogs.showNewAccountWindow();
            storeData.writeInitData();
            storeData.writeAccountData();
            String accountPatch = AccountManager.getActiveAccount().getPath() + "\\" + AccountManager.getActiveAccount().getName() + "'s Bilder";
            if(!FileHandler.fileExist(accountPatch)) FileHandler.createDir(accountPatch);
        } else {
            initData = storeData.loadInitData();
        }
    }

    private void loadingData() {
        if(FileHandler.fileExist(parentPath + "tagdata.dat")) {
            storeData.loadTagData();
        }
        if(FileHandler.fileExist(parentPath + "acc.dat")) {
            storeData.loadAccountData();
        }
        dataManager.setRootPath(AccountManager.getActiveAccount().getPath() + "\\" + AccountManager.getActiveAccount().getName() + "'s Bilder");
        dataManager.import_all_image_data();
        storeData.loadImageData();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
