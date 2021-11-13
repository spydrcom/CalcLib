
package net.myorb.math.expressions.commands;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.charting.fractals.Mandelbrot;
import net.myorb.math.expressions.charting.DisplayGraph;

/**
 * support for commands handling Mandelbrot plots
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MandelbrotGraphics<T> extends Utilities<T>
{


	public MandelbrotGraphics (Environment<T> environment)
	{
		super (environment);
		this.symbols = environment.getSymbolMap ();
		this.valueManager = environment.getValueManager ();
		this.manager = environment.getSpaceManager ();
	}
	protected ExpressionSpaceManager<T> manager;
	protected ValueManager<T> valueManager;
	protected SymbolMap symbols;


	/**
	 * prepare Mandelbrot plot
	 * @param tokens the list of tokens from the command
	 */
	public void plot (CommandSequence tokens)
	{
		int pointsPerAxis = 700 , pointSize = 3;
		ValueManager.GenericValue symbolValue = null;

		if (tokens.size() > 1)
		{
			if (tokens.get (1).getTokenType() == TokenParser.TokenType.INT)
				pointsPerAxis = Integer.parseInt (tokens.get (1).getTokenImage ());
			else symbolValue = symbols.getValue (tokens.get (1).getTokenImage ());
		}

		if (tokens.size() > 2)
		{
			if (tokens.get (2).getTokenType() == TokenParser.TokenType.INT)
				pointsPerAxis = Integer.parseInt (tokens.get (2).getTokenImage ());
			else symbolValue = symbols.getValue (tokens.get (2).getTokenImage ());
		}

		if (pointsPerAxis < 550) pointSize = 5;
		else if (pointsPerAxis < 650) pointSize = 4;

		if (symbolValue == null)
		{
			Mandelbrot.fullSizeView (pointsPerAxis, pointSize);
			return;
		}

		ValueManager.DimensionedValue<T>
			array = valueManager.toDimensionedValue (symbolValue);
		double x = convert (0, array), y = convert (1, array), axisWidth = convert (2, array);
		Mandelbrot.plot (new DisplayGraph.Point (x, y), (float)axisWidth, pointsPerAxis, pointSize);
	}

	private double convert
		(
			int index, ValueManager.DimensionedValue<T> array
		)
	{
		return manager.convertToDouble (array.getValues ().get (index));
	}

}
