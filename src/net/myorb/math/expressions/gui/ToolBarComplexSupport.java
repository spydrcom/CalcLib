
package net.myorb.math.expressions.gui;

import net.myorb.math.expressions.gui.SegmentConfig;
import net.myorb.math.expressions.gui.DisplayConsole;

import net.myorb.math.computational.IterativeSeriesConvergence;
import net.myorb.math.computational.IterativeIntegralApproximation;
import net.myorb.math.computational.IterativeRootApproximation;

import net.myorb.math.expressions.gui.DisplayIO.CommandProcessor;
import net.myorb.math.expressions.symbols.AbstractFunction;
import net.myorb.math.expressions.symbols.DefinedFunction;
import net.myorb.math.expressions.evaluationstates.*;
import net.myorb.math.expressions.*;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.MenuManager;

import javax.swing.KeyStroke;

import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;

import java.io.PrintStream;
import java.util.Map;

/**
 * full complex menu item implementations
 * @author Michael Druckman
 */
public class ToolBarComplexSupport extends ToolBarSelectionSupport
{
	ToolBarComplexSupport
	(String command, DisplayIO.CommandProcessor processor, String tableName, Component c)
	{
		super (command, processor, tableName, c);
	}
}


/**
 * process function of 2 parameters where order does not matter
 */
class CommutativeBinaryFunctionAssignmentCommand extends DualSelectedAssignmentCommand
{
	CommutativeBinaryFunctionAssignmentCommand
	(String command, DisplayIO.CommandProcessor processor, Component c)
	{
		super (command, "SymbolTable", processor, c);
	}
}


/**
 * provide for ordering of choices selected in GUI
 */
class OrderedDualSelectedAssignmentCommand extends DualSelectedAssignmentCommand
{

	public OrderedDualSelectedAssignmentCommand
	(String command, String tableName, CommandProcessor processor, Component c)
	{
		super(command, tableName, processor, c);
	}

	/**
	 * @return permutations of ordering of parameters
	 */
	String[] getChoices ()
	{
		String parameters[];
		if ((parameters = getValuesAt (getTable (), 0)) == null) return null;
		String resultSymbol1 = formatParameters (parameters[0], parameters[1]);
		String resultSymbol2 = formatParameters (parameters[1], parameters[0]);
		return new String[]{resultSymbol1, resultSymbol2};
	}

	/**
	 * @param choices the list of choices to present to user
	 * @return the chosen item
	 */
	Object choose (String[] choices)
	{
		return chooseFromList ("Choose parameter order", "Specify Order", choices);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.DualSelectedAssignmentCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String choices[];
		if ((choices = getChoices ()) == null) return;
		Object chosenElement = checkForCancel (choose (choices));
		String formattedParameters = chosenElement.toString ().replace (separator, ",");
		processor.execute (chosenElement + " = " + command + "(" + formattedParameters + ")");
	}

}


/**
 * execute command with ordered pair of parameters
 */
class OrderedDualSelectedCommand extends OrderedDualSelectedAssignmentCommand
{

	public OrderedDualSelectedCommand(String command, String tableName, CommandProcessor processor, Component c)
	{
		super(command, tableName, processor, c);
		separator = ",";
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.OrderedDualSelectedAssignmentCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String choices[] = getChoices (); checkForCancel (choices);
		Object chosen = choose (choices); checkForCancel (chosen);
		processor.execute (command + " " + chosen.toString ());
	}

}


/**
 * GUI representation of FITHARMONIC command
 */
class HarmonicRegression extends OrderedDualSelectedAssignmentCommand
{

	public HarmonicRegression (CommandProcessor processor, Component c)
	{
		super("FITHARMONIC", "SymbolTable", processor, c);
	}

	/**
	 * @return omega multiplier for scaling of cycle period
	 */
	public Object getOmega ()
	{
		return getSimpleTextResponse ("Specify Omega", "Harmonic Regression");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.OrderedDualSelectedAssignmentCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String choices[] = getChoices (); checkForCancel (choices);
		Object chosenElement = choose (choices); checkForCancel (chosenElement);
		Object omegaFrequencyMultiplier = getOmega (); checkForCancel (omegaFrequencyMultiplier);
		String formattedParameters = chosenElement.toString ().replace (separator, ",");

		processor.execute
		(
			chosenElement + " = " + command + "(" + formattedParameters + "," + omegaFrequencyMultiplier+ ")"
		);
	}

}


/**
 * harmonic series established by GUI input
 */
class SeriesRegression extends HarmonicRegression
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedAssignmentCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String parameter = getValueAt (getTable (), 0, true); checkForCancel (parameter);
		Object omegaFrequencyMultiplier = getOmega (); checkForCancel (omegaFrequencyMultiplier);

		processor.execute
		(
			parameter + "_FITHARMONIC = FITHARMONIC (" + parameter + "," + omegaFrequencyMultiplier + ")"
		);
	}

	public SeriesRegression (CommandProcessor processor, Component c) { super(processor, c); }

}


