package hw1;

import java.math.BigInteger;

public class EuclidUtil {

	public static BigInteger gcd(BigInteger p, BigInteger q) {
		BigInteger x = BigInteger.ZERO;
		BigInteger y = BigInteger.ONE;
		BigInteger oldX = BigInteger.ONE;
		BigInteger oldY = BigInteger.ZERO;
		BigInteger temp;
		while (!p.equals(BigInteger.ZERO)) {
			if (q.equals(BigInteger.ZERO)) {
				return p;
			}
			BigInteger newResult = p.mod(q);
			p = q;
			q = newResult;

			temp = x;
			x = oldX.subtract(q.multiply(x));
			oldX = temp;

			temp = y;
			y = oldY.subtract(q.multiply(y));
			oldY = temp;
		}
		return q;
	}

	public static BigInteger multiplicativeInverse(BigInteger number, BigInteger modulus) {
		{
			BigInteger mainModulus = modulus;
			BigInteger y = BigInteger.ZERO;
			BigInteger x = BigInteger.ONE;

			if (modulus.equals(BigInteger.ONE))
				return BigInteger.ZERO;

			while (number.compareTo(BigInteger.ONE) == 1) {
				System.out.println(modulus);
				BigInteger q = number.divide(modulus);
				BigInteger newModulus = modulus;

				modulus = number.mod(modulus);
				number = newModulus;
				newModulus= y;
				y = x.subtract(q.multiply(y));
				x = newModulus;
			}

			if (x.compareTo(BigInteger.ZERO) == -1)
				x = x.add(mainModulus);

			return x;
		}
	}

	public static boolean checkRelativelyPrime(BigInteger num1, BigInteger num2) {
		return num1.gcd(num2).equals(BigInteger.ONE);
	}

	public static void main(String[] args) {
		/*System.out.println("Find the GCD of 41234567890098765432 and 213213213213");
		System.out.println(gcd(new BigInteger("41234567890098765432"), new BigInteger("213213213213")));
		System.out.println("Find the GCD of 15 and 5");
		System.out.println(gcd(new BigInteger("15"), new BigInteger("5")));
		System.out.println("Find the multiplicative inverse of 15 and 11");*/
		System.out.println(multiplicativeInverse(new BigInteger("33"), new BigInteger("7")));
	/*	System.out.println("Find the multiplicative inverse of 15213 and 1123");
		System.out.println(multiplicativeInverse(new BigInteger("15213"), new BigInteger("1123")));
		System.out.println("Check if 41234567890098765432 and 155 is relatively prime or not");
		System.out.println(checkRelativelyPrime(new BigInteger("41234567890098765432"), new BigInteger("155")));
		System.out.println("Check if 5 and 15 is relatively prime or not");
		System.out.println(checkRelativelyPrime(new BigInteger("5"), new BigInteger("15")));
		System.out.println("Check if 3 and 2 is relatively prime or not");
		System.out.println(checkRelativelyPrime(new BigInteger("3"), new BigInteger("2")));*/
	}
}
