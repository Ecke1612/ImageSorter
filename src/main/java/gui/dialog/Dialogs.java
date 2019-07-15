package gui.dialog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

    public List<File> fileChooser() {
        FileChooser fileChooser = new FileChooser();
        if(fileChooseLastPath != null) fileChooser.setInitialDirectory(fileChooseLastPath);
        fileChooser.setTitle("Open Resource File");
        List<File> files = fileChooser.showOpenMultipleDialog(Main.primaryStage);
        if(files.size() > 0) fileChooseLastPath = new File(files.get(0).getParent());
        return files;
    }

    public void showNewAccountWindow() {
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

        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.BASELINE_RIGHT);
        Button btn_abort = new Button("Abbrechen");
        btn_abort.setOnAction(event -> stage.close());

        Button btn_okay = new Button("Okay");
        btn_okay.setOnAction(event -> {
            if(!textFieldName.equals("")) {
                AccountManager.addNewAccount(new AccountObject(textFieldName.getText(), mainFolderPath));
                System.out.println("Account " + textFieldName.getText() + " wurder erstellt.");
                stage.close();
            }
        });

        bottomRow.getChildren().addAll(btn_abort, btn_okay);

        mainVBox.getChildren().addAll(label, row1, row2, bottomRow);
        marginBox.getChildren().add(mainVBox);
        marginBox.setMargin(mainVBox, new Insets(10));


        stage.showAndWait();
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
