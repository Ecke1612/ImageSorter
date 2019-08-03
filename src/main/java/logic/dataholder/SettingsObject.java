package logic.dataholder;

public class SettingsObject {

    private boolean isCut = false;
    private boolean isTag = false;
    private boolean isSubTag = false;
    private boolean isMonthly = false;

    public boolean isCut() {
        return isCut;
    }

    public void setCut(boolean cut) {
        isCut = cut;
    }

    public boolean isTag() {
        return isTag;
    }

    public void setTag(boolean tag) {
        isTag = tag;
    }

    public boolean isSubTag() {
        return isSubTag;
    }

    public void setSubTag(boolean subTag) {
        isSubTag = subTag;
    }

    public boolean isMonthly() {
        return isMonthly;
    }

    public void setMonthly(boolean monthly) {
        isMonthly = monthly;
    }
}
