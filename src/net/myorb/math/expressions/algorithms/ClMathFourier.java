
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.LibraryObject;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.math.computational.integration.FourierNucleus;
import net.myorb.math.computational.TrapezoidIntegration;

import net.myorb.math.complexnumbers.ComplexValue;

import java.util.HashMap;
import java.util.Map;

/**
 * a manager for building Fourier Transform consumer objects 
 * @author Michael Druckman
 */
public class ClMathFourier extends InstanciableFunctionLibrary<ComplexValue<Double>>
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
		return new TransformAbstraction (named);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public SymbolMap.Named getInstance (String sym, LibraryObject<ComplexValue<Double>> lib)
	{
		this.sym = sym;
		this.options = lib.getParameterization ();
		return new TransformAbstraction (sym);
	}
	protected Map<String, Object> options;
	protected String sym;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getIterationConsumerDescription()
	 */
	public Map<String, Object> getIterationConsumerDescription ()
	{
		Map<String, Object> description = new HashMap<String, Object>();
		description.put ("CLASSPATH", ClMathFourier.class.getCanonicalName ());
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
		TransformAbstraction transform = new TransformAbstraction (sym);
		return transform.getIterationConsumer ();
	}


	/**
	 * Quad function object base class
	 */
	public class TransformAbstraction extends QuadratureBase<ComplexValue<Double>>
		implements SymbolMap.ImportedConsumer
	{

		TransformAbstraction (String sym)
		{
			super (sym, environment);
			this.vm = environment.getValueManager ();
			this.configuration = new HashMap<String, Object>();
			this.configuration.put ("FACTORY", ClMathFourier.class.getCanonicalName ());
			this.configuration.put ("SYMBOL", sym);
			this.configuration.putAll (options);
		}

		/**
		 * @param integrand the integrand to use in the integral computation
		 * @param over the range object providing the parameters to the integration
		 * @return the computed integral value
		 */
		public ComplexValue<Double> integralOf
			(FourierIntegrand integrand, RangeNodeDigest<ComplexValue<Double>> over)
		{
			TrapezoidIntegration<ComplexValue<Double>> integral =
					new TrapezoidIntegration<ComplexValue<Double>>(integrand);
			return compute (integral, over);
		}

		/**
		 * @param integral the integral approximation object
		 * @param over the range object providing the parameters to the integration
		 * @return the computed integral value
		 */
		public ComplexValue<Double> compute
			(
				TrapezoidIntegration<ComplexValue<Double>> integral,
				RangeNodeDigest<ComplexValue<Double>> over
			)
		{
			return integral.eval
				(
					vm.toDiscrete (over.getLoBnd ()),
					vm.toDiscrete (over.getHiBnd ()),
					vm.toDiscrete (over.getDelta ())
				);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.QuadratureBase#evaluate(net.myorb.math.expressions.tree.RangeNodeDigest)
		 */
		public GenericValue evaluate (RangeNodeDigest<ComplexValue<Double>> digest)
		{
			FourierIntegrand integrand = new FourierIntegrand
				(digest, environment, configuration);
			return vm.newDiscreteValue (integralOf (integrand, digest));
		}
		protected final ValueManager<ComplexValue<Double>> vm;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.SymbolMap.ConfiguredImport#getConfiguration()
		 */
		public Map<String, Object> getConfiguration ()
		{
			return configuration;
		}
		protected Map<String, Object> configuration;

	}


}


/**
 * function description of expression in integral target
 */
class FourierIntegrand extends FourierNucleus
{

	FourierIntegrand
		(
			RangeNodeDigest <ComplexValue <Double>> digest,
			Environment <ComplexValue <Double>> environment,
			Map <String, Object> configuration
		)
	{
		super (environment, configuration);
		this.manager = environment.getSpaceManager ();
		digest.initializeLocalVariable ();
		this.digest = digest;
	}
	protected final ExpressionSpaceManager<ComplexValue<Double>> manager;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.RealIntegrandFunctionBase#eval(java.lang.Double)
	 */
	public ComplexValue<Double> eval (ComplexValue<Double> t)
	{
		ComplexValue<Double> nucleus = super.eval (t);
		digest.setLocalVariableValue (vm.newDiscreteValue (t));
		ComplexValue<Double> functionValue = vm.toDiscrete (digest.evaluateTarget ());
		return manager.multiply (nucleus, functionValue); 
	}
	protected RangeNodeDigest<ComplexValue<Double>> digest;

}

