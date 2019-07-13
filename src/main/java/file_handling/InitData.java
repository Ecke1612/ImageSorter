package file_handling;

public class InitData {

    private int activeAccount = 0;

    public int getActiveAccount() {
        return activeAccount;
    }

    public void setActiveAccount(int activeAccount) {
        System.out.println("init activ account set: " + activeAccount);
        this.activeAccount = activeAccount;
    }
}
