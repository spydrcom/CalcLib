
package net.myorb.math.expressions.tree;

import net.myorb.math.polynomial.PolynomialOptimizer;
import net.myorb.math.polynomial.RepresentationTools;

import net.myorb.math.expressions.tree.LexicalAnalysis.ParenthesisNestingError;
import net.myorb.math.expressions.tree.SemanticAnalysis.SemanticError;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.symbols.AbstractFunction;
import net.myorb.math.expressions.symbols.DefinedFunction;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.math.computational.CommonSplineDescription;
import net.myorb.math.GeneratingFunctions.Coefficients;

import net.myorb.data.notations.json.JsonPrettyPrinter;
import net.myorb.data.notations.json.JsonSemantics;
import net.myorb.data.notations.json.JsonReader;

import net.myorb.data.abstractions.SimpleStreamIO;
import net.myorb.data.abstractions.ZipSource;

import java.util.ArrayList;
import java.util.List;

import java.io.PrintStream;
import java.io.File;

/**
 * tree management 
 * @param <T> type of values from expressions
 * @author Michael Druckman
 */
public class Gardener<T>
{


	/**
	 * construct file access to source
	 * @param name the simple filename for the source
	 * @param extension the file type of the source
	 * @return the file access to the source
	 */
	public static File fileCalled (String name, String extension)
	{ return new File (EXPRESSION_DIRECTORY, name + "." + extension); }
	public static final File EXPRESSION_DIRECTORY = new File ("expressions");
	public static final String SINGLE_TREE_EXTENSION = "json", FOREST_EXTENSION = "zip";


	/**
	 * list gardeners participating in multi-tree events
	 * @param <T> data type in trees
	 */
	public static class Associates<T> extends ArrayList<Gardener<T>>
	{
		/**
		 * prepare a report on associated gardeners
		 * @param out a print stream object for display of the report
		 * @param symbols a symbol map for symbol descriptions
		 */
		public void displayTo (PrintStream out, SymbolMap symbols) {}
		private static final long serialVersionUID = -7982883639744936921L;
	}


	/**
	 * formal parameter lists as expected in profile objects
	 */
	public static class FormalList extends Profile.ParameterList
	{ private static final long serialVersionUID = 3262064790279518487L; }


	/**
	 * just the empty object
	 */
	public Gardener () {}

	/**
	 * retain a space manager to use in evaluations
	 * @param environment access to the utility methods
	 */
	public Gardener (Environment<T> environment)
	{
		this.spaceManager = environment.getSpaceManager ();
		this.environment = environment;
	}
	Environment<T> environment;


	/**
	 * get access to space manager
	 * @return a space manager provided for expression evaluations
	 */
	public ExpressionSpaceManager<T> getExpressionSpaceManager ()
	{
		return this.spaceManager;
	}
	protected ExpressionSpaceManager<T> spaceManager;


	/**
	 * build expression tree from source
	 * @param source text of representation of expression
	 * @param symbols the symbol table describing the source references
	 * @throws ParenthesisNestingError for mis-match of parenthesis in source
	 * @throws SemanticError for inconsistent semantics found
	 */
	public void plant
		(
			String source,
			SymbolMap symbols
		)
	throws ParenthesisNestingError, SemanticError
	{
		setTokens (new StringBuffer (source));
		setExpression (LexicalAnalysis.expand (tokens));
		SemanticAnalysis.attributeAndReduce (expression, spaceManager, symbols);
	}


	/**
	 * parse token stream into expression tree
	 * @param tokens the sequence of tokens parsed in the source stream
	 * @throws ParenthesisNestingError for mis-matched parenthesis
	 */
	public void completeLexicalAnalysis
	(List<TokenParser.TokenDescriptor> tokens) throws ParenthesisNestingError
	{ setTokens (tokens); setExpression (LexicalAnalysis.expand (tokens)); }


