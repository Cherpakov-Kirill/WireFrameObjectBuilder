package nsu.graphics.thirdlab.template;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PointsPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private final Dimension panelSize;          // visible image size
    private final JScrollPane spIm;
    private BufferedImage img = null;           // image to view
    private Rectangle scalingRectangle;
    private int lastX = 0, lastY = 0;           // last captured mouse coordinates
    private final double zoomK = 0.05;          // scroll zoom coefficient
    private double zoomCoefficient;             // scroll zoom value
    private Dimension imSize;
    private int reminderWidth;
    private int reminderHeight;

    private List<Point> keyPoints;

    private Spline spline;
    private int numberOfSegmentsPerInterval;

    private Color keyPointsColor;
    private Color splineColor;
    private final int circleSize;

    /**
     * Creates default Image-viewer in the given JScrollPane.
     * Visible space will be painted in black.
     * <p>
     *
     * @param scrollPane - JScrollPane to add a new Image-viewer
     * @param width      - start width of panel
     * @param height     - start height of panel
     */
    public PointsPanel(JScrollPane scrollPane, int width, int height, int numberOfSegmentsPerInterval) {
        this.numberOfSegmentsPerInterval = numberOfSegmentsPerInterval;
        zoomCoefficient = 1;
        spIm = scrollPane;
        spIm.setWheelScrollingEnabled(false);
        spIm.setDoubleBuffered(true);
        spIm.setViewportView(this);
        panelSize = new Dimension(width, height);
        spIm.validate();
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        keyPointsColor = Color.RED;
        splineColor = Color.BLUE;
        circleSize = 24;
        keyPoints = new LinkedList<>();
        keyPoints.add(new Point(-400, -200));
        keyPoints.add(new Point(-400, 0));
        keyPoints.add(new Point(-400, 200));
        keyPoints.add(new Point(400, 200));
        keyPoints.add(new Point(400, 0));
        keyPoints.add(new Point(400, -200));
        this.spline = new Spline(keyPoints, numberOfSegmentsPerInterval);
        setPoints(width, height);
    }

    public void setPoints(List<Point> keyPoints, int numberOfSegmentsPerInterval) {
        this.keyPoints = keyPoints;
        setNumberOfSegmentsPerInterval(numberOfSegmentsPerInterval);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setColor(Color.WHITE);
        if (img != null) {
            g2d.drawImage(img, 0, 0, panelSize.width, panelSize.height, null);
            g2d.drawLine(panelSize.width / 2, 0, panelSize.width / 2, panelSize.height);
            g2d.drawLine(0, panelSize.height / 2, panelSize.width, panelSize.height / 2);
        }
        if (scalingRectangle != null) {
            g2d.setColor(Color.CYAN);
            g2d.draw(scalingRectangle);
        }
    }

    public List<Point> getSplinePoints(){
        return spline.getSplinePoints();
    }

    public List<Point> getKeyPoints(){
        return keyPoints;
    }

    public Point getPointPosition(int numberOfPoint) {
        return keyPoints.get(numberOfPoint);
    }

    public int getNumberOfPoints() {
        return keyPoints.size();
    }

    public void setNumberOfSegmentsPerInterval(int N) {
        this.numberOfSegmentsPerInterval = N;
        this.spline = new Spline(keyPoints, N);
        setPoints(img.getWidth(), img.getHeight());
    }



    public void changePointPosition(int index, Point point) {
        keyPoints.set(index, point);
        this.spline = new Spline(keyPoints, numberOfSegmentsPerInterval);
        setPoints(img.getWidth(), img.getHeight());
    }

    public void setKeyPointsColor(Color color) {
        keyPointsColor = color;
        setPoints(img.getWidth(), img.getHeight());
    }

    public void setSplineColor(Color color) {
        splineColor = color;
        setPoints(img.getWidth(), img.getHeight());
    }

    public void changePointNumber(int newValue) {
        int currNumber = keyPoints.size();
        int delta = Math.abs(currNumber - newValue);
        if (newValue > currNumber) for (int i = 0; i < delta; i++) addPointToRight();
        if (newValue < currNumber) for (int i = 0; i < delta; i++) deleteRightPoint();
    }

    public void addPointToLeft() {
        Point last = keyPoints.get(0);
        Point prev = keyPoints.get(1);
        Collections.reverse(keyPoints);
        double dx = last.getX() - prev.getX();
        double dy = last.getY() - prev.getY();
        keyPoints.add(new Point((int) (last.getX() + dx), (int) (last.getY() + dy)));
        Collections.reverse(keyPoints);
        this.spline = new Spline(keyPoints, numberOfSegmentsPerInterval);
        setPoints(img.getWidth(), img.getHeight());
    }

    public void addPointToRight() {
        Point last = keyPoints.get(keyPoints.size() - 1);
        Point prev = keyPoints.get(keyPoints.size() - 2);
        double dx = last.getX() - prev.getX();
        double dy = last.getY() - prev.getY();
        keyPoints.add(new Point((int) (last.getX() + dx), (int) (last.getY() + dy)));
        this.spline = new Spline(keyPoints, numberOfSegmentsPerInterval);
        setPoints(img.getWidth(), img.getHeight());
    }

    public void deleteLeftPoint() {
        if (keyPoints.size() == 4) return;
        keyPoints.remove(0);
        this.spline = new Spline(keyPoints, numberOfSegmentsPerInterval);
        setPoints(img.getWidth(), img.getHeight());
    }

    public void deleteRightPoint() {
        if (keyPoints.size() == 4) return;
        keyPoints.remove(keyPoints.size() - 1);
        this.spline = new Spline(keyPoints, numberOfSegmentsPerInterval);
        setPoints(img.getWidth(), img.getHeight());
    }

    private int getXOnAxis(int x) {
        return (int) ((x - panelSize.width / 2) / zoomCoefficient);
    }

    private int getYOnAxis(int y) {
        return (int) ((y - panelSize.height / 2) / zoomCoefficient);
    }

    private int getXOnImage(int x) {
        return imSize.width / 2 + x;
    }

    private int getYOnImage(int y) {
        return imSize.height / 2 + y;
    }

    void drawCircle(Graphics2D g, int x, int y) {
        g.drawOval(x - circleSize / 2, y - circleSize / 2, circleSize, circleSize);
    }

    private void setPoints(int width, int height) {
        img = createEmptyImage(width, height);
        imSize = new Dimension(this.img.getWidth(), this.img.getHeight());
        int newPW = (int) (img.getWidth() * zoomCoefficient);
        setPanelSize(newPW, (int) ((long) newPW * imSize.height / imSize.width));
        Graphics2D g = img.createGraphics();
        g.setColor(keyPointsColor);
        int i = 0;
        int pointsNumber = keyPoints.size();
        for (Point point : keyPoints) {
            int x = getXOnImage(point.x);
            int y = getYOnImage(point.y);
            if (x < 0 && y < 0) {
                int deltaX = Math.abs(x) * 2 + 50;
                int deltaY = Math.abs(y) * 2 + 50;
                setPoints(width + deltaX, height + deltaY);
                return;
            }
            if (x < 0) {
                int deltaX = Math.abs(x) * 2 + 50;
                setPoints(width + deltaX, height);
                return;
            }
            if (y < 0) {
                int deltaY = Math.abs(y) * 2 + 50;
                setPoints(width, height + deltaY);
                return;
            }
            if (x > width && y > height) {
                setPoints(x + 100, y + 100);
                return;
            }
            if (x > width) {
                setPoints(x + 100, height);
                return;
            }
            if (y > height) {
                setPoints(width, y + 100);
                return;
            }

            drawCircle(g, x, y);
            if (i < pointsNumber - 1) {
                Point nextPoint = keyPoints.get(i + 1);
                int xNext = getXOnImage(nextPoint.x);
                int yNext = getYOnImage(nextPoint.y);
                g.drawLine(x, y, xNext, yNext);
            }
            i++;
        }
        List<Point> splinePoints = spline.getSplinePoints();
        i = 0;
        g.setColor(splineColor);
        pointsNumber = splinePoints.size();
        for (Point point : splinePoints) {
            int x = getXOnImage(point.x);
            int y = getYOnImage(point.y);
            if (i < pointsNumber - 1) {
                Point nextPoint = splinePoints.get(i + 1);
                int xNext = getXOnImage(nextPoint.x);
                int yNext = getYOnImage(nextPoint.y);
                g.drawLine(x, y, xNext, yNext);
            }
            i++;
        }
        g.dispose();
        repaint();
        revalidate();
        spIm.validate();
        spIm.repaint();
    }

    private BufferedImage createEmptyImage(int width, int height) {
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.dispose();
        return newImage;
    }

    public void componentResized() {
        int width = getWidth();
        int height = getHeight();
        int newImgWidth = Math.max(width, panelSize.width);
        int newImgHeight = Math.max(height, panelSize.height);
        if (newImgWidth > panelSize.width || newImgHeight > panelSize.height) {
            img = new BufferedImage(newImgWidth, newImgHeight, BufferedImage.TYPE_INT_ARGB);
            imSize = new Dimension(newImgWidth, newImgHeight);
            zoomCoefficient = 1;
            setPanelSize(newImgWidth, newImgHeight);
            setPoints(newImgWidth, newImgHeight);
        }
        normalize();
    }

    ///IMAGE VIEW

    /**
     * Sets normalized image on view.
     */
    public void normalize() {
        Dimension bounds = getVisibleRectSize();

        JScrollBar horizontal = spIm.getHorizontalScrollBar();
        horizontal.setValue((horizontal.getMaximum() - bounds.width) / 2);

        JScrollBar vertical = spIm.getVerticalScrollBar();
        vertical.setValue((vertical.getMaximum() - bounds.height) / 2);
        spIm.repaint();
    }


    ///SIZE OF PANEL

    private void setPanelSize(int width, int height) {
        panelSize.width = width;
        panelSize.height = height;
    }

    /**
     * @return Dimension object with the current view-size
     */
    private Dimension getVisibleRectSize() {
        // maximum size for panel without scrolling (inner border of the ScrollPane)
        Dimension viewportSize = spIm.getViewport().getSize();
        if (viewportSize.height == 0) return new Dimension(spIm.getWidth() - 3, spIm.getHeight() - 3);
        else return viewportSize;
    }

    ///SCROLL OF IMAGE

    private void setView(Rectangle rect) {
        setView(rect, 10);
    }

    private void setView(Rectangle rect, int minSize) {
        if (img == null) return;
        if (imSize.width < minSize || imSize.height < minSize) return;

        if (minSize <= 0) minSize = 10;

        if (rect.width < minSize) rect.width = minSize;
        if (rect.height < minSize) rect.height = minSize;
        if (rect.x < 0) rect.x = 0;
        if (rect.y < 0) rect.y = 0;
        if (rect.x > imSize.width - minSize) rect.x = imSize.width - minSize;
        if (rect.y > imSize.height - minSize) rect.y = imSize.height - minSize;
        if ((rect.x + rect.width) > imSize.width) rect.width = imSize.width - rect.x;
        if ((rect.y + rect.height) > imSize.height) rect.height = imSize.height - rect.y;

        Dimension viewSize = getVisibleRectSize();
        double kw = (double) rect.width / viewSize.width;
        double kh = (double) rect.height / viewSize.height;
        double k = Math.max(kh, kw);

        int newPW = (int) (imSize.width / k);
        int newPH = (int) (imSize.height / k);
        // Check for size whether we can still zoom out
        if (newPW == (int) (newPW * (1 - 2 * zoomK))) setView(rect, minSize * 2);
        setPanelSize(newPW, newPH);
        zoomCoefficient = (double) panelSize.width / (double) imSize.width;

        revalidate();
        spIm.validate();

        int xc = rect.x + rect.width / 2, yc = rect.y + rect.height / 2;
        xc = (int) (xc / k);
        yc = (int) (yc / k);    // we need to center new view
        //int x0 = (int)(rect.x/k), y0 = (int)(rect.y/k);
        spIm.getViewport().setViewPosition(new Point(xc - viewSize.width / 2, yc - viewSize.height / 2));
        revalidate();    // spIm.validate();
        spIm.paintAll(spIm.getGraphics());
    }

    @Override
    public Dimension getPreferredSize() {
        return panelSize;
    }

    /**
     * Change zoom when scrolling
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (img == null)
            return;
        // Zoom
        double k = 1 - e.getWheelRotation() * zoomK;
        // Check for minimum size where we can still increase size
        int newPW = (int) (panelSize.width * k);
        if (newPW == (int) (newPW * (1 + zoomK))) return;

        if (k > 1) {
            int newPH = (int) (panelSize.height * k);
            Dimension viewSize = getVisibleRectSize();
            int pixSizeX = newPW / imSize.width;
            int pixSizeY = newPH / imSize.height;
            if (pixSizeX > 0 && pixSizeY > 0) {
                int pixNumX = viewSize.width / pixSizeX;
                int pixNumY = viewSize.height / pixSizeY;
                if (pixNumX < 2 || pixNumY < 2)
                    return;
            }
        }

        setPanelSize(newPW, (int) ((long) newPW * imSize.height / imSize.width));
        zoomCoefficient = (double) panelSize.width / (double) imSize.width;

        // Move so that mouse position doesn't visibly change
        int x = (int) (e.getX() * k);
        int y = (int) (e.getY() * k);
        Point scroll = spIm.getViewport().getViewPosition();
        scroll.x -= e.getX();
        scroll.y -= e.getY();
        scroll.x += x;
        scroll.y += y;

        repaint();
        revalidate();
        spIm.validate();
        spIm.getHorizontalScrollBar().setValue(scroll.x);
        spIm.getVerticalScrollBar().setValue(scroll.y);
        spIm.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }


    @Override
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        lastY = e.getY();
        if (e.getButton() == MouseEvent.BUTTON3) scalingRectangle = new Rectangle();
    }

    /**
     * Move visible image part when dragging
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) {
            int x = Math.min(lastX, e.getX());
            int y = Math.min(lastY, e.getY());
            int width = Math.abs(lastX - e.getX());
            int height = Math.abs(lastY - e.getY());
            scalingRectangle.setBounds(x, y, width, height);
            repaint();
            return;
        }
        for (int i = 0; i < keyPoints.size(); i++) {
            Point point = keyPoints.get(i);
            int xMouseAxis = getXOnAxis(lastX);
            int yMouseAxis = getYOnAxis(lastY);
            double r = Math.sqrt(Math.pow((point.x - xMouseAxis), 2.0) + Math.pow((point.y - yMouseAxis), 2.0));
            if (r <= circleSize) {
                xMouseAxis = getXOnAxis(e.getX());
                yMouseAxis = getYOnAxis(e.getY());
                keyPoints.set(i, new Point(xMouseAxis, yMouseAxis));
                this.spline = new Spline(keyPoints, numberOfSegmentsPerInterval);
                setPoints(img.getWidth(), img.getHeight());
                lastX = e.getX();
                lastY = e.getY();
                return;
            }
        }
        // move picture using scroll
        Point scroll = spIm.getViewport().getViewPosition();
        scroll.x += (lastX - e.getX());
        scroll.y += (lastY - e.getY());
        spIm.getHorizontalScrollBar().setValue(scroll.x);
        spIm.getVerticalScrollBar().setValue(scroll.y);
        spIm.repaint();
    }

    /**
     * When a rectangle is selected with pressed right button,
     * we zoom image to that rectangle
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON3) return;
        scalingRectangle = null;

        int x1 = e.getX();
        int y1 = e.getY();
        if (Math.abs(x1 - lastX) < 5 && Math.abs(y1 - lastY) < 5) return;

        double k = (double) imSize.width / panelSize.width;

        int x0 = (int) (k * lastX);
        int y0 = (int) (k * lastY);
        x1 = (int) (k * x1);
        y1 = (int) (k * y1);

        int w = Math.abs(x1 - x0);
        int h = Math.abs(y1 - y0);
        if (x1 < x0) x0 = x1;
        if (y1 < y0) y0 = y1;
        setView(new Rectangle(x0, y0, w, h));
    }

    ///NOT USED

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