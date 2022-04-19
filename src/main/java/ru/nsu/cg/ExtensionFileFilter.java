package ru.nsu.cg;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * File filter which leaves only directories and files with specific extension
 *
 * @author Tagir F. Valeev
 */
public class ExtensionFileFilter extends FileFilter {
    String extension, description;

    /**
     * Constructs filter
     *
     * param extension   - extension (without point), for example, "txt"
     * param description - file type description, for example, "Text files"
     */
    public ExtensionFileFilter(String extension) {
        this.extension = extension;
        this.description = extension;
    }

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith("." + extension.toLowerCase());
    }

    @Override
    public String getDescription() {
        return description + " (*." + extension + ")";
    }
}