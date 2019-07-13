package file_handling;

import javafx.scene.paint.Color;
import logic.AccountManager;
import logic.DataManager;
import main.Main;
import objects.AccountObject;
import objects.ImageObject;
import objects.SimpleTagObject;
import objects.TagObject;

import java.util.ArrayList;

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
        for(TagObject t : dataManager.getTagObjects()) {
            data.add("tagnew");
            data.add(t.getName());
            data.add(t.getColor().toString());
        }
        data.add("subdata");
        for(TagObject t : dataManager.getSubTagObjects()) {
            data.add("subnew");
            data.add(t.getName());
            data.add(t.getColor().toString());
        }
        FileHandler.fileWriterNewLine(Main.parentPath + "tagdata.dat", data);
        System.out.println("tag data stored");
    }

    public void loadTagData() {
        ArrayList<String> data = FileHandler.fileLoader(Main.parentPath + "tagdata.dat");
        int tagindex = 0;
        int subtagindex = 0;
        for(int i = 0; i < data.size(); i++) {
            if(data.get(i).equals("tagnew")) {
                dataManager.getTagObjects().add(new TagObject(tagindex, data.get(i + 1), Color.valueOf(data.get(i + 2))));
                tagindex++;
            }
            if(data.get(i).equals("subnew")) {
                dataManager.getSubTagObjects().add(new TagObject(subtagindex, data.get(i + 1), Color.valueOf(data.get(i + 2))));
                subtagindex++;
            }
        }
        System.out.println("tag data loaded");
    }

    public  void storeTagsForImages() {
        ArrayList<String> data = new ArrayList<>();
        for(ImageObject i : dataManager.getImageObjects()) {
            data.add("newImage_");
            data.add("tagdata_");
            for(SimpleTagObject t : i.getTagObjects()) {
                data.add(t.getName());
            }
            data.add("subtagdata_");
            for(String t : i.getSubNameTagObjects()) {
                data.add(t);
            }
        }
        FileHandler.fileWriterNewLine(Main.parentPath + "imgdata.dat", data);
        FileHandler.hideFile(Main.parentPath + "imgdata.dat");
    }

    public void loadTagsForImages() {
        ArrayList<String> data = FileHandler.fileLoader(Main.parentPath + "imgdata.dat");
        int index = 0;
        int tagindex = 0;
        int subtagindex = 0;
        boolean tagData = false;
        boolean subData = false;
        for(String s : data) {
            if(s.equals("newImage_"))  {
                tagData = false;
                subData = false;
                index++;
            } else if(s.equals("tagdata_")) {
                tagData = true;
                subData = false;
            } else if(s.equals("subtagdata_")) {
                tagData = false;
                subData = true;
            } else {
                if(tagData) {
                    //dataManager.getImageObjects().get(index).getTagObjects().add(s, Color.WHITE);
                }
            }

        }
    }

    public void saveAllData() {
        storeTagData();
    }

}
