package presentation.gui.controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import logic.dataholder.ImageObject;

import java.io.File;


public class ImageObjectController extends MediaObjectController{

    public ImageView imageview;

    private ImageObject imageObject;


    public ImageObjectController(ImageObject imageObject) {
        super(imageObject);
        this.imageObject = imageObject;
    }

    public void initialize() {
        Image image = new Image(new File(imageObject.getPath()).toURI().toString(), 175,125,true, false, true);
        imageview.setImage(image);

        initGlobal();
    }
}
