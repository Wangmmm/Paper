package code;

import Jama.Matrix;

import java.util.Random;

public class Help {
    public static Matrix  Generate(int l){
        Random r = new Random();
        double[][] m1 = new double[l][l];
        Matrix matrix = new Matrix(m1);
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < l; j++) {
                if (i == j) {
                    matrix.set(i, j, r.nextInt(10) + 1);
                } else {
                    matrix.set(i, j, 0);
                }
            }
        }
        return matrix;
    }
    public static Matrix Inverse(Matrix matrix){
        Matrix res = matrix.inverse();
        return res;
    }

}
