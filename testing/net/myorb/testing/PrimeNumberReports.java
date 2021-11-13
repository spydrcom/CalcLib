
package net.myorb.testing;

import net.myorb.math.primenumbers.*;
import net.myorb.math.primenumbers.sieves.SieveOfEratosthenes;

/**
 * 
 * test the functions provided by the prime number report generators
 * 
 * @author Michael Druckman
 *
 */
public class PrimeNumberReports extends ReportGenerators
{

	/**
	 * the super class initialization builds the factorization table
	 * @param factorsToCompute the number of factorizations to be generated
	 */
	public PrimeNumberReports (int factorsToCompute)
	{
		super (factorsToCompute);
	}

	/**
	 * generate 100 factors and dump primes and factor table
	 * @param support instance of report generator
	 */
	public static void first100 (PrimeNumberReports support)
	{
		System.out.println ("Stats for small (first 100) table generation");
		Factorization.setImplementation (support = new PrimeNumberReports (100));
		support.initFactorizationsWithStats (new SieveOfEratosthenes (support));
		support.dumpFactors (System.out); support.dumpTable (System.out);
		support.dumpPrimes (System.out);
		support.dumpBitsizeMap ();
		System.out.println ("***");
		System.out.println ();
	}

	/**
	 * compute 100,000 factorizations showing generation frequency
	 * @param support instance of report generator
	 */
	public static void first100k (PrimeNumberReports support)
	{
		System.out.println ("Stats for mid-size (100k) table generation");
		Factorization.setImplementation (support = new PrimeNumberReports (100*1000));
		support.setOptionParameter (DUMP_PRIME_GENERATION_BLOCK_SIZE, 1000);
		support.initFactorizationsWithStats (support);
		support.dumpPrimeCounts (System.out);
		System.out.println ("***");
		System.out.println ();
	}

	/**
	 * compute 4,000,000 factorizations for metric on timing and prime count
	 * @param support instance of report generator
	 * @return report generator
	 */
	public static PrimeNumberReports first4m (PrimeNumberReports support)
	{
		System.out.println ("Stats for large (4M) table generation");
		Factorization.setImplementation (support = new PrimeNumberReports (4*1000*1000));
		support.initFactorizationsWithStats ();
		System.out.println ("***");
		System.out.println ();
		return support;
	}

	/**
	 * last 100 Factorizations of each 1 million
	 * @param support instance of report generator
	 */
	public static void dumpFactors (PrimeNumberReports support)
	{
		System.out.println ("Factor Dump");
		//support.dumpFactors (2*1000*1000 - 100, 2*1000*1000 - 1);
		//support.dumpFactors (3*1000*1000 - 100, 3*1000*1000 - 1);
		support.dumpFactors (4*1000*1000 - 100, 4*1000*1000 - 1, System.out);
		System.out.println ("***");
		System.out.println ();
	}

	/**
	 * dump blocks of primes
	 * @param support instance of report generator
	 */
	public static void dumpPrimes (PrimeNumberReports support)
	{
		System.out.println ("Prime Dump");
		//support.dumpPrimes (support.primes.size()-2010, support.primes.size()-2000);
		//support.dumpPrimes (support.primes.size()-1010, support.primes.size()-1000);
		//support.dumpPrimes (support.primes.size()-10, support.primes.size());

		int start = 800, end = 300;
		support.dumpPrimesBetween (1*1000*1000 - start, 1*1000*1000 - end);
		support.dumpPrimesBetween (2*1000*1000 - start, 2*1000*1000 - end);
		support.dumpPrimesBetween (3*1000*1000 - start, 3*1000*1000 - end);
		support.dumpPrimesBetween (4*1000*1000 - start, 4*1000*1000 - end);
		System.out.println ("===");

		start += 500000; end += 500000;
		support.dumpPrimesBetween (1*1000*1000 - start, 1*1000*1000 - end);
		support.dumpPrimesBetween (2*1000*1000 - start, 2*1000*1000 - end);
		support.dumpPrimesBetween (3*1000*1000 - start, 3*1000*1000 - end);
		support.dumpPrimesBetween (4*1000*1000 - start, 4*1000*1000 - end);
		System.out.println ("===");

		start += 250000; end += 250000;
		support.dumpPrimesBetween (1*1000*1000 - start, 1*1000*1000 - end);
		support.dumpPrimesBetween (2*1000*1000 - start, 2*1000*1000 - end);
		support.dumpPrimesBetween (3*1000*1000 - start, 3*1000*1000 - end);
		support.dumpPrimesBetween (4*1000*1000 - start, 4*1000*1000 - end);
		System.out.println ("===");

		System.out.println ("***");
		System.out.println ();
	}

	/**
	 * dump blocks of prime gaps
	 * @param support instance of report generator
	 */
	public static void dumpPrimeGaps (PrimeNumberReports support)
	{
		System.out.println ("Prime gaps in range");
		support.dumpPrimeGapsBetween (1, 99, System.out);
		System.out.println ("***");
		System.out.println ();

		support.dumpPrimeGapsBetween
		(4*1000*1000 - 1000, 4*1000*1000 - 1, System.out);
		System.out.println ("***");
		System.out.println ();
	}

	/**
	 * show the table of primes approximating 2^n
	 * @param support instance of report generator
	 */
	public static void dumpPower2Primes (PrimeNumberReports support)
	{
		System.out.println ("Primes neighboring 2^n");
		support.dumpBitlengthPrimes ();
		System.out.println ("***");
		System.out.println ();
	}

	/**
	 * build tables and report stats on tables produced
	 * @param args not used
	 */
	public static void main (String... args)
	{
		PrimeNumberReports support = null;
		first100 (support); first100k (support);
		support = first4m (support);

		dumpPower2Primes (support);
		dumpFactors (support);
		dumpPrimes (support);

		dumpPrimeGaps (support);
	}

}
