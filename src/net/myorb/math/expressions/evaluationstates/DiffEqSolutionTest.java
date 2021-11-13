
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.commands.CommandSequence;
import net.myorb.math.expressions.evaluationstates.DeclarationSupport.TokenStream;
import net.myorb.math.expressions.TokenParser;

import java.util.ArrayList;
import java.util.List;

/**
 * preparation steps for running differential equation solution tests
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class DiffEqSolutionTest<T>
{


	/**
	 * @param definitions call back to define functions
	 */
	public DiffEqSolutionTest (FunctionDefinition<T> definitions)
	{
		this.definitions = definitions;
	}
	FunctionDefinition<T> definitions;


	/**
	 * @param named name of function
	 * @param body tokens in body of function
	 */
	public void defineFunction (String named, TokenStream body)
	{
		definitions.defineFunction (named, parameterNameList, body);
	}
	public void defineFunction (String named, StringBuffer body)
	{ defineFunction (named, new TokenStream (TokenParser.parse (body))); }
	public void defineFunction (String named, String body)
	{ defineFunction (named, new StringBuffer (body)); }


	/**
	 * @param to name of function to call
	 * @param parameter the parameter to the function
	 * @return the formatted text in buffer
	 */
	public StringBuffer formatCall (String to, String parameter)
	{ return new StringBuffer (to).append (" ( ").append (parameter).append (" ) "); }


	/**
	 * set description of parameter
	 */
	public void prepareParameters ()
	{
		parameterNameList = new ArrayList<String>();
		parameterNameList.add (parameterName);
	}
	List<String> parameterNameList;
	String parameterName = "x";


	/**
	 * translate tokens to equation elements
	 * @param tokens the command tokens
	 */
	public void prepareCoefficients (CommandSequence tokens)
	{
		prepareParameters ();
		alias = tokens.get (2).getTokenImage ();
		polynomialName = tokens.get (1).getTokenImage ();
	}
	public StringBuffer getPoly () { return new StringBuffer (polynomialName); }
	String polynomialName;


	/**
	 * prepare alias settings for polynomial solution test
	 * @param tokens the command tokens
	 */
	public void preparePolynomialSolutionTest
		(CommandSequence tokens)
	{
		prepareCoefficients (tokens);
		defineFunction (alias, getPoly ().append (POLYOP).append (parameterName));
		defineFunction (alias+PRIME, getPoly ().append (POLYPOP).append (parameterName));
		defineFunction (alias+DPRIME, getPoly ().append (POLYDPOP).append (parameterName));
	}
	public static final String
	POLYOP = OperatorNomenclature.POLY_EVAL_OPERATOR, POLYPOP = OperatorNomenclature.POLY_PRIME_OPERATOR,
	POLYDPOP = OperatorNomenclature.POLY_DPRIME_OPERATOR;


	/**
	 * translate tokens to equation elements
	 * @param tokens the command tokens
	 */
	public void prepareProfile (CommandSequence tokens)
	{
		prepareParameters ();
		alias = tokens.get (2).getTokenImage ();
		functionName = tokens.get (1).getTokenImage ();
	}
	String functionName;
	String alias;


	/**
	 * prepare alias settings for function solution test.
	 *  derivatives are approximated with rise-over-run evaluations
	 * @param tokens the command tokens
	 */
	public void prepareFunctionSolutionTest
		(CommandSequence tokens)
	{
		prepareProfile (tokens);
		String delta = tokens.get (3).getTokenImage ();
		defineFunction (alias, formatCall (functionName, parameterName));
		defineDerivativeApproximation (alias, DPRIME, delta);
		defineDerivativeApproximation (alias, PRIME, delta);
	}
	public static final String PRIME = OperatorNomenclature.PRIME_OPERATOR, DPRIME = OperatorNomenclature.DPRIME_OPERATOR;


	/**
	 * format derivative approximation reference
	 * @param name the name of the function to be evaluated
	 * @param prime the PRIME or DPRIME operator for first/second derivative
	 * @param delta the value of RUN in the approximation
	 */
	public void defineDerivativeApproximation (String name, String prime, String delta)
	{
		StringBuffer derivativeName =
			new StringBuffer (name).append (prime);
		StringBuffer body = new StringBuffer (derivativeName)
			.append (" ( ").append (parameterName).append (" <> ").append (delta).append (" )");
		defineFunction (derivativeName.toString (), body);
	}


}

