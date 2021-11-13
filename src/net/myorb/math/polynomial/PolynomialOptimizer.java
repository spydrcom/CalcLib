
package net.myorb.math.polynomial;

import net.myorb.math.polynomial.families.chebyshev.ChebyshevPolynomialCalculus;
import net.myorb.math.polynomial.families.chebyshev.ChebyshevSplineFunction;

import net.myorb.math.expressions.evaluationstates.DeclarationSupport;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.symbols.AbstractFunction;
import net.myorb.math.expressions.symbols.FunctionWrapper;

import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.TokenParser;

import net.myorb.math.expressions.tree.SemanticAnalysis;
import net.myorb.math.expressions.tree.JsonBinding;
import net.myorb.math.expressions.tree.Expression;
import net.myorb.math.expressions.tree.Element;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;
import net.myorb.math.Function;

import java.util.HashMap;
import java.util.Map;

/**
 * embed coefficients into polynomial definitions to improve efficiency
 * @param <T> type of values from expressions
 * @author Michael Druckman
 */
public class PolynomialOptimizer <T>
{


	/**
	 * a function wrapper for ordinary polynomials
	 */
	public class OrdinarySegment extends PolynomialFunctionWrapper <T>
		implements Polynomial.PowerFunction <T>, JsonBinding.JsonRepresentation <T>
	{
		OrdinarySegment (Polynomial.Coefficients <T> coefficients)
		{ super (new Polynomial <T> (spaceManager), coefficients, spaceManager); }
	}


	/**
	 * a function wrapper for Chebyshev polynomials
	 */
	public class ChebyshevSegment extends ChebyshevSplineFunction <T>
		implements Polynomial.PowerFunction <T>, JsonBinding.JsonRepresentation <T>
	{
		@Override public int getDegree () { return this.getCoefficients ().size () - 1; }
		@Override public Polynomial <T> getPolynomial () { return this; }
		ChebyshevSegment (Polynomial.Coefficients <T> coefficients)
		{ super (coefficients, spaceManager); }
	}


	/**
	 * base class for optimizations
	 */
	public abstract class Optimization
	{

		/**
		 * modify coefficients to conform to operator.
		 *  this is the optimization, the derivative operations become baked into the coefficients.
		 *  this is constant folding, the re-evaluation of the coefficients is no longer done redundantly
		 * @param coefficients the original coefficients of the source function
		 * @return the coefficients for the modified function
		 */
		Polynomial.Coefficients<T>
		getOptimizedCoefficients (Polynomial.Coefficients<T> coefficients)
		// no constant folding necessary for matching original and optimized operators
		// default is unmodified, extended classes will override and insert modifiers
		{ return coefficients; }

		/**
		 * @return the tokens of the description string
		 */
		TokenParser.TokenSequence descriptionTokens () { return FunctionWrapper.tokensFor (getDescription ()); }

		/**
		 * @param coefficients the coefficients of the source function
		 * @return the coefficients for the second derivative
		 */
		Polynomial.Coefficients<T> getSecondDerivativeFor (Polynomial.Coefficients<T> coefficients)
		{ return getDerivativeFor (getDerivativeFor (coefficients)); }

		/**
		 * @param coefficients the coefficients of the source function
		 * @return the coefficients for the derivative
		 */
		abstract Polynomial.Coefficients<T> getDerivativeFor (Polynomial.Coefficients<T> coefficients);

		/**
		 * build the optimized function
		 * @param coefficients the unmodified function coefficients
		 * @return the polynomial function wrapper with the modified coefficients
		 */
		abstract Function<T> getOptimizedFunction (Polynomial.Coefficients<T> coefficients);

		/**
		 * @return the text of the function description
		 */
		abstract String getDescription ();

		/**
		 * @return the operator used by the optimized function
		 */
		abstract String getOptimizedOp ();

		/**
		 * @return the operator used by the original function
		 */
		abstract String getOriginalOp ();

	}


	/*
	 * optimization base classes for each polynomial type
	 */

	/**
	 * ordinary polynomial optimization
	 */
	public class PolynomialOptimization extends Optimization
	{

		PolynomialOptimization ()
		{ this.ordinaryPolynomialCalculus = new OrdinaryPolynomialCalculus<T> (spaceManager); }
		protected OrdinaryPolynomialCalculus <T> ordinaryPolynomialCalculus;

		Polynomial.Coefficients <T>
		getDerivativeFor (Polynomial.Coefficients<T> coefficients)
		{ return ordinaryPolynomialCalculus.computeDerivativeCoefficients (coefficients); }

