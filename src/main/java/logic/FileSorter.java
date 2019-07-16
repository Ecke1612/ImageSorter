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

    public FileSorter(StoreData storeData) {
        this.storeData = storeData;
    }

    public void sortAndSaveFiles(ArrayList<ImageObject> imageObjects, ArrayList<ImageObjectController> imageObjectControllers, boolean isMonthly, boolean isTags, boolean isSubtags, boolean isCut, DataManager dataManager) {
        int index = 0;
        ArrayList<ImageObject> deleteList = new ArrayList<>();
        for(ImageObject i : imageObjects) {
            if (imageObjectControllers.get(index).checkbox.isSelected() || imageObjectControllers.get(index).getImageObject().getTagObjects().size() > 0
                    || imageObjectControllers.get(index).getImageObject().getSubTagObjects().size() > 0) {
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

                String originalFilePath = i.getPath();

                Path FROM = Paths.get(i.getPath());
                Path TO = Paths.get(topath + i.getName());
                CopyOption[] options = new CopyOption[]{
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES
                };

                if(!FileHandler.fileExist(topath + i.getName())) {
                    i.setPath(topath + i.getName());
                    i.setParentPath(topath);
                    dataManager.getAllImageObjects().add(i);
                    i.setFixed(true);
                }

                try {
                    Files.copy(FROM, TO, options);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (isCut) {
                    File file = new File(originalFilePath);
                    file.delete();
                }
                deleteList.add(i);

                //System.out.println("pathes: " + topath + i.getName());
            }
            index++;
        }
        System.out.println("remove temp befor: " + dataManager.getTempImages().size());
        for(ImageObject i : deleteList) {
            dataManager.getDisplayedImageObjects().remove(i);
            dataManager.getTempImages().remove(i);
        }
        System.out.println("remove temp after: " + dataManager.getTempImages().size());
    }

    public String deleteLastChar(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == '\\') {
            str = str.substring(0, str.length() - 1);
            System.out.println("returned: " + str);
        }
        return str;
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
