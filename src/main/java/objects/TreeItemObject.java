package objects;

public class TreeItemObject {

    private String name;
    private String path;

    public TreeItemObject(String name, String path) {
        this.name = name;
        this.path = path;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