/**
 * use EIG command to establish eigen value/vector from GUI input
 */
class EigenvalueSpecialCaseCommand extends SelectedAssignmentCommand
{

	public EigenvalueSpecialCaseCommand
	(String command, String tableName, CommandProcessor processor, Component c)
	{ super(command, tableName, processor, true, c); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedAssignmentCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String parameter;
		checkForCancel (parameter = getValueAt (getTable (), 0, true));
		String parameters = parameter + "," + parameter + "_EIGENVECTOR";
		processor.execute (parameter + "_EIGENVALUE = EIG(" + parameters + ")");
	}

}


/**
 * display equation rendered from MathML
 */
class RenderFunction extends FunctionSelection implements MenuManager.MnemonicAvailable
{

	RenderFunction (CommandProcessor processor, Component c)
	{
		super (processor, c);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		for (String funtionName : getSelectedFunctionNames ())
		{ processor.execute ("RENDERF " + funtionName); }
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	public char getMnemonic () { return 'R'; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Render"; }

}


/**
 * execute PRETTYPRINT from GUI for symbol or function
 */
class PrettyPrint extends SelectedCommand
{

	public PrettyPrint (String tableName, DisplayIO.CommandProcessor processor, Component parent)
	{
		super ("PRETTYPRINT ", tableName, processor, true, parent);
		this.parent = parent;
	}
	Component parent;

	/**
	 * @return number of digits for display
	 */
	Object getResponse ()
	{ return chooseFromList ("", "Precision to be displayed", choices); }
	String[] choices = new String[]{"5", "10", "15", ""};

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		Object symbol = checkForCancel (getValueAt (getTable (), 0, true));
		Object precision = checkForCancel (precision = getResponse ());
		processor.execute (command + symbol + " " + precision);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "PrettyPrint"; }

}
class PrettyPrintSymbol extends PrettyPrint  implements MenuManager.HotKeyAvailable
{
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_T, ActionEvent.ALT_MASK); }
	public PrettyPrintSymbol (DisplayIO.CommandProcessor processor, Component parent)
	{ super ("SymbolTable", processor, parent); }
}
class PrettyPrintFunction extends PrettyPrint  implements MenuManager.HotKeyAvailable
{
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_T, ActionEvent.CTRL_MASK); }
	public PrettyPrintFunction (DisplayIO.CommandProcessor processor, Component parent)
	{ super ("FunctionTable", processor, parent); }
}

/**
 * polar chart generation
 */
class PolarCommand extends MultipleSelectionCommand
{

	PolarCommand
		(
			String command, String[] prompts, TrackingList buttonTrackingList,
			DisplayIO.CommandProcessor processor, Component c
		)
	{
		super (command, "SymbolTable", processor, true, c);
		this.buttonTrackingList = buttonTrackingList;
		this.prompts = prompts;
	}
	String prompts[]; TrackingList buttonTrackingList;

	/**
	 * provide choices for user selection
	 * @param prompt the prompt to the user for the selection
	 * @param choices the choice from which user is to choose
	 * @param buffer choice will be appended to buffer
	 */
	void choose (String prompt, String[] choices, StringBuffer buffer)
	{
		Object choice = chooseFromList
			(prompt, "Plot Parameters", choices);
		if (choice != null) buffer.append (choice).append (" ");
		else terminate ("Plot canceled");
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		buttonTrackingList.checkType ();

		StringBuffer buffer = new StringBuffer (command).append (" ");
		String[] selections = getValuesAt (getTable (), 0);

		if (selections != null)
		{
			for (String prompt : prompts) choose (prompt, selections, buffer);
			new CommandThread (processor, buffer.toString ());
		}
	}

}


/**
 * differentiation rules applied by GUI
 */
class AppliedDifferentiationRules extends FunctionSelection implements ActionListener, MenuManager.HotKeyAvailable
{

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Apply Rule (Differentiation)"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_D, ActionEvent.CTRL_MASK); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		String ruleDeclaration = null;
		String rule = checkForCancel (getRule ()).toString ();
		String[] functions = getSelectedFunctionNames (); checkForCancel (functions);
		Object nameToBeDeclared = checkForCancel (nameForFunction ());

		if (useSimpleDeclaration)
		{
			String functionNames = "";
			for (String n : functions) functionNames += n + " ";
			ruleDeclaration = functionNames;
		}
		else
		{
			DifferentiationRules.Rules r = DifferentiationRules.getRule (rule);
			if (r == DifferentiationRules.Rules.RECIPROCAL_RULE)
			{
				if (functions.length != 1)
					terminate ("Reciprocal rule requires a single function");
				ruleDeclaration = "{ 1 / " + functions[0] + "() }";
			}
			else if (r == DifferentiationRules.Rules.SUM_RULE)
			{ formatSumRule (nameToBeDeclared.toString (), functions); return; }
			else if (functions.length != 2) terminate ("Rule requires exactly two functions selected");
			else ruleDeclaration = formatRule (r, reordered (r, functions));
		}

