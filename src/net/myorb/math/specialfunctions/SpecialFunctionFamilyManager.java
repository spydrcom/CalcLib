
package net.myorb.math.specialfunctions;

import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.symbols.DefinedTransform;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.polynomial.PolynomialFamilyManager;
import net.myorb.data.abstractions.SimpleUtilities;
import net.myorb.math.Function;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * support for descriptions of families of special functions
 * @author Michael Druckman
 */
public class SpecialFunctionFamilyManager
{


	public static final String DEFAULT_SPECIALFUNCTIONS_PACKAGE = "net.myorb.math.specialfunctions";


	/**
	 * describe functions
	 * @param <T> data type manager
	 */
	public interface FunctionDescription<T> extends Function<T>
	{

		/**
		 * @return a description for the function
		 */
		StringBuffer getFunctionDescription ();

		/**
		 * @return a name for the function
		 */
		String getFunctionName ();

	}
	public static class FunctionList<T> extends ArrayList<FunctionDescription<T>>
	{ private static final long serialVersionUID = -5063327574313296872L; }


	@SuppressWarnings("rawtypes") static HashMap<String, SpecialFunctionsFamily>
	families = new HashMap<String, SpecialFunctionsFamily>();


	/**
	 * @param name the name of the family
	 * @param path the class-path to the implementation of the family
	 * @param environment the central environment object
	 * @param <T> type of data in environment
	 */
	public static <T> void importFamilyDescription (String name, String path, Environment<T> environment)
	{
		try
		{
			//System.out.println ("Family: " + name);
			if (path.startsWith ("*")) path = DEFAULT_SPECIALFUNCTIONS_PACKAGE;
			Object newInstance = Class.forName (path + "." + name + "Functions").newInstance ();
			SpecialFunctionsFamily<T> fam = getFamily (newInstance);
			fam.init (environment.getSpaceManager ());
			families.put (name, fam);
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			System.out.println ("Polynomial Family Description not found: " + name);
		}
	}


	/**
	 * @param <T> data type of function
	 * @param from object that describes family
	 * @return object cast to family type
	 */
	@SuppressWarnings("unchecked")
	public static <T> SpecialFunctionsFamily<T> getFamily (Object from)
	{ return (SpecialFunctionsFamily<T>) from; }


	/**
	 * @param name the name of the family
	 * @param kind the kind (typically first &amp; second) 
	 * @param count the highest order of function to be imported
	 * @param environment the central environment object
	 * @param <T> type of data in environment
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> void importFamilyFunctions
	(String name, String kind, int count, Environment<T> environment)
	{
		boolean kindIsPolynomial = false;
		SpecialFunctionsFamily sfFamily = families.get (name);
		if (sfFamily != null) kindIsPolynomial = sfFamily.isPolynomial (kind);

		if (sfFamily == null || kindIsPolynomial)
		{
			PolynomialFamilyManager.importFamilyFunctions
				(name, kind, count, environment);
			return;
		}

		SpecialFunctionFamilyManager.FunctionList<T>
				functions = sfFamily.getFunctions (kind, count);				// count of functions of the kind
		//dump (functions, environment.getSpaceManager ());
		
		for (int i=0; i<functions.size(); i++)
		{
			postFunctionDefinition (functions.get (i), environment);			// function definition posted
		}
	}


	/**
	 * @param function description of transform to be added
	 * @param environment a core environment object
	 * @param <T> type of data in environment
	 */
	static <T> void postFunctionDefinition
	(FunctionDescription<T> function, Environment<T> environment)
	{
		postFunctionDefinition
		(
			function.getFunctionName (), parameterProfile, function,
			function.getFunctionDescription (), environment
		);
	}
	static String parameterName = "x", parameterProfile[] = new String[]{parameterName};


	/**
	 * @param name the name of the function
	 * @param parameters the list of parameters to the function
	 * @param f the function object that provides EVAL capability
	 * @param functionBody text of the definition of the function
	 * @param environment a core environment object
	 * @param <T> type of data in environment
	 */
	public static <T> void postFunctionDefinition
		(
			String name, String[] parameters, Function<T> f,
			StringBuffer functionBody, Environment<T> environment
		)
	{
		DefinedTransform<T> defnition =
			new DefinedTransform<T>
				(
					name, SimpleUtilities.toList (parameters),
					TokenParser.parse (functionBody), f
				);
		environment.processDefinedFunction (defnition);
	}


}

