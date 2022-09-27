
package net.myorb.math.computational.splines;

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
	public static final int SPLINE_TICKS = 23, SPLINE_SPACES = SPLINE_TICKS - 1;


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
	public static final double[] CHEBYSHEV_POINTS = new double[]
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


}

