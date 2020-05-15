package code;

import java.util.ArrayList;
import java.util.Random;
import Jama.Matrix;


public class Code {

    public static void main(String[] args) {
        Random r = new Random();
        int l =80;

        //定义2个可逆矩阵m1、m2，并填充随机数
        Matrix matrix1 = Help.Generate(l);
        Matrix matrix2 = Help.Generate(l);

        //定义比特串s
        int[] s = new int[l];
        for (int i = 0; i < l; i++) {
            s[i] = r.nextInt(2);
        }

        //定义d个随机数
        double[] d = new double[l - 4];
        for (int i = 0; i < d.length; i++) {
            d[i] = r.nextInt(10);
            if (d[d.length - 1] == 0) {
                d[d.length - 1] = r.nextInt(10) + 1;
            }
        }

         //定义（loc_p,loc_1）的位置分别为1和2
         int loc_p = 1;
         int loc_1 = 2;

         //创建一个l-2维的基向量Oo
         double[] Oo = new double[l - 2];

        //返回值
         double[][] res1 = CircleTest_Enc(l, matrix1, matrix2, s, d);
         double[] Ca_1 = new double[l];
         double[] Cb_1 = new double[l];
         for (int i = 0; i < l; i++) {
             Ca_1[i] = res1[0][i];
             Cb_1[i] = res1[1][i];
         }

         //调用函数
        CircleTest_Dec(l, matrix1, matrix2, s, Ca_1, Cb_1);

        //返回值
        double[][] res3 = CircleTest_Trapdoor(l, matrix1, matrix2, s, d);
        double[] Ta_1 = new double[l];
        double[] Tb_1 = new double[l];
        for (int i = 0; i < l; i++) {
            Ta_1[i] = res3[0][i];
            Tb_1[i] = res3[1][i];
        }
        CircleTest_Compare(Ca_1, Cb_1, Ta_1, Tb_1);

         //调用函数
        double[] Cp_1 = RangeQuery_Enc(l, Oo, loc_p, loc_1, matrix2);
        RangeQuery_Dec(Cp_1,matrix2);
        double[][] res4 = RangeQuery_Trapdoor(l, Oo, loc_p, loc_1, matrix2);
        double[] T_1 = new double[l];
        double[] T_2 = new double[l];
        for (int i = 0; i < l; i++) {
            T_1[i] = res4[0][i];
            T_2[i] = res4[1][i];
        }
        RangeQuery_Compare(Cp_1, T_1, T_2);

    }

    /**
     * CircleTest 加密函数
     * @param l
     * @param matrix1
     * @param matrix2
     * @param s
     * @param d
     * @return
     */
    public static double[][] CircleTest_Enc(int l, Matrix matrix1, Matrix matrix2, int[] s, double[] d) {
        //定义一个坐标点(x,y)=(5,9)
        double x = 5;
        double y = 9;
        double[][] res = new double[2][l];
        double[] pa = new double[l];
        double[] pb = new double[l];
        Random r = new Random();
        ArrayList<Double> p = new ArrayList<>();
        double t0 = System.currentTimeMillis();
        p.add(x * x + y * y);
        p.add(-2 * x);
        p.add(-2 * y);
        p.add(1.0);
        for (int i = 0; i < l - 4; i++) {
            p.add(d[i]);
        }

        //根据s[i]分裂
        for (int i = 0; i < l; i++) {
            if (s[i] == 1) {
                pa[i] = r.nextInt(10);
                pb[i] = p.get(i) - pa[i];
            } else {
                pa[i] = pb[i] = p.get(i);
            }
        }
        matrix1.transpose();
        matrix2.transpose();

        double[] Ca = MatrixMethod.VectorMul(matrix1, pa);
        double[] Cb = MatrixMethod.VectorMul(matrix2, pb);
        double t1 = System.currentTimeMillis();
        System.out.println("加密坐标耗时：" + (t1 - t0) + "ms");

        // 返回值
        for (int i = 0; i < l; i++) {
            res[0][i] = Ca[i];
            res[1][i] = Cb[i];
        }
        return res;
    }

    /**
     * CircleTest解密函数
     * @param l
     * @param matrix1
     * @param matrix2
     * @param s
     * @param Ca
     * @param Cb
     */
    public static void CircleTest_Dec(int l, Matrix matrix1, Matrix matrix2, int[] s, double[] Ca, double[] Cb) {
        double t2 = System.currentTimeMillis();
        double[] Pa = MatrixMethod.VectorMul(Help.Inverse(matrix1), Ca);
        double[] Pb = MatrixMethod.VectorMul(Help.Inverse(matrix2), Cb);
        double[] P = new double[l];
        for (int i = 0; i < l; i++) {
            if (s[i] == 1) {
                P[i] = Pa[i] + Pb[i];
            } else {
                P[i] = Pa[i] = Pb[i];
            }
        }
        double t3 = System.currentTimeMillis();
        System.out.print("坐标明文为："+"（"+"x=" + -(P[1] / 2)+"，"+"y=" + -(P[2] / 2)+"）");
        System.out.println();
        System.out.println("解密坐标耗时：" + (t3 - t2) + "ms");
    }

    /**
     * CircleTest 陷门生成函数
     * @param l
     * @param matrix1
     * @param matrix2
     * @param s
     * @param d
     * @return
     */

