
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.gui.rendering.Atomics;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.LibraryObject;
import net.myorb.math.expressions.tree.RangeNodeDigest;

import net.myorb.math.expressions.DataConversions;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.math.computational.integration.Quadrature;
import net.myorb.math.computational.Parameterization;

import java.util.Map;

/**
 * a manager for consumer objects using fractional calculus
 * - implementation of integral and derivative of orders in real number domain
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ClMathCalculus <T>
	extends InstanciableFunctionLibrary <T>
	implements SymbolMap.FactoryForImports
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.FactoryForImports#importSymbolFrom(java.lang.String, java.util.Map)
	 */
	public SymbolMap.Named importSymbolFrom
	(String named, Map<String, Object> configuration)
	{
		this.sym = named;
		this.options = Parameterization.copy (configuration);
		return new FraculusAbstraction (named);
	}
	
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public SymbolMap.Named getInstance (String sym, LibraryObject<T> lib)
	{
		this.sym = sym;
		this.options = Parameterization.copy (lib.getParameterization ());
		return new FraculusAbstraction (sym);
	}
	protected Parameterization.Hash options;
	protected String sym;
	
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getIterationConsumerDescription()
	 */
	public Map<String, Object> getIterationConsumerDescription ()
	{
		return new Parameterization.Hash (sym, "CLASSPATH", ClMathCalculus.class, options);
	}
	
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#buildIterationConsumer(java.util.Map)
	 */
	public IterationConsumer buildIterationConsumer (Map<String, Object> options)
	{
		this.options =
			Parameterization.copy (options);
		this.sym = options.get ("SYMBOL").toString ();
		FraculusAbstraction quad = new FraculusAbstraction (sym);
		return quad.getIterationConsumer ();
	}
	
	
	/**
	 * Fractional Calculus Abstraction function object base class
	 */
	public class FraculusAbstraction extends QuadratureBase<T>
			implements SymbolMap.ImportedConsumer
	{
	
		FraculusAbstraction (String sym)
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
		 * get access to a quadrature configured to use Cauchy
		 * @param digest description of integral target
		 * @return the quadrature object
		 */
		public Quadrature.Integral integralFor (RangeNodeDigest <T> digest)
		{
			return getTransform ().constructIntegral (digest, options);
		}

		/**
		 * FracUlus uses the Cauchy transformed integrand (of some variation)
		 * @return access to the transformed integrand
		 */
		public Quadrature.UsingTransform <T> getTransform ()
		{
			Quadrature.UsingTransform <T>
				transform = algorithm.getTransform ();
			environment.provideAccessTo (transform);
			return transform;
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
			(sym, "FACTORY", ClMathCalculus.class, options);
		}
		protected Parameterization.Hash configuration;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.symbols.AbstractVectorReduction#markupForDisplay(java.lang.String, net.myorb.math.expressions.symbols.AbstractVectorReduction.Range, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
		 */
		public String markupForDisplay (String operator, Range range, String parameters, NodeFormatting using)
		{
			String id = options.get ("ID").toString (),
				order = options.get ("orderNode").toString ();
			return using.rangeSpecificationNotation
					(
						Atomics.nonLocalOperation (id, range, order),
						parameters
					);
		}

	}

}


