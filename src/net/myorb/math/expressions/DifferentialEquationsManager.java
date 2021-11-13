
package net.myorb.math.expressions;

import net.myorb.math.Polynomial;
import net.myorb.math.computational.RungeKutta;
import net.myorb.math.GeneratingFunctions.Coefficients;
import net.myorb.math.computational.CommonSplineDescription;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.symbols.FunctionWrapper;
import net.myorb.math.expressions.symbols.AbstractFunction;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.expressions.commands.CommandSequence;

import net.myorb.math.expressions.evaluationstates.DeclarationSupport.TokenStream;
import net.myorb.math.expressions.evaluationstates.FunctionDefinition;
import net.myorb.math.expressions.evaluationstates.Primitives;

import net.myorb.math.expressions.gui.FormalActualParameters;
import net.myorb.data.abstractions.CommonCommandParser;
import net.myorb.data.abstractions.SimpleUtilities;
import net.myorb.data.abstractions.Status;

import net.myorb.gui.components.SimpleScreenIO.Alert;
import net.myorb.gui.components.SimpleScreenIO;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * collect properties of Differential Equations
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class DifferentialEquationsManager <T>
{


	/**
	 * provide call-back to command
	 */
	public interface FollowUp
	{
		/**
		 * execute follow-up steps
		 * @param usingParameters values assigned to formal parameters
		 */
		void runFollowUp (Map <String, String> usingParameters);
	}


	/**
	 * describe solution segments
	 */
	public interface SolutionManagement
	{

		/**
		 * capture description of most recent built solution
		 * @param solution the function used to compute points on the segment
		 * @param T0 the initial T0 value for the segment
		 * @param Tn the domain effective high
		 */
		void saveSolution (AbstractFunction <Double> solution, Double T0, Double Tn);

		/**
		 * @return the last solution function saved
		 */
		AbstractFunction <Double> getLastSavedSolution ();

		/**
		 * discard last of all solution description objects
		 */
		void discardLastSolution ();

	}


	/**
	 * symbol manipulation methods
	 */
	public interface EquationSymbols extends SolutionManagement
	{

		/**
		 * @param name the name of a function
		 * @return the descriptor for the function
		 * @param <T> type on which operations are to be executed
		 */
		<T> AbstractFunction <T> getFunctionFor (String name);

		/**
		 * @param name the name of the function
		 * @param parameters the formal parameters
		 * @param tokenStream the tokens that make the function body
		 */
		void declare (String name, List <String> parameters, TokenStream tokenStream);

		/**
		 * @param name the name of the results vector
		 * @param results the list of values of the results
		 * @param <T> type on which operations are to be executed
		 */
		<T> void assign (String name, List <T> results);

		/**
		 * @param identifiers the identifiers to be found
	 	 * @param defaultValue a default value for identifiers not found
	 	 * @param settings the map of names to values
		 */
		void include (Set <String> identifiers, String defaultValue, Map <String, String> settings);

		/**
		 * @param T0 new value of T0
		 * @param Y0 function evaluation at T0
		 */
		void resetInitialConditions (double T0, double Y0);

		/**
		 * display spline
		 * @throws Alert for incomplete spline
		 */
		void showSplineSegments () throws Alert;

	}


	/**
	 * properties used by test runner
	 */
	public interface EquationProperties extends EquationSymbols
	{

		/**
		 * issue error function declaration.
		 *  this establishes links between formal 
		 *  and actual parameters of the equation.
		 */
		void declareEquation ();

		/**
		 * @return the name of the DiffEQ error function
		 */
		String getFunctionName ();

		/**
		 * @return a text description of the function
		 */
		String getDescription ();

		/**
		 * @return the name of the associated RK4 function
		 */
		String getApproximation ();

		/**
		 * run the Runge-Kutta solution check script
		 */
		void completeApproximation ();

		/**
		 * check formal/actual parameter matches
		 * @return name/value property pairs
		 */
		Map <String, String> evaluate ();

		/**
		 * @return reassembled token list
		 */
		String getFunctionBodyText ();

		/**
		 * @return current segment effective domain high
		 */
		double getCurrentEffectiveHigh ();

		/**
		 * execute follow-up processing if applicable
		 */
		void runFollowUp ();

	}


	/**
	 * @param environment access to the symbol table
	 */
	public DifferentialEquationsManager (Primitives <T> environment)
	{
		this.tracker = new SymbolTracking <T> (environment);
		this.mgr = new ExpressionFloatingFieldManager ();
		this.environment = environment;
	}
	protected Primitives <?> environment;
	protected ExpressionSpaceManager <Double> mgr;
	protected SymbolTracking <T> tracker;


	/*
	 * equations description methods
	 */


	/**
	 * format as standard differential equation
	 * @param f the tokens of the body of the function
	 * @return a copy of the function with = 0 indicating DiffEQ form
	 */
	public static TokenParser.TokenSequence diffEqFormFor (TokenParser.TokenSequence f)
	{
		TokenParser.TokenSequence functionTokens = new TokenParser.TokenSequence ();
		functionTokens.addAll (f); functionTokens.addAll (TokenParser.parse (EQZ));
		return functionTokens;
	}
	static final StringBuffer EQZ = new StringBuffer (" = 0 ");


	/**
	 * connect equation with description and possibly with an approximation function
	 * @param tokens the command tokens holding the function name and the description
	 */
	public void identifyDiffEq (CommandSequence tokens)
	{
		try
		{
			RecognizedEquation eq =
				getDiffEq (tokens.get (1).getTokenImage ());						// token[1] = equation name
			CommonCommandParser.TokenDescriptor t = tokens.get (2);					// token[2] = approximation or description
			if (t.getTokenType () != CommonCommandParser.TokenType.QOT)				// token[3] = description when approximation present
			{ eq.setApproximation (t.getTokenImage ()); t = tokens.get (3); }		// the description will be a QOT token
			eq.setDescription (t.getTokenImage ());									// QOT token is passed as description
		}
		catch (Alert alert) { alert.presentDialog (); }
	}


	/**
	 * set of segments collected for solution
	 */
	public class SolutionSet implements SolutionManagement
	{

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.DifferentialEquationsManager.SolutionManagement#saveSolution(net.myorb.math.expressions.symbols.AbstractFunction, java.lang.Double, java.lang.Double)
		 */
		public void saveSolution (AbstractFunction <Double> solution, Double T0, Double Tn) { savedSolutions.add (solution); solutionBases.add (T0); setTn (Tn); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.DifferentialEquationsManager.EquationSymbols#discardLastSolution()
		 */
		public void discardLastSolution () { discardLastSavedSolution (); discardLastBase (); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.DifferentialEquationsManager.EquationSymbols#getLastSavedSolution()
		 */
		public AbstractFunction<Double> getLastSavedSolution () { return savedSolutions.get (SimpleUtilities.lastEntryOf (savedSolutions)); }

		/**
		 * discard last solution function
		 */
		public void discardLastSavedSolution () { SimpleUtilities.discardLastEntryOf (savedSolutions); }
		protected List < AbstractFunction <Double> > savedSolutions = new ArrayList <> ();

		/**
		 * @param Tn the effective domain hi for last segment
		 */
		public void setTn (double Tn) { this.Tn = Tn; }
		protected Double Tn;

		/**
		 * get the spline knot values
		 * @return the solution bases which establish the spline knots
		 */
		public Double [] getKnots ()
		{
			int solutionCount = solutionBases.size ();
			if (solutionCount == 1) return new Double [] {}; // single segment, so not really a spline
			if (solutionCount == 0) throw new RuntimeException ("No solutions have been captured");
			return SimpleUtilities.arrayOf (solutionBases.subList (1, solutionCount));
		}

		/**
		 * discard last solution base
		 */
		public void discardLastBase () { SimpleUtilities.discardLastEntryOf (solutionBases); }
		protected List < Double > solutionBases = new ArrayList <> ();

		/**
		 * solutions are power functions.
		 *  so coefficients can be read directly from function description objects
		 * @return a set of coefficients per segment
		 */
		public List <Coefficients <Double>> getSegmentCoefficients ()
		{
			List <Coefficients <Double>> coefficients = new ArrayList <> ();
			for (AbstractFunction <Double> segment : savedSolutions)
			{
				Polynomial.PowerFunction <Double> segmentSolution =
					(Polynomial.PowerFunction <Double>) ( (FunctionWrapper <Double>) segment ).getFunction ();
				coefficients.add (segmentSolution.getCoefficients ());
			}
			return coefficients;
		}

		/**
		 * describe full domain of solution spline
		 * @return Map of Lo/Hi/Delta for error plot
		 */
		protected Map <String, String> getFollowUpParameters ()
		{
			Map <String, String> parameters = new HashMap <String, String> ();
			parameters.put ("LO", solutionBases.get (0).toString ());
			parameters.put ("HI", Tn.toString ());
			parameters.put ("DELTA", "0.01");
			return parameters;
		}

	}


	/*
	 * symbol descriptions
	 */

	/**
	 * manager for symbols associated with equations
	 */
	public class SymbolManagement extends SolutionSet implements EquationSymbols
	{


		/**
		 * prepare function manager to be used for declarations
		 * @param functionManager the manager for function declarations
		 */
		public void setFunctionManager
		(FunctionDefinition <T> functionManager)
		{ this.functionManager = functionManager; }
		protected FunctionDefinition <T> functionManager;


		/**
		 * pass text command to evaluation control
		 * @param command text of command to be executed
		 */
		public void execute (String command) { environment.getSpaceManager ().getEvaluationControl ().execute (command); }


		/*
		 * implement interface specific methods
		 */

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.DifferentialEquationsManager.EquationSymbols#showSplineSegments()
		 */
		public void showSplineSegments () throws Alert
		{
			// post results to symbol table
			getSpline ().postSymbols ("y", "x", environment);
			SimpleScreenIO.presentToUser (SPLINE_CREATED);
			// getSpline ().format (); // debug dump //
		}
		static final String CREATED = "Spline has been created";
		Status SPLINE_CREATED = new Status (CREATED, Status.Level.INFO);

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.DifferentialEquationsManager.EquationSymbols#resetInitialConditions(double, double)
		 */
		public void resetInitialConditions (double T0, double Y0) { execute ("T0 = " + T0 + " ; Y0 = " + Y0); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.DifferentialEquationsManager.EquationSymbols#include(java.util.Set, java.lang.String, java.util.Map)
		 */
		public void include
		(Set <String> identifiers, String defaultValue, Map <String, String> settings)
		{ tracker.include (identifiers, defaultValue, settings); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.DifferentialEquationsManager.EquationSymbols#getFunctionFor(java.lang.String)
		 */
		@SuppressWarnings ("unchecked") public AbstractFunction <T> getFunctionFor (String name) { return tracker.findFunction (name); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.DifferentialEquationsManager.EquationSymbols#assign(java.lang.String, java.util.List)
		 */
		public <A> void assign (String name, List <A> results)
		{ environment.setSymbol (name, new ValueManager <A> ().newDimensionedValue (results)); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.DifferentialEquationsManager.EquationSymbols#declare(java.lang.String, java.util.List, net.myorb.math.expressions.evaluationstates.DeclarationSupport.TokenStream)
		 */
		public void declare (String name, List <String> parameters, TokenStream tokenStream)
		{ functionManager.defineFunction (name, parameters, tokenStream); }


		/*
		 * spline operations
		 */

		/**
		 * captured knots and coefficients are used to build a spline for the solution
		 */
		public class SolutionSpline extends CommonSplineDescription <Double>
		{
			public SolutionSpline (ExpressionSpaceManager <Double> mgr)
			{
				super (mgr); buildFromList (getKnots (), getSegmentCoefficients ());
			}
		}

		/**
		 * build a new solution spline
		 * @return spline object for solution
		 * @throws Alert for errors in spline definition
		 */
		public CommonSplineDescription <Double> getSpline () throws Alert
		{
			SolutionSpline spline = null;
			try { spline = new SolutionSpline (mgr); }
			catch (Exception e) { SimpleScreenIO.alertError (e.getMessage ()); }
			return spline;
		}

	}


	/**
	 * collected properties
	 */
	public class RecognizedEquation extends SymbolManagement implements EquationProperties
	{

		AbstractFunction<T> definition;				// the symbol table definition
		AbstractFunction<T> approximation;			// associated RK approximation function
		String functionName, description;			// the name and description of the function
		TokenParser.TokenSequence tokens;			// the tokens that make the body of the function
		List <String> parameterList;				// the parameters of the function stored as a list
		Set <String> identifiers;					// the identifiers used in the equation
		Set <String> parameters;					// the parameters stored as a set
		boolean wasRendered;						// render has already been done
		FollowUp followUp;							// connected processing steps

		/**
		 * print system output dump of record
		 */
		public void dump ()
		{
			System.out.print (getFunctionName ());
			if (description != null) System.out.println (getDescription ());
			System.out.print (" - "); System.out.print (SymbolTracking.getFunctionBody (tokens));
			System.out.print (" - "); System.out.print (identifiers);
			System.out.println ();
		}

		/*
		 * processing of body token symbols
		 */

		/**
		 * body tokens searched for identifiers
		 */
		public void analyzeBody ()
		{
			for (CommonCommandParser.TokenDescriptor token : tokens)
			{
				// keep set of identifiers
				if (token.getTokenType () == CommonCommandParser.TokenType.IDN)
				{ identifiers.add (token.getTokenImage ());	}
			}
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.DifferentialEquationsManager.EquationProperties#evaluate()
		 */
		public Map <String, String> evaluate ()
		{
			Map <String, String> settings = new HashMap <> ();

			for (String identifier : identifiers)
			{
				if (parameters.contains (identifier))
				{
					// recognize formal parameter of differential equation
					settings.put (identifier, "Domain Parameter");
				}
				else
				{
					// identifiers sought in symbol table
					tracker.include (identifier, "", settings);
				}
			}

			return settings;
		}

		/*
		 * POJO get/set methods for equation properties
		 */

		public void setDescription (String description)
		{ this.description = TokenParser.stripQuotes (description); }
		public String getDescription () { return description==null? "": description; }
		public void setApproximation (String functionName) { setApproximation (environment.getSymbolMap ().lookup (functionName)); }
		public void setApproximation (SymbolMap.Named symbol) { approximation = AbstractFunction.cast (symbol); }
		public String getApproximation () { return approximation==null? "": approximation.getName (); }
		public String getFunctionBodyText () { return SymbolTracking.getFunctionBody (tokens); }
		public String getFunctionName () { return functionName; }
		public double getCurrentEffectiveHigh () { return Tn; }
		public FollowUp getFollowUp () { return followUp; }
		public void setFollowUp (FollowUp followUp)
		{ this.followUp = followUp; }

		/*
		 * implementation of follow-up for this equation
		 */

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.DifferentialEquationsManager.EquationProperties#runFollowUp()
		 */
		public void runFollowUp () { if (followUp != null) followUp.runFollowUp (getFollowUpParameters ()); }

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.DifferentialEquationsManager.EquationProperties#completeApproximation()
		 */
		public void completeApproximation ()
		{ execute ("READ RungeKutta.txt"); SimpleScreenIO.presentToUser (SEGMENT_POSTED); }
		static final String POSTED = "Segment interpolation and error have been posted";
		Status SEGMENT_POSTED = new Status (POSTED, Status.Level.INFO);

		/*
		 * declaration of ODE method
		 */

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.DifferentialEquationsManager.EquationProperties#declareEquation()
		 */
		public void declareEquation ()
		{
			declare (functionName, parameterList, new TokenStream (definition.getFunctionTokens ()));
		}

		/**
		 * @param functionName the name of the function
		 * @param definition the symbol table definition of the function
		 * @param tokens the tokens that make the body of the function
		 */
		RecognizedEquation (String functionName, AbstractFunction<T> definition, TokenParser.TokenSequence tokens)
		{
			this.parameters = new HashSet <> ();
			this.parameterList = definition.getParameterNames ();
			this.parameters.addAll (parameterList);
			this.identifiers = new HashSet <> ();
			this.functionName = functionName;
			this.definition = definition;
			this.approximation = null;
			this.wasRendered = false;
			this.description = null;
			this.followUp = null;
			this.tokens = tokens;
			analyzeBody ();
		}

	}


	/*
	 * posting of equations
	 */


	/**
	 * construct equation description
	 *  and add to map of recognized equations
	 * @param functionName the name of the function
	 * @param definition the symbol table definition of the function
	 * @param tokens the tokens that make the body of the function
	 * @return the equation descriptor
	 */
	public RecognizedEquation post
		(
			String functionName, AbstractFunction<T> definition, TokenParser.TokenSequence tokens
		)
	{
		RecognizedEquation properties =
			new RecognizedEquation (functionName, definition, tokens);
		posted.put (functionName, properties);
		return properties;
	}
	protected Map<String, RecognizedEquation> posted = new HashMap <> ();


	/**
	 * function must be found in symbol table
	 * @param name the name of the function
	 * @return the equation descriptor
	 * @throws Alert for errors
	 */
	public RecognizedEquation post (String name) throws Alert
	{
		AbstractFunction<T> f = tracker.findFunction (name);
		if (f == null) SimpleScreenIO.alertError ("No such function: " + name);
		return post (name, f, diffEqFormFor (f.getFunctionTokens ()));
	}


	/**
	 * lazy init (post) for equations
	 * @param forName the name of the function
	 * @return the equation descriptor
	 * @throws Alert for errors
	 */
	public RecognizedEquation getDiffEq (String forName) throws Alert
	{
		RecognizedEquation eq = posted.get (forName);
		if (eq == null) eq = post (forName);
		return eq;
	}


	/**
	 * has equation been posted
	 * @param name the name of the function
	 * @return TRUE = equation has been defined
	 */
	public boolean isRecognized (String name)
	{
		return posted.containsKey (name);
	}


	/**
	 * identify equation by name
	 * @param functionName the name of the error function
	 * @return the equation description
	 * @throws Alert for errors
	 */
	public RecognizedEquation find (String functionName) throws Alert
	{
		RecognizedEquation
		selectedEquation = posted.get (functionName);
		if (selectedEquation == null) SimpleScreenIO.alertError
			(functionName + " is not recognized as a differential equation");
		return selectedEquation;
	}


	/**
	 * connect follow-up action to identified function
	 * @param functionName the function to associate with the action
	 * @param followUp the processing to be associated
	 * @throws Alert for errors
	 */
	public void connect (String functionName, FollowUp followUp) throws Alert
	{
		find (functionName).setFollowUp (followUp);
	}


	/*
	 * equation rendering
	 */


	/**
	 * get the display tokens for the function
	 * @param forName the name of the function to be displayed
	 * @return the token sequence for rendering the function
	 * @throws Alert for errors
	 */
	public TokenParser.TokenSequence getRenderSequence (String forName) throws Alert
	{
		RecognizedEquation eq = getDiffEq (forName);
		eq.wasRendered = true;
		return eq.tokens;
	}


	/**
	 * recognize redundant declarations
	 * @param name the name of the function
	 * @return TRUE = has already been rendered
	 */
	public boolean wasRendered (String name)
	{
		if (isRecognized (name))
		{ return posted.get (name).wasRendered; }
		else return false;
	}


	/*
	 * equation quality metrics
	 */


	/**
	 * analyze the alias and constant settings
	 * @param tokens the tokens from the command line
	 * @param functionManager a function definition manager
	 */
	public void runTest
	(CommandSequence tokens, FunctionDefinition<T> functionManager)
	{
		try { runTest (tokens.get (1).getTokenImage (), functionManager); }
		catch (Alert alert) { alert.presentDialog (); }
	}


	/**
	 * initialize and start the Test Runner
	 * @param functionName the name of the function that identifies the equation
	 * @param functionManager a function management object
	 * @throws Alert for errors
	 */
	public void runTest (String functionName, FunctionDefinition<T> functionManager) throws Alert
	{
		RecognizedEquation selectedEquation = find (functionName);
		selectedEquation.setFunctionManager (functionManager);
		new TestRunner ().init (selectedEquation);
	}


}


/**
 * collect properties of object that control test runs
 */
class TestRunner implements FormalActualParameters.Callbacks
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.FormalActualParameters.Callbacks#proceed()
	 */
	@Override public void proceed () throws Alert
	{
		check (); declareEquation (); selectedEquation.runFollowUp ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.FormalActualParameters.Callbacks#takeAction(java.lang.String)
	 */
	@Override public void takeAction (String called) throws Alert
	{
		if (RELOAD.equals (called))
		{ display.setItems (selectedEquation.evaluate ()); }								// Reload
		else if (SPLINE.equals (called)) { selectedEquation.showSplineSegments (); }		// Spline
		else new ApproximationRunner ().init (selectedEquation);							// RK4
	}
	static final String RELOAD = "Reload", SPLINE = "Spline", RK4 = "RK4";

	/**
	 * verify all parameters are set
	 */
	public void check () throws Alert { display.verify (); }

	/**
	 * show map of formal/actual parameters
	 */
	public void showForm ()
	{
		display = new FormalActualParameters (this);
		display.addFormalActual ("Function", selectedEquation.getFunctionName ());
		display.addFormalActual ("Equation", selectedEquation.getFunctionBodyText ());
		display.addFormalActual ("Description", selectedEquation.getDescription ());
		optionallyInclude ("Approximation", selectedEquation.getApproximation ());
		display.addItems (selectedEquation.evaluate ());
		display.showFrame ("Solution Test", ACTIONS);
	}
	static final String[] ACTIONS = new String[] {RELOAD, SPLINE, RK4};

	/**
	 * omit empty values from display
	 * @param field title for value displayed
	 * @param value the value for this field
	 */
	protected void optionallyInclude (String field,  String value)
	{ if ( ! value.isEmpty () ) display.addFormalActual (field, value); }
	protected FormalActualParameters display;

	/**
	 * reset DiffEQ alias links
	 */
	public void declareEquation () { selectedEquation.declareEquation (); }

	/**
	 * link alias symbols and show form
	 * @param selectedEquation equation that will display solution error metrics
	 */
	public void init (DifferentialEquationsManager.EquationProperties selectedEquation)
	{ this.selectedEquation = selectedEquation; declareEquation (); showForm (); }
	protected DifferentialEquationsManager.EquationProperties selectedEquation;

}


/**
 * collect properties of object that are required for RK4
 */
class ApproximationRunner implements FormalActualParameters.Callbacks
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.FormalActualParameters.Callbacks#proceed()
	 */
	@Override public void proceed () throws Alert
	{
		try
		{
			FormulaParameters p = new FormulaParameters (settings);
			declareResultVector (new RungeKutta <Double> (getFunction (), p));
			declareApproximation (); selectedEquation.completeApproximation ();
			AbstractFunction <Double> y = selectedEquation.getFunctionFor ("y");
			selectedEquation.saveSolution (y, p.getT0 (), p.getTn ());
		}
		catch (Alert a)
		{
			a.presentDialog ();
		}
		catch (NumberFormatException e)
		{
			SimpleScreenIO.alertError ("Invalid Parameter");
		}
	}

	/**
	 * assign the result vector (Y)
	 * @param approximation the RK4 computation object
	 */
	public void declareResultVector (RungeKutta <Double> approximation)
	{
		selectedEquation.assign ("Y", approximation.doIterations ());
	}

	/**
	 * post approximation function definition (f)
	 */
	public void declareApproximation ()
	{
		String functionName = selectedEquation.getApproximation ();
		StringBuffer profile = new StringBuffer (functionName).append ("(Tn, Yn)");
		TokenStream tokenStream = new TokenStream (TokenParser.parse (profile));
		selectedEquation.declare ("f", parameters, tokenStream);
	}

	/**
	 * @return find the approximation function symbol
	 */
	public AbstractFunction <Double> getFunction ()
	{
		return selectedEquation.getFunctionFor (selectedEquation.getApproximation ());
	}

	/**
	 * @param f function to use for evaluation
	 * @param at the value of Tn to use in this evaluation
	 * @return the calculated Yn for specified Tn
	 * @throws Alert for errors
	 */
	public double evalSolutionFunction (AbstractFunction <Double> f, double at) throws Alert
	{
		if (f == null)
			SimpleScreenIO.alertError ("No solution function ('y') available");
		return f.toSimpleFunction ().eval (at);
	}
	public double evalLastSavedSolutionFunction (double at) throws Alert
	{
		return evalSolutionFunction (selectedEquation.getLastSavedSolution (), at);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.FormalActualParameters.Callbacks#takeAction(java.lang.String)
	 */
	@Override public void takeAction (String called)
	{
		if ( ! RELOAD.equals (called) ) getNextT0 (called);
		loadSymbols (); display.setItems (settings);
	}
	void getNextT0 (String operation)
	{
		try
		{
			Double Tn = selectedEquation.getCurrentEffectiveHigh ();
			if (BACK.equals (operation)) selectedEquation.discardLastSolution ();
			double T0 = SimpleScreenIO.requestNumericInput (null, "Enter T0", "Run Approximation For", Tn.toString ()).doubleValue ();
			selectedEquation.resetInitialConditions (T0, evalLastSavedSolutionFunction (T0));
		} catch (Alert alert) { alert.presentDialog (); }
	}
	static final String RELOAD = "Reload", BACK = "Back", NEXT = "Next";

	/**
	 * show map of formal/actual parameters
	 */
	public void showForm ()
	{
		display = new FormalActualParameters (this);
		display.addFormalActual ("Function", selectedEquation.getFunctionName ());
		display.addFormalActual ("Equation", selectedEquation.getFunctionBodyText ());
		display.addFormalActual ("Description", selectedEquation.getDescription ());
		display.addFormalActual ("Approximation", selectedEquation.getApproximation ());
		display.addItems (settings); display.showFrame ("Solution Approximation", ACTIONS);
	}
	protected FormalActualParameters display;

	/**
	 * verify approximation is available
	 * @throws Alert for errors
	 */
	public void checkEquation () throws Alert
	{
		if (selectedEquation.getApproximation ().isEmpty ())
		{
			SimpleScreenIO.alertError ("Approximation formula is not available");
		}
	}
	protected DifferentialEquationsManager.EquationProperties selectedEquation;

	/**
	 * find symbol values for identifiers
	 */
	public void loadSymbols ()
	{ selectedEquation.include (identifiers, "", settings); }
	protected Map <String, String> settings = new HashMap <> ();

	/**
	 * link alias symbols and show form
	 * @param selectedEquation equation that will display solution error metrics
	 * @throws Alert for errors
	 */
	public void init (DifferentialEquationsManager.EquationProperties selectedEquation) throws Alert
	{
		this.selectedEquation = selectedEquation;
		checkEquation ();
		loadSymbols ();
		showForm ();
	}

	/**
	 * constants required for form
	 */
	static final Set <String> identifiers = new HashSet <String> ();
	static final List <String> parameters = new ArrayList <String> ();
	static final String[] ACTIONS = new String[]{RELOAD, BACK, NEXT};

	static
	{

		/*
		 * standard parameters to RK approximation formulas
		 */
		identifiers.add ("Y0");
		identifiers.add ("T0");
		identifiers.add ("h");
		identifiers.add ("N");

		/*
		 * standard parameters to RK approximation functions
		 */
		parameters.add ("Tn");
		parameters.add ("Yn");

	}

}

/**
 * manage RK4 Formula Parameters
 */
class FormulaParameters extends RungeKutta.FormulaParameters <Double>
{

	/**
	 * @param settings map of properties from form
	 */
	FormulaParameters (Map <String, String> settings)
	{
		N	=  (int)
			   Double.parseDouble (settings.get ("N"));
		h	=  Double.parseDouble (settings.get ("h"));
		Y0  =  Double.parseDouble (settings.get ("Y0"));
		T0  =  Double.parseDouble (settings.get ("T0"));
		Tn	=	T0 + h * N;
	}

	/**
	 * @return the identified T0 value
	 */
	double getT0 () { return T0; }

	/**
	 * @return the effective hi computed and verified
	 * @throws Alert for CANCEL on form or invalid data entry
	 */
	double getTn () throws Alert
	{
		return SimpleScreenIO.requestNumericInput
		(
			null, "Enter Effective HI", "Effective Domain High", Tn.toString ()
		)
		.doubleValue ();
	}
	Double Tn;

}
