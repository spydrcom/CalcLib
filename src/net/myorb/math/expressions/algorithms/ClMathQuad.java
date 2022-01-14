
package net.myorb.math.expressions.algorithms;

import net.myorb.math.computational.integration.Quadrature;
import net.myorb.math.computational.integration.RealIntegrandFunctionBase;

import net.myorb.math.expressions.SymbolMap.Named;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.symbols.LibraryObject;
import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.math.expressions.DataConversions;

import java.util.Map;

/**
 * a manager for building quadrature consumer objects 
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ClMathQuad<T> extends AlgorithmImplementationAbstraction<T>
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public Named getInstance (String sym, LibraryObject<T> lib)
	{
		return new QuadAbstraction (sym, lib);
	}


	/**
	 * Quad function object base class
	 */
	public class QuadAbstraction extends QuadratureBase<T>
	{

		QuadAbstraction (String sym, LibraryObject<T> lib)
		{
			super (sym, environment);
			this.options = lib.getParameterization ();
			this.cvt = environment.getConversionManager ();
		}
		protected Map<String, Object> options;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.QuadratureBase#evaluate(net.myorb.math.expressions.tree.RangeNodeDigest)
		 */
		public GenericValue evaluate (RangeNodeDigest<T> digest)
		{
			Quadrature.Integral algorithm = new Quadrature
					( new QuadIntegrand<T> (digest, environment), options ).getIntegral ();
			double lo = cvt.convert (digest.getLoBnd ()), hi = cvt.convert (digest.getHiBnd ());
			return cvt.convert (algorithm.eval (0.0, lo, hi));
		}
		protected DataConversions<T> cvt;

	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction#configureManager(java.util.Map)
	 */
	public net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction.Configuration<T>
	configureManager (Map<String, Object> parameterMap)
	{ return null; }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.AlgorithmImplementationAbstraction#provideImplementation()
	 */
	public AlgorithmImplementationAbstraction<T>.Implementation
	provideImplementation ()
	{ return null; }


}


/**
 * function description of expression in integral target
 */
class QuadIntegrand<T> extends RealIntegrandFunctionBase
{

	QuadIntegrand (RangeNodeDigest<T> digest, Environment<T> environment)
	{
		digest.initializeLocalVariable ();
		this.cvt = environment.getConversionManager ();
		this.digest = digest;
	}
	protected RangeNodeDigest<T> digest;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.RealIntegrandFunctionBase#eval(java.lang.Double)
	 */
	public Double eval (Double t)
	{
		digest.setLocalVariableValue (cvt.convert (t));
		return cvt.convert (digest.evaluateTarget ());
	}
	protected DataConversions<T> cvt;

}

