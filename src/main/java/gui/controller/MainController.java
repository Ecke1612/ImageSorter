package gui.controller;

import file_handling.FileHandler;
import file_handling.StoreData;
import gui.dialog.Dialogs;
import javafx.beans.NamedArg;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import logic.AccountManager;
import logic.DataManager;
import logic.FileSorter;
import logic.tasks.SearchTask;
import objects.ImageObject;
import objects.SimpleTagObject;
import objects.TreeItemObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static main.Main.primaryStage;

public class MainController {

    public Label label_accountname;
    public VBox vboxTags;
    public VBox vboxSubTags;
    public ScrollPane scrollpane_image;
    public CheckBox check_monthly;
    public FlowPane flow_images;
    public CheckBox check_tags;
    public CheckBox check_subtags;
    public CheckBox checkbox_cut;
    public TreeView treeView;
    public Button btn_store;
    public TextField searchTag_1;
    public TextField searchTag_2;
    public TextField searchTag_3;
    public TextField searchTag_4;

    private DataManager dataManager;
    private FileSorter fileSorter;
    private ArrayList<ImageObjectController> imageObjectControllers = new ArrayList<>();
    private StoreData storeData;


    public MainController(DataManager dataManager, StoreData storeData) {
        this.dataManager = dataManager;
        this.storeData = storeData;
        fileSorter = new FileSorter(storeData);
        dataManager.setMainController(this);
    }

