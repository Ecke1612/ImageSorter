package logic;

import java.io.File;
import java.io.FileFilter;

public class ImageFileFilter implements FileFilter {

    private final String[] acceptedImageFiles = new String[] { "jpg", "jpeg", "png", "gif", "mp4"};

    public boolean accept(File file) {
        for (String extension : acceptedImageFiles) {
            if (file.getName().toLowerCase().endsWith(extension) || file.isDirectory()) {
                return true;
            }
        }
        return false;
    }
}
