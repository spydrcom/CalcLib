
package net.myorb.math;

import java.math.BigInteger;
import java.util.Hashtable;

/**
 * collect properties for libraries to use in computational approximations
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ComputationConfiguration<T> extends SignManager
{

	/**
	 * map from names of option to the value to be used
	 */
	public static class OptionMapping extends Hashtable<String, Integer>
	{ private static final long serialVersionUID = -2481735277277985774L; }
	
	/**
	 * a mapping of operqations to the defaults to be used
	 */
	public static final OptionMapping DEFAULT_TERM_COUNTS = new OptionMapping ();

	/**
	 * value for default in absense of any other setting found
	 */
	public static final Integer DEFAULT_TERM_COUNT = 5;
	
	/**
	 * set the value for the named option in  the specified mapping
	 * @param forOptionNamed name of the operation to be changed in specified map
	 * @param inMap the mapping object to be modified
	 * @param toValue the new value to use
	 */
	public static void setOption
	(String forOptionNamed, OptionMapping inMap, Integer toValue)
	{ inMap.put (forOptionNamed, toValue); }

	/**
	 * set the term count for the named operation in the default mapping
	 * @param forOperationNamed name of the operation to be changed in the default map
	 * @param toValue the new value to use as default
	 */
	public static void setDefaultTermCount
	(String forOperationNamed, Integer toValue)
	{ setOption (forOperationNamed, DEFAULT_TERM_COUNTS, toValue); }

	/**
	 * get the value for the named option from the specified mapping
	 * @param forOptionNamed name of the operation
	 * @param inMap the mapping to look in
	 * @return the term count
	 */
	public static Integer getOptionValue
	(String forOptionNamed, OptionMapping inMap)
	{
		if (!inMap.containsKey (forOptionNamed))
		{
			return DEFAULT_TERM_COUNT;
		}
		return inMap.get (forOptionNamed);
	}

	/**
	 * create a mapping object and keep access in this object
	 */
	public void allocateIterationMap ()
	{
		termCounts = new OptionMapping ();
		termCounts.putAll (DEFAULT_TERM_COUNTS);
	}
	protected OptionMapping selectedOptions = null;
	protected OptionMapping termCounts = null;

	/**
	 * set the value of an option
	 * @param forOptionNamed the name of the option
	 * @param toValue the value to attribute to the named option
	 */
	public void setOptionParameter
	(String forOptionNamed, Integer toValue)
	{
		if (selectedOptions == null)
			selectedOptions = new OptionMapping ();
		selectedOptions.put (forOptionNamed, toValue);
	}

	/**
	 * determine if a named option was selected
	 * @param forOptionNamed the name of the option
	 * @return TRUE = option was selected
	 */
	public boolean isSelected (String forOptionNamed)
	{ return selectedOptions != null && selectedOptions.containsKey (forOptionNamed); }

	/**
	 * mark a named option as selected
	 * @param forOptionNamed the name of the option
	 */
	public void markOptionSelected (String forOptionNamed)
	{ setOptionParameter (forOptionNamed, 1); }

	/**
	 * remove an option from the map
	 * @param forOptionNamed the name of the option
	 */
	public void markOptionDeselected (String forOptionNamed)
	{ selectedOptions.remove (forOptionNamed); }

	/**
	 * get an option value
	 * @param forOptionNamed the name of the option
	 * @return the value of the option parameter
	 */
	public Integer getOptionParameter (String forOptionNamed)
	{ return selectedOptions.get (forOptionNamed); }

	/**
	 * alternate version of option selection check
	 * @param named the name of the option to be checked
	 * @return TRUE if option selected
	 */
	public boolean isOptionEnabled (String named) { return isSelected (named); }

	/**
	 * set the term count for the named operation in  the local mapping
	 * @param forOperationNamed name of the operation
	 * @param toValue the new value to use
	 */
	public void setTermCount
	(String forOperationNamed, Integer toValue)
	{ termCounts.put (forOperationNamed, toValue); }

	/**
	 * get the term count for named operation from the local mapping
	 * @param forOperationNamed name of the operation
	 * @return the term count
	 */
	public Integer getTermCount (String forOperationNamed)
	{
		return getOptionValue (forOperationNamed, termCounts);
	}


	/**
	 * display maximum factor value as part of trace
	 * @param sqrt the maximum potential factor is treated as the sqrt of the value
	 * @param bitLength number of bits in original value
	 */
	public static void dumpMaxFactor (BigInteger sqrt, int bitLength)
	{
		System.out.println ("[ bits: " + bitLength + ", ~sqrt: " + sqrt + ", ~sqrt^2: " + (sqrt.multiply (sqrt)) + " ]");
	}


	/**
	 * establish an object to be used for reduction of terms
	 * @param reductionMechanism the reference to the reduction mechanism object
	 */
	public void setReductionMechanism
		(ReductionMechanism<T> reductionMechanism)
	{ this.reductionMechanism = reductionMechanism; }
	protected ReductionMechanism<T> reductionMechanism;

	/**
	 * get access to the reduction mechanism object
	 * @return access to the reduction mechanism object
	 */
	public ReductionMechanism<T> getReductionMechanism ()
	{ return reductionMechanism; }


	// symbols for access to term count for the operation
	public static final String SIN, COS, ASIN, ATAN, SQRT, EXP, LOG;
	
	// set the default values for each symbol
	
	static
	{
		setDefaultTermCount (EXP = "exp", 15);
		setDefaultTermCount (COS = "cos", 10);
		setDefaultTermCount (SQRT = "sqrt", 200);
		setDefaultTermCount (ATAN = "atan", 500000);
		setDefaultTermCount (ASIN = "asin", 10);
		setDefaultTermCount (SIN = "sin", 10);
		setDefaultTermCount (LOG = "log", 50);
	}

	/**
	 * options for providing trace on iterative terms and factorizations
	 */
	public static final String
	DUMP_DISTRIBUTED_OPERATIONS = "DUMP_DISTRIBUTED_OPERATIONS",
	DUMP_PRIME_GENERATION_BLOCK_SIZE = "PRIME_GENERATION_BLOCK_SIZE",
	DUMP_ITERATIVE_TERM_VALUES = "DUMP_ITERATIVE_TERM_VALUES",
	DUMP_PRIME_FACTORIZATION = "DUMP_PRIME_FACTORIZATION";

	/**
	 * hash 2 booleans into integer 0-3 as a bit mask
	 * @param x left side boolean value
	 * @param y right side boolean
	 * @return bit mask value
	 */
	public static int bitHash (boolean x, boolean y)
	{
		int hash = x? LEFT_BIT_ONLY: NEITHER_BIT;
		hash += y? RIGHT_BIT_ONLY: BIT_NOT_SET;
		return hash;
	}

	/**
	 * this implies a bit hash has come up outside range 0-3
	 */
	public static void internalError ()
	{ throw new RuntimeException ("Internal error"); }

	/**
	 * throw RuntimeException with message
	 * @param message text of message to be carried
	 * @return nothing returned, only used for error conditions
	 * @throws RuntimeException with error message
	 */
	public T raiseException (String message) throws RuntimeException
	{ throw new RuntimeException (message);	}

	/**
	 * hash left and right side value objects into bit mask of sign flags
	 * @param x left side value object
	 * @param y right side object
	 * @return sign flag mask
	 */
	public static int signHash (SignManagementOperations x, SignManagementOperations y)
	{
		return bitHash (x.isNegative(), y.isNegative());
	}

	/**
	 * bit mask constants
	 */
	public static final int
		NEITHER_BIT = 0, BIT_NOT_SET = 0,
		RIGHT_BIT_ONLY = 1,
		LEFT_BIT_ONLY = 2,
		BOTH_BITS = 3;

}
