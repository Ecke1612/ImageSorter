package logic;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import gui.controller.MainController;
import gui.dialog.Dialogs;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import objects.ImageObject;
import objects.SimpleTagObject;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private ArrayList<ImageObject> imageObjects = new ArrayList<>();
    private ArrayList<SimpleTagObject> tagObjects = new ArrayList<>();
    private ArrayList<SimpleTagObject> subTagObjects = new ArrayList<>();
    private DateTimeFormatter dateFormatter = new DateTimeFormatter();
    private Dialogs dialogs = new Dialogs();
    private MainController mainController;
    private String[] acceptetFiles = {"jpg", "png", "bmp"};

    public DataManager() {

    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void addToTagList(boolean sub, SimpleTagObject tagObject, TextField textField, ColorPicker colorPicker) {
        tagObject.nameProperty().bind(textField.textProperty());
        tagObject.colorProperty().bind(colorPicker.valueProperty());
        if(!sub) tagObjects.add(tagObject);
        else subTagObjects.add(tagObject);
    }

    public void deleteFromTagList(boolean sub, int index) {
        if(!sub) tagObjects.remove(index);
        else subTagObjects.remove(index);
    }

    public int import_images_dialog() {
        List<File> files = dialogs.fileChooser();
        if(files != null) {
            for(File f : files) {
                LocalDateTime date = dateFormatter.checkFileNameForDate(f.getName());
                if(date != null) {
                    imageObjects.add(new ImageObject(f.getName(), date, f.getAbsolutePath()));
                } else {
                    imageObjects.add(readMeta(f));
                }
            }
            return files.size();
        }
        return -1;
    }

    public int import_images_byPath(String path) {
        imageObjects.clear();
        File folder = new File(path);
        int count = 0;
        File[] files = folder.listFiles(new ImageFileFilter());

        for (File fileEntry : files) {
            if(fileEntry.isFile()) {
                LocalDateTime date = dateFormatter.checkFileNameForDate(fileEntry.getName());
                if(date != null) {
                    imageObjects.add(new ImageObject(fileEntry.getName(), date, fileEntry.getAbsolutePath()));
                } else {
                    imageObjects.add(readMeta(fileEntry));
                }
                count++;
            }
        }
        return count;
    }


    private ImageObject readMeta(File f) {
        LocalDateTime date = null;
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(f);
            boolean found = false;
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    //System.out.println(tag.getTagName());
                    if (!found) {
                        if (tag.getTagName().equals("Date/Time") || tag.getTagName().equals("Creation Time")) {
                            date = dateFormatter.formate(tag.getDescription(), f.getName());
                            found = true;
                        } else if (tag.getTagName().equals("Date/Time Original")) {
                            date = dateFormatter.formate(tag.getDescription(), f.getName());
                            found = true;
                        } else if (tag.getTagName().equals("Date/Time Digitized")) {
                            date = dateFormatter.formate(tag.getDescription(), f.getName());
                            found = true;
                        } else if (tag.getTagName().equals("File Modified Date")) {
                            date = dateFormatter.formate(tag.getDescription(), f.getName());
                            found = true;
                            System.out.println("!Achtung, Datum ggf. verfälscht");
                        } else {
                            //System.out.println("not found date by: " + f.getName());
                        }
                    }
                }
                if (directory.hasErrors()) {
                    for (String error : directory.getErrors()) {
                        System.err.format("ERROR: %s", error);
                    }
                }
            }
            if(!found) {
                System.out.println("");
                System.out.println("FINAL not found date by: " + f.getName());
                for (Directory directory : metadata.getDirectories()) {
                    for (Tag tag : directory.getTags()) {
                        System.out.println(tag.getTagName() + " : " + tag.getDescription());
                    }
                }
            }

        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ImageObject(f.getName(), date, f.getAbsolutePath());
    }



    public ArrayList<ImageObject> getImageObjects() {
        return imageObjects;
    }

    public ArrayList<SimpleTagObject> getSubTagObjects() {
        return subTagObjects;
    }

    public ArrayList<SimpleTagObject> getTagObjects() {
        return tagObjects;
    }
}
