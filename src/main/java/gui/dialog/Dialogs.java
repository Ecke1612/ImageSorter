package gui.dialog;

import gui.controller.ImageObjectController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.AccountManager;
import main.Main;
import objects.AccountObject;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class Dialogs {

    private String mainFolderPath = "";
    private File fileChooseLastPath;
    private boolean initAccountCorrectly = false;
    private boolean fileReplace = false;

    public List<File> fileChooser() {
        FileChooser fileChooser = new FileChooser();
        if(fileChooseLastPath != null) fileChooser.setInitialDirectory(fileChooseLastPath);
        fileChooser.setTitle("Open Resource File");
        List<File> files = fileChooser.showOpenMultipleDialog(Main.primaryStage);
        if(files.size() > 0) fileChooseLastPath = new File(files.get(0).getParent());
        return files;
    }

    public boolean showNewAccountWindow() {
        Stage stage = new Stage();
        stage.setTitle("Neuer Account");
        VBox marginBox = new VBox();
        VBox mainVBox = new VBox(10);
        Scene scene = new Scene(marginBox);
        stage.setScene(scene);
        stage.initOwner(Main.primaryStage);

        Label label = new Label("Anlegen eines neuen Accounts");
        label.setStyle("-fx-font-size: 14");

        HBox row1 = new HBox(10);
        Label labelName = new Label("Name: ");
        HBox.setHgrow(labelName, Priority.ALWAYS);
        TextField textFieldName = new TextField();
        row1.getChildren().addAll(labelName, textFieldName);

        HBox row2 = new HBox(10);
        Label labelPath = new Label("Speicherort: ");
        HBox.setHgrow(labelPath, Priority.ALWAYS);
        Button btn_path = new Button("wähle");
        btn_path.setOnAction(event -> {
            mainFolderPath = chooseDir();
        });

        row2.getChildren().addAll(labelPath, btn_path);

        HBox bottomRow = new HBox(5);
        bottomRow.setAlignment(Pos.BASELINE_RIGHT);
        Button btn_abort = new Button("Abbrechen");
        btn_abort.setOnAction(event -> {
            stage.close();
        });

        Button btn_okay = new Button("Okay");
        btn_okay.setOnAction(event -> {
            if(!textFieldName.equals("") && !mainFolderPath.equals("")) {
                AccountManager.addNewAccount(new AccountObject(textFieldName.getText(), mainFolderPath));
                System.out.println("Account " + textFieldName.getText() + " wurder erstellt.");
                initAccountCorrectly = true;
                stage.close();
            }
        });

        bottomRow.getChildren().addAll(btn_okay);

        mainVBox.getChildren().addAll(label, row1, row2, bottomRow);
        marginBox.getChildren().add(mainVBox);
        marginBox.setMargin(mainVBox, new Insets(10));

        stage.showAndWait();
        return initAccountCorrectly;
    }

    public static String chooseDir() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory =
                directoryChooser.showDialog(Main.primaryStage);

        if(selectedDirectory == null){
            System.out.println("No Directory selected");
            return "";
        }else{
            return (selectedDirectory.getAbsolutePath());
        }
    }

    public boolean fileAlreadyExistDialog(String path1, String path2) {
        Stage stage = new Stage();
        stage.setTitle("Datei existiert bereits");
        VBox marginBox = new VBox();
        VBox mainVBox = new VBox(10);
        marginBox.getChildren().add(mainVBox);
        Scene scene = new Scene(marginBox, 450,300);
        stage.setScene(scene);
        stage.initOwner(Main.primaryStage);

        Label text = new Label("Die Datei exisitiert bereits, wie soll vorgegangen werden?");

        File f1 = new File(path1);
        File f2 = new File(path2);


        VBox vBoxController1 = new VBox(5);
        Label label_name1 = new Label(f1.getName());
        Image image1 = new Image(f1.toURI().toString(), 175,125,true, false, true);
        ImageView imageView1 = new ImageView(image1);
        vBoxController1.getChildren().addAll(label_name1, imageView1);

        VBox vBoxController2 = new VBox(5);
        Label label_name2 = new Label(f2.getName());
        Image image2 = new Image(f2.toURI().toString(), 175,125,true, false, true);
        ImageView imageView2 = new ImageView(image2);
        vBoxController2.getChildren().addAll(label_name2, imageView2);

        HBox hboxImages = new HBox(10);
        hboxImages.getChildren().addAll(vBoxController1, vBoxController2);

        Button btn_ersetzen = new Button("Ersetzen");
        btn_ersetzen.setOnAction(event -> {
            fileReplace = true;
            stage.close();
        });

        Button btn_both = new Button("beide behalten");
        btn_both.setOnAction(event -> {
            fileReplace = false;
            stage.close();
        });

        HBox hbox_buttons = new HBox(5);
        hbox_buttons.setAlignment(Pos.CENTER_RIGHT);
        hbox_buttons.getChildren().addAll(btn_both, btn_ersetzen);

        mainVBox.getChildren().addAll(text, hboxImages, hbox_buttons);

        stage.showAndWait();
        return fileReplace;
    }

    public static boolean ConfirmDialog(String title, String header, String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        //alert.initOwner(Main_Application.primaryStage);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }

}