		Function<T> getOptimizedFunction
		(Polynomial.Coefficients <T> coefficients)
		{ return new OrdinarySegment (getOptimizedCoefficients (coefficients)); }

		String getOptimizedOp () { return OperatorNomenclature.POLY_EVAL_OPERATOR; }
		String getOriginalOp () { return OperatorNomenclature.POLY_EVAL_OPERATOR; }
		String getDescription () { return "Ordinary polynomial"; }

	}

	/**
	 * Chebyshev polynomial optimization
	 */
	public class ChebyshevOptimization extends Optimization
	{

		ChebyshevOptimization ()
		{ this.chebyshevPolynomialCalculus = new ChebyshevPolynomialCalculus<T> (spaceManager); }
		protected ChebyshevPolynomialCalculus <T> chebyshevPolynomialCalculus;

		Polynomial.Coefficients <T>
		getDerivativeFor (Polynomial.Coefficients<T> coefficients)
		{ return chebyshevPolynomialCalculus.getFirstKindDerivative (coefficients); }

		Function<T> getOptimizedFunction
		(Polynomial.Coefficients <T> coefficients)
		{ return new ChebyshevSegment (getOptimizedCoefficients (coefficients)); }

		String getOriginalOp () { return OperatorNomenclature.CLENSHAW_EVAL_OPERATOR; }
		String getOptimizedOp () { return OperatorNomenclature.CLENSHAW_EVAL_OPERATOR; }
		String getDescription () { return "Chebyshev polynomial"; }

	}


	/*
	 * extended versions of optimization base classes by polynomial type and derivative order
	 */

