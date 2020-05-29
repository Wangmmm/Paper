package EPMQ1;

import Jama.Matrix;
import code.Help;
import code.MatrixMethod;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;

import java.util.Random;

public class EPMQ1 {
    public static void main(String[] args) {
        TypeACurveGenerator pg = new TypeACurveGenerator(160,512);
        PairingParameters typeAParams = pg.generate();
        Pairing pairing = PairingFactory.getPairing(typeAParams);
        Element g = pairing.getG1().newRandomElement().getImmutable();
        Field Zp = pairing.getZr();
        Element x = Zp.newRandomElement().getImmutable();  //随机生成主密钥x
        Element ru = Zp.newRandomElement().getImmutable();  //生成随机ru
        Element xu = Zp.newRandomElement().getImmutable();  //生成随机xu

        String keyword = "hotel";
        ///将“keyword”这个字符进行哈希计算之后映射到G1群上某一个元素，构造关键字的时候可以在这一步构造
        Element hash_q = pairing.getG1().newElement().setFromHash(keyword.getBytes(), 0, keyword.length()).getImmutable();

        double t0 = System.currentTimeMillis();
        Enc(g,pairing,x);
        double t1 = System.currentTimeMillis();
        System.out.println("加密耗时："+(t1-t0)+"ms");

        double t2 = System.currentTimeMillis();
        trapdoor(g,pairing,Zp,ru,xu,hash_q);
        double t3 = System.currentTimeMillis();
        System.out.println("陷门生成耗时："+(t3-t2)+"ms");

        double t4 = System.currentTimeMillis();
        search(g,pairing,x,ru,xu,hash_q);
        double t5 = System.currentTimeMillis();
        System.out.println("检索耗时："+(t5-t4)+"ms");
    }


    

    public static void Enc (Element g, Pairing pairing, Element x){
        String Cd = "(1,2)";
        Element hash_Cd = pairing.getG1().newElement().setFromHash(Cd.getBytes(), 0,Cd.length()).getImmutable();
        Element res1 = hash_Cd.powZn(x);
        Element E1_Cd = pairing.pairing(res1, g).getImmutable();

        Matrix M = Help.Generate(3);
        double xd = 3;
        double yd = 6;
        double temp = -0.5 * (xd * xd + yd * yd);
        double[] coordinate = {xd, yd, temp};
        double[] E2 = MatrixMethod.VectorMul(M, coordinate);

    }
    public static void trapdoor (Element g, Pairing pairing, Field Zp, Element ru, Element xu, Element hash_q){


        Element U = hash_q.powZn(xu); //h1(q)^xu

        Element res1 = xu.mul(ru);
        Element V = hash_q.powZn(res1);//h1(q)^xu*ru

        Matrix M = Help.Generate(3);
        Matrix M_inverse = Help.Inverse(M);
        Random R = new Random();
        int r = R.nextInt()+1;

        double Xu = 4;
        double Yu = 8;
        double[] user_coord = {r*Xu, r*Yu, r};
        double[] res = MatrixMethod.VectorMul(Help.Inverse(M), user_coord);

    }

    public static void search(Element g,Pairing pairing,Element x, Element ru, Element xu, Element hash_q){
        Element res1 = x.div(xu);
        Element res2 = res1.add(ru);
        Element Enrk_u = g.powZn(res2);


        Element U = hash_q.powZn(xu);

        Element res3 = xu.mul(ru);
        Element V = hash_q.powZn(res3);


        Element T1 = pairing.pairing(Enrk_u,U ).getImmutable();// 计算e（Enrk_u,h1(q)^xu）
        Element T2 = pairing.pairing(g,V).getImmutable();// 计算e（g,h1(q)^xu*ru）
        Element E1_C = T1.div(T2);
    }

}




