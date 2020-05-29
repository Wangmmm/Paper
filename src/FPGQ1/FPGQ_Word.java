package FPGQ1;


import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;


public class FPGQ_Word {
    public static void main(String[] args) {
        TypeACurveGenerator pg = new TypeACurveGenerator(160,512);
        PairingParameters typeAParams = pg.generate();
        Pairing pairing = PairingFactory.getPairing(typeAParams);
        Element g = pairing.getG1().newRandomElement().getImmutable();
        Field Zp = pairing.getZr();
        Element a = Zp.newRandomElement().getImmutable();  //随机生成主密钥a
        Encryption(g, pairing, a, Zp);
    }

    public static void Encryption (Element g, Pairing pairing, Element a, Field Zp){
        String keyword = "hotel";

        double t0 = System.currentTimeMillis();
        Element r = Zp.newRandomElement().getImmutable();  //生成随机r
        Element hash_keyword = pairing.getZr().newElement().setFromHash(keyword.getBytes(), 0, keyword.length()).getImmutable();
        Element res1 = a.mul(hash_keyword);
        Element res2 = res1.mul(r);
        Element U = g.powZn(res2);

        Element V = g.powZn(r);
        double t1 = System.currentTimeMillis();
        System.out.println("加密耗时：" + (t1 - t0) + "ms");


        double t2 = System.currentTimeMillis();
        Element r1 = Zp.newRandomElement().getImmutable();  //生成随机r1
        Element hash_keyword1 = pairing.getZr().newElement().setFromHash(keyword.getBytes(), 0, keyword.length()).getImmutable();
        Element res3 = a.mul(hash_keyword1);
        Element res4 = res3.mul(r1);
        Element M = g.powZn(res4);

        Element N = g.powZn(r1);
        double t3 = System.currentTimeMillis();
        System.out.println("陷门生成耗时：" + (t3 - t2) + "ms");

        double t4 = System.currentTimeMillis();
        Element T1 = pairing.pairing(U, N).getImmutable();// 计算e（U,M）
        Element T2 = pairing.pairing(V, M).getImmutable();// 计算e（V,N）
        double t5 = System.currentTimeMillis();
        System.out.println("查询耗时：" + (t5 - t4) + "ms");

    }


}





