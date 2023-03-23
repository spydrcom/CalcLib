
package net.myorb.math.polynomial.algebra;

import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.symbols.DefinedFunction;
import net.myorb.math.expressions.commands.CommandSequence;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.notations.json.JsonPrettyPrinter;

/**
 * command implementation for Series Expansion algorithm
 * @author Michael Druckman
 */
public class SeriesExpansion <T> extends ParameterManagement
{


	public SeriesExpansion
		(Environment <T> environment) { this.environment = environment; }
	protected Environment <T> environment;


	/**
	 * produce expanded version of function sequence
	 * @param functionName the name of the function in the symbol table
	 * @return the expanded sequence
	 */
	public CommandSequence expandSequence (String functionName)
	{
		return new CommandSequence
		(
			TokenParser.parse
			(
				expandedDescription
				(
					functionName
				)
			)
		);
	}


	/**
	 * buffer the text of expanded version of equation
	 * @param functionName the name of the function in the symbol table
	 * @return buffer holding text of expanded equation
	 */
	public StringBuffer expandedDescription (String functionName)
	{
		Elements.Factor expanded =
			reducedForm ( performExpansion (functionName) );
		if (showFunctionExpanded) System.out.println (expanded);
		return new StringBuffer ( expanded.toString () );
	}


	/**
	 * perform expansion of named polynomial
	 * @param functionName the function name given to the polynomial
	 * @return the expanded equation
	 */
	public Elements.Factor performExpansion (String functionName)
	{
		return RepresentationConversions.organizeTerms
		(
			expandSymbol (functionName, null, this)
		);
	}


	/**
	 * construct element tree for a polynomial in the symbol table
	 * @param functionName the name of the function expected to be a polynomial
	 * @param parameter the description of the parameter used in the symbol reference
	 * @param root the expansion object for this processing request
	 * @return the root Factor node for describing this symbol
	 */
	public Elements.Factor expandSymbol
		(String functionName, Elements.Factor parameter, SeriesExpansion <?> root)
	{
		String formalParameter = getPolynomialVariable ();
		JsonValue jsonTree = getJsonDescription (functionName);
		if (parameter != null) prepareParameterSubstitution (parameter);
		Elements.Factor result = RepresentationConversions.translate ( trace (jsonTree), root );
		setPolynomialVariable (formalParameter);
		return result;
	}


	/**
	 * process the profile of a function
	 * @param functionName the name of the function
	 * @return the expression tree found linked to the Subroutine
	 */
	public JsonValue getJsonDescription (String functionName)
	{
		Subroutine <T> udf = null;
		try { udf = DefinedFunction.asUDF ( lookup (functionName) ); }
		catch (Exception e) { error ( functionName + " not recognized", e ); }
		setPolynomialVariable ( udf.getParameterNames () );
		return getExpressionTree ( udf );
	}


	/**
	 * read expression tree from posted Subroutine
	 * @param symbol the Subroutine object found in the symbol table
	 * @return the expression tree found linked to the Subroutine
	 */
	public JsonValue getExpressionTree (Subroutine <T> symbol)
	{
		JsonValue root = null;
		try { root = symbol.getExpressionTree (); }
		catch (Exception e) { error ( "Unable to build expression tree", e ); }
		return root;
	}


	/**
	 * format a trace message for an expression tree
	 * @param jsonTree the JSON root node to be traced
	 * @return the node passed as parameter for chaining
	 */
	public JsonValue trace (JsonValue jsonTree)
	{
		if (showFunctionJson)
		{
			try { JsonPrettyPrinter.sendTo ( jsonTree, System.out ); }
			catch (Exception e) { error ( "JSON trace formatter failed", e ); }
		}
		return jsonTree;
	}


	/**
	 * find function in the symbol table
	 * @param functionName the name of the function
	 * @return the symbol if found otherwise null
	 */
	protected Object lookup (String functionName) { return environment.getSymbolMap ().get (functionName); }
	protected boolean showFunctionJson = false, showFunctionExpanded = false;


}

