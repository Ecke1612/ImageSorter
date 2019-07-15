package logic;

import file_handling.FileHandler;
import file_handling.StoreData;
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

    public void sortAndSaveFiles(ArrayList<ImageObject> imageObjects, boolean isMonthly, boolean isTags, boolean isSubtags, DataManager dataManager) {
        for(ImageObject i : imageObjects) {
            String month = "";
            String tag = "";
            String subTag = "";

            if(isMonthly) month = i.getStringMonth() + "\\";
            if(isTags) tag = buildTagFolder(i) + "\\";
            if(isSubtags) subTag = buildSubTagFolder(i) + "\\";

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

            System.out.println("pathes: " + topath + i.getName());

            i.setPath(topath + i.getName());
            i.setParentPath(topath);
            dataManager.getAllImageObjects().add(i);
            i.setFixed(true);

            try {
                Files.copy(FROM, TO, options);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
