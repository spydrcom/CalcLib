
package net.myorb.math.expressions;

// evaluation states
import net.myorb.math.expressions.evaluationstates.DeclarationSupport;
import net.myorb.math.expressions.evaluationstates.ExtendedArrayFeatures;
import net.myorb.math.expressions.evaluationstates.IndirectionProcessing;
import net.myorb.math.expressions.evaluationstates.FunctionDefinition;
import net.myorb.math.expressions.evaluationstates.Environment;

// IOLIB error handling
import net.myorb.data.abstractions.ErrorHandling;

// JRE
import java.util.List;

/**
 * evaluate expressions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class EvaluationEngine <T>
{


	/**
	 * engine requires a symbol table and a type manager
	 * @param symbols the symbols collected for use by this engine
	 * @param spaceManager the type manager for processing calculations
	 * @param control the higher level control logic object
	 */
	public EvaluationEngine
		(
			SymbolMap symbols,
			ExpressionSpaceManager <T> spaceManager,
			EvaluationControlI <T> control
		)
	{
		this (new Environment <T> (symbols, spaceManager), control);
	}
	public EvaluationEngine
		(Environment <T> environment, EvaluationControlI <T> control)
	{
		this (environment); this.setControl (control); this.setKeywordMap ();
	}
	public EvaluationEngine (Environment <T> environment)
	{
		this.setFunctionDefinition (this.environment = environment);
		this.setIndirectionProcessor (environment);
	}
	protected Environment <T> environment;


	/**
	 * prepare for function definitions
	 * @param environment a description of the system
	 */
	public void setFunctionDefinition
	(Environment <T> environment) { this.setFunctionDefinition (new FunctionDefinition <T> (environment)); }
	public void setFunctionDefinition (FunctionDefinition <T> functionManager) { this.functionManager = functionManager; }
	protected FunctionDefinition <T> functionManager;


	/**
	 * prepare indirect access processor
	 * @param environment a description of the system
	 */
	public void setIndirectionProcessor (Environment <T> environment)
	{ this.indirectionProcessor = new IndirectionProcessing <T> (environment); }
	protected IndirectionProcessing <T> indirectionProcessor;


	/**
	 * @param control the connection between evaluation engine and GUI
	 */
	public void setControl (EvaluationControlI <T> control)
	{
		this.environment.connectControl (control);
		this.scriptManager = new ScriptManager<T> (control, environment);
	}
	protected ScriptManager <T> scriptManager;


	/**
	 * establish map of commands
	 */
	public void setKeywordMap ()
	{ this.setKeywordMap (new KeywordMap <T> (this)); }
	public void setKeywordMap (KeywordMap <T> keywordMap) { this.keywordMap = keywordMap; }
	protected KeywordMap <T> keywordMap;


	/**
	 * evaluate a set of tokens and process errors
	 * @param tokens an ordered list of token comprising the expression
	 */
	public void processWithCatch (List <TokenParser.TokenDescriptor> tokens)
	{
		try
		{
			process (tokens);
		}
		catch (Exception e)
		{
			processErrorInLine (tokens, e);
		}
	}

	
	/**
	 * produce appropriately formatted diagnostic
	 * @param lineTokens the tokens of the error source line
	 * @param exceptionSeen an executable process to be invoked
	 */
	public void processErrorInLine
		(
			List <TokenParser.TokenDescriptor> lineTokens,
			Exception exceptionSeen
		)
	{
		ErrorHandling.process
		(
			lineTokens, exceptionSeen,
			environment.getOutStream (),
			supressingErrorMessages,
			dumpingRequested ()
		);
	}


	/**
	 * compare a type name to the expected type name
	 * @param requiredType name of the type being required
	 * @param expected the expected value for a match
	 * @return TRUE when supported otherwise FALSE
	 */
	public static boolean supports (String requiredType, ExpressionSpaceManager.ValueCharacterization expected)
	{
		try
		{ return ExpressionSpaceManager.ValueCharacterization.valueOf (requiredType.toUpperCase ()) == expected; }
		catch (Exception e) { throw new ErrorHandling.Terminator ("Data type name not recognized"); }
	}


	/**
	 * suppress errors from display
	 */
	public void
	supressErrorMessages ()
	{ supressingErrorMessages = true; }
	protected boolean supressingErrorMessages = false;


	/**
	 * determine processing status and act on token stream as expected
	 * @param tokens an ordered list of token comprising the expression
	 */
	public void process (List <TokenParser.TokenDescriptor> tokens)
	{
		if (evaluationBlock == null || evaluationBlock.isToBeProcessed (tokens))
		{
			processEnabled (tokens);
		}
	}


	/**
	 * control for blocks of commands
	 * - support for conditional and loop blocks
	 * @return the current block control object
	 */
	public EvaluationBlock <T> getEvaluationBlock () { return evaluationBlock; }

	/**
	 * @param evaluationBlock a block control object
	 */
	public void setEvaluationBlock (EvaluationBlock <T> evaluationBlock) { this.evaluationBlock = evaluationBlock; }
	protected EvaluationBlock <T> evaluationBlock = new EvaluationBlock <> ();


	/**
	 * evaluate a set of tokens
	 * @param tokens an ordered list of token comprising the expression
	 */
	public void processEnabled (List <TokenParser.TokenDescriptor> tokens)
	{
		environment.init ();
		if (dumpingRequested ())
			System.out.println (tokens);
		if (keywordMap.isKeywordCommand (tokens)) return;

		for (int tokenPosition = 0; tokenPosition < tokens.size (); tokenPosition ++)
		{
			environment.setToken (tokens.get (tokenPosition));

			if (environment.inPointerExpression ())											// recognize a pointer at position
			{
				indirectionProcessor.processDereference (tokens.get (++ tokenPosition));	// dereference the pointer to get symbol
			}

			if (typeIsIdentifier ()) environment.processIdentifier ();						// identifier may be recognized as operator
			else if (!typeIsOperator ()) environment.processValue ();						// all recognized values are processed commonly
			if (typeIsOperator ()) environment.processOperator ();							// identifier processing may have switched

			if (environment.arrayProcessingIsOpen ())
			{
				tokenPosition = processArray (tokens, tokenPosition);
			}
		}

		if (dumpingRequested ()) environment.showValueStack ();	// for debugging purposes
		environment.flush ();	// once tokens have been exhausted the operations stack must be flushed completely
	}
	boolean typeIsIdentifier () { return environment.getTokenType () == TokenParser.TokenType.IDN; }
	boolean typeIsOperator ()   { return environment.getTokenType () == TokenParser.TokenType.OPR; }


	/**
	 * get access to the symbol map for this environment
	 * @return the symbol map object
	 */
	public SymbolMap getSymbolMap () { return environment.getSymbolMap (); }


	/**
	 * get access to the keyword map for this environment
	 * @return keyword map object
	 */
	public KeywordMap <T> getKeywordMap () { return keywordMap; }


	/**
	 * get access to the function definition manager
	 * @return the function definition object
	 */
	public FunctionDefinition <T> getFunctionManager () { return functionManager; }


	/**
	 * get access to the environment structure for this engine
	 * @return the environment object
	 */
	public Environment <T> getEnvironment () { return environment; }


	/**
	 * process an array constructor
	 * @param tokens the token stream being processed
	 * @param tokenPosition the current position within the stream
	 * @return the position in the stream following the array constructor
	 */
	public int processArray (List <TokenParser.TokenDescriptor> tokens, int tokenPosition)
	{
		TokenParser.TokenSequence sequence = new TokenParser.TokenSequence (tokens);
		return new ExtendedArrayFeatures <T> ().process (sequence, tokenPosition, environment);
	}


	/**
	 * get the object from the top of the value stack
	 * @return top object from the value stack
	 */
	public ValueManager.GenericValue popValueStack () { return environment.getValueStack ().pop (); }


	/**
	 * push vale to the value stack
	 * @param value generic value to be pushed
	 */
	public void pushValueStack (ValueManager.GenericValue value) { environment.getValueStack ().push (value); }


	/**
	 * check dumping flag
	 * @return TRUE = DUMPING == 1
	 */
	public boolean dumpingRequested () { return environment.isDumpingSet (); }


	/**
	 * get a copy of the ScriptManager object
	 * @return a copy of the ScriptManager object
	 */
	public ScriptManager <T> getScriptManager () { return scriptManager; }


	/**
	 * get a copy of a DeclarationSupport object
	 * @return access to DeclarationSupport object
	 */
	public DeclarationSupport <T> getDeclarationSupport () { return new DeclarationSupport <T> (environment); }


	/**
	 * get a copy of a data import object
	 * @return a new data importer
	 */
	public DataIO <T> getDataIO () { return new DataIO <T> (environment); }


}


