
package net.myorb.math.linalg;

import net.myorb.math.matrices.decomposition.ColtSVD;
import net.myorb.math.expressions.ExpressionSpaceManager;

/**
 * implementation of Solution Primitives using Colt SVD
 * - Colt implementation only available using Double data type
 * - GET function provides for S, V, U, RANK, COND, and 2NORM
 * @author Michael Druckman
 */
public class ColtSVDSolution extends ColtSVD
	implements SolutionPrimitives <Double>
{
	public ColtSVDSolution (ExpressionSpaceManager <Double> mgr) {}
}
