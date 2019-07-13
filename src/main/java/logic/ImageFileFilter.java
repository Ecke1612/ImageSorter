package logic;

import java.io.File;
import java.io.FileFilter;

class ImageFileFilter implements FileFilter {

    private final String[] acceptedImageFiles = new String[] { "jpg", "jpeg", "png", "gif" };

    public boolean accept(File file) {
        for (String extension : acceptedImageFiles) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
