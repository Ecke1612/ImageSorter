package logic.workers;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import logic.dataholder.ImageObject;
import logic.workers.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class ImageObjectFACTORY {

    private DateTimeFormatter dateTimeFormatter = new DateTimeFormatter();

    public ImageObject createImageObject(File file, boolean isFixed) {
        if (file.isFile()) {
            LocalDateTime date = dateTimeFormatter.checkFileNameForDate(file.getName());
            boolean isMovie = false;
            if (file.getName().toLowerCase().endsWith("mp4")) isMovie = true;
            if (date != null) {
                return new ImageObject(file.getName(), date, file.getAbsolutePath(), file.getParent(), isFixed, isMovie);
            } else {
                return readMeta(file, isFixed, isMovie);
            }
        }
        return null;
    }

    private ImageObject readMeta(File f, boolean isFixed, boolean isMovie) {
        LocalDateTime date = null;
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(f);
            boolean found = false;
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    if (!found) {
                        if (tag.getTagName().equals("Date/Time") || tag.getTagName().equals("Creation Time")) {
                            date = dateTimeFormatter.formate(tag.getDescription(), f.getName());
                            found = true;
                        } else if (tag.getTagName().equals("Date/Time Original")) {
                            date = dateTimeFormatter.formate(tag.getDescription(), f.getName());
                            found = true;
                        } else if (tag.getTagName().equals("Date/Time Digitized")) {
                            date = dateTimeFormatter.formate(tag.getDescription(), f.getName());
                            found = true;
                        } else if (tag.getTagName().equals("File Modified Date")) {
                            date = dateTimeFormatter.formate(tag.getDescription(), f.getName());
                            found = true;
                            System.out.println("!Achtung, Datum ggf. verfälscht");
                        } else {
                            //System.out.println("not found date by: " + f.getName());
                        }
                    }
                }
                if (directory.hasErrors()) {
                    for (String error : directory.getErrors()) {
                        System.err.format("ERROR: %s", error);
                    }
                }
            }
            if(!found) {
                System.out.println("");
                System.out.println("FINAL not found date by: " + f.getName());
                for (Directory directory : metadata.getDirectories()) {
                    for (Tag tag : directory.getTags()) {
                        System.out.println(tag.getTagName() + " : " + tag.getDescription());
                    }
                }
            }

        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ImageObject(f.getName(), date, f.getAbsolutePath(), f.getParent(), isFixed, isMovie);
    }

}