		processRuleCommand (nameToBeDeclared, rule, ruleDeclaration);
	}
	void processRuleCommand (Object nameToBeDeclared, String rule, String ruleDeclaration)
	{
		processor.execute
		("!% " + nameToBeDeclared + "(x) <>= " + rule + " " + ruleDeclaration);
		refresh ();
	}
	String[] reordered (DifferentiationRules.Rules r, String[] functions)
	{
		boolean reorder;

		switch (r)
		{
			case QUOTIENT_RULE:	reorder = mustReorder (functions[0], " / ", functions[1]); break;
			case INVERSE_RULE:	reorder = mustReorder (functions[0], " ^(-1)= ", functions[1]); break;
			case CHAIN_RULE:	reorder = mustReorder (functions[0], " => ", functions[1]); break;
			case POWER_RULE:	reorder = mustReorder (functions[0], " ^ ", functions[1]); break;
			default: return functions;
		}

		if (reorder) return new String[]{functions[1], functions[0]};
		else return functions;
	}
	boolean mustReorder (String choice1, String op, String choice2)
	{
		String[] choices = new String[]{choice1 + op + choice2, choice2 + op + choice1};
		Object choice = chooseFromList ("Select operation order", "Equation Ordering", choices);
		return checkForCancel (choice).toString ().startsWith (choice2 + " ");
	}
	String formatRule (DifferentiationRules.Rules r, String[] functions)
	{
		switch (r)
		{
			case POWER_RULE:	return "{ " + functions[0] + "() ^ " + functions[1] + "() }";
			case PRODUCT_RULE:	return "{ " + functions[0] + "() * " + functions[1] + "() }";
			case QUOTIENT_RULE:	return "{ " + functions[0] + "() / " + functions[1] + "() }";
			case INVERSE_RULE:	return "{ " + functions[0] + "() ^-1= " + functions[1] + "() }";
			case CHAIN_RULE:	return "{ " + functions[0] + " (" + functions[1] + "()) }";
			default: terminate ("Internal error"); return null;
		}
	}
	void formatSumRule (String name, String[] f)
	{
		int l = f.length;
		// { - a'() + b''() - c() + d() }
		if (l < 2) terminate ("Too few functions selected");
		formatSumRuleUsingForm (name, f);
		//formatSimpleSumRule (name, f);
	}
	void formatSimpleSumRule (String name, String[] f)
	{
		String terms = f[0]+"()";
		for (int i=1; i<f.length; i++) terms += " + " + f[i] + "()";
		formatSumRuleCommand (name, terms);
	}
	void formatSumRuleUsingForm (String name, String[] f)
	{
		SequenceFormPublisher publisher =
			new SequenceFormPublisher (name, f);
		SequenceForm.requestCoefficientsForFunctionSeries (f, publisher);
	}
	public void formatSumRuleCommand (String name, String terms)
	{ processRuleCommand (name, "SUM", "{ " + terms + " }"); }
	boolean useSimpleDeclaration = false;

	/**
	 * publisher object for sequence form
	 */
	class SequenceFormPublisher implements SequenceForm.Publisher
	{
		public void publish(SequenceForm.TextItemList items)
		{
			String terms = multiplier (items.get(0), "", f[0]+"()");
			for (int i=1; i<f.length; i++) terms += multiplier (items.get(i), "  +  ", f[i] + "()");
			formatSumRuleCommand (name, terms);
		}
		String multiplier (String value, String add, String call)
		{
			double dblValue; String result;
			if ((dblValue = Double.parseDouble (value)) == 0) return "";
			if (dblValue <= 0) { result =  "  -  "; dblValue = -dblValue; } else result =  add;
			if (dblValue != 1) result += dblValue + " ";
			return result + call;
		}
		SequenceFormPublisher (String name, String[] f)
		{ this.name = name; this.f = f; }
		String name, f[];
	}

	/**
	 * @return POWER_RULE | PRODUCT_RULE | QUOTIENT_RULE | ...
	 */
	Object getRule ()
	{
		return chooseFromList
		(
			"Select Rule", "Differentiation Rule to apply",
			DifferentiationRules.listOfRules ()
		);
	}

	public AppliedDifferentiationRules
	(CommandProcessor processor, Component c)
	{ super (processor, c); }

}


/**
 * integration transform applied by GUI
 */
