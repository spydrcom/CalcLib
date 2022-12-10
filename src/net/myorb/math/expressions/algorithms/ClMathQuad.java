
package net.myorb.math.expressions.algorithms;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.integration.Quadrature;
import net.myorb.math.computational.integration.RealIntegrandFunctionBase;
import net.myorb.math.computational.splines.GenericSplineQuad.AccessToTarget;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.tree.RangeNodeDigest;

import net.myorb.math.expressions.DataConversions;
import net.myorb.math.expressions.SymbolMap;

import java.util.Map;

/**
 * a manager for building quadrature consumer objects 
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ClMathQuad <T> extends ClMathLibraryFoundation <T>
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.ClMathLibraryFoundation#generateTool(java.lang.String)
	 */
	public SymbolMap.Named generateTool (String named)
	{
		return new QuadAbstraction (named);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#buildIterationConsumer(java.util.Map)
	 */
	public IterationConsumer buildIterationConsumer (Map<String, Object> options)
	{
		this.options =
			Parameterization.copy (options);
		this.sym = options.get ("SYMBOL").toString ();
		QuadAbstraction quad = new QuadAbstraction (sym);
		return quad.getIterationConsumer ();
	}


	/**
	 * Quad function object base class
	 */
	public class QuadAbstraction extends QuadratureBase<T>
			implements SymbolMap.ImportedConsumer, QuadratureBase.AlgorithmExposure
	{

		QuadAbstraction (String sym)
		{
			super (sym, environment);
			this.processConfiguration ();
			this.cvt = environment.getConversionManager ();
			this.algorithm = new Quadrature (this.configuration);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.QuadratureBase.AlgorithmExposure#getAlgorithm()
		 */
		public Quadrature getAlgorithm () { return algorithm; }
		protected Quadrature algorithm;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.QuadratureBase#evaluate(net.myorb.math.expressions.tree.RangeNodeDigest)
		 */
		public GenericValue evaluate (RangeNodeDigest<T> digest)
		{
			return cvt.toGeneric
			(
				integralFor (digest).eval
				(
					0.0,
					cvt.toDouble (digest.getLoBnd ()),
					cvt.toDouble (digest.getHiBnd ())
				)
			);
		}
		protected DataConversions<T> cvt;

		/**
		 * @param digest description of integral target
		 * @return the quadrature object
		 */
		public Quadrature.Integral integralFor (RangeNodeDigest<T> digest)
		{
			QuadIntegrand<T> target =
					new QuadIntegrand<T>(digest, environment);
			Quadrature.Integral integral = algorithm.getIntegral (target);
			environment.provideAccessTo (integral);
			return integral;
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.QuadratureBase#specialCaseRenderSection(net.myorb.math.expressions.symbols.AbstractVectorReduction.Range, net.myorb.math.expressions.gui.rendering.NodeFormatting)
		 */
		public String specialCaseRenderSection (Range range, NodeFormatting using)
		{ return algorithm.specialCaseRenderSection (range, using); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.SymbolMap.ConfiguredImport#getConfiguration()
		 */
		public Map<String, Object> getConfiguration ()
		{
			return configuration;
		}
		public void processConfiguration ()
		{
			this.configuration = new Parameterization.Hash
			(sym, "FACTORY", ClMathQuad.class, options);
		}
		protected Parameterization.Hash configuration;

	}


}


/**
 * function description of expression in integral target
 * @param <T> data type being processed
 */
class QuadIntegrand<T> extends RealIntegrandFunctionBase implements AccessToTarget
{

	QuadIntegrand (RangeNodeDigest<T> digest, Environment<T> environment)
	{
		digest.initializeLocalVariable ();
		this.cvt = environment.getConversionManager ();
		this.digest = digest;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.ClMathQuad.AccessToTarget#getTargetAccess()
	 */
	public RangeNodeDigest<T> getTargetAccess () { return digest; }
	protected RangeNodeDigest<T> digest;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.RealIntegrandFunctionBase#eval(java.lang.Double)
	 */
	public Double eval (Double t)
	{
		digest.setLocalVariableValue (cvt.toGeneric (t));
		return cvt.toDouble (digest.evaluateTarget ());
	}
	protected DataConversions<T> cvt;

}

