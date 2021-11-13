
package net.myorb.math.primenumbers;

import net.myorb.math.expressions.gui.DisplayConsole;
import net.myorb.math.expressions.managers.*;

import java.io.PrintStream;

import java.util.Map;
import java.util.HashMap;

import java.util.Arrays;
import java.util.Set;

import java.math.BigInteger;

/**
 * evaluate a factorization and attempt to reduce "blob" size
 * @author Michael Druckman
 */
public class FactorAdjustment
{


	static final BigInteger V100 = BigInteger.valueOf (100);

	public FactorizationManager mgr = new FactorizationManager ();
	public ExpressionFactorizedFieldManager ffm = new ExpressionFactorizedFieldManager ();


	/**
	 * format display of a factorization
	 * @param tag the text to be put as prompt in display
	 * @param value the value to be displayed
	 */
	public void formalDisplay (String tag, Factorization value)
	{
		ffm.setDisplayPrecision (30);
		out.println (tag + ffm.format (value));
		ffm.resetDisplayPrecision ();
	}
	public void floatingDisplay (String tag, Factorization value)
	{
		out.println (tag + ffm.toNumber (value));
	}

	/**
	 * format decimal only display of a factorization
	 * @param tag the text to be put as prompt in display
	 * @param value the value to be displayed
	 */
	public void simpleDecimalDisplay (String tag, Factorization value)
	{
		out.println (tag + ffm.toDecimalString (value));
	}


	/**
	 * sort a set of big integers
	 * @param set the set to be sorted
	 * @return sorted array of set values
	 */
	public BigInteger[] sort (Set<BigInteger> set)
	{
		BigInteger[] values = set.toArray (new BigInteger[1]);
		Arrays.sort (values);
		return values;
	}

	/**
	 * get largest value from set
	 * @param set the set to be evaluated
	 * @return the largest value from set
	 */
	public BigInteger largestOf (Set<BigInteger> set)
	{
		BigInteger[] values = sort (set);
		return values[values.length - 1];
	}

	/**
	 * get smallest value from set
	 * @param set the set to be evaluated
	 * @return the smallest value from set
	 */
	public BigInteger smallestOf (Set<BigInteger> set)
	{
		BigInteger[] values = sort (set);
		return values[0];
	}


	/**
	 * look at nearby values for more orderly factor set
	 * @param value the value to be examined and adjusted
	 * @param mapOfFactors map from blob to factor set
	 */
	public void mapPossibilities
	(BigInteger value, Map<BigInteger,Factorization> mapOfFactors)
	{
		String flag;
		BigInteger rep = value.subtract (V100);
		BigInteger ofs = V100.negate ();
		Factorization f;

		for (int i=0; i<201; i++)
		{
			f = mgr.attemptFactorization (rep);

			mapOfFactors.put (largestOf (f.getFactors ().getPrimes ()), f);

			if (provideVerboseAnalysis)
			{
				flag = rep.isProbablePrime (20)? "*": " ";
				out.println (ofs + " " + flag + rep + ":   " + f);
				ofs = ofs.add (BigInteger.ONE);
			}

			rep = rep.add (BigInteger.ONE);
		}
	}


	/**
	 * replace blob factor with close approximation
	 * @param value the value to be examined and adjusted
	 * @return adjusted factorization
	 */
	public Factorization reduce (BigInteger value)
	{
		BigInteger smallestPrime;
		Factorization smallestPrimeFactors;
		Map<BigInteger,Factorization> possibilities;

		if (provideVerboseAnalysis) out.println ("=====");
		mapPossibilities (value, possibilities = new HashMap<BigInteger,Factorization> ());
		smallestPrimeFactors = possibilities.get (smallestPrime = smallestOf (possibilities.keySet ()));

		if (provideVerboseAnalysis)
		{
			int
				fullValueSize = value.bitLength (),
				blobSize = smallestPrime.bitLength (),
				reducedSize = fullValueSize - blobSize;
			BigInteger smallestBlob = smallestPrimeFactors.reduce ();

			out.println ("=====");
			out.println ("Reduction: " + reducedSize + " / " + fullValueSize);
			out.println ("Most Reduced:  " + smallestBlob + ":   " + smallestPrimeFactors);
		}

		return smallestPrimeFactors;
	}


	/**
	 * find largest factor
	 * @param value the value to be examined
	 * @return the largest prime of the factorization
	 */
	public BigInteger identifyBlob (Factorization value)
	{
		return largestOf (value.getFactors ().getPrimes ());
	}


	/**
	 * replace identified blob with best found substitute
	 * @param value the value to be examined
	 * @return updated value
	 */
	public Factorization replaceBlob (Factorization value)
	{
		BigInteger blob;
		Factorization reducedBlob = reduce (blob = identifyBlob (value));
		Factorization nOverBlob = value.divideBy (mgr.attemptFactorization (blob));
		return nOverBlob.multiplyBy (reducedBlob);
	}


	/**
	 * show analysis of blob and selected substitute
	 * @param value the value to be examined
	 * @return updated value
	 */
	public Factorization substituteAndAnalyze (Factorization value)
	{
		Factorization alternate;
		provideVerboseAnalysis = out != null;
		formalDisplay ("alternate:  ", alternate = replaceBlob (value));
		//formalDisplay ("error:  ", value.add (alternate.negate ()));
		floatingDisplay ("error:  ", value.add (alternate.negate ()));
		provideVerboseAnalysis = false;
		return alternate;
	}
	boolean provideVerboseAnalysis = false;


	/**
	 * build console for display
	 * @param size the size of the frame
	 * @return a stream for the console
	 */
	public static PrintStream consoleStreamFor (int size)
	{
		return DisplayConsole.showConsole ("Factor Adjustment", new DisplayConsole.StreamProperties (), size);
	}


	/**
	 * redirect output to print stream
	 * @param out the stream to be used
	 */
	public FactorAdjustment (java.io.PrintStream out) { this.out = out; }
	public FactorAdjustment (int size) { this (consoleStreamFor (size)); }
	public FactorAdjustment () { this.out = System.out; }
	java.io.PrintStream out;


}

