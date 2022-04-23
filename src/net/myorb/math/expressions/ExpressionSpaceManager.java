
package net.myorb.math.expressions;

import net.myorb.data.abstractions.SpaceConversion;

import net.myorb.math.SpaceManager;

/**
 * extend a space manager to include value parser for expression analysis
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public interface ExpressionSpaceManager<T>
	extends SpaceManager<T>, ValueManager.Formatter<T>, SpaceConversion<T>
{

	/**
	 * connect with evaluation object
	 * @param control the associated evaluation object
	 */
	void setEvaluationControl (EvaluationControlI<T> control);
	EvaluationControlI<T> getEvaluationControl ();

	/**
	 * evaluate an expression
	 * @param expression the text of the expression
	 * @return computed value
	 */
	T evaluate (String expression);

	/**
	 * parse a value token
	 * @param type the type of token recognized by parser
	 * @param image the text of the value parsed to be converted
	 * @return the value represented in the space manager
	 */
	T parseValueToken (TokenParser.TokenType type, String image);

	/**
	 * determine that a value
	 *  may be treated as integer
	 * @param value the value in question
	 * @return NULL = not integer, otherwise the integer value
	 */
	Integer convertToInteger (T value);

	/**
	 * get a data conversion object based on THIS space manager
	 * @return a new data conversion object
	 */
	DataConversions<T> getDataConversions ();

	/**
	 * a description of the nature of a value
	 */
	public enum ValueCharacterization
	{
		INTEGER,	// within tolerance of an integer value
		REAL,		// within tolerance of a real value (insignificant (or no) imaginary component)
		COMPLEX		// contains significant imaginary component (or no real component)
	}
	
	/**
	 * verify type is supported
	 * @param requiredType the name of the type needing support
	 * @return TRUE if supported otherwise FALSE
	 */
	boolean providesSupportFor (String requiredType);

	/**
	 * determine the nature of a value
	 * @param value the value in question
	 * @return the ValueCharacterization
	 */
	ValueCharacterization characterize (T value);
	
	/**
	 * provide for formatting options
	 * @param value the value to be formatted
	 * @param mode the name of the mode to be used, null = simple decimal
	 * @return value formatted to parameters of specified mode
	 */
	String formatToMode (T value, String mode);
	
	/**
	 * get a list of names of available modes
	 * @return a list of available modes, null = not implemented
	 */
	String[] getformattingModes ();

}
