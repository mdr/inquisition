package uk.co.bytemark.vm.enigma.inquisition.gui.images;

import java.awt.Image;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

public enum Icons {

    FAVICON("JI.png"),
    TICK("tick.png"),
    SMALL_TICK("tick16.png"),
    CROSS("cross.png"),
    SMALL_CROSS("cross16.png"),
    BACK("bb_back.png"),
    FORWARD("bb_forward.png"),
    FIRST("bb_bback.png"),
    LAST("bb_fforward.png");


    private Image image;

    private ImageIcon icon;

    private Icons(String fileName) {
        URL url = this.getClass().getResource(fileName);
        if (url == null) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.log(Level.WARNING, "Could not find application icon '" + name() + "' in file '" + fileName + "'");
        } else {
            icon = new ImageIcon(url);
            image = icon.getImage();
        }
    }

    public Image getImage() {
        return image;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public boolean isAvailable() {
        return image != null;
    }
}
