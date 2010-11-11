/*
 * TransparentPane.java
 * 
 * Created on 18 September 2006, 13:14
 */
package uk.co.bytemark.vm.enigma.inquisition.gui.quiz;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.io.Serializable;

import javax.swing.*;

/**
 * A transparent <tt>JPanel</tt> that draws a translucent image at the requested position.
 */
public class TransparentPane extends JPanel implements ActionListener {

    private final static float TRANSPARENCY = 0.75f;

    private final static int DELAY = 15;

    private final static int STEPS = 15;

    private Point position;

    private Point originalPosition;

    private BufferedImage image;

    private AlphaComposite alphaComposite;

    private Timer timer;

    private double deltaX;

    private double deltaY;

    private double precisionX;

    private double precisionY;

    private int steps;

    private Callback callback;

    public TransparentPane() {
        alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, TRANSPARENCY);
        setOpaque(false);
    }

    void setTransparency(float transparency) {
        alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null)
            return;

        ((Graphics2D) g).setComposite(alphaComposite);

        int x = (int) (position.getX() - image.getWidth() / 2.0);
        int y = (int) (position.getY() - image.getHeight() / 2.0);

        ((Graphics2D) g).drawImage(image, x, y, null);
    }

    void setPosition(Point position) {
        this.position = position;
    }

    void activate(Point p, BufferedImage anImage) {
        this.image = anImage;
        setPosition(p);
        originalPosition = p;
        setVisible(true);
    }

    // TODO: do this overloading the other way round

    void sendBackToOrigin(Callback aCallback, Point originPoint, BufferedImage anImage) {
        this.image = anImage;
        sendBackToOrigin(aCallback, originPoint);
    }

    void sendBackToOrigin(Callback aCallback, Point originPoint) {
        originalPosition = originPoint;
        sendBackToOrigin(aCallback);
    }

    /**
     * Animate the fragment returning back to the bin
     */
    void sendBackToOrigin(Callback aCallback) {

        this.callback = aCallback;
        precisionX = position.x;
        precisionY = position.y;
        int targetX = originalPosition.x;
        int targetY = originalPosition.y;

        steps = STEPS;

        deltaX = (targetX - precisionX) / steps;
        deltaY = (targetY - precisionY) / steps;

        if (Math.abs(deltaX + deltaY) < 4) {
            endAnimation();
            return;
        }

        // System.out.println(deltaX + " " + deltaY + " " + steps);
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void endAnimation() {
        // System.out.println("Ending animation");
        // if (callback != null) {

        callback.returnedToOrigin();
        // }
        setVisible(false);
    }

    public void actionPerformed(ActionEvent e) {

        // TODO: eh? ... 
        // Hack to re-show the transparent pane if it gets turned off by mistake
        if (!isVisible())
            setVisible(true);

        precisionX += deltaX;
        precisionY += deltaY;
        setPosition(new Point((int) precisionX, (int) precisionY));
        repaint();
        steps -= 1;
        if (steps <= 0) {
            timer.stop();
            endAnimation();
        }
    }

    interface Callback extends Serializable {
        void returnedToOrigin();
    }
}
