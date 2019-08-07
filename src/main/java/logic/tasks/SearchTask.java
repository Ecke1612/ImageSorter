package logic.tasks;

import javafx.concurrent.Task;
import logic.dataholder.ImageObject;
import presentation.dataholder.SimpleTagObject;

import java.util.HashMap;

public class SearchTask extends Task {

    private String s1 = "";
    private String s2 = "";
    private String s3 = "";
    private String s4 = "";
    private HashMap<String, ImageObject> map;

    public SearchTask(HashMap<String, ImageObject> map) {
        this.map = map;
    }

    @Override
    protected HashMap<String, ImageObject> call() throws Exception {
        System.out.println("s1: " + s1 + "; s2: " + s2 + "; s3: " + s3 + "; s4: " + s4);
        HashMap<String, ImageObject> resultList = new HashMap<>();
        if(!s1.equals("") && map.size() > 0) {
            resultList = search(s1, map);
            if(!s2.equals("") && resultList.size() > 0) {
                resultList = search(s2, resultList);
                if(!s3.equals("") && resultList.size() > 0) {
                    resultList = search(s3, resultList);
                    if(!s4.equals("") && resultList.size() > 0) {
                        resultList = search(s4, resultList);
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

    public void setS1(String s1) {
        this.s1 = s1;
        try {
            call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setS2(String s2) {
        this.s2 = s2;
        try {
            call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setS3(String s3) {
        this.s3 = s3;
        try {
            call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setS4(String s4) {
        this.s4 = s4;
        try {
            call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMap(HashMap<String, ImageObject> map) {
        this.map = map;
    }
}
