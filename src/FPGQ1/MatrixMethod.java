package FPGQ1;

import Jama.Matrix;

public class MatrixMethod {

    //矩阵与向量相乘
    public static double[] VectorMul(Matrix matrix, double[] p) {
        double[] C = new double[p.length];
        for (int i = 0; i < p.length; i++) {
            for (int j = 0; j < p.length; j++) {
                C[i] += matrix.get(i, j) * p[j];
            }
        }
        return C;
    }

    //向量与矩阵相乘
    public static double[] VectorMul2(Matrix matrix, double[] p) {
        double[] C = new double[p.length];
        for (int i = 0; i < p.length; i++) {
            for (int j = 0; j < p.length; j++) {
                C[i]+= p[j]*matrix.get(j,i);
            }
        }
        return C;
    }


   //向量内积运算

    public static double InnerProduct(double[] a, double[] b) {
        double res = 0;
        for (int i = 0; i < a.length; i++) {
            res += a[i] * b[i];
        }
        return res;
    }

    //向量除法
    public static double[] Division(double[] P,int b){
        for (int i = 0; i <P.length; i++) {
            P[i]=P[i]/b;
        }
        return P;
    }


}


