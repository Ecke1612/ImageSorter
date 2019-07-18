package logic;

import file_handling.FileHandler;
import file_handling.StoreData;
import gui.controller.ImageObjectController;
import gui.controller.MediaObjectController;
import gui.dialog.Dialogs;
import objects.ImageObject;
import objects.SimpleTagObject;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FileSorter {

    private StoreData storeData;
    private Dialogs dialogs = new Dialogs();

    public FileSorter(StoreData storeData) {
        this.storeData = storeData;
    }

    public void sortAndSaveFiles(ArrayList<ImageObject> imageObjects, ArrayList<MediaObjectController> mediaObjectControllers,
                                 boolean isMonthly, boolean isTags, boolean isSubtags, boolean isCut, DataManager dataManager, boolean move) {
        int index = 0;
        ArrayList<ImageObject> deleteList = new ArrayList<>();
        for (ImageObject i : imageObjects) {
            boolean execute = false;
            if (move) {
                if (mediaObjectControllers.get(index).checkbox.isSelected()) {
                    execute = true;
                    System.out.println("execute because of move with check");
                }
            } else {
                if (mediaObjectControllers.get(index).getImageObject().getTagObjects().size() > 0 || mediaObjectControllers.get(index).getImageObject().getSubTagObjects().size() > 0
                        || mediaObjectControllers.get(index).checkbox.isSelected()) {
                     execute = true;
                    System.out.println("execute because of check or tags with no move");
                 }
            }

            if (execute) {
                String month = "";
                String tag = "";
                String subTag = "";
                String movie = "";

                if (isMonthly) month = i.getStringMonth() + "\\";
                if (isTags) tag = buildTagFolder(i.getTagObjects()) + "\\";
                if (isSubtags) subTag = buildTagFolder(i.getSubTagObjects()) + "\\";
                if(i.isMovie()) movie = "Videos\\";

                String topath = AccountManager.getActiveAccount().getPath() + "\\" + AccountManager.getActiveAccount().getName() + "'s Bilder\\" + i.getStringYear() + "\\" + tag + month + subTag + movie;

                if (!FileHandler.fileExist(topath)) {
                    FileHandler.createDirs(topath);
                }

                String originalFilePath = i.getPath();
                if (FileHandler.fileExist(topath + i.getName())) {
                    int remoteIndex = -1;
                    int counter = 0;
                    for (ImageObject ri : dataManager.getAllImageObjects()) {
                        if (i.getName().equals(ri.getName())) {
                            remoteIndex = counter;
                        }
                        counter++;
                    }
                    if (remoteIndex >= 0) {
                        boolean replace = dialogs.fileAlreadyExistDialog(i.getPath(), dataManager.getAllImageObjects().get(remoteIndex).getPath());
                        if (!replace) {
                            Random r = new Random();
                            int number = r.nextInt(1000);
                            String[] newName = i.getName().split("\\.");
                            i.setName(newName[0] + "_" + number + "." + newName[1]);
                        }
                    }
                }

                Path FROM = Paths.get(i.getPath());
                Path TO = Paths.get(topath + i.getName());
                CopyOption[] options = new CopyOption[]{
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.COPY_ATTRIBUTES,
                };

                if (!FileHandler.fileExist(topath + i.getName()) && !move) {
                    i.setPath(topath + i.getName());
                    i.setParentPath(topath);
                    dataManager.getAllImageObjects().add(i);
                    i.setFixed(true);
                } else if(!FileHandler.fileExist(topath + i.getName()) && move) {
                    boolean isMovie = false;
                    if (i.getName().toLowerCase().endsWith("mp4")) isMovie = true;
                    ImageObject newI = new ImageObject(i.getName(), i.getDate(), topath + i.getName(), topath, true, isMovie);
                    newI.setTagObjects(i.getTagObjects());
                    newI.setSubTagObjects(i.getSubTagObjects());
                    dataManager.getAllImageObjects().add(newI);
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
            }
            index++;
        }
        for(ImageObject i : deleteList) {
            dataManager.getDisplayedImageObjects().remove(i);
            dataManager.getTempImages().remove(i);
        }
    }


    public String deleteLastChar(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == '\\') {
            str = str.substring(0, str.length() - 1);
            System.out.println("returned: " + str);
        }
        return str;
    }

    private String buildTagFolder(ArrayList<SimpleTagObject> taglist) {
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

    private void createFolderStructure(String path) {

    }

}
