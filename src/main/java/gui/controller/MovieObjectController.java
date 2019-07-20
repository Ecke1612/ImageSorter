package gui.controller;


import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import objects.ImageObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;


public class MovieObjectController extends MediaObjectController{

    public MediaView mediaview;
    public CheckBox checkbox;
    public VBox vbox_main;
    public HBox hbox_tags;
    public HBox hbox_subtags;
    public Label label_date;
    public Label label_filename;

    private ImageObject imageObject;
    private MediaPlayer mediaPlayer;
    private Media media;


    public MovieObjectController(ImageObject imageObject) {
        super(imageObject);
        this.imageObject = imageObject;
    }

    public void initialize() {
        File file = new File(imageObject.getPath());
        try {
            media = new Media(file.toURI().toURL().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaview.setMediaPlayer(mediaPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocalDateTime date = imageObject.getDate();
        label_date.setText(date.getDayOfMonth() + "." + date.getMonth().getValue() + "." +date.getYear());
        label_filename.setText(imageObject.getName());
        if(imageObject.isFixed()) vbox_main.setStyle("-fx-border-color: rgb(75,75,75); -fx-border-radius: 8;");
        else vbox_main.setStyle("-fx-border-color: rgb(194, 75, 56); -fx-border-radius: 8;");

        setTagOnGui(false);
        setTagOnGui(true);
    }

    public void resetMedia() {
        System.out.println(imageObject.getPath() + " media disposed");
        mediaPlayer.dispose();
    }

    public void play() {
        mediaPlayer.play();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void pause() {
        mediaPlayer.pause();
    }
}
