
package net.myorb.math.polynomial;

import net.myorb.math.expressions.ValueManager;

import net.myorb.math.expressions.evaluationstates.DeclarationSupport;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.tree.CalculationEngine;
import net.myorb.math.expressions.tree.SubExpression;
import net.myorb.math.expressions.tree.Element;

import net.myorb.math.Polynomial;

/**
 * utilities for manipulations of coefficient arrays
 * @param <T> type of values from expressions
 * @author Michael Druckman
 */
public class CoefficientUtilities<T>
{

	/**
	 * @param environment access to general utility methods
	 */
	CoefficientUtilities (Environment<T> environment)
	{ this.environment = environment; }
	Environment<T> environment;

	/**
	 * read coefficient array from variable storage
	 * @param fromSymbol the name of the symbol containing the coefficients
	 * @return a coefficients object containing the values
	 */
	Polynomial.Coefficients<T> getCoefficients (String fromSymbol)
	{
		Polynomial.Coefficients<T>
			coefficients = new Polynomial.Coefficients<T> ();
		ValueManager.GenericValue value = environment.getValue (fromSymbol);
		coefficients.addAll (environment.getValueManager ().toDimensionedValue (value).getValues ());
		return coefficients;
	}

	/**
	 * tokens have form indicating coefficients are stored in variable
	 * @param tokens the token stream defining the original function
	 * @return the coefficients taken from the named variable
	 */
	Polynomial.Coefficients<T> getCoefficients (DeclarationSupport.TokenStream tokens)
	{ return getCoefficients (tokens.get (0).getTokenImage ()); }

	/**
	 * expression has form indicating coefficients are stored in variable
	 * @param coefficientsReference the expression tree node referring to variable
	 * @return the coefficients taken from the named variable
	 */
	Polynomial.Coefficients<T> getCoefficientsFrom (Element coefficientsReference)
	{
		switch (coefficientsReference.getElementType ())
		{
			case	IDENTIFIER:		return getCoefficients (getIdentifier (coefficientsReference));
			default:				return computeCoefficients (coefficientsReference, environment);
		}
	}


	/*
	 * static utilities
	 */

	/**
	 * expression tree node contains aggregate of coefficient values
	 * @param coefficientsReference the aggregate node from the expression tree
	 * @param environment access to common utility objects using central environment object
	 * @return the coefficients computed from the expression tree aggregate node
	 * @param <T> data type
	 */
	public static <T> Polynomial.Coefficients <T>
	computeCoefficients (Element coefficientsReference, Environment <T> environment)
	{
		CalculationEngine <T> engine =
			CalculationEngine.newCalculationEngine
				(SubExpression.cast (coefficientsReference), environment);
		return new Polynomial.Coefficients <T>
		(
			environment.getValueManager ().toDiscreteValues (engine.evaluateElement (coefficientsReference))
		);
	}

	/**
	 * verify a match between a
	 *  symbol reference in an expression and a specific name
	 * @param element the symbol reference from an expression
	 * @param symbolName a text name to match the symbol
	 * @return TRUE = identifier matches symbol
	 */
	public static boolean identifierMatches (Element element, String symbolName)
	{
		return getIdentifier (element).equals (symbolName);
	}

	/**
	 * get the name of an identifier referenced in an expression node
	 * @param element an identifier reference node in an expression
	 * @return the name of the identifier
	 */
	public static String getIdentifier (Element element)
	{ return element.toString (); }

}
