
package net.myorb.testing.squareRoot;

import java.util.Date;

import net.myorb.math.primenumbers.Factorization;
import net.myorb.math.primenumbers.FactorizationFieldManager;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.TaylorPolynomials;
import net.myorb.math.Polynomial;

/**
 * implementation of Taylor polynomial series for calculation of SQRT
 * @author Michael Druckman
 */
public class TaylorSeries extends AbstractTestingEnvironment
{


	/* (non-Javadoc)
	 * @see net.myorb.testing.squareRoot.AbstractTestingEnvironment#getHeaderText()
	 */
	public String getHeaderText () { return "Table of computations of Taylor SQRT polynomial series"; }


	/* (non-Javadoc)
	 * @see net.myorb.testing.squareRoot.AbstractTestingEnvironment#getFlavorDescription(int)
	 */
	public String getFlavorDescription (int flavor)  { return ""; }


	/* (non-Javadoc)
	 * @see net.myorb.testing.squareRoot.AbstractTestingEnvironment#getTitleText()
	 */
	public String getTitleText ()  { return "Taylor series"; }


	/**
	 * compute sqrt(2) using Taylor series.
	 *  over 40 terms of the series are required to cover all displayed decimal places of accuracy.
	 *  the ratio of term/term coefficients presents an interesting pattern.
	 */
	public void runCoefficientTest ()
	{
		System.out.println ();
		System.out.println ("===");
		System.out.println ("Computation of coefficients of Taylor SQRT polynomial series");
		System.out.println ("===");
		System.out.println ();

		Date start = new Date ();
		Factorization nth = performCoefficientGeneration ();
		Date finish = new Date ();

		long millis = finish.getTime() - start.getTime();
		System.out.println ("Calculation time = " + millis + "ms");

		System.out.println
		("===\rFull factorization of last term");
		System.out.println (nth);						// this is a display of the prime factorization of the nth coefficient
		System.out.println ("===");
	}


	/**
	 * compute sqrt(2) using Taylor series.
	 *  over 40 terms of the series are required to cover all displayed decimal places of accuracy.
	 *  the ratio of term/term coefficients presents an interesting pattern.
	 * @return computed result as factors
	 */
	public Factorization performCoefficientGeneration ()
	{
		// object that manage prime factorizations
		FactorizationFieldManager mgr = new FactorizationFieldManager ();
		// generic Taylor series evaluation object instanced to perform computations on prime factorizations
		TaylorPolynomials<Factorization> taylor = new TaylorPolynomials<Factorization> (mgr);

		// the polynomial evaluation object and the coefficients list for the series
		Polynomial<Double> p = new Polynomial<Double> (new DoubleFloatingFieldManager ());
		Polynomial.PowerFunction<Double> poly = p.getPolynomialFunction (sqrtCoef);
		Factorization nth = mgr.newScalar (1);

		double testValue = 2, parm = 1/testValue - 1, c = 1.0, dratio, fOfX;	// values needed to evaluate series

		for (int i = 0; i < 100; i++) 					// 100 iterations should be a large number (but VERY slow convergence)
		{
			// TaylorPolynomial object has method
			//  compute coefficient of next polynomial term
			Factorization nthP1 = taylor.sqrtCoefficient (i);

			// ratio of coefficient (N+1) to coefficient (N)
			Factorization ratio = mgr.multiply (nthP1, mgr.invert (nth));
			// convert to double value and do multiplication to get real value of coefficient (N+1)
			dratio = mgr.toNumber (ratio).doubleValue ();

			sqrtCoef.add (c = c * dratio);				// (n+1)th coefficient added to polynomial
			fOfX = poly.eval (parm) * testValue;			// evaluate series with new term in place

			System.out.print ("n = " + i);				// display results of series with new term
			System.out.print (", ratio = " + ratio);
			System.out.print (" = " + dratio);
			System.out.print (", c = " + c);
			System.out.print (", approx = " + fOfX);
			System.out.println ();

			nth = nthP1;								// prepare for next term
		}

		return nth;
	}


	/**
	 * evaluate Taylor SQRT series at X
	 * @param x value of X for series evaluation
	 * @return computed result
	 */
	public double sqrtSeries (double x)
	{
		double powers = 1,
			result = sqrtCoef.get (0);
		for (int i = 1; i < MAXIMUM; i++)
		{ result += sqrtCoef.get (i) * (powers *= x); }
		return result;
	}
	protected Polynomial.Coefficients<Double> sqrtCoef = new Polynomial.Coefficients<Double> ();


	/**
	 * apply Taylor series to compute SQRT
	 * @param x the value searching for the root
	 * @param multiplier compensation for using 1/x where x was GT 1
	 * @param divisor compensation for domain reduction
	 * @return computed result
	 */
	public double sqrt (double x, double multiplier, double divisor)
	{
		if (useGenericAbstraction)
			return sqrtSeriesPoly.eval ((x * divisor * divisor) - 1) * multiplier / divisor;
		return sqrtSeries ((x * divisor * divisor) - 1) * multiplier / divisor;
	}
	public static final TaylorPolynomials<Double> TAYLOR = new TaylorPolynomials<Double> (new DoubleFloatingFieldManager ());
	public static final Polynomial.PowerFunction<Double> sqrtSeriesPoly = TAYLOR.getSqrtSeries (MAXIMUM);
	public static final boolean useGenericAbstraction = false;


	/**
	 * compute multiplier and divisor for domain reduction
	 * @param x the value of X being transformed for domain reduction
	 * @return the final computed SQRT result
	 */
	public double sqrtReduced (double x)
	{
		double firstReduction = x, multiplier = 1;
		if (x > 1) firstReduction = 1 / (multiplier = x);						//       first reduction gives domain 0 < X < 1

		double secondReduction =
			perfectSquaresReductionFactor (firstReduction);						//      second reduction domain is 0.9 < X < 1.1
		return sqrt (firstReduction, multiplier, secondReduction);				// 80% narrower and centered around 1 (series uses x-1)
	}


	/* (non-Javadoc)
	 * @see net.myorb.testing.squareRoot.AbstractTestingEnvironment#operateOnLimitedDomain(double, int)
	 */
	public double operateOnLimitedDomain (double x, int flavor)
	{
		return sqrt (x, 1, perfectSquaresReductionFactor (x));
	}


	/**
	 * computation of SQRT using Taylor polynomial series
	 *  with domain reduction algorithm (above) which build divisors of SQRTs of perfect squares
	 * @param x parameter to SQRT function
	 * @param approximationType not used
	 * @return computed result
	 */
	/* (non-Javadoc)
	 * @see net.myorb.testing.squareRoot.AbstractTestingEnvironment#sqrtAlgorithmImplementation(double, int)
	 */
	public double sqrtAlgorithmImplementation (double x, int approximationType)
	{ iterations = MAXIMUM; return unitDomainLimitation (x, approximationType); }


}
