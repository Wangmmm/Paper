package FPGQ1;

import Jama.Matrix;
import code.Help;
import code.MatrixMethod;

import java.util.ArrayList;
import java.util.Random;


public class FGPQ_Coordination {

    public static void main(String[] args) {
        Random r = new Random();
        int l = 10;

        //定义2个可逆矩阵m1、m2，并填充随机数
        Matrix matrix1 = Help.Generate(l);
        Matrix matrix2 = Help.Generate(l);

        //定义比特串s
        int[] s = new int[l];
        for (int i = 0; i < l; i++) {
            s[i] = r.nextInt(2);
        }

        //定义73个随机数w (0~72)===(8~80)
        double[] w = new double[l-7];
        for (int i = 0; i < w.length; i++) {
            w[i] = r.nextInt(10);
            if (w[w.length - 1] == 0) {
                w[w.length - 1] = r.nextInt(10) + 1;
            }
        }

        //返回值
        double[][] res1 = FPGQ_Enc(l, matrix1, matrix2, s, w);
        double[] Ca_1 = new double[l];
        double[] Cb_1 = new double[l];
        for (int i = 0; i < l; i++) {
            Ca_1[i] = res1[0][i];
            Cb_1[i] = res1[1][i];
        }

        //返回值
        double[][] res3 = FGPQ_Trapdoor(l, matrix1, matrix2, s, w);
        double[] Ta_1 = new double[l];
        double[] Tb_1 = new double[l];
        for (int i = 0; i < l; i++) {
            Ta_1[i] = res3[0][i];
            Tb_1[i] = res3[1][i];
        }
        FPGQ_Compare(Ca_1, Cb_1, Ta_1, Tb_1);


    }


    public static double[][] FPGQ_Enc(int l, Matrix matrix1, Matrix matrix2, int[] s, double[] w) {
        //定义一个坐标点(x,y)=(5,9)
        double x = 5;
        double y = 9;
        double[][] res = new double[2][l];
        double[] pa = new double[l];
        double[] pb = new double[l];
        Random r = new Random();
        ArrayList<Double> p = new ArrayList<>();
        double t0 = System.currentTimeMillis();
        p.add(1.0);
        p.add(1.0);
        p.add(x * x);
        p.add(y * y);
        p.add(-2 * x);
        p.add(-2 * y);
        p.add(-1.0);

        double[] r1 = new double[l-8];
        for (int i=0;i<r1.length;i++) {
            r1[i] = r.nextInt(10);
            p.add(r1[i]);
        }

        double sum = 0;
        for (int i = 0; i < l-8; i++) {
            sum += r1[i] * w[i];
        }
        sum = -sum;
        double res1 = sum / w[l-8];
        p.add(res1);

//        System.out.println("sum:"+sum);
//        for (int i = 0; i < w.length; i++) {
//            System.out.print(w[i]+" ");
//        }
//        System.out.println();
//        for (int i = 0; i < r1.length; i++) {
//            System.out.print(r1[i]+" ");
//        }
//        System.out.println();
//        for (int i = 0; i < p.size(); i++) {
//            System.out.print(p.get(i)+" ");
//        }
//        System.out.println();

        //根据s[i]分裂
        for (int i = 0; i < l; i++) {
            if (s[i] == 1) {
                pa[i] = r.nextInt(10);
                pb[i] = p.get(i) - pa[i];
            } else {
                pa[i] = pb[i] = p.get(i);
            }
        }

        double[] Ca = MatrixMethod.VectorMul2(matrix1, pa);
        double[] Cb = MatrixMethod.VectorMul2(matrix2, pb);
        double t1 = System.currentTimeMillis();
        System.out.println("FPGQ加密耗时：" + (t1 - t0) + "ms");

        // 返回值
        for (int i = 0; i < l; i++) {
            res[0][i] = Ca[i];
            res[1][i] = Cb[i];
        }
        return res;
    }

    public static double[][] FGPQ_Trapdoor(int l, Matrix matrix1, Matrix matrix2, int[] s, double[] w) {
        //用户坐标(xu,yu)=(3,4);半径T=10
        double xu = 3;
        double yu = 4;
        double T= 10;
        double t5 = System.currentTimeMillis();
        Random r = new Random();
        double[] qa = new double[l];
        double[] qb = new double[l];
        double[][] res2 = new double[2][l];


        ArrayList<Double> q = new ArrayList<>();
        q.add(xu * xu);
        q.add(yu * yu);
        q.add(1.0);
        q.add(1.0);
        q.add(xu);
        q.add(yu);
        q.add(T * T);
        for (int i = 0; i < w.length; i++) {
            q.add(w[i]);
        }
        int b = 3;
        for (double i : q) {
            i = i * b;
        }

        for (int i = 0; i < l; i++) {
            if (s[i] == 0) {
                qa[i] = r.nextInt(10);
                qb[i] = q.get(i) - qa[i];
            } else {
                qa[i] = qb[i] = q.get(i);
            }
        }


        double[] Ta = MatrixMethod.VectorMul(Help.Inverse(matrix1), qa);
        double[] Tb = MatrixMethod.VectorMul(Help.Inverse(matrix2), qb);
        double t6 = System.currentTimeMillis();
        System.out.println("FPGQ陷门生成耗时：" + (t6 - t5) + "ms");
        for (int i = 0; i < l; i++) {
            res2[0][i] = Ta[i];
            res2[1][i] = Tb[i];
        }
        return res2;

    }

    public static void FPGQ_Compare(double[] Ca, double[] Cb, double[] Ta, double[] Tb) {
        double t7 = System.currentTimeMillis();
        double res = MatrixMethod.InnerProduct(Ca, Ta) + MatrixMethod.InnerProduct(Cb, Tb);
        double t8 = System.currentTimeMillis();
        System.out.println("FPGQ匹配耗时：" + (t7 - t8) + "ms");

    }


}
