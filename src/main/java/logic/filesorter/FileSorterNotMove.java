package logic.filesorter;

import com.ed.filehandler.FileTransfer;
import logic.DataManager;
import logic.dataholder.ImageObject;
import presentation.gui.controller.MediaObjectController;

import java.io.File;
import java.util.ArrayList;

public class FileSorterNotMove extends FileSorterInterface{

    private FileTransfer fileTransfer = new FileTransfer();

    @Override
    public void sort(File fileTo, ImageObject imageObject, DataManager dataManager, String topath, MediaObjectController mediaObjectController,
                     String originalFilePath, String originalName) {
        System.out.println("sort move");
        String toPathWidthFileName = topath + imageObject.getName();
        if(!fileTo.exists()) {
            System.out.println("NotMove NotCut: ImgObj only exist in Display and TempList, so change pathes of ImgObj and add to allImgList");
            imageObject.setPath(toPathWidthFileName);
            imageObject.setParentPath(topath);
            dataManager.getAllImageObjects().add(imageObject);
            imageObject.setFixed(true);

            disposeMedia(imageObject, mediaObjectController);
            fileTransfer.copyFile(originalFilePath, toPathWidthFileName);
            //copyFile(originalFilePath, toPathWidthFileName, imageObject, mediaObjectController);
        } else if(fileTo.exists()) {
            System.out.println("NotMove NotCut but File exist an so it should be replaced. We need to get the existing RemoteIMG and change that parameters to the new one");
            overrideRemoteIWithoutDeleting(dataManager, imageObject, toPathWidthFileName, topath, mediaObjectController);
        }
    }

    @Override
    public boolean checkExecutionImageObject(ArrayList<MediaObjectController> mediaObjectControllers, int index) {
        boolean executeSorting = false;
        if (mediaObjectControllers.get(index).getImageObject().getTagObjects().size() > 0 || mediaObjectControllers.get(index).getImageObject().getSubTagObjects().size() > 0
                || mediaObjectControllers.get(index).checkbox.isSelected()) {
            executeSorting = true;
            System.out.println("execute because of check or tags with no move");
        }
        return executeSorting;
    }
}
