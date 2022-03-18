
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

	/**
	 * @param lo the lo end of integration range
	 * @param hi the hi end of integration range
	 * @param coefficients the polynomial coefficients for this range
	 * @return the computed integral
	 */
	double evalIntegralOver (double lo, double hi, GeneratingFunctions.Coefficients<Double> coefficients);

	/**
	 * identify the object that will correctly interpret the spline description 
	 * @return class-path for interpretation object
	 */
	String getInterpreterPath ();

}
