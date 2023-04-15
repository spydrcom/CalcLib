
package net.myorb.math.polynomial.algebra;

import net.myorb.math.polynomial.families.*;
import net.myorb.math.polynomial.InitialConditionsProcessor;

import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.expressions.symbols.DefinedFunction;
import net.myorb.math.expressions.commands.CommandSequence;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.computational.ArithmeticFundamentals;

import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.notations.json.JsonPrettyPrinter;

import net.myorb.data.abstractions.CommonCommandParser;
import net.myorb.data.abstractions.ConfigurationParser;

/**
 * command implementation for Series Expansion algorithm
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class SeriesExpansion <T> extends ParameterManagement
	implements ConfigurationParser.Interpreter
{


	public SeriesExpansion (Environment <T> environment)
	{
		this.establishSolutions ( this.environment = environment );
		this.symbolManager = new SymbolicReferenceManager <T> ( environment );
		this.setGeneratedSolutions ( new Solution.LinkedSolutions () );
	}
	protected SymbolicReferenceDetails <T> symbolManager;
	protected Environment <T> environment;


	/**
	 * post a function based on a polynomial expansion
	 * @param sourceFunctionName the function to expand
	 * @param newFunctionName the function to post
	 */
	public void declareExpandedSource
		(
			String sourceFunctionName, String newFunctionName
		)
	{
		this.setSolutionBeingBuilt (newFunctionName);
		CommandSequence seq = expandSequence ( sourceFunctionName );

		DefinedFunction.defineUserFunction
		(
			newFunctionName, parameterList (), seq, environment
		);
	}


	/**
	 * identify a solution series
	 * @return name assigned to the solution
	 */
	public String getSolutionBeingBuilt () { return solutionBeingBuilt; }
	public void setSolutionBeingBuilt (String solutionBeingBuilt)
	{ this.solutionBeingBuilt = solutionBeingBuilt; }
	protected String solutionBeingBuilt;


	/**
	 * link solutions to the generating equation
	 * @param generatedSolutions the map object for solution links
	 */
	public void setGeneratedSolutions
	(Solution.LinkedSolutions generatedSolutions) { this.generatedSolutions = generatedSolutions; }
	public Solution.LinkedSolutions getGeneratedSolutions () { return generatedSolutions; }
	public Solution <T> getSolution () {  return solution;  }
	protected Solution.LinkedSolutions generatedSolutions;


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
		this.setFunctionName (functionName);
		Elements.Factor expandedRoot = reducedForm ( performExpansion (functionName) );
		if (showFunctionExpanded) System.out.println (expandedRoot);
		return new StringBuffer ( expandedRoot.toString () );
	}


	/**
	 * perform expansion of named polynomial
	 * @param functionName the function name given to the polynomial
	 * @return the expanded equation
	 */
	public Elements.Factor performExpansion (String functionName)
	{
		this.profile = symbolManager.getProfile (functionName);
		Factor tree = RepresentationConversions.organizeTerms ( expandSymbol ( profile ) );
		this.profile.setSeries (this, tree);
		return tree;
	}
	protected SymbolicReferenceDetails.FunctionProfile <T> profile;


	/**
	 * construct element tree from a profile description
	 * @param profile the profile of a UDF in the symbol table
	 * @return the root Factor node for describing this symbol
	 */
	public Elements.Factor expandSymbol (SymbolicReferenceDetails.FunctionProfile <T> profile)
	{
		return expandSymbol ( getExpressionTreeFrom (profile), this );
	}


	// symbol table queries and expression tree links


	/**
	 * @return the name of the core expanded function
	 */
	public String getFunctionName () { return functionName; }
	public void setFunctionName (String functionName)
	{ this.functionName = functionName; }
	protected String functionName;


	/**
	 * get the expression tree linked to a profile
	 * @param profile the profile of a UDF in the symbol table
	 * @return the expression tree found linked to the profile
	 */
	public JsonValue getExpressionTreeFrom (SymbolicReferenceDetails.FunctionProfile <T> profile)
	{
		setPolynomialVariable ( profile.getParameterName () );
		return getExpressionTree ( profile );
	}


	/**
	 * read expression tree from posted Subroutine
	 * @param symbol the Subroutine object found in the symbol table
	 * @return the expression tree found linked to the Subroutine
	 */
	public JsonValue getExpressionTree (SymbolicReferenceDetails.FunctionProfile <T> symbol)
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
	public JsonValue getExpressionTreeFrom
		(String functionName, Elements.Factor parameter)
	{
		JsonValue tree = getExpressionTreeFrom
			( symbolManager.getProfile (functionName) );
		prepareParameterSubstitution (parameter);
		return tree;
	}


	//  element tree link


	public Factor getAssociatedElementTree () { return associatedElementTree; }
	public void setAssociatedElementTree (Factor associatedElementTree)
	{ this.associatedElementTree = associatedElementTree; }
	protected Factor associatedElementTree = null;


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
	protected boolean showFunctionJson = false, showFunctionExpanded = false;


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

		for (Integer power : analysis.getPowers ())
		{
			stream.print (power.toString ());
			stream.print ("\t"); stream.print (analysis.getTermFor (power));
			stream.println ();
		}

		stream.println ("===");
		stream.println ();
	}


	// solve coefficient equations


	/**
	 * find simultaneous equation solution
	 *  for coefficients of expanded polynomial series
	 * @param expandedFunctionName name of an expanded series function
	 * @param sourceName name of the polynomial solution to analyze
	 * @param solutionFunction name for solution being built
	 * @param tokens parameters listed on command line
	 * @param position the starting parameter
	 */
	public void solve
		(
			String expandedFunctionName,
			String sourceName, String solutionFunction,
			CommandSequence tokens, int position
		)
	{
		SeriesExpansion <T> expandedSeries =
				seriesFor ( expandedFunctionName );
		expandedSeries.setSolutionBeingBuilt ( solutionFunction );
		expandedSeries.generatedSolutions.put (solutionFunction, this.solution);
		this.parse ( tokens, position ); expandedSeries.showAnalysis ( expandedFunctionName );
		this.solution.analyze ( expandedSeries, this.symbolTable );
		this.describeSolution ( solutionFunction,  sourceName );
	}


	/**
	 * post solution to symbol table
	 * @param solutionFunctionName the name requested for the solution
	 * @param sourceFunction name of the polynomial solution to analyze
	 */
	public void describeSolution
		(String solutionFunctionName, String sourceFunction)
	{
		SymbolicReferenceDetails.FunctionProfile <T>
			sourceProfile = symbolManager.getProfile ( sourceFunction );
		StringBuffer display = new StringBuffer (solutionFunctionName).append (" = ");
		display.append ( formattedVector ( solutionFunctionName, sourceProfile ) );
		this.environment.getOutStream ().println ( display );
		this.trace ();
	}
	String formattedVector
		(
			String solutionFunctionName,
			SymbolicReferenceDetails.FunctionProfile <T> sourceProfile
		)
	{
		return this.symbolManager.post
		(
			this.solution.getCoefficientsVector
				( getCoefficientsFrom ( sourceProfile.getSeriesRoot () ) ),
			solutionFunctionName
		).toString ();
	}
	void trace () { if ( SHOW ) this.solution.showCollectedSolutionTableContent (); }
	protected boolean SHOW = false;


	/**
	 * get series linked to function symbol
	 * @param functionName name of the function to locate
	 * @return the series expansion linked to symbol
	 */
	public SeriesExpansion <T> seriesFor (String functionName)
	{
		SeriesExpansion <T> linkedSeries =
			symbolManager.getProfile (functionName).getSeries ();
		errorForNull ( linkedSeries, "No linked series" );
		return linkedSeries;
	}


	// command line token parser


	/**
	 * process symbol specifications in command
	 * @param tokens the text from the command line
	 * @param position the starting position to use
	 */
	public void parse (CommandSequence tokens, int position)
	{
		for (int i = position; i > 0; i--) tokens.remove (i - 1);
		ConfigurationParser.process (tokens, this);
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ConfigurationParser.Interpreter#process(java.lang.String, net.myorb.data.abstractions.CommonCommandParser.TokenDescriptor)
	 */
	public void process (String symbol, CommonCommandParser.TokenDescriptor token)
	{ symbolTable.add ( symbol, CommonCommandParser.unQuoted (token) ); }


	// SHOSOL command implementation


	/**
	 * show solutions
	 * @param tokens the command line source
	 */
	public void showSolutions (CommandSequence tokens)
	{
		SolutionReports <T> reports = new SolutionReports <T> ( environment );
		String equationName = getEquationRequest ( tokens ), solutionName = getSolutionRequest ( tokens );
		Solution.LinkedSolutions solutions = symbolManager.getLinkedSolutions ( equationName );

		if (solutionName == null)
		{ reports.showSolutions ( SolutionReports.fromSolutionSet (solutions) ); }
		else reports.showSolution ( solutionName, solutions );
	}

	/**
	 * equation name required
	 * @param tokens the command line source
	 * @return name taken from token
	 */
	public static String getEquationRequest (CommandSequence tokens)
	{
		String equationName;
		errorForNull ( equationName = getSolutionRequest (tokens, 1), "Equation name required" );
		return equationName;
	}

	/**
	 * solution name optional
	 * @param tokens the command line source
	 * @return name taken from token or null if not specified
	 */
	public static String getSolutionRequest (CommandSequence tokens)
	{ return getSolutionRequest (tokens, 2); }

	/**
	 * check for operand specified in command
	 * @param tokens the token supplied in the command
	 * @param operand the index of the operand to check
	 * @return operand if present otherwise null
	 */
	public static String getSolutionRequest
			(CommandSequence tokens, int operand)
	{
		if ( tokens.size () < operand+1 ) return null;
		else return tokens.get ( operand ).getTokenImage ();
	}


	// general initialization


	/**
	 * initialize helper objects for solutions
	 * @param environment the application environment
	 */
	public void establishSolutions (Environment <T> environment)
	{
		this.converter = ArithmeticFundamentals.getConverter ( environment.getSpaceManager () );
		this.symbolTable = new Solution.SymbolValues (this.converter);
		this.solution = new Solution <T> ( environment );
	}
	protected Solution.SymbolValues symbolTable;
	protected Solution <T> solution;


	/**
	 * @param nodeText the text from the node image
	 * @return a parsed constant
	 */
	public Constant getConstantFromNodeImage ( String nodeText )
	{ return new Constant ( converter, converter.fromText (nodeText) ); }
	public ArithmeticFundamentals.Conversions <?> getConverter () { return converter; }
	protected ArithmeticFundamentals.Conversions <?> converter;


	// initialization of Initial Conditions Processors library


	/**
	 * initialize management for Initial Conditions processors
	 * @param manager space manager for data type
	 * @param <T> data type used
	 */
	public static <T> void addInitialConditionsProcessors (ExpressionSpaceManager <T> manager)
	{
		String expectedFamily = LaguerrePolynomial.getFamilyName ();
		if ( InitialConditionsProcessor.hasProcessorFor (expectedFamily) ) return;
		InitialConditionsProcessor.addProcessor (new LaguerrePolynomial <T> (manager));
		InitialConditionsProcessor.addProcessor (new LegendrePolynomial <T> (manager));
	}


}

