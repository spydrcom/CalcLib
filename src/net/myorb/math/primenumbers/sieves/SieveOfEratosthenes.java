
package net.myorb.math.primenumbers.sieves;

import net.myorb.math.primenumbers.TableManager;

/**
 * 
 * implementation of Sieve Of Eratosthenes.
 * prime generation method based on marking composites by multiples of primes
 * (description available on wikipedia.org)
 * 
 * @author Michael Druckman
 *
 */
public class SieveOfEratosthenes extends SievePrimitives implements SieveDriver
{

	/**
	 * construct given a set of table management primitives
	 * @param tableManager the table manager object being used
	 */
	public SieveOfEratosthenes (TableManager tableManager)
	{
		super (tableManager);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.sieves.SieveDriver#getName()
	 */
	public String getName ()
	{
		return "Sieve Of Eratosthenes";
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.sieves.SieveDriver#generatePrimes()
	 */
	public void generatePrimes ()
	{
		int candidate = 1;
		markComposite (1, 1, 1);								// set first entry to be consistant

		while (true)
		{
			do
			{													// search table for next prime indicated by base[value] == 0 (markNotPresent)
				noteCount ();									// place current count into primeCounts (parallel to composites) list for use as PI function
				if (++candidate >= tableSize) return;			// done when the count has reached end of table, candidate is value being checked
			} while (markIsPresent (candidate));				// if no mark exists for this number then it is recognized as prime

			markPrimeAndCompositesFor (candidate);				// mark recognized value as prime and mark associated composites as well
		}
	}

}
