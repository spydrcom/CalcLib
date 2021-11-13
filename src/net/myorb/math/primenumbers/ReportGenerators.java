
package net.myorb.math.primenumbers;

import java.math.BigInteger;

import java.io.PrintStream;
import java.util.List;

/**
 * 
 * generate reports from the prime factorizations tabulated
 * 
 * @author Michael Druckman
 *
 */
public class ReportGenerators extends FactorizationImplementationUsingBitlength
{

	/**
	 * the super class initialization builds the factorization table
	 * @param factorsToCompute the number of factorizations to be generated
	 */
	public ReportGenerators (int factorsToCompute)
	{
		super (factorsToCompute);
	}

	/**
	 * dump raw factors table to system.out
	 * @param out output stream
	 */
	public void dumpTable (PrintStream out)
	{
		dumpTable (2, base.length-1, out);
	}

	/**
	 * dump the raw factors table in range
	 * @param from starting point in table to be dumped
	 * @param to last entry in table to dump
	 * @param out output stream
	 */
	public void dumpTable (int from, int to, PrintStream out)
	{
		if (from > to || to >= base.length)
		{ throw new RuntimeException ("Bad dump range"); }
		else out.println ("Factors table between " + from + " & " + to);

		for (int i=from; i<=to; i++)
		{
			out.print (i);
			out.print (" = ");

			out.print (base[i]);
			if (baseFactors[i] > 1)
			{
				out.print ("^");
				out.print (baseFactors[i]);
			}

			if (multiplier[i] > 1)
			{
				out.print (" * ");
				out.print (formatMultiplier (i));
			}
			out.println ();
		}

		out.println ("---");
	}

	/**
	 * insert brackets {} around non-prime values
	 * @param value the table entry being formatted
	 * @return digits with non-prime flag when appropriate
	 */
	public String formatMultiplier (int value)
	{
		int m = multiplier[value];
		String digits = Integer.toString (m);
		if (!isPrime (m)) return "{" + digits + "}";
		return digits;
	}
	
	/**
	 * dump computed factors table to system.out
	 * @param out output stream
	 */
	public void dumpFactors (PrintStream out)
	{
		dumpFactors (2, base.length-1, out);
	}

	/**
	 * dump the factors table in range
	 * @param from starting point in table to be dumped
	 * @param to last entry in table to dump
	 * @param out output stream
	 */
	public void dumpFactors (int from, int to, PrintStream out)
	{
		if
			(
				from > to ||
				to >= base.length
			)
		{
			throw new RuntimeException ("Bad dump range");
		}

		out.println ("Primes factors between " + from + " & " + to);

		Factorization f;
		Factorization.Underlying implementation = Factorization.getImplementation ();
		for (int i=from; i<=to; i++)
		{
			out.print (i); out.print (" = ");
			out.print (f = implementation.factorsFor (i));
			if (f.getFactors ().isPrime ()) { out.print ("                *** PRIME ***"); }
			out.println ();
		}

		out.println ("---");
	}

	/**
	 * dump list of primes to stream
	 * @param out output stream
	 */
	public void dumpPrimes (PrintStream out)
	{
		out.println ("Primes between " + primes.get(0) + " & " + primes.get(primes.size()-1));
		out.println (primes);
	}

	/**
	 * dump the primes list in a range
	 * @param from the starting point of the range
	 * @param to the ending point
	 * @param out output stream
	 */
	public void dumpPrimes (int from, int to, PrintStream out)
	{
		if
			(
				from > to ||
				to > primes.size ()
			)
		{
			throw new RuntimeException ("Bad dump range");
		}

		out.println ("Primes between " + from + " & " + to);

		List<BigInteger> shortList = primes.subList (from, to);
		int range = primes.get(to).intValue() - primes.get(from).intValue() + 1, percent = shortList.size () * 10000 / range;
		out.print (shortList.size ()); out.print (" / "); out.print (range);
		out.print ("   "); out.print (percent); out.print ("/100%");
		out.print ("   "); out.println (shortList);
	}

	/**
	 * find primes in table between limits
	 * @param lo lowest of the range to dump
	 * @param hi highest of the range
	 */
	public void dumpPrimesBetween
	(int lo, int hi) { dumpPrimesBetween (lo, hi, System.out); }
	public void dumpPrimesBetween (int lo, int hi, PrintStream out)
	{
		int loIndex = 0, hiIndex = 0;
		for (int i=0; i<primes.size(); i++)
		{
			int prime = primes.get (i).intValue ();
			if (prime <= lo) loIndex = i;
			if (prime <= hi) hiIndex = i;
		}
		dumpPrimes (loIndex, hiIndex, out);
	}

	/**
	 * dump a range of prime numbers and the adjoining gaps
	 * @param lo the low end of the range
	 * @param hi the high value
	 * @param out output stream
	 */
	public void dumpPrimeGapsBetween (int lo, int hi, PrintStream out)
	{
		out.println ("Prime gaps " + lo + " - " + hi);

		int loIndex = 0, hiIndex = 0;
		for (int i=0; i<primes.size(); i++)
		{
			int prime = primes.get (i).intValue ();
			if (prime <= lo) loIndex = i;
			if (prime <= hi) hiIndex = i;
		}

		List<BigInteger> shortList = primes.subList (loIndex, hiIndex);

		BigInteger previous = shortList.get (0);
		for (BigInteger prime : shortList)
		{
			BigInteger gap = prime.subtract (previous);
			out.print (prime);
			out.print ("   ");
			out.println (gap);
			previous = prime;
		}
	}

	/**
	 * format dump of list of counts
	 * @param listOfCounts the list of counts captured
	 * @param blockSize the block size used for the capture
	 * @param out output stream
	 */
	public void dumpPrimeCounts (List<Integer> listOfCounts, int blockSize, PrintStream out)
	{
		out.println ("###");
		out.println ("# Prime generation dump by blocks of " + blockSize);
		out.println ("###");

		for (int count : listOfCounts)
		{
			out.println (count);
		}

		out.println ("###");
	}

	/**
	 * format display of prime counts
	 * @param out output stream
	 */
	public void dumpPrimeCounts (PrintStream out)
	{
		dumpPrimeCounts (primeCounts, getOptionParameter (DUMP_PRIME_GENERATION_BLOCK_SIZE), out);
	}
	
}
