
package net.myorb.math.expressions.evaluationstates;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.computational.FunctionRoots;

import net.myorb.math.expressions.commands.CommandDictionary;
import net.myorb.math.expressions.commands.ExtendedKeywordCommand;

import net.myorb.math.expressions.algorithms.LambdaExpressions;
import net.myorb.math.expressions.symbols.*;
import net.myorb.math.expressions.*;

import net.myorb.data.abstractions.ErrorHandling;
import net.myorb.data.abstractions.Function;

import java.io.PrintStream;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * primitive data objects and support methods
 * @param <T> type on which operations are to be executed
 */
public class Primitives<T>
{


	/**
	 * base class for errors that terminate processing
	 */
	public static class FatalError extends ErrorHandling.Terminator
	{
		public FatalError (String message) { super (message); }
		public FatalError (String message, Exception e) { super (message, e); }
		private static final long serialVersionUID = -3390690308336665935L;
	}


	/**
	 * used for special case generated by negate unary operator.
	 * ZERO is pushed on the stack in front of "-" operator
	 * allowing it to function as subtraction
	 */
	protected T ZERO;


	/**
	 * set data sources
	 * @param symbols the symbol table to use for symbol resolution
	 * @param spaceManager the type manager for evaluation control
	 */
	protected void setDataSources
	(SymbolMap symbols, ExpressionSpaceManager<T> spaceManager)
	{
		this.ZERO = spaceManager.getZero ();
		this.conversions = new DataConversions<T>(spaceManager);
		this.spaceManager = spaceManager;
		this.symbols = symbols;
	}


	/**
	 * establish library to be used
	 * @param powerLibrary the library implementation object
	 */
	public void
	setLibrary (ExtendedPowerLibrary<T> powerLibrary) { this.powerLibrary = powerLibrary; }
	public ExtendedPowerLibrary<T> getLibrary () { return powerLibrary; }
	private ExtendedPowerLibrary<T> powerLibrary;


	/**
	 * get the type manager set for this engine
	 * @return the type management object
	 */
	public ExpressionSpaceManager<T>
	getSpaceManager () { return spaceManager; }
	private ExpressionSpaceManager<T> spaceManager;


	/**
	 * get a copy of the conversion manager object
	 * @return the data conversion object
	 */
	public DataConversions<T>
	getConversionManager () { return conversions; }
	private DataConversions<T> conversions;


	/**
	 * get access to the symbol map
	 * @return the symbol map object
	 */
	public SymbolMap getSymbolMap () { return symbols; }
	private SymbolMap symbols;


	/**
	 * complete the processing of a subroutine
	 * @param subroutine the definition of the subroutine being constructed
	 */
	public void processSubroutine (Subroutine<T> subroutine)
	{
		subroutine.setSpaceManager (spaceManager);
		subroutine.setSymbolTable (symbols);
	}


	/*
	 * symbol processing
	 */


	/**
	 * @return access to command symbols
	 */
	public CommandDictionary getCommandDictionary () { return commands; }
	protected CommandDictionary commands =  new CommandDictionary ();


	/**
	 * @return access to command subordinate keywords
	 */
	public List<String> getSubordinateKeywords () { return subordinateKeywords; }
	protected List<String> subordinateKeywords =  new ArrayList<String> ();


	/**
	 * @return processing object for Lambda expressions
	 */
	public LambdaExpressions<T> getLambdaExpressionProcessor ()
	{
		return lambdaExpressions; //TODO: may need some changes
	}
	protected LambdaExpressions<T> lambdaExpressions = new LambdaExpressions<T> ();


	/**
	 * @param command an command descriptor containing subordinates
	 */
	public void addToKeywordList (ExtendedKeywordCommand command)
	{
		if (command == null) return;
		for (String keyword : command.includingSubordinateKeywords ())
		{ subordinateKeywords.add (keyword.toLowerCase ()); }
	}


