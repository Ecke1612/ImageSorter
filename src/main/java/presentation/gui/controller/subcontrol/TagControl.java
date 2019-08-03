package presentation.gui.controller.subcontrol;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import presentation.dataholder.SimpleTagObject;
import presentation.gui.controller.MediaObjectController;

import java.util.ArrayList;

public class TagControl {

    public void setTagMenuItem(SimpleTagObject t, ContextMenu popup, MediaObjectController mediaObjectController, ArrayList<MediaObjectController> mediaObjectControllers) {
        MenuItem item = new MenuItem(t.getName());
        popup.getItems().add(item);
        item.setOnAction(event -> {
            if (mediaObjectController.getImageObject().getTagObjects().size() <= 5) {
                taglogic(mediaObjectController, t, false);
            }
            for (MediaObjectController i : mediaObjectControllers) {
                if (i.checkbox.isSelected()) {
                    if (i.getImageObject().getTagObjects().size() <= 5) {
                        taglogic(i, t, false);
                    }
                }
            }
        });
    }

    public void setSubTagMenuItem(SimpleTagObject st, ContextMenu popup, MediaObjectController mediaObjectController, ArrayList<MediaObjectController> mediaObjectControllers) {
        MenuItem item = new MenuItem(st.getName());
        popup.getItems().add(item);
        item.setOnAction(event -> {
            if (mediaObjectController.getImageObject().getSubTagObjects().size() <= 5) {
                taglogic(mediaObjectController, st, true);
            }
            for (MediaObjectController i : mediaObjectControllers) {
                if (i.checkbox.isSelected()) {
                    if (i.getImageObject().getSubTagObjects().size() <= 5) {
                        taglogic(i, st, true);
                    }
                }
            }
        });
    }

    private void taglogic(MediaObjectController mediaObjectController, SimpleTagObject t, boolean sub) {
        if(!sub) {
            if (checkForTagDuplicatesInList(mediaObjectController.getImageObject().getTagObjects(), t)) {
                mediaObjectController.getImageObject().getTagObjects().remove(t);
            } else {
                mediaObjectController.getImageObject().getTagObjects().add(t);
            }
            mediaObjectController.setTagOnGui(false);
        } else {
            if (checkForTagDuplicatesInList(mediaObjectController.getImageObject().getSubTagObjects(), t)) {
                mediaObjectController.getImageObject().getSubTagObjects().remove(t);
            } else {
                mediaObjectController.getImageObject().getSubTagObjects().add(t);
            }
            mediaObjectController.setTagOnGui(true);
        }
        mediaObjectController.checkbox.setSelected(false);

    }

    private Boolean checkForTagDuplicatesInList(ArrayList<SimpleTagObject> list, SimpleTagObject t) {
        if(list.contains(t)) return true;
        else return false;
    }

}
