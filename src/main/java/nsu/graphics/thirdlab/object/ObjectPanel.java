package nsu.graphics.thirdlab.object;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.*;

public class ObjectPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private final Dimension panelSize;          // visible image size
    private BufferedImage img = null;           // image to view
    private Dimension imSize = null;            // real image size
    private int lastX = 0, lastY = 0;           // last captured mouse coordinates
    private ObjectCoordinates object;
    private int numberOfTurns;
    private int numberOfSections;
    private int numberOfLinesPerSection;
    private int numberOfSplinePoints;
    private final Color objectColor;
    private int rotDegreeY;
    private int rotDegreeZ;
    private int numberOfIntervalsInSpline;
    private int numberOfSplinePointsPerInterval;

    /**
     * Creates default Object viewer panel.
     * Visible space will be painted in black.
     * <p>
     *
     * @param width  - start width of panel
     * @param height - start height of panel
     */
    public ObjectPanel(int width, int height) {
        panelSize = new Dimension(width, height);
        img = createEmptyImage(width, height);
        objectColor = Color.BLACK;
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        if (img != null) g2d.drawImage(img, 0, 0, panelSize.width, panelSize.height, null);
    }

    private int getXOnImage(int x) {
        return imSize.width / 2 + x;
    }

    private int getYOnImage(int y) {
        return imSize.height / 2 + y;
    }

    private void drawObject(Point[][] points, int width, int height) {
        img = createEmptyImage(width, height);
        imSize = new Dimension(this.img.getWidth(), this.img.getHeight());
        setPanelSize(width, height);
        Graphics2D g = img.createGraphics();
        g.setColor(objectColor);

        for (int section = 0; section < numberOfSections; section++) {
            for (int pointInLine = 0; pointInLine < numberOfSplinePoints; pointInLine++) {
                int x = getXOnImage(points[section * numberOfLinesPerSection][pointInLine].x);
                int y = getYOnImage(points[section * numberOfLinesPerSection][pointInLine].y);
                if (x < 0 && y < 0) {
                    int deltaX = Math.abs(x) * 2 + 50;
                    int deltaY = Math.abs(y) * 2 + 50;
                    drawObject(points, width + deltaX, height + deltaY);
                    return;
                }
                if (x < 0) {
                    int deltaX = Math.abs(x) * 2 + 50;
                    drawObject(points, width + deltaX, height);
                    return;
                }
                if (y < 0) {
                    int deltaY = Math.abs(y) * 2 + 50;
                    drawObject(points, width, height + deltaY);
                    return;
                }
                if (x > width && y > height) {
                    drawObject(points, x + 100, y + 100);
                    return;
                }
                if (x > width) {
                    drawObject(points, x + 100, height);
                    return;
                }
                if (y > height) {
                    drawObject(points, width, y + 100);
                    return;
                }
                if (pointInLine < numberOfSplinePoints - 1) {
                    int xNext = getXOnImage(points[section * numberOfLinesPerSection][pointInLine + 1].x);
                    int yNext = getYOnImage(points[section * numberOfLinesPerSection][pointInLine + 1].y);
                    g.drawLine(x, y, xNext, yNext);
                }
            }
            for (int line = 0; line < numberOfLinesPerSection; line++) {
                int numberOfLine = section * numberOfLinesPerSection + line;
                for (int pointInLine = 0; pointInLine < numberOfIntervalsInSpline; pointInLine++) {
                    int x = getXOnImage(points[numberOfLine][pointInLine*numberOfSplinePointsPerInterval].x);
                    int y = getYOnImage(points[numberOfLine][pointInLine*numberOfSplinePointsPerInterval].y);
                    int xNext = getXOnImage(points[(numberOfLine+1)%numberOfTurns][pointInLine*numberOfSplinePointsPerInterval].x);
                    int yNext = getYOnImage(points[(numberOfLine+1)%numberOfTurns][pointInLine*numberOfSplinePointsPerInterval].y);
                    g.drawLine(x, y, xNext, yNext);
                }
            }
        }
        repaint();
    }

    public void setTemplate(List<Point> splinePoints, int N, int K, int m, int M) {
        this.numberOfIntervalsInSpline = K - 2;
        this.numberOfSplinePointsPerInterval = N;
        this.rotDegreeY = 0;
        this.rotDegreeZ = 0;
        this.numberOfSplinePoints = splinePoints.size();
        this.numberOfSections = m;
        this.numberOfLinesPerSection = M;
        this.numberOfTurns = m * M;
        this.object = new ObjectCoordinates(splinePoints, m, M);
        drawObject(object.getCamProjectionPoints(), img.getWidth(), img.getHeight());
    }

    private BufferedImage createEmptyImage(int width, int height) {
        BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImg.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.dispose();
        repaint();
        return newImg;
    }

    public void componentResized() {
        int width = getWidth();
        int height = getHeight();
        int newImgWidth = Math.max(width, panelSize.width);
        int newImgHeight = Math.max(height, panelSize.height);
        if (newImgWidth > panelSize.width || newImgHeight > panelSize.height) {
            BufferedImage newResizedImage = new BufferedImage(newImgWidth, newImgHeight, BufferedImage.TYPE_INT_ARGB);
            imSize = new Dimension(newImgWidth, newImgHeight);
            setPanelSize(newImgWidth, newImgHeight);
            Graphics2D g = newResizedImage.createGraphics();
            g.setComposite(AlphaComposite.Src);
            g.fillRect(0, 0, width, height);
            g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(img, (newImgWidth - img.getWidth())/2, (newImgHeight - img.getHeight())/2, img.getWidth(), img.getHeight(), null);
            g.dispose();
            img = newResizedImage;
            repaint();
        }
    }

    public void normalizeObject() {
        rotDegreeY = 0;
        rotDegreeZ = 0;
        object.rotate(rotDegreeY, rotDegreeZ);
        drawObject(object.getCamProjectionPoints(), img.getWidth(), img.getHeight());
    }

    ///SIZE OF PANEL
    private void setPanelSize(int width, int height) {
        panelSize.width = width;
        panelSize.height = height;
    }

    @Override
    public Dimension getPreferredSize() {
        return panelSize;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        object.increaseDistanceToProjection((-10) * e.getWheelRotation());
        drawObject(object.getCamProjectionPoints(), img.getWidth(), img.getHeight());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int currDegreeY = rotDegreeY + (lastX- e.getX());
        int currDegreeZ = rotDegreeZ + (e.getY() - lastY);
        object.rotate(currDegreeY, currDegreeZ);
        drawObject(object.getCamProjectionPoints(), img.getWidth(), img.getHeight());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        rotDegreeY += (lastX- e.getX());
        rotDegreeZ += (e.getY() - lastY);
    }

    ///NOT USED
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}