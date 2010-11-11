package uk.co.bytemark.vm.enigma.inquisition.gui.quiz;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ResizeListener extends ComponentAdapter {

    @Override
    public void componentResized(ComponentEvent evt) {
        Component component = evt.getComponent();
        Dimension minSize = component.getMinimumSize();
        Rectangle bounds = component.getBounds();
        int oldX = bounds.x;
        int oldY = bounds.y;
        int newX = component.getX();
        int newY = component.getY();
        int newWidth = component.getWidth();
        int newHeight = component.getHeight();
        int diffX = 0;
        int diffY = 0;
        // Check if any corrections are needed
        if (newWidth < minSize.width)
            diffX = minSize.width - newWidth;
        if (newHeight < minSize.height)
            diffY = (minSize.height + 25) - newHeight; // 25 is a correction to avoid an
        // undersize bug
        // If any corrections are needed, resize (and possibly undo previous move)
        if (diffX > 0 || diffY > 0) {
            component.setBounds(oldX, oldY, newWidth + diffX, newHeight + diffY);
            bounds.setBounds(oldX, oldY, newWidth + diffX, newHeight + diffY);
        } else {
            bounds.setBounds(newX, newY, newWidth, newHeight);
        }
    }

    @Override
    public void componentMoved(ComponentEvent evt) {
        Component component = evt.getComponent();
        Rectangle bounds = component.getBounds();
        // Update only for non-resizing moves (resizing moves will be handled in
        // ComponentResized
        if (component.getWidth() == bounds.width && component.getHeight() == bounds.height) {
            bounds.setLocation(component.getX(), component.getY());
        }
    }
}
