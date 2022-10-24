
package net.myorb.math.computational.splines;

import net.myorb.data.abstractions.DataSequence;

/**
 * implement mechanisms of Chebyshev spline
 *  using approximation theory algorithms of points
 * @author Michael Druckman
 */
public class ChebyshevNodes
	extends ChebyshevCoreFunctionality
	implements SplineMechanisms
{


	/*
	 * constants that describe the optimal Chebyshev T-Polynomial Spline based on points
	 */

	public static final double SPLINE_LO = -1, SPLINE_HI = 1;
	public static final double SPLINE_RANGE = SPLINE_HI - SPLINE_LO;


	/*
	 * 

		N = 22 ; d = pi/N
		!! f(x) = - cos x

		// matrix order is 1 larger than point count
		ORDER = N + 1

		// collect array of the points curve
		points = ARRAY [ 0 <= i <= N ]   (    f (  i * d  )    )

	 *
	 */ 
	public static final double[] CHEBYSHEV_POINTS_22 = new double[]
		{
				-1,
				-0.9898214418809327,  -0.9594929736144974,
				-0.9096319953545184,  -0.8412535328311812,  -0.7557495743542583,  -0.6548607339452851,
				-0.5406408174555977,  -0.41541501300188644, -0.2817325568414298,  -0.14231483827328512,

				-6.123233995736766E-17,

				 0.142314838273285,    0.28173255684142967,  0.4154150130018863,   0.5406408174555977,
				 0.654860733945285,    0.7557495743542582,   0.8412535328311811,   0.9096319953545184,
				 0.9594929736144974,   0.9898214418809327,
				 1
		};

	public static final double[] CHEBYSHEV_POINTS = new double[]
			{
				-1,
				-0.9951847266721969,  -0.9807852804032304,  -0.9569403357322088, -0.9238795325112867,
				-0.881921264348355,   -0.8314696123025452,  -0.773010453362737,  -0.7071067811865476, 
				-0.6343932841636455,  -0.5555702330196023,  -0.4713967368259978, -0.38268343236508984, 
				-0.29028467725446233, -0.19509032201612833, -0.09801714032956077, 

				-6.123233995736766E-17, 

				 0.09801714032956065,  0.1950903220161282,   0.29028467725446216, 0.3826834323650897, 
				 0.4713967368259977,   0.555570233019602,    0.6343932841636454,  0.7071067811865475, 
				 0.773010453362737,    0.8314696123025453,   0.8819212643483549,  0.9238795325112867, 
				 0.9569403357322088,   0.9807852804032304,   0.9951847266721968, 
				 1
			};
	//(-1, -0.9951847266721969, -0.9807852804032304, -0.9569403357322088, -0.9238795325112867, -0.881921264348355, -0.8314696123025452, -0.773010453362737, -0.7071067811865476, -0.6343932841636455, -0.5555702330196023, -0.4713967368259978, -0.38268343236508984, -0.29028467725446233, -0.19509032201612833, -0.09801714032956077, -6.123233995736766E-17, 0.09801714032956065, 0.1950903220161282, 0.29028467725446216, 0.3826834323650897, 0.4713967368259977, 0.555570233019602, 0.6343932841636454, 0.7071067811865475, 0.773010453362737, 0.8314696123025453, 0.8819212643483549, 0.9238795325112867, 0.9569403357322088, 0.9807852804032304, 0.9951847266721968, 1)


	/**
	 * construct objects implementing Chebyshev Polynomial functionalities
	 */
	public ChebyshevNodes ()
	{
		super ();
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineMechanisms#getSplineOptimalLo()
	 */
	public double getSplineOptimalLo ()
	{
		return SPLINE_LO;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineMechanisms#getInterpreterPath()
	 */
	public String getInterpreterPath ()
	{
		return ChebyshevNodes.class.getCanonicalName ();
	}


	/**
	 * @param lo the lo end of the domain
	 * @param hi the hi end of the domain
	 * @param mul the multipliers for the points
	 * @return the sequence of points in the domain
	 */
	public static DataSequence <Double> compute (double lo, double hi, double [] mul)
	{
		DataSequence <Double> domain = new DataSequence <> ();
		double range = hi - lo, halfRange = range / 2, mid = lo + halfRange;
		for (int i = 0; i < mul.length; i++) { domain.add ( mid + halfRange * mul [i] ); }
		return domain;
	}


	/**
	 * apply points to the range of the domain
	 * @param lo the low end of the span of the domain
	 * @param hi the high end of the span of the domain
	 * @return a data sequence holding domain values
	 */
	public static DataSequence <Double>
		getSplineDomainFor (double lo, double hi)
	{ return compute (lo, hi, CHEBYSHEV_POINTS); }


	/**
	 * select points between the Chebyshev points
	 * - these are the test points for the regression
	 * - these are added to the points used to build the model
	 * @return the list of multipliers
	 */
	public static double [] getCombMultipliers ()
	{
		double last = CHEBYSHEV_POINTS[0];
		double [] comb = new double [CHEBYSHEV_POINTS.length-1];
		for (int i = 1; i < CHEBYSHEV_POINTS.length; i++)
		{
			double next = CHEBYSHEV_POINTS[i];
			comb[i-1] = (last + next) / 2;
			last = next;
		}
		return comb;
	}


	/**
	 * compute comb points for the specified range
	 * @param lo the low end of the span of the domain
	 * @param hi the high end of the span of the domain
	 * @return a data sequence holding domain values
	 */
	public static DataSequence <Double>
		getCombDomainFor (double lo, double hi)
	{ return compute (lo, hi, getCombMultipliers ()); }


}

