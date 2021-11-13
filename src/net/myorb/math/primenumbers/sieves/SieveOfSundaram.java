
package net.myorb.math.primenumbers.sieves;

import net.myorb.math.primenumbers.TableManager;

import java.util.Arrays;

/**
 * 
 * implementation of Sieve Of Sundaram.
 * prime generation method based on marking composites by algebraic methods
 * (description available on wikipedia.org)
 * 
 * @author Michael Druckman
 *
 */
public class SieveOfSundaram extends SievePrimitives implements SieveDriver
{

	/**
	 * construct given a set of table management primitives
	 * @param tableManager the table manager object being used
	 */
	public SieveOfSundaram (TableManager tableManager)
	{
		super (tableManager);
	}

	/**
	 * translate sieve output to list of primes
	 * @param sieve the Sundaram elimination array result
	 */
	public void generatePrimes (boolean[] sieve)
	{
		int prime;
		markComposite (1, 1, 1);							// set first entry to be consistant
		markCompositesFor (2);
		markPrime (2);										// the algorithm does not generate 2 automatically

		for (int i=1; i<sieve.length; i++)					// table describes 1..length-1
		{
			if (sieve[i])									// the value "i" was not eliminated, associated prime is 2i+1
			{
				if ((prime = 2*i + 1) < tableSize)			// compute prime and check table range
				{
					markPrimeAndCompositesFor (prime);		// mark recognized value as prime and mark associated composites as well
				}
			}
		}
	}

	/*/
	 * 
	 * i + j + 2ij where:
	 * i,j in N, 1 <= i <= j
	 * i + j + 2ij <= n
	 * 
	/*/

	/**
	 * execute Sundaram algorithm to eliminate composites
	 * @param n the size of boolean elimination table to generate
	 * @return boolean array TRUE = not eliminated
	 */
	public static boolean[] executeSieve (int n)
	{
		boolean[] marked = new boolean[n + 1];
		Arrays.fill (marked, 0, n+1, true);

		for (int j = 1; j <= n/3; j++)
		{
			for (int i = 1; i <= j; i++)
			{
				int sum = i + j + 2*i*j;
				if (sum <= n) marked[sum] = false;
				else break;
			}
		}

		return marked;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.sieves.SieveDriver#generatePrimes()
	 */
	public void generatePrimes ()
	{
		generatePrimes (executeSieve (tableSize / 2 + 1));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.sieves.SieveDriver#getName()
	 */
	public String getName ()
	{
		return "Sieve Of Sundaram";
	}

}