    public void initialize() {
        flow_images.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double deltaY = event.getDeltaY()*1; // *6 to make the scrolling a bit faster
                double width = scrollpane_image.getContent().getBoundsInLocal().getWidth();
                double vvalue = scrollpane_image.getVvalue();
                scrollpane_image.setVvalue(vvalue + -deltaY/width); // deltaY/width to make the scrolling equally fast regardless of the actual width of the component
            }
        });

        for(SimpleTagObject t : dataManager.getTagObjects()) {
            addTagLogic(false, true, t.getName(), t.getColor());
        }
        for(SimpleTagObject t: dataManager.getSubTagObjects()) {
            addTagLogic(true, true, t.getName(), t.getColor());
        }
        initTreeView();

        searchTag_1.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null) {
                    searching(newValue);
                }
            }
        });
        searchTag_2.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null) {
                    searching(newValue);
                }
            }
        });
        searchTag_3.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null) {
                    searching(newValue);
                }
            }
        });
        searchTag_4.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null) {
                    searching(newValue);
                }
            }
        });
    }



    public void initAccountLabel(String name) {
        label_accountname.setText("Willkommen " + name);
    }

    public void import_images() {
        dataManager.import_images_dialog();
        showImagesinGrid();
        refreshTreeView();
    }

    public void searching(String searchString) {
        SearchTask searchTask = new SearchTask(searchTag_1.getText(), searchTag_2.getText(), searchTag_3.getText(), searchTag_4.getText(), dataManager.getDisplayedImageObjects());
        searchTask.setSearchString1(searchString);
        searchTask.setList(dataManager.getAllImageObjects());
        try {

            searchTask.setOnRunning((succeesesEvent) -> {

            });

            searchTask.setOnSucceeded((succeededEvent) -> {
                dataManager.getDisplayedImageObjects().clear();
                dataManager.getDisplayedImageObjects().addAll((ArrayList<ImageObject>) searchTask.getValue());
                showImagesinGrid();
            });

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(searchTask);
            executorService.shutdown();

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void showImagesinGrid() {
        flow_images.getChildren().clear();
        imageObjectControllers.clear();
        if(dataManager.getDisplayedImageObjects().size() > 0) {
            if (dataManager.getDisplayedImageObjects().get(0).isFixed()) btn_store.setText("Verschieben");
            else btn_store.setText("Einsortieren");
        }

        for(ImageObject i : dataManager.getDisplayedImageObjects()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ImageObjectWindow.fxml"));
                ImageObjectController imageObjectController = new ImageObjectController(i);
                fxmlLoader.setController(imageObjectController);
                imageObjectControllers.add(imageObjectController);
                Parent root = fxmlLoader.load();

                ContextMenu optionsMenu = new ContextMenu();
                MenuItem opt_item1 = new MenuItem("Öffnen");
                opt_item1.setOnAction(event -> openWindow(i.getPath()));

                MenuItem opt_item2 = new MenuItem("Zeige im Explorer");
                opt_item2.setOnAction(event -> openWindow(i.getParentPath()));

                optionsMenu.getItems().addAll(opt_item1, opt_item2);


                ContextMenu popup = new ContextMenu();
                root.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        if (t.getButton() == MouseButton.SECONDARY) {
                            if(t.isControlDown()) {
                                optionsMenu.show(primaryStage, t.getScreenX(), t.getScreenY());
                            } else {
                                dropDownMenu(popup, imageObjectController);
                                popup.show(primaryStage, t.getScreenX(), t.getScreenY());
                            }
                        } else if (t.getButton() == MouseButton.PRIMARY) {
                            if(t.isShiftDown()) {
                                shiftSelecting(true, i);
                            } else if(t.isControlDown()) {
                                shiftSelecting(false, i);
                            }
                            if(imageObjectController.checkbox.isSelected()) imageObjectController.checkbox.setSelected(false);
                            else imageObjectController.checkbox.setSelected(true);
                        }
                    }
                });
                flow_images.getChildren().add(root);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void shiftSelecting(boolean isAdd, ImageObject i) {
        int startindex = dataManager.getDisplayedImageObjects().indexOf(i) - 1;
        if(isAdd) {
            while (startindex >= 0 && !imageObjectControllers.get(startindex).checkbox.isSelected()) {
                imageObjectControllers.get(startindex).checkbox.setSelected(isAdd);
                startindex--;
            }
        } else {
            while (startindex >= 0 && imageObjectControllers.get(startindex).checkbox.isSelected()) {
                imageObjectControllers.get(startindex).checkbox.setSelected(isAdd);
                startindex--;
            }
        }

    }

    private void dropDownOptionsMenu(ImageObjectController i) {


    }

    private void dropDownMenu(ContextMenu popup, ImageObjectController imageObjectController) {
        popup.getItems().clear();
        for(SimpleTagObject t : dataManager.getTagObjects()) {
            MenuItem item = new MenuItem(t.getName());
            popup.getItems().add(item);
            item.setOnAction(event -> {
                if (imageObjectController.getImageObject().getTagObjects().size() <= 5) {
                    taglogic(imageObjectController, t, false);
                }
                for (ImageObjectController i : imageObjectControllers) {
                    if (i.checkbox.isSelected()) {
                        if (i.getImageObject().getTagObjects().size() <= 5) {
                            taglogic(i, t, false);
                        }
                    }
                }
            });
        }

        popup.getItems().add(new SeparatorMenuItem());
        for(SimpleTagObject st : dataManager.getSubTagObjects()) {
            MenuItem item = new MenuItem(st.getName());
            popup.getItems().add(item);
            item.setOnAction(event -> {
                if (imageObjectController.getImageObject().getSubTagObjects().size() <= 5) {
                    taglogic(imageObjectController, st, true);
                }
                for (ImageObjectController i : imageObjectControllers) {
                    if (i.checkbox.isSelected()) {
                        if (i.getImageObject().getSubTagObjects().size() <= 5) {
                            taglogic(i, st, true);
                        }
                    }
                }
            });
        }
    }

    public void openWindow(String path) {
        File file = new File (path);
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void taglogic(ImageObjectController imageObjectController, SimpleTagObject t, boolean sub) {
        if(!sub) {
            if (checkForTagDuplicatesInList(imageObjectController.getImageObject().getTagObjects(), t)) {
                imageObjectController.getImageObject().getTagObjects().remove(t);
            } else {
                imageObjectController.getImageObject().getTagObjects().add(t);
            }
            imageObjectController.setTagOnGui(false);
        } else {
            if (checkForTagDuplicatesInList(imageObjectController.getImageObject().getSubTagObjects(), t)) {
                imageObjectController.getImageObject().getSubTagObjects().remove(t);
            } else {
                imageObjectController.getImageObject().getSubTagObjects().add(t);
            }
            imageObjectController.setTagOnGui(true);
        }
        imageObjectController.checkbox.setSelected(false);

    }

    private Boolean checkForTagDuplicatesInList(ArrayList<SimpleTagObject> list, SimpleTagObject t) {
        if(list.contains(t)) return true;
        else return false;
    }

    private void initTreeView() {
        File file = new File(dataManager.getRootPath());
        TreeItem<TreeItemObject> rootItem = new TreeItem<>(new TreeItemObject(AccountManager.getActiveAccount().getName() + "'s Bilder", dataManager.getRootPath(), countFiles(file.listFiles())));
        treeView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<TreeItem<TreeItemObject>>) (observable, old_val, new_val) -> {
              if(new_val != null && new_val != old_val) {
                  if(new_val.getValue().getName().equals("Temporär")) {
                      dataManager.reloadTempImages();
                      showImagesinGrid();
                  } else {
                      TreeItem<TreeItemObject> selectedItem = new_val;
                      dataManager.fillDisplayedImages(selectedItem.getValue().getPath(), true);
                      showImagesinGrid();
                  }
            }
        });

        rootItem.setExpanded(true);
        TreeItem<TreeItemObject> tempItem = new TreeItem<>(new TreeItemObject("Temporär", "", 0));
        rootItem.getChildren().add(tempItem);
        createSubTrees(rootItem, dataManager.getRootPath());

        treeView.setRoot(rootItem);

    }

    private void createSubTrees(TreeItem<TreeItemObject> treeItem, String path) {
        File file = new File(path);
        String[] fileNames = file.list();
        for(String s : fileNames) {
            String newPath = path + "\\" + s;
            File newFile = new File(newPath);
            if(newFile.isDirectory()) {
                TreeItem<TreeItemObject> item = new TreeItem<>(new TreeItemObject(s, newPath, countFiles(newFile.listFiles())));
                item.setExpanded(true);
                treeItem.getChildren().add(item);
                createSubTrees(item, newPath);
            }
        }
    }

    private int countFiles(File[] file) {
        int count = 0;
        for(File f : file) {
            if(f.isFile()) count++;
        }
        return count;
    }

    private void refreshTreeView() {
        treeView.getRoot().getChildren().clear();
        TreeItem<TreeItemObject> tempItem = new TreeItem<>(new TreeItemObject("Temporär", "", 0));
        treeView.getRoot().getChildren().add(tempItem);
        createSubTrees(treeView.getRoot(), dataManager.getRootPath());
    }

    public void close() {
        primaryStage.close();
        storeData.saveAllData();
    }

    public void show_about() {

    }

    public void show_accountManager(ActionEvent actionEvent) {

    }

    public void addTag() {
        addTagLogic(false, false,"", Color.WHITE);
    }

    public void addTagLogic(boolean sub, boolean guiOnly, String name, Color color) {
        TextField textField = new TextField();
        textField.setText(name);
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(color);

        if(!guiOnly) dataManager.addToTagList(sub, new SimpleTagObject(name, color), textField, colorPicker);

        if(!sub) vboxTags.getChildren().add(getTagRow(sub, vboxTags, dataManager.getTagObjects(), textField, colorPicker));
        else vboxSubTags.getChildren().add(getTagRow(sub, vboxSubTags, dataManager.getSubTagObjects(), textField, colorPicker));
    }

    public void add_subtag() {
        addTagLogic(true, false ,"", Color.WHITE);
    }

    private HBox getTagRow(boolean sub, VBox mainTagVBox, ArrayList<SimpleTagObject> tagList, TextField textField, ColorPicker colorPicker) {
        HBox hbox = new HBox(5);
        hbox.setPadding(new Insets(3));
        textField.setPrefWidth(100);
        hbox.setAlignment(Pos.CENTER);
        HBox.setHgrow(textField, Priority.ALWAYS);
        hbox.setStyle("-fx-border-radius: 5;");
        colorPicker.getStylesheets().add(getClass().getResource("/css/colorpicker.css").toExternalForm());

        textField.setStyle("-fx-background-color: transparent;" + "-fx-border-color:  white;" + "-fx-border-width: 0.7;" + "-fx-border-radius: 2;" +
                "-fx-font-family: Segoe UI;" + "-fx-font-size: 12;" + "-fx-text-fill: white;");

        Button btn_delete = new Button("\uE107");
        btn_delete.setStyle("-fx-font-family: 'Segoe MDL2 Assets';" + "-fx-text-fill: rgb(198, 34, 34);" + "-fx-background-color:  transparent");
        btn_delete.setOnAction(event ->{
            int index;
            index = mainTagVBox.getChildren().indexOf(hbox);

            for(ImageObjectController i : imageObjectControllers) {
                i.checkForDeletedTags(sub, tagList.get(index));
            }
            dataManager.deleteFromTagList(sub, index);
            mainTagVBox.getChildren().remove(index);
        });

        hbox.getChildren().addAll(colorPicker, textField, btn_delete);
        return hbox;
    }

    public void selectAll() {
        for(ImageObjectController i : imageObjectControllers) {
            i.checkbox.setSelected(true);
        }
    }


    public void storeImages(ActionEvent actionEvent) {
        boolean move;
        if(btn_store.getText().equals("Verschieben")) move = true;
        else move = false;
        fileSorter.sortAndSaveFiles(dataManager.getDisplayedImageObjects(), imageObjectControllers, check_monthly.isSelected(), check_tags.isSelected(), check_subtags.isSelected(), checkbox_cut.isSelected(), dataManager, move);
        //dataManager.import_all_image_data();
        showImagesinGrid();
        refreshTreeView();
    }

    public void invertselection() {
        for(ImageObjectController i : imageObjectControllers) {
            if(i.checkbox.isSelected()) i.checkbox.setSelected(false);
            else i.checkbox.setSelected(true);
        }
    }

    public void selectNone() {
        for(ImageObjectController i : imageObjectControllers) {
            i.checkbox.setSelected(false);
        }
    }

    public void deleteFile() {
        if(Dialogs.ConfirmDialog("Löschen", "Ausgewählte Dateien Löschen", "Sollen die ausgewählten Dateien wirklich gelöscht werden?")) {
            for(ImageObjectController i : imageObjectControllers) {
                if(i.checkbox.isSelected()) {
                    if(FileHandler.fileExist(i.getImageObject().getPath())) {
                        File file = new File(i.getImageObject().getPath());
                        file.delete();
                        System.out.println(i.getImageObject().getName() + " wurder gelöscht");
                    }
                    dataManager.getAllImageObjects().remove(i.getImageObject());
                    dataManager.getDisplayedImageObjects().remove(i.getImageObject());
                    dataManager.getTempImages().remove(i.getImageObject());
                }
            }
            showImagesinGrid();
        }
    }

    public void setCut() {
        if(checkbox_cut.isSelected()) {
            checkbox_cut.setTextFill(Color.INDIANRED);
            checkbox_cut.setFont(Font.font("Segoe UI", FontWeight.BOLD,12));
        } else {
            checkbox_cut.setTextFill(Color.WHITE);
            checkbox_cut.setFont(Font.font("Segoe UI", FontWeight.NORMAL,12));
        }
    }
}