	/**
	 * complete the processing of a function definition
	 * @param defnition the definition object being constructed
	 */
	public void processDefinedFunction (AbstractFunction<T> defnition)
	{
		processSubroutine (defnition);
		symbols.add (defnition);
	}


	/**
	 * associate a name with a value
	 * @param name the name from the identifier
	 * @param value the value assigned
	 * @return a new storage object
	 */
	public NamedObject generateSymbolStorage (String name, ValueManager.GenericValue value)
	{
		ValueManager.GenericValue v;
		ValueManager.setFormatter (v = determineValue (value), spaceManager);
		return generateAssignedVariableStorage (name, v, value.getMetadata ());
	}
	public ValueManager.GenericValue determineValue (ValueManager.GenericValue value)
	{
		ValueManager.GenericValue v;
		if (valueManager.isArray (v = value))
		{ v = consolidatedArray (valueManager.toArray (v)); }
		else if (valueManager.isParameterList (v)) v = consolidatedList (v);
		return v;
	}
	public NamedObject generateAssignedVariableStorage (String name, ValueManager.GenericValue value, ValueManager.Metadata m)
	{ value.setMetadata (m); value.setName (name); return new AssignedVariableStorage (name, value); }


	/**
	 * reduce dimension level where appropriate before assignment
	 * @param generic a generic wrapper that may hold dimensioned values
	 * @return generic wrapper, possibly still dimensioned
	 */
	public ValueManager.GenericValue consolidatedList (ValueManager.GenericValue generic)
	{
		ValueManager.RawValueList<T> values = new ValueManager.RawValueList<T> ();
		ValueManager.ValueList list = (ValueManager.ValueList)generic;

		for (ValueManager.GenericValue v : list.getValues ())
		{
			if (v instanceof ValueManager.DiscreteValue)
			{ values.add (valueManager.toDiscreteValue (v).getValue ()); }
			else values.addAll (valueManager.toDiscreteValues (v));
		}

		return consolidatedArray (values);
	}
	public ValueManager.GenericValue consolidatedArray (ValueManager.RawValueList<T> values)
	{
		if (values.size() == 1) return valueManager.newDiscreteValue (values.get (0));
		else return valueManager.newDimensionedValue (values);
	}


	/**
	 * set the value of a symbol
	 * @param name the name of the symbol
	 * @param value the new value to assign
	 * @return storage object
	 */
	public SymbolMap.Named setSymbol (String name, ValueManager.GenericValue value)
	{
		SymbolMap.Named n;
		if (value instanceof ValueManager.ValueList)
		{
			System.out.println ("value list found");
		}
		symbols.add (n = generateSymbolStorage (name, value));
		return n;
	}


	/**
	 * add symbol into symbol table as undefined symbol
	 * @param name the name of the symbol being constructed
	 * @return an undefined symbol reference descriptor
	 */
	public SymbolMap.Named processUndefinedSymbol (String name)
	{
		return setSymbol (name, valueManager.newUndefinedSymbolReference (name));
	}


	/**
	 * management for properties of Differential Equations
	 * @return manager for Differential Equations
	 */
	public DifferentialEquationsManager<T>
		getDifferentialEquationsManager () { return differentialEquationsManager; }
	DifferentialEquationsManager<T> differentialEquationsManager = new DifferentialEquationsManager <T> (this);


	/*
	 * token management
	 */


	/**
	 * simple function direct lookup
	 * @param functionName name of function to find
	 * @return the function if found
	 */
	protected Function<T> lookupFunction (String functionName)
	{
		return conversions.toSimpleFunction (symbols.lookup (functionName));
	}


	/**
	 * attempt a symbol lookup
	 * @param name the name of the symbol to be found
	 * @return a symbol map object
	 */
	protected SymbolMap.Named lookup (String name)
	{
		SymbolMap.Named n;
		if ((n = symbols.lookup (name)) == null)
			if (type == TokenParser.TokenType.OPR)
				throw new FatalError ("Unrecognized operator: " + name);
			else n = processUndefinedSymbol (name);
		return n;
	}


