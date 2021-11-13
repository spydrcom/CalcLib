
package net.myorb.testing;

import net.myorb.math.fractions.Fraction;
import net.myorb.math.primenumbers.Distribution;
import net.myorb.math.primenumbers.Factorization;
import net.myorb.math.primenumbers.FactorizationFieldManager;
import net.myorb.math.primenumbers.FactorizationImplementation;
import net.myorb.math.*;

import java.math.BigInteger;

/**
 * 
 * compile series of tests for fraction objects as basis for computational algorithms
 * 
 * @author Michael Druckman
 *
 */
public class FractionLibraryTest
{


	// field managers for simple integer and fractions based on integer
	static Monitor.IntegerFraction integerFractionMonitor = new Monitor.IntegerFraction ();
	static SpaceManager<Fraction<BigInteger>> fractionFieldManager = integerFractionMonitor.manager;

	/**
	 * scalar conversion to Integer fraction
	 * @param value integer scalar value
	 * @return fraction object
	 */
	public static Fraction<BigInteger> scalar (int value)
	{ return fractionFieldManager.newScalar (value); }

	/**
	 * prepare ReductionMechanism object
	 * @return a ReductionMechanism object for Fraction(BigInteger)
	 */
	public ReductionMechanism<Fraction<BigInteger>> introduceReduction ()
	{
		FactorizationFieldManager mgr = new FactorizationFieldManager ();
		ReductionMechanism<Fraction<BigInteger>> reductionMechanism =
			new Distribution (mgr).getReductionMechanism ();
		return reductionMechanism;
		//return null;
	}


	/**
	 * test fractions with Integer components
	 */


	/**
	 * 
	 * extension to Exponentiation library coded to run computational tests
	 * 
	 * @author Michael Druckman
	 *
	 */
	static class ExponentiationTest extends ExponentiationLib<Fraction<BigInteger>>
	{
		ExponentiationTest (ReductionMechanism<Fraction<BigInteger>> reductionMechanism )
		{
			super (fractionFieldManager);
			setReductionMechanism (reductionMechanism);
		}
		
		void runTestSeries ()
		{
			markOptionSelected (DUMP_ITERATIVE_TERM_VALUES);
			integerFractionMonitor.activity ("exp(1) = ", exp (scalar (1)));
		}
	}

	/**
	 * 
	 * extension to trig library coded to run computational tests
	 * 
	 * @author Michael Druckman
	 *
	 */
	static class TrigTest extends TrigLib<Fraction<BigInteger>>
	{
		TrigTest (ReductionMechanism<Fraction<BigInteger>> reductionMechanism )
		{
			super (fractionFieldManager);
			setReductionMechanism (reductionMechanism);
		}
		
		void runTestSeries ()
		{
			Fraction<BigInteger> result =
				asin (scalar (2).inverted ()).multiplyBy (scalar (6));
			integerFractionMonitor.activity ("6*asin(0.5) = ", result);

			setTermCount (ComputationConfiguration.COS, 5);
			integerFractionMonitor.activity ("cos(pi/4) = ", cos (piTimes (1, 4)));
			integerFractionMonitor.activity ("cos(0) = ", cos (piTimes (0, 1)));

			setTermCount (ComputationConfiguration.SIN, 5);
			integerFractionMonitor.activity ("sin(pi/6) = ", sin (piTimes (1, 6)));
			integerFractionMonitor.activity ("sin(-pi/4) = ", sin (piTimes (-1, 4)));
		}
	}

	/**
	 * execute trig and exponentiation tests as separate class extensions
	 */
	public void integerFractionTest ()
	{
		ReductionMechanism<Fraction<BigInteger>> reductionMechanism = introduceReduction ();
		new ExponentiationTest (reductionMechanism).runTestSeries ();
		new TrigTest (reductionMechanism).runTestSeries ();
	}


	// field managers for simple float and fractions based on float
	static Monitor.FloatFraction floatFractionMonitor = new Monitor.FloatFraction ();
	static SpaceManager<Fraction<Double>> floatFractionFieldManager = floatFractionMonitor.manager;

	/**
	 * scalar conversion to floating fraction
	 * @param value integer scalar value
	 * @return fraction object
	 */
	public static Fraction<Double> dblScalar (int value)
	{ return floatFractionFieldManager.newScalar (value); }

	/**
	 * test fractions with Float components
	 */
	public void floatFractionTest ()
	{
		ExponentiationLib<Fraction<Double>> elib =
			new ExponentiationLib<Fraction<Double>>(floatFractionFieldManager);
		floatFractionMonitor.activity ("exp(1) = ", elib.exp (dblScalar (1)));

		elib.setTermCount (ComputationConfiguration.LOG, 20);
		floatFractionMonitor.activity ("ln(0.5) = ", elib.ln (dblScalar (2).inverted ()));

		TrigLib<Fraction<Double>> tlib =
			new TrigLib<Fraction<Double>>(floatFractionFieldManager);
		floatFractionMonitor.activity ("6*asin(0.5) = ", tlib.asin (dblScalar (2).inverted ()).multiplyBy (dblScalar (6)));

		tlib.setTermCount (ComputationConfiguration.COS, 5);
		floatFractionMonitor.activity ("cos(pi/4) = ", tlib.cos (tlib.piTimes (1, 4)));

		tlib.setTermCount (ComputationConfiguration.SIN, 5);
		floatFractionMonitor.activity ("sin(pi/6) = ", tlib.sin (tlib.piTimes (1, 6)));
	}


	/**
	 * run tests for integer and float based fraction objects
	 * @param args unused
	 */
	public static void main (String... args)
	{
		// initialization of factorization
		//   used in fraction reduction
		FactorizationImplementation support =
			new FactorizationImplementation (1000);
		Factorization.setImplementation (support);
		support.initFactorizationsWithStats ();
		System.out.println ();

		FractionLibraryTest fractionLibraryTest = new FractionLibraryTest ();

		System.out.println ("=======");
		System.out.println ();
		
		System.out.println ("---");
		System.out.println ("Integer Fraction Test");
		System.out.println ("---");
		fractionLibraryTest.integerFractionTest ();
		System.out.println ();

		System.out.println ("---");
		System.out.println ("Float Fraction Test");
		System.out.println ("---");
		fractionLibraryTest.floatFractionTest ();
		System.out.println ();
	}


}
