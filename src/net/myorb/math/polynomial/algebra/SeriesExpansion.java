
package net.myorb.math.polynomial.algebra;

import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.symbols.DefinedFunction;
import net.myorb.math.expressions.commands.CommandSequence;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;

import net.myorb.data.notations.json.JsonPrettyPrinter;

import java.util.ArrayList;

/**
 * command implementation for Series Expansion algorithm
 * @author Michael Druckman
 */
public class SeriesExpansion <T>
{


	public SeriesExpansion (Environment <T> environment)
	{ this.environment = environment; }
	Environment <T> environment;


	/**
	 * get and set variable name
	 * @param polynomialVariable the name of the parameter in the profile
	 */
	public void setPolynomialVariable
	(String polynomialVariable) { this.polynomialVariable =  polynomialVariable; }
	public String getPolynomialVariable () { return polynomialVariable; }
	String polynomialVariable = null;


	/**
	 * verify polynomial variable description from profile
	 * @param profile the Subroutine ParameterList description
	 */
	public void setPolynomialVariable (Subroutine.ParameterList profile)
	{
		setPolynomialVariable (profile.get (0));
	}

	/**
	 * verify polynomial variable description
	 * @return the parameter read from the function profile
	 */
	public ArrayList <String> parameterList ()
	{
		ArrayList <String>
			parameterNameList = new ArrayList <> ();
		try { parameterNameList.add ( getPolynomialVariable () ); }
		catch (Exception e) { error ( "Error in function profile", e ); }
		return parameterNameList;
	}


	/**
	 * produce expanded version of function sequence
	 * @param functionName the name of the function in the symbol table
	 * @param tokens the command tokens specified on the request
	 * @param tokenPosition the position of the next token
	 * @return the expanded sequence
	 */
	public CommandSequence expandSequence
	(String functionName, CommandSequence tokens, int tokenPosition)
	{
		return new CommandSequence
		(
			TokenParser.parse
			(
				expandedDescription
				(
					functionName, tokens, tokenPosition
				)
			)
		);
	}


	/**
	 * buffer the text of expanded version of equation
	 * @param functionName the name of the function in the symbol table
	 * @param tokens the command tokens specified on the request
	 * @param tokenPosition the position of the next token
	 * @return buffer holding text of expanded equation
	 */
	public StringBuffer expandedDescription
	(String functionName, CommandSequence tokens, int tokenPosition)
	{
		Elements.Factor expanded =
			performExpansion (functionName, tokens, tokenPosition);
		if (showFunctionExpanded) System.out.println (expanded);
		return new StringBuffer (expanded.toString ());
	}


	/**
	 * perform expansion of named polynomial
	 * @param functionName the function name given to the polynomial
	 * @param tokens the command tokens specified on the request
	 * @param tokenPosition the position of the next token
	 * @return the expanded equation
	 */
	public Elements.Factor performExpansion
	(String functionName, CommandSequence tokens, int tokenPosition)
	{
		return RepresentationConversions.organizeTerms
		(
			expandSymbol (functionName, this)
		);
	}


	/**
	 * construct element tree for a polynomial in the symbol table
	 * @param functionName the name of the function expected to be a polynomial
	 * @param root the expansion object for this processing request
	 * @return the root Factor node for describing this symbol
	 */
	public Elements.Factor expandSymbol (String functionName, SeriesExpansion <?> root)
	{
		JsonValue jsonTree = getJsonDescription (functionName);
		return RepresentationConversions.translate ( trace (jsonTree), root );
	}
	JsonValue getJsonDescription (String functionName)
	{
		Subroutine <T> udf = null;
		try { udf = DefinedFunction.asUDF ( lookup (functionName) ); }
		catch (Exception e) { error ( functionName + " not recognized", e ); }
		setPolynomialVariable (udf.getParameterNames ());
		return getExpressionTree (udf);
	}
	JsonValue getExpressionTree (Subroutine <T> s)
	{
		JsonValue root = null;
		try { root = s.getExpressionTree (); }
		catch (Exception e) { error ( "Unable to build expression tree", e ); }
		return root;
	}
	JsonValue trace (JsonValue jsonTree)
	{
		if (showFunctionJson)
		{
			try { JsonPrettyPrinter.sendTo ( jsonTree, System.out ); }
			catch (Exception e) { error ( "JSON trace formatter failed", e ); }
		}
		return jsonTree;
	}
	Object lookup (String functionName) { return environment.getSymbolMap ().get (functionName); }
	void error (String message, Exception source) { throw new RuntimeException (message, source); }
	boolean showFunctionJson = false, showFunctionExpanded = false;


}

