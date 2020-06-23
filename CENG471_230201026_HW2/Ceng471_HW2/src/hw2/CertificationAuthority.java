package hw2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

import hw1.EuclidUtil;
import hw1.FastModularExponentiation;
import hw1.FermatLittleTheorem;

public class CertificationAuthority {

	private String name;
	private BigInteger privateKey;
	private BigInteger publicKey;
	private DomainParameters domainParameters;

	public CertificationAuthority(String name, BigInteger privateKey, DomainParameters domainParameters) {
		this.name = name;
		this.domainParameters = domainParameters;
		this.privateKey = privateKey;
		this.publicKey = domainParameters.getGenerator().modPow(privateKey,
				domainParameters.getPrime());/* Creates the public key as: publicKey = g^privateKey (mod prime) */
	}

	public boolean prepareCertificate(String issuerName, String certificateTextPath, DomainParameters domainParameters, BigInteger publicKeyOfIssuer) throws UnsupportedEncodingException {
		/*
		 * Prepares a .txt file with 
		 * Certificate Authority Name
		 * Issuer Name
		 * Issuer Public Key
		 * Domain Parameters
		 * Signed(with Certificate Authority's private key by Certificate Authority) public key of issuer.
		 */
		BigInteger randomNumber = FermatLittleTheorem
				.randomBigInteger(domainParameters.getPrime().subtract(BigInteger.ONE));
		while (!EuclidUtil.gcd(randomNumber, domainParameters.getPrime().subtract(BigInteger.ONE))
				.equals(BigInteger.ONE) || randomNumber.compareTo(BigInteger.ONE) < 1) {
			randomNumber = FermatLittleTheorem.randomBigInteger(domainParameters.getPrime().subtract(BigInteger.TWO));
		}
		BigInteger randomNumbersInverse = randomNumber.modInverse(domainParameters.getPrime().subtract(BigInteger.ONE));
		/*To create the signature pair s1,s2:
		 * s1 = g^k (mod p) where 1<k<p-1
		 * s2 = (g^Hash(message) - privateKey * s1) * k^-1 (mod p)
		 * 
		 * To compute the hash value, I used Java's own hashCode method.*/
		
		StringBuilder certificateContent = new StringBuilder("Certificate Authority Name: ").append(this.name)
				.append("\nIssuer Name: ").append(issuerName).append("\nIssuer Public Key: ").append(publicKeyOfIssuer)
				.append(domainParameters);
		BigInteger s1 = FastModularExponentiation.fastModularExponentiation(domainParameters.getGenerator(), randomNumber, domainParameters.getPrime());
		BigInteger certificateTextHash = new BigInteger(Long.toString(certificateContent.toString().hashCode()));		
		BigInteger s2 = (certificateTextHash.subtract(this.privateKey.multiply(s1))
				.multiply(randomNumbersInverse)).mod(domainParameters.getPrime().subtract(BigInteger.ONE));

		SignaturePair signaturePair = new SignaturePair(s1, s2);
		
		certificateContent.append("\nSignature of Certificate Authority: ").append(signaturePair.toString());
		
		try (BufferedWriter bufferedWriter = new BufferedWriter(
				new FileWriter("Certificate_" + issuerName + ".txt"));) {
			
			bufferedWriter.write(certificateContent.toString());
		} catch (IOException e) {
			System.out.println("Error while opening or creating the file");
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public BigInteger getPublicKey() {
		return publicKey;
	}

	public DomainParameters getDomainParameters() {
		return domainParameters;
	}

	public void setDomainParameters(DomainParameters domainParameters) {
		this.domainParameters = domainParameters;
	}

}
