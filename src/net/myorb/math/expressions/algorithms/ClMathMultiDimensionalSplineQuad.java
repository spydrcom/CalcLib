
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.math.expressions.ValueManager;

/**
 * compute integral of a function 
 *  which has been attached to a spline for evaluation
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ClMathMultiDimensionalSplineQuad <T>
		extends ClMathSplineQuad <T>
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.ClMathSplineQuad#getQuadAbstraction(java.lang.String)
	 */
	protected QuadAbstraction getQuadAbstraction (String named)
	{
		return new MultiDimensionalSplineQuadAbstraction (named);
	}


	/**
	 * Quad function object base class
	 */
	public class MultiDimensionalSplineQuadAbstraction
				extends QuadAbstraction
	{

		MultiDimensionalSplineQuadAbstraction (String sym)
		{
			super (sym);
			this.vm = environment.getValueManager ();
			this.mgr = environment.getSpaceManager ();
		}
		ExpressionSpaceManager <T> mgr;
		ValueManager <T> vm;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.QuadratureBase#evaluate(net.myorb.math.expressions.tree.RangeNodeDigest)
		 */
		public GenericValue evaluate (RangeNodeDigest <T> digest)
		{
			for (String id : digest.getTargetExpression ().getIdentifiers ())
			{
				Object item = digest.find (id);

				if (item instanceof Subroutine)
				{
					@SuppressWarnings("unchecked")
					Subroutine <T> s = (Subroutine <T>) item;

					if (s.hasAttachedSpline ())
					{
						return computeQuadrature (s, digest);
					}
				}
			}

			throw new RuntimeException ("No spline found for quadrature");
		}

		/**
		 * @param spline the spline found in the quadrature expression
		 * @param digest the digest which holds the range of the evaluation
		 * @return the result of the quadrature computation as a generic value
		 */
		public GenericValue computeQuadrature
			(Subroutine <T> spline, RangeNodeDigest <T> digest)
		{
			double
				lo = mgr.convertToDouble (vm.toDiscrete (digest.getLoBnd ())),
				hi = mgr.convertToDouble (vm.toDiscrete (digest.getHiBnd ()));
			T result = spline.evalIntegralOver (lo, hi);
			return vm.newDiscreteValue (result);
		}

	}


}

