
package net.myorb.math.polynomial;

import net.myorb.math.GeneratingFunctions;

import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.expressions.OperatorNomenclature;
import net.myorb.math.expressions.symbols.DefinedFunction;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.abstractions.SimpleUtilities;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * implementation of polynomial family import functionality
 * @author Michael Druckman
 */
public class PolynomialFamilyManager
{

	public static final String DEFAULT_POLYNOMIAL_PACKAGE = "net.myorb.math.polynomial.families";

	public static class PowerFunctionList<T> extends ArrayList<Polynomial.PowerFunction<T>>
	{ private static final long serialVersionUID = -5063327574313296872L; }

	@SuppressWarnings("rawtypes") static HashMap<String, PolynomialFamily>
	families = new HashMap<String, PolynomialFamily>();

	/**
	 * @param name the name of the family
	 * @param path the class-path to the implementation of the family
	 * @param environment the central environment object
	 * @param <T> type of data in environment
	 */
	public static <T> void importFamilyDescription (String name, String path, Environment<T> environment)
	{
		importFamilyDescription (name, path, environment.getSpaceManager ());
	}
	public static <T> void importFamilyDescription (String name, String path, SpaceManager<T> spaceManager)
	{
		try
		{
			//System.out.println ("Polynomial Family: " + name);
			if (path.startsWith ("*")) path = DEFAULT_POLYNOMIAL_PACKAGE;
			Object newInstance = Class.forName (path + "." + name + "Polynomial").newInstance ();
			PolynomialFamily<T> poly = getFamily (newInstance);
			families.put (name, poly);
			poly.init (spaceManager);
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			System.out.println ("Polynomial Family Description not found: " + name);
		}
	}
	
	/**
	 * @param <T> data type of polynomial
	 * @param from object that describes family
	 * @return object cast to family type
	 */
	@SuppressWarnings("unchecked")
	public static <T> PolynomialFamily<T> getFamily (Object from)
	{ return (PolynomialFamily<T>) from; }

	/**
	 * @param name the name of the family
	 * @param kind the kind (typically first &amp; second, null if only one) 
	 * @param count the highest order of function to be imported
	 * @param environment the central environment object
	 * @param <T> type of data in environment
	 */
	public static <T> void importFamilyFunctions (String name, String kind, int count, Environment<T> environment)
	{
		if (!families.containsKey (name))
			throw new RuntimeException ("Named family not available: " + name);
		PolynomialFamily<T> family = getFamily (families.get (name));
		ValueManager<T> vm = environment.getValueManager ();
		String id = family.getIdentifier (kind);

		PowerFunctionList<T>
			functions = family.getPolynomialFunctions (id, count);				// count of functions from conventional ID
		//dump (functions, environment.getSpaceManager ());
		
		for (int i=0; i<functions.size(); i++)
		{
			String cName = id.toLowerCase () + i;
			String fName = id.toUpperCase () + i;								// names built from conventional ID

			Polynomial.PowerFunction<T> f = functions.get (i);
			GeneratingFunctions.Coefficients<T> c = f.getCoefficients ();		// power function coefficients

			environment.setSymbol
			(cName, vm.newCoefficientList (c));									// coefficients array posted
			postPolynomialFunctionDefinition
			(fName, cName, environment);										// function definition posted
		}
	}

	/**
	 * @param functionName name of function declared
	 * @param coefficientsName the name of the array holding the coefficients
	 * @param environment the central environment object
	 * @param <T> type of data in environment
	 */
	public static <T> void postPolynomialFunctionDefinition
	(String functionName, String coefficientsName, Environment<T> environment)
	{
		StringBuffer functionBody = new StringBuffer ().append (coefficientsName)
				.append (OperatorNomenclature.POLY_EVAL_OPERATOR).append (parameterName);
		postFunctionDefinition (functionName, parameterProfile, functionBody, environment);
	}
	static String parameterName = "x", parameterProfile[] = new String[]{parameterName};

	/**
	 * @param name the name of the function
	 * @param parameters the name(s) of the parameters
	 * @param functionBody the text of the function body description
	 * @param environment the central environment object
	 * @param <T> type of data in environment
	 */
	public static <T> void postFunctionDefinition
		(
			String name, String[] parameters,
			StringBuffer functionBody, Environment<T> environment
		)
	{
		DefinedFunction<T> defnition =
			new DefinedFunction<T>
				(
					name, SimpleUtilities.toList (parameters),
					TokenParser.parse (functionBody)
				);
		environment.processDefinedFunction (defnition);
	}

	/**
	 * display a list of polynomial functions
	 * @param list the functions to be dumped to system out
	 * @param spaceManager a manager for the data type
	 * @param <T> type of data in environment
	 */
	public static <T> void dump
	(PowerFunctionList<T> list, SpaceManager<T> spaceManager)
	{
		PolynomialSpaceManager<T> psm = new PolynomialSpaceManager<T>(spaceManager);
		for (Polynomial.PowerFunction<T> pf : list) System.out.println (psm.toString (pf));
	}

	/**
	 * use a general recurrence formula class to generate polynomials
	 * @param recurrenceManager the manager that will generate the polynomials
	 * @param upTo number of polynomials to generate using the manager
	 * @param spaceManager a manager for the data type
	 * @param <T> type of data in environment
	 */
	public static <T> void dump
	(GeneralRecurrence<T> recurrenceManager, int upTo, SpaceManager<T> spaceManager)
	{
		dump (recurrenceManager.constructFuntions (upTo), spaceManager);
	}

}

