
package net.myorb.math.expressions;

import net.myorb.math.expressions.EvaluationControlI;
import net.myorb.math.expressions.commands.CommandSequence;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.*;
import net.myorb.math.*;

import java.util.List;

/**
 * control the sequence of steps for use of the expression evaluation objects
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class EvaluationControl<T> implements EvaluationControlI<T>
{


	/**
	 * the type manager must be identified for evaluation of atomic operations
	 * @param environment access to the evaluation environment
	 */
	public EvaluationControl (Environment<T> environment)
	{ this (environment, new SymbolTableManager<T> (environment), true); }
	public EvaluationControl (Environment<T> environment, SymbolTableManagerI<T> stabMgr)
	{
		this.stabMgr = stabMgr;
		this.environment = environment;
		this.symbols = environment.getSymbolMap ();
		this.valueManager = environment.getValueManager ();
		this.spaceManager = environment.getSpaceManager ();
		this.spaceManager.setEvaluationControl (this);
		this.initializeEngine ();
	}
	public EvaluationControl (Environment<T> environment, SymbolTableManagerI<T> stabMgr, boolean startGui)
	{
		this.stabMgr = stabMgr;
		this.environment = environment;
		this.symbols = environment.getSymbolMap ();
		this.valueManager = environment.getValueManager ();
		this.spaceManager = environment.getSpaceManager ();
		this.spaceManager.setEvaluationControl (this);
		this.initializeSymbolTable (); initializeEngine ();
		if (startGui) connectGui (environment);
	}
	protected Environment<T> environment;
	protected ValueManager<T> valueManager;
	protected ExpressionSpaceManager<T> spaceManager;
	protected SymbolTableManagerI<T> stabMgr;
	protected SymbolMap symbols;


	/**
	 * connect calculation environment to DisplayIO GUI objects
	 * @param environment the object controling the evaluation environment
	 */
	public void connectGui (Environment<T> environment)
	{
		guiSymbolMap = DisplayIO.connectConsole (toString (), 700, environment, this);
		environment.setOutStream (DisplayConsole.getStreamFor ("MainConsole", guiSymbolMap));
		environment.setConsoleWriter (DisplayConsole.getWriterFor ("MainConsole", guiSymbolMap));
		guiSymbolMap.put ("ExecutionEnvironment", environment);
		guiSymbolMap.put ("SymbolMap", symbols);
	}
	public DisplayConsole.StreamProperties getGuiMap () { return guiSymbolMap; }
	protected DisplayConsole.StreamProperties guiSymbolMap;


	/**
	 * get access to SymbolTableManager
	 * @return the SymbolTableManager object for this environment
	 */
	public SymbolTableManagerI<T> getSymbolTableManager () { return stabMgr; }


	/**
	 * import key symbols into the local symbol table
	 */
	public void initializeSymbolTable ()
	{
		symbols.addCoreOperators ();
		stabMgr.importFromTrigLibrary (new OptimizedMathLibrary<T> (spaceManager), symbols);
		stabMgr.importFromSpaceManager (symbols);
	}


	/**
	 * import functions from a high speed floating power/trig library
	 */
	public void useSpeedLibrary ()
	{
		stabMgr.importFromSpeedLibrary (speedLibrary = new HighSpeedMathLibrary (), symbols);
	}
	protected HighSpeedMathLibrary speedLibrary;


	/**
	 * import functions from the specified power library
	 * @param powerLibrary an object that implements the power library interface
	 */
	public void usePowerLibrary (PowerLibrary<T> powerLibrary)
	{
		stabMgr.importFromPowerLibrary (powerLibrary, symbols);
	}


	/**
	 * allocate an engine object that will operate in the specified space
	 */
	public void initializeEngine ()
	{ engine = new EvaluationEngine<T> (environment, this); }
	public EvaluationEngine<T> getEngine () { return engine; }
	protected EvaluationEngine<T> engine;


	/**
	 * execute streams of tokens
	 * @param tokens the list of tokens to execute
	 * @param processWithCheck TRUE = catch thrown exceptions
	 */
	public void run (List<TokenParser.TokenDescriptor> tokens, boolean processWithCheck)
	{
		int pos;
		List<TokenParser.TokenDescriptor> remaining = tokens, current;
		while (remaining != null)
		{
			for (pos = 0; pos < remaining.size (); pos++)
			{
				if (remaining.get (pos).isIdentifiedAs (OperatorNomenclature.END_OF_STATEMENT_DELIMITER)) break;
			}

			current = remaining.subList (0, pos);
			if (pos+1 >= remaining.size ()) remaining = null;
			else remaining = remaining.subList (pos + 1, remaining.size ());

			if (processWithCheck) engine.processWithCatch (current);
			else engine.process (current);
		}
	}


	/**
	 * parse source and execute parsed token stream
	 * @param source text of the expression to be executed
	 */
	public void execute (String source, boolean processWithCheck)
	{
		CommandSequence tokens =
			new CommandSequence (TokenParser.parse (new StringBuffer (source)));
		if (engine.dumpingRequested ()) { System.out.println (tokens); }
		run (tokens, processWithCheck);
	}
	public void execute (String source) { execute (source, true); }


	/**
	 * evaluate an expression
	 * @param source the text of the expression
	 * @return computed value
	 */
	public T evaluate (String source)
	{
		if (engine.dumpingRequested ()) System.out.println (source);
		execute (source, true); return engine.getEnvironment ().getValueStack ().popValue ();
	}


	/**
	 * find and output specified symbol
	 * @param symbol the name of the symbol
	 */
	public void dump (String symbol)
	{
		System.out.println (symbol + " = " + symbols.lookup (symbol));
	}


	/**
	 * read a symbol value from table
	 * @param symbol the name of the symbol
	 * @return the value of that symbol
	 * @throws Exception list error
	 */
	public T lookup (String symbol) throws Exception
	{
		ValueManager.GenericValue v = symbols.getValue (symbols.lookup (symbol));
		return valueManager.toDiscrete (valueManager.check (v));
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return "CALCLIB";
	}


}

