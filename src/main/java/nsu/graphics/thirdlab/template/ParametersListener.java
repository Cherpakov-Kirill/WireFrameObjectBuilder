package nsu.graphics.thirdlab.template;

import java.awt.*;

public interface ParametersListener {
    void normalize();
    void setNumberOfSegmentsPerInterval(int N);

    void changePointNumber(int newValue);

    void addPointToRight();

    void deleteRightPoint();

    void addPointToLeft();

    void deleteLeftPoint();

    void disposeTemplateWindow();

    void acceptTemplate(int N, int K, int m, int M);

    int getNumberOfPoints();
    Point getPointPosition(int numberOfPoint);

    void changePointPosition(int index, Point point);

    void setSplineColor(Color color);

    void setKeyPointsColor(Color color);
}
