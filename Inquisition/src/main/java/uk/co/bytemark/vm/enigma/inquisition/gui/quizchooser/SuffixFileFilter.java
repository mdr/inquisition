package uk.co.bytemark.vm.enigma.inquisition.gui.quizchooser;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class SuffixFileFilter extends FileFilter {

    private final String suffix;

    private final String type;

    public SuffixFileFilter(String suffix, String type) {
        this.suffix = suffix;
        this.type = type;
    }

    @Override
    public boolean accept(File file) {
        return file.isDirectory() || file.getName().matches(".*\\." + suffix);
    }

    @Override
    public String getDescription() {
        return "*." + suffix + " (" + type + ")";
    }
    
    
    public String addSuffixTo(String fileName) {
        return fileName+"." + suffix;
    }

    public static void setSoleFileFilter(JFileChooser fileChooser, SuffixFileFilter fileFilterToUse) {
        FileFilter[] choosableFileFilters = fileChooser.getChoosableFileFilters();
        for (FileFilter fileFilter : choosableFileFilters) {
            fileChooser.removeChoosableFileFilter(fileFilter);
        }
        fileChooser.addChoosableFileFilter(fileFilterToUse);
        fileChooser.setFileFilter(fileFilterToUse);
    }

}
