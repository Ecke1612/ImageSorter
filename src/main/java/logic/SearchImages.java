package logic;

import logic.dataholder.ImageObject;
import logic.tasks.SearchTask;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchImages {

    private DataManager dataManager;

    public SearchImages(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    public void search(String s1, String s2, String s3, String s4, String searchString) {
        SearchTask searchTask = new SearchTask(s1, s2, s3, s4, dataManager.getDisplayedImageObjects());
        searchTask.setSearchString1(searchString);
        searchTask.setList(dataManager.getAllImageObjects());
        try {
            searchTask.setOnRunning((succeesesEvent) -> {
            });

            searchTask.setOnSucceeded((succeededEvent) -> {
                dataManager.getDisplayedImageObjects().clear();
                dataManager.getDisplayedImageObjects().addAll((ArrayList<ImageObject>) searchTask.getValue());
            });

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(searchTask);
            executorService.shutdown();

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

}