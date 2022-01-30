
package net.myorb.math.expressions.algorithms;

import net.myorb.math.computational.TrapezoidIntegration;
import net.myorb.math.computational.integration.transforms.FourierNucleus;
import net.myorb.math.computational.integration.transforms.TransformParameters;
import net.myorb.math.expressions.tree.RangeNodeDigest;
import net.myorb.math.expressions.ValueManager.GenericValue;

import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.LibraryObject;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import java.util.HashMap;
import java.util.Map;

/**
 * a manager for building Transform consumer objects 
 * @author Michael Druckman
 */
public class ClMathTransform<T> 
		extends InstanciableFunctionLibrary<T>
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
	public SymbolMap.Named getInstance (String sym, LibraryObject<T> lib)
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
		description.put ("CLASSPATH", ClMathTransform.class.getCanonicalName ());
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
	public class TransformAbstraction extends QuadratureBase<T>
		implements SymbolMap.ImportedConsumer
	{

		TransformAbstraction (String sym)
		{
			super (sym, environment);
			this.vm = environment.getValueManager ();
			this.configuration = new HashMap<String, Object>();
			this.configuration.put ("FACTORY", ClMathTransform.class.getCanonicalName ());
			this.configuration.put ("SYMBOL", sym);
			this.configuration.putAll (options);
		}
		protected ValueManager<T> vm;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.algorithms.QuadratureBase#evaluate(net.myorb.math.expressions.tree.RangeNodeDigest)
		 */
		public GenericValue evaluate (RangeNodeDigest<T> digest)
		{
			digest.initializeLocalVariable (); setKernel ();
			return integralOf (getIntegrand (digest), digest);
		}

		/**
		 * prepare a function that will server as the integration target
		 * @param digest the range description holding the target function and integration bounds
		 * @return the constructed function
		 */
		public Function<T> getIntegrand (RangeNodeDigest<T> digest)
		{
			return new Function<T>()
			{
				public T eval (T t)
				{
					T nucleus = kernel.eval (t);
					digest.setLocalVariableValue (vm.newDiscreteValue (t));
					T functionValue = vm.toDiscrete (digest.evaluateTarget ());
					return manager.multiply (nucleus, functionValue); 
				}
				public SpaceManager<T> getSpaceDescription () { return manager; }
				public SpaceManager<T> getSpaceManager () { return manager; }
			};
		}
		protected Function<T> kernel;

		/**
		 * identify Transform kind and construct a kernel object
		 */
		void setKernel ()
		{
			TransformParameters parameters = new TransformParameters (configuration);
			
			switch (parameters.getKind ())
			{
				case FOURIER:
					kernel = new FourierNucleus<T> (environment, parameters);
					break;

				default:
					throw new RuntimeException ("Kernel kind not recognized");
			}
		}

		/**
		 * compute integral of the integrand function over digest bounds
		 * @param integrand the function describing the integration target
		 * @param digest the range description holding the target function and integration bounds
		 * @return the calculated integral
		 */
		public GenericValue integralOf (Function<T> integrand, RangeNodeDigest<T> digest)
		{
			TrapezoidIntegration<T> integral =
					new TrapezoidIntegration<T>(integrand);
			return vm.newDiscreteValue (compute (integral, digest));
		}

		/**
		 * @param integral the integral approximation object
		 * @param over the range object providing the parameters to the integration
		 * @return the computed integral value
		 */
		public T compute
			(
				TrapezoidIntegration<T> integral,
				RangeNodeDigest<T> over
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
		 * @see net.myorb.math.expressions.SymbolMap.ConfiguredImport#getConfiguration()
		 */
		public Map<String, Object> getConfiguration ()
		{
			return configuration;
		}
		protected Map<String, Object> configuration;

	}


}

