
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.computational.*;
import net.myorb.math.expressions.symbols.*;
import net.myorb.math.polynomial.PolynomialOptimizer;
import net.myorb.math.polynomial.OrdinaryPolynomialCalculus;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;
import net.myorb.math.polynomial.families.chebyshev.ChebyshevPolynomialCalculus;
import net.myorb.math.polynomial.families.HyperGeometricPolynomial;
import net.myorb.math.expressions.commands.CommandSequence;
import net.myorb.data.abstractions.Function;
import net.myorb.math.expressions.*;
import net.myorb.math.*;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * processing of function definition statements
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class FunctionDefinition<T> extends DeclarationSupport<T>
{


	/**
	 * function definition processing as required by the environment
	 * @param environment the environment object driving the processing
	 */
	public FunctionDefinition (Environment<T> environment) { super (environment); }


	/**
	 * import polynomial power functions
	 * @param tokens the token list being processed
	 */
	public void importFamily (List<TokenParser.TokenDescriptor> tokens)
	{
		String kind = null;
		String name = tokens.get (1).getTokenImage ();
		String count = tokens.get (2).getTokenImage ();

		int tokenCount = tokens.size ();
		if (tokenCount > 3) kind = tokens.get (3).getTokenImage ();
		for (int i=4; i<tokenCount; i++) kind += tokens.get (i).getTokenImage ();

		SpecialFunctionFamilyManager.importFamilyFunctions
		(
			name, kind, Integer.parseInt (count), environment
		);
	}


	/**
	 * create a symbol entry for a library
	 * @param tokens the tokens of the definition string
	 */
	public void defineLibrary (List<TokenParser.TokenDescriptor> tokens)
	{
		tokens.remove (0);
		String libraryName = processName (tokens.get (0));

		tokens.remove (0);
		String classPath = TokenParser.toString (tokens).replace (" ", "");
		SymbolMap.Named library;

		try
		{
			Map<String,Method> methods = getMethodMap (Class.forName (classPath));
			LibraryObject<T> libObj = new LibraryObject<T> (classPath, libraryName, methods);
			libObj.setEnvironment (environment);
			library = libObj;
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Requested library not available");
		}

		environment.getSymbolMap ().add (library);
	}


	/**
	 * @param c class to be mapped
	 * @return a Map of methods from name
	 */
	public static Map<String,Method> getMethodMap (Class<?> c)
	{
		Map<String,Method> methods = new HashMap<String,Method> ();
		for (Method m : c.getMethods ()) { methods.put (m.getName (), m); }
		return methods;
	}


	/**
	 * supply configuration parameters for library
	 * @param tokens the tokens of the configuration string
	 */
	public void configureLibrary (List<TokenParser.TokenDescriptor> tokens)
	{
		String name;
		LibraryObject<T> lib = getLib (name = tokens.get (1).getTokenImage ());
		Map<String, Object> parameters = lib.getParameterization ();
		configure (name, tokens, parameters);
	}
	LibraryObject<T> getLib (String name)
	{
		@SuppressWarnings("unchecked")
		LibraryObject<T> lib = (LibraryObject<T>) environment
			.getSymbolMap ().get (name);
		return lib;
	}
	void configure
		(
			String name,
			List<TokenParser.TokenDescriptor> tokens,
			Map<String, Object> parameters
		)
	{
		int n = 2;
		String sym, val;
		while (n+1 < tokens.size())
		{
			sym = tokens.get (n++).getTokenImage ();
			val = strip (tokens.get (n++).getTokenImage ());
			parameters.put (sym, val);
		}
		System.out.println ("Lib " + name + " config " + parameters);
	}
	String strip (String text) { return TokenParser.stripQuotes (text); }


	/**
	 * supply configuration parameters for library
	 * @param tokens the tokens of the configuration string
	 */
	public void instanceSymbol (List<TokenParser.TokenDescriptor> tokens)
	{
		LibraryObject.newInstance
		(
			tokens.get (1).getTokenImage (),
			tokens.get (2).getTokenImage (),
			environment
		);
	}


	/**
	 * add defined function to symbol table
	 * @param functionName the name of the function
	 * @param parameterNames the list of parameters from the function definition
	 * @param functionTokens the tokens that define the function
	 */
	public void defineFunction
		(
			String functionName,
			List<String> parameterNames,
			TokenStream functionTokens
		)
	{
		AbstractFunction<T> defnition =
			new DefinedFunction<T> (functionName, parameterNames, functionTokens);
		environment.processDefinedFunction (defnition);

		if (environment.isDumpingSet ())
		{
			System.out.println ("Function Defined");
			System.out.println ("  name: " + functionName);
			System.out.println ("  parameterNames: " + parameterNames);
			System.out.println ("  functionTokens: " + functionTokens);
		}
	}
	public void defineFunctions
		(
			String functionName,
			List<String> parameterNames,
			TokenStream functionTokens
		)
	{
		int pos = 0, rem = functionTokens.size();
		for (pos=rem-1; pos>0; pos--)
		{
			if (functionTokens.get (pos).getTokenImage ().startsWith (";"))
			{
				TokenStream derivativeTokens = functionTokens.between (pos+1, rem);
				defineFunction (functionName + "'", parameterNames, derivativeTokens);
				functionTokens = functionTokens.between (0, pos);
				break;
			}
		}
		defineFunction (functionName, parameterNames, functionTokens);
	}


	/**
	 * add defined transform to symbol table
	 * @param functionName the name of the function
	 * @param parameterNames the list of parameters from the function definition
	 * @param functionTokens the tokens that define the function
	 * @param transform the function to execute on reference
	 * @return the function definition
	 */
	public AbstractFunction<T> defineTransform
		(
			String functionName,
			List<String> parameterNames,
			TokenStream functionTokens,
			Function<T> transform
		)
	{
		AbstractFunction<T> defnition =
			new DefinedTransform<T> (functionName, parameterNames, functionTokens, transform);
		environment.processDefinedFunction (defnition);

		if (environment.isDumpingSet ())
		{
			System.out.println ("Transform Defined");
			System.out.println ("  name: " + functionName);
			System.out.println ("  parameterNames: " + parameterNames);
			System.out.println ("  functionTokens: " + functionTokens);
			System.out.println ("===");
		}
		return defnition;
	}


	/**
	 * add imported function to symbol table
	 * @param functionName the name of the function
	 * @param parameterNames the list of parameters from the function definition
	 * @param functionTokens the tokens that define the function
	 */
	public void importFunction
		(
			String functionName,
			List<String> parameterNames,
			TokenStream functionTokens
		)
	{
		AbstractFunction<T> defnition =
			new ImportedFunction<T> (functionName, parameterNames, functionTokens, environment);
		environment.processDefinedFunction (defnition);
	}


	/**
	 * process the segments of a segmented function definition
	 * @param functionName the name of the function being declared
	 * @param parameterNames the list of parameters from the function definition
	 * @param functionTokens the tokens that define the function
	 */
	public void defineSegmentedFunction
		(
			String functionName,
			List<String> parameterNames,
			TokenStream functionTokens
		)
	{
		List<String>
		symbolNames = new ArrayList<String>();
		getSymbolList (functionTokens, symbolNames, 0);
		AbstractFunction<T> definition = new Spline<T>
		(functionName, parameterNames, symbolNames, functionTokens, environment);
		environment.processDefinedFunction (definition);
	}


	/**
	 * verify profile
	 * @param function the function to check
	 * @return the same function object
	 */
	public AbstractFunction <T> check (AbstractFunction <T> function)
	{
		if (function == null) throw new RuntimeException ("Function not found");
		if (function.parameterNames.size () != 1) throw new RuntimeException ("Function profile is wrong");
		return function;
	}


	/**
	 * INLINE version of optimization
	 * @param function a function description
	 * @return the optimized definition for this function
	 */
	public TokenStream optimized (AbstractFunction <T> function)
	{
		StringBuffer definition =
			new PolynomialOptimizer <T> (environment)
				.getOptimizedDefinition (check (function));
		return new TokenStream (TokenParser.parse (definition));
	}


	/**
	 * use Sequence object to optimize function access
	 * @param function the original dot product invocation
	 * @return the replacement optimized function
	 */
	public AbstractFunction <T> getOptimizedFunction (AbstractFunction <T> function)
	{
		return new PolynomialOptimizer <T> (environment).getOptimizedFunction (check (function));
	}


	/**
	 * optimize polynomial function use by embedding coefficients
	 * @param tokens the source tokens from the command
	 */
	public void optimizePolynomial (CommandSequence tokens)
	{
		AbstractFunction <T> function, definition;
		boolean inline = false; int n = 1, size = tokens.size ();
		String next = tokens.get (n++).getTokenImage (), functionName = next;

		if (size > n)
		{
			if ("INLINE".equals (next.toUpperCase ()))
			{ inline = true; functionName = tokens.get (n++).getTokenImage (); }
			if (size > n) functionName += tokens.get (n).getTokenImage ();
		}

		function = AbstractFunction.cast (environment.lookup (functionName));

		if (inline)
		{ definition = new DefinedFunction<T> (functionName, function.parameterNames, optimized (function)); }
		else { definition = getOptimizedFunction (function); }
		environment.processDefinedFunction (definition);
	}


	/**
	 * prepare Differential Equation Solution Test
	 * @param tokens the source tokens from the command
	 */
	public void prepareDiffEqSolutionTest (CommandSequence tokens)
	{
		new DiffEqSolutionTest<T> (this).prepareFunctionSolutionTest (tokens);
	}


	/**
	 * prepare Differential Equation Solution Test for polynomial
	 * @param tokens the source tokens from the command
	 */
	public void preparePolynomialDiffEqSolutionTest (CommandSequence tokens)
	{
		new DiffEqSolutionTest<T> (this).preparePolynomialSolutionTest (tokens);
	}


	/**
	 * processing for all function definition forms.
	 *  !! define a function, !+ import a function, !$ build a segmented function, !% build a transform of a function
	 * @param tokens the token list being parsed
	 */
	public void processFunctionDefinition (List<TokenParser.TokenDescriptor> tokens)
	{
		List<String> parameterNames = new ArrayList<String>();
		String directive = tokens.get (0).getTokenImage (), operator = null,
			functionName = processFunctionDefinitionName (tokens);
		int operatorPosition;
		
		if (directive.charAt (1) == '%')
		{
			operatorPosition =
				getSymbolList (tokens, parameterNames, 2) + 1;
			operator = tokens.get (operatorPosition).getTokenImage ();
		}
		else operatorPosition = getParameterList (tokens, parameterNames);
		TokenStream body = new TokenStream (tokens.subList (operatorPosition + 1, tokens.size ()));

		switch (directive.charAt (1))
		{
			case '!':
				defineFunction (functionName, parameterNames, body);
				break;
			case '$':
				defineSegmentedFunction (functionName, parameterNames, body);
				break;
			case '^':
				enableTransform (functionName, parameterNames, body);
				break;
			case '%':
				transformFunction (functionName, parameterNames, operator, body);
				break;
			case '*':
				declareHGPolynomial (functionName, parameterNames, body);
				break;
			case '+':
				importFunction (functionName, parameterNames, body);
				break;
		}
	}
	public SymbolMap.Named processFunctionDefinition
		(
			String profile, String body
		)
	{
		processFunctionDefinition (parseDeclaration (profile, body));
		String name = processFunctionDefinitionName (parseDeclaration (profile, body));
		return environment.lookup (name);
	}

	/**
	 * @param profile parameter profile of function
	 * @param body the sequence of operations
	 * @return the list of tokens
	 */
	private List<TokenParser.TokenDescriptor>
		parseDeclaration (String profile, String body)
	{ return TokenParser.parse (declarationFrom (profile, body)); }
	private StringBuffer declarationFrom (String profile, String body)
	{
		StringBuffer declaration = new StringBuffer ()
			.append ("!! ").append (profile).append (" = ").append (body);
		//System.out.println (declaration);
		return declaration;
	}

	/**
	 * process a HyperGeometric Polynomial declaration
	 * @param functionName the function name to be assigned
	 * @param parameterNames the list of declared parameter names
	 * @param polynomialParameters tokens describing the polynomial
	 * @throws RuntimeException for various source errors
	 */
	public void declareHGPolynomial
		(
			String functionName,
			List<String> parameterNames,
			TokenStream polynomialParameters
		)
	{
		HGListProcessor processor;
		TokenStream definitionTokens = new TokenStream ();
		ListSupport.processValueList (polynomialParameters, processor = new HGListProcessor ());
		HyperGeometricPolynomial<T> hgp = new HyperGeometricPolynomial<T> (environment.getSpaceManager ());
		Function<T> transform = hgp.generatePolynomialFor (processor.getNumeratorValues (), processor.getDenominatorValues ());
		describeDefinition ("HGPOLY[" + TokenParser.toString (polynomialParameters) + "]", definitionTokens);
		defineTransform (functionName, parameterNames, definitionTokens, transform);
	}

	/**
	 * managed array backed up by list for large numerator or denominator specifications
	 */
	public static class HGHybridListProcessor extends ListSupport.HybridValueProcessor
	{
		public HGHybridListProcessor ()
		{ super (new ListSupport.RawValueProcessorGrownUsingList (ANTICIPATED_LARGEST_LIST_SIZE)); }
		public static final int ANTICIPATED_LARGEST_LIST_SIZE = 10;
	}

	/**
	 * collect values to lists of numerator/denominator values
	 */
	public static class HGListProcessor implements ListSupport.FullTokenProcessor<Double>
	{

		public HGListProcessor ()
		{
			numerator = new HGHybridListProcessor ();
			denominator = new HGHybridListProcessor ();
			current = numerator;
		}
		HGHybridListProcessor numerator, denominator, current;

		public double[] getDenominatorValues () { return denominator.getProcessor ().getValues (); }
		public double[] getNumeratorValues () { return numerator.getProcessor ().getValues (); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.evaluationstates.ListSupport.ItemProcessor#process(java.lang.Object)
		 */
		public void process (Double value) { current.process (value); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.evaluationstates.ListSupport.TokenProcessor#process(net.myorb.math.expressions.TokenParser.TokenType, java.lang.String)
		 */
		public void process (TokenParser.TokenType type, String token)
		{ if (token.equals (":")) current = denominator; }

	}

	/**
	 * enable function as integration transform source
	 * @param functionName the function name to be assigned
	 * @param parameterNames the list of declared parameter names
	 * @param transformTokens tokens describing the transform
	 * @throws RuntimeException for various source errors
	 */
	public void enableTransform
		(
			String functionName,
			List<String> parameterNames,
			TokenStream transformTokens
		)
	throws RuntimeException
	{
		String source;
		TokenStream definitionTokens;
		if (transformTokens.size () != 1)
			throw new RuntimeException ("No transform declaration found");
		Function<T> f = getFunction (source = transformTokens.get (0).getTokenImage ());
		definitionTokens = new TokenStream (); describeDefinition ("{" + source + "}", definitionTokens);
		Function<T> transform = new IntegrationTransformBySubstitution<T> (environment, f);
		defineTransform (functionName, parameterNames, definitionTokens, transform);
	}

	/**
	 * process function transform
	 * @param functionName the function name to be assigned
	 * @param parameterNames the list of declared parameter names
	 * @param operator the operation that specifies the transform
	 * @param transformTokens tokens describing the transform
	 * @throws RuntimeException for various source errors
	 */
	public void transformFunction
		(
			String functionName,
			List<String> parameterNames, String operator,
			TokenStream transformTokens
		)
	throws RuntimeException
	{
		OperatorNomenclature.TRANSFORM_TYPE type =
			OperatorNomenclature.TRANFORM_MAP.get (operator);
		if (type == null) throw new RuntimeException ("Invalid transform operator");

		if (environment.isDumpingSet ())
		{
			System.out.print ("Transform: (");
			System.out.print ("name: " + functionName);
			System.out.print (", operator: " + operator);
			System.out.print (", type: " + type);
			System.out.println (")");
		}
		transformFunction (functionName, parameterNames, transformTokens, type);
	}
	void transformFunction
		(
			String functionName, List<String> parameterNames,
			TokenStream transformTokens, OperatorNomenclature.TRANSFORM_TYPE type
		)
	{
		TokenStream definitionTokens = new TokenStream ();
		Function<T> transform = null;

		switch (type)
		{
			case INTEGRAL:		transform = integral (parameterNames, transformTokens, definitionTokens);		break;
			case DERIVATIVE:	transform = derivative (parameterNames, transformTokens, definitionTokens); 	break;
			case EXPONENTIAL:	transform = exponential (parameterNames, transformTokens, definitionTokens);	break;
			case POLYNOMIAL:	transform = polynomial (parameterNames, transformTokens, definitionTokens); 	break;
			case CHEBYSHEV:		transform = chebyshev (parameterNames, transformTokens, definitionTokens);		break;
			case HARMONIC:		transform = harmonic (parameterNames, transformTokens, definitionTokens);		break;
			case GENERIC:		transform = generic (parameterNames, transformTokens, definitionTokens);		break;
			case DCT:			transform = dct (parameterNames, transformTokens, definitionTokens);			break;
		}

		if (transform == null)
		{
			defineFunctions (functionName, parameterNames, definitionTokens);
		}
		else
		{
			defineTransform (functionName, parameterNames, definitionTokens, transform);
		}
	}

	/**
	 * apply transform as specified in interface
	 * @param parameterNames list of parameter names in transform declaration
	 * @param transformTokens the tokens describing the body of the transform
	 * @param definitionTokens the tokens to display in the function list
	 * @return the transformed function
	 */
	Function<T> generic
		(
			List<String> parameterNames,
			TokenStream transformTokens,
			TokenStream definitionTokens
		)
	{
		String functionName;
		if (transformTokens.size () == 0)
			throw new RuntimeException ("No transform declaration found");
		Function<T> f = getFunction (functionName = transformTokens.get (0).getTokenImage ());
		if (!(f instanceof GenericTransform)) { throw new RuntimeException ("Symbol does not represent a transform"); }
		return ((GenericTransform<T>)f).apply (functionName, parameterNames, transformTokens, definitionTokens);
	}

	/**
	 * apply DCT
	 * @param parameterNames list of parameter names in transform declaration
	 * @param transformTokens the tokens describing the body of the transform
	 * @param definitionTokens the tokens to display in the function list
	 * @return the transformed function
	 */
	Function<T> dct
		(
			List<String> parameterNames,
			TokenStream transformTokens,
			TokenStream definitionTokens
		)
	{
		if (transformTokens.size () == 0)
			throw new RuntimeException ("No transform declaration found");
		throw new RuntimeException ("DCT not implemented"); //TODO: implement
	}

	/**
	 * integrate source function and save integral as transform object
	 * @param parameterNames list of parameter names in transform declaration
	 * @param transformTokens the tokens describing the body of the transform
	 * @param definitionTokens the tokens to display in the function list
	 * @return the transformed function
	 */
	Function<T> integral
		(
			List<String> parameterNames,
			TokenStream transformTokens,
			TokenStream definitionTokens
		)
	{
		String functionName, parameter = getParameter (parameterNames);
		Function<T> f = getFunction (functionName = getToken (transformTokens));
		describeDefinition ("INTEGRAL " + functionName + "(" + parameter + ") delta " + parameter, definitionTokens);
		return calculusFor (f).getFunctionIntegral (f);
	}

	/**
	 * differentiate source function and save derivative as transform object.
	 *  alternate processing for Differentiation Rules being applied function combinations.
	 * @param parameterNames list of parameter names in transform declaration
	 * @param transformTokens the tokens describing the body of the transform
	 * @param definitionTokens the tokens to display in the function list
	 * @return the transformed function
	 */
	Function<T> derivative
		(
			List<String> parameterNames,
			TokenStream transformTokens,
			TokenStream definitionTokens
		)
	{
		if (transformTokens.size() > 1)
		{
			return complexDerivativeExpansion (parameterNames, transformTokens, definitionTokens);
		}
		String functionName, parameter = getParameter (parameterNames);
		Function<T> f = getFunction (functionName = getToken (transformTokens));
		describeDefinition (functionName + "'(" + parameter + ")", definitionTokens);
		return calculusFor (f).getFunctionDerivative (f);
	}
	Function<T> complexDerivativeExpansion
		(
			List<String> parameterNames,
			TokenStream transformTokens,
			TokenStream definitionTokens
		)
	{
		String parameter = getParameter (parameterNames);
		new DifferentiationRules ().transform (parameter, transformTokens, definitionTokens);
		return null;
	}

	/**
	 * describe an ordinary polynomial as a transformed function
	 * @param parameterNames list of parameter names in transform declaration
	 * @param transformTokens the tokens describing the body of the transform
	 * @param definitionTokens the tokens to display in the function list
	 * @return the transformed function
	 */
	Function<T> polynomial
		(
			List<String> parameterNames,
			TokenStream transformTokens,
			TokenStream definitionTokens
		)
	{
		return new OrdinaryPolynomialCalculus<T> (environment.getSpaceManager ()).getPolynomialFunction
		(getSeriesCoefficients (parameterNames, OperatorNomenclature.POLY_EVAL_OPERATOR, transformTokens, definitionTokens));
	}

	/**
	 * describe a Chebyshev polynomial series as a transformed function
	 * @param parameterNames list of parameter names in transform declaration
	 * @param transformTokens the tokens describing the body of the transform
	 * @param definitionTokens the tokens to display in the function list
	 * @return the transformed function
	 */
	Function<T> chebyshev
		(
			List<String> parameterNames,
			TokenStream transformTokens,
			TokenStream definitionTokens
		)
	{
		return new ChebyshevPolynomialCalculus<T> (environment.getSpaceManager ()).getPolynomialFunction
		(getSeriesCoefficients (parameterNames, OperatorNomenclature.CLENSHAW_EVAL_OPERATOR, transformTokens, definitionTokens));
	}

	/**
	 * describe an exponential as a transformed function
	 * @param parameterNames list of parameter names in transform declaration
	 * @param transformTokens the tokens describing the body of the transform
	 * @param definitionTokens the tokens to display in the function list
	 * @return the transformed function
	 */
	Function<T> exponential
		(
			List<String> parameterNames,
			TokenStream transformTokens,
			TokenStream definitionTokens
		)
	{
		ExpressionSpaceManager<T>
		manager = environment.getSpaceManager ();
		PowerLibrary<T> lib = new JavaPowerLibrary <T> (manager); //PowerPrimitives
		return new ExponentialPolynomialCalculus<T> (manager, lib).getPolynomialFunction
		(getSeriesCoefficients (parameterNames, OperatorNomenclature.EXP_EVAL_OPERATOR, transformTokens, definitionTokens));
	}

	/**
	 * describe an harmonic as a transformed function
	 * @param parameterNames list of parameter names in transform declaration
	 * @param transformTokens the tokens describing the body of the transform
	 * @param definitionTokens the tokens to display in the function list
	 * @return the transformed function
	 */
	Function<T> harmonic
		(
			List<String> parameterNames,
			TokenStream transformTokens,
			TokenStream definitionTokens
		)
	{
		SeriesParameters<T> p = new SeriesParameters<T>
		(getSeriesCoefficients (parameterNames, OperatorNomenclature.HAR_EVAL_OPERATOR, transformTokens, definitionTokens));
		// use parameter management object to process parameter list and build function
		return p.process (environment).buildFuntion ();
	}

}


/**
 * parameter manager for harmonic series transform declaration
 * @param <T> type on which operations are to be executed
 */
class SeriesParameters<T>
{

	/**
	 * manager object is constructed starting with the cos coefficients
	 * @param c a coefficients object holding the values for the cos terms of the series
	 */
	SeriesParameters (GeneratingFunctions.Coefficients<T> c) { cos = c; }

	/**
	 * process each value found on the stack
	 * @param environment the control object holding stack, space, and value managers
	 * @return THIS object allows the sequence to transition
	 */
	@SuppressWarnings("rawtypes")
	SeriesParameters<T> process (Environment<T> environment)
	{
		manager = environment.getSpaceManager ();
		ValueStack vs = environment.getValueStack ();
		ValueManager<T> vm = environment.getValueManager ();
		ValueManager.GenericValue v;
		omega = manager.getOne ();

		while (!vs.isEmpty ())
		{
			if (vm.isArray (v = vs.pop ()))
			{
				sin = new GeneratingFunctions.Coefficients<T>();
				sin.addAll (vm.toArray (v));
			}
			else if (vm.isDiscrete (v))
			{
				omega = vm.toDiscreteValue (v).getValue ();
			}
		}

		return this;
	}

	/**
	 * use Fourier series calculus object to construct a function version of object
	 * @return a function object that can evaluate the series
	 */
	Function<T> buildFuntion ()
	{
		return new FourierSeriesCalculus<T>(manager).newFunctionInstance (omega, cos, sin);
	}
	GeneratingFunctions.Coefficients<T> sin, cos;
	ExpressionSpaceManager<T> manager;
	T omega;

}