class AppliedIntegrationTransform extends FunctionSelection implements ActionListener, MenuManager.HotKeyAvailable
{

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Apply Transform (Integration)"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_I, ActionEvent.CTRL_MASK); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object transform = checkForCancel (getTransform ());
		SymbolMap.Named function = getSelectedFunction (); checkForCancel (function);
		Object kernelOrInverse = checkForCancel (getType ());
		Object name = checkForCancel (nameForFunction ());

		String f = function.getName(), t = f + "_TRANSFORM", p = getParameter (function);
		String v = t + " " + transform + " " + kernelOrInverse + " 0 1 0.01";

		processor.execute ("!^ " + t + "(" + p + ")" + " = " + f);
		processor.execute ("!% " + name + "(" + p + ") ^= " + v);
		refresh ();
	}

	/**
	 * @return KERNEL | INVERSE
	 */
	Object getType ()
	{
		return chooseFromList
		(
			"Select Type", "Kernel or Inverse function",
			new String[]{"KERNEL", "INVERSE"}
		);
	}

	/**
	 * @return LAPLACE | MELLIN | FOURIER | ...
	 */
	Object getTransform ()
	{
		return chooseFromList
		(
			"Select Transform", "Integration Transforrm to apply",
			TraditionalTransforms.listOfTransforms ()
		);
	}

	public AppliedIntegrationTransform (CommandProcessor processor, Component c)
	{
		super (processor, c);
	}

}


/**
 * support methods for iterative approximation algorithms
 * @param <T> type on which operations are to be executed
 */
abstract class IterativeApproximation<T> extends LimitedFunctionSelection<T>
	implements IntervalForm.Publisher, Runnable
{

	public static boolean RENDERING = true;

	/**
	 * show form to request range parameters
	 * @param title the title for the form frame
	 * @param defaults the values to show as defaults in form fields
	 */
	protected void inputFromForm (String title, String[] defaults)
	{ new IntervalForm (title, this, new String[]{parameterName}, defaults); }

	/**
	 * @return a description of the range requested
	 */
	protected TypedRangeDescription.TypedRangeProperties <T> getRangeDescription ()
	{
		Environment <T> env =
				EnvironmentCore.getExecutionEnvironment (processor);
		TokenParser.TokenSequence tokens = TokenParser.parse (rangeDescription);
		return new ExtendedArrayFeatures <T> ().getArrayDescriptor (tokens, 0, env);
	}
	private StringBuffer rangeDescription = null;

	/**
	 * @param descriptor the bounds of the range in CalcLib notation
	 */
	public abstract void renderOverRange (String descriptor);

	/**
	 * start the approximation run
	 */
	protected void runInBackground ()
	{
		SimpleScreenIO.startBackgroundTask (this);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.IntervalForm.Publisher#publish(java.lang.String)
	 */
	public void publish (String descriptor)
	{
		rangeDescription = new StringBuffer (descriptor);
		renderOverRange (descriptor);
		runInBackground ();
	}

	/**
	 * @param title the title for the frame
	 * @return a new screen display frame
	 */
	protected PrintStream newDisplay (String title)
	{
		return DisplayConsole.showConsole (title, new DisplayConsole.StreamProperties(), 700);
	}

	protected IterativeApproximation (CommandProcessor processor, Component c)
	{ super (processor, c); }

}


/**
 * show iterative integration calculations
 */
class ApproximateIntegral<T> extends IterativeApproximation<T> implements MenuManager.HotKeyAvailable, ActionListener
{

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Approximate Integral Iteratively"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_A, ActionEvent.CTRL_MASK); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		getLimitedSelectedFunction ();

		Object multiplier = checkForCancel
				(getSimpleTextResponse ("Enter Area Multiplier", "Base Area Multiple", "1"));
		mul = Double.parseDouble (multiplier.toString ()); areaMultiplier = mul==1? "": mul + " * ";

		if (RENDERING) render ("INTEGRALI"); // the indefinite form of the integral before the range is applied
		inputFromForm ("Function Integration Parameters", IntervalForm.GRAPH_VALUE_DEFAULTS);
	}
	protected String areaMultiplier = "";
	protected double mul = 1;

	/**
	 * render the integral being approximated
	 * @param operator the INTEGRAL with/without range defined
	 */
	public void render (String operator)
	{
		processor.execute
		(
			"RENDER " + areaMultiplier + operator +
			" ( "
					+ profile + " * <*> " + parameterName +
			" ) "														// use internal <*> operator to improve render
		);
	}
	public void renderOverRange (String descriptor)
	{
		if (RENDERING) render ("INTEGRAL " + descriptor);				// the definite form with the range applied
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		Object iterations = checkForCancel
			(getSimpleTextResponse ("Enter Iteration Count", "Refinement Iterations", "23"));
		runApproximation (Integer.parseInt (iterations.toString ()));
	}

	/**
	 * run in background to avoid performance hit on GUI
	 * @param iterations the number of iterations for approximation
	 */
	public void runApproximation (int iterations)
	{
		getIterativeIntegralApproximation ().execute (iterations, mul);
	}

	/**
	 * @return the integral approximation tool implementation
	 */
	public IterativeIntegralApproximation<T> getIterativeIntegralApproximation ()
	{
		return new IterativeIntegralApproximation<T> (getRangeDescription (), getIdentifiedFunction (), false);
	}

	public ApproximateIntegral (CommandProcessor processor, Component c) { super (processor, c); }

}


