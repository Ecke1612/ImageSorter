package logic.dataholder;

public class AccountObject {

    private String name;
    private String path;

    public AccountObject(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
