/**
 * author: Vadym Sapronov
 * created_at: 11/28/2023
 *
 */
package com.pro.utils;

import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.linear.MatrixUtils;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.linear.SingularValueDecomposition;

public class ScipyNdImage {
    public static double getHeightOfPoint(double[] point) {
        if(point.length != 2) return 0;

        double height = bilinearInterpolation(MlatEnums.wgs84Geoid, point[0], point[1]);

        return (int)height;
    }
    private static double bilinearInterpolation(double[][] points, double x, double y) {
        int x0 = (int) Math.floor(x);
        int x1 = x0 + 1;
        int y0 = (int) Math.floor(y);
        int y1 = y0 + 1;

        double q11 = points[x0][y0];
        double q21 = points[x1][y0];
        double q12 = points[x0][y1];
        double q22 = points[x1][y1];

        double h1 = interplate1D(new double[]{y0, y1}, new double[]{q11, q21}, y);

        double h2 = interplate1D(new double[]{y0, y1}, new double[]{q12, q22}, y);

        double h = interplate1D(new double[]{x0, x1}, new double[]{h1, h2}, x);

        return h;
    }

    private static double interplate1D(double[] x, double[] h, double interVal) {
        double fraction = interVal - x[0];
        double height = h[0] + (h[1] - h[0]) * fraction;

        return height;
    }

    public static double[] solveLeastSquares(double[][] a, double[] b) throws MathIllegalArgumentException {
        // Create RealMatrix from 2D array 'a'
        RealMatrix matrixA = MatrixUtils.createRealMatrix(a);

        // Create RealMatrix from 1D array 'b'
        RealMatrix matrixB = MatrixUtils.createColumnRealMatrix(b);

        // Use the Singular Value Decomposition (SVD) method to solve the least squares problem
        SingularValueDecomposition svd = new SingularValueDecomposition(matrixA);
        RealMatrix solution;
        try {
            solution = svd.getSolver().solve(matrixB);
        } catch (MathIllegalArgumentException e) {
            // Handle exception
            throw e;
        }

        // Extract the solution vector as a double array
        return solution.getColumn(0);
    }



}
