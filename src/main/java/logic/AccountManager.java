package logic;

import main.Main;
import logic.dataholder.AccountObject;

import java.util.ArrayList;

public class AccountManager {

    public static ArrayList<AccountObject> accountObjects = new ArrayList<>();
    private static int activeAcountIndex = 0;

    public static void addNewAccount(AccountObject accountObject) {
        accountObjects.add(accountObject);
        activeAcountIndex = accountObjects.size() - 1;
        Main.initData.setActiveAccount(activeAcountIndex);
    }

    public static AccountObject getActiveAccount() {
        return accountObjects.get(activeAcountIndex);
    }

    public static int getActiveAcountIndex() {
        return activeAcountIndex;
    }
}
