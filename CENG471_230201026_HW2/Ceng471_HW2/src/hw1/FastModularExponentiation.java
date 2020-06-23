package hw1;

import java.math.BigInteger;

public class FastModularExponentiation {

	public static BigInteger fastModularExponentiation(BigInteger base, BigInteger power, BigInteger modulus) {
		BigInteger result = BigInteger.ONE;
		base = base.mod(modulus);
		while (power.compareTo(BigInteger.ZERO) > 0) {
			if (power.mod(BigInteger.TWO).equals(BigInteger.ONE)) {
				result = result.multiply(base);
				result = result.mod(modulus);
			}
			power = power.divide(BigInteger.TWO);
			base = base.multiply(base);
			base = base.mod(modulus);
		}
		return result;
	}

	public static void main(String[] args) {
		long startTime = System.nanoTime();
		System.out.println(
				fastModularExponentiation(new BigInteger("343281"), new BigInteger("327847324"), new BigInteger("39")));
		System.out.println("Fast modular exponentiation of 4222 to 1333 (mod 4957)");
		System.out.println(new BigInteger("343281").modPow(new BigInteger("327847324"), new BigInteger("39")));
		// System.out.println(fastModularExponentiation(new BigInteger("4222"), new
		// BigInteger("1333"), new BigInteger("4957")));
		long endTime = System.nanoTime();
		System.out.println("Calculation took: " + (endTime - startTime) / 1e6 + " milliseconds.");
		System.out.println("Fast modular exponentiation of 5 to 3 (mod 35)");
		long startTime1 = System.nanoTime();
		System.out.println(fastModularExponentiation(new BigInteger("5"), new BigInteger("3"), new BigInteger("35")));
		long endTime1 = System.nanoTime();
		System.out.println("Calculation took: " + (endTime1 - startTime1) / 1e6 + " milliseconds.");
	}
}
