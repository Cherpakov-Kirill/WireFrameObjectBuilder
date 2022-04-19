package nsu.graphics.thirdlab.object;

import java.awt.*;
import java.util.List;

import static nsu.graphics.thirdlab.MathUtils.*;

public class ObjectCoordinates {
    private static final int screenIncreaseSize = 400;
    private final double camPos;
    private double distanceToProjection;
    private final Point3D[][] localObjectPoints;
    private final Point3D[][] globalObjectPoints;
    private final Point[][] camProjectionPoints;
    private double[][] rotateX;
    private double[][] rotateY;
    private double[][] rotateZ;
    private final int numberOfColumns;
    private final int numberOfPointsInLine;

    private void initRotateXOperator(double deg) {
        rotateX = new double[4][4];
        rotateX[0][0] = 1;
        rotateX[1][1] = cosDeg(deg);
        rotateX[2][2] = rotateX[1][1];
        rotateX[2][1] = sinDeg(deg);
        rotateX[1][2] = (-1.0) * rotateX[2][1];
        rotateX[3][3] = 1;
    }

    private void initRotateYOperator(double deg) {
        rotateY = new double[4][4];
        rotateY[0][0] = cosDeg(deg);
        rotateY[0][2] = sinDeg(deg);
        rotateY[1][1] = 1;
        rotateY[2][2] = rotateY[0][0];
        rotateY[2][0] = (-1.0) * rotateY[0][2];
        rotateY[3][3] = 1;
    }

    private void initRotateZOperator(double deg) {
        rotateZ = new double[4][4];
        rotateZ[0][0] = cosDeg(deg);
        rotateZ[1][1] = rotateZ[0][0];
        rotateZ[1][0] = sinDeg(deg);
        rotateZ[0][1] = (-1.0) * rotateZ[1][0];
        rotateZ[2][2] = 1;
        rotateZ[3][3] = 1;
    }

    private void initLocalObjectPoints(List<Point> splinePoints) {
        double maxRadius = 0;
        for (int i = 0; i < numberOfPointsInLine; i++) {
            Point point2D = splinePoints.get(i);
            maxRadius = Math.max(maxRadius, Math.abs(point2D.x));
            maxRadius = Math.max(maxRadius, Math.abs(point2D.y));
        }
        for (int i = 0; i < numberOfPointsInLine; i++) {
            Point point2D = splinePoints.get(i);
            localObjectPoints[0][i] = new Point3D(point2D.x / maxRadius, point2D.y / maxRadius, 0);
        }
        for (int turn = 1; turn < numberOfColumns; turn++) {
            for (int i = 0; i < numberOfPointsInLine; i++) {
                Point3D point = localObjectPoints[turn - 1][i];
                double[][] result = matrixMultiplying(rotateX, new double[][]{{point.x()}, {point.y()}, {point.z()}, {1}});
                localObjectPoints[turn][i] = new Point3D(result[0][0], result[1][0], result[2][0]);
            }
        }
    }

    private void initGlobalObjectPoints() {
        for (int j = 0; j < numberOfColumns; j++) {
            for (int i = 0; i < numberOfPointsInLine; i++) {
                Point3D point = localObjectPoints[j][i];
                globalObjectPoints[j][i] = new Point3D(point.x(), point.y(), point.z() - camPos);
            }
        }
    }

    private void initCamProjectionPoints() {
        for (int turn = 0; turn < numberOfColumns; turn++) {
            for (int i = 0; i < numberOfPointsInLine; i++) {
                Point3D point = globalObjectPoints[turn][i];
                double mul = distanceToProjection / point.z();
                camProjectionPoints[turn][i] = new Point((int) Math.round(point.x() * mul * screenIncreaseSize), (int) Math.round(point.y() * mul * screenIncreaseSize));
            }
        }
    }

    public Point[][] getCamProjectionPoints() {
        return camProjectionPoints;
    }

    public void increaseDistanceToProjection(int delta) {
        this.distanceToProjection -= delta * (0.2);
        initCamProjectionPoints();
    }

    public ObjectCoordinates(List<Point> splinePoints, int numberOfSections, int numberLinesPerSection) {
        camPos = -10;
        numberOfColumns = numberOfSections * numberLinesPerSection;
        numberOfPointsInLine = splinePoints.size();
        double deltaPhi = 360.0 / numberOfColumns;
        localObjectPoints = new Point3D[numberOfColumns][numberOfPointsInLine];
        globalObjectPoints = new Point3D[numberOfColumns][numberOfPointsInLine];
        camProjectionPoints = new Point[numberOfColumns][numberOfPointsInLine];

        initRotateXOperator(deltaPhi);
        initLocalObjectPoints(splinePoints);
        initGlobalObjectPoints();
        this.distanceToProjection = 5.0;
        initCamProjectionPoints();
    }


    public void rotate(int rotDegreeY, int rotDegreeZ) {
        initRotateYOperator(rotDegreeY);
        initRotateZOperator(rotDegreeZ);
        for (int turn = 0; turn < numberOfColumns; turn++) {
            for (int i = 0; i < numberOfPointsInLine; i++) {
                Point3D point = localObjectPoints[turn][i];
                double[][] result = matrixMultiplying(rotateY, new double[][]{{point.x()}, {point.y()}, {point.z()}, {1}});
                result = matrixMultiplying(rotateZ, result);
                globalObjectPoints[turn][i] = new Point3D(result[0][0], result[1][0], result[2][0] - camPos);
            }
        }
        initCamProjectionPoints();
    }
}
