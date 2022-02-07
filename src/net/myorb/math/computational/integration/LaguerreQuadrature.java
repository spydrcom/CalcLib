
package net.myorb.math.computational.integration;

import net.myorb.math.computational.IterativeProcessingSupportTabular;
import net.myorb.math.expressions.gui.rendering.Atomics;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.symbols.AbstractVectorReduction.Range;
import net.myorb.math.computational.GLQuadrature;

/**
 * configuration object for Gauss-Laguerre quadrature implementations
 * @author Michael Druckman
 */
public class LaguerreQuadrature extends CommonQuadrature
{

	public LaguerreQuadrature
		(
			RealIntegrandFunctionBase integrand,
			Configuration parameters
		)
	{
		super (integrand, parameters);
		configureLists ();
	}

	public void configureLists ()
	{
		String using = parameters.getParameterUC ("using");
		
		if (using != null)
		{
			laguerreLists = GLQuadrature.getListCalled (using);
		}
		else
		{
			int forOrder = parameters.getValue ("order").intValue ();
			double domainHi = parameters.getValue ("hi").doubleValue ();
			laguerreLists = GLQuadrature.computeWeights (forOrder, domainHi);
		}

		if (parameters.getParameter ("show") != null)
		{
			IterativeProcessingSupportTabular.enableDisplay ();
			GLQuadrature.show ("lists", laguerreLists);
		}
	}
	protected GLQuadrature.LaguerreLists laguerreLists;

	/**
	 * format a special case portion of a render
	 * @param range the range descriptor that introduced the integral
	 * @param using the node formatting support object supplied for the render
	 * @return mark-up for section specific to an algorithm
	 */
	public static String specialCaseRenderSection (Range range, NodeFormatting using)
	{
		return using.formatSuperScript
				(
					Atomics.reference ("epsilon", using),
					Atomics.negateOperatorReference (using) + Atomics.reference (range.getIdentifier (), using)
				) + Atomics.multiplicationOperatorReference (using);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.Quadrature.Integral#eval(double, double, double)
	 */
	public double eval (double x, double lo, double hi)
	{
		return GLQuadrature.approximateIntegral (integrand, laguerreLists);
	}

}
