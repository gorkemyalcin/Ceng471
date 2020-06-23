package hw1;

import java.math.BigInteger;
import java.util.Random;

public class FermatLittleTheorem {

	private static boolean isPrime(BigInteger num, BigInteger testAmount) {
		while (testAmount.compareTo(BigInteger.ZERO) == 1) {
			BigInteger randomNum = randomBigInteger(num);
			if(randomNum.equals(BigInteger.ZERO)){
				return false;
			}
			if (!randomNum.modPow(num, num).equals(randomNum)) {
				return false;
			}
			testAmount = testAmount.subtract(BigInteger.ONE);
		}
		return true;
	}

	public static BigInteger randomBigInteger(BigInteger upperLimit) {
		if(upperLimit.equals(BigInteger.ZERO)) {
			return BigInteger.ZERO;
		}
		Random rand = new Random();
		BigInteger result;
		do {
			result = new BigInteger(upperLimit.bitLength(), rand); // (2^4-1) = 15 is the maximum value
		} while (result.compareTo(upperLimit) >= 0); // exclusive of 13
		return result;
	}

	public static void main(String[] args) {
		System.out.println("To check if 18532395500947174450709383384936679868383424444311405679463280782405796233163977 is prime in 10000 tries: ");
		System.out.println(isPrime(new BigInteger("18532395500947174450709383384936679868383424444311405679463280782405796233163977"), new BigInteger("10000")));
		System.out.println("To check if 18532395500947174450709383384936679868383424444311405679463280782405796233163978 is prime in 1000 tries: ");
		System.out.println(isPrime(new BigInteger("18532395500947174450709383384936679868383424444311405679463280782405796233163977"), new BigInteger("1000")));
		System.out.println("To check if 18532395500947174450709383384936679868383424444311405679463280782405796233163976 is prime in 10000 tries: ");
		System.out.println(isPrime(new BigInteger("18532395500947174450709383384936679868383424444311405679463280782405796233163976"), new BigInteger("10000")));
		System.out.println("To check if 100 is prime in 10000 tries: ");
		System.out.println(isPrime(new BigInteger("100"), new BigInteger("10000")));
		System.out.println("To check if 3 is prime in 1000 tries: ");
		System.out.println(isPrime(new BigInteger("3"), new BigInteger("1000")));
		System.out.println("To check if 2 is prime in 100 tries: ");
		System.out.println(isPrime(new BigInteger("2"), new BigInteger("100")));
		System.out.println("To check if 1 is prime in 100 tries: ");
		System.out.println(isPrime(new BigInteger("1"), new BigInteger("100")));
		System.out.println("To check if 0 is prime in 100 tries: ");
		System.out.println(isPrime(new BigInteger("0"), new BigInteger("100")));
	}
}
