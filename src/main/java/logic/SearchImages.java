package logic;

import logic.dataholder.ImageObject;
import logic.tasks.SearchTask;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchImages {

    private DataManager dataManager;

    public SearchImages(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void search(String s1, String s2, String s3, String s4) {
        SearchTask searchTask = new SearchTask(dataManager.getAllImageObjectsMap());
        searchTask.setS1(s1);
        searchTask.setS2(s2);
        searchTask.setS3(s3);
        searchTask.setS4(s4);
        try {
            searchTask.setOnRunning((succeesesEvent) -> {
            });

            searchTask.setOnSucceeded((succeededEvent) -> {
                dataManager.getDisplayedImageObjects().clear();
                dataManager.getDisplayedImageObjects().addAll(((HashMap<String, ImageObject>) searchTask.getValue()).values());
            });

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(searchTask);
            executorService.shutdown();

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

}
