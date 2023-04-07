
package net.myorb.math.polynomial;

import net.myorb.math.polynomial.algebra.SeriesExpansion;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.math.expressions.commands.CommandSequence;
import net.myorb.math.expressions.symbols.DefinedFunction;

/**
 * generator for polynomial functions
 * @param <T> the data type to operate on
 * @author Michael Druckman
 */
public class PolynomialGenerator <T>
{


	public PolynomialGenerator (Environment <T> environment)
	{
		SeriesExpansion.addInitialConditionsProcessors
			( environment.getSpaceManager () );
		this.environment = environment;
	}
	protected Environment <T> environment;


	/**
	 * declare polynomial functions described by naming conventions
	 * @param processorName the name of the naming conventions processor
	 * @param tokens parameters listed on command line
	 * @param position the starting parameter
	 * @throws RuntimeException for errors
	 */
	public void declare
		(
			String processorName, CommandSequence tokens, int position
		)
	throws RuntimeException
	{
		declare ( InitialConditionsProcessor.getNamingConventions (processorName), tokens, position );
	}


	/**
	 * declare polynomial functions described by naming conventions
	 * @param naming a name conventions provider for the polynomial family
	 * @param tokens parameters listed on command line
	 * @param position the starting parameter
	 * @throws RuntimeException for errors
	 */
	public void declare
		(
			NamingConventions naming, CommandSequence tokens, int position
		)
	throws RuntimeException
	{
		declare
		(
			naming.getPolynomialNameConvention (), naming.getParameterNameConvention (),
			naming.getCoefficientNameConvention (), tokens, position
		);
	}


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
		int degree = tokens.get (position).getTokenValue ().intValue ();
		this.parameterList.add (this.parameterName = parameterName); this.coefficientName = coefficientName;
		this.poly0 = new StringBuffer (); this.poly1 = new StringBuffer (); this.poly2 = new StringBuffer ("2").append (OP.TIMES);
		this.establishInitialConditions (); this.defineFunctions (functionName, degree);
	}
	protected StringBuffer poly0, poly1, poly2;


	/**
	 * first term coefficients present initial conditions
	 *			c0 = f(0) and c1 = df/dx(0)
	 */
	public void establishInitialConditions ()
	{
		this.addCoef (0, poly0).append (OP.PLUS); this.addCoef (1, poly1).append (OP.PLUS); this.addCoef (2, poly2).append (OP.PLUS);
		this.addProduct (1, 1, poly0); this.addProduct (2, 2, poly1); this.addProduct (6, 3, poly2);
	}


	/**
	 * complete function expression formatting
	 *  and post declarations to symbol table with appropriate names
	 * @param functionName the name of the function being declared
	 * @param degree the highest power of the polynomial
	 */
	public void defineFunctions (String functionName, int degree)
	{
		for (int i = 2; i <= degree; i++)
		{ int i1 = i + 1, i2 = i + 2; addTerm (i, 1, i, poly0); addTerm (i, i1, i1, poly1); addTerm (i, i1*i2, i2, poly2); }
		define (functionName, poly0); define (functionName+OP.FIRST, poly1); define (functionName+OP.SECOND, poly2);
		new SeriesExpansion <T> (environment).expandSequence (functionName);
	}


	// formatting for terms and coefficient products


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
		buffer.append (OP.PLUS).append (OP.OPEN);
		addCoefWithMultiplier (multiple, n, buffer);
		buffer.append (parameterName).append (OP.POW).append (power);
		return buffer.append (OP.CLOSE);
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
		buffer.append (OP.OPEN);
		addCoefWithMultiplier (multiple, n, buffer).append (parameterName);
		return buffer.append (OP.CLOSE);
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
		if (multiple != 1) buffer.append (multiple).append (OP.TIMES);
		addCoef (n, buffer).append (OP.TIMES);
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
		// underscore render only works for single digit
		if (n < 10) { buffer.append (OP.SUB); }
		buffer.append (n);
		return buffer;
	}
	protected String coefficientName;


	// symbol table profile posting


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