	/**
	 * add semantic attributes to expression tree nodes
	 * @param symbols a symbol table that resolves symbol references in expression tree
	 * @param spaceManager a manager for the data type to be used in expression evaluation
	 * @throws SemanticError for errors in semantic resolution attempts
	 */
	public void completeSemanticAnalysis
	(SymbolMap symbols, ExpressionSpaceManager<T> spaceManager) throws SemanticError
	{ this.spaceManager = spaceManager; completeSemanticAnalysis (symbols); }


	/**
	 * reduce sub-expression sequences to tree nodes
	 * @param symbols a symbol table that resolves symbol references in expression tree
	 * @throws SemanticError for errors in semantic resolution attempts
	 */
	public void completeSemanticAnalysis (SymbolMap symbols) throws SemanticError
	{ SemanticAnalysis.attributeAndReduce (expression, spaceManager, symbols); }


	/**
	 * allocate a Calculation Engine for a tree evaluation
	 * @return a new instance of the engine
	 */
	public CalculationEngine<T> getCalculationEngine ()
	{ return new CalculationEngine<T> (spaceManager); }


	/**
	 * identify the token stream that matches the expression tree
	 * @param tokens the token list to hold in buffer
	 */
	public void setTokens (TokenParser.TokenSequence tokens) { this.tokens = tokens; }
	public void setTokens (Object source) { setTokens (new StringBuffer (source.toString ())); }
	public void setTokens (StringBuffer buffer) { setTokens (TokenParser.parse (buffer)); }

	/**
	 * get a list of tokens that represent the expression tree
	 * @return the token sequence
	 */
	public TokenParser.TokenSequence getTokens() { return tokens; }
	protected TokenParser.TokenSequence tokens;


	/**
	 * identify the tree being managed
	 * @param expression the expression to be associated
	 */
	public void setExpression (Expression<T> expression) { this.expression = expression; }

	/**
	 * get access to the tree being managed
	 * @return the tree being managed
	 */
	public Expression<T> getExpression() { return expression; }
	protected Expression<T> expression;


	/**
	 * get access to the tree being managed
	 * @return the tree being managed
	 */
	public SectionedSpline<T> getSectionedSpline () { return sectionedSpline; }
	protected SectionedSpline<T> sectionedSpline;


	/**
	 * build tree from JSON version
	 * @param source the JSON text source to read
	 * @throws Exception for any errors
	 */
	public void growFrom
		(SimpleStreamIO.TextSource source)
	throws Exception
	{
		restore = new JsonRestore<T> (spaceManager, environment.getSymbolMap ());
		JsonSemantics.JsonValue value = JsonReader.readFrom (source);

		if ( ! (value instanceof JsonSemantics.JsonObject) )
		{ throw new RuntimeException ("JSON representation error"); }
		jsonTree = new JsonBinding.Node ((JsonSemantics.JsonObject) value);

		if ( ! JsonBinding.isNode (jsonTree) )
		{
			throw new RuntimeException ("Source is not a CalcLib representation");
		}

		switch (nodeType = JsonBinding.getNodeTypeOf (jsonTree))
		{
			case Profile:
				restore.setProfile (value);
				expression = restore.getExpression ();
				break;
			case Sectioned:
				sectionedSpline = restore.getSectionedSpline (value);
				break;
			case Segment: case Spline:
				restore.setProfile (new Profile (jsonTree)); break;
			default: throw new RuntimeException ("Invalid node type");
		}
	}
	protected JsonBinding.NodeTypes nodeType;
	protected JsonBinding.Node jsonTree;


	/**
	 * @param symbols the symbol table for symbol resolution
	 * @throws Exception for any errors
	 */
	public void defineFunction
		(
			SymbolMap symbols
		)
	throws Exception
	{
		switch (nodeType = JsonBinding.getNodeTypeOf (jsonTree))
		{
			case	 Spline: defineSplineFunction ();			break;
			case 	Segment: defineSegmentFunction (symbols);	break;
			case  Sectioned: defineSectionedFunction (symbols);	break;
			case	Profile: defineUserFunction (symbols);		break;
			default: throw new RuntimeException ("Invalid node type");
		}
	}