	/**
	 * get value associated with name
	 * @param name the name of the value to find
	 * @return the associated value
	 */
	public ValueManager.GenericValue getValue (String name)
	{
		type = TokenParser.TokenType.IDN;
		return symbols.getValue (lookup (name));
	}


	/**
	 * perform a name lookup on current token image
	 * @return the named object found
	 */
	protected SymbolMap.Named lookupImage ()
	{
		return lookup (image);
	}


	/**
	 * capture the description of the current token
	 * @param t the descriptor for the token being processed
	 */
	public void setToken (TokenParser.TokenDescriptor t)
	{
		type = t.getTokenType ();
		image = t.getTokenImage ();
	}


	/**
	 * get the text of the token currently being processed
	 * @return the token image
	 */
	public String getTokenImage () { return image; }
	private String image;


	/**
	 * get the type of the token currently being processed
	 * @return the token type
	 */
	public TokenParser.TokenType getTokenType () { return type; }
	protected void setTokenType (TokenParser.TokenType type) { this.type = type; }
	private TokenParser.TokenType type;


	/*
	 * flag operator being seen
	 */


	/**
	 * @return current flag value
	 */
	protected boolean isOperatorLastSeen () { return this.operatorLastSeen; }
	private boolean operatorLastSeen = true;

	/**
	 * @param newStatus mark as seen TRUE or not seen FALSE
	 */
	protected void setOperatorStatus (boolean newStatus) { this.operatorLastSeen = newStatus; }

	/**
	 * flag reset to false
	 */
	protected void resetOperatorLastSeen () { setOperatorStatus (false); }

	/**
	 * flag set to true
	 */
	protected void setOperatorLastSeen () { setOperatorStatus (true); }



	/*
	 * assignment preparation primitives
	 */


	/**
	 * @return name of most recently prepared assignment
	 */
	protected String getPreparedAssignment () { return assignTo; }
	private String assignTo = null;

	/**
	 * @param symbolName name of symbol to be assigned
	 */
	protected void flagAssignment (String symbolName) { this.assignTo = symbolName; }

	/**
	 * @return TRUE for assignment prepared
	 */
	protected boolean assignmentActive () { return this.assignTo != null; }

	/**
	 * reset prepared assignment to inactive
	 */
	protected void resetAssignment () { this.assignTo = null; }

	/**
	 * @return pending assignment has been flagged
	 */
	protected boolean assignmentIsPending () { return this.assignmentPending; }
	protected void resetPendingAssignment () { this.assignmentPending = false; }
	protected void flagPendingAssignment () { this.assignmentPending = true; }
	private boolean assignmentPending = false;


	/*
	 * operations and values stacks
	 */


	private ValueStack<T> valueStack = new ValueStack<T>();
	private List<SymbolMap.Operation> operationStack = new ArrayList<SymbolMap.Operation>();
	private SymbolMap.Operation tos = SymbolMap.getTerminator ();


	/*
	 * stack operations
	 */


	/**
	 * mark TOS with termination operator
	 */
	protected void initTos ()
	{
		tos = SymbolMap.getTerminator ();
	}


	/**
	 * read the precedence value of the TOS operation
	 * @return the precedence value
	 */
	protected int getTosPrecedence ()
	{
		return tos.getPrecedence ();
	}


	/**
	 * push TOS object into list and change TOS
	 * @param op the new operation to have on TOS
	 */
	protected void pushOpStack (SymbolMap.Operation op)
	{
		operationStack.add (tos);
		tos = op;
	}


	/**
	 * get top operation from stack
	 * @return a symbol map operation descriptor
	 */
	protected SymbolMap.Operation popOpStack ()
	{
		int size = operationStack.size ();
		if (size == 0) throw new FatalError ("Underflow on operations stack");
		return operationStack.remove (size - 1);
	}


