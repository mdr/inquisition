/*
 * DragAndDropFragmentBinPanel.java
 * 
 * Created on 11 September 2006, 11:01
 */

package uk.co.bytemark.vm.enigma.inquisition.gui.quiz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import uk.co.bytemark.vm.enigma.inquisition.questions.DragAndDropQuestion;

/**
 * A "bin" of fragments for a {@link DragAndDropQuestion}.
 * 
 * @see DragAndDropPanel
 */
class DragAndDropFragmentBinPanel extends JPanel {

    private int preferredSizeY = 50;

    private int preferredSizeX = 300;

    // The list of fragments (todo: bundle up this, paintFragment, fragmentRectangles and fragmentImages into a data
    // structure).
    private List<String> fragments;

    // Whether the each fragment is "in" the fragment bin
    private boolean[] paintFragment;

    // The location of each fragment in the bin is represented as a rectangle
    private Rectangle2D[] fragmentRectangles;

    // A mapping between a fragment and an image representing it
    private Map<String, BufferedImage> fragmentImages = new HashMap<String, BufferedImage>();

    private boolean isBinActive = true; // The bin is inactive in review mode

    // Drawing constants
    private static final int SPACING = 10;

    private static final int START_X = 25;

    private static final int START_Y = 25;

    private static final int SHADOW_X = 5;

    private static final int SHADOW_Y = 5;

    private static final int WIDTH_PADDING = 20;

    private static final int CORNER_RADIUS = 10;

    private static final Color SHADOW_COLOUR = Color.GRAY;

    private static final Color TEXT_COLOUR = Color.BLACK;

    private static final Color INACTIVE_TEXT_COLOUR = Color.darkGray;

    public static final Color FRAGMENT_FILL_COLOUR = new Color(0.9f, 0.9f, 1.0f);

    private static final Color INACTIVE_FRAGMENT_FILL_COLOUR = new Color(0.915f, 0.9f, 0.9f);

    /**
     * Creates a new DragAndDropFragmentBinPanel.
     * 
     * @param fragments
     *            the list of fragments to be used in the question. The constructor will make a copy of the list, and
     *            then randomly shuffle the order.
     */
    public DragAndDropFragmentBinPanel(List<String> fragments) {
        this.fragments = new ArrayList<String>(fragments);
        Collections.shuffle(this.fragments); // Randomise order

        fragmentRectangles = new Rectangle2D[fragments.size()];

        paintFragment = new boolean[fragments.size()];
        for (int i = 0; i < fragments.size(); i++)
            paintFragment[i] = true;

        // Create a store of fragment images for use in drag-and-drop
        for (String fragment : fragments)
            fragmentImages.put(fragment, createImage(fragment));

    }

