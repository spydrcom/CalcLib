
package net.myorb.math.computational.splines;

import net.myorb.math.computational.integration.Quadrature;

/**
 * implement Chebyshev T-Polynomial series expansion.
 * - coefficients to be computed using the integration formula.
 * @author Michael Druckman
 */
public class ChebyshevSeriesExpansion
{

//	!! c(k) = 2/pi * INTEGRAL [ -1 <= x <= 1 ] ( f(x) * T_k(x) / sqrt (1-x^2) * <*> x )
//  !! f(x) = SIGMA [ 0 <= k <= N ] ( c_k * T_k (x) )

	public ChebyshevSeriesExpansion (Quadrature.Integral integrationMethod)
	{
		this.integrationMethod = integrationMethod;
	}
	Quadrature.Integral integrationMethod;

}
