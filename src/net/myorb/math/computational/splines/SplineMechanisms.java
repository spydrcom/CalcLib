
package net.myorb.math.computational.splines;

import net.myorb.math.GeneratingFunctions;

/**
 * provide method to implement mechanisms of a spline algorithm
 * @author Michael Druckman
 */
public interface SplineMechanisms
{

	/**
	 * get spline optimal domain lo value
	 * @return the lo of the spline standard domain
	 */
	double getSplineOptimalLo ();
	
	/**
	 * @param x the point of the domain to evaluate the spline at
	 * @param coefficients the polynomial coefficients for the spline being evaluated
	 * @return the value of the spline at the specified domain point
	 */
	double evalSplineAt (double x, GeneratingFunctions.Coefficients<Double> coefficients);

}
