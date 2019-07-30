package logic;

import file_handling.FileHandler;
import file_handling.StoreData;
import gui.controller.MediaObjectController;
import gui.controller.MovieObjectController;
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
                String originalName = i.getName();

                //ImageObject sortieren für Temporäre Dateien (Ersetzen

                ExistObject existObject = checkIfFileExist(topath, i, dataManager);
                if(existObject.isAlreadyThere()) {
                    if (!existObject.isReplace()) {
                        changeNameOfImageObjectRandomly(i);
                    }
                }

                String fullToPath = topath + i.getName();
                File fileTo = new File(fullToPath);


                if(!fileTo.exists() && !move) {
                    System.out.println("TEMP beide behalten: path ändern und zu allImage");
                    i.setPath(fullToPath);
                    i.setParentPath(topath);
                    dataManager.getAllImageObjects().add(i);
                    i.setFixed(true);

                    copyFile(originalFilePath, fullToPath, i, mediaObjectControllers.get(index));

                }else if(fileTo.exists() && !move) {
                    System.out.println("TEMP Ersetzen daher RemotI kopieren");
                    int remoteIndex = getRemoteImageObject(i, dataManager);
                    if(remoteIndex >= 0) {
                        ImageObject remoteI = dataManager.getAllImageObjects().get(remoteIndex);
                        copyImageObject(i, remoteI, fullToPath, topath);

                        copyFile(i.getPath(), fullToPath, i, mediaObjectControllers.get(index));
                    } else System.out.println("no remoteI found");



                } else if(!fileTo.exists() && move && !isCut) {
                    System.out.println("Beide behalten, move und nicht schneiden");
                    boolean isMovie = false;
                    if (i.getName().toLowerCase().endsWith("mp4")) isMovie = true;
                    ImageObject newI = new ImageObject(i.getName(), i.getDate(), topath + i.getName(), topath, true, isMovie);
                    newI.getTagObjects().addAll(i.getTagObjects());
                    newI.getSubTagObjects().addAll(i.getSubTagObjects());
                    dataManager.getAllImageObjects().add(newI);
                    i.setName(originalName);

                    copyFile(i.getPath(), fullToPath, i, mediaObjectControllers.get(index));

                } else if(fileTo.exists() && move && !isCut) {
                    System.out.println("Ersetzen, daher remoteI überschreiben ohne I zu löschen");
                    int remoteIndex = getRemoteImageObject(i, dataManager);
                    if(remoteIndex >= 0) {
                        ImageObject remoteI = dataManager.getAllImageObjects().get(remoteIndex);
                        copyImageObject(i, remoteI, fullToPath, topath);

                        copyFile(i.getPath(), fullToPath, i, mediaObjectControllers.get(index));
                    } else System.out.println("no remoteI found");



                } else if(!fileTo.exists() && move && isCut){
                    System.out.println("Beide behalten: move & isCut = pfad und parentpath ändern");
                    i.setPath(fullToPath);
                    i.setParentPath(topath);

                    moveFile(originalFilePath, fullToPath, i, mediaObjectControllers.get(index));
                } else if(fileTo.exists() && move && isCut) {
                    System.out.println("Ersetzen: move & isCut = kopiere zu remotI");
                    int remoteIndex = getRemoteImageObject(i, dataManager);
                    if(remoteIndex >= 0) {
                        ImageObject remoteI = dataManager.getAllImageObjects().get(remoteIndex);
                        copyImageObject(i, remoteI, fullToPath, topath);

                        moveFile(i.getPath(), fullToPath, i, mediaObjectControllers.get(index));
                        dataManager.getAllImageObjects().remove(i);
                    } else System.out.println("no remoteI found");
                }


         /*
                if (!FileHandler.fileExist(topath + i.getName()) && !move) {
                    System.out.println("!move = path ändern und zu allImage");
                    i.setPath(topath + i.getName());
                    i.setParentPath(topath);
                    dataManager.getAllImageObjects().add(i);
                    i.setFixed(true);
                } else if(!FileHandler.fileExist(topath + i.getName()) && move && !isCut) {
                    System.out.println("move && !cut = new ImageObject und zu AllImage");
                    boolean isMovie = false;
                    if (i.getName().toLowerCase().endsWith("mp4")) isMovie = true;
                    ImageObject newI = new ImageObject(i.getName(), i.getDate(), topath + i.getName(), topath, true, isMovie);
                    newI.getTagObjects().addAll(i.getTagObjects());
                    newI.getSubTagObjects().addAll(i.getSubTagObjects());
                    dataManager.getAllImageObjects().add(newI);
                } else if(move && isCut) {
                    System.out.println("move & cut = pfad und parentpath ändern");
                    i.setPath(topath + i.getName());
                    i.setParentPath(topath);
                    //dataManager.getAllImageObjects().remove(i);
                }*/


                deleteList.add(i);
            }
            index++;
        }
        for(ImageObject i : deleteList) {
            boolean reDis = dataManager.getDisplayedImageObjects().remove(i);
            boolean reTemp = dataManager.getTempImages().remove(i);
        }
    }

    private void copyImageObject(ImageObject i, ImageObject remoteI, String fullToPath, String topath) {
        remoteI.setName(i.getName());
        remoteI.setPath(fullToPath);
        remoteI.setParentPath(topath);
        remoteI.getTagObjects().clear();
        remoteI.getTagObjects().addAll(i.getTagObjects());
        remoteI.getSubTagObjects().clear();
        remoteI.getSubTagObjects().addAll(i.getSubTagObjects());
    }

    private void copyFile(String from, String to, ImageObject i, MediaObjectController mediaObjectController) {
        //Path FROM = Paths.get(i.getPath());
        //Path TO = Paths.get(fullToPath);
        disposeMedia(i, mediaObjectController);
        Path FROM = Paths.get(from);
        Path TO = Paths.get(to);
        CopyOption[] optionsCopy = new CopyOption[]{
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES
        };
        try {
            Files.copy(FROM, TO, optionsCopy);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void moveFile(String from, String to, ImageObject i, MediaObjectController mediaObjectController) {
        //Path FROM = Paths.get(i.getPath());
        //Path TO = Paths.get(fullToPath);
        disposeMedia(i, mediaObjectController);
        Path FROM = Paths.get(from);
        Path TO = Paths.get(to);
        CopyOption[] optionsCopy = new CopyOption[]{
                StandardCopyOption.REPLACE_EXISTING
        };
        try {
            Files.move(FROM, TO, optionsCopy);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disposeMedia(ImageObject i, MediaObjectController mediaObjectController) {
        if(i.isMovie()) {
            MovieObjectController m = (MovieObjectController) mediaObjectController;
            m.resetMedia();
        }
    }

    private ExistObject checkIfFileExist(String fullPath, ImageObject i, DataManager dataManager) {
        if (FileHandler.fileExist(fullPath)) {
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

    private int getRemoteImageObject(ImageObject i, DataManager dataManager) {
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

    private void changeNameOfImageObjectRandomly(ImageObject i) {
        Random r = new Random();
        int number = r.nextInt(1000);
        String[] newName = i.getName().split("\\.");
        i.setName(newName[0] + "_" + number + "." + newName[1]);
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
