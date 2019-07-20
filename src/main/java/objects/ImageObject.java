package objects;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ImageObject {

    private String name;
    private LocalDateTime date;
    private String path;
    private String parentPath;
    private ArrayList<SimpleTagObject> tagObjects = new ArrayList<>();
    private ArrayList<SimpleTagObject> subTagObjects = new ArrayList<>();
    private boolean fixed;
    private boolean isMovie;

    public ImageObject(String name, LocalDateTime date, String path, String parentPath, Boolean fixed, boolean isMovie) {
        this.name = name;
        this.date = date;
        this.path = path;
        this.parentPath = parentPath;
        this.fixed = fixed;
        this.isMovie = isMovie;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
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

    public String getParentPath() {
        return parentPath;
    }

    public String getStringMonth() {
        String month = String.valueOf(date.getMonth().getValue() + " " + date.getMonth());
        return month;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTagObjects(ArrayList<SimpleTagObject> tagObjects) {
        this.tagObjects = tagObjects;
    }

    public void setSubTagObjects(ArrayList<SimpleTagObject> subTagObjects) {
        this.subTagObjects = subTagObjects;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public boolean isMovie() {
        return isMovie;
    }


}
