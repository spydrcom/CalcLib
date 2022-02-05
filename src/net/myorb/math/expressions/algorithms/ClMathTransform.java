
package net.myorb.math.expressions.algorithms;

import net.myorb.math.computational.TrapezoidIntegration;
import net.myorb.math.computational.integration.Quadrature;
import net.myorb.math.computational.integration.RealIntegrandFunctionBase;
import net.myorb.math.computational.integration.transforms.*;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.tree.RangeNodeDigest;

import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.LibraryObject;
import net.myorb.math.expressions.ConventionalNotations;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.OperatorNomenclature;
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
		this.sym = named; this.copy (configuration);
		return new TransformAbstraction (named);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public SymbolMap.Named getInstance (String sym, LibraryObject<T> lib)
	{
		this.sym = sym; this.copy (lib.getParameterization ());
		return new TransformAbstraction (sym);
	}
	protected String sym;


	/**
	 * @param configuration the parameter hash for the transform
	 */
	public void copy (Map<String, Object> configuration)
	{
		this.options = new HashMap<String, Object>();
		this.options.putAll (configuration);
	}
	protected Map<String, Object> options;


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
				result = compute (new Quadrature (quadIg, options).getIntegral (), digest, sm);
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
		 * @see net.myorb.math.expressions.algorithms.QuadratureBase#markupForDisplay(java.lang.String, net.myorb.math.expressions.symbols.AbstractVectorReduction.Range, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
		 */
		public String markupForDisplay (String operator, Range range, String parameters, NodeFormatting using)
		{
			String transformRender =
				reference (kernel.getKernelName (), using) +
				parameterList (range.getIdentifier (), using) +
				multiplicationOperatorReference (using);
			return using.rangeSpecificationNotation
				(
					using.integralRange
					(
						OperatorNomenclature.INTEGRAL_OPERATOR,
						range
					),
					transformRender + parameters
				);
		}
		public String parameterList (String rangeIdentifier, NodeFormatting using)
		{
			StringBuffer references = new StringBuffer ();
			String rangeId = reference (rangeIdentifier, using),
					basis = reference (options.get ("basis").toString (), using);
			if (kernel.isKernelInverse ()) references.append (rangeId).append (",").append (basis);
			else references.append (basis).append (",").append (rangeId);
			return using.formatParenthetical (references.toString ());
		}
		public String reference (String symbol, NodeFormatting using)
		{
			String identifier =
				ConventionalNotations.determineNotationFor (symbol);
			return using.formatIdentifierReference (identifier);
		}
		public String multiplicationOperatorReference (NodeFormatting using)
		{
			String renderAs = OperatorNomenclature.MULTIPLICATION_OPERATOR;
			String identifiedNotation = ConventionalNotations.findMarkupFor (renderAs);
			return using.formatOperatorReference (identifiedNotation==null? renderAs: identifiedNotation);
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

