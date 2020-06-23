package hw1;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Aes {

	public static String encrypt(String plainText, SecretKey secretKey) {
		byte[] plainTextByte = plainText.getBytes();
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			byte[] encryptedByte = cipher.doFinal(plainTextByte);

			Base64.Encoder encoder = Base64.getEncoder();
			return encoder.encodeToString(encryptedByte);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |

				BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decrypt(String cipherText, SecretKey secretKey) {
		try {
			Base64.Decoder decoder = Base64.getDecoder();
			byte[] encryptedTextByte = decoder.decode(cipherText);

			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
			return new String(decryptedByte);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static SecretKey generateKey() {
		KeyGenerator keyGenerator = null;
		try {
			keyGenerator = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyGenerator.init(128);
		return keyGenerator.generateKey();
	}

	public static void main(String[] args) {
		System.out.println(BigInteger.valueOf(8).hashCode());
		String plainText = "CENG471_TESTCIPHERTEXT";
		SecretKey key = generateKey();
		System.out.println("Before encrypting, plaintext:");
		System.out.println(plainText);
		String cipherText = encrypt(plainText, key);
		System.out.println("Encryption complete, the ciphertext is");
		System.out.println(cipherText);
		String plainTextDecrypted = decrypt(cipherText, key);
		System.out.println("Decryption complete, the plaintext is");
		System.out.println(plainTextDecrypted);

	}
}
