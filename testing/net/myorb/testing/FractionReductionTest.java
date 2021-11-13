
package net.myorb.testing;

import net.myorb.math.SpaceManager;
import net.myorb.math.TrigLib;
import net.myorb.math.ExponentiationLib;
import net.myorb.math.ReductionMechanism;

import net.myorb.math.ComputationConfiguration;
import net.myorb.math.fractions.Fraction;
import net.myorb.math.primenumbers.*;

import java.math.BigInteger;

/**
 * 
 * execute computation tests using fractions. additionally show effects of canceled fraction factors
 * 
 * @author Michael Druckman
 *
 */
public class FractionReductionTest
{


	// field managers for simple integer and fractions based on integer
	static Monitor.IntegerFraction integerFractionMonitor = new Monitor.IntegerFraction ();
	static SpaceManager<Fraction<BigInteger>> fractionFieldManager = integerFractionMonitor.manager;


	/**
	 * use fraction manager to convert scalar to fraction
	 * @param value the integer to convert to fraction
	 * @return the fraction representation
	 */
	public static Fraction<BigInteger> scalar (int value)
	{ return fractionFieldManager.newScalar (value); }


	/**
	 * use a fraction reduction mechanism during computation of e = exp(1)
	 */
	public void fractionReductionMechanismTest ()
	{
		ExponentiationLib<Fraction<BigInteger>> elib =
			new ExponentiationLib<Fraction<BigInteger>>(fractionFieldManager);

		FactorizationFieldManager mgr = new FactorizationFieldManager ();
		ReductionMechanism<Fraction<BigInteger>> reductionMechanism =
			new Distribution (mgr).getReductionMechanism ();
		elib.setReductionMechanism (reductionMechanism);

		Fraction<BigInteger> result = elib.exp (scalar (1));
		integerFractionMonitor.activity ("exp(1) = ", result);
		System.out.println ("---");
	}


	/**
	 * demonstrate effect of redution on fractions as iterative computation progresses
	 */
	public void fractionReductionTest ()
	{
		ExponentiationLib<Fraction<BigInteger>> elib =
			new ExponentiationLib<Fraction<BigInteger>>(fractionFieldManager);
		Fraction<BigInteger> result = elib.exp (scalar (1));

		integerFractionMonitor.activity ("exp(1) = ", result);
		Factorization nf = FactorizationManager.forValue (result.getNumerator ());
		Factorization df = FactorizationManager.forValue (result.getDenominator ());
		
		System.out.println ("---");
		System.out.println ("N = " + nf); System.out.println ("D = " + df);
		System.out.println ("----------------------------------------------------------");

		Factorization ratio = nf.divideBy (df);
		System.out.println ("ratio = " + ratio);
		System.out.println ("---");
		
		FactorizationFieldManager mgr = new FactorizationFieldManager ();
		System.out.println ("---");

		System.out.println ("before reduction = " + result);
		ReductionMechanism<Fraction<BigInteger>> reductionMechanism =
				new Distribution (mgr).getReductionMechanism ();
		reductionMechanism.reduce (result);

		System.out.println ("reduced = " + result);
		System.out.println ("decimal = " + mgr.toDecimalString (ratio));
		System.out.println ("---");
	}

	/**
	 * execute computation tests based on fractions using integer components
	 */
	public void integerFractionTest ()
	{
		TrigLib<Fraction<BigInteger>> tlib =
			new TrigLib<Fraction<BigInteger>>(fractionFieldManager);
		ExponentiationLib<Fraction<BigInteger>> elib =
			new ExponentiationLib<Fraction<BigInteger>>(fractionFieldManager);
		tlib.setTermCount (ComputationConfiguration.COS, 5);
		tlib.setTermCount (ComputationConfiguration.SIN, 5);
		Fraction<BigInteger> result;

		integerFractionMonitor.activity ("exp(1) = ", elib.exp (scalar (1)));

		result = tlib.asin (scalar (2).inverted());
		integerFractionMonitor.activity ("6*asin(0.5) = ", result.multiplyBy (scalar (6)));

		result = tlib.cos (tlib.piTimes (1, 4));
		integerFractionMonitor.activity ("cos(pi/4) = ", result);

		result = tlib.cos (tlib.piTimes (0, 1));
		integerFractionMonitor.activity ("cos(0) = ", result);

		result = tlib.sin (tlib.piTimes (1, 6));
		integerFractionMonitor.activity ("sin(pi/6) = ", result);

		result = tlib.sin (tlib.piTimes (-1, 4));
		integerFractionMonitor.activity ("sin(-pi/4) = ", result);
	}


	// field managers for simple float and fractions based on float
	static Monitor.FloatFraction floatFractionMonitor = new Monitor.FloatFraction ();
	static SpaceManager<Fraction<Double>> floatFractionFieldManager = floatFractionMonitor.manager;


	/**
	 * use fraction manager to convert scalar to fraction
	 * @param value the integer to convert to fraction
	 * @return the fraction representation
	 */
	public static Fraction<Double> dblScalar (int value)
	{ return floatFractionFieldManager.newScalar (value); }

	/**
	 * execute computation tests based on fractions using float components
	 */
	public void floatFractionTest ()
	{
		TrigLib<Fraction<Double>> tlib =
			new TrigLib<Fraction<Double>>(floatFractionFieldManager);
		ExponentiationLib<Fraction<Double>> elib =
			new ExponentiationLib<Fraction<Double>>(floatFractionFieldManager);
		elib.setTermCount (ComputationConfiguration.LOG, 20);
		tlib.setTermCount (ComputationConfiguration.COS, 5);
		tlib.setTermCount (ComputationConfiguration.SIN, 5);
		Fraction<Double> result;

		result = elib.exp (dblScalar (1));
		floatFractionMonitor.activity ("exp(1) = ", elib.exp (dblScalar (1)));

		result = elib.ln (dblScalar (10).inverted());
		floatFractionMonitor.activity ("ln(0.1) = ", result);

		result = tlib.asin (dblScalar (2).inverted());
		floatFractionMonitor.activity ("6*asin(0.5) = ", result.multiplyBy (dblScalar (6)));

		result = tlib.cos (tlib.piTimes (1, 4));
		floatFractionMonitor.activity ("cos(pi/4) = ", result);

		result = tlib.sin (tlib.piTimes (1, 6));
		floatFractionMonitor.activity ("sin(pi/6) = ", result);
	}


	/**
	 * execute test series
	 * @param args not used
	 */
	public static void main (String... args)
	{
		FractionReductionTest ftst = new FractionReductionTest ();

		System.out.println ("---");
		System.out.println ("Integer Fraction Test");
		System.out.println ("---");
		ftst.integerFractionTest ();
		System.out.println ();

		System.out.println ("---");
		System.out.println ("Float Fraction Test");
		System.out.println ("---");
		ftst.floatFractionTest ();
		System.out.println ();

		System.out.println ("---");
		System.out.println ("Fraction Reduction Test");
		System.out.println ("---");

		FactorizationImplementation support = new FactorizationImplementation (1000 * 1000);
		Factorization.setImplementation (support);
		support.initFactorizationsWithStats ();

		ftst.fractionReductionMechanismTest ();
		ftst.fractionReductionTest ();
	}


}
