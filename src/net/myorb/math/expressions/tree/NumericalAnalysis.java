
package net.myorb.math.expressions.tree;

import net.myorb.math.expressions.ValueManager.GenericValue;

/**
 * hook for introduction of Numerical Analysis algorithms into slice mechanisms
 * @param <T> data type used in expressions
 * @author Michael Druckman
 */
public interface NumericalAnalysis<T>
{

	/**
	 * apply algorithm to range description
	 * @param digest the description of the node to be analyzed
	 * @return the value computed for the node
	 */
	GenericValue evaluate (RangeNodeDigest<T> digest);

	/**
	 * identify an analyzer to use for evaluations
	 * @param analyzer the analyzer to be used for processing
	 */
	void setAnalyzer (NumericalAnalysis<T> analyzer);

	/**
	 * get access to analyzer configuration
	 * @return the analyzer used by this object
	 */
	NumericalAnalysis<T> getAnalyzer ();

}
