package objects;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ImageObject {

    private String name;
    private LocalDateTime date;
    private String path;
    private ArrayList<SimpleTagObject> tagObjects = new ArrayList<>();
    private ArrayList<SimpleTagObject> subTagObjects = new ArrayList<>();

    public ImageObject(String name, LocalDateTime date, String path) {
        this.name = name;
        this.date = date;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<SimpleTagObject> getTagObjects() {
        return tagObjects;
    }

    public ArrayList<SimpleTagObject> getSubTagObjects() {
        return subTagObjects;
    }

    public String getStringYear() {
        String year = String.valueOf(date.getYear());
        System.out.println(name + ": " + year);
        return year;
    }

    public String getStringMonth() {
        //System.out.println("name: " + name);
        //System.out.println(String.valueOf(date.getMonth()));
        String month = String.valueOf(date.getMonth().getValue() + " " + date.getMonth());
        return month;
    }
}
