
package net.myorb.math.primenumbers;

import net.myorb.math.primenumbers.sieves.SieveOfEratosthenes;

import java.util.ArrayList;

/**
 * 
 * collect data about prime generation patterns
 * 
 * @author Michael Druckman
 *
 */
public class GenerationMonitorReport extends ReportGenerators
{

	/**
	 * extending FactorizationImplementation allows addition of collection of prime generation data
	 * @param factorsToCompute value to pass to super constructor
	 * @param blockSizeToUse block size
	 */
	public GenerationMonitorReport (int factorsToCompute, int blockSizeToUse)
	{
		super (factorsToCompute);
		
		capturedIndicies = new ArrayList<Integer> ();
		count = blockSize = blockSizeToUse;
		captureBlocks = true;
	}
	ArrayList<Integer> capturedIndicies;
	boolean captureBlocks = false;
	int blockSize = 0, count = 0;

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorizationImplementationUsingBitlength#markPrime(int)
	 */
	public void markPrime (int value)
	{
		super.markPrime (value);

		if (!captureBlocks) return;

		count -= value - lastSeen;
		lastSeen = value;

		if (count <= 0)										// end of block is found
		{													// prime count is captured at end of each block
			capturedIndicies.add (primes.size () - 1);		// this capture provides counts of primes generated at each block end (showing pattern)
			count += blockSize;								// count starts at block size and decrements to zero where list size is captured
		}
	}
	int lastSeen = 0;

	/**
	 * print contents of capture array
	 */
	public void formatReport ()
	{
		dumpPrimeCounts (capturedIndicies, blockSize, System.out);
	}

	/**
	 * compute 100,000 factorizations showing generation frequency
	 * @param support instance of report generator
	 * @return Generation Monitor Report
	 */
	public static GenerationMonitorReport first100k (GenerationMonitorReport support)
	{
		Factorization.setImplementation (support);
		System.out.println ("Stats for mid-size (100k) table generation");
		support.initFactorizationsWithStats (new SieveOfEratosthenes (support));
		System.out.println ("***");
		System.out.println ();
		return support;
	}

	/**
	 * build tables and report stats on tables produced
	 * @param args not used
	 */
	public static void main (String... args)
	{
		GenerationMonitorReport support =
			new GenerationMonitorReport (100*1000, 1000);
		first100k (support).formatReport ();
	}

}
