package Others.G_Method;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;




public class Utils {
    public static void main(String[] args) {
        TypeACurveGenerator pg = new TypeACurveGenerator(160, 256);
        PairingParameters typeAParams = pg.generate();
        Pairing pairing = PairingFactory.getPairing(typeAParams);

        Element g = pairing.getG1().newRandomElement().getImmutable();

        Field Zp = pairing.getZr();
        Element r = Zp.newRandomElement().getImmutable();
        double t0 = System.currentTimeMillis();
        Element res1 = g.powZn(r);
        double t1 = System.currentTimeMillis();
        System.out.println("取幂操作耗时：" + (t1 - t0)+"ms");

        double t2 = System.currentTimeMillis();
        Element res2 = pairing.pairing(g, g).getImmutable();
        double t3 = System.currentTimeMillis();
        System.out.println("双线性配对操作耗时：" + (t3 - t2)+"ms");

        double t4 = System.currentTimeMillis();
        Element res3 = g.mul(g);
        double t5 = System.currentTimeMillis();
        System.out.println("G中乘法操作耗时：" + (t5 - t4)+"ms");
    }
}