package presentation.gui.controller.subcontrol;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import logic.AccountManager;
import logic.DataManager;
import main.Main;
import presentation.dataholder.TreeItemObject;
import presentation.gui.controller.MainController;

import java.io.File;

public class TreeViewControl {


    private DataManager dataManager;
    private TreeView treeView;

    public TreeViewControl(DataManager dataManager, TreeView treeView) {
        this.dataManager = dataManager;
        this.treeView = treeView;
    }

    public void initTreeView(MainController mainController) {
        File file = new File(dataManager.getRootPath());
        TreeItem<TreeItemObject> rootItem = new TreeItem<>(new TreeItemObject(AccountManager.getActiveAccount().getName() + "'s Bilder", dataManager.getRootPath(), countFiles(file.listFiles())));
        treeView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<TreeItem<TreeItemObject>>) (observable, old_val, new_val) -> {
            if(new_val != null && new_val != old_val) {
                if(new_val.getValue().getName().equals("Temporär")) {
                    System.out.println("select temporär");
                    mainController.isTempState = true;
                    dataManager.reloadTempImages();
                    //showImagesinGrid();
                } else {
                    mainController.isTempState = false;
                    TreeItem<TreeItemObject> selectedItem = new_val;
                    dataManager.fillDisplayedImagesMap(selectedItem.getValue().getPath(), true);
                    //showImagesinGrid();
                }
                System.out.println("tempstate: " + mainController.isTempState);
            }
        });

        rootItem.setExpanded(true);
        TreeItem<TreeItemObject> tempItem = new TreeItem<>(new TreeItemObject("Temporär", "", 0));
        rootItem.getChildren().add(tempItem);
        createSubTrees(rootItem, dataManager.getRootPath());

        treeView.setRoot(rootItem);

    }

    private void createSubTrees(TreeItem<TreeItemObject> treeItem, String path) {
        File file = new File(path);
        String[] fileNames = file.list();
        for(String s : fileNames) {
            String newPath = path + "\\" + s;
            File newFile = new File(newPath);
            if(newFile.isDirectory()) {
                //System.out.println("newFile Size: " + newFile.listFiles().length);
                if(newFile.listFiles().length > 0) {
                    TreeItem<TreeItemObject> item = new TreeItem<>(new TreeItemObject(s, newPath, countFiles(newFile.listFiles())));
                    item.setExpanded(true);
                    treeItem.getChildren().add(item);
                    createSubTrees(item, newPath);
                }
            }
        }
    }

    private int countFiles(File[] file) {
        int count = 0;
        for(File f : file) {
            if(f.isFile()) count++;
        }
        return count;
    }

    public void refreshTreeView() {
        treeView.getRoot().getChildren().clear();
        TreeItem<TreeItemObject> tempItem = new TreeItem<>(new TreeItemObject("Temporär", "", 0));
        treeView.getRoot().getChildren().add(tempItem);
        createSubTrees(treeView.getRoot(), dataManager.getRootPath());
    }

}
