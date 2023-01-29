
package net.myorb.math.computational.integration;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.data.abstractions.FunctionWrapper;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * description primitives for quadrature target functions
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class QuadratureEntities
{


	/**
	 * function description of target to quadrature algorithms
	 */
	public interface TargetSpecification <T> extends FunctionWrapper.F <T> {}


	/**
	 * function wrapper for quadrature integration targets
	 */
	public static class TargetWrapper <T> implements Function <T>
	{

		public TargetWrapper
			(
				TargetSpecification <T> I,
				ExpressionComponentSpaceManager <T> manager
			)
		{ this.I = I; this.manager = manager; }
		protected SpaceManager <T> manager;

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#getSpaceManager()
		 */
		public SpaceManager < T > getSpaceManager () { return manager; }
		public SpaceDescription < T > getSpaceDescription () { return manager; }
		protected TargetSpecification <T> I;

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
		 */
		public T eval (T t) { return I.body (t); }

	}

}
