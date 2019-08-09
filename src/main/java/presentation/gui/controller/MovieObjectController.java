package presentation.gui.controller;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import logic.dataholder.ImageObject;

import java.io.File;

public class MovieObjectController extends MediaObjectController{

    public MediaView mediaview;

    private ImageObject imageObject;
    private MediaPlayer mediaPlayer;
    private Media media;


    public MovieObjectController(ImageObject imageObject, MainController mainController) {
        super(imageObject, mainController);
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
        initGlobal();
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
