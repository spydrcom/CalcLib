
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.tree.JsonBinding.JsonRepresentation;
import net.myorb.math.expressions.tree.Profile;
import net.myorb.math.expressions.evaluationstates.ParameterListDescription;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.Function;

import net.myorb.data.abstractions.ExpressionTokenParser;
import net.myorb.data.notations.json.JsonLowLevel.JsonValue;
import net.myorb.data.notations.json.JsonPrettyPrinter;
import net.myorb.data.notations.json.JsonSemantics;

import java.io.PrintStream;
import java.io.File;

/**
 * wrap a simple function object as a user defined function.
 *  this allows posting of spline objects and other interpolations.
 *  this will also be used to allow import of foreign functions which implement Function
 * @param <T> the data type for the function
 * @author Michael Druckman
 */
public class FunctionWrapper <T> extends AbstractFunction <T>
{


	/**
	 * @param name the name of the function
	 * @param parameterName name the name of the function parameter
	 * @param functionTokens a token sequence that describes the function
	 * @param function the function object being wrapped
	 */
	public FunctionWrapper
		(
			String name, String parameterName,
			ExpressionTokenParser.TokenSequence functionTokens,
			Function <T> function
		)
	{
		super
		(
			name,
			new ParameterListDescription (parameterName),
			functionTokens
		);
		this.valueManager = new ValueManager <T> ();
		this.parameterName = parameterName;
		this.function = function;
	}
	protected ValueManager <T> valueManager;
	protected Function <T> function;


	/**
	 * @return the wrapped function
	 */
	public Function <T> getFunction () { return function; }


	/**
	 * @return the value of the parameter
	 */
	public ValueManager.GenericValue getParameterValue ()
	{
		return ((AssignedVariableStorage) symbols.lookup (parameterName)).getValue ();
	}
	protected String parameterName;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ExecutableUnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	@Override public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
	{ return valueManager.newDiscreteValue (function.eval (valueManager.toDiscrete (parameter))); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Subroutine#run()
	 */
	@Override public void run () { constructEngine ().pushValueStack (execute (getParameterValue ())); }


	/**
	 * save objects that have JsonRepresentation
	 */
	public void saveAsJson ()
	{
		if ( ! (function instanceof JsonRepresentation) )
		{ throw new RuntimeException ("Function does not have JSON Representation"); }
		JsonValue value = tagged ( (JsonRepresentation <?>) function );
		try { JsonPrettyPrinter.sendTo (value, getStream ()); }
		catch (Exception e) { e.printStackTrace (); }

	}
	public JsonValue tagged (JsonRepresentation <?> rep)
	{
		JsonValue value = rep.toJson ();
		JsonSemantics.JsonObject object = (JsonSemantics.JsonObject) value;
		object.addMemberNamed ("Class", JsonSemantics.stringOrNull (function.getClass ().getSimpleName ()));
		object.addMemberNamed ("Parameter", JsonSemantics.stringOrNull (parameterName));
		object.addMemberNamed ("Name", JsonSemantics.stringOrNull (name));
		object.setOrderedMembersList (Profile.getNameList ());
		return value;
	}
	public PrintStream getStream () throws Exception
	{
		return new PrintStream (new File ("expressions", name+".json"));
	}


}