	/**
	 * ordinary polynomial first derivative optimization
	 */
	public class PolynomialPrimeOptimization extends PolynomialOptimization
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.polynomial.PolynomialOptimizer.Optimization#getOptimizedCoefficients(net.myorb.math.GeneratingFunctions.Coefficients)
		 */
		Polynomial.Coefficients<T> getOptimizedCoefficients
		(Polynomial.Coefficients<T> coefficients) { return getDerivativeFor (coefficients); }
		String getDescription () { return "First order derivative interpolation polynomial"; }
		String getOriginalOp () { return OperatorNomenclature.POLY_PRIME_OPERATOR; }
	}

	/**
	 * Chebyshev polynomial first derivative optimization
	 */
	public class ChebyshevPrimeOptimization extends ChebyshevOptimization
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.polynomial.PolynomialOptimizer.Optimization#getOptimizedCoefficients(net.myorb.math.GeneratingFunctions.Coefficients)
		 */
		Polynomial.Coefficients<T> getOptimizedCoefficients
		(Polynomial.Coefficients<T> coefficients) { return getDerivativeFor (coefficients); }
		String getDescription () { return "Chebyshev first order derivative interpolation"; }
		String getOriginalOp () { return OperatorNomenclature.CLENSHAW_PRIME_OPERATOR; }
	}

	/**
	 * ordinary polynomial second derivative optimization
	 */
	public class PolynomialDPrimeOptimization extends PolynomialOptimization
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.polynomial.PolynomialOptimizer.Optimization#getOptimizedCoefficients(net.myorb.math.GeneratingFunctions.Coefficients)
		 */
		Polynomial.Coefficients<T> getOptimizedCoefficients
		(Polynomial.Coefficients<T> coefficients) { return getSecondDerivativeFor (coefficients); }
		String getDescription () { return "Second order derivative interpolation polynomial"; }
		String getOriginalOp () { return OperatorNomenclature.POLY_DPRIME_OPERATOR; }
	}

	/**
	 * Chebyshev second derivative polynomial optimization
	 */
	public class ChebyshevDPrimeOptimization extends ChebyshevOptimization
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.polynomial.PolynomialOptimizer.Optimization#getOptimizedCoefficients(net.myorb.math.GeneratingFunctions.Coefficients)
		 */
		Polynomial.Coefficients<T> getOptimizedCoefficients
		(Polynomial.Coefficients<T> coefficients) { return getSecondDerivativeFor (coefficients); }
		String getDescription () { return "Chebyshev second order derivative interpolation"; }
		String getOriginalOp () { return OperatorNomenclature.CLENSHAW_DPRIME_OPERATOR; }
	}


	/*
	 * map of optimization classes by operator name
	 */

	/**
	 * add each optimization object to the map
	 */
	void mapOptimizations ()
	{
		mapOptimization (new PolynomialOptimization ());
		mapOptimization (new PolynomialPrimeOptimization ());
		mapOptimization (new PolynomialDPrimeOptimization ());
		mapOptimization (new ChebyshevDPrimeOptimization ());
		mapOptimization (new ChebyshevPrimeOptimization ());
		mapOptimization (new ChebyshevOptimization ());
	}
	void mapOptimization (Optimization optimization)
	{
		map.put (optimization.getOriginalOp (), optimization);
	}


	/**
	 * use map to choose optimization based on referenced operation
	 * @param operation the text of the operation reference
	 * @return the associated optimization descriptor
	 */
	public Optimization getOptimizationAssociatedWith (String operation)
	{
		Optimization optimization = map.get (operation);
		if (optimization == null) throw new RuntimeException ("Operation to be optimized must be for polynomial operands");
		return optimization;
	}
	protected Map<String,Optimization> map = new HashMap <> ();


	/*
	 * constructor for optimizer object
	 */

	/**
	 * @param environment source of utility objects
	 */
	public PolynomialOptimizer (Environment <T> environment)
	{
		this.utilities = new CoefficientUtilities <T> (environment);
		this.spaceManager = environment.getSpaceManager ();
		this.mapOptimizations ();
	}
	protected CoefficientUtilities <T> utilities;
	protected SpaceManager <T> spaceManager;


	/*
	 * selection of function form
	 */

	/**
	 * use function wrapper to hold polynomial with coefficients.
	 *  choose expression tree as source if available, otherwise tokens.
	 * @param original the original dot product invocation function
	 * @return the function wrapper for the optimized function
	 */
	public AbstractFunction <T> getOptimizedFunction (AbstractFunction <T> original)
	{
		try { original.enableExpression (); }
		catch (Exception e) { e.printStackTrace (); }
		Expression<T> expression = original.getExpression ();
		if (expression != null) return getOptimizedFunctionFrom (expression, original);
		return getOptimizedFunctionFromTokens (original);
	}


	/*
	 * processing for embedded / INLINE form of function
	 */

	/**
	 * definition for INLINE coefficient optimization
	 * @param function the description of the function being optimized
	 * @return a string buffer with the optimized definition
	 */
	public StringBuffer getOptimizedDefinition (AbstractFunction <T> function)
	{
		return getOptimizedDefinition (function, function.getParameterNameList ().getSingletonParameterName ());
	}
	public StringBuffer getOptimizedDefinition (AbstractFunction <T> function, String parameter)
	{
		DeclarationSupport.TokenStream tokens = checkTokens (function, parameter);
		Optimization optimization = getOptimizationAssociatedWith (tokens.get (1).getTokenImage ());
		Polynomial.Coefficients <T> coefficients = optimization.getOptimizedCoefficients (utilities.getCoefficients (tokens));
		return getEmbeddedDefinition (coefficients, parameter, optimization);
	}

	/**
	 * use embedded coefficients form
	 * @param coefficients the coefficients array
	 * @param parameter the name of the function parameter
	 * @param optimization the associated optimization object
	 * @return a buffer with the optimized text
	 */
	public StringBuffer getEmbeddedDefinition
		(
			Polynomial.Coefficients<T> coefficients,
			String parameter, Optimization optimization
		)
	{
		StringBuffer definition =
			new StringBuffer (coefficients.toString ());
		definition.append (optimization.getOptimizedOp ());
		definition.append (parameter);
		return definition;
	}


	/*
	 * process forms taken from expression tree nodes
	 */

	/**
	 * optimization based on expression tree as source
	 * @param expression use expression to generate function
	 * @param original the abstract function definition
	 * @return the optimized version
	 */
	public AbstractFunction <T> getOptimizedFunctionFrom
		(Expression<T> expression, AbstractFunction <T> original)
	{
		SemanticAnalysis.BinaryOperatorNode <T>
			binary = verifyRoot (expression.get (0));						// root must be binary operation
		Optimization optimization = getOptimizationFor (binary);			// check operator in map
		return getOptimizedFunctionFrom (original, binary, optimization);	// process node
	}
	AbstractFunction <T> getOptimizedFunctionFrom
		(AbstractFunction <T> original, SemanticAnalysis.BinaryOperatorNode <T> node, Optimization optimization)
	{
		String parameter;
		verifyParameter (node.getRightOperand (), parameter = original.getParameterNameList ().getSingletonParameterName ());
		return getOptimizedFunctionFrom (original.getName (), parameter, optimization, utilities.getCoefficientsFrom (node.getLeftOperand ()));
	}
	@SuppressWarnings ("unchecked") SemanticAnalysis.BinaryOperatorNode <T> verifyRoot (Element element)
	{
		if ( ! (element instanceof SemanticAnalysis.BinaryOperatorNode) )
		{ throw new RuntimeException ("Expression must refer to polynomial operation"); }
		return (SemanticAnalysis.BinaryOperatorNode <T>) element;
	}
	void verifyParameter (Element element, String parameter)
	{
		switch (element.getElementType ())
		{
			case IDENTIFIER:
				if (CoefficientUtilities.identifierMatches (element, parameter)) return;
			default: throw new RuntimeException ("Expression parameter reference error");
		}
	}
	Optimization getOptimizationFor (SemanticAnalysis.BinaryOperatorNode <T> operation)
	{ return getOptimizationAssociatedWith (operation.getopName ()); }


	/*
	 * process forms taken from token streams
	 */

	/**
	 * optimization based on token stream definition as source
	 * @param original the abstract function definition
	 * @return the optimized version
	 */
	public AbstractFunction <T> getOptimizedFunctionFromTokens (AbstractFunction <T> original)
	{
		String parameter =
			original.getParameterNameList ().getSingletonParameterName ();
		DeclarationSupport.TokenStream tokens = checkTokens (original, parameter);
		Optimization optimization = getOptimizationAssociatedWith (tokens.get (1).getTokenImage ());
		return getOptimizedFunctionFrom (original.getName (), parameter, optimization, utilities.getCoefficients (tokens));
	}

	/**
	 * check the function definition token sequence pattern
	 * @param function the description of the function being optimized
	 * @param parameter the formal parameter used in the definition
	 * @return the tokens from the function definition
	 */
	public DeclarationSupport.TokenStream checkTokens (AbstractFunction <T> function, String parameter)
	{
		DeclarationSupport.TokenStream tokens = new DeclarationSupport.TokenStream (function.getFunctionTokens ());
		if ( ! patternMatch (tokens, parameter) ) throw new RuntimeException (errorFor (tokens, parameter));
		return tokens;
	}
	String errorFor (DeclarationSupport.TokenStream tokens, String parameter)
	{ return "Function form not recognized, size = " + tokens.size () + ", parameter = " + parameter; }
	boolean patternMatch (DeclarationSupport.TokenStream tokens, String parameter)
	{ return tokens.size () == 3 && tokens.get (2).matches (parameter); }


	/*
	 * common translation from optimization to function wrapper
	 */

	/**
	 * wrap function for export
	 * @param name the name of the function
	 * @param parameter the parameter to the function
	 * @param optimization the associated optimization object
	 * @param coefficients the coefficients of the polynomial
	 * @return the abstract description of the function
	 */
	public AbstractFunction <T> getOptimizedFunctionFrom
	(String name, String parameter, Optimization optimization, Polynomial.Coefficients <T> coefficients)
	{
		return new FunctionWrapper <T>
		(
			name, parameter,
			optimization.descriptionTokens (),
			optimization.getOptimizedFunction (coefficients)
		);
	}


	/*
	 * segment processing steps for restore operations
	 */

	/**
	 * build raw segment for restore
	 * @param name the name of the function
	 * @param parameter the parameter to the function
	 * @param segmentClass the name of the class implementing the function
	 * @param coefficients the coefficients of the polynomial
	 * @return the abstract description of the function
	 */
	public AbstractFunction <T> getOptimizedFunctionFrom
	(String name, String parameter, String segmentClass, Polynomial.Coefficients <T> coefficients)
	{ return getOptimizedFunctionFrom (name, parameter, getOptimizationFor (segmentClass), coefficients); }

	/**
	 * get appropriate optimization object for restore
	 * @param segmentClass the name of the class being restored
	 * @return the appropriate optimization object
	 */
	public Optimization getOptimizationFor (String segmentClass)
	{
		switch (OptimizedTypes.valueOf (segmentClass))
		{

			case OrdinarySegment: return new PolynomialOptimization ();
			case ChebyshevSegment: return new ChebyshevOptimization ();

			default:
				{
					throw new RuntimeException ("Invalid class for optimized polynomial");
				}
		}
	}
	public enum OptimizedTypes {OrdinarySegment, ChebyshevSegment}


}

