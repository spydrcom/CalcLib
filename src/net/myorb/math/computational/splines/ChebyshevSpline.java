
package net.myorb.math.computational.splines;

/**
 * implement spline algorithms using VanChe31 algorithms
 * @author Michael Druckman
 */
public class ChebyshevSpline
	extends ChebyshevCoreFunctionality
	implements SplineMechanisms
{


	/*
	 * constants that describe the optimal Chebyshev T-Polynomial Spline
	 */

	public static final double SPLINE_LO = -1.5, SPLINE_HI = 1.5;
	public static final double SPLINE_RANGE = SPLINE_HI - SPLINE_LO;
	public static final int SPLINE_TICKS = 31, SPLINE_SPACES = SPLINE_TICKS - 1;


	/**
	 * construct objects implementing Chebyshev Polynomial functionalities
	 */
	public ChebyshevSpline ()
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
		return ChebyshevSpline.class.getCanonicalName ();
	}


}

