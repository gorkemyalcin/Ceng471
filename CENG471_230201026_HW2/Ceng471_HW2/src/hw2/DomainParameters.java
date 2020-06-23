package hw2;

import java.math.BigInteger;

public class DomainParameters {

	private BigInteger prime;
	private BigInteger generator;

	public DomainParameters(String prime, String generator) {
		this.prime = new BigInteger(prime);
		this.generator = new BigInteger(generator);
	}
	
	public String toString() {
		return "\nPrime: " + prime +"\nGenerator: " + generator; 
	}

	public BigInteger getGenerator() {
		return generator;
	}

	public void setGenerator(BigInteger generator) {
		this.generator = generator;
	}

	public BigInteger getPrime() {
		return prime;
	}

	public void setPrime(BigInteger prime) {
		this.prime = prime;
	}
}
