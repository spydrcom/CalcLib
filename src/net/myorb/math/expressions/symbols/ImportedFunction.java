
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.*;
import net.myorb.math.expressions.evaluationstates.Environment;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * describe a function imported from an external class
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ImportedFunction<T> extends AbstractFunction<T>
{


	public ImportedFunction
		(
			String name, List<String> parameterNames,
			TokenParser.TokenSequence functionTokens,
			Environment<T> environment
		)
	{
		super (name, parameterNames, functionTokens);
		this.converter = new ExtendedDataConversions<T>(environment);
		this.valueManager = environment.getValueManager ();
		this.symbols = environment.getSymbolMap ();

		try
		{
			parseFunction ();
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}
	protected ExtendedDataConversions<T> converter;
	protected ValueManager<T> valueManager;
	protected SymbolMap symbols;


	/**
	 * use the token list to find the method within the class
	 */
	public void parseFunction ()
	{
		String libraryName = functionTokens.get (0).getTokenImage ();
		SymbolMap.Library library = (SymbolMap.Library) symbols.lookup (libraryName);
		declaration.append (libraryName).append (".");

		functionName = functionTokens.get (2).getTokenImage ();
		declaration.append (functionName).append (" (");

		function = library.getMethods ().get (functionName);
		Type[] parameterTypes = function.getGenericParameterTypes ();
		parameterType = new ExtendedDataConversions.Types[parameterTypes.length];
		returnType = ExtendedDataConversions.getTypeFor (function.getGenericReturnType ().toString ());

		for (int i = 0; i < parameterTypes.length; i++)
		{
			if (i > 0) declaration.append (", ");
			String t = parameterTypes[i].toString ();
			switch (parameterType[i] = getTypeFor (t))
			{
				case MAT: case VEC: case TXT: 
					declaration.append (parameterType[i].toString ()); break;
				case COMPLEX: declaration.append ("complex"); break;
				default: declaration.append (t); break;
			}
		}

		declaration.append (")");
	}
	ExtendedDataConversions.Types getTypeFor (String name)
	{
		ExtendedDataConversions.Types t;
		if ((t = ExtendedDataConversions.getTypeFor (name)) == null)
		{
			if (name.contains ("Complex"))
			{ return ExtendedDataConversions.Types.COMPLEX; }
			else return ExtendedDataConversions.Types.OTHER;
		} else return t;
	}
	protected String functionName;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.DefinedFunction#execute(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
	{
		try
		{
			return returnValueFor (function.invoke (enclosingClass, actualParametersFor (parameters)));
		} catch (Exception e)
		{
			throw new RuntimeException ("Library function invocation failed", e);
		}
	}
	protected Object enclosingClass = null;		// assumption is that imported class uses static methods
	protected Method function;


	/**
	 * process returned function value
	 * @param returned the object returned from call
	 * @return the value as a manager generic
	 */
	public ValueManager.GenericValue returnValueFor (Object returned)
	{
		if (returned == null)
			return valueManager.newUndefinedSymbolReference (functionName);
		else return converter.convertObject (returned, returnType);
	}
	protected ExtendedDataConversions.Types returnType;


	/**
	 * build an actual parameter set
	 * @param parameters the parameters as described in the value manager
	 * @return an array of values boxed in typed objects
	 */
	public Object[] actualParametersFor (ValueManager.GenericValue parameters)
	{
		List<Object> parameterSet = new ArrayList<Object>();

		if (parameterType.length == 1)
		{
			parameterSet.add (converter.convertToType (parameters, parameterType[0]));
		}
		else if (parameterType.length > 0)
		{
			int n = 0;											// stepping through positional parameters
			if (valueManager.isArray (parameters))
			{
				for (T v : valueManager.toArray (parameters))
				{ parameterSet.add (converter.convertToType (v, parameterType[n++])); }
			}
			else
			{
				List<ValueManager.GenericValue> parameterList = ((ValueManager.ValueList)parameters).getValues ();
				for (ValueManager.GenericValue v : parameterList) { parameterSet.add (converter.convertToType (v, parameterType[n++])); }
			}
		}

		return parameterSet.toArray ();
	}
	protected ExtendedDataConversions.Types[] parameterType;


	// show the import with original library and formal parameters

	public String toFormatted (boolean pretty) { return toString (); }
	public String toPrettyText () { return toString (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Subroutine#toString()
	 */
	public String toString () { return declaration.toString (); }
	StringBuffer declaration = new StringBuffer ();


}
