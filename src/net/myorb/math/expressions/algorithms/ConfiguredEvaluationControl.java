
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.DisplayConsole;
import net.myorb.math.expressions.gui.DisplayIO;
import net.myorb.math.expressions.*;

import java.util.List;

/**
 * evaluation control for system that uses serial (file or stream based) symbol configuration
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class ConfiguredEvaluationControl <T> implements EvaluationControlI <T>
{


	public ConfiguredEvaluationControl (Environment <T> environment)
	{
		establishEnvironment (environment);
		initializeSymbolTable ();
	}
	public ConfiguredEvaluationControl (Environment <T> environment,  boolean startGui)
	{
		this (environment); initializeEngine ();
		if (startGui) connectGui (environment);
	}

	/**
	 * get copies of symbol map and space manager
	 * @param environment the environment object holding objects
	 */
	public void establishEnvironment (Environment <T> environment)
	{
		this.environment = environment;
		this.symbols = environment.getSymbolMap ();
		this.spaceManager = environment.getSpaceManager ();
		this.spaceManager.setEvaluationControl (this);
	}
	protected Environment <T> environment;
	protected ExpressionSpaceManager <T> spaceManager;
	protected SymbolMap symbols;


	/**
	 * connect calculation environment to DisplayIO GUI objects
	 * @param environment the object controlling the evaluation environment
	 */
	public void connectGui (Environment<T> environment)
	{
		guiSymbolMap = DisplayIO.connectConsole (toString (), 700, environment, this);
		environment.setOutStream (DisplayConsole.getStreamFor (CoreMainConsole, guiSymbolMap));
		environment.setConsoleWriter (DisplayConsole.getWriterFor (CoreMainConsole, guiSymbolMap));
		guiSymbolMap.put (CoreExecutionEnvironment, environment);
		guiSymbolMap.put (CoreSymbolMap, symbols);
	}
	public DisplayConsole.StreamProperties getGuiMap () { return guiSymbolMap; }
	protected DisplayConsole.StreamProperties guiSymbolMap;


	/**
	 * import key symbols into the local symbol table
	 */
	public void initializeSymbolTable ()
	{
		symbols.addCoreOperators ();
	}


	/**
	 * allocate an engine object that will operate in the specified space
	 */
	public void initializeEngine ()
	{ setEngine (new EvaluationEngine<T> (environment, this)); }
	public void setEngine (EvaluationEngine<T> engine) { this.engine = engine; }
	public EvaluationEngine<T> getEngine () { return engine; }
	protected EvaluationEngine<T> engine;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.EvaluationControlI#run(java.util.List, boolean)
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


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.EvaluationControlI#execute(java.lang.String, boolean)
	 */
	public void execute (String source, boolean processWithCheck)
	{
		TokenParser.TokenSequence tokens = TokenParser.parse (new StringBuffer (source));
		if (engine.dumpingRequested ()) System.out.println (tokens);
		run (tokens, processWithCheck);
	}
	public void execute (String source) { execute (source, true); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.EvaluationControlI#evaluate(java.lang.String)
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
	 */
	public T lookup (String symbol)
	{
		return environment.getValueManager ().toDiscrete (symbols.getValue (symbol));
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return "CALCLIB";
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.EvaluationControlI#getSymbolTableManager()
	 * @deprecated
	 */
	public SymbolTableManagerI<T> getSymbolTableManager() 
	{
		return null;
	}


}

