package logic.filesorter;

import com.ed.filehandler.FileTransfer;
import logic.DataManager;
import logic.dataholder.ImageObject;
import presentation.gui.controller.MediaObjectController;

import java.io.File;
import java.util.ArrayList;

public class FileSorterTemp extends FileSorterInterface{

    private FileTransfer fileTransfer = new FileTransfer();

    @Override
    public void sort(ImageObject imageObject, DataManager dataManager, String topath, MediaObjectController mediaObjectController,
                     String originalFilePath, String originalName) {
        String toPathWidthFileName = topath + imageObject.getName();
        File fileTo = new File(toPathWidthFileName);
        if(!fileTo.exists()) {
            System.out.println("NotMove NotCut: ImgObj only exist in Display and TempList, so change pathes of ImgObj and add to allImgList");

            imageObject.setPath(toPathWidthFileName);
            imageObject.setParentPath(topath);
            dataManager.getAllImageObjectsMap().put(imageObject.getPath(), imageObject);
            dataManager.getTempImages().remove(originalFilePath);
            imageObject.setFixed(true);

            disposeMedia(imageObject, mediaObjectController);
            fileTransfer.copyFile(originalFilePath, toPathWidthFileName);
        } else if(fileTo.exists()) {
            System.out.println("NotMove NotCut but File exist an so it should be replaced. We need to get the existing RemoteIMG and change that parameters to the new one");
            dataManager.getAllImageObjectsMap().replace(imageObject.getPath(), imageObject);
            dataManager.getTempImages().remove(originalFilePath);
        }
    }
}
