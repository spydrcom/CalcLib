
package net.myorb.math.polynomial;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.math.expressions.commands.CommandSequence;
import net.myorb.math.expressions.symbols.DefinedFunction;
import net.myorb.math.polynomial.algebra.SeriesExpansion;

/**
 * generator for polynomial functions
 * @param <T> the data type to operate on
 * @author Michael Druckman
 */
public class PolynomialGenerator <T>
{


	public PolynomialGenerator (Environment <T> environment)
	{
		this.environment = environment;
	}
	protected Environment <T> environment;


	/**
	 * declare a function formatted as standard polynomial
	 * @param functionName the name of the function being declared
	 * @param parameterName the name of the parameter to use for the profile
	 * @param coefficientName the name to use in generating coefficient references
	 * @param tokens parameters listed on command line
	 * @param position the starting parameter
	 */
	public void declare
		(
			String functionName, String parameterName, String coefficientName,
			CommandSequence tokens, int position
		)
	{
		this.parameterList.add (this.parameterName = parameterName); this.coefficientName = coefficientName;
		StringBuffer poly0 = new StringBuffer (), poly1 = new StringBuffer (), poly2 = new StringBuffer ("2*");
		addCoef (0, poly0).append (" + "); addCoef (1, poly1).append (" + "); addCoef (2, poly2).append (" + ");
		addProduct (1, 1, poly0); addProduct (2, 2, poly1); addProduct (6, 3, poly2);
		int degree = Integer.parseInt (tokens.get (position).getTokenImage ());

		for (int i = 2; i <= degree; i++)
		{ int i1 = i + 1, i2 = i + 2; addTerm (i, 1, i, poly0); addTerm (i, i1, i1, poly1); addTerm (i, i1*i2, i2, poly2); }
		define (functionName, poly0); define (functionName+"'", poly1); define (functionName+"''", poly2);
		new SeriesExpansion <T> (environment).expandSequence (functionName);
	}


	/**
	 * append full power term
	 * @param power the degree of the term
	 * @param multiple the constant multiple for the term
	 * @param n the index of the coefficient to use
	 * @param buffer the buffer to append
	 * @return the buffer for chaining
	 */
	StringBuffer addTerm (int power, int multiple, int n, StringBuffer buffer)
	{
		buffer.append (" + (");
		addCoefWithMultiplier (multiple, n, buffer);
		buffer.append (parameterName).append ("^").append (power);
		return buffer.append (")");
	}
	protected String parameterName;

	/**
	 * add parameter multiple to buffer
	 * @param multiple the constant multiple for the term
	 * @param n the index of the coefficient to use
	 * @param buffer the buffer to append
	 * @return the buffer for chaining
	 */
	StringBuffer addProduct (int multiple, int n, StringBuffer buffer)
	{
		buffer.append ("(");
		addCoefWithMultiplier (multiple, n, buffer).append (parameterName);
		return buffer.append (")");
	}

	/**
	 * append buffer with multiple of coefficient
	 * @param multiple the constant multiple for the term
	 * @param n the index of the coefficient to use
	 * @param buffer the buffer to append
	 * @return the buffer for chaining
	 */
	StringBuffer addCoefWithMultiplier (int multiple, int n, StringBuffer buffer)
	{
		if (multiple != 1) buffer.append (multiple).append ("*");
		addCoef (n, buffer).append ("*");
		return buffer;
	}

	/**
	 * append coefficient with index to buffer
	 * @param n the index of the coefficient to use
	 * @param buffer the buffer to append
	 * @return the buffer for chaining
	 */
	StringBuffer addCoef (int n, StringBuffer buffer)
	{
		buffer.append (coefficientName);
		if (n < 10) { buffer.append ("_"); } // underscore render only works for single digit
		buffer.append (n);
		return buffer;
	}
	protected String coefficientName;


	/**
	 * post function to symbol table
	 * @param name the name of the function to be posted
	 * @param source the buffer of text that gives the function expression
	 * @return the function definition object posted
	 * @param <T> the data type used
	 */
	DefinedFunction <T> define (String name, StringBuffer source)
	{
		return DefinedFunction.defineUserFunction
		(
			name, parameterList, CommandSequence.parse (source), environment
		);
	}
	protected Subroutine.ParameterList parameterList = new Subroutine.ParameterList ();


}