/**
 * show iterative integration calculations
 */
class ApproximateSeries<T> extends IterativeApproximation<T> implements MenuManager.HotKeyAvailable, ActionListener
{

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Approximate Series Convergence"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_S, ActionEvent.CTRL_MASK); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		getLimitedSelectedFunction ();

		inputFromForm
		(
			"Series Interval",
			IntervalForm.SUMMATION_VALUE_DEFAULTS
		);
	}

	/**
	 * render the series being approximated
	 * @param operator the SIGMA notation with bounds
	 */
	public void renderOverRange (String descriptor)
	{
		if (RENDERING)
		{
			processor.execute
			(
				"RENDER SUMMATION " + descriptor +
				" ( "
						+ profile +
				" ) "
			);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		runApproximation ();
	}

	/**
	 * run in background to avoid performance hit on GUI
	 */
	public void runApproximation ()
	{
		getIterativeSeriesConvergence ().executeIterations ();
	}

	/**
	 * @return the series convergence tool implementation
	 */
	public IterativeSeriesConvergence<T> getIterativeSeriesConvergence ()
	{ return new IterativeSeriesConvergence<T> (getRangeDescription (), getIdentifiedFunction ()); }

	public ApproximateSeries (CommandProcessor processor, Component c) { super (processor, c); }

}


/**
 * show iterative root approximation calculations
 */
class ApproximateRoot<T> extends IterativeApproximation<T> implements MenuManager.HotKeyAvailable, ActionListener
{

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Newton Root Approximation"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_N, ActionEvent.CTRL_MASK); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		getLimitedSelectedFunction ();
		getLimitedSelectedDerivative ();
		getLimitedSelectedSecondDerivative ();
		runInBackground ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.IterativeApproximation#renderOverRange(java.lang.String)
	 */
	public void renderOverRange (String descriptor) {}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		Object initialRoot = checkForCancel
		(getSimpleTextResponse ("Enter Starting Point", "Enter Initial Root Approximation", "1"));
		Object iterationCount = checkForCancel (getSimpleTextResponse ("Enter Iteration Count", "Refinement Iterations", "20"));
		runApproximation (Double.parseDouble (initialRoot.toString ()), Integer.parseInt (iterationCount.toString ()));
	}

	/**
	 * run in background to avoid performance hit on GUI
	 */
	public void runApproximation (double initialRoot, int iterationCount)
	{
		getIterativeSeriesConvergence ().executeIterations (initialRoot, iterationCount);
	}

	/**
	 * @return the series convergence tool implementation
	 */
	public IterativeRootApproximation<T> getIterativeSeriesConvergence ()
	{
		return new IterativeRootApproximation<T>
		(
			getIdentifiedFunction (),
			getIdentifiedDerivative (),
			getIdentifiedSecondDerivative ()
		);
	}

	public ApproximateRoot (CommandProcessor processor, Component c) { super (processor, c); }

}


/**
 * arrays converted to series function coefficients by GUI commands
 */
class PublishAs extends MultipleSelectionCommand implements MenuManager.MnemonicAvailable
{

	public PublishAs (DisplayIO.CommandProcessor processor, Component parent) { this ("PublishAs ", "SymbolTable", processor, parent);}
	public PublishAs (String command, String tableName, DisplayIO.CommandProcessor processor, Component parent)
	{ super (command, tableName, processor, true, parent); }

	/**
	 * identify type of function being published
	 * @return selected choice
	 */
	Object getTypeResponse ()
	{ return checkForCancel (chooseFromList ("Select Type", "Type of Function", choices)); }
	static String[] choices = new String[]
	{
		"Polynomial +*^", "Chebyshev @*^", "Exponential *^#",
		"Harmonic Series +#*", "Array Extrapolation @#", "Spline VC31", "DCT  @"
	};

	/**
	 * identify function by name
	 * @return the name specified by user
	 */
	Object getNameResponse ()
	{
		return checkForCancel (getSimpleTextResponse ("Specify Function Name", "Name For Function"));
	}

	/**
	 * select pattern for function
	 *  [Odd, Even, Non-Specific] are options
	 * @return the selected option or NULL for cancel
	 */
	Object getFunctionPattern ()
	{ return checkForCancel (chooseFromList ("Select Pattern", "Pattern of Function", patterns, "Non-Specific")); }
	static String[] patterns = new String[]{"Odd", "Even", "Non-Specific"};

