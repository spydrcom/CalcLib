
package net.myorb.math.expressions.algorithms;

import net.myorb.math.specialfunctions.Gamma;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.symbols.IterationConsumer;
import net.myorb.math.expressions.symbols.LibraryObject;
import net.myorb.math.expressions.tree.RangeNodeDigest;

import net.myorb.math.expressions.DataConversions;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.math.computational.integration.Quadrature;
import net.myorb.math.computational.integration.RealIntegrandFunctionBase;
import net.myorb.math.computational.splines.GenericSplineQuad.AccessToTarget;
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
		//System.out.println ("import " + named + " : " + configuration);
		return new FraculusAbstraction (named);
	}
	
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.InstanciableFunctionLibrary#getInstance(java.lang.String, net.myorb.math.expressions.symbols.LibraryObject)
	 */
	public SymbolMap.Named getInstance (String sym, LibraryObject<T> lib)
	{
		this.sym = sym;
		this.options = Parameterization.copy (lib.getParameterization ());
		//System.out.println ("instance " + sym + " : " + options);
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
		//System.out.println ("build " + sym + " : " + options);
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
		 * @param digest description of integral target
		 * @return the quadrature object
		 */
		public Quadrature.Integral integralFor (RangeNodeDigest<T> digest)
		{
			FracQuadIntegrand<T> target =
					new FracQuadIntegrand<T>(digest, options, environment);
			Quadrature.Integral integral = algorithm.getIntegral (target);
			//System.out.println ("integral for " + sym + " : " + options);
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
			(sym, "FACTORY", ClMathCalculus.class, options);
		}
		protected Parameterization.Hash configuration;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.symbols.AbstractVectorReduction#markupForDisplay(java.lang.String, net.myorb.math.expressions.symbols.AbstractVectorReduction.Range, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
		 */
		public String markupForDisplay (String operator, Range range, String parameters, NodeFormatting using)
		{
			//return using.integralRange (using.formatIdentifierReference ("I"), range) + parameters;
			return using.rangeSpecificationNotation (op (range), parameters);
		}

		String op (Range range)
		{
			String text =
					"  <mrow>" +
					"    <msub> <mrow><mo>.</mo></mrow>" + range.getLoBound () + "</msub>" +
					"  </mrow>" +
					"  <mrow>" +
					"    <msubsup>" +
					"      <mrow><mi>I</mi></mrow>" +
					"      <mrow>" + range.getHiBound () + "</mrow>" +
					"      <mrow>" + "<mn>1/2</mn>" + "</mrow>" +
					"    </msubsup>" +
					"  </mrow>";
			return text;
		}
	}

}


/**
 * function description of expression in integral target
 * @param <T> data type being processed
 */
class FracQuadIntegrand<T>
	extends RealIntegrandFunctionBase
	implements AccessToTarget
{

	FracQuadIntegrand
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options,
			Environment <T> environment
		)
	{
		digest.initializeLocalVariable ();
		this.cvt = environment.getConversionManager ();
		this.parameters = new ParameterManager <T> (environment);
		this.prepareMu (digest, options);
		this.digest = digest;
	}

	double eval (String source)
	{
		parameters.setExpression (source);
		return cvt.toDouble (parameters.eval ());
	}
	protected ParameterManager <T> parameters;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.algorithms.ClMathQuad.AccessToTarget#getTargetAccess()
	 */
	public RangeNodeDigest <T> getTargetAccess () { return digest; }
	protected RangeNodeDigest <T> digest;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.RealIntegrandFunctionBase#eval(java.lang.Double)
	 */
	public Double eval (Double t)
	{
		digest.setLocalVariableValue (cvt.toGeneric (t));
		double integrandValue = cvt.toDouble (digest.evaluateTarget ());
		return integrandValue * mu (t) * coef;
	}
	protected DataConversions <T> cvt;
	
	/**
	 * compute the values needed for the mu computation
	 * @param digest the digest of the integrand consumer
	 * @param options the options specified on the consumer
	 */
	void prepareMu
		(
			RangeNodeDigest <T> digest,
			Parameterization.Hash options
		)
	{
		this.GAMMA = new Gamma ();
		this.upperBound = cvt.toDouble (digest.getHiBnd ()) ;
		this.parameterize (eval (options.get ("order").toString ()));
		options.put ("alpha", alpha);
		options.put ("K", k);
	}
	protected double upperBound, exponent, coef;
	protected Gamma GAMMA;

	void parameterize (double p)
	{
		this.alpha = p;
		while (alpha < 1) { alpha += 1; k++; }
		this.coef = 1 / GAMMA.eval (alpha);
		this.exponent = alpha - 1;
		this.p = p;
	}
	protected int k = 0;		// order of required derivatives	}	p + alpha = k
	protected double p = 1;		// requested order of result		}	and
	protected double alpha;		// order of the integral			}	alpha > 1 to avoid asymptote

	/**
	 * @param t the value of the integrand variable
	 * @return the computed mu factor
	 */
	double mu (double t)
	{
		return coef * Math.pow (upperBound - t, exponent);
	}

}

