package presentation.gui.controller;

import com.ed.filehandler.PlainFileHandler;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import logic.SearchImages;
import logic.dataholder.SettingsObject;
import logic.filesorter.FileSorterFactory;
import logic.filesorter.FileSorterInterface;
import persistentData.file_handling.ReadWriteAppData;
import presentation.gui.controller.subcontrol.TagControl;
import presentation.gui.controller.subcontrol.TreeViewControl;
import presentation.gui.dialog.Dialogs;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import logic.DataManager;
import logic.dataholder.ImageObject;
import presentation.dataholder.SimpleTagObject;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

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
    public Label label_sortinfo;
    public Button btn_selectTags;

    private DataManager dataManager;
    private FileSorterFactory fileSorterfactory = new FileSorterFactory();
    private ArrayList<MediaObjectController> mediaObjectControllers = new ArrayList<>();
    private ReadWriteAppData readWriteAppData;
    private SearchImages searchImages;
    private TagControl tagControl = new TagControl();
    private TreeViewControl treeViewControl;
    private PlainFileHandler plainHandler = new PlainFileHandler();
    public boolean isTempState = false;
    private Dialogs dialogs = new Dialogs();
    private SettingsObject settingObject = new SettingsObject();
    public int selectionCount = 0;


    public MainController(DataManager dataManager, ReadWriteAppData readWriteAppData) {
        this.dataManager = dataManager;
        this.readWriteAppData = readWriteAppData;
        //dataManager.setMainController(this);
        dataManager.getDisplayedImageObjects().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                System.out.println("obs List");
                showImagesinGrid();
            }
        });
        searchImages = new SearchImages(dataManager);
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
        treeViewControl = new TreeViewControl(dataManager, treeView);
        treeViewControl.initTreeView(this);

        addTextfieldListener(searchTag_1);
        addTextfieldListener(searchTag_2);
        addTextfieldListener(searchTag_3);
        addTextfieldListener(searchTag_4);

        calculateLabelSortInfo();

        btn_selectTags.setTooltip(new Tooltip("Selektiert alle mit Tag versehenen Bilder"));
    }

    private void addTextfieldListener(TextField t) {
        t.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null) {
                    searching();
                }
            }
        });
    }


    public void initAccountLabel(String name) {
        label_accountname.setText("Willkommen " + name);
    }

    public void import_images() {
        List<File> files = dialogs.fileChooser();
        dataManager.import_images_dialog(files);
        treeViewControl.refreshTreeView();
        treeView.getSelectionModel().select(1);
    }

    private void searching() {
        searchImages.search(searchTag_1.getText(), searchTag_2.getText(), searchTag_3.getText(), searchTag_4.getText());
    }

    public void showImagesinGrid() {
        System.out.println("show images in grid");
        disposeAllMedia();
        flow_images.getChildren().clear();
        mediaObjectControllers.clear();
        selectionCount = 0;
        if(dataManager.getDisplayedImageObjects().size() > 0) {
            if (dataManager.getDisplayedImageObjects().get(0).isFixed()) {
                setCut();
            }
            else {
                btn_store.setText("Einsortieren");
            }
        }

        for(ImageObject i : dataManager.getDisplayedImageObjects()) {
            try {
                FXMLLoader fxmlLoader = null;
                MediaObjectController mediaObjectController;
                if(!i.isMovie()) {
                    fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ImageObjectWindow.fxml"));
                    mediaObjectController = new ImageObjectController(i, this);
                }
                else {
                    fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MovieObjectWindow.fxml"));
                    mediaObjectController = new MovieObjectController(i, this);
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

                mediaObjectController.checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if(newValue) {
                            selectionCount++;
                            calculateLabelSortInfo();
                        } else {
                            selectionCount--;
                            calculateLabelSortInfo();
                        }
                    }
                });

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
                            if(mediaObjectController.checkbox.isSelected()) {
                                mediaObjectController.checkbox.setSelected(false);
                            }
                            else {
                                mediaObjectController.checkbox.setSelected(true);
                            }
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

    private void dropDownMenu(ContextMenu popup, MediaObjectController mediaObjectController) {
        popup.getItems().clear();
        for(SimpleTagObject t : dataManager.getTagObjects()) {
            tagControl.setTagMenuItem(t, popup, mediaObjectController, mediaObjectControllers);
        }

        popup.getItems().add(new SeparatorMenuItem());
        for(SimpleTagObject st : dataManager.getSubTagObjects()) {
            tagControl.setSubTagMenuItem(st, popup, mediaObjectController, mediaObjectControllers);
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

    public void close() {
        primaryStage.close();
        readWriteAppData.saveAllData(true);
    }

    public void show_about() {

    }

    public void show_accountManager(ActionEvent actionEvent) {
        for(MediaObjectController i : mediaObjectControllers) {
            try {
                MovieObjectController m = (MovieObjectController) i;
                m.resetMedia();
                //showImagesinGrid();
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

        if(!sub) {
            vboxTags.getChildren().add(getTagRow(sub, vboxTags, dataManager.getTagObjects(), textField, colorPicker));
        }
        else vboxSubTags.getChildren().add(getTagRow(sub, vboxSubTags, dataManager.getSubTagObjects(), textField, colorPicker));
    }

    public void add_subtag() {
        addTagLogic(true, false ,"", Color.WHITE);
    }

    private HBox getTagRow(boolean sub, VBox mainTagVBox, ArrayList<SimpleTagObject> tagList, TextField textField, ColorPicker colorPicker) {
        HBox hbox = new HBox(4);
        hbox.setPadding(new Insets(3));
        textField.setPrefWidth(80);
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

        Button btn_selectTags = new Button("\uE0A2");
        btn_selectTags.setStyle("-fx-font-family: 'Segoe MDL2 Assets'; -fx-background-color:  transparent; -fx-font-size: 11;");
        btn_selectTags.setPadding(new Insets(2,0,2,2));
        btn_selectTags.setTextFill(Color.WHITE);
        btn_selectTags.setOnAction(event -> {
            for(MediaObjectController m : mediaObjectControllers) {
                for(SimpleTagObject t : m.getImageObject().getTagObjects()) {
                    if(textField.getText().equals(t.getName())) {
                        m.checkbox.setSelected(true);
                    }
                }
                for(SimpleTagObject t : m.getImageObject().getSubTagObjects()) {
                    if(textField.getText().equals(t.getName())) {
                        m.checkbox.setSelected(true);
                    }
                }
            }
        });

        hbox.getChildren().addAll(btn_selectTags, colorPicker, textField, btn_delete);
        return hbox;
    }

    public void selectAll() {
        for(MediaObjectController i : mediaObjectControllers) {
            i.checkbox.setSelected(true);
        }
    }


    public void storeImages(ActionEvent actionEvent) {
        FileSorterInterface fileSorter = fileSorterfactory.getFileSorter(isTempState, checkbox_cut.isSelected());
        fileSorter.sortAndSaveFiles(this, dataManager);
        treeViewControl.refreshTreeView();
        readWriteAppData.saveAllData(false);
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

    public void tagSelection() {
        for(MediaObjectController i : mediaObjectControllers) {
            if(i.getImageObject().getTagObjects().size() > 0 || i.getImageObject().getSubTagObjects().size() > 0) i.checkbox.setSelected(true);
            else i.checkbox.setSelected(false);
        }
    }

    public void deleteFile() {
        if(dialogs.ConfirmDialog("Löschen", "Ausgewählte Dateien Löschen", "Sollen die ausgewählten Dateien wirklich gelöscht werden?")) {
            ArrayList<MediaObjectController> delList = new ArrayList<>();
            for(MediaObjectController i : mediaObjectControllers) {
                if(i.checkbox.isSelected()) {
                    delList.add(i);
                    if(i.getImageObject().isFixed()) {
                        if (plainHandler.fileExist(i.getImageObject().getPath())) {
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
            for(MediaObjectController mi : delList) {
                dataManager.getAllImageObjectsMap().remove(mi.getImageObject().getPath());
                dataManager.getDisplayedImageObjects().remove(mi.getImageObject());
                dataManager.getTempImages().remove(mi.getImageObject().getPath());
            }
            treeViewControl.refreshTreeView();
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
            if(!isTempState) btn_store.setText("Verschieben");
            calculateLabelSortInfo();
        } else {
            checkbox_cut.setTextFill(Color.WHITE);
            checkbox_cut.setFont(Font.font("Segoe UI", FontWeight.NORMAL,12));
            if(!isTempState) btn_store.setText("Kopieren");
            calculateLabelSortInfo();
        }
    }

    private int countImagesWhichWillBeSorted() {
        int sortImageCount = 0;
        for(MediaObjectController m : mediaObjectControllers) {
            if(m.checkbox.isSelected()) sortImageCount++;
        }
        return sortImageCount;
    }

    public void calculateLabelSortInfo() {
        if(selectionCount == 0) {
            btn_store.setDisable(true);
        } else btn_store.setDisable(false);
        //int sortCount = countImagesWhichWillBeSorted();
        String tagBaustein = " nach Tags ";
        String subTagBaustein = " und nach SubTags ";
        //String imageBaustein = " ";
        String monthtlyBaustein = ", sowie nach Monaten ";
        //String imageBaustein = " einsortiert";
        String einsortiertBaustein = " einsortiert.\n";
        String cutBaustein = "Die Qelldateien werden gelöscht.";

        String info = selectionCount + " Bilder werden ";
        if(check_tags.isSelected()) info = info + tagBaustein;
        if(check_subtags.isSelected()) info = info + subTagBaustein;
        //info = info + imageBaustein;
        if(check_monthly.isSelected()) info = info + monthtlyBaustein;
        info = info + einsortiertBaustein;
        if(checkbox_cut.isSelected()) info = info + cutBaustein;
        label_sortinfo.setText(info);
    }

    public void refreshTreeView() {
        treeViewControl.refreshTreeView();
    }

    private void setUpSettings() {
        settingObject.setCut(checkbox_cut.isSelected());
        settingObject.setMonthly(check_monthly.isSelected());
        settingObject.setSubTag(check_subtags.isSelected());
        settingObject.setTag(check_tags.isSelected());
    }

    public ArrayList<MediaObjectController> getMediaObjectControllers() {
        return mediaObjectControllers;
    }

}
