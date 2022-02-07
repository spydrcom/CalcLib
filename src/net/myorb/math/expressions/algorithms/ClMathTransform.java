
package net.myorb.math.expressions.algorithms;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.TrapezoidIntegration;

import net.myorb.math.computational.integration.Quadrature;
import net.myorb.math.computational.integration.RealIntegrandFunctionBase;
import net.myorb.math.computational.integration.transforms.*;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.gui.rendering.Atomics;

import net.myorb.math.expressions.tree.RangeNodeDigest;

import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.LibraryObject;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

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
		this.options = Parameterization.copy (configuration);
		return new TransformAbstraction (named);
	}
	protected Parameterization.Hash options;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public SymbolMap.Named getInstance (String sym, LibraryObject<T> lib)
	{
		this.sym = sym;
		this.options = Parameterization.copy (lib.getParameterization ());
		return new TransformAbstraction (sym);
	}
	protected String sym;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getIterationConsumerDescription()
	 */
	public Map<String, Object> getIterationConsumerDescription ()
	{
		return new Parameterization.Hash (sym, "CLASSPATH", ClMathTransform.class, options);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#buildIterationConsumer(java.util.Map)
	 */
	public IterationConsumer buildIterationConsumer (Map<String, Object> options)
	{
		this.sym =
			options.get ("SYMBOL").toString ();
		this.options = Parameterization.copy (options);
		return new TransformAbstraction (sym).getIterationConsumer ();
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
			this.processConfiguration ();
			this.setKernel ();
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
		protected NucleusCore<T> kernel;

		/**
		 * identify Transform kind and construct a kernel object
		 */
		void setKernel ()
		{
			parameters = new TransformParameters (configuration);
			
			switch (parameters.getKind ())
			{
				case FOURIER:
					kernel = new FourierNucleus<T> (environment, parameters);
					break;

				case HILBERT:
					kernel = new HilbertNucleus<T> (environment, parameters);
					break;

				case LAPLACE:
					kernel = new LaplaceNucleus<T> (environment, parameters);
					break;

				case MELLIN:
					kernel = new MellinNucleus<T> (environment, parameters);
					break;

				default:
					throw new RuntimeException ("Kernel specified is not implemented");
			}
		}
		protected TransformParameters parameters;

		/**
		 * compute integral of the integrand function over digest bounds
		 * @param integrand the function describing the integration target
		 * @param digest the range description holding the target function and integration bounds
		 * @return the calculated integral
		 */
		public GenericValue integralOf (Function<T> integrand, RangeNodeDigest<T> digest)
		{
			T result = null;
			String method = parameters.getParameter ("method");

			if (method == null)
			{
				TrapezoidIntegration<T> integral =
						new TrapezoidIntegration<T>(integrand);
				result = compute (integral, digest);
			}
			else
			{
				ExpressionSpaceManager<T> sm = environment.getSpaceManager ();
				RealIntegrandFunctionBase quadIg = new QuadIntegrandWrapper<T> (integrand, sm);
				result = compute (new Quadrature (options).getIntegral (quadIg), digest, sm);
			}

			return vm.newDiscreteValue (result);
		}

		/**
		 * approximation of integral using real-number Quadrature
		 * @param integral the Quadrature approximation object based on Double data type
		 * @param over the range object providing the parameters to the integration
		 * @param sm a space manager for the integrand data type
		 * @return the computed Quadrature value
		 */
		public T compute
			(
				Quadrature.Integral integral, RangeNodeDigest<T> over,
				ExpressionSpaceManager<T> sm
			)
		{
			return sm.convertFromDouble
			(
				integral.eval
				(
					0.0,
					sm.convertToDouble (vm.toDiscrete (over.getLoBnd ())),
					sm.convertToDouble (vm.toDiscrete (over.getHiBnd ()))
				)
			);
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
		 * @see net.myorb.math.expressions.algorithms.QuadratureBase#specialCaseRenderSection(net.myorb.math.expressions.symbols.AbstractVectorReduction.Range, net.myorb.math.expressions.gui.rendering.NodeFormatting)
		 */
		public String specialCaseRenderSection (Range range, NodeFormatting using)
		{
			return	kernelReference (range, using) + Atomics.multiplicationOperatorReference (using);
		}
		public String kernelReference (Range range, NodeFormatting using)
		{
			return	Atomics.reference (kernel.getKernelName (), using) +
					parameterList (range.getIdentifier (), using);
		}
		public String parameterList (String rangeIdentifier, NodeFormatting using)
		{
			StringBuffer references = new StringBuffer ();
			String rangeId = Atomics.reference (rangeIdentifier, using),
					basis = Atomics.reference (options.get ("basis").toString (), using);
			if (kernel.isKernelInverse ()) references.append (rangeId).append (",").append (basis);
			else references.append (basis).append (",").append (rangeId);
			return using.formatParenthetical (references.toString ());
		}

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
			(sym, "FACTORY", ClMathTransform.class, options);
		}
		protected Parameterization.Hash configuration;

	}


}


/**
 * wrap a typed function as operating on real numbers
 * @param <T> the specified data type
 */
class QuadIntegrandWrapper<T> extends RealIntegrandFunctionBase
{

	QuadIntegrandWrapper
		(
			Function<T> integrand,
			ExpressionSpaceManager<T> sm
		)
	{
		this.integrand = integrand;
		this.sm = sm;
	}
	protected ExpressionSpaceManager<T> sm;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.RealIntegrandFunctionBase#eval(java.lang.Double)
	 */
	public Double eval (Double t)
	{
		T result =
			integrand.eval (sm.convertFromDouble (t));
		return sm.convertToDouble (result);
	}
	protected Function<T> integrand;
	
}