	/**
	 * restore spline from JSON and post as functions
	 * @throws Exception for restore errors
	 */
	public void defineSplineFunction () throws Exception
	{
		Profile profile = getRestoredProfile ();
		CommonSplineDescription.restoreFormJson (spaceManager, jsonTree, restore)
		.postSymbols (profile.getProfileIdentifier (), profile.getProfileParameters ().get (0), environment);
	}


	/**
	 * restore segment from JSON and post as function
	 * @param symbols the symbol table to use locating symbol references
	 * @throws Exception for restore errors
	 */
	public void defineSegmentFunction (SymbolMap symbols) throws Exception
	{
		Profile profile = getRestoredProfile ();
		Coefficients <T> coefficients = new Coefficients <T> ();
		RepresentationTools.loadCoefficients (coefficients, jsonTree, spaceManager);
		String name = profile.getProfileIdentifier (), parameter = profile.getProfileParameters ().get (0);
		defineUserFunction (name, parameter, coefficients);
	}
	public void defineUserFunction
		(
			String name, String parameter, Coefficients <T> coefficients
		)
	{
		AbstractFunction<T> function = new PolynomialOptimizer<T> (environment)
		.getOptimizedFunctionFrom (name, parameter, jsonTree.getMemberString ("Class"), coefficients);
		environment.processDefinedFunction (function);
	}


	/**
	 * add an imported sectioned spline function to the symbol table
	 * @param symbols the symbol map for the environment
	 */
	public void defineSectionedFunction (SymbolMap symbols)
	{
		symbols.add (sectionedSpline.getFuntion (jsonTree));
	}


	/**
	 * add expression tree to symbol map as function
	 * @param symbols the symbol table to use locating symbol references
	 */
	public void defineUserFunction
		(
			SymbolMap symbols
		)
	{
		setTokens (expression);
		Profile profile = getRestoredProfile ();
		defineUserFunction (profile, symbols);
		applyDescription (profile, symbols);
	}
	void defineUserFunction (Profile profile, SymbolMap symbols)
	{
		DefinedFunction.defineUserFunction
		(
			profile.getProfileIdentifier (),
			profile.getProfileParameters (),
			tokens, spaceManager, symbols
		)
		.useExpressionTree (this);
	}


	/**
	 * get the Profile descriptor of most-recently loaded expression
	 * @return profile object recovered on restore
	 */
	public Profile getRestoredProfile ()
	{
		if (restore == null || restore.profile == null)
		{ throw new RuntimeException ("Profile not available"); }
		return restore.profile;
	}
	protected JsonRestore<T> restore = null;


	/**
	 * get the text representation of the profile
	 * @return the profile found in the restore
	 */
	public String getProfile ()
	{
		if (restore != null && restore.profile != null)
		{ return restore.profile.toString (); }
		else return null;
	}


	/**
	 * use expression tree to calculate result
	 * @param expression the tree to be used in calculation
	 * @param symbols the symbols referenced in the expression
	 * @return the value calculated using the expression
	 * @throws Exception for any errors
	 */
	public GenericValue reap
		(Expression<T> expression, SymbolMap symbols)
	throws Exception
	{
		return getCalculationEngine ().evaluate (expression, symbols);
	}

	/**
	 * run an evaluation of the tree being managed 
	 * @param symbols the symbols referenced in the expression
	 * @return the value calculated using the expression
	 * @throws Exception for any errors
	 */
	public GenericValue reap (SymbolMap symbols) throws Exception
	{ return reap (expression, symbols); }


	/**
	 * copy tree to new sink
	 * @param expression the tree source
	 * @param to the new sink point to receive JSON tree copy
	 * @throws Exception for any errors
	 * @param <T> data type
	 */
	public static <T> void transplant
		(Expression<T> expression, SimpleStreamIO.TextSink to)
	throws Exception
	{
		JsonPrettyPrinter.sinkTo (expression.toJson (), to);
	}


