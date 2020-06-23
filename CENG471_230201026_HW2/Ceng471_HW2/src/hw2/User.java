package hw2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import hw1.EuclidUtil;
import hw1.FastModularExponentiation;
import hw1.FermatLittleTheorem;

public class User {

	private String name;
	private BigInteger privateKey;// mod p
	private BigInteger publicKey;
	private DomainParameters domainParameters;
	private BigInteger secretKey;
	private BigInteger sharedSecret;
	private BigInteger privateValue;
	private static final String UTF = "UTF-8";
	private static final String AES = "AES";

	public User(String name, BigInteger privateKey, DomainParameters domainParameters) {
		this.name = name;
		this.domainParameters = domainParameters;
		this.privateKey = privateKey;
		this.publicKey = FastModularExponentiation.fastModularExponentiation(domainParameters.getGenerator(), privateKey, domainParameters.getPrime());
		this.secretKey = FermatLittleTheorem.randomBigInteger(domainParameters.getPrime().subtract(BigInteger.TWO));
	}
	
	public String toString() {
		return "Username: "+ name +"\nDomain Parameters: " + domainParameters.toString() +"\nPrivate key: " + privateKey + "\nPublic key: " + publicKey + "\nSecret key: " + secretKey;
	}

	public boolean validateSignatureFromCertificate(String nameOfValidatedUser, String knownCAName,
			String knownCAPublicKey) {
		try (BufferedReader bufferedReader = new BufferedReader(
				new FileReader("Certificate_" + nameOfValidatedUser + ".txt"))) {
			String caName = "";
			String userName = "";
			String caSignature = "";
			String line = bufferedReader.readLine();
			StringBuilder certificateText = new StringBuilder();
			if (line != null && line.startsWith("Certificate Authority Name: ")) {
				certificateText.append(line + "\n");
				caName = line.substring(28);
				line = bufferedReader.readLine();
				if (line.startsWith("Issuer Name: ")) {
					certificateText.append(line + "\n");
					userName = line.substring(13);
					line = bufferedReader.readLine();
					if (line.startsWith("Issuer Public Key: ")) {
						certificateText.append(line + "\n");
						line = bufferedReader.readLine();
						if (line.startsWith("Prime: ")) {
							certificateText.append(line + "\n");
							line = bufferedReader.readLine();
							if (line.startsWith("Generator: ")) {
								certificateText.append(line);
								line = bufferedReader.readLine();
								if (line.startsWith("Signature of Certificate Authority: ")) {
									caSignature = line.substring(36);
									return bufferedReader.readLine() == null; 
									/*
									 * At this point we have the issuer
									 * name, caName, ca's signature of the
									 * file and the whole file in a string.
									*/
								}
							}
						}
					}
				}

			}
			if (userName.equals(nameOfValidatedUser)
					&& caName.equals(knownCAName)) {/* If the names hold, validate the signature. */
				return validateSignature(certificateText.toString(), caSignature, knownCAPublicKey);
			}
		} catch (

		IOException e) {
			System.out.println("File is not found.");
		}
		return false;
	}

	private boolean validateSignature(String certificateText, String caSignature, String knownCAPublicKey) {
		/*
		 * Calculate the v1 = g^hash(m) (mod p) v2 = publicKeyOfCA^s1 * s1^s2 (mod p) if
		 * v1 == v2 then return true meaning that the signature holds, false otherwise.
		 */
		BigInteger v1 = FastModularExponentiation.fastModularExponentiation(domainParameters.getGenerator(), new BigInteger(String.valueOf((certificateText.toString()).hashCode())), domainParameters.getPrime());
		String s1 = findTheFirstValue(caSignature);
		String s2 = findTheSecondValue(caSignature);
		BigInteger v2 = FastModularExponentiation.fastModularExponentiation(BigInteger.valueOf(Long.valueOf(knownCAPublicKey)), BigInteger.valueOf(Integer.valueOf(s1)), domainParameters.getPrime());
		v2 = FastModularExponentiation.fastModularExponentiation(BigInteger.valueOf(Integer.valueOf(s1)),BigInteger.valueOf(Integer.valueOf(s2)),domainParameters.getPrime());
		return v1.equals(v2);
	}

	public SignedMessage signMessage(BigInteger message) {
		/*
		 * k = randomNumber which is between 2 and prime-2 s1 = g^k (mod p) s2 =
		 * (hash(m) - privateKey * s1)*k^-1 (mod p-1) signature with the message is ->
		 * message, (s1,s2)
		 */
		BigInteger randomNumber = FermatLittleTheorem
				.randomBigInteger(domainParameters.getPrime().subtract(BigInteger.TWO));
		while (!EuclidUtil.gcd(randomNumber, domainParameters.getPrime().subtract(BigInteger.ONE))
				.equals(BigInteger.ONE) || randomNumber.compareTo(BigInteger.ONE) != 1) {
			randomNumber = FermatLittleTheorem.randomBigInteger(domainParameters.getPrime().subtract(BigInteger.TWO));
		}
		BigInteger randomNumbersInverse = randomNumber.modInverse(domainParameters.getPrime().subtract(BigInteger.ONE));

		BigInteger s1 = FastModularExponentiation.fastModularExponentiation(domainParameters.getGenerator(), randomNumber, domainParameters.getPrime());

		BigInteger s2 = (new BigInteger(Integer.toString(message.hashCode())).subtract(this.privateKey.multiply(s1))
				.multiply(randomNumbersInverse)).mod(domainParameters.getPrime().subtract(BigInteger.ONE));

		return new SignedMessage(message, new SignaturePair(s1, s2));
	}

