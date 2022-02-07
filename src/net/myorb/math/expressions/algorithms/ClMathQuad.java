
package net.myorb.math.expressions.algorithms;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.integration.Quadrature;
import net.myorb.math.computational.integration.RealIntegrandFunctionBase;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.LibraryObject;
import net.myorb.math.expressions.tree.RangeNodeDigest;

import net.myorb.math.expressions.DataConversions;
import net.myorb.math.expressions.SymbolMap;

import java.util.HashMap;
import java.util.Map;

/**
 * a manager for building quadrature consumer objects 
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ClMathQuad<T> extends InstanciableFunctionLibrary<T>
	implements SymbolMap.FactoryForImports
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.FactoryForImports#importSymbolFrom(java.lang.String, java.util.Map)
	 */
	public SymbolMap.Named importSymbolFrom
	(String named, Map<String, Object> configuration)
	{
		this.sym = named;
		this.options = configuration;
		return new QuadAbstraction (named);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public SymbolMap.Named getInstance (String sym, LibraryObject<T> lib)
	{
		this.sym = sym;
		this.options = lib.getParameterization ();
		return new QuadAbstraction (sym);
	}
	protected Map<String, Object> options;
	protected String sym;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getIterationConsumerDescription()
	 */
	public Map<String, Object> getIterationConsumerDescription ()
	{
		Map<String, Object> description = new HashMap<String, Object>();
		description.put ("CLASSPATH", ClMathQuad.class.getCanonicalName ());
		description.put ("SYMBOL", sym);
		description.putAll (options);
		return description;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#buildIterationConsumer(java.util.Map)
	 */
	public IterationConsumer buildIterationConsumer (Map<String, Object> options)
	{
		this.options = options;
		this.sym = options.get ("SYMBOL").toString ();
		QuadAbstraction quad = new QuadAbstraction (sym);
		return quad.getIterationConsumer ();
	}


	/**
	 * Quad function object base class
	 */
	public class QuadAbstraction extends QuadratureBase<T>
			implements SymbolMap.ImportedConsumer
	{

		QuadAbstraction (String sym)
		{
			super (sym, environment);
			this.processConfiguration ();
			this.cvt = environment.getConversionManager ();
			this.algorithm = new Quadrature (this.configuration);
		}
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
			Quadrature.Integral integral;
			integral = algorithm.getIntegral (new QuadIntegrand<T>(digest, environment));
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
		digest.setLocalVariableValue (cvt.toGeneric (t));
		return cvt.toDouble (digest.evaluateTarget ());
	}
	protected DataConversions<T> cvt;

}