	/**
	 * @return last 3 characters of type query response
	 */
	String getOperator ()
	{
		Object type = getTypeResponse (); String operator = type.toString ();
		return operator.substring (operator.length() - 3);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		String symbols[];
		checkForCancel (symbols = getValuesAt (getTable (), 0));
		processTransformOrSpline (getNameResponse ().toString (), symbols);
		refresh ();
	}

	/**
	 * multiple symbols selected indicates hetrogeneous spline
	 * @param name the name of the function being declared
	 * @param symbols the symbols selected in the GUI
	 */
	void processTransformOrSpline (String name, String symbols[])
	{
		if (symbols.length == 1)
		{
			// single function selection is either transform or spline
			processTransformOrSpline (name, symbols[0]);
		}
		else
		{
			// multi-segment spline of hetrogeneous segment types
			hetrogeneousSpline (name, symbols);
		}
	}

	/**
	 * special processing if VC31 is selected operation
	 * @param name the name of the function being declared
	 * @param symbol the symbol selected in the GUI
	 */
	void processTransformOrSpline (String name, String symbol)
	{ processTransformOrSpline (name, symbol, getOperator ()); }
	void processTransformOrSpline (String name, String symbol, String operator)
	{
		if (operator.equals ("C31"))	//TODO: C31?  verify?
		{
			// VC31 spline passing coefficient matrix to BIF
			vc31 (name, symbol, checkForCancel (getFunctionPattern ()));
		}
		else
		{
			processTransformOrOperator (name, symbol, operator, processor);
		}
	}

	/**
	 * declare transform or simple function depending on operator
	 * @param name the name of the function being declared
	 * @param symbol the symbol selected in the GUI
	 * @param operator defining action of function
	 * @param processor command interface
	 */
	static void processTransformOrOperator
		(
			String name, String symbol, String operator,
			DisplayIO.CommandProcessor processor
		)
	{
		String transform = operator.trim () + "=";
		if (OperatorNomenclature.TRANFORM_MAP.get (transform) != null)
		{
			// function defined as transform
			processor.execute ("!% " + name + "(x) " + transform + " " + symbol);
		}
		else
		{
			// function defined based on operator
			processor.execute ("!! " + name + "(x) = " + symbol + " " + operator + " x");
		}
	}

	/**
	 * define a spline function
	 *  built from multiple function constraints
	 * @param name the name assigned to the function
	 * @param symbols the symbols defining segments
	 */
	void hetrogeneousSpline (String name, String[] symbols)
	{
		String sep = ""; StringBuffer list = new StringBuffer ();
		for (String s : symbols) { list.append (sep).append (s); sep = ", "; }
		processor.execute ("!$ " + name + "(x) = ( " + list + " )");
	}

	/**
	 * execute function definition for VC31 spline
	 * @param name the name assigned to the function
	 * @param spline matrix of segment coefficients
	 * @param pattern user selected pattern
	 */
	void vc31 (String name, String spline, Object pattern)
	{
		Object base = "0"; boolean isOdd = false;
		if (pattern.equals ("Non-Specific")) { base = getBaseResponse (); } else isOdd = pattern.equals ("Odd");
		String definition = "!! " + name + "(x) = EVALSPLINE (" + spline + ", " + base + ", abs(x))";
		processor.execute (isOdd? definition + " * sgn(x)": definition);
	}

	/**
	 * get the value of the lowest knot for the spline
	 * @return the values entered at the request
	 */
	Object getBaseResponse ()
	{
		return checkForCancel (getSimpleTextResponse ("Specify Function Base", "Base For Function", "0"));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.IntervalGenerator#getMnemonic()
	 */
	public char getMnemonic () { return 'F'; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "To Function"; }

}


/**
 * processing for series coefficients entered using sequence GUI
 */
class SeriesDecl extends SequenceDecls implements ActionListener, SequenceForm.Publisher, MenuManager.MnemonicAvailable
{