	public boolean validateMessage(String signedMessage, BigInteger otherUserPublicKey) {
		BigInteger message = new BigInteger(signedMessage.substring(0, signedMessage.indexOf(',')));
		String signaturePair = signedMessage.substring(signedMessage.indexOf(',') + 1);
		BigInteger r = new BigInteger(findTheFirstValue(signaturePair));
		BigInteger s = new BigInteger(findTheSecondValue(signaturePair));
		if (domainParameters.getPrime().compareTo(r) > 0 && r.compareTo(BigInteger.ZERO) > 0
				&& domainParameters.getPrime().compareTo(BigInteger.ZERO) > 0
				&& domainParameters.getPrime().subtract(BigInteger.ONE).compareTo(s) > 0) {
			BigInteger leftSide = FastModularExponentiation.fastModularExponentiation(domainParameters.getGenerator(), new BigInteger(String.valueOf(message.hashCode())), domainParameters.getPrime());
			BigInteger rightSide = FastModularExponentiation.fastModularExponentiation(otherUserPublicKey, r, domainParameters.getPrime());
			rightSide = rightSide.multiply(FastModularExponentiation.fastModularExponentiation(r, s, domainParameters.getPrime()));
			rightSide = rightSide.mod(domainParameters.getPrime());
			return leftSide.equals(rightSide);
		}
		return false;
	}

	public String encryptSignedMessage(SignedMessage signedMessage)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		byte[] key = sha.digest(sharedSecret.toString().getBytes(UTF));
		SecretKeySpec aesKey = new SecretKeySpec(key, AES);
		byte[] iv = new byte[16];
		new SecureRandom().nextBytes(iv);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));
		byte[] ciphertext = cipher.doFinal(signedMessage.toString().getBytes());
		byte[] encrypted = new byte[iv.length + ciphertext.length];
		System.arraycopy(iv, 0, encrypted, 0, iv.length);
		System.arraycopy(ciphertext, 0, encrypted, iv.length, ciphertext.length);
		return Base64.getEncoder().encodeToString(encrypted);
	}

	public String decryptMessage(String cipherText)
			throws NoSuchAlgorithmException, UnsupportedEncodingException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchPaddingException {
		MessageDigest sha = MessageDigest.getInstance("SHA-256");
		byte[] key = sha.digest(sharedSecret.toString().getBytes(UTF));
		SecretKeySpec aesKey = new SecretKeySpec(key, AES);
		byte[] encrypted = Base64.getDecoder().decode(cipherText);
		byte[] iv = new byte[16];
		System.arraycopy(encrypted, 0, iv, 0, iv.length);
		byte[] ciphertext = new byte[encrypted.length - iv.length];
		System.arraycopy(encrypted, iv.length, ciphertext, 0, ciphertext.length);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));
		return new String(cipher.doFinal(ciphertext), UTF);
	}

	public BigInteger getSharedSecret() {
		return sharedSecret;
	}

	public void setSharedSecret(BigInteger sharedSecret) {
		this.sharedSecret = sharedSecret;
	}

	public BigInteger getPrivateValue() {
		return privateValue;
	}

	public void setPrivateValue(BigInteger privateValue) {
		this.privateValue = privateValue;
	}

	private String findTheSecondValue(String caSignature) {
		int index = caSignature.indexOf(',');
		return caSignature.substring(index + 1, caSignature.length() - 1);
	}

	private String findTheFirstValue(String caSignature) {
		int index = caSignature.indexOf(',');
		return caSignature.substring(1, index);
	}

	public BigInteger createSecretKey() {
		this.privateValue = FastModularExponentiation.fastModularExponentiation(domainParameters.getGenerator(), secretKey, domainParameters.getPrime());
		return this.privateValue;
	}

	public BigInteger createSecretKeyWithSomeone(BigInteger theirValue) {
		System.out.println(theirValue +","+ secretKey +","+ domainParameters.getPrime());
		this.sharedSecret = FastModularExponentiation.fastModularExponentiation(theirValue, secretKey, domainParameters.getPrime());
		return this.sharedSecret;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigInteger getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(BigInteger privateKey) {
		this.privateKey = privateKey;
	}

	public BigInteger getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(BigInteger publicKey) {
		this.publicKey = publicKey;
	}

	public DomainParameters getDomainParameters() {
		return domainParameters;
	}

	public void setDomainParameters(DomainParameters domainParameters) {
		this.domainParameters = domainParameters;
	}

	public BigInteger getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(BigInteger secretKey) {
		this.secretKey = secretKey;
	}
}
