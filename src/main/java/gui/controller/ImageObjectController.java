package gui.controller;


import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import objects.ImageObject;
import objects.SimpleTagObject;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;



public class ImageObjectController extends MediaObjectController{

    public ImageView imageview;
    public CheckBox checkbox;
    public VBox vbox_main;
    public HBox hbox_tags;
    public HBox hbox_subtags;
    public Label label_date;
    public Label label_filename;

    private ImageObject imageObject;


    public ImageObjectController(ImageObject imageObject) {
        super(imageObject);
        this.imageObject = imageObject;
    }

    public void initialize() {
        Image image = new Image(new File(imageObject.getPath()).toURI().toString(), 175,125,true, false, true);
        imageview.setImage(image);

        LocalDateTime date = imageObject.getDate();
        label_date.setText(date.getDayOfMonth() + "." + date.getMonth().getValue() + "." +date.getYear());
        label_filename.setText(imageObject.getName());
        if(imageObject.isFixed()) vbox_main.setStyle("-fx-border-color: rgb(75,75,75); -fx-border-radius: 8;");
        else vbox_main.setStyle("-fx-border-color: rgb(194, 75, 56); -fx-border-radius: 8;");

        setTagOnGui(false);
        setTagOnGui(true);
    }
}
