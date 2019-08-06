package logic.filesorter;

import com.ed.filehandler.FileTransfer;
import logic.DataManager;
import logic.dataholder.ImageObject;
import presentation.gui.controller.MediaObjectController;

import java.io.File;
import java.util.ArrayList;

public class FileSorterMove extends FileSorterInterface{

    private FileTransfer fileTransfer = new FileTransfer();

    @Override
    public void sort(File fileTo, ImageObject imageObject, DataManager dataManager, String topath, MediaObjectController mediaObjectController, String originalFilePath, String originalName) {
        System.out.println("sort Move");
        String toPathWidthFileName = topath + imageObject.getName();
        if(!fileTo.exists()) {
            System.out.println("Move, NotCut: so we are going to create a copy of the file. Therfore we need a brand new ImgObj with all perameters of the old one except for new" +
                    "pathes. The new ImgObj will be added to the allImgList. Because of the earlier renaming, we are going to restore the original name for the old ImgObj. Then the File will be copied.");

            boolean isMovie = false;
            if (imageObject.getName().toLowerCase().endsWith("mp4")) isMovie = true;
            ImageObject newI = new ImageObject(imageObject.getName(), imageObject.getDate(), topath + imageObject.getName(), topath, true, isMovie);
            newI.getTagObjects().addAll(imageObject.getTagObjects());
            newI.getSubTagObjects().addAll(imageObject.getSubTagObjects());
            dataManager.getAllImageObjectsMap().put(newI.getPath(), newI);
            //dataManager.getAllImageObjects().add(newI);
            imageObject.setName(originalName);

            disposeMedia(imageObject, mediaObjectController);
            fileTransfer.copyFile(imageObject.getPath(), toPathWidthFileName);
            //copyFile(imageObject.getPath(), toPathWidthFileName, imageObject, mediaObjectController);

        } else if(fileTo.exists()) {
            System.out.println("Move and NotCut but File exist and will be replaced: So there is already an ImgObj in AllImgList which we need to find and change these parameters to" +
                    "the new one.");
            //overrideRemoteIWithoutDeleting(dataManager, imageObject, toPathWidthFileName, topath,mediaObjectController);
            dataManager.getAllImageObjectsMap().replace(imageObject.getPath(), imageObject);
        }
    }

    @Override
    public boolean checkExecutionImageObject(ArrayList<MediaObjectController> mediaObjectControllers, int index) {
        boolean executeSorting = false;
        if (mediaObjectControllers.get(index).checkbox.isSelected()) {
            executeSorting = true;
            System.out.println("execute because of move with check");
        }
        return executeSorting;
    }
}
