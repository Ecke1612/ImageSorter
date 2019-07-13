package gui.controller;

import file_handling.FileHandler;
import gui.dialog.Dialogs;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import logic.AccountManager;
import logic.DataManager;
import logic.FileSorter;
import objects.ImageObject;
import objects.SimpleTagObject;
import objects.TagObject;
import objects.TreeItemObject;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.util.ArrayList;

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
    public TreeView treeView;
    public TextField searchTag_1;
    public TextField searchTag_2;
    public TextField searchTag_3;
    public TextField searchTag_4;

    private DataManager dataManager;
    private FileSorter fileSorter = new FileSorter();
    private ArrayList<ImageObjectController> imageObjectControllers = new ArrayList<>();


    public MainController(DataManager dataManager) {
        this.dataManager = dataManager;
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



        for(TagObject t : dataManager.getTagObjects()) {
            //vboxTags.getChildren().add(getTagRow(false, t.getName(), t.getColor(), vboxTags.getChildren().size()));
        }
        for(TagObject t: dataManager.getSubTagObjects()) {
            //vboxSubTags.getChildren().add(getTagRow(false, t.getName(), t.getColor(), vboxSubTags.getChildren().size()));
        }
        initTreeView();
    }

    public void initAccountLabel(String name) {
        label_accountname.setText("Willkommen " + name);
    }

    public void import_images() {
        int filesize = dataManager.import_images_dialog();
        if(filesize > 0) {
            showImagesinGrid(filesize, false);
        }
        initTreeView();
    }

    private void import_images_byPath(String path) {
        int filesize = dataManager.import_images_byPath(path);
        if(filesize >= 0) {
            showImagesinGrid(filesize, true);
        }
    }

    private void showImagesinGrid(int lenght, boolean reinit) {
        if(reinit) flow_images.getChildren().clear();
        imageObjectControllers.clear();
        System.out.println("länge: " + lenght);

        for(int i = 0; i < lenght; i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ImageObjectWindow.fxml"));
                ImageObjectController imageObjectController = new ImageObjectController(dataManager.getImageObjects().get((dataManager.getImageObjects().size() - lenght) + i), dataManager);
                fxmlLoader.setController(imageObjectController);
                imageObjectControllers.add(imageObjectController);
                Parent root = fxmlLoader.load();

                ContextMenu popup = new ContextMenu();
                root.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        if (t.getButton() == MouseButton.SECONDARY) {
                            dropDownMenu(popup, imageObjectController);
                            popup.show(primaryStage, t.getScreenX(), t.getScreenY());
                        } else if (t.getButton() == MouseButton.PRIMARY) {
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

    private void dropDownMenu(ContextMenu popup, ImageObjectController imageObjectController) {
        popup.getItems().clear();
        for(TagObject t : dataManager.getTagObjects()) {
            MenuItem item = new MenuItem(t.getName());
            popup.getItems().add(item);
            item.setOnAction(event -> {
                if (imageObjectController.getImageObject().getTagNameObjects().size() <= 5) {
                    taglogic(imageObjectController, t, false);
                }
                for (ImageObjectController i : imageObjectControllers) {
                    if (i.checkbox.isSelected()) {
                        if (i.getImageObject().getTagNameObjects().size() <= 5) {
                            taglogic(i, t, false);
                        }
                    }
                }
            });
        }

        popup.getItems().add(new SeparatorMenuItem());

        for(TagObject st : dataManager.getSubTagObjects()) {
            MenuItem item = new MenuItem(st.getName());
            popup.getItems().add(item);
            item.setOnAction(event -> {
                if (imageObjectController.getImageObject().getSubNameTagObjects().size() <= 5) {
                    taglogic(imageObjectController, st, true);
                }
                for (ImageObjectController i : imageObjectControllers) {
                    if (i.checkbox.isSelected()) {
                        if (i.getImageObject().getSubNameTagObjects().size() <= 5) {
                            taglogic(i, st, true);
                        }
                    }
                }
            });
        }
    }

    private void taglogic(ImageObjectController imageObjectController, TagObject t, boolean sub) {
        if(!sub) {
            if (checkForTagDuplicates(imageObjectController.getImageObject().getTagNameObjects(), t)) {
                imageObjectController.getImageObject().getTagNameObjects().remove(dataManager.getTagObjects().get(t.getIndex()).getName());
            } else {
                imageObjectController.getImageObject().getTagNameObjects().add(dataManager.getTagObjects().get(t.getIndex()).getName());
            }
            imageObjectController.setTagOnGui();
        } else {
            if (checkForTagDuplicates(imageObjectController.getImageObject().getSubNameTagObjects(), t)) {
                imageObjectController.getImageObject().getSubNameTagObjects().remove(dataManager.getSubTagObjects().get(t.getIndex()).getName());
            } else {
                imageObjectController.getImageObject().getSubNameTagObjects().add(dataManager.getSubTagObjects().get(t.getIndex()).getName());
            }
            imageObjectController.setSubTagOnGui();
        }
        imageObjectController.checkbox.setSelected(false);

    }

    private Boolean checkForTagDuplicates(ArrayList<String> list, TagObject t) {
        boolean alreadyThere = false;
        for(String s : list) {
            if(s.equals(t.getName())) {
                alreadyThere = true;
            }
        }
        return alreadyThere;
    }

    private void initTreeView() {
        String rootpath = AccountManager.getActiveAccount().getPath() + "\\" + AccountManager.getActiveAccount().getName() + "'s Bilder";
        TreeItem<TreeItemObject> rootItem = new TreeItem<>(new TreeItemObject(AccountManager.getActiveAccount().getName() + "'s Bilder", rootpath));

        treeView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<TreeItem<TreeItemObject>>) (observable, old_val, new_val) -> {
            TreeItem<TreeItemObject> selectedItem = new_val;
            import_images_byPath(selectedItem.getValue().getPath());
        });

        rootItem.setExpanded(true);
        createSubTrees(rootItem, rootpath);

        treeView.setRoot(rootItem);

    }

    private void createSubTrees(TreeItem<TreeItemObject> treeItem, String path) {
        File file = new File(path);
        String[] fileNames = file.list();
        for(String s : fileNames) {
            String newPath = path + "\\" + s;
            File newFile = new File(newPath);
            if(newFile.isDirectory()) {
                TreeItem<TreeItemObject> item = new TreeItem<>(new TreeItemObject(s, newPath));
                item.setExpanded(true);
                treeItem.getChildren().add(item);
                createSubTrees(item, newPath);
            }
        }
    }

    public void close() {

    }

    public void show_about() {

    }

    public void show_accountManager(ActionEvent actionEvent) {
        for(SimpleTagObject t : dataManager.getObsTagObjects()) {
            System.out.println("tagname: " + t.getName());
            System.out.println("tagcolor: " + t.getColor());
        }

    }

    public void addTag1() {
        //vboxTags.getChildren().add(getTagRow(false, "", Color.color(1,1,1), vboxTags.getChildren().size()));
    }

    public void addTag() {
        TextField textField = new TextField();
        ColorPicker colorPicker = new ColorPicker();
        dataManager.addToTagList(new SimpleTagObject("Test", Color.WHITE), textField, colorPicker);
        vboxTags.getChildren().add(getTagRow(false, textField, colorPicker));
    }

    private HBox getTagRow(boolean sub, TextField textField, ColorPicker colorPicker) {
        HBox hbox = new HBox(5);

        //ColorPicker colorPicker = new ColorPicker();
        colorPicker.getStylesheets().add(getClass().getResource("/css/colorpicker.css").toExternalForm());
        //colorPicker.setValue(color);
        //TextField textField = new TextField(name);
        textField.setPrefWidth(100);
        /*textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    if(sub) updateSubTagList();
                    else updateTagList();
                }
            }
        });*/

        /*colorPicker.setOnAction(event -> {
            if(sub) updateSubTagList();
            else updateTagList();
        });*/

        Button btn_delete = new Button("-");

        btn_delete.setOnAction(event ->{
            System.out.println("auf gehts");
            //dataManager.getObsTagObjects().remove();
            int index = 0;
            for(Node nv : vboxTags.getChildren()) {
                HBox hboxtemp = (HBox) nv;
                if(hboxtemp.getChildren().get(2) == btn_delete) {

                    break;
                }
                else index++;

            }
            /*if(!sub) vboxTags.getChildren().remove(hbox);
            else vboxSubTags.getChildren().remove(hbox);
            if(!sub) updateTagList();
            else updateSubTagList();
            showImagesinGrid(dataManager.getImageObjects().size(), true);
            */
        });

        hbox.getChildren().addAll(colorPicker, textField, btn_delete);
        return hbox;
    }

    private void updateTagList() {
        dataManager.getTagObjects().clear();
        int index = 0;
        for(Node nv : vboxTags.getChildren()) {
            HBox hbox = (HBox) nv;
            ColorPicker c = (ColorPicker) hbox.getChildren().get(0);
            TextField t = (TextField) hbox.getChildren().get(1);
            dataManager.getTagObjects().add(new TagObject(index, t.getText(), c.getValue()));
            index++;
        }
        System.out.println("TagList geuptated");
    }

    public void add_subtag() {
        //vboxSubTags.getChildren().add(getTagRow(true, "", Color.color(1,1,1), vboxSubTags.getChildren().size()));
    }

    private void updateSubTagList() {
        int index = 0;
        dataManager.getSubTagObjects().clear();
        for(Node nv : vboxSubTags.getChildren()) {
            HBox hbox = (HBox) nv;
            ColorPicker c = (ColorPicker) hbox.getChildren().get(0);
            TextField t = (TextField) hbox.getChildren().get(1);
            dataManager.getSubTagObjects().add(new TagObject(index, t.getText(), c.getValue()));
            index++;
        }
        System.out.println("SubTagList geuptated");
    }

    public void selectAll(ActionEvent actionEvent) {
        for(ImageObjectController i : imageObjectControllers) {
            i.checkbox.setSelected(true);
        }
    }


    public void storeImages(ActionEvent actionEvent) {
        fileSorter.sortAndSaveFiles(dataManager.getImageObjects(), check_monthly.isSelected(), check_tags.isSelected(), check_subtags.isSelected());
        initTreeView();
    }

    public void invertselection(ActionEvent actionEvent) {
        for(ImageObjectController i : imageObjectControllers) {
            if(i.checkbox.isSelected()) i.checkbox.setSelected(false);
            else i.checkbox.setSelected(true);
        }
    }

    public void selectNone(ActionEvent actionEvent) {
        for(ImageObjectController i : imageObjectControllers) {
            i.checkbox.setSelected(false);
        }
    }

    public void deleteFile(ActionEvent actionEvent) {
        if(Dialogs.ConfirmDialog("Löschen", "Ausgewählte Dateien Löschen", "Sollen die ausgewählten Dateien wirklich gelöscht werden?")) {
            for(ImageObjectController i : imageObjectControllers) {
                if(i.checkbox.isSelected()) {
                    if(FileHandler.fileExist(i.getImageObject().getPath())) {
                        File file = new File(i.getImageObject().getPath());
                        file.delete();
                        System.out.println(i.getImageObject().getName() + " wurder gelöscht");
                    }
                    dataManager.getImageObjects().remove(i.getImageObject());
                }
            }
            showImagesinGrid(dataManager.getImageObjects().size(), true);
        }
    }
}
