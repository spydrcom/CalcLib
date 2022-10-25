
package net.myorb.math.computational.splines;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.VCNLUD;

import net.myorb.data.abstractions.DataSequence;

/**
 * algorithm support for spline built on Chebyshev nodes
 * @author Michael Druckman
 */
public interface ApproximationTheory
{

	/**
	 * @return the configuration specified for the session
	 */
	Parameterization getConfiguration ();

	/**
	 * generate points for the specified sequence
	 * @param lo the lo end of the domain
	 * @param hi the hi end of the domain
	 * @return sequence of points
	 */
	DataSequence <Double> getSplineDomainFor (double lo, double hi);

	/**
	 * generate comb points for the specified sequence
	 * @param lo the lo end of the domain
	 * @param hi the hi end of the domain
	 * @return sequence of comb points
	 */
	DataSequence <Double> getCombDomainFor (double lo, double hi);

	/**
	 * @return the computed points for the specified order
	 */
	double [] getChebyshevPoints ();

	/**
	 * @return the LUD for the translation of function to polynomial
	 */
	VCNLUD getLud ();

}
