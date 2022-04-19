package nsu.graphics.thirdlab;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;

public class MathUtils {
    public static boolean isNumeric(String string) {
        double value;
        if(string == null || string.equals("")) {
            System.out.println("String cannot be parsed, it is null or empty.");
            return false;
        }

        try {
            value = Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Input String cannot be parsed to Numeric type.");
        }
        return false;
    }

    public static double[][] matrixMultiplying(double[][] A, double[][] B){
        int m = A.length;
        int n = B[0].length;
        int o = B.length;
        double[][] res = new double[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < o; k++) {
                    res[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return res;
    }

    public static double cosDeg(double degrees){
        return cos(toRadians(degrees));
    }

    public static double sinDeg(double degrees){
        return sin(toRadians(degrees));
    }
}
