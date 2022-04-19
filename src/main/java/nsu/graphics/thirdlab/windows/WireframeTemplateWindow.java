package nsu.graphics.thirdlab.windows;

import nsu.graphics.thirdlab.template.Parameters;
import nsu.graphics.thirdlab.template.ParametersListener;
import nsu.graphics.thirdlab.template.ParametersPanel;
import nsu.graphics.thirdlab.template.PointsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.util.List;


public class WireframeTemplateWindow extends JFrame implements ParametersListener, ComponentListener {
    private static final int numberOfSegmentsPerInterval = 2;
    private final PointsPanel pointsPanel;
    private final ParametersPanel parametersPanel;
    private TemplateWindowListener listener;
    private JScrollPane scrollPane;

    public WireframeTemplateWindow(TemplateWindowListener listener) {
        super("Object Template");
        setSize(1000, 600);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ///setAlwaysOnTop(true);
        this.listener = listener;
        try {
            setLayout(new BorderLayout());
            scrollPane = new JScrollPane();
            pointsPanel = new PointsPanel(scrollPane, 1000, 400, numberOfSegmentsPerInterval);
            scrollPane.setViewportView(pointsPanel);

            parametersPanel = new ParametersPanel(this, numberOfSegmentsPerInterval);
            add(scrollPane, BorderLayout.CENTER);
            add(parametersPanel, BorderLayout.PAGE_END);
            addComponentListener(this);
            setBackground(Color.WHITE);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void openFile(File file) {
        try {
            Parameters parameters = JSONUtils.readJson(file);
            parametersPanel.setValues(parameters.K(), parameters.N(), parameters.m(), parameters.M());
            pointsPanel.setPoints(parameters.keyPoints(), parameters.N());
            acceptTemplate(parameters.N(),parameters.K(),parameters.m(), parameters.M());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveFile(File file) {
        int K = parametersPanel.getK();
        int N = parametersPanel.getN();
        int m = parametersPanel.getm();
        int M = parametersPanel.getM();
        List<Point> keyPoints = pointsPanel.getKeyPoints();
        try {
            JSONUtils.writeJson(file, new Parameters(K, N, m, M, keyPoints));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void normalize() {
        pointsPanel.normalize();
    }

    @Override
    public void setNumberOfSegmentsPerInterval(int N) {
        pointsPanel.setNumberOfSegmentsPerInterval(N);
    }

    @Override
    public void changePointNumber(int newValue) {
        pointsPanel.changePointNumber(newValue);
    }

    @Override
    public void addPointToLeft() {
        pointsPanel.addPointToLeft();
    }

    @Override
    public void addPointToRight() {
        pointsPanel.addPointToRight();
    }

    @Override
    public void deleteLeftPoint() {
        pointsPanel.deleteLeftPoint();
    }

    @Override
    public void disposeTemplateWindow() {
        //this.setVisible(false);
        this.dispose();
    }

    @Override
    public void acceptTemplate(int N, int K, int m, int M) {
        listener.setTemplate(pointsPanel.getSplinePoints(), N, K, m, M);
        disposeTemplateWindow();
    }

    @Override
    public int getNumberOfPoints() {
        return pointsPanel.getNumberOfPoints();
    }

    @Override
    public Point getPointPosition(int numberOfPoint) {
        return pointsPanel.getPointPosition(numberOfPoint);
    }

    @Override
    public void changePointPosition(int index, Point point) {
        pointsPanel.changePointPosition(index, point);
    }

    @Override
    public void deleteRightPoint() {
        pointsPanel.deleteRightPoint();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        pointsPanel.componentResized();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
