package logic.filesorter;

import com.ed.filehandler.FileTransfer;
import logic.DataManager;
import logic.dataholder.ImageObject;
import presentation.gui.controller.MediaObjectController;

import java.io.File;
import java.util.ArrayList;

public class FileSorterMoveAndCut extends FileSorterInterface{

    private FileTransfer fileTransfer = new FileTransfer();

    @Override
    public void sort(ImageObject imageObject, DataManager dataManager, String topath, MediaObjectController mediaObjectController,
                     String originalFilePath, String originalName) {
        System.out.println("sort Move and Cut");
        String toPathWidthFileName = topath + imageObject.getName();
        File fileTo = new File(originalFilePath);
        if(!fileTo.exists()){
            System.out.println("Move And Cut: so ImgObj is already in allImgList and we just need to change the pathes. On top of that, we move the the file to new position");
            imageObject.setPath(toPathWidthFileName);
            imageObject.setParentPath(topath);

            disposeMedia(imageObject, mediaObjectController);
            fileTransfer.moveFile(originalFilePath, toPathWidthFileName);

        } else if(fileTo.exists()) {
            System.out.println("Move and Cut but File exist, so be replaced. That means, there is an ImgObj in allImgList which we need to find. The parameters of this ImgObj " +
                    "will be replaced by the new ones. Further more, the File will be moved to the new position");

            dataManager.getAllImageObjectsMap().replace(imageObject.getPath(), imageObject);
            disposeMedia(imageObject, mediaObjectController);
            fileTransfer.moveFile(imageObject.getPath(), toPathWidthFileName);
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
