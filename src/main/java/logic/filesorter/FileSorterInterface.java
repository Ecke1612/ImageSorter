package logic.filesorter;

import com.ed.filehandler.PlainFileHandler;
import logic.AccountManager;
import logic.DataManager;
import logic.dataholder.ImageObject;
import presentation.dataholder.SimpleTagObject;
import presentation.gui.controller.MainController;
import presentation.gui.controller.MediaObjectController;
import presentation.gui.controller.MovieObjectController;
import presentation.gui.dialog.Dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public abstract class FileSorterInterface {

    private PlainFileHandler plainHandler = new PlainFileHandler();
    private Dialogs dialogs = new Dialogs();


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
                String toPathWidthFileName = topath + imageObject.getName();

                if(dataManager.getAllImageObjectsMap().containsKey(toPathWidthFileName)) {
                    ImageObject existingImg = dataManager.getAllImageObjectsMap().get(toPathWidthFileName);
                    if(!dialogs.fileAlreadyExistDialog(imageObject.getPath(), existingImg.getPath(), imageObject.isMovie())) {
                        changeNameOfImageObjectRandomly(imageObject);
                    }
                }

                sort(imageObject, dataManager, topath, mainController.getMediaObjectControllers().get(index),
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

    public abstract void sort(ImageObject imageObject, DataManager dataManager, String toPath, MediaObjectController mediaObjectController,
                              String originalFilePath, String originalName);


    public boolean checkExecutionImageObject(ArrayList<MediaObjectController> mediaObjectControllers, int index) {
        boolean executeSorting = false;
        if (mediaObjectControllers.get(index).checkbox.isSelected()) {
            executeSorting = true;
            System.out.println("execute because of move with check");
        }
        return executeSorting;
    }

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

    public void disposeMedia(ImageObject i, MediaObjectController mediaObjectController) {
        if(i.isMovie()) {
            MovieObjectController m = (MovieObjectController) mediaObjectController;
            m.resetMedia();
        }
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
