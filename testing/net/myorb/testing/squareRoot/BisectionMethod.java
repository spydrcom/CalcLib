
package net.myorb.testing.squareRoot;

/**
 * implementation of Bisection method for calculation of SQRT
 * @author Michael Druckman
 */
public class BisectionMethod extends AbstractTestingEnvironment
{


	/* (non-Javadoc)
	 * @see net.myorb.testing.squareRoot.AbstractTestingEnvironment#getFlavorDescription(int)
	 */
	public String getFlavorDescription (int flavor)  { return APPROXIMATION_TYPE[flavor]; }
	static String[] APPROXIMATION_TYPE = new String[]{"[no domain restriction]", "[using restricted domain]", "[2^n approximation]"};


	/* (non-Javadoc)
	 * @see net.myorb.testing.squareRoot.AbstractTestingEnvironment#getHeaderText()
	 */
	public String getHeaderText () { return "Table of computations of bisection method for SQRT "; }


	/* (non-Javadoc)
	 * @see net.myorb.testing.squareRoot.AbstractTestingEnvironment#getTitleText()
	 */
	public String getTitleText ()  { return "Bisection Method "; }


	/**
	 * find root using bisection.
	 *  domain of X has been reduced and
	 *  approximated root range has been set
	 * @param x the reduced value of X for which we seek a root
	 * @param lo the low side of the expected root computation
	 * @param hi the high side of expected root
	 * @return the computed root
	 */
	public double sqrt (double x, double lo, double hi)
	{
		double a /* approximated root */, f /* funtion value */;
FOUND:	while (true)
		{
			for (int i = 7*MAXIMUM; i > 0; i--)
			{
				iterations++;
				a = (hi + lo) / 2; f = a * a - x;
				if (Library.withinTolerance (f)) break FOUND;
				if (f < 0) lo = a; else hi = a;
			}
			errorTermination (FAILED_CONVERGENCE);
		}
		return a;
	}


	/**
	 * apply reduction factor to SQRT computation
	 * @param x the value searching for the computed root
	 * @param reductionFactor the perfect squares reduction factor
	 * @return the value of the root found
	 */
	public double sqrtReduced (double x, double reductionFactor)
	{
		double reducedApproximation = x * reductionFactor * reductionFactor;
		return sqrt (reducedApproximation, REDUCED_DOMAIN_LO, REDUCED_DOMAIN_HI) / reductionFactor;
	}


	/**
	 * use log approximation of SQRT
	 * @param x the value searching for the root
	 * @param log2x an approximation of log base 2 of X
	 * @return the value of the root found
	 */
	public double sqrtLogApproximated (double x, int log2x)
	{
		double lo = Library.pow (2.0, log2x/2 - 1);
		double hi = Library.pow (2.0, log2x/2 + 1);
		return sqrt (x, lo, hi);
	}


	/**
	 * find root using bisection
	 * @param x the value searching for the root
	 * @param reducedDomain reduce domain before bisection
	 * @return the value of the root found
	 */
	public double sqrt (double x, boolean reducedDomain)
	{
		iterations = 0;
		double lo = 0, hi = 1, divisor = 1;			// using 1/x for x > 1 allows nice bisection domain of 0 < x < 1
		if (reducedDomain)
		{
			lo = REDUCED_DOMAIN_LO; hi = REDUCED_DOMAIN_HI;		// bisection domain starts 80% narrower
			divisor = perfectSquaresReductionFactor (x);
			x *= divisor * divisor;
		}
		return sqrt (x, lo, hi) / divisor;
	}


	/* (non-Javadoc)
	 * @see net.myorb.testing.squareRoot.AbstractTestingEnvironment#operateOnLimitedDomain(double, int)
	 */
	public double operateOnLimitedDomain (double x, int flavor)
	{
		switch (flavor)
		{
			case 2:
				return sqrtLogApproximated (x, log2Approximation (x));			// [2^n approximation]
			case 1:
				return sqrtReduced (x, perfectSquaresReductionFactor (x));		// [using restricted domain]
			default:
				return sqrt (x, 0, 1);											// [no domain restriction]
		}
	}


	/**
	 * standard SQRT domain reduction
	 * @param x value in search of root
	 * @param approximationType 1 = use reduced domain, 2 = use log approximation
	 * @return the computed root
	 */
	/* (non-Javadoc)
	 * @see net.myorb.testing.squareRoot.AbstractTestingEnvironment#sqrtAlgorithmImplementation(double, int)
	 */
	public double sqrtAlgorithmImplementation (double x, int approximationType)
	{ iterations = 0; return unitDomainLimitation (x, approximationType); }


}


