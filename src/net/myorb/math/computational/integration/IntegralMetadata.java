
package net.myorb.math.computational.integration;

/**
 * description of a common meta-data collected for integral computations
 * @author Michael Druckman
 */
public interface IntegralMetadata
{

	/**
	 * @return an estimation of the error in the analysis
	 */
	public double getErrorEstimate ();

	/**
	 * @return the count of function evaluations used
	 */
	public int getEvaluationCount ();

}
