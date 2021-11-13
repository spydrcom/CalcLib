
package net.myorb.math;

import java.util.List;

import net.myorb.math.computational.PolynomialRoots;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

/**
 * implementation of PowerLibrary for
 *  Double Floating values with no generic overhead
 * @author Michael Druckman
 */
public class HighSpeedMathLibrary extends OptimizedMathLibrary<Double>
			implements ExtendedPowerLibrary<Double>
{

	protected static
	DoubleFloatingFieldManager mgr =
		new DoubleFloatingFieldManager ();
	public HighSpeedMathLibrary () { super (mgr); }

	protected TaylorPolynomials<Double> taylor = new TaylorPolynomials<Double> (mgr);
	protected PolynomialRoots<Double> roots = new PolynomialRoots<Double> (mgr, this);


	/**
	 * standard SGN function
	 * @param value the value to use as parameter
	 * @return -1 if parameter is negative, otherwise 1
	 */
	public double sgn (double value)
	{
		return value<0? -1: 1;
	}


	/**
	 * standard absolute value function
	 * @param value the value to use as parameter
	 * @return absolute value of parameter
	 */
	public double abs (double value)
	{
		return value<0? -value: value;
	}


	/**
	 * check value for proximity to zero
	 * @param value the value to be checked
	 * @return TRUE = value within tolerance range
	 */
	public boolean withinTolerance (double value)
	{
		return abs (value) < TOLERANCE;
	}


	/**
	 * change value of tolerance
	 * @param to new value for tolerance
	 */
	public void setTolerance (double to) { TOLERANCE = to; }
	protected double TOLERANCE = 0.0001;


	/*
	 * methods for computation of exponentials
	 */


	/**
	 * compute log having reduced domain
	 * @param x the value in search of its logarithm
	 * @param threshold the low end of the domain restriction
	 * @param base the base of the logarithm to compute
	 * @param baseConversion base change ratio denominator
	 * @return the logarithm of X with specified base
	 */
	public Double computeLogUsingReducedDomain
	(Double x, double threshold, double base, double baseConversion)
	{
		int characteristic = 0; while (x < threshold) { x = x * base; characteristic--; }
		return taylor.getLnSeries (50).eval (x - 1)/baseConversion + characteristic;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#ln(java.lang.Object)
	 */
	public Double ln (Double value) 
	{
		if (value <= 0)
		{ throw new RuntimeException ("Invalid parameter for Ln"); }
		else if (value > 1) return - computeLogUsingReducedDomain (1 / value, 0.5, e, 1);
		else return computeLogUsingReducedDomain (value, 0.5, e, 1);
	}

	/**
	 * compute binary log (base 2)
	 * @param value the value in search of its logarithm
	 * @return log[2](value)
	 */
	public Double logBinary (Double value)
	{
		if (value <= 0)
		{ throw new RuntimeException ("Invalid parameter for LOG"); }
		else if (value > 1) return - computeLogUsingReducedDomain (1 / value, 0.7, 2, LN2);
		else return computeLogUsingReducedDomain (value, 0.7, 2, LN2);
	}
	static final double LN2 = 0.693147180559945309417232121;

	/**
	 * compute common log (base 10)
	 * @param value the value in search of its logarithm
	 * @return log[10](value)
	 */
	public Double logCommon (Double value)
	{
		if (value <= 0)
		{ throw new RuntimeException ("Invalid parameter for LOG"); }
		else if (value > 1) return - ln (1 / value) / LN10;
		else return ln (value) / LN10;
	}
	static final double LN10 = 2.30258509299404568401799145;

	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#exp(java.lang.Object)
	 */
	public Double exp (Double value)
	{
		double characteristic = value.intValue (), mantissa = value - characteristic;
		return taylor.getExpSeries (20).eval  (mantissa) * pow (e, (int)characteristic);
	}
	public static final double e = 2.7182818284590452353602874713527;

	public Double exp10 (Double x)  { return exp (x*LN10); }		// 10^x
	public Double exp2 (Double x) { return exp (x*LN2); }			// 2^x


	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#pow(java.lang.Object, int)
	 */
	public Double pow (Double x, int n)
	{
		if (n < 0) return 1 / (pow (x, -n));
		else if (n == 0) return 1.0;
		else if (n == 1) return x;
		
		Double v = x;
		Double square = v * v;
		Double result = square;

		while ((n -= 2) >= 2)
		{
			result *= square;
		}

		if (n == 1)
			return result * v;
		else return result;
	}


	/**
	 * compute factorial of parameter
	 * @param n the value used in computation
	 * @return the resulting factorial
	 */
	public Double factorial (Double n)
	{
		return taylor.factorial (n);
	}


	/**
	 * find nearest 2^n to use as approximation
	 * @param x the value seeking SQRT approximation 0 LT X LT 1
	 * @return computed approximation
	 */
	public static double power2ApproximatedSqrt (double x)
	{
		double root = 0.5;
		while (x < root*root) root /= 2;
		return root;
	}


	/**
	 * use Newton-Raphson root solution
	 * @param value the value seeking root
	 * @return computed root
	 */
	public double newtonRaphsonSqrtLoop (double value)
	{
		double y, yPrime, xn, xnp1, toleranceCheck;
		double x = power2ApproximatedSqrt (value);

		for (int i = 500; i > 0; i--)								// more iterations needed for very small numbers
		{
			y = x * x - value;										// value of the function at the approximated root
			yPrime = x * 2;											// value of the derivative at the approximated root
	
			xn = x;
			xnp1 = xn - (y / yPrime);								// compute x(n+1), next iteration of approximation
			x = xnp1;
	
			toleranceCheck = abs (xnp1 - xn) / abs (xnp1);
			if (withinTolerance (toleranceCheck)) return x;
		}

		throw new RuntimeException ("Convergence failure");
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.PowerLibrary#sqrt(java.lang.Object)
	 */
	public Double sqrt (Double x)
	{
		if (x == 0) return 0.0; if (x == 1) return 1.0;
		else if (x < 0) { throw new RuntimeException ("Illegal SQRT parameter"); }
		else if (x > 1) return newtonRaphsonSqrtLoop (1 / x) * x;
		return newtonRaphsonSqrtLoop (x);
	}


	/*
	 * methods for computation of inverse trig functions
	 */


	/*
	 * domain reduction for single parameter ATAN
	 */


	static final double
	   PI	= 3.1415926535897932384626433832795,
	  rad2	= 1.4142135623730950488016887242097,
	  rad3	= 1.7320508075688772935274463415059;


	static final double rad2m1 = rad2 - 1,
	tanPI24 = (rad3-3*rad2m1) / (rad3*rad2m1+3);


	static final double
	 tanAlpha[] = new double[]{tanPI24, 2-rad3, rad2m1, rad3/3},
		alpha[] = new double[]{7.50000, 15.000, 22.500, 30.000};


	/*
	 * based on identity equation:  tan (a+b) = [ tan (a) + tan (b) ] / [ 1 - tan (a) * tan (b) ]
	 */


	/**
	 * compute the angle given by TAN(alpha+beta)
	 * @param tanAlphaPlusBeta the value of TAN(alpha+beta)
	 * @param alpha the angle given by TAN(alpha)
	 * @param tanAlpha the value of TAN(alpha)
	 * @return the value alpha+beta
	 */
	public double atan (double tanAlphaPlusBeta, double alpha, double tanAlpha)
	{
		return alpha + taylor.atan (tanBeta (tanAlphaPlusBeta, tanAlpha));
	}


	/**
	 * compute TAN(beta) given other two quantities
	 * @param tanAlphaPlusBeta the value of TAN(alpha+beta)
	 * @param tanAlpha the value of TAN(alpha)
	 * @return the value TAN(beta)
	 */
	public double tanBeta (double tanAlphaPlusBeta, double tanAlpha)
	{ return (tanAlphaPlusBeta - tanAlpha) / (tanAlphaPlusBeta*tanAlpha + 1); }


	/**
	 * compute ATAN using tanBeta domain reduction algorithm
	 * @param x the value of X for the function evaluation
	 * @return computed ATAN of X
	 */
	public Double atan (Double x)
	{
		if (x == 0) return 0.0;
		else if (x == 1) return PI/4;
		else if (x < 0) return - atan (-x);
		else if (x > 1) return PI_OVER_2 - atan (1/x);

		// domain now constrained 0 < X < 1
		double angleDegrees = 0, angleRadians = 0, tanAlphaI;

		for (int i = alpha.length - 1; i >= 0; i--)
		{
			if (x > (tanAlphaI = tanAlpha[i]))
			{
				x = tanBeta (x, tanAlphaI);
				angleDegrees += alpha[i];
			}
		}

		// now 0 < X < tan(PI/24)
		angleRadians = taylor.atan (x);
		return angleRadians + angleDegrees*RADIANS_PER_DEGREE;
	}
	static final double RADIANS_PER_DEGREE = PI / 180, PI_OVER_2 = PI / 2;


	/*
	 * domain reduction for ASIN
	 */


	static final double
	sinPI36 = 0.08715574274765817355806427083747,
	cosPI36 = 0.99619469809174553229501040247389;

	static final double
	rad6 = rad2 * rad3, r6m2 = rad6 - rad2, r6p2 = rad6 + rad2;

	static final double
	 sinBeta[] = new double[]{sinPI36, sinPI36, r6m2/4, 0.5000, rad2/2, rad3/2},
	 cosBeta[] = new double[]{cosPI36, cosPI36, r6p2/4, rad3/2, rad2/2, 0.5000},
		beta[] = new double[]{5.00000, 5.00000, 15.000, 30.000, 45.000, 60.000};

	static final int DOWN_TO = 0; // this can be raised to improve speed at little loss of precision


	/*
	 * based on identity equation:  sin (a + b) = sin (a) * cos (b) + cos (a) * sin (b)
	 */


	/**
	 * compute the remainder after a reduction
	 * @param sinAlphaPlusBeta the full value being reduced
	 * @param sinBeta the sin value of the reducing angle
	 * @param cosBeta the cos value of the reducing angle
	 * @return the sin of the remainder
	 */
	public List<Double> sinAlpha
	(double sinAlphaPlusBeta, double sinBeta, double cosBeta)
	{
		double
		a = 1.0,
		b = - 2 * sinAlphaPlusBeta * cosBeta,
		c = sinAlphaPlusBeta*sinAlphaPlusBeta - sinBeta*sinBeta;
		return roots.quadratic (a, b, c);
	}


	/**
	 * compute ASIN using sinAlpha domain reduction and Taylor series
	 * @param x the value to find angle for
	 * @return computed angle
	 */
	public Double asin (Double x)
	{
		if (x == 0) return 0.0;
		if (x >= 1) return PI_OVER_2;
		if (x < 0) return - asin (-x);

		// domain now constrained 0 < X < 1
		double angleDegrees = 0, angleRadians = 0, sinBetaI;

		for (int i = beta.length - 1; i >= DOWN_TO; i--)
		{
			if (x > (sinBetaI = sinBeta[i]))
			{
				x = sinAlpha (x, sinBetaI, cosBeta[i]).get (0);
				angleDegrees += beta[i];
			}
		}

		// now 0 < X < sin(PI/36) 
		//    when DOWN_TO = 0
		angleRadians = taylor.asin (x);
		return angleRadians + angleDegrees*RADIANS_PER_DEGREE;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.TrigLib#atanHook(java.lang.Object)
	 */
	public Double atanHook (Double x) { return atan (x); }


	/**
	 * hook for use by expression evaluator
	 * @param parameters the list of parameters
	 * @return the computed result
	 */
	public Double atan (List<Double> parameters)
	{ return atan (parameters.toArray (new Double[]{})); }
	public Double atan (Double... parameters)
	{
		switch (parameters.length)
		{
			case 1:  return atan (parameters[0]);
			case 2:  return atan (parameters[0], parameters[1]);
			default: throw new RuntimeException ("ATAN function requires 1 or 2 parameters");
		}
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.ExtendedPowerLibrary#nThRoot(java.lang.Object, int)
	 */
	public Double nThRoot (Double x, int root) { return exp (ln (x) / root); }


}


