package hw2;

import java.math.BigInteger;

public class SignaturePair {

	private BigInteger s1;
	private BigInteger s2;

	public SignaturePair(BigInteger s1, BigInteger s2) {
		this.s1 = s1;
		this.s2 = s2;
	}
	
	public String toString() {
		return "(" + s1.toString() + "," +s2.toString() + ")";
	}

	public BigInteger getS1() {
		return s1;
	}

	public void setS1(BigInteger s1) {
		this.s1 = s1;
	}

	public BigInteger getS2() {
		return s2;
	}

	public void setS2(BigInteger s2) {
		this.s2 = s2;
	}
}
