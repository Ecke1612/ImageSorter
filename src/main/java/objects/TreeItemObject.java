package objects;

public class TreeItemObject {

    private String name;
    private String path;
    private int fileCount;

    public TreeItemObject(String name, String path, int fileCount) {
        this.name = name;
        this.path = path;
        this.fileCount = fileCount;
    }

    @Override
    public String toString() {
        if(fileCount == 0) return name;
        else return name + " (" + fileCount + ")";
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