	/**
	 * save value to file
	 * @param name the name of the file to generate
	 * @param value the JSON Value to be saved
	 * @throws Exception for any errors
	 */
	public static void sinkToFile
	(String name, JsonSemantics.JsonValue value)
	throws Exception
	{
		SimpleStreamIO.TextSink to =
			new SimpleStreamIO.TextSink (fileCalled (name, SINGLE_TREE_EXTENSION));
		JsonPrettyPrinter.sinkTo (value, to);
	}


	/**
	 * save expression to JSON file
	 * @param name the name of the file to generate
	 * @throws Exception for any errors
	 */
	public void standardTransplant (String name) throws Exception
	{
		sinkToFile (name, expression.toJson ());
	}


	/**
	 * construct Profile object for expression
	 * @param name the name of the function being profiled
	 * @param parameters the list of formal parameter names
	 * @param description a description for the function (or NULL)
	 * @return a new Profile object for managed expression
	 */
	public Profile getProfileFor
	(String name, List<String> parameters, String description)
	{
		Profile.ParameterList parameterList =
				new Profile.ParameterList (parameters);
		Profile profile = Profile.representing (name, parameterList);
		profile.addImports (expression.describeImports ());
		profile.setProfileDescription (description);
		profile.setExpression (expression);
		return profile;
	}


	/**
	 * save function with profile
	 * @param name the name of the function
	 * @param parameters the list of formal parameter names
	 * @param description a description for the function
	 * @throws Exception for any errors
	 */
	public void profiledTransplant
	(String name, List<String> parameters, String description) throws Exception
	{
		Profile p = getProfileFor (name, parameters, description);
		p.orderMembersList (); sinkToFile (name, p);
	}


	/**
	 * description from profile goes into help table
	 * @param profile the profile of the function being defined
	 * @param symbols the symbol table to use locating symbol references
	 */
	public void applyDescription (Profile profile, SymbolMap symbols)
	{
		String description;;
		if ((description = profile.getProfileDescription ()) == null) return;
		symbols.addDescription (profile.getProfileIdentifier (), description);
	}


	/**
	 * ZIP file holds multiple trees, hence the forest analogy
	 * @param path the path to tree source being processed
	 * @return TRUE = path is forest
	 */
	public static boolean isForest (String path)
	{ return path.toLowerCase ().endsWith (FOREST_EXTENSION); }


	/**
	 * load all entries in a ZIP file
	 * @param name the name of the source file
	 * @param environment access to utility methods
	 * @return the Participants involved in the growing of trees
	 * @throws Exception for errors found
	 * @param <T> data type used
	 */
	public static <T> Associates<T> loadFromZip
		(
			String name, Environment<T> environment
		)
	throws Exception
	{
		Gardener<T> gardener;
		File file = fileCalled (name, FOREST_EXTENSION);
		Associates<T> participants = new EmployedAssociates<T>();
		ZipSource zip = new ZipSource (file);

		while (zip.positionToNext () != null)
		{
			SimpleStreamIO.Source source = zip.getSource ();
			if ( ! (source instanceof SimpleStreamIO.TextSource) ) continue;
			gardener = loadFrom ((SimpleStreamIO.TextSource) source, environment);
			gardener.setName (zip.getEntryProperties ().getName ());
			participants.add (gardener);
		}

		return participants;
	}


	/**
	 * @param apparantName the name found in the source
	 */
	public void setName (String apparantName)
	{
		expressionName = apparantName;
		if (restore != null && restore.profile != null)
		{ expressionName = restore.profile.getProfileIdentifier (); }
		//System.out.println (expressionName);
	}


	/**
	 * @return the name associated with the expression
	 */
	public String getExpressionName () { return expressionName; }
	protected String expressionName;


	/**
	 * load single expression from JSON file
	 * @param functionName the name of the source file
	 * @param environment access to utility methods
	 * @return a gardener for the tree restored from the source
	 * @throws Exception for errors found
	 * @param <T> data type used
	 */
	public static <T> Gardener<T> loadFromJson
		(
			String functionName,
			Environment<T> environment
		)
	throws Exception
	{
		File file = fileCalled (functionName, SINGLE_TREE_EXTENSION);
		return loadFromJson (file, environment);
	}


