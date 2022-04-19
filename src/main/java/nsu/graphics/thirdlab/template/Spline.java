package nsu.graphics.thirdlab.template;

import nsu.graphics.thirdlab.MathUtils;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Spline {
    private final List<Point> keyPoints;
    private final List<Point> splinePoints;

    private static double[][] initM() {
        double[][] matrix = {{-1, 3, -3, 1}, {3, -6, 3, 0}, {-3, 0, 3, 0}, {1, 4, 1, 0}};
        double multiplier = 1.0 / 6.0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matrix[i][j] *= multiplier;
            }
        }
        return matrix;
    }

    private static List<double[]> initTList(int numberOfSegmentsPerInterval) {
        List<double[]> tMatrixList = new LinkedList<>();
        double deltaT = 1.0 / numberOfSegmentsPerInterval;
        double t = deltaT;
        tMatrixList.add(new double[]{0, 0, 0, 1});
        for (int i = 0; i < numberOfSegmentsPerInterval; i++) {
            double[] T = new double[4];
            T[3] = 1.0;
            for (int pos = 2; pos >= 0; pos--) {
                T[pos] = T[pos + 1] * t;
            }
            tMatrixList.add(T);
            t += deltaT;
        }
        return tMatrixList;
    }

    private double getXFromPoint(int numberOfPoint) {
        return keyPoints.get(numberOfPoint).getX();
    }

    private double getYFromPoint(int numberOfPoint) {
        return keyPoints.get(numberOfPoint).getY();
    }

    public Spline(List<Point> keyPoints, int numberOfSegmentsPerInterval) {
        double[][] m = initM();
        List<double[]> tMatrixList = initTList(numberOfSegmentsPerInterval);
        this.splinePoints = new LinkedList<>();
        this.keyPoints = keyPoints;
        int maxNumberOfPoint = keyPoints.size() - 3;
        for (int intervalNumber = 1; intervalNumber <= maxNumberOfPoint; intervalNumber++) {
            double[][] Gx = {{getXFromPoint(intervalNumber - 1)}, {getXFromPoint(intervalNumber)},
                    {getXFromPoint(intervalNumber + 1)}, {getXFromPoint(intervalNumber + 2)}};
            double[][] Gy = {{getYFromPoint(intervalNumber - 1)}, {getYFromPoint(intervalNumber)},
                    {getYFromPoint(intervalNumber + 1)}, {getYFromPoint(intervalNumber + 2)}};
            for (int segmentNumber = 0; segmentNumber <= numberOfSegmentsPerInterval; segmentNumber++) {
                if (segmentNumber == numberOfSegmentsPerInterval && intervalNumber != maxNumberOfPoint) break;
                double[][] T = {tMatrixList.get(segmentNumber)};
                double[][] resTM = MathUtils.matrixMultiplying(T, m);
                double[][] resX = MathUtils.matrixMultiplying(resTM, Gx);
                double[][] resY = MathUtils.matrixMultiplying(resTM, Gy);
                splinePoints.add(new Point((int) Math.round(resX[0][0]), (int) Math.round(resY[0][0])));
            }
        }
    }

    public List<Point> getSplinePoints() {
        return splinePoints;
    }
}
