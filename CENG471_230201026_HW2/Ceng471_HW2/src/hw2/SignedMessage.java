package hw2;

import java.math.BigInteger;

public class SignedMessage {

	private BigInteger message;
	private SignaturePair signaturePair;

	public SignedMessage(BigInteger message, SignaturePair signaturePair) {
		this.message = message;
		this.signaturePair = signaturePair;
	}
	
	public String toString() {
		return message.toString() + "," + signaturePair.toString();
	}

	public BigInteger getMessage() {
		return message;
	}

	public void setMessage(BigInteger message) {
		this.message = message;
	}

	public SignaturePair getSignaturePair() {
		return signaturePair;
	}

	public void setSignaturePair(SignaturePair signaturePair) {
		this.signaturePair = signaturePair;
	}
}