    /**
     * Paints this component, and also calculates a new preferred height from its given width while doing so.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Rectangle2D.Float rectangle;
        int maxX = getWidth();
        int currentX = START_X;
        int currentY = START_Y;

        // The maximum height for this row of fragments
        // (these is very likely the same for all fragments at present)
        int maxHeight = 0;

        // Loop over each fragment
        for (int i = 0; i < fragments.size(); i++) {

            // Skip fragments that have been moved out of the bin
            if (!paintFragment[i]) {
                fragmentRectangles[i] = null;
                continue;
            }

            String fragment = fragments.get(i);
            // Precalculate the width before drawing
            int width = fragmentWidth(g2, fragment);

            // If the new fragment takes us passed the maximum width,
            // skip to the next row
            if (currentX + width > maxX - 10) {
                currentX = START_X;
                currentY += maxHeight + SPACING;
                maxHeight = 0;
            }

            // Draw the fragment, and stash its bounds
            rectangle = drawFragment(g2, fragment, currentX, currentY);
            fragmentRectangles[i] = rectangle;

            // Update drawing postion ready for next fragment
            currentX += ((int) rectangle.width) + SPACING;
            if (rectangle.height > maxHeight)
                maxHeight = (int) rectangle.height;

        }

        // We now know our new preferred size
        preferredSizeX = maxX;
        preferredSizeY = currentY + 50;

        // Call this here to make first appearance in frame work
        revalidate();
    }

    /**
     * Returns the dimensions needed to render this fragment.
     */
    private Rectangle2D.Float rectangleNeededFor(String fragment) {
        // Create a throwaway Graphics2D to trial-render this component
        BufferedImage image = new BufferedImage(2048, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        return drawFragment(g, fragment);
    }

    /**
     * Renders a fragment.
     */
    private Rectangle2D.Float drawFragment(Graphics2D g, String fragmentText, int inX, int inY) {
        Font font = new Font("Monospaced", Font.PLAIN, 12);
        FontRenderContext fontRenderContext = g.getFontRenderContext();
        Rectangle2D stringBoundsRectangle = font.getStringBounds(fragmentText, fontRenderContext);

        int fragmentWidth = (int) (stringBoundsRectangle.getWidth());
        int fragmentHeight = (int) (stringBoundsRectangle.getHeight());
        int x = inX + WIDTH_PADDING / 2;
        int y = inY + fragmentHeight;
        Shape fragmentShape = new RoundRectangle2D.Float(x - WIDTH_PADDING / 2, y - fragmentHeight, fragmentWidth + WIDTH_PADDING, fragmentHeight
                + fragmentHeight / 2, CORNER_RADIUS, CORNER_RADIUS);

        // The shadow is the same size as the fragment rectangle,
        // but shifted SHADOW_X/Y pixels across
        Shape shadow = new RoundRectangle2D.Float(x - WIDTH_PADDING / 2 + SHADOW_X, y - fragmentHeight + SHADOW_Y, fragmentWidth
                + WIDTH_PADDING, fragmentHeight + fragmentHeight / 2, CORNER_RADIUS, CORNER_RADIUS);

        g.setPaint(SHADOW_COLOUR);
        g.fill(shadow);

        if (isBinActive) {
            g.setPaint(FRAGMENT_FILL_COLOUR);
            g.fill(fragmentShape);
            g.setPaint(TEXT_COLOUR);
            g.draw(fragmentShape);
        } else {
            g.setPaint(INACTIVE_FRAGMENT_FILL_COLOUR);
            g.fill(fragmentShape);
            g.setPaint(INACTIVE_TEXT_COLOUR);
            g.draw(fragmentShape);
        }

        g.setFont(font);
        g.drawString(fragmentText, x, y);

        return new Rectangle2D.Float(inX, inY, fragmentWidth + WIDTH_PADDING + SHADOW_X, (int) (fragmentHeight * 1.5) + SHADOW_Y);

    }

    /**
     * Draws a fragment (convenience overloaded function)
     */
    private Rectangle2D.Float drawFragment(Graphics2D graphics2D, String f) {
        return drawFragment(graphics2D, f, 0, 0);
    }

    /**
     * Precalculate fragment width Fixme: violates "don't repeat yourself"
     */
    private int fragmentWidth(Graphics2D g, String s) {
        Font font = new Font("Monospaced", Font.PLAIN, 12);
        FontRenderContext frc = g.getFontRenderContext();
        Rectangle2D r = font.getStringBounds(s, frc);
        int fWidth = (int) (r.getWidth());
        return fWidth + WIDTH_PADDING + SHADOW_X;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(preferredSizeX, preferredSizeY);
    }

    /**
     * Returns the fragment at this point in the fragment bin (if there's one active)
     */
    String getFragmentAtPoint(Point point) {
        for (int i = 0; i < fragments.size(); i++) {
            Rectangle2D fragmentRectangle = fragmentRectangles[i];
            if (paintFragment[i] && fragmentRectangle != null && fragmentRectangle.contains(point.x, point.y))
                return fragments.get(i);
        }
        return null;
    }

    /**
     * Returns the index of the fragment in the list of fragments.
     */
    private int findIndex(String f) {
        int index = -1;
        for (int i = 0; i < fragments.size(); i++) {
            if (f.equals(fragments.get(i))) {
                index = i;
                break;
            }
        }
        return index;
    }

    void hideFragment(String f) {
        int index = findIndex(f);
        if (index >= 0)
            paintFragment[index] = false;
        repaint();
    }

    void showFragment(String f) {
        int index = findIndex(f);
        if (index >= 0)
            paintFragment[index] = true;
        repaint();
    }

    /**
     * Fetches a cached <tt>BufferedImage</tt> representing the given fragment.
     */
    BufferedImage getFragmentImage(String f) {
        return fragmentImages.get(f);
    }

    /**
     * Returns a new <tt>BufferedImage</tt> representing the given fragment.
     */
    private BufferedImage createImage(String f) {
        Rectangle2D r = rectangleNeededFor(f);
        BufferedImage image = new BufferedImage((int) r.getWidth(), (int) r.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        drawFragment(g, f);
        return image;

    }

    void setActive(boolean active) {
        isBinActive = active;
    }

}
