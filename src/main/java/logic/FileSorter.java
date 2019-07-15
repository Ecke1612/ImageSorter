package logic;

import file_handling.FileHandler;
import file_handling.StoreData;
import gui.controller.ImageObjectController;
import objects.ImageObject;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

public class FileSorter {

    private StoreData storeData;

    private String oldPath = "";

    public FileSorter(StoreData storeData) {
        this.storeData = storeData;
    }

    public void sortAndSaveFiles(ArrayList<ImageObject> imageObjects, ArrayList<ImageObjectController> imageObjectControllers, boolean isMonthly, boolean isTags, boolean isSubtags, boolean isCut, DataManager dataManager) {
        int index = 0;
        ArrayList<ImageObject> deleteList = new ArrayList<>();
        for(ImageObject i : imageObjects) {
            if (imageObjectControllers.get(index).checkbox.isSelected()) {
                System.out.println("sort");
                String month = "";
                String tag = "";
                String subTag = "";

                if (isMonthly) month = i.getStringMonth() + "\\";
                if (isTags) tag = buildTagFolder(i) + "\\";
                if (isSubtags) subTag = buildSubTagFolder(i) + "\\";

                String topath = AccountManager.getActiveAccount().getPath() + "\\" + AccountManager.getActiveAccount().getName() + "'s Bilder\\" + i.getStringYear() + "\\" + tag + month + subTag;

                if (!FileHandler.fileExist(topath)) {
                    FileHandler.createDirs(topath);
                }

                Path FROM = Paths.get(i.getPath());
                Path TO = Paths.get(topath + i.getName());
                CopyOption[] options = new CopyOption[]{
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES
                };

                try {
                    Files.copy(FROM, TO, options);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (isCut) {
                    File file = new File(i.getPath());
                    file.delete();
                }
                deleteList.add(i);

            /*if(!FileHandler.fileExist(topath + i.getName())) {
                i.setPath(topath + i.getName());
                i.setParentPath(topath);
                dataManager.getAllImageObjects().add(i);
                i.setFixed(true);
            }*/

                //System.out.println("pathes: " + topath + i.getName());
            }
            index++;
        }
        for(ImageObject i : deleteList) {
            dataManager.getDisplayedImageObjects().remove(i);
            dataManager.getTempImages().remove(i);
        }
    }

    private String buildTagFolder(ImageObject i) {
        StringBuilder tagName = new StringBuilder();
        for(int s = 0; s < i.getTagObjects().size(); s++) {
            tagName.append(i.getTagObjects().get(s).getName());
            if(i.getTagObjects().size() - 1 != s) {
                tagName.append("_");
            }
        }
        return tagName.toString();
    }

    private String buildSubTagFolder(ImageObject i) {
        StringBuilder tagName = new StringBuilder();
        for(int s = 0; s < i.getSubTagObjects().size(); s++) {
            tagName.append(i.getSubTagObjects().get(s).getName());
            if(i.getSubTagObjects().size() - 1 != s) tagName.append("_");
        }
        return tagName.toString();
    }

    private void createFolderStructure(String path) {

    }

}