	SeriesDecl
	(DisplayIO.CommandProcessor processor, Component parent)
	{ super ("New Series", processor, parent); setPublisher (this); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SequenceForm.Publisher#publish(java.util.List)
	 */
	public void publish (SequenceForm.TextItemList items)
	{
		PublishAs publisher = new PublishAs (processor, parent);
		String name = getNewName (), seriesCoefficients = name + "_COEFFICIENTS";
		setSymbol (seriesCoefficients, items);

		publisher.processTransformOrSpline
			(name, seriesCoefficients);
		refresh ();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		showForm ("Enter Coefficients ", "Create New Series");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.IntervalGenerator#getMnemonic()
	 */
	public char getMnemonic () { return 'N'; }

}


/**
 * formatter for GRAPH commands
 */
class FunctionProcessingSupportFormatters
{

	/**
	 * function profiles for multi-plot
	 * @param functionNames names of the functions
	 * @param parameter the parameter profile
	 * @return fully formatted list
	 */
	private static String formattedFunctionCalls (String[] functionNames, String parameter)
	{
		String functions = functionNames[0];
		for (int i=1; i<functionNames.length; i++)
		{ functions += parameter + ", " + functionNames[i]; }
		return functions + parameter;
	}

	/**
	 * format as f1(x), f2(x), ...
	 * @param functonNames names of the functions
	 * @param parameterName name of the parameter to use
	 * @return the formatted text
	 */
	public static String formatFunctionCalls (String[] functonNames, String parameterName)
	{
		String parameterProfile = "(" + parameterName + ")";
		return formattedFunctionCalls (functonNames, parameterProfile);
	}

	/**
	 * PLOTF command formatter
	 * @param function name of the function
	 * @param descriptor description of array
	 * @return single PLOTF command
	 */
	public static String singlePlotCommand (String function, String descriptor)
	{
		return OperatorNomenclature.PLOTF_KEYWORD + " " + function + " " + descriptor;
	}

	/**
	 * GRAPH multi-plot formatter
	 * @param descriptor description of array
	 * @param functions formatted list of functions
	 * @return multi-plot GRAPH command
	 */
	public static String multiPlotCommand (String descriptor, String functions)
	{
		return OperatorNomenclature.GRAPH_KEYWORD + " " + descriptor + " ( " + functions + " )";
	}

}

/**
 * low level processing for function plot support
 */
abstract class FunctionProcessingSupportProperties
	extends MultipleSelectionCommand implements IntervalForm.Publisher
{
	
	FunctionProcessingSupportProperties
	(DisplayIO.CommandProcessor processor, Component parent)
	{ super ("Plot Function", "FunctionTable", processor, true, parent); }

	/**
	 * evaluate function for PLOTF
	 * @param selectedFunction the name of the selected function
	 * @return name of formal parameter
	 */
	@SuppressWarnings("rawtypes")
	private String evaluateFunction (String selectedFunction)
	{
		SymbolMap.Named symbol; AbstractFunction abstractFunction;
		symbol = EnvironmentCore.getSymbolMap (processor).lookup (selectedFunction);
		(abstractFunction = DefinedFunction.verifyAbstractFunction (symbol)).copyConstraints (FORM_DEFAULTS);
		if (abstractFunction.parameterCount () != 1) { terminate ("Function must have exactly one parameter"); }
		return abstractFunction.getParameterNames ().get (0).toString ();
	}
	protected String[] FORM_DEFAULTS = IntervalForm.GRAPH_VALUE_DEFAULTS;

	protected String formattedFunctionCalls ()
	{ return FunctionProcessingSupportFormatters.formatFunctionCalls (selectedFunctions, form.getSelectedIdentifier ()); }
	protected boolean isSingleFunction () { return selectedFunctions != null && selectedFunctions.length == 1; }
	protected String singletonFunctionName () { return selectedFunctions[0]; }

	/**
	 * read function table selections and present interval form
	 */
	protected void collectFunctionSelectionData ()
	{
		selectedFunctions = getValuesAt (getTable (), 0);
		if (selectedFunctions == null) terminate ("No functions selected");
	}
	protected void specificallyUseFunction (String named)
	{
		selectedFunctions = new String[]{named};
	}
	private String selectedFunctions[];

	/**
	 * show interval form for user input
	 * @param defaults the LO/HI range default values
	 */
	protected void presentIntervalForm (String[] defaults)
	{
		String parameterName = null;
		if (isSingleFunction ()) { parameterName = evaluateFunction (singletonFunctionName ()); }
		form = new IntervalForm ("Function Plot Parameters", this, IntervalForm.GRAPH_ID_DEFAULTS, defaults);
		form.setIdentifier (parameterName);
	}
	protected void presentIntervalForm () { presentIntervalForm (FORM_DEFAULTS); }
	private IntervalForm form;
}

/**
 * plot command formatters
 */
abstract class FunctionProcessingSupport extends FunctionProcessingSupportProperties
{
	
	FunctionProcessingSupport
	(DisplayIO.CommandProcessor processor, Component parent)
	{ super (processor, parent); }

	/**
	 * @param descriptor description of the array
	 * @return formatted single plot command
	 */
	private String singlePlotCommand (String descriptor)
	{
		return FunctionProcessingSupportFormatters.singlePlotCommand (singletonFunctionName (), descriptor);
	}

	/**
	 * @param descriptor description of the array
	 * @return formatted multi plot command
	 */
	private String multiPlotCommand (String descriptor)
	{
		return FunctionProcessingSupportFormatters.multiPlotCommand (descriptor, formattedFunctionCalls ());
	}

	/**
	 * plot format selection and generation
	 */
	public class Processor implements Runnable
	{
		public void run ()
		{
			processor.execute
			(
				isSingleFunction ()?
				singlePlotCommand (descriptor):
				multiPlotCommand (descriptor)
			);
		}
		Processor (String descriptor)
		{ this.descriptor = descriptor; }
		String descriptor;
	}

}

/**
 * function plot generation using interval GUI
 */
class FunctionPlot extends FunctionProcessingSupport
	implements IntervalForm.Publisher, MenuManager.HotKeyAvailable
{

	FunctionPlot
	(DisplayIO.CommandProcessor processor, Component parent)
	{ super (processor, parent); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		collectFunctionSelectionData ();			// set selected functions list
		presentIntervalForm ();						// request array data from user
	}

	/**
	 * @param functionName the specifically requested function
	 */
	public void plotSpecifically (String functionName)
	{
		specificallyUseFunction (functionName);
		presentIntervalForm ();
	}
	public void plotSpecifically (String functionName, Map <String, String> usingParameters)
	{
		specificallyUseFunction (functionName);
		String lo = usingParameters.get ("LO"), hi = usingParameters.get ("HI"),
						delta = usingParameters.get ("DELTA");
		String[] defaults = new String [] {lo, hi, delta};
		presentIntervalForm (defaults);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.IntervalForm.Publisher#publish(java.lang.String)
	 */
	public void publish (String descriptor)
	{
		new SegmentConfig (new Processor (descriptor));
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_G, ActionEvent.CTRL_MASK); }

}

/**
 * start spline tool for specified function function
 */
class SplineToolCommand extends SelectedCommand implements MenuManager.HotKeyAvailable
{

	SplineToolCommand
	(DisplayIO.CommandProcessor processor, Component parent)
	{ super ("Spline Tool ", "FunctionTable", processor, true, null); }

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_T, ActionEvent.CTRL_MASK); }

}

/**
 * intervals for calculations or functions
 */
class IntervalGenerator extends ToolBarComplexSupport implements IntervalForm.Publisher, ActionListener, MenuManager.MnemonicAvailable
{

