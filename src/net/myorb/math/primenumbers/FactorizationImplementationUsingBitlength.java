
package net.myorb.math.primenumbers;

import net.myorb.math.ComputationConfiguration;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * extending FactorizationImplementation to provide bitscale implementation of ListOfPrimes
 * 
 * @author Michael Druckman
 *
 */
public class FactorizationImplementationUsingBitlength extends FactorizationImplementation
{

	/**
	 * a list of prime list indexes to the enteries that approximate 2^n.
	 * in a factorization an approximation is made of SQRT(x) by taking bitlength/2;
	 * the resulting bit count is used as an index to this table to get the closest prime to the SQRT approximation
	 */
	protected final List<Integer> bitSizeMap;

	
	/**
	 * extending FactorizationImplementation allows addition of collection of bit size map list
	 * @param factorsToCompute value to pass to super constructor
	 */
	public FactorizationImplementationUsingBitlength (int factorsToCompute)
	{
		super (factorsToCompute);

		bitSizeMap = new ArrayList<Integer> ();					// bitsize and prime lists grow reletive to primes found during table generation

		bitSizeMap.add (0); 									// 0-1 bits map to first prime (2)
		bitSizeMap.add (0);										// 2^0 = 1 & 2^1 = 2

		nextBitMap = 4;											// 2^2 = 4
	}
	protected int nextBitMap;


	/********************************************************
	 * overrides of super class FactorizationImplementation *
	 ********************************************************/

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorizationImplementation#algotithmUsed()
	 */
	protected String algotithmUsed () { return "BINARY SQRT"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.FactorizationImplementation#markPrime(int)
	 */
	public void markPrime (int value)
	{
		super.markPrime (value);
		
		/*
		 * this override allows extra check for adding
		 * to bitSizeMap when appropriate
		 */
		
		if (value > nextBitMap)									// have we crossed next 2^n value
		{
			bitSizeMap.add (primes.size () - 1);				// copy index into list
			nextBitMap *= 2;									// next is now 2^(n+1)
		}
	}

	/************************************************************************************
	 * override of super class FactorizationImplementation implementation of Underlying *
	 ************************************************************************************/

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.Factorization.Underlying#getPrimesForCheck(java.math.BigInteger, int)
	 */
	public List<BigInteger> getPrimesForCheck
		(BigInteger value, int bitscale)
	{
		 return getListOfPrimesByBinarySqrt (bitscale);			// based on power of 2
	}

	/**
	 * use bit length mapping to approximate SQRT value
	 * @param bitlength number of bits in value being factorized
	 * @return a list of BigIntegers which include the primes identified by the implementation
	 */
	public List<BigInteger> getListOfPrimesByBinarySqrt (int bitlength)
	{
		int last = bitSizeMap.size() - 1;
		last = bitlength < last? bitSizeMap.get (bitlength): primes.size() - 1;
		if (isOptionEnabled (ComputationConfiguration.DUMP_PRIME_FACTORIZATION))
		{
			BigInteger p = primes.get (last);
			String fullflag = last==primes.size()-1? "<FULL>": "";
			System.out.println ("{ IDX: " + last + fullflag + " }");
			FactorizationImplementation.dumpMaxFactor (p, bitlength);
		}
		return primes.subList (0, last);
	}

	/*********************************************
	 * reports available related to bit size map *
	 *********************************************/

	/**
	 * bitsize map is the index points into primes map closest to powers of 2
	 */
	public void dumpBitsizeMap ()
	{
		System.out.print ("Bit Size Index Map: ");
		System.out.print (bitSizeMap);
		dumpBitlengthPrimes ();
	}

	/**
	 * dump the primes at the bitsize index points
	 */
	public void dumpBitlengthPrimes ()
	{
		int power = 1;
		List<Integer> bitSizeMappings = new ArrayList<Integer> (), powers = new ArrayList<Integer> (); powers.add (1);
		for (Integer i : bitSizeMap) { bitSizeMappings.add (primes.get (i).intValue ()); powers.add (power*=2); }
		System.out.print ("   Prime By Bit Size: "); System.out.println (bitSizeMappings);
		System.out.print ("       Powers of Two:  "); System.out.println (powers);
		System.out.println ("---");
	}

}
