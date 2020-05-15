package code;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;



public class Word {
    public static void main(String[] args) {

        Pairing pairing = PairingFactory.getPairing("a.properties");

        Element g1 = pairing.getG1().newRandomElement().getImmutable();
        Element g2 = pairing.getG1().newRandomElement().getImmutable();


        Field Zp = pairing.getZr();
        Element SKl = Zp.newRandomElement().getImmutable();
        Element PKl = g2.mulZn(SKl).getImmutable();


        Element SKs = Zp.newRandomElement().getImmutable();
        Element PKs = g1.mulZn(SKs).getImmutable();

        Element SKu = Zp.newRandomElement().getImmutable();
        Element PKu = g2.mulZn(SKu).getImmutable();


        String keyword = "hello";
        double t0 = System.currentTimeMillis();

        Element r = Zp.newRandomElement().getImmutable();  //生成随机数r

        Element hash_keyword = pairing.getZr().newElement().setFromHash(keyword.getBytes(),
                0, keyword.length()).getImmutable();

        Element CW1 = g1.mulZn(r);
        Element CW2 = g2.mulZn(r);

        Element temp1 = SKl.mulZn(hash_keyword).getImmutable();
        Element res1 = temp1.mulZn(r);

        Element temp2 = pairing.pairing(PKs, PKu).getImmutable();
        Element CW3 = temp2.powZn(res1);
        double t1 = System.currentTimeMillis();
        System.out.println("加密耗时：" + (t1 - t0)+"ms");

        double t2 = System.currentTimeMillis();
        Element r1 = Zp.newRandomElement().getImmutable();
        Element res_1 = SKu.mulZn(hash_keyword).getImmutable();
        Element res_2 =PKl.mulZn(res_1);
        Element res_3 = g2.mulZn(r1);

        Element T1 = res_2.add(res_3);
        Element T2 = PKs.mulZn(r1);
        double t3 = System.currentTimeMillis();
        System.out.println("陷门生成耗时：" + (t3 - t2) + "ms");

        double t4 = System.currentTimeMillis();
        Element U= pairing.pairing(CW1, T1).getImmutable();
        Element Left = U.powZn(SKs);

        Element V = pairing.pairing(T2, CW2).getImmutable();
        Element Right = CW3.mul(V);
        double t5 = System.currentTimeMillis();
        System.out.println("查询耗时：" + (t5 - t4) + "ms");
        System.out.println("Left:"+Left);
        System.out.println("Right:"+Right);


    }


}




