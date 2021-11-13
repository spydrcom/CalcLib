
package net.myorb.testing.squareRoot;

/**
 * implementation of Newton-Raphson method for calculation of SQRT
 * @author Michael Druckman
 */
public class NewtonRaphsonMethod extends AbstractTestingEnvironment
{


	/* (non-Javadoc)
	 * @see net.myorb.testing.squareRoot.AbstractTestingEnvironment#getFlavorDescription(int)
	 */
	public String getFlavorDescription (int flavor)  { return APPROXIMATION_TYPE[flavor]; }
	static String[] APPROXIMATION_TYPE = new String[]
	{
		"[bit length approximation]", "[restricted domain]", "[2^n approximation]", "[LOG2 approximation]", "[POW2 approximation]"
	};


	/* (non-Javadoc)
	 * @see net.myorb.testing.squareRoot.AbstractTestingEnvironment#getHeaderText()
	 */
	public String getHeaderText () { return "Table of computations of Newton-Raphson method for SQRT "; }


	/* (non-Javadoc)
	 * @see net.myorb.testing.squareRoot.AbstractTestingEnvironment#getTitleText()
	 */
	public String getTitleText ()  { return "Newton Method "; }


	/**
	 * SQRT computation loop
	 * @param x approximated root
	 * @param value parameter for sqrt computation
	 * @return computed result
	 */
	public double sqrt (double x, double value)
	{
		String errorMessage = SMALL_DERIVATIVE;
		double y, yPrime, xn, xnp1, toleranceCheck;

FOUND:	do
		{
ERROR:		do
			{
				for (int i = 2*MAXIMUM; i > 0; i--)							// more iterations needed for very small numbers
				{
					y = x * x - value;										// value of the function at the approximated root
					yPrime = x * 2;											// value of the derivative at the approximated root
			
					if (Library.abs (yPrime) < Library.EPSILON) break ERROR;
			
					xn = x;
					xnp1 = xn - (y / yPrime);								// compute x(n+1), next iteration of approximation
					x = xnp1;
			
					iterations++;
					toleranceCheck = Library.abs (xnp1 - xn) / (Library.abs (xnp1));
					if (Library.withinTolerance (toleranceCheck)) break FOUND;
				}
				errorMessage = FAILED_CONVERGENCE; break ERROR;				// maximum iteration count reached
			} while (true); errorTermination (errorMessage);				// error loop break point
		} while (true); return x;											// root found
	}


	/**
	 * use Newton method approximation of sqrt.
	 *  root approximation is done based on bit length of parameter
	 * @param value parameter for sqrt computation
	 * @return computed result
	 */
	public double sqrtUsingBitlength (double value)
	{
		iterations = 0;
		double x = bitLengthApproximatedSqrt (value);						// approximated root
		return sqrt (x, value);
	}


	/**
	 * use Newton method approximation of sqrt.
	 *  domain is restricted to 0 LT X LT 1 allowing use of 1 as approximation
	 * @param value the value of parameter for sqrt computation
	 * @param approximationType 1=1, 2=2^n
	 * @return computed result
	 */
	public double restrictedSqrt
		(double value, int approximationType)
	{
		double x = 1; iterations = 0;										// approximated root = 1, domain reduced to 0 < x < 1
		if (approximationType == 2) x = power2ApproximatedSqrt (value);		// approximated root based on 2^n
		return sqrt (x, value);
	}


	/* (non-Javadoc)
	 * @see net.myorb.testing.squareRoot.AbstractTestingEnvironment#operateOnLimitedDomain(double, int)
	 */
	public double operateOnLimitedDomain (double x, int flavor)
	{
		switch (flavor)
		{
			case 3: return sqrt
				(Library.pow (2.0, log2Approximation (x)), x);		// approximated root based on log
			case 2: return sqrt (power2ApproximatedSqrt (x), x);	// approximated root based on 2^n evaluation
		}
		return sqrt (1, x);											// approximated root = 1, domain reduced to 0 < x < 1
	}


	/**
	 * use Newton method approximation of sqrt
	 * @param x parameter for sqrt computation
	 * @param approximationType 0=bit length, 1=1, 2=2^n
	 * @return computed result
	 */
	/* (non-Javadoc)
	 * @see net.myorb.testing.squareRoot.AbstractTestingEnvironment#sqrtAlgorithmImplementation(double, int)
	 */
	public double sqrtAlgorithmImplementation (double x, int approximationType)
	{
		iterations = 0;

		switch (approximationType)
		{
			case 0: return sqrtUsingBitlength (x);					// bitlength root approximation
			case 4: return sqrt (power2ApproximatedSqrt (x), x);	// power2 approximation of root
		}

		return unitDomainLimitation (x, approximationType);			// use perfect squares reduction
	}


}

