package Others.AES_Encryption;

public class testAES {

	//待加密的原文
	public static final String DATA = "（11，33）||hotel";
	
	public static void main(String[] args) throws Exception {
		//获得密钥
		byte[] aesKey = AESUtil.initKey();
		System.out.println("AES 密钥 : " + BytesToHex.fromBytesToHex(aesKey));
		//加密
		byte[] encrypt = AESUtil.encryptAES(DATA.getBytes(), aesKey);
		double t0 = System.currentTimeMillis();
		System.out.println("密文："+BytesToHex.fromBytesToHex(encrypt));
		double t1 = System.currentTimeMillis();
		System.out.println("AES加密耗时："+(t1-t0)+"ms");
		//解密
		byte[] plain = AESUtil.decryptAES(encrypt, aesKey);
		System.out.println(DATA + " AES 解密 : " + new String(plain));
	}
}
