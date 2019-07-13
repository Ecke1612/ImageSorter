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
import main.Main;
import objects.ImageObject;
import objects.TagObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private ArrayList<ImageObject> imageObjects = new ArrayList<>();
    private ArrayList<TagObject> tagObjects = new ArrayList<>();
    private ObservableList<TagObject> obsTagObjects = FXCollections.observableArrayList();
    private ArrayList<TagObject> subTagObjects = new ArrayList<>();
    private DateTimeFormatter dateFormatter = new DateTimeFormatter();
    private Dialogs dialogs = new Dialogs();
    private MainController mainController;

    public DataManager() {

    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        obsTagObjects.addListener(new ListChangeListener<TagObject>() {
            @Override
            public void onChanged(Change<? extends TagObject> c) {
                System.out.println("change: " + c);
            }
        });
    }

    public void addToTagList(TagObject tagObject) {
        obsTagObjects.add(tagObject);
    }

    public void deleteFromTagList(TagObject tagObject) {
        obsTagObjects.remove(tagObject);
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
        for (final File fileEntry : folder.listFiles()) {
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

    public ArrayList<TagObject> getTagObjects() {
        return tagObjects;
    }

    public ArrayList<TagObject> getSubTagObjects() {
        return subTagObjects;
    }

    public TagObject getTagObjectByName(String name) {
        for(TagObject t : tagObjects) {
            if(name.equals(t.getName())) return t;
        }
        return null;
    }

    public TagObject getSubTagObjectByName(String name) {
        for(TagObject t : subTagObjects) {
            if(name.equals(t.getName())) return t;
        }
        return null;
    }
}
