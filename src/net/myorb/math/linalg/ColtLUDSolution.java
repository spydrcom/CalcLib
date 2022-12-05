
package net.myorb.math.linalg;

import net.myorb.math.matrices.decomposition.ColtLUD;
import net.myorb.math.expressions.ExpressionSpaceManager;

/**
 * implementation of Solution Primitives using Colt LUD
 * - Colt implementation only available using Double data type
 * - this is useful for access to lower and upper matrix components
 * - GET function provides for L, U, P, and also DET
 * @author Michael Druckman
 */
public class ColtLUDSolution extends ColtLUD
	implements SolutionPrimitives.Determinable <Double>,
			SolutionPrimitives <Double>
{
	public ColtLUDSolution (ExpressionSpaceManager <Double> mgr) {}
}