	IntervalGenerator
	(DisplayIO.CommandProcessor processor, Component parent)
	{ super ("INTERVAL", processor, "SymbolTable", parent); }
	public char getMnemonic () { return 'I'; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{ form = new IntervalForm ("Interval Parameters", this, IntervalForm.SUMMATION_ID_DEFAULTS, IntervalForm.SUMMATION_VALUE_DEFAULTS); }
	protected IntervalForm form;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SequenceForm.Publisher#publish(java.util.List)
	 */
	public void publish (String descriptor)
	{
		Object expression = getSimpleTextResponse ("Enter Expression", "Expression for Elements", "");
		String intervalExpression = descriptor + " ( " + expression + " )";
		assign (intervalExpression); refresh ();
	}

	/**
	 * cause expression to be assigned as a symbol with the resulting value
	 *  or alternately as the result of a function with a given name and parameter profile
	 * @param intervalExpression the text of the expression built
	 */
	void assign (String intervalExpression) {}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Summation"; }

}
class IntervalArray extends IntervalGenerator
{

	IntervalArray
	(DisplayIO.CommandProcessor processor, Component parent)
	{ super (processor, parent); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.Summation#assign(java.lang.String)
	 */
	void assign (String intervalExpression)
	{
		Object assignTo = getSimpleTextResponse ("Assign To", "Assign Interval Array to Symbol", "");
		processor.execute (assignTo + " = " + intervalExpression);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Interval Array"; }

}
class SummationEquation extends IntervalGenerator
{

	SummationEquation
	(DisplayIO.CommandProcessor processor, Component parent)
	{ super (processor, parent); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.IntervalGenerator#getMnemonic()
	 */
	public char getMnemonic () { return 'S'; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.IntervalGenerator#assign(java.lang.String)
	 */
	void assign (String intervalExpression)
	{
		Object assignTo = getSimpleTextResponse ("Assign To", "Assign Summation Result to Symbol", "");
		processor.execute (assignTo + " = " + "SIGMA " + intervalExpression);
	}

}
class SummationFunction extends IntervalGenerator
{

	SummationFunction
	(DisplayIO.CommandProcessor processor, Component parent)
	{ super (processor, parent); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.IntervalGenerator#getMnemonic()
	 */
	public char getMnemonic () { return 'S'; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.IntervalGenerator#assign(java.lang.String)
	 */
	void assign (String intervalExpression)
	{
		Object assignTo = getSimpleTextResponse ("Enter Function Profile", "Define Summation Result as Function", "");
		processor.execute ("!! " + assignTo + " = " + "SIGMA " + intervalExpression);
	}

}


