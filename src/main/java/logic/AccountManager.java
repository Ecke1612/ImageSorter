package logic;

import main.Main;
import objects.AccountObject;

import java.util.ArrayList;

public class AccountManager {

    public static ArrayList<AccountObject> accountObjects = new ArrayList<>();
    private static int activeAcount = 0;

    public static void addNewAccount(AccountObject accountObject) {
        accountObjects.add(accountObject);
        activeAcount = accountObjects.size() - 1;
        Main.initData.setActiveAccount(activeAcount);
    }

    public static AccountObject getActiveAccount() {
        return accountObjects.get(activeAcount);
    }

}
