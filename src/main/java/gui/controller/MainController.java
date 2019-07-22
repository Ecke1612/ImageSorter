package gui.controller;

import com.drew.tools.FileUtil;
import com.sun.java.swing.plaf.motif.MotifEditorPaneUI;
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
import org.apache.commons.io.FileUtils;

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
    private ArrayList<MediaObjectController> mediaObjectControllers = new ArrayList<>();
    private StoreData storeData;
    private String branch = "BRaaaanch";


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
        disposeAllMedia();
        flow_images.getChildren().clear();
        mediaObjectControllers.clear();
        if(dataManager.getDisplayedImageObjects().size() > 0) {
            if (dataManager.getDisplayedImageObjects().get(0).isFixed()) btn_store.setText("Verschieben");
            else btn_store.setText("Einsortieren");
        }

        for(ImageObject i : dataManager.getDisplayedImageObjects()) {
            try {
                FXMLLoader fxmlLoader = null;
                MediaObjectController mediaObjectController;
                if(!i.isMovie()) {
                    fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ImageObjectWindow.fxml"));
                    mediaObjectController = new ImageObjectController(i);
                }
                else {
                    fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MovieObjectWindow.fxml"));
                    mediaObjectController = new MovieObjectController(i);
                }

                fxmlLoader.setController(mediaObjectController);
                mediaObjectControllers.add(mediaObjectController);
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
                                dropDownMenu(popup, mediaObjectController);
                                popup.show(primaryStage, t.getScreenX(), t.getScreenY());
                            }
                        } else if (t.getButton() == MouseButton.PRIMARY) {
                            if(t.isShiftDown()) {
                                shiftSelecting(true, i);
                            } else if(t.isControlDown()) {
                                shiftSelecting(false, i);
                            }
                            if(mediaObjectController.checkbox.isSelected()) mediaObjectController.checkbox.setSelected(false);
                            else mediaObjectController.checkbox.setSelected(true);
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
            while (startindex >= 0 && !mediaObjectControllers.get(startindex).checkbox.isSelected()) {
                mediaObjectControllers.get(startindex).checkbox.setSelected(isAdd);
                startindex--;
            }
        } else {
            while (startindex >= 0 && mediaObjectControllers.get(startindex).checkbox.isSelected()) {
                mediaObjectControllers.get(startindex).checkbox.setSelected(isAdd);
                startindex--;
            }
        }

    }

    private void dropDownOptionsMenu(ImageObjectController i) {


    }

    private void dropDownMenu(ContextMenu popup, MediaObjectController mediaObjectController) {
        popup.getItems().clear();
        for(SimpleTagObject t : dataManager.getTagObjects()) {
            MenuItem item = new MenuItem(t.getName());
            popup.getItems().add(item);
            item.setOnAction(event -> {
                if (mediaObjectController.getImageObject().getTagObjects().size() <= 5) {
                    taglogic(mediaObjectController, t, false);
                }
                for (MediaObjectController i : mediaObjectControllers) {
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
                if (mediaObjectController.getImageObject().getSubTagObjects().size() <= 5) {
                    taglogic(mediaObjectController, st, true);
                }
                for (MediaObjectController i : mediaObjectControllers) {
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

    private void taglogic(MediaObjectController mediaObjectController, SimpleTagObject t, boolean sub) {
        if(!sub) {
            if (checkForTagDuplicatesInList(mediaObjectController.getImageObject().getTagObjects(), t)) {
                mediaObjectController.getImageObject().getTagObjects().remove(t);
            } else {
                mediaObjectController.getImageObject().getTagObjects().add(t);
            }
            mediaObjectController.setTagOnGui(false);
        } else {
            if (checkForTagDuplicatesInList(mediaObjectController.getImageObject().getSubTagObjects(), t)) {
                mediaObjectController.getImageObject().getSubTagObjects().remove(t);
            } else {
                mediaObjectController.getImageObject().getSubTagObjects().add(t);
            }
            mediaObjectController.setTagOnGui(true);
        }
        mediaObjectController.checkbox.setSelected(false);

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
        storeData.saveAllData(true);
    }

    public void show_about() {

    }

    public void show_accountManager(ActionEvent actionEvent) {
        for(MediaObjectController i : mediaObjectControllers) {
            try {
                MovieObjectController m = (MovieObjectController) i;
                m.resetMedia();
                showImagesinGrid();
            }catch (Exception e) {

            }
        }
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

            for(MediaObjectController i : mediaObjectControllers) {
                i.checkForDeletedTags(sub, tagList.get(index));
            }
            dataManager.deleteFromTagList(sub, index);
            mainTagVBox.getChildren().remove(index);
        });

        hbox.getChildren().addAll(colorPicker, textField, btn_delete);
        return hbox;
    }

    public void selectAll() {
        for(MediaObjectController i : mediaObjectControllers) {
            i.checkbox.setSelected(true);
        }
    }


    public void storeImages(ActionEvent actionEvent) {
        boolean move;
        if(btn_store.getText().equals("Verschieben")) move = true;
        else move = false;
        fileSorter.sortAndSaveFiles(dataManager.getDisplayedImageObjects(), mediaObjectControllers, check_monthly.isSelected(), check_tags.isSelected(), check_subtags.isSelected(), checkbox_cut.isSelected(), dataManager, move);
        //dataManager.import_all_image_data();
        showImagesinGrid();
        refreshTreeView();
        storeData.saveAllData(false);
    }

    public void invertselection() {
        for(MediaObjectController i : mediaObjectControllers) {
            if(i.checkbox.isSelected()) i.checkbox.setSelected(false);
            else i.checkbox.setSelected(true);
        }
    }

    public void selectNone() {
        for(MediaObjectController i : mediaObjectControllers) {
            i.checkbox.setSelected(false);
        }
    }

    public void deleteFile() {
        if(Dialogs.ConfirmDialog("Löschen", "Ausgewählte Dateien Löschen", "Sollen die ausgewählten Dateien wirklich gelöscht werden?")) {
            ArrayList<MediaObjectController> delList = new ArrayList<>();
            for(MediaObjectController i : mediaObjectControllers) {
                if(i.checkbox.isSelected()) {
                    if(i.getImageObject().isFixed()) {
                        dataManager.getAllImageObjects().remove(i.getImageObject());
                        dataManager.getDisplayedImageObjects().remove(i.getImageObject());
                        dataManager.getTempImages().remove(i.getImageObject());

                        if (FileHandler.fileExist(i.getImageObject().getPath())) {
                            File file = new File(i.getImageObject().getPath());
                            try {
                                if(i.getImageObject().isMovie()) {
                                    MovieObjectController m = (MovieObjectController) i;
                                    m.resetMedia();
                                }
                                FileUtils.forceDelete(file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            System.out.println(i.getImageObject().getName() + " wurde gelöscht");
                        }
                    }
                }
            }
            showImagesinGrid();
            initTreeView();
        }
    }

    public void disposeAllMedia() {
        for(MediaObjectController m : mediaObjectControllers) {
            if (m.getImageObject().isMovie()) {
                MovieObjectController movieCTR = (MovieObjectController) m;
                movieCTR.resetMedia();
            }
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
