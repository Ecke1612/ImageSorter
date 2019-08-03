package logic.filesorter;

import com.ed.filehandler.FileTransfer;
import com.ed.filehandler.PlainHandler;
import logic.AccountManager;
import logic.DataManager;
import logic.dataholder.ImageObject;
import presentation.dataholder.SimpleTagObject;
import presentation.gui.controller.MainController;
import presentation.gui.controller.MediaObjectController;
import presentation.gui.controller.MovieObjectController;
import presentation.gui.dialog.Dialogs;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public abstract class FileSorterInterface {

    //private FileHandlerFACADE fileHandler = new FileHandlerFACADE();
    private PlainHandler plainHandler = new PlainHandler();
    private Dialogs dialogs = new Dialogs();
    private FileTransfer fileTransfer= new FileTransfer();


    public void sortAndSaveFiles(MainController mainController, DataManager dataManager) {
        int index = 0;
        ArrayList<ImageObject> deleteList = new ArrayList<>();
        for (ImageObject imageObject : dataManager.getDisplayedImageObjects()) {

            if (checkExecutionImageObject(mainController.getMediaObjectControllers(), index)) {
               String topath = buildToPath(mainController, imageObject);

                if (!plainHandler.fileExist(topath)) {
                    plainHandler.createDirs(topath);
                }

                String originalFilePath = imageObject.getPath();
                String originalName = imageObject.getName();

                //ImageObject sortieren für Temporäre Dateien (Ersetzen
                ExistObject existObject = checkIfFileExist(topath, imageObject, dataManager);
                if(existObject.isAlreadyThere()) {
                    if (!existObject.isReplace()) {
                        changeNameOfImageObjectRandomly(imageObject);
                    }
                }

                String toPathWidthFileName = topath + imageObject.getName();
                File fileTo = new File(toPathWidthFileName);

                sort(fileTo, imageObject, dataManager, topath, mainController.getMediaObjectControllers().get(index),
                        originalFilePath, originalName);

                deleteList.add(imageObject);
            }
            index++;
        }
        for(ImageObject i : deleteList) {
            dataManager.getDisplayedImageObjects().remove(i);
            dataManager.getTempImages().remove(i);
        }
    }

    public abstract void sort(File fileTo, ImageObject imageObject, DataManager dataManager, String toPath,
                              MediaObjectController mediaObjectController, String originalFilePath, String originalName);

    public abstract boolean checkExecutionImageObject(ArrayList<MediaObjectController> mediaObjectControllers, int index);

    public String buildToPath(MainController mainController, ImageObject imageObject) {
        String month = "";
        String tag = "";
        String subTag = "";
        String movie = "";

        if (mainController.check_monthly.isSelected()) month = imageObject.getStringMonth() + "\\";
        if (mainController.check_tags.isSelected()) tag = buildTagFolder(imageObject.getTagObjects()) + "\\";
        if (mainController.check_subtags.isSelected()) subTag = buildTagFolder(imageObject.getSubTagObjects()) + "\\";
        if(imageObject.isMovie()) movie = "Videos\\";

        String topath = AccountManager.getActiveAccount().getPath() + "\\" + AccountManager.getActiveAccount().getName() + "'s Bilder\\" + imageObject.getStringYear() + "\\" + tag + month + subTag + movie;
        return topath;
    }

    public void copyImageObject(ImageObject i, ImageObject remoteI, String fullToPath, String topath) {
        remoteI.setName(i.getName());
        remoteI.setPath(fullToPath);
        remoteI.setParentPath(topath);
        remoteI.getTagObjects().clear();
        remoteI.getTagObjects().addAll(i.getTagObjects());
        remoteI.getSubTagObjects().clear();
        remoteI.getSubTagObjects().addAll(i.getSubTagObjects());
    }

    public void overrideRemoteIWithoutDeleting(DataManager dataManager, ImageObject imageObject, String toPathWidthFileName, String topath, MediaObjectController mediaObjectController) {
        int remoteIndex = getRemoteImageObject(imageObject, dataManager);
        if (remoteIndex >= 0) {
            ImageObject remoteI = dataManager.getAllImageObjects().get(remoteIndex);
            copyImageObject(imageObject, remoteI, toPathWidthFileName, topath);

            disposeMedia(imageObject, mediaObjectController);
            fileTransfer.copyFile(imageObject.getPath(), toPathWidthFileName);
        } else System.out.println("no remoteImg found");
    }

    public void disposeMedia(ImageObject i, MediaObjectController mediaObjectController) {
        if(i.isMovie()) {
            MovieObjectController m = (MovieObjectController) mediaObjectController;
            m.resetMedia();
        }
    }

    public ExistObject checkIfFileExist(String fullPath, ImageObject i, DataManager dataManager) {
        if (plainHandler.fileExist(fullPath)) {
            int remoteIndex = getRemoteImageObject(i, dataManager);
            if (remoteIndex >= 0) {
                boolean replace = dialogs.fileAlreadyExistDialog(i.getPath(), dataManager.getAllImageObjects().get(remoteIndex).getPath(), i.isMovie());
                return new ExistObject(true, replace, dataManager.getAllImageObjects().get(remoteIndex));
            }
        }
        return new ExistObject(false, false, null);
    }

    public class ExistObject {
        private boolean alreadyThere;
        private boolean replace;
        private ImageObject imageObject;

        public ExistObject(boolean alreadyThere, boolean replace, ImageObject imageObject) {
            this.alreadyThere = alreadyThere;
            this.replace = replace;
            this.imageObject = imageObject;
        }
        public boolean isReplace() {return replace;}
        public ImageObject getImageObject() {return imageObject;}
        public boolean isAlreadyThere() {return alreadyThere;}
    }

    public int getRemoteImageObject(ImageObject i, DataManager dataManager) {
        int remoteIndex = -1;
        int counter = 0;
        for (ImageObject ri : dataManager.getAllImageObjects()) {
            if (i.getName().equals(ri.getName())) {
                if(!i.getPath().equals(ri.getPath())) {
                    remoteIndex = counter;
                }
            }
            counter++;
        }
        return remoteIndex;
    }

    public void changeNameOfImageObjectRandomly(ImageObject i) {
        Random r = new Random();
        int number = r.nextInt(1000);
        String[] newName = i.getName().split("\\.");
        i.setName(newName[0] + "_" + number + "." + newName[1]);
    }

    public String buildTagFolder(ArrayList<SimpleTagObject> taglist) {
        String[] tags = new String[taglist.size()];
        for(int x = 0; x < tags.length; x++) {
            tags[x] = taglist.get(x).getName();
        }
        Arrays.sort(tags);
        StringBuilder tagName = new StringBuilder();
        for(int s = 0; s < tags.length; s++) {
            tagName.append(tags[s]);
            if(tags.length - 1 != s) {
                tagName.append("_");
            }
        }
        return tagName.toString();
    }

}
