
package net.myorb.math.expressions.commands;

// CalcLib Matrix
import net.myorb.math.matrices.Matrix;

// CalcLib expressions
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.GraphManager;
import net.myorb.math.expressions.PrettyFormatter;
import net.myorb.math.expressions.OperatorNomenclature;

// CalcLib spline functionality
import net.myorb.math.expressions.gui.SplineTool;
import net.myorb.math.expressions.symbols.SplineExport;
import net.myorb.math.expressions.symbols.SplineDescriptor;

//CalcLib snip editing functionality
import net.myorb.math.expressions.gui.editor.SnipTool;

// CalcLib evaluation states
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

// IOlib utilities
import net.myorb.data.abstractions.SimpleUtilities;

// JRE 
import java.util.HashMap;
import java.util.Map;

/**
 * support for utilities that require environment access
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class EnvironmentalUtilities<T> extends CommandAdministration<T>
{


	/**
	 * pass access as an object
	 */
	public interface AccessToTopOfStack
	{
		/**
		 * @param tokens the expression being evaluated
		 * @return the value from top of stack
		 */
		ValueManager.GenericValue getValue (CommandSequence tokens);
	}


	public EnvironmentalUtilities
	(Environment<T> environment)
	{ super (environment); }


	/**
	 * get a copy of the pretty formatter object
	 * @return a pretty formatter object
	 */
	public PrettyFormatter<T> getPrettyFormatter ()
	{
		if (prettyFormatter != null) return prettyFormatter;
		else return prettyFormatter = new PrettyFormatter<T> (environment);
	}
	private PrettyFormatter<T> prettyFormatter = null;


	/**
	 * get a copy of the GraphManager object
	 * @return a copy of the GraphManager object
	 */
	public GraphManager<T> getGraphManager () { return new GraphManager<T> (environment); }


	/**
	 * resolve a symbol to a matrix
	 * @param matrixSymbol the symbol to resolve
	 * @return the matrix found
	 */
	public Matrix<T> getMatrixFrom (String matrixSymbol)
	{
		Matrix<T> matrix = environment.getValueManager ().toMatrix
		(environment.getSymbolMap ().getValue (matrixSymbol));
		return matrix;
	}


	/**
	 * show named renderer, allocate new if not yet present
	 * @param tokens the source tokens from the command
	 */
	public void setCurrentRenderer (CommandSequence tokens)
	{
		if (renderer != null)
		{
			renderer.hideRenderFrame ();									// hide current renderer if visible
		}

		if (tokens.size () == 0)											// absence of name implies default
		{
			renderer = displays.get (DEFAULT);								// get default from display map
			if (renderer == null) generateDefaultRenderer ();				// or create entry for default if first reference
		}
		else select (TokenParser.unQuoted (tokens.get (0)));				// can be QOT or IDN

		renderer.showRenderFrame ();										// make named item visible
	}


	/**
	 * @param name the name of a display
	 */
	public void select (String name)
	{
		if (displays.containsKey (name))									// is new or was created before
			renderer = displays.get (name);									// use previous if found in map
		else
		{
			renderer = new Rendering<T> (name, environment);				// create new entry with new name
			displays.put (name, renderer);									// add new entry to map
		}
	}
	protected Map <String, Rendering<T>> displays = new HashMap<>();		// map name to its entry


	/**
	 * @return the selected renderer
	 */
	public Rendering<T> getCurrentRenderer ()
	{
		if (renderer == null)
			generateDefaultRenderer ();										// force default if first reference
		return renderer;
	}
	protected Rendering<T> renderer = null;


	/**
	 * a default renderer if no SELECT is given
	 */
	public void generateDefaultRenderer ()
	{
		renderer = new Rendering<T> (TITLE, environment);					// use title Rendered Equations
		displays.put (DEFAULT, renderer);									// use name user cannot conflict with
	}
	static final String DEFAULT = "#DEFAULT#", TITLE = "Rendered Equations";


	/**
	 * display HELP table
	 */
	public void help ()
	{
		new DocumentManagement<T> (environment).help (commands);
	}


	/**
	 * use SNIP editor
	 */
	public void editSnip ()
	{
		SnipTool.addSnip (environment);
	}


	/**
	 * include commands for prime table functionality
	 */
	public void addPrimeNumberCommands ()
	{
		PrimeNumbers<T> primes = new PrimeNumbers<T>(environment);
		addAsLowerCase (OperatorNomenclature.RUNSIEVE_KEYWORD, primes.constructRunsieveKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.PRIMETABLE_KEYWORD, primes.constructPrimetableKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.PRIMEGAPS_KEYWORD, primes.constructPrimegapsKeywordCommand ());
	}


	/**
	 * add additional special commands for complex domains
	 */
	public void addComplexKeywordMap ()
	{
		Plotting<T> plotting = new Plotting<T>(environment);
		addAsLowerCase (OperatorNomenclature.POLAR_ANGULAR_KEYWORD, plotting.constructAngularKeywordCommand ());
		addAsLowerCase (OperatorNomenclature.POLAR_RADIAL_KEYWORD, plotting.constructRadialKeywordCommand ());
	}


	/**
	 * mark expression as being represented by a tree structure
	 * @param name the name of the function to be converted to tree
	 */
	public void allowExpressionTree (String name)
	{
		Subroutine<T> s = Subroutine.cast (environment.getSymbolMap ().get (name));
		if (s == null) throw new RuntimeException ("Symbol is not a user defined function: " + name);
		s.allowExpressionTree ();
	}


	/**
	 * create a spline tool
	 * @param tokens the source tokens from the command
	 */
	public void splineToolInvocation (CommandSequence tokens)
	{
		String functionSymbol = Utilities.getSequenceFollowing (2, tokens);
		Subroutine<T> s = Subroutine.cast (environment.getSymbolMap ().get (functionSymbol));
		new SplineTool<T> (s.toSimpleFunction (), environment.getSpaceManager ());
	}


	/**
	 * export spline descriptor
	 * @param tokens the source tokens from the command
	 */
	public void encodeSpline (CommandSequence tokens)
	{
		String functionSymbol = getFunctionName (tokens);
		Object symbol = environment.getSymbolMap ().get (functionSymbol);
		new SplineExport<T>().forDescriptor (getSplineDescriptor (symbol));
	}
	@SuppressWarnings("unchecked") SplineDescriptor<T> getSplineDescriptor (Object symbol)
	{
		SplineDescriptor<T> s = SimpleUtilities.verifyClass (symbol, SplineDescriptor.class);
		if (s == null) throw new RuntimeException ("Symbol is not a segmented function");
		else return s;
	}


	/**
	 * add description to function in dictionary
	 * @param tokens the source tokens from the command
	 */
	public void describeFunction (CommandSequence tokens)
	{
		StringBuffer functionName = new StringBuffer ();
		int pos = getFunctionName (0, tokens, functionName);
		String description = TokenParser.toString (startingFrom (pos, tokens));
		environment.getSymbolMap ().addDescription (functionName.toString (), description);
	}


	/**
	 * plot array on TOS
	 * @param type the type of plot
	 * @param tokens the text of the command
	 * @param tosAccess access to top of stack
	 */
	public void plot
	(GraphManager.Types type, CommandSequence tokens, AccessToTopOfStack tosAccess)
	{ getGraphManager ().plot (type, tosAccess.getValue (tokens)); }


	/**
	 * allow LIM keyword
	 * @param type the type of plot
	 * @param tokens the text of the command
	 * @param tosAccess access to top of stack
	 */
	public void plotWithLimit
	(GraphManager.Types type, CommandSequence tokens, AccessToTopOfStack tosAccess)
	{ processLimit (tokens); plot (type, tokens, tosAccess); resetLimit (); }


	/**
	 * eliminate keyword in tokens
	 * @param tokens the source tokens
	 * @return same tokens with command token removed
	 */
	public static CommandSequence
		withCommandRemoved (CommandSequence tokens)
	{ return new CommandSequence (startingFrom (1, tokens)); }


	/**
	 * treat token list as a filename
	 * @param tokens the list of tokens to translate
	 * @return the text of a filename
	 */
	public static String filename (CommandSequence tokens)
	{ return getTextOfSequence (withCommandRemoved (tokens)); }


}

