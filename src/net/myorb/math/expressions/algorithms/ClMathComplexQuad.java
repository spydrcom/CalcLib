
package net.myorb.math.expressions.algorithms;

import net.myorb.math.computational.Parameterization;
import net.myorb.math.computational.integration.Quadrature;
import net.myorb.math.computational.integration.RealIntegrandFunctionBase;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.LibraryObject;
import net.myorb.math.expressions.tree.RangeNodeDigest;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.DataConversions;
import net.myorb.math.expressions.SymbolMap;

import java.util.Map;

/**
 * a manager for building quadrature consumer objects 
 * @param <T> data type being processed
 * @author Michael Druckman
 */
public class ClMathComplexQuad<T> extends InstanciableFunctionLibrary<T>
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
		return new QuadAbstraction (named);
	}
	
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public SymbolMap.Named getInstance (String sym, LibraryObject<T> lib)
	{
		this.sym = sym;
		this.options = Parameterization.copy (lib.getParameterization ());
		return new QuadAbstraction (sym);
	}
	protected Parameterization.Hash options;
	protected String sym;
	
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getIterationConsumerDescription()
	 */
	public Map<String, Object> getIterationConsumerDescription ()
	{
		return new Parameterization.Hash (sym, "CLASSPATH", ClMathComplexQuad.class, options);
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
			QuadAxisIntegrand<T> integrand = new QuadAxisIntegrand<T>(digest, environment);
			double lo = cvt.toDouble (digest.getLoBnd ()), hi = cvt.toDouble (digest.getHiBnd ());
			integrand.setAxisComponentNumber (0); double real = integralFor (integrand).eval (0.0, lo, hi);
			integrand.setAxisComponentNumber (1); double imag = integralFor (integrand).eval (0.0, lo, hi);
			return integrand.construct (real, imag);
		}
		protected DataConversions<T> cvt;
	
		/**
		 * @param integrand  description of integral target
		 * @return the quadrature object
		 */
		public Quadrature.Integral integralFor (QuadAxisIntegrand<T> integrand)
		{
			Quadrature.Integral integral = algorithm.getIntegral (integrand);
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
			(sym, "FACTORY", ClMathComplexQuad.class, options);
		}
		protected Parameterization.Hash configuration;
	
	}


}


/**
 * function description of expression in integral target
 * @param <T> data type being processed
 */
class QuadAxisIntegrand<T> extends RealIntegrandFunctionBase
	implements ClMathQuad.AccessToTarget<T>
{

	QuadAxisIntegrand (RangeNodeDigest<T> digest, Environment<T> environment)
	{
		this.mgr = (ExpressionComponentSpaceManager<T>) environment.getSpaceManager ();
		(this.digest = digest).initializeLocalVariable ();
		this.vm = environment.getValueManager ();
	}
	protected ExpressionComponentSpaceManager<T> mgr;
	protected RangeNodeDigest<T> digest;
	protected ValueManager<T> vm;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.RealIntegrandFunctionBase#eval(java.lang.Double)
	 */
	public Double eval (Double t)
	{
		digest.setLocalVariableValue
		(vm.newDiscreteValue (mgr.convertFromDouble (t)));
		T result = vm.toDiscrete (digest.evaluateTarget ());
		return mgr.component (result, componentNumber);
	}
	public void setAxisComponentNumber (int componentNumber)
	{ this.componentNumber = componentNumber; }
	protected int componentNumber;

	/**
	 * @param components the components collected
	 * @return the constructed result as generic value
	 */
	public GenericValue construct (double... components)
	{
		return vm.newDiscreteValue (mgr.construct (components));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.ClMathQuad.AccessToTarget#getTargetAccess()
	 */
	public RangeNodeDigest<T> getTargetAccess ()
	{
		return digest;
	}

}

