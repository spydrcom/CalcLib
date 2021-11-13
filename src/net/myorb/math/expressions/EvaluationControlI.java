
package net.myorb.math.expressions;

import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.gui.DisplayConsole;

import java.util.List;

/**
 * implemented actions for execution of commands
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public interface EvaluationControlI <T>
{

	/*
	 * GUI map core object names
	 * see net.myorb.math.expressions.gui.EnvironmentCore
	 */

	public static final String
		CoreMainConsole = "MainConsole",
		CoreExecutionEnvironment = "ExecutionEnvironment",
		CoreSymbolMap = "SymbolMap"
	;

	/**
	 * get the Evaluation Engine object
	 * @return the environment EvaluationEngine
	 */
	public EvaluationEngine<T> getEngine ();

	/**
	 * evaluate an expression
	 * @param source the text of the expression
	 * @return computed value
	 */
	T evaluate (String source);

	/**
	 * parse source and execute parsed token stream
	 * @param source text of the expression to be executed
	 */
	void execute (String source);

	/**
	 * parse source and execute parsed token stream
	 * @param command the command to be parsed and executed
	 * @param processWithCheck TRUE = catch thrown exceptions
	 */
	void execute (String command, boolean processWithCheck);

	/**
	 * execute streams of tokens
	 * @param tokens the list of tokens to execute
	 * @param processWithCheck TRUE = catch thrown exceptions
	 */
	void run (List<TokenParser.TokenDescriptor> tokens, boolean processWithCheck);

	/**
	 * @return the symbol table manager
	 */
	SymbolTableManagerI<T> getSymbolTableManager ();

	/**
	 * @return map of stream properties
	 */
	DisplayConsole.StreamProperties getGuiMap ();

}
