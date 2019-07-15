package logic;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import file_handling.FileHandler;
import gui.controller.MainController;
import gui.dialog.Dialogs;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import objects.ImageObject;
import objects.SimpleTagObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataManager {

    private ArrayList<ImageObject> displayedImageObjects = new ArrayList<>();
    private ArrayList<ImageObject> allImageObjects = new ArrayList<>();
    private ArrayList<ImageObject> tempImages = new ArrayList<>();
    private ArrayList<SimpleTagObject> tagObjects = new ArrayList<>();
    private ArrayList<SimpleTagObject> subTagObjects = new ArrayList<>();
    private DateTimeFormatter dateFormatter = new DateTimeFormatter();
    private String rootPath = "";
    private Dialogs dialogs = new Dialogs();
    private MainController mainController;
    private ArrayList<ImageObject> deleteList = new ArrayList<>();

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

    public void import_all_image_data() {
        allImageObjects.clear();
        List<File> files = new ArrayList<>();
        files = getAllFilesInFolderAndSubFolder(rootPath, files);
        importImageData(files, allImageObjects, true);
    }

    public void import_images_dialog() {
        List<File> files = dialogs.fileChooser();
        deleteList.clear();
        importImageData(files, displayedImageObjects, false);
        for(ImageObject i : displayedImageObjects) {
            if(i.isFixed()) deleteList.add(i);
            else tempImages.add(i);
        }
        removeByDeleteList(displayedImageObjects);
    }

    public void reloadTempImages() {
        displayedImageObjects.clear();
        displayedImageObjects.addAll(tempImages);
    }

    public void importImageData(List<File> files, ArrayList<ImageObject> storeList, boolean isFixed) {
        if(files != null) {
            for (File fileEntry : files) {
                if (fileEntry.isFile()) {
                    LocalDateTime date = dateFormatter.checkFileNameForDate(fileEntry.getName());
                    if (date != null) {
                        storeList.add(new ImageObject(fileEntry.getName(), date, fileEntry.getAbsolutePath(), fileEntry.getParent(), isFixed));
                    } else {
                        storeList.add(readMeta(fileEntry, isFixed));
                    }
                }
            }
        }
    }

    public void fillDisplayedImages(String path, boolean reinit) {
        if(reinit) displayedImageObjects.clear();
        deleteList.clear();
        //System.out.println("global path: " + path);
        for(ImageObject i : allImageObjects) {
            //System.out.println("searchpath: " + i.getParentPath());
            if(i.getParentPath().equals(path) || i.getParentPath().equals(path + "\\")) {
                //System.out.println("match");
                if(FileHandler.fileExist(i.getPath())) {
                    displayedImageObjects.add(i);
                } else {
                    deleteList.add(i);
                }
            }
        }
        removeByDeleteList(allImageObjects);
    }

    private ImageObject readMeta(File f, boolean isFixed) {
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
        return new ImageObject(f.getName(), date, f.getAbsolutePath(), f.getParent(), isFixed);
    }

    public List<File> getAllFilesInFolderAndSubFolder(String path, List<File> files) {
        File directory = new File(path);

        File[] fList = directory.listFiles(new ImageFileFilter());
        if (fList != null) {
            for (File file : fList) {
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    getAllFilesInFolderAndSubFolder(file.getAbsolutePath(), files);
                }
            }
        }
        return files;
    }

    public void removeByDeleteList(ArrayList<ImageObject> list) {
        for(ImageObject d : deleteList) {
            list.remove(d);
        }
    }

    public ArrayList<ImageObject> getDisplayedImageObjects() {
        return displayedImageObjects;
    }

    public ArrayList<SimpleTagObject> getSubTagObjects() {
        return subTagObjects;
    }

    public ArrayList<SimpleTagObject> getTagObjects() {
        return tagObjects;
    }

    public ArrayList<ImageObject> getAllImageObjects() {
        return allImageObjects;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public ArrayList<ImageObject> getTempImages() {
        return tempImages;
    }
}
