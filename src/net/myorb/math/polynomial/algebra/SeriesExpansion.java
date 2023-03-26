
package net.myorb.math.polynomial.algebra;

import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.symbols.DefinedFunction;
import net.myorb.math.expressions.commands.CommandSequence;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.notations.json.JsonPrettyPrinter;

import net.myorb.data.abstractions.CommonCommandParser;
import net.myorb.data.abstractions.ConfigurationParser;

/**
 * command implementation for Series Expansion algorithm
 * @author Michael Druckman
 */
public class SeriesExpansion <T> extends ParameterManagement
	implements ConfigurationParser.Interpreter
{


	public SeriesExpansion
		(Environment <T> environment) { this.environment = environment; }
	protected Environment <T> environment;


	// expansion driver


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
		expandedRoot = reducedForm ( performExpansion (functionName) );
		if (showFunctionExpanded) System.out.println (expandedRoot);
		return new StringBuffer ( expandedRoot.toString () );
	}
	protected Elements.Factor expandedRoot;


	/**
	 * perform expansion of named polynomial
	 * @param functionName the function name given to the polynomial
	 * @return the expanded equation
	 */
	public Elements.Factor performExpansion (String functionName)
	{
		Subroutine <T> profile;
		( profile = getProfile (functionName) ).setSeries (this);
		return RepresentationConversions.organizeTerms ( expandSymbol ( profile ) );
	}


	/**
	 * construct element tree from a profile description
	 * @param profile the profile of a UDF in the symbol table
	 * @return the root Factor node for describing this symbol
	 */
	public Elements.Factor expandSymbol (Subroutine <T> profile)
	{
		return expandSymbol ( getExpressionTreeFrom (profile), this );
	}


	// symbol table queries and expression tree links


	/**
	 * get profile for function
	 * @param functionName the name of the function
	 * @return the profile object or null for error
	 */
	public Subroutine <T> getProfile (String functionName)
	{
		try { return DefinedFunction.asUDF ( lookup (functionName) ); }
		catch (Exception e) { error ( functionName + " not recognized", e ); }
		return null;
	}


	/**
	 * get the expression tree linked to a profile
	 * @param profile the profile of a UDF in the symbol table
	 * @return the expression tree found linked to the profile
	 */
	public JsonValue getExpressionTreeFrom (Subroutine <T> profile)
	{
		setPolynomialVariable ( profile.getParameterNames () );
		return getExpressionTree ( profile );
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
	 * process the profile of a function
	 * @param functionName the name of the function
	 * @param parameter the description of the parameter used in the symbol reference
	 * @return the expression tree found linked to the Subroutine
	 */
	public JsonValue getExpressionTreeFrom (String functionName, Elements.Factor parameter)
	{
		JsonValue tree = getExpressionTreeFrom ( getProfile (functionName) );
		prepareParameterSubstitution (parameter);
		return tree;
	}


	// expansion algorithm layers


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
		JsonValue expressionTree = getExpressionTreeFrom (functionName, parameter);
		Elements.Factor result = expandSymbol ( expressionTree, root );
		setPolynomialVariable (formalParameter);
		return result;
	}


	/**
	 * construct element tree for a polynomial in the symbol table
	 * @param expressionTree the expression tree found linked to the Subroutine
	 * @param root the expansion object for this processing request
	 * @return the root Factor node for describing this symbol
	 */
	public Elements.Factor expandSymbol
	(JsonValue expressionTree, SeriesExpansion <?> root)
	{
		return RepresentationConversions.translate ( trace (expressionTree), root );
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
	 * link to analysis of the equation done by Powers in Manipulations
	 * @param analysis the Powers analysis of the equation
	 */
	public void linkAnalysis
	(Manipulations.Powers analysis) { this.analysis = analysis; }
	protected Manipulations.Powers analysis;


	/**
	 * show analysis of equation
	 * @param functionName name of the function
	 */
	public void showAnalysis (String functionName)
	{
		java.io.PrintStream stream = environment.getOutStream ();

		stream.println (); stream.println ("===");
		stream.println (functionName); stream.println ("===");

		for (Double power : analysis.getPowers ())
		{
			stream.print (Constant.asInteger (power.toString ()));
			stream.print ("\t"); stream.print (analysis.getTermFor (power));
			stream.println ();
		}

		stream.println ("===");
		stream.println ();
	}


	/**
	 * find function in the symbol table
	 * @param functionName the name of the function
	 * @return the symbol if found otherwise null
	 */
	protected Object lookup (String functionName) { return environment.getSymbolMap ().get (functionName); }
	protected boolean showFunctionJson = false, showFunctionExpanded = false;


	// solve coefficient equations


	/**
	 * find simultaneous equation solution 
	 *  for coefficients of expanded polynomial series
	 * @param sourceFunctionName name of an expanded series function
	 * @param solutionFunctionName name for solution being built
	 * @param tokens parameters listed on command line
	 * @param position the starting parameter
	 */
	public void solve
		(
			String sourceFunctionName, String solutionFunctionName,
			CommandSequence tokens, int position
		)
	{
		Subroutine <T> profile =
				getProfile (sourceFunctionName);
		SeriesExpansion <T> linkedSeries = profile.getSeries ();
		if (linkedSeries == null) throw new RuntimeException ("No linked series");
		parse ( tokens, position ); linkedSeries.showAnalysis ( sourceFunctionName );
		new Solution <T> (environment).analyze (linkedSeries, profile, symbolTable);
	}

	/**
	 * process symbol specifications in command
	 * @param tokens the text from the command line
	 * @param position the starting position to use
	 */
	void parse (CommandSequence tokens, int position)
	{
		for (int i = position; i > 0; i--) tokens.remove (i - 1);
		ConfigurationParser.process (tokens, this);
	}
	protected Solution.SymbolValues symbolTable = new Solution.SymbolValues ();

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ConfigurationParser.Interpreter#process(java.lang.String, net.myorb.data.abstractions.CommonCommandParser.TokenDescriptor)
	 */
	public void process (String symbol, CommonCommandParser.TokenDescriptor token)
	{
		symbolTable.add (symbol, token.getTokenImage ());
	}


}