    public static double[][] CircleTest_Trapdoor(int l, Matrix matrix1, Matrix matrix2, int[] s, double[] d) {
        //用户坐标(xu,yu)=(3,4);半径R=10
        double xu = 3;
        double yu = 4;
        double R = 10;
        double t5 = System.currentTimeMillis();
        Random r = new Random();
        double[] qa = new double[l];
        double[] qb = new double[l];
        double[][] res2 = new double[2][l];
        double[] r1 = new double[l - 5];
        for (int i = 0; i < l - 5; i++) {
            r1[i] = r.nextInt(10);
        }

        ArrayList<Double> q = new ArrayList<>();
        q.add(1.0);
        q.add(xu);
        q.add(yu);
        q.add(xu * xu + yu * yu - R * R);
        for (int i = 0; i < l - 5; i++) {
            q.add(r1[i]);
        }
        double sum = 0;
        for (int i = 0; i < l - 5; i++) {
            sum += r1[i] * d[i];
        }
        //System.out.println(sum);
        q.add(-sum);

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
        System.out.println("坐标陷门生成耗时：" + (t6 - t5) + "ms");
        for (int i = 0; i < l; i++) {
            res2[0][i] = Ta[i];
            res2[1][i] = Tb[i];
        }
        return res2;

    }

    /**
     * CircleTest测试函数
     * @param Ca
     * @param Cb
     * @param Ta
     * @param Tb
     */

    public static void CircleTest_Compare(double[] Ca, double[] Cb, double[] Ta, double[] Tb) {
        double t7 = System.currentTimeMillis();
        double res = MatrixMethod.InnerProduct(Ca, Ta) + MatrixMethod.InnerProduct(Cb, Tb);
        double t8 = System.currentTimeMillis();
        System.out.println("坐标匹配耗时：" + (t7 - t8) + "ms");

    }

    /**
     * RangeQuery加密函数
     * @param l
     * @param Oo
     * @param loc_p
     * @param loc_1
     * @param matrix
     * @return
     */

    public static double[] RangeQuery_Enc(int l, double[] Oo, int loc_p, int loc_1, Matrix matrix) {
        double sum = 0;
        Random r = new Random();
        for (int i = 0; i < Oo.length; i++) {
            Oo[i] = r.nextInt(10) + 1;
        }
        double[] Ov = new double[l - 2];
        for (int i = 0; i < l - 3; i++) {
            Ov[i] = r.nextInt(10);
        }
        for (int i = 0; i < l - 3; i++) {
            sum += Oo[i] * Ov[i];
        }
        Ov[Ov.length - 1] = (0 - sum) / (Oo[Oo.length - 1]);

//        for(double i:Oo){
//            System.out.print(i+" ");
//        }
//        System.out.println();
//        for(double i:Ov){
//            System.out.print(i+" ");
//        }
//        System.out.println();

        //假设价格Price为5
        int Price = 5;
        double t1 = System.currentTimeMillis();
        double[] P = new double[l];

        P[0] = Price;
        P[1] = -1;
        for (int i = 2; i < l; i++) {
            P[i] = Ov[i - 2];
        }

        for (int i = 0; i < l; i++) {
            //设置b=3
            P[i] = 3 * P[i];
        }

        double[] Cp = MatrixMethod.VectorMul(Help.Inverse(matrix), P);
//        for (double i: Cp){
//            System.out.print(i+" ");
//        }
        System.out.println();
        double t2 = System.currentTimeMillis();
        System.out.println("加密价格耗时：" + (t2 - t1) + "ms");
        return Cp;

    }

    /**
     * RangeQuery解密函数
     * @param Cp
     * @param matrix
     */
    public static void RangeQuery_Dec(double[] Cp ,Matrix matrix){
        double t7 = System.currentTimeMillis();
        double[] P1 = MatrixMethod.VectorMul(matrix, Cp);
        double[] res = MatrixMethod.Division(P1, 3);
        System.out.print("价格明文："+"P="+res[0]);
        System.out.println();
        double t8 = System.currentTimeMillis();
        System.out.println("解密价格耗时：" + (t8 - t7) + "ms");

    }

    /**
     * RangeQuery 陷门生成函数
     * @param l
     * @param Oo
     * @param loc_p
     * @param loc_1
     * @param matrix
     * @return
     */
    public static double[][] RangeQuery_Trapdoor(int l, double[] Oo, int loc_p, int loc_1, Matrix matrix) {
        //查询价格区间[P_left,P_right]=[10,20]
        int P_left = 10;
        int P_right = 20;
        double[][] res = new double[2][l];
        double t3 = System.currentTimeMillis();
        double[] P1 = new double[l];
        double[] P2 = new double[l];
        double[] Ou = new double[l - 2];
        Random r = new Random();
        int a = r.nextInt(10);
        for (int i = 0; i < l - 2; i++) {
            Ou[i] = a * Oo[i];
        }
        P1[0] = 1;
        P1[1] = P_left;
        P2[0] = 1;
        P2[1] = P_right;
        for (int i = 2; i < l; i++) {
            P1[i] = Ou[i - 2];
            P2[i] = Ou[i - 2];
        }
        Matrix m_Tran = matrix.transpose();
        double[] T1 = MatrixMethod.VectorMul(m_Tran, P1);
        double[] T2 = MatrixMethod.VectorMul(m_Tran, P2);
        double t4 = System.currentTimeMillis();
        System.out.println("价格陷门生成耗时：" + (t4 - t3) + "ms");

        for (int i = 0; i < l; i++) {
            res[0][i] = T1[i];
            res[1][i] = T2[i];
        }
        return res;
    }

    /**
     * RangeQuery测试函数
     * @param Cp
     * @param T1
     * @param T2
     */
    public static void RangeQuery_Compare(double[] Cp, double[] T1, double[] T2) {
        double t5 = System.currentTimeMillis();
        double res1 = MatrixMethod.InnerProduct(Cp, T1);
        double res2 = MatrixMethod.InnerProduct(Cp, T2);
        double t6 = System.currentTimeMillis();
        System.out.println("价格匹配耗时：" + (t6 - t5) + "ms");
    }



}








