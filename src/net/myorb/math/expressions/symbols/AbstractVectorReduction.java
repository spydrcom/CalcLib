
package net.myorb.math.expressions.symbols;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;

/**
 * base class for range specification operations
 * 	i.e. integral, sigma, PI, etc...
 * @author Michael Druckman
 */
public abstract class AbstractVectorReduction extends AbstractParameterizedFunction
{

	/**
	 * representation of range parsed for standard
	 * [lo LE identifier LE hi DELTA delta]
	 */
	public interface Range
	{
		/**
		 * @return expression describing lo bound
		 */
		String getLoBound ();

		/**
		 * @return expression describing increment (delta) value
		 */
		String getIncrement ();

		/**
		 * @return name of identifier specified
		 */
		String getIdentifier ();

		/**
		 * @return expression describing hi bound
		 */
		String getHiBound ();
	}

	/**
	 * @param name the function name specified in configuration
	 */
	public AbstractVectorReduction (String name)
	{
		super (name);
	}

	/**
	 * build Range Specification Notation display
	 * @param operator the operation name to be changed to proper notation
	 * @param range the description of the specified range (as Range object)
	 * @param parameters the expression evaluated over range
	 * @param using the formatter object to be used
	 * @return the formatted node text
	 */
	public String markupForDisplay (String operator, Range range, String parameters, NodeFormatting using) { return null; }

}

