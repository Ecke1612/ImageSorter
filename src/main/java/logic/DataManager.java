package logic;

import com.ed.filehandler.PlainHandler;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import logic.workers.ImageObjectFACTORY;
import presentation.gui.controller.MainController;
import presentation.gui.dialog.Dialogs;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import logic.dataholder.ImageObject;
import presentation.dataholder.SimpleTagObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    //private ArrayList<ImageObject> displayedImageObjects = new ArrayList<>();
    private ObservableList<ImageObject> displayedImageObjects = FXCollections.observableArrayList();
    private ArrayList<ImageObject> allImageObjects = new ArrayList<>();
    private ArrayList<ImageObject> tempImages = new ArrayList<>();
    private ArrayList<SimpleTagObject> tagObjects = new ArrayList<>();
    private ArrayList<SimpleTagObject> subTagObjects = new ArrayList<>();
    private String rootPath = "";
    //private Dialogs dialogs = new Dialogs();
    //private MainController mainController;
    private ArrayList<ImageObject> deleteList = new ArrayList<>();
    private ImageObjectFACTORY imageObjectFACTORY = new ImageObjectFACTORY();
    private PlainHandler plainHandler = new PlainHandler();

    public DataManager() {

    }

    public void setMainController(MainController mainController) {
        //this.mainController = mainController;
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

    public void import_images_dialog(List<File> files) {
        deleteList.clear();
        int displayListsizeHolder = displayedImageObjects.size();
        int index = 0;
        importImageData(files, displayedImageObjects, false);
        for(ImageObject i : displayedImageObjects) {
            if(i.isFixed()) deleteList.add(i);
            else {
                if(index >= displayListsizeHolder) tempImages.add(i);
            }
            index++;
        }
        removeByDeleteList(displayedImageObjects);
        //mainController.showImagesinGrid();
    }

    public void reloadTempImages() {
        displayedImageObjects.clear();
        displayedImageObjects.addAll(tempImages);
    }

    public void importImageData(List<File> files, List<ImageObject> storeList, boolean isFixed) {
        if (files != null) {
            for (File fileEntry : files) {
                if (fileEntry.isFile()) {
                    storeList.add(imageObjectFACTORY.createImageObject(fileEntry, isFixed));
                }
            }
        }
    }


    public void fillDisplayedImages(String path, boolean reinit) {
        if(reinit) displayedImageObjects.clear();
        ArrayList<ImageObject> tempList = new ArrayList<>();
        deleteList.clear();
        //System.out.println("global path: " + path);
        for(ImageObject i : allImageObjects) {
            //System.out.println("searchpath: " + i.getPath());
            if(i.getParentPath().equals(path) || i.getParentPath().equals(path + "\\")) {
                //System.out.println("match");
                if(plainHandler.fileExist(i.getPath())) {
                    tempList.add(i);
                } else {
                    deleteList.add(i);
                }
            }
        }
        displayedImageObjects.addAll(tempList);
        removeByDeleteList(allImageObjects);
        //mainController.showImagesinGrid();
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

    public void removeByDeleteList(List<ImageObject> list) {
        for(ImageObject d : deleteList) {
            list.remove(d);
        }
    }

    public ObservableList<ImageObject> getDisplayedImageObjects() {
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
