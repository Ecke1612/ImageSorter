package gui.controller;


import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import logic.DataManager;
import objects.ImageObject;
import objects.SimpleTagObject;
import objects.TagObject;

import java.io.File;
import java.util.ArrayList;



public class ImageObjectController {

    public ImageView imageview;
    public CheckBox checkbox;
    public HBox hbox_tags;
    public HBox hbox_subtags;

    private ImageObject imageObject;


    public ImageObjectController(ImageObject imageObject) {
        this.imageObject = imageObject;
    }

    public void initialize() {
        Image image = new Image(new File(imageObject.getPath()).toURI().toString(), 100,85,true, false, true);
        imageview.setImage(image);

        setTagOnGui(false);
        setTagOnGui(true);
    }


    public void setTagOnGui(boolean sub) {
        HBox mainHbox = hbox_tags;
        ArrayList<SimpleTagObject> tagList = imageObject.getTagObjects();
        if(sub) {
            mainHbox = hbox_subtags;
            tagList = imageObject.getSubTagObjects();
        }
        mainHbox.getChildren().clear();
        for(SimpleTagObject t : tagList) {
            HBox hbox = new HBox(5);
            Label colorLabel = new Label();
            colorLabel.setStyle(
                    "-fx-font-size: 12;" +
                    "-fx-pref-width: 25;" +
                            "-fx-alignment: center;"
            );

            ObjectProperty<Background> background = colorLabel.backgroundProperty();
            background.bind(Bindings.createObjectBinding(() -> {
                BackgroundFill fill = new BackgroundFill(t.getColor(), CornerRadii.EMPTY, Insets.EMPTY);
                return new Background(fill);
            }, t.colorProperty()));

            colorLabel.textProperty().bind(Bindings.createStringBinding(() ->
                t.getName().substring(0,1), t.nameProperty()));

            t.colorProperty().addListener(new ChangeListener<Color>() {
                @Override
                public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                    System.out.println("new Color: " + newValue.toString());
                }
            });

            if(t.getColor().getBrightness() < 0.81) colorLabel.setTextFill(Color.WHITE);
            else colorLabel.setTextFill(Color.BLACK);

            hbox.getChildren().addAll(colorLabel);
            mainHbox.getChildren().add(hbox);
        }
    }

    public void checkForDeletedTags(boolean sub, SimpleTagObject t) {
        ArrayList<SimpleTagObject> listToCheck = imageObject.getTagObjects();
        HBox hboxRemoveFrom = hbox_tags;
        if(sub) {
            listToCheck = imageObject.getSubTagObjects();
            hboxRemoveFrom = hbox_subtags;
        }
        if(listToCheck.contains(t)) {
            int index = listToCheck.indexOf(t);
            listToCheck.remove(index);
            hboxRemoveFrom.getChildren().remove(index);
        }
    }

    public void show_imageOptions() {

    }

    public ImageObject getImageObject() {
        return imageObject;
    }
}
