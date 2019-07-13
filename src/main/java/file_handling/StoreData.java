package file_handling;

import javafx.scene.paint.Color;
import logic.AccountManager;
import logic.DataManager;
import main.Main;
import objects.AccountObject;
import objects.ImageObject;
import objects.SimpleTagObject;

import java.util.ArrayList;
import java.util.Arrays;

public class StoreData {

    DataManager dataManager;

    public StoreData(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void writeInitData() {
        ArrayList<String> data = new ArrayList<>();
        data.add(String.valueOf(Main.initData.getActiveAccount()));
        FileHandler.fileWriterNewLine(Main.parentPath + "init.dat", data);
    }

    public InitData loadInitData() {
        ArrayList<String> data = FileHandler.fileLoader(Main.parentPath + "init.dat");
        InitData initData = new InitData();
        initData.setActiveAccount(Integer.parseInt(data.get(0)));
        return initData;
    }

    public void writeAccountData() {
         ArrayList<String> data = new ArrayList<>();
         int count = 0;
         for(AccountObject a : AccountManager.accountObjects) {
             data.add("accnew");
             //data.add(String.valueOf(count));
             data.add(a.getName());
             data.add(a.getPath());
             count++;
         }
         FileHandler.fileWriterNewLine(Main.parentPath + "acc.dat", data);
    }

    public void loadAccountData() {
        ArrayList<String> data = FileHandler.fileLoader(Main.parentPath + "acc.dat");
        for(int i = 0; i < data.size(); i++) {
            if(data.get(i).equals("accnew")) {
                AccountManager.addNewAccount(new AccountObject(data.get(i + 1), data.get(i + 2)));
            }
        }
        System.out.println("accounts loaded");
    }

    public void storeTagData() {
        ArrayList<String> data = new ArrayList<>();
        for(SimpleTagObject t : dataManager.getTagObjects()) {
            data.add("tagnew");
            data.add(t.getName());
            data.add(t.getColor().toString());
        }
        data.add("subdata");
        for(SimpleTagObject t : dataManager.getSubTagObjects()) {
            data.add("subnew");
            data.add(t.getName());
            data.add(t.getColor().toString());
        }
        FileHandler.fileWriterNewLine(Main.parentPath + "tagdata.dat", data);
        System.out.println("tag data stored");
    }

    public void loadTagData() {
        ArrayList<String> data = FileHandler.fileLoader(Main.parentPath + "tagdata.dat");
        for(int i = 0; i < data.size(); i++) {
            if(data.get(i).equals("tagnew")) {
                dataManager.getTagObjects().add(new SimpleTagObject(data.get(i + 1), Color.valueOf(data.get(i + 2))));
            }
            if(data.get(i).equals("subnew")) {
                dataManager.getSubTagObjects().add(new SimpleTagObject(data.get(i + 1), Color.valueOf(data.get(i + 2))));
            }
        }
        System.out.println("tag data loaded");
    }

    public void storeImageData(String path, ArrayList<ImageObject> imageList) {
        System.out.println("save Img Data: " + path);
        if(FileHandler.fileExist(path)) {
            ArrayList<String> data = new ArrayList<>();
            for (ImageObject i : imageList) {
                for (SimpleTagObject t : i.getTagObjects()) {
                    data.add("tagN:::" + t.getName() + ":::" + t.getColor().toString());
                }
                for (SimpleTagObject t : i.getSubTagObjects()) {
                    data.add("subtagN:::" + t.getName() + ":::" + t.getColor().toString());
                }
                data.add("newImage_");
            }
            FileHandler.fileWriterNewLine(path + "imgdata.dat", data);
            FileHandler.hideFile(path + "imgdata.dat");
        }
    }

    public void loadImageData(String path) {
        if(FileHandler.fileExist(path + "\\imgdata.dat")) {
            System.out.println("loadImgData");
            ArrayList<String> data = FileHandler.fileLoader(path + "\\imgdata.dat");
            int index = 0;
            for (String s : data) {
                if (s.equals("newImage_")) {
                    index++;
                } else {
                    String[] strray = s.split(":::");
                    String key = strray[0];
                    if (key.equals("tagN")) {
                        dataManager.getImageObjects().get(index).getTagObjects().add(new SimpleTagObject(strray[1], Color.valueOf(strray[2])));
                    } else if (key.equals("subtagN")) {
                        dataManager.getImageObjects().get(index).getSubTagObjects().add(new SimpleTagObject(strray[1], Color.valueOf(strray[2])));
                    } else {
                        System.out.println("something went wrong: ");
                        System.out.println(Arrays.toString(strray));
                    }
                }
            }
        }
    }

    public void saveAllData() {
        storeTagData();
    }

}
