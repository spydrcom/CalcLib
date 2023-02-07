
package net.myorb.math.expressions.charting.multidimensional;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.charting.PlotComplexMapping;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.data.abstractions.Configurable;
import net.myorb.math.Function;

import java.util.Map;

/**
 * display a plot of a function mapping
 * - plot lines connect the function parameter to the function result
 * @author Michael Druckman
 */
public class Zplot implements Configurable,
	Environment.AccessAcceptance < ComplexValue <Double> >
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment
		(Environment < ComplexValue <Double> > environment)
	{ this.symbols = environment.getSymbolMap (); }
	protected SymbolMap symbols;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Configurable#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		this.slices = lookup ("slices");
		this.portions = lookup ("portions");
		this.divergenceFilterEnabled = lookup ("filter");
		this.segments = lookup ("segments");
		this.rasterSize = lookup ("size");
		this.title = lookup ("title");
	}
	String lookup (String name)
	{
		Object symbol = symbols.get (name);
		return symbol==null? null: symbol.toString ();
	}


	/**
	 * plot function as named in symbol table
	 * @param functionName the name of the function
	 */
	public void displayPlot (String functionName)
	{
		@SuppressWarnings("unchecked")
		Function < ComplexValue <Double> > function =
			(Function < ComplexValue <Double> >)
				symbols.get (functionName);
		PlotComplexMapping.displayPlot
		(
			rasterSize, (z) -> function.eval (z),
			slices, portions, segments, divergenceFilterEnabled,
			title == null ? functionName : title
		);
	}
	public String slices = "24", portions = "60", segments = "20";
	public String divergenceFilterEnabled = "FALSE";
	public String rasterSize = "800";
	public String title = null;


}

