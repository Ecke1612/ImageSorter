package persistentData.file_handling;

import com.ed.filehandler.JsonHandler;
import persistentData.debugData.LogFile;
import persistentData.debugData.Stats;
import javafx.scene.paint.Color;
import logic.AccountManager;
import logic.DataManager;
import main.Main;
import logic.dataholder.AccountObject;
import logic.dataholder.ImageObject;
import logic.dataholder.ImageVerifyObject;
import presentation.dataholder.SimpleTagObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import logic.net.FTP_Handler;
import com.ed.filehandler.PlainHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class ReadWriteAppData {

    private DataManager dataManager;
    private PlainHandler plainHandler = new PlainHandler();
    private JsonHandler jsonHandler = new JsonHandler();

    public ReadWriteAppData(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void storeInitData() {
        JSONObject initObj = new JSONObject();
        initObj.put("activeAccount", String.valueOf(AccountManager.getActiveAcountIndex()));
        initObj.put("width", (int)(Main.primaryStage.getScene().getWidth()));
        initObj.put("height", (int)Main.primaryStage.getScene().getHeight());

        jsonHandler.writeJsonData(initObj, Main.parentPath + "\\" + "init.dat");
    }

    public InitData loadInitData() {
        InitData initData = new InitData();
        try {
            JSONObject initObj = jsonHandler.readJsonData(Main.parentPath + "\\" + "init.dat");
            initData.setActiveAccount(Integer.parseInt(initObj.get("activeAccount").toString()));
            initData.setWidth(Integer.parseInt(initObj.get("width").toString()));
            initData.setHeight(Integer.parseInt(initObj.get("height").toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return initData;
    }

    public void writeAccountData() {
        ArrayList<String> data = new ArrayList<>();
        for (AccountObject a : AccountManager.accountObjects) {
            data.add("accnew");
            data.add(a.getName());
            data.add(a.getPath());
        }
        plainHandler.fileWriterNewLine(Main.parentPath + "acc.dat", data);
    }

    public void loadAccountData() {
        ArrayList<String> data = plainHandler.fileLoader(Main.parentPath + "acc.dat");
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).equals("accnew")) {
                AccountManager.addNewAccount(new AccountObject(data.get(i + 1), data.get(i + 2)));
            }
        }
        System.out.println("accounts loaded");
    }

    public void storeTagData() {
        ArrayList<String> data = new ArrayList<>();
        for (SimpleTagObject t : dataManager.getTagObjects()) {
            data.add("tagnew");
            data.add(t.getName());
            data.add(t.getColor().toString());
        }
        data.add("subdata");
        for (SimpleTagObject t : dataManager.getSubTagObjects()) {
            data.add("subnew");
            data.add(t.getName());
            data.add(t.getColor().toString());
        }
        plainHandler.fileWriterNewLine(Main.parentPath + AccountManager.getActiveAccount().getName() + "\\tagdata.dat", data);
        System.out.println("tag data stored");
    }

    public void loadTagData() {
        ArrayList<String> data = plainHandler.fileLoader(Main.parentPath + AccountManager.getActiveAccount().getName() + "\\tagdata.dat");
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).equals("tagnew")) {
                dataManager.getTagObjects().add(new SimpleTagObject(data.get(i + 1), Color.valueOf(data.get(i + 2))));
            }
            if (data.get(i).equals("subnew")) {
                dataManager.getSubTagObjects().add(new SimpleTagObject(data.get(i + 1), Color.valueOf(data.get(i + 2))));
            }
        }
        System.out.println("tag data loaded");
    }

    public void storeImageData(HashMap<String, ImageObject> storelist, String filename) {
        System.out.println("write Image Data");
        JSONObject mainobj = new JSONObject();
        JSONArray dataArray = new JSONArray();
        mainobj.put("data", dataArray);
        for (ImageObject i : storelist.values()) {
            JSONObject imgObj = new JSONObject();
            JSONArray tagArray = new JSONArray();
            JSONArray subTagArray = new JSONArray();

            for(SimpleTagObject t : i.getTagObjects()) {
                JSONObject tagObj = new JSONObject();
                tagObj.put("tagname", t.getName());
                tagObj.put("tagcolor", t.getColor().toString());
                tagArray.add(tagObj);
            }

            for(SimpleTagObject t : i.getSubTagObjects()) {
                JSONObject subtagObj = new JSONObject();
                subtagObj.put("tagname", t.getName());
                subtagObj.put("tagcolor", t.getColor().toString());
                subTagArray.add(subtagObj);
            }

            imgObj.put("name", i.getName());
            imgObj.put("path", i.getPath());
            imgObj.put("parentpath", i.getParentPath());
            imgObj.put("date", i.getDate().toString());
            imgObj.put("isMovie", i.isMovie());
            imgObj.put("tag", tagArray);
            imgObj.put("subtag", subTagArray);
           dataArray.add(imgObj);
        }
        jsonHandler.writeJsonData(mainobj, Main.parentPath + "\\" + AccountManager.getActiveAccount().getName() + "\\" + filename);
    }

    public void loadImageData(String fileName, boolean isTempList) {
        if(plainHandler.fileExist(Main.parentPath + AccountManager.getActiveAccount().getName() + "\\" + fileName)) {
            System.out.println("load image data");
            JSONObject mainObj = jsonHandler.readJsonData(Main.parentPath + "\\" + AccountManager.getActiveAccount().getName() + "\\" + fileName);
            JSONArray dataArray = (JSONArray) mainObj.get("data");
            for(int i = 0; i < dataArray.size(); i++) {
                JSONObject imgObj = (JSONObject) dataArray.get(i);
                JSONArray tagArray = (JSONArray) imgObj.get("tag");
                JSONArray subTagArray = (JSONArray) imgObj.get("subtag");

                ArrayList<SimpleTagObject> tagList = new ArrayList<>();
                for(int t = 0; t < tagArray.size(); t++) {
                    JSONObject tagObj = (JSONObject) tagArray.get(t);
                    for(SimpleTagObject st : dataManager.getTagObjects()) {
                       if(st.getName().equals(tagObj.get("tagname").toString()) &&
                               st.getColor().toString().equals(tagObj.get("tagcolor").toString())) {
                           tagList.add(st);
                       }
                    }
                }

                ArrayList<SimpleTagObject> subTagList = new ArrayList<>();
                for(int t = 0; t < subTagArray.size(); t++) {
                    JSONObject subTagObj = (JSONObject) subTagArray.get(t);
                    for(SimpleTagObject st : dataManager.getSubTagObjects()) {
                        if(st.getName().equals(subTagObj.get("tagname").toString()) &&
                                st.getColor().toString().equals(subTagObj.get("tagcolor").toString())) {
                            subTagList.add(st);
                        }
                    }
                }

                String name = imgObj.get("name").toString();
                String path = imgObj.get("path").toString();
                String parentPath = null;
                LocalDateTime date = null;
                Boolean isMovie = null;
                try {
                    parentPath = imgObj.get("parentpath").toString();
                    date = LocalDateTime.parse(imgObj.get("date").toString());
                    isMovie = Boolean.valueOf(imgObj.get("isMovie").toString());
                } catch(Exception e) {
                    e.printStackTrace();
                }

                if(!isTempList) {
                    ImageObject imageObject = verifyImageMapData(path);
                    if(imageObject != null) {
                        imageObject.setTagObjects(tagList);
                        imageObject.setSubTagObjects(subTagList);
                    } else System.out.println("image map not verified");
                } else {
                    if(parentPath != null && date != null && isMovie != null) {
                        if (plainHandler.fileExist(path)) {
                            ImageObject imageObject = new ImageObject(name, date, path, parentPath, false, isMovie);
                            imageObject.setTagObjects(tagList);
                            imageObject.setSubTagObjects(subTagList);
                            dataManager.getTempImages().put(imageObject.getPath(), imageObject);
                            System.out.println("tempimage added");
                        }
                    }
                }
            }
        }
    }

    private ImageObject verifyImageMapData(String path) {
        if(dataManager.getAllImageObjectsMap().containsKey(path)) {
            return dataManager.getAllImageObjectsMap().get(path);
        } else return null;
    }

    public void storeLog() {
        plainHandler.fileWriterNewLine(Main.parentPath + AccountManager.getActiveAccount().getName() + "\\log.txt", LogFile.logfiles);
    }

    public void storeStats() {
        JSONObject statObj = new JSONObject();
        statObj.put("startcounter", String.valueOf(Stats.startCount));
        jsonHandler.writeJsonData(statObj,Main.parentPath + "\\" +  AccountManager.getActiveAccount().getName() + "\\stats.dat");
        System.out.println("stats saved");
    }

    public void loadStats() {
        if(plainHandler.fileExist(Main.parentPath + AccountManager.getActiveAccount().getName() + "\\" + "stats.dat")) {
            JSONObject statObj = (JSONObject) jsonHandler.readJsonData(Main.parentPath + "\\" + AccountManager.getActiveAccount().getName() + "\\stats.dat");
            Stats.startCount = Integer.parseInt(statObj.get("startcounter").toString());
            Stats.startCount++;
            System.out.println("startcounter: " + Stats.startCount);
        }
    }

    public void saveAllData(boolean upload) {
        storeTagData();
        storeImageData(dataManager.getAllImageObjectsMap(), "imgdata.dat");
        storeImageData(dataManager.getTempImages(), "tempimgdata.dat");
        storeInitData();
        storeLog();
        storeStats();
        if(upload) uploadData();
    }

    private void uploadData() {
        try {
            FTP_Handler ftp_handler = new FTP_Handler("ecke1612.bplaced.net", "ecke1612_interval", "8h6AszzvM9SjzEhB");
            ftp_handler.uploadFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
