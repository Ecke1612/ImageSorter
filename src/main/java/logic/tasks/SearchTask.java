package logic.tasks;

import javafx.concurrent.Task;
import logic.dataholder.ImageObject;
import presentation.dataholder.SimpleTagObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchTask extends Task {

    private String searchString1;
    private String searchString2;
    private String searchString3;
    private String searchString4;
    private HashMap<String, ImageObject> map;

    public SearchTask(String searchString1, String searchString2, String searchString3, String searchString4, HashMap<String, ImageObject> map) {
        this.searchString1 = searchString1;
        this.searchString2 = searchString2;
        this.searchString3 = searchString3;
        this.searchString4 = searchString4;
        this.map = map;
    }

    @Override
    protected HashMap<String, ImageObject> call() throws Exception {
        HashMap<String, ImageObject> resultList = new HashMap<>();
        if(!searchString1.equals("") && map.size() > 0) {
            resultList = search(searchString1, map);
            if(!searchString2.equals("") && resultList.size() > 0) {
                resultList = search(searchString2, resultList);
                if(!searchString3.equals("") && resultList.size() > 0) {
                    resultList = search(searchString3, resultList);
                    if(!searchString4.equals("") && resultList.size() > 0) {
                        resultList = search(searchString4, resultList);
                    }
                }
            }
        }
        return resultList;
    }

    private HashMap<String, ImageObject> search(String str, HashMap<String, ImageObject> map) {
        HashMap<String, ImageObject> resultMap = new HashMap<>();
        for(ImageObject i : map.values()) {
            for(SimpleTagObject t : i.getTagObjects()) {
                if(t.getName().toLowerCase().startsWith(str.toLowerCase())) {
                    if(!resultMap.containsValue(i)) {
                        resultMap.put(i.getPath(), i);
                    }
                }
            }
            for(SimpleTagObject t : i.getSubTagObjects()) {
                if(t.getName().toLowerCase().startsWith(str.toLowerCase())) {
                    if(!resultMap.containsValue(i)) {
                        resultMap.put(i.getPath(), i);
                    }
                }
            }
        }
        return resultMap;
    }

    public void setSearchString1(String searchString1) {
        this.searchString1 = searchString1;
    }

    public void setSearchString2(String searchString2) {
        this.searchString2 = searchString2;
    }

    public void setSearchString3(String searchString3) {
        this.searchString3 = searchString3;
    }

    public void setSearchString4(String searchString4) {
        this.searchString4 = searchString4;
    }

    public void setMap(HashMap<String, ImageObject> map) {
        this.map = map;
    }
}
