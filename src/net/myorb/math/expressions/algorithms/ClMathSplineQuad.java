
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.SymbolMap;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.LibraryObject;
//import net.myorb.math.expressions.symbols.AbstractVectorReduction.Range;
import net.myorb.math.expressions.tree.RangeNodeDigest;

import net.myorb.math.computational.integration.RealDomainIntegration;
import net.myorb.math.computational.splines.GenericSplineQuad;
import net.myorb.math.computational.Parameterization;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * quadrature consumer objects for spline symbols
 * @param <T> the data type for the integration results
 * @author Michael Druckman
 */
public class ClMathSplineQuad<T> extends ClMathQuad<T>
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.FactoryForImports#importSymbolFrom(java.lang.String, java.util.Map)
	 */
	public SymbolMap.Named importSymbolFrom
	(String named, Map<String, Object> configuration)
	{
		this.sym = named;
		this.options = Parameterization.copy (configuration);
		return new SplineQuadAbstraction (named);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public SymbolMap.Named getInstance (String sym, LibraryObject<T> lib)
	{
		this.sym = sym;
		this.options = Parameterization.copy (lib.getParameterization ());
		return new SplineQuadAbstraction (sym);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#buildIterationConsumer(java.util.Map)
	 */
	public IterationConsumer buildIterationConsumer (Map<String, Object> options)
	{
		this.options =
			Parameterization.copy (options);
		this.sym = options.get ("SYMBOL").toString ();
		QuadAbstraction quad = new SplineQuadAbstraction (sym);
		return quad.getIterationConsumer ();
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getIterationConsumerDescription()
	 */
	public Map<String, Object> getIterationConsumerDescription ()
	{
		return new Parameterization.Hash (sym, "CLASSPATH", ClMathSplineQuad.class, options);
	}


	/**
	 * Quad function object base class
	 */
	public class SplineQuadAbstraction extends QuadAbstraction
	{

		SplineQuadAbstraction (String sym)
		{
			super (sym);
			this.digestMap = new HashMap<>();
			this.vm = environment.getValueManager ();
		}
		Map<RangeNodeDigest<T>,RealDomainIntegration<T>> digestMap;
		ValueManager<T> vm;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.QuadratureBase#evaluate(net.myorb.math.expressions.tree.RangeNodeDigest)
		 */
		public GenericValue evaluate (RangeNodeDigest<T> digest)
		{
			RealDomainIntegration<T> splineOps = digestMap.get (digest);

			if (splineOps == null)
			{
				splineOps = examineDigest (digest);
				digestMap.put (digest, splineOps);
			}

			return vm.newDiscreteValue
			(
				splineOps.evalIntegralOver
				(
					cvt.toDouble (digest.getLoBnd ()),
					cvt.toDouble (digest.getHiBnd ())
				)
			);
		}

		/**
		 * @param digest the description of the integrand
		 * @return the spline symbol integration interface
		 */
		public RealDomainIntegration<T> examineDigest (RangeNodeDigest<T> digest)
		{
			Set<String> ids = GenericSplineQuad.connectIntegral (digest);
			return GenericSplineQuad.findSymbol (ids, environment);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.QuadratureBase#markupForDisplay(java.lang.String, net.myorb.math.expressions.symbols.AbstractVectorReduction.Range, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
		 */
		public String markupForDisplay (String operator, Range range, String parameters, NodeFormatting using)
		{
			return using.rangeSpecificationNotation
				(
					integralRange (range, using),
					specialCaseRenderSection (range, using) + parameters
				);
		}

	}


}