	/**
	 * pop from stack to TOS and return previous top
	 * @return previous top
	 */
	protected SymbolMap.Operation popOpStackToTos ()
	{
		SymbolMap.Operation op = tos;
		tos = popOpStack ();
		return op;
	}


	/**
	 * replace TOS operation with substitute
	 * @param op operation to become TOS
	 * @return prior TOS operation
	 */
	protected SymbolMap.Operation substituteTos
			(SymbolMap.Operation op)
	{
		SymbolMap.Operation prior =
			popOpStackToTos ();
		pushOpStack (op);
		return prior;
	}


	/*
	 * value stack object controlled operations
	 */


	/**
	 * get access to the value stack
	 * @return the value stack object
	 */
	public ValueStack<T> getValueStack ()
	{
		return valueStack;
	}


	/**
	 * parse token as value and push onto stack
	 */
	public void pushTokenOnValueStack ()
	{
		if (type == TokenParser.TokenType.SEQ)
		{
			valueStack.push (valueManager.newCapturedValue (image), null);	//TODO: meta-data for captured sequences
		}
		else
		{
			valueStack.push (spaceManager.parseValueToken (type, image));
		}
	}


	/**
	 * request stack dump
	 */
	public void showValueStack ()
	{
		valueStack.dump ();
	}


	/**
	 * check for error conditions after operations exhausted
	 * @throws ValueStack.StackCycle for unexpected or inadequate stack remnants
	 */
	public void checkValueStack () throws ValueStack.StackCycle
	{
		if (valueStack.isEmpty ()) return;
		
		while (!valueStack.isEmpty ())
		{
			try
			{
				/*
				 * bug determined 09/29/18
				 * reference to xxx' fails if xxx is not defined
				 * analysis short circuits on UDF recognition
				 * must recognize xxx' as entity
				 * below exception/message seen
				 */
				valueManager.check (valueStack.pop ());
			}
			catch (ValueManager.UndefinedValueError u)
			{
				throw u;
			}
			catch (Exception e)
			{
				valueStack.recognizeContextError (e);
			}
		}

		throw new ValueStack.StackOverflow ();
	}


	/**
	 * force stacks empty
	 */
	public void clearStacks ()
	{
		operationStack.clear ();
		valueStack.clear ();
	}


	/**
	 * show value stack if dumping is enabled
	 */
	public void dumpValueStack ()
	{
		if (traceEnabled) showValueStack ();
	}


	/*
	 * tracing control
	 */


	/**
	 * check flag for dump generation
	 * @return TRUE = dump should be generated
	 */
	public boolean isDumpingSet ()
	{
		Object dumpingFlagObject;
		if ((dumpingFlagObject = symbols.get (OperatorNomenclature.DUMPING_KEYWORD)) == null) return false;
		ValueManager.GenericValue flag = ((SymbolMap.VariableLookup) dumpingFlagObject).getValue ();
		return !spaceManager.isZero (valueManager.toDiscrete (flag));
	}
	protected void setTraceStatus () { traceEnabled = isDumpingSet (); }
	protected boolean traceIsEnabled () { return traceEnabled; }
	private boolean traceEnabled;


	/*
	 * roots manager
	 */


	/**
	 * lazy initialization of the root iteration algorithm implementer
	 * @return a root management object
	 */
	public FunctionRoots<T> getRootsManager ()
	{
		if (rootsManager == null)
		{ rootsManager = new FunctionRoots<T> (getSpaceManager (), getLibrary ()); }
		return rootsManager;
	}
	private FunctionRoots<T> rootsManager = null;


	/**
	 * improve function root approximation by iterations
	 * @param functionSymbol a symbol object for the function
	 * @param variableName name of variable holding approximation
	 */
	public void findFunctionRoot (SymbolMap.Named functionSymbol, String variableName)
	{ findFunctionRoot (conversions.toSimpleFunction (functionSymbol), variableName); }


