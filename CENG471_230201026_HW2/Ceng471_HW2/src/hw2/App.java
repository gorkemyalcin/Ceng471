package hw2;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class App {

	public static final DomainParameters domainParameters = new DomainParameters("23", "2"); /*Domain parameters can be changed from here to any value thus supporting the multi precision numbers.*/
	public static final String CA_NAME = "Görkem";

	public static void main(String[] args)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
		
		CertificationAuthority certificationAuthority = new CertificationAuthority(CA_NAME,
		new BigInteger("123456789123456789987654321987654321"), domainParameters); /*Gorkem is the CA's name, 123456789123456789987654321987654321 is the CA's private key, domain parameters are 23 and 2 which can be changed from the variable.*/
		User alice = new User("Alice", new BigInteger("4"), domainParameters);/*Alice is the name of the user with the private key 3 and with the same domain parameters*/
		System.out.println("Alice is created with the values:\n" + alice);
		User bob = new User("Bob", new BigInteger("3"), domainParameters);/*Bob is created*/
		System.out.println("\nBob is created with the values:\n" + bob +"\n");

		alice.createSecretKey();/*Alice creates her secret key*/
		System.out.println("Alice created her own private value");
		bob.createSecretKey();/*Bob creates his secret key.*/
		System.out.println("Bob created his own private value");

		System.out.println("\nCertificate Authority prepared certificates for Bob and Alice");
		certificationAuthority.prepareCertificate(alice.getName(), "Certificate_" + alice.getName() + ".txt", domainParameters, alice.getPublicKey());/*CA prepares a certificate for alice.*/
		certificationAuthority.prepareCertificate(bob.getName(), "Certificate_" + bob.getName() + ".txt", domainParameters, bob.getPublicKey());/*CA prepares a certificate for bob.*/
		
		boolean aliceVerificationForBob = alice.validateSignatureFromCertificate("Bob", CA_NAME, certificationAuthority.getPublicKey().toString());/*Alice validates bob's certificate using Bob's name to find the file and CA's name and public key.*/
		System.out.println("\nAlice verified Bob's certificate using the CA's public key. Result is: " + aliceVerificationForBob);

		boolean bobVerificationForAlice = bob.validateSignatureFromCertificate("Alice", CA_NAME, certificationAuthority.getPublicKey().toString());/*Bob validates alice's certificate with the same way.*/
		System.out.println("Bob verified Alice's certificate using the CA's public key. Result is: " + bobVerificationForAlice);

		if(aliceVerificationForBob && bobVerificationForAlice) {
			BigInteger aliceSharedSecretWithBob = alice.createSecretKeyWithSomeone(bob.getPrivateValue());/*Alice uses bob's private value to create a shared secret with Bob.*/
			BigInteger bobSharedSecretWithAlice = bob.createSecretKeyWithSomeone(alice.getPrivateValue());/*Bob uses alice's private value to create a shared secret with Alice.*/
			
			System.out.println("\nAlice created a shared secret with Bob. Alice's shared secret with Bob: " + aliceSharedSecretWithBob);
			System.out.println("Bob created a shared secret with Alice. Bob's shared secret with Alice: " + bobSharedSecretWithAlice);
			
			System.out.println("\nAlice signed a message(123454321)");
			SignedMessage signedMessage = alice.signMessage(new BigInteger("123454321"));/*Alice signs the 1112 message.*/
			System.out.println("Signed message: " + signedMessage);
			
			System.out.println("\nAlice encrypted the message with the shared secret.");
			String encryptedMessage = alice.encryptSignedMessage(signedMessage);/*Alice encrypts the signed message with AES using the shared secret.*/
			System.out.println("Encrypted message: " + encryptedMessage);
			
			System.out.println("\nBob decrypted the message with the shared secret.");
			String decryptMessage = bob.decryptMessage(encryptedMessage);/*Bob decrypts the cipher text using the shared secret.*/
			System.out.println("Decrypted message: " + decryptMessage);
			
			System.out.println("\nBob will use Alice's public key to validate the decrytped message");
			BigInteger alicePublicKey = alice.getPublicKey();
			System.out.println("Alice public key: " + alicePublicKey);
			
			System.out.println("\nBob is validating the message.");
			if(bob.validateMessage(decryptMessage, alicePublicKey)) {/*Bob validates the signature with alice's public key.*/
				System.out.println("Signature holds, message is valid.");
			}
			else {
				System.out.println("Signautre verification failed.");
			}
		}
		else {
			System.out.println("Certificate verification for Bob and Alice does not hold.");
		}
	}
}
