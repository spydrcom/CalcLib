
package net.myorb.math.computational.splines;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;

/**
 * FittedFunction specific to Chebyshev T-Polynomial spline mechanisms
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ChebyshevFittedFunction <T> extends FittedFunction <T>
{

	public ChebyshevFittedFunction
		(
			ExpressionComponentSpaceManager <T> mgr
		)
	{
		super (mgr, new ChebyshevSpline (mgr));
	}

}