	/**
	 * run approximation update iterations (invoker)
	 * @param function description of function seeking root
	 * @param variableName name of variable holding approximation
	 */
	public void findFunctionRoot (Function<T> function, String variableName)
	{
		T updatedApproximation = runIterations
			(function, getDiscreteFrom (variableName), getRootsManager ());
		setSymbol (variableName, valueManager.newDiscreteValue (updatedApproximation));
	}


	/**
	 * run approximation update iterations (implementer)
	 * @param function description of function seeking root
	 * @param initialApproximation a value near the root being sought
	 * @param roots a root management object with the iteration algorithm
	 * @return the updated approximation of the root
	 */
	public T runIterations (Function<T> function, T initialApproximation, FunctionRoots<T> roots)
	{ return roots.newtonMethodApproximated (function, initialApproximation, roots.getDefaultDelta ()); }


	/*
	 * value management
	 */


	/**
	 * get a copy of the value management object
	 * @return the value management object
	 */
	public ValueManager<T> getValueManager () { return valueManager; }
	private ValueManager<T> valueManager = new ValueManager<T> ();

	/**
	 * @param variableName name of variable holding value
	 * @return the value from the named variable
	 */
	public T getDiscreteFrom (String variableName)
	{
		return valueManager.toDiscrete (getValue (variableName));
	}

	/**
	 * value stream marker for internal error condition
	 * @return undefined symbol reference
	 */
	public ValueManager.GenericValue internalError ()
	{
		return valueManager.newUndefinedSymbolReference ("Error in expression");
	}


	/*
	 * processing integer values
	 */


	/**
	 * use value manager to convert generic to integer
	 * @param source the generic value to be coerced
	 * @return the coerced integer value
	 */
	public int intValue (ValueManager.GenericValue source)
	{
		return valueManager.toInt (source, spaceManager);
	}


	/*
	 * metadata lookup
	 */


	/**
	 * get array metadata for value
	 * @param value the generic value expected to contain metadata
	 * @return an array descriptor
	 */
	@SuppressWarnings("unchecked")
	public Arrays.Descriptor<T> getArrayMetadataFor (ValueManager.GenericValue value)
	{
		ValueManager.Metadata metadata = value.getMetadata ();
		if (metadata == null) throw new RuntimeException ("No metadata available");
		if (!(metadata instanceof Arrays.Descriptor)) throw new RuntimeException ("Array metadata expected");
		return (Arrays.Descriptor<T>) metadata;
	}


	/*
	 * values from parameter lists
	 */


	/**
	 * get an integer value from a parameter list
	 * @param number the position number within the list
	 * @param parameterList the value list of parameters
	 * @return the integer value of identified parameter
	 */
	public int intParameter (int number, List<ValueManager.GenericValue> parameterList)
	{
		return intValue (parameterList.get (number));
	}


	/**
	 * get an integer value from a parameter list
	 * @param number the position number within the list
	 * @param parameterList the value list of parameters
	 * @return the integer value of identified parameter
	 */
	public int intParameter (int number, ValueManager.ValueList parameterList)
	{
		return intParameter (number, parameterList.getValues ());
	}


	/**
	 * get parameter integer value.
	 *  if position not in list default is returned
	 * @param number the position number within the list
	 * @param parameterList the value list of parameters
	 * @param defaultValue default in absence of specification
	 * @return the integer value of identified parameter
	 */
	public int intParameterWithDefault (int number, ValueManager.ValueList parameterList, int defaultValue)
	{
		List<ValueManager.GenericValue> values = parameterList.getValues ();
		if (number >= values.size ()) return defaultValue;
		return intParameter (number, values);
	}


	/**
	 * print stream for display output
	 * @return the stream object for output
	 */
	public PrintStream getOutStream () { return outStream; }
	public void setOutStream (PrintStream outStream) { this.outStream = outStream; }
	public void setConsoleWriter (PrintWriter writer) { this.writer = writer; }
	public PrintWriter getConsoleWriter () { return writer; }
	protected PrintStream outStream = System.out;
	protected PrintWriter writer;


}

