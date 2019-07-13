package gui.controller;


import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import logic.DataManager;
import objects.ImageObject;
import objects.TagObject;

import java.io.File;
import java.util.ArrayList;



public class ImageObjectController {

    public ImageView imageview;
    public CheckBox checkbox;
    //public VBox vbox_tags;
    //public FlowPane flow_tags;
    public HBox hbox_tags;
    public HBox hbox_subtags;

    private ImageObject imageObject;
    private DataManager dataManager;


    public ImageObjectController(ImageObject imageObject, DataManager dataManager) {
        this.imageObject = imageObject;
        this.dataManager = dataManager;
    }

    public void initialize() {
        Image image = new Image(new File(imageObject.getPath()).toURI().toString(), 100,85,true, false, true);
        imageview.setImage(image);

        setTagOnGui();
        setSubTagOnGui();
    }

    public void setTagOnGui() {
        //vbox_tags.getChildren().clear();
        hbox_tags.getChildren().clear();
        ArrayList<String> deleteList = new ArrayList<>();
        for(String s : imageObject.getTagNameObjects()) {
            TagObject t = dataManager.getTagObjectByName(s);
            if(t!=null) {
                HBox hbox = new HBox(5);
                Label colorLabel = new Label();
                colorLabel.setStyle(
                        "-fx-font-size: 12;" +
                        "-fx-pref-width: 25;" +
                                "-fx-alignment: center;"
                );
                colorLabel.setBackground(new Background(new BackgroundFill(t.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));

                char firstChar = t.getName().charAt(0);
                colorLabel.setText(String.valueOf(firstChar));

                if(t.getColor().getBrightness() < 0.81) colorLabel.setTextFill(Color.WHITE);
                else colorLabel.setTextFill(Color.BLACK);

                hbox.getChildren().addAll(colorLabel);
                hbox_tags.getChildren().add(hbox);
            } else {
                deleteList.add(s);
            }
        }
        for(String d : deleteList) {
            imageObject.getTagNameObjects().remove(d);
        }
    }

    public void setSubTagOnGui() {
        hbox_subtags.getChildren().clear();
        ArrayList<String> deleteList = new ArrayList<>();
        for(String s : imageObject.getSubNameTagObjects()) {
            TagObject t = dataManager.getSubTagObjectByName(s);
            if(t!=null) {
                HBox hbox = new HBox(5);
                Label colorLabel = new Label();
                colorLabel.setStyle(
                        "-fx-pref-width: 25;" +
                                "-fx-alignment: center;"
                );
                colorLabel.setBackground(new Background(new BackgroundFill(t.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));

                char firstChar = t.getName().charAt(0);
                colorLabel.setText(String.valueOf(firstChar));

                if(t.getColor().getBrightness() < 0.81) colorLabel.setTextFill(Color.WHITE);
                else colorLabel.setTextFill(Color.BLACK);

                hbox.getChildren().addAll(colorLabel);
                hbox_subtags.getChildren().add(hbox);
            } else {
                deleteList.add(s);
            }
        }
        for(String d : deleteList) {
            imageObject.getTagNameObjects().remove(d);
        }
    }

    public void show_imageOptions() {

    }

    public ImageObject getImageObject() {
        return imageObject;
    }
}
