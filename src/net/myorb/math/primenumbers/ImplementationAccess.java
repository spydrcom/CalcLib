
package net.myorb.math.primenumbers;

import net.myorb.math.ComputationConfiguration;

import java.math.BigInteger;

/**
 * 
 * static methods that expose access to implementation layer
 * 
 * @author Michael Druckman
 *
 */
public class ImplementationAccess
{

	/**
	 * check configuration to see if the dump option was set
	 * @return TRUE = dump option was selected
	 */
	public static boolean dumpSelected ()
	{
		return getImplementation ().isOptionEnabled
		(ComputationConfiguration.DUMP_PRIME_FACTORIZATION);
	}

	/**
	 * get the size of the factors table
	 * @return the count of the factors in the optimized table
	 */
	public static int getFactorizationCount ()
	{
		return getImplementation ().factorizationCount ();
	}

	/**
	 * call implementation method to get factors for value
	 * @param value the value to be factorized using implementation table
	 * @return the Factorization for the value
	 */
	public static Factorization factorsFor (int value)
	{
		return getImplementation ().factorsFor (value);
	}

	/**
	 * check for values that can be factored out of constant table
	 * @param value the value to be checked against range
	 * @return TRUE if expedited table can be used
	 */
	public static boolean isSmallEnough (BigInteger value)
	{ return getImplementation ().inTableRange (value); }

	/**
	 * get access to underlying implementation
	 * @return the implementation object
	 */
	public static Factorization.Underlying getImplementation ()
	{ return Factorization.getImplementation (); }

}
