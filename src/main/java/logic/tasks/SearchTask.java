package logic.tasks;

import javafx.concurrent.Task;
import objects.ImageObject;
import objects.SimpleTagObject;

import java.util.ArrayList;

public class SearchTask extends Task {

    private String searchString1;
    private String searchString2;
    private String searchString3;
    private String searchString4;
    private ArrayList<ImageObject> list;

    public SearchTask(String searchString1, String searchString2, String searchString3, String searchString4, ArrayList<ImageObject> list) {
        this.searchString1 = searchString1;
        this.searchString2 = searchString2;
        this.searchString3 = searchString3;
        this.searchString4 = searchString4;
        this.list = list;
    }

    @Override
    protected ArrayList<ImageObject> call() throws Exception {
        ArrayList<ImageObject> resultList = new ArrayList<>();
        if(!searchString1.equals("") && list.size() > 0) {
            resultList = search(searchString1, list);
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

    private ArrayList<ImageObject> search(String str, ArrayList<ImageObject> list) {
        ArrayList<ImageObject> resultList = new ArrayList<>();
        for(ImageObject i : list) {
            for(SimpleTagObject t : i.getTagObjects()) {
                if(t.getName().toLowerCase().startsWith(str.toLowerCase())) {
                    if(!resultList.contains(i)) {
                        resultList.add(i);
                    }
                }
            }
            for(SimpleTagObject t : i.getSubTagObjects()) {
                if(t.getName().toLowerCase().startsWith(str.toLowerCase())) {
                    if(!resultList.contains(i)) {
                        resultList.add(i);
                    }
                }
            }
        }
        return resultList;
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

    public void setList(ArrayList<ImageObject> list) {
        this.list = list;
    }
}
