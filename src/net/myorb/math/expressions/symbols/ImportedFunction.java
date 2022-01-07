
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.FunctionDefinition;

import net.myorb.math.expressions.ExtendedDataConversions;
import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.SymbolMap;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		connectTo (environment); doParse ();
	}


	/**
	 * track exceptions from function parser
	 */
	public void doParse ()
	{
		try { parseFunction (); }
		catch (Exception e) { e.printStackTrace (); }
	}


	/**
	 * get conversion and symbol objects
	 * @param environment source of control objects
	 */
	public void connectTo (Environment<T> environment)
	{
		this.converter = new ExtendedDataConversions<T>(environment);
		this.valueManager = environment.getValueManager ();
		this.symbols = environment.getSymbolMap ();
	}
	protected ExtendedDataConversions<T> converter;
	protected ValueManager<T> valueManager;
	protected SymbolMap symbols;


	/**
	 * @param source the object holding the function
	 * @return the map of methods
	 */
	Map<String,Method> getMethodMap (Object source)
	{
		Map<String,Method> methods;
		if (source instanceof LibraryObject)
		{
			methods = ((LibraryObject<?>) source).getMethods ();
		}
		else
		{
			methods = FunctionDefinition.getMethodMap (source.getClass ());
			this.setContainerObject (source);
		}
		return methods;
	}


	/**
	 * use the token list to find the method within the class
	 */
	public void parseFunction ()
	{
		String libraryName =
			functionTokens.get (0).getTokenImage ();
		Object libObj = symbols.lookup (libraryName);
		Map<String,Method> methods = getMethodMap (libObj);

		declaration.append (libraryName).append (".");
		functionName = functionTokens.get (2).getTokenImage ();
		declaration.append (functionName).append (" (");

		function = methods.get (functionName);
		Type[] parameterTypes = function.getGenericParameterTypes ();
		parameterType = new ExtendedDataConversions.Types[parameterTypes.length];
		String fReturnType = function.getGenericReturnType ().toString ();
		returnType = ExtendedDataConversions.getTypeFor (fReturnType);

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
			Object[] actual = actualParametersFor (parameters);
			return returnValueFor (function.invoke (enclosingClass, actual));
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Library function invocation failed", e);
		}
	}
	protected Method function;


	/**
	 * @param container the object containing the function
	 */
	public void setContainerObject (Object container)
	{
		this.enclosingClass = container;
	}
	protected Object enclosingClass = null;		// default assumption is that imported class uses static methods


	/**
	 * process returned function value
	 * @param returned the object returned from call
	 * @return the value as a manager generic
	 */
	public ValueManager.GenericValue returnValueFor (Object returned)
	{
		if (returned == null)
		{
			return valueManager.newUndefinedSymbolReference (functionName);
		}
		else
		{
			return converter.convertObject (returned, returnType);
		}
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

		if (parameterType.length == 0)
		{
			return new Object[]{};
		}
		else if (parameterType.length == 1)
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

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Subroutine#toFormatted(boolean)
	 */
	public String toFormatted (boolean pretty) { return toString (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Subroutine#toPrettyText()
	 */
	public String toPrettyText () { return toString (); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Subroutine#toString()
	 */
	public String toString () { return declaration.toString (); }
	protected StringBuffer declaration = new StringBuffer ();


}

