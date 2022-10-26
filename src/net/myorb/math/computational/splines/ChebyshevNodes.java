
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


	/**
	 * backward compatible parameter set
	 */
	public ChebyshevNodes ()
	{
		super ();
	}

	/**
	 * construct objects implementing Chebyshev Polynomial functionalities
	 * @param approx Chebyshev nodes spline configuration
	 */
	public ChebyshevNodes (ApproximationTheory approx)
	{ super (); this.approx = approx; }
	ApproximationTheory approx;


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