	/**
	 * load single expression from JSON file
	 * @param json a JSON source file for import
	 * @param environment access to utility methods
	 * @return a gardener for the tree restored from the source
	 * @throws Exception for errors found
	 * @param <T> data type used
	 */
	public static <T> Gardener<T> loadFromJson
		(
			File json, Environment<T> environment
		)
	throws Exception
	{
		return loadFrom (SimpleStreamIO.getFileSource (json), environment);
	}


	/**
	 * load expression from text source
	 * @param source the text source to read
	 * @param environment access to utility methods
	 * @return a gardener for the tree restored from the source
	 * @throws Exception for errors found
	 * @param <T> data type used
	 */
	public static <T> Gardener<T> loadFrom
		(
			SimpleStreamIO.TextSource source,
			Environment<T> environment
		)
	throws Exception
	{
		Gardener<T> g = new Gardener<T> (environment);
		g.growFrom (source); g.defineFunction (environment.getSymbolMap ());
		return g;
	}


	/**
	 * display list of component sub-expressions
	 */
	public void dump ()
	{
		System.out.println ("{");
		dump (expression.invocations);
		System.out.println ("=======");
		dump (expression.components);
		System.out.println ("}");
	}
	public void dump (List<SubExpression<T>> expressions)
	{
		for (SubExpression<T> se : expressions)
		{ System.out.println ("\t" + se); }
	}


	/**
	 * set flag to print metrics of evaluation run
	 */
	public void showLoopStats () { RangeEvaluator.displayMetrics = true; }


}


/**
 * report generator for loaded expression entries of ZIP loads
 * @param <T> data type used by expressions
 */
class EmployedAssociates<T> extends Gardener.Associates<T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.Gardener.Associates#displayTo(java.io.PrintStream, net.myorb.math.expressions.SymbolMap)
	 */
	public void displayTo (PrintStream out, SymbolMap symbols)
	{
		out.println ();
		out.println ("Functions Loaded:");
		out.println ();

		String name, desc;
		for (Gardener<T> gardener : this)
		{
			name = gardener.getExpressionName ();
			desc = symbols.getDescription (name);

			if (desc == null)
			{
				also.add (gardener);
				continue;
			}

			out.print ("\t"); out.print (name);
			if (desc != null) out.print (" - " + desc);
			out.println ();
		}

		also.titleRequiredWhen (also.size () < this.size ()).displayTo (out, symbols);
		out.println (); out.println ("End of load");
		out.println ();
	}

	private SupportAssociates<T> also = new SupportAssociates<T>();
	private static final long serialVersionUID = -8015463950606849441L;

}


/**
 * report generator for loaded expressions with no descriptions
 * @param <T> data type used by expressions
 */
class SupportAssociates<T> extends Gardener.Associates<T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.tree.Gardener.Associates#displayTo(java.io.PrintStream, net.myorb.math.expressions.SymbolMap)
	 */
	public void displayTo (PrintStream out, SymbolMap symbols)
	{
		int rem;
		if ((rem = this.size ()) > 0)
		{
			if (showHeader)
			{
				out.println ();
				out.println ("Also loaded with no description:");
				out.println ();
			}

			int online = 5; out.print ("\t");
			for (int n = 0; n < this.size (); n++)
			{
				if (-- online == 0)
				{ out.println (); out.print ("\t"); online = 5; }
				out.print (this.get (n).getExpressionName ());
				if (-- rem > 0) out.print (", ");
			}
			out.println ();
		}
	}
	private boolean showHeader;

	/**
	 * is header required for this section
	 * @param showHeader TRUE = header should be displayed
	 * @return THIS for chain calls
	 */
	public Gardener.Associates<T> titleRequiredWhen
		(boolean showHeader) { this.showHeader = showHeader; return this; }
	private static final long serialVersionUID = 2322389777938709375L;

}

