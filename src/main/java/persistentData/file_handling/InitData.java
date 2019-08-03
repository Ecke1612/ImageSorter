package persistentData.file_handling;

public class InitData {

    private int activeAccount = 0;
    private int width = 1280;
    private int height = 720;

    public int getActiveAccount() {
        return activeAccount;
    }

    public void setActiveAccount(int activeAccount) {
        System.out.println("init activ account set: " + activeAccount);
        this.activeAccount = activeAccount;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
