
package net.myorb.math.expressions.charting.multidimensional;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.symbols.ImportedFunctionWrapper;
import net.myorb.math.expressions.charting.PlotComplexMapping;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.data.abstractions.Configurable;
import net.myorb.math.Function;

import java.util.Map;

/**
 * display a plot of a function mapping
 * - plot lines connect the function parameter to the function result
 * @author Michael Druckman
 */
public class Zplot implements Configurable, SymbolMap.Plotter,
	Environment.AccessAcceptance < ComplexValue <Double> >
{


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ConfiguredImport#getConfiguration()
	 */
	public Map<String, Object>
		getConfiguration () { return configuration; }
	protected Map<String, Object> configuration;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.evaluationstates.Environment.AccessAcceptance#setEnvironment(net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void setEnvironment
		(Environment < ComplexValue <Double> > environment)
	{ this.symbols = environment.getSymbolMap (); post (); }
	protected SymbolMap symbols;


	/**
	 * post this plotter feature to symbol table
	 */
	public void post ()
	{
		if (this.featureName == null)
		{ throw new RuntimeException ("Name expected for plotter"); }
		this.symbols.put (featureName, this);
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Configurable#addConfiguration(java.util.Map)
	 */
	public void addConfiguration (Map <String, Object> parameters)
	{
		this.divergenceFilterEnabled =
			lookup ("filter", this.divergenceFilterEnabled, parameters);
		this.featureName = lookup ("named", this.featureName, parameters);
		this.segments = lookup ("segments", this.segments, parameters);
		this.rasterSize = lookup ("size", this.rasterSize, parameters);
		this.portions = lookup ("portions", this.portions, parameters);
		this.slices = lookup ("slices", this.slices, parameters);
		this.title = lookup ("title", this.title, parameters);
		this.configuration = parameters;
	}
	String lookup
		(
			String name, String defaultValue,
			Map <String, Object> from
		)
	{
		Object symbol = from.get (name);
		if (symbol == null) return defaultValue;
		else return symbol.toString ();
	}


	/**
	 * plot function as named in symbol table
	 * @param functionName the name of the function
	 */
	public void displayPlot (String functionName)
	{
		Function < ComplexValue <Double> >
			function = verify (symbols.get (functionName));
		PlotComplexMapping.displayPlot
		(
			rasterSize, (z) -> function.eval (z),
			slices, portions, segments, divergenceFilterEnabled,
			title == null ? functionName : title
		);
	}
	@SuppressWarnings("unchecked") Function < ComplexValue <Double> >
					verify (Object symbol)
	{
		if (symbol instanceof ImportedFunctionWrapper)
		{
			ImportedFunctionWrapper < ComplexValue <Double> > wrapper = 
				(ImportedFunctionWrapper < ComplexValue <Double> >) symbol;
			return wrapper.getFunctionAccess ();
		}
		if (symbol instanceof Subroutine)
		{
			Subroutine < ComplexValue <Double> >
				subroutine = (Subroutine < ComplexValue <Double> >) symbol;
			return subroutine.toSimpleFunction ();
		}
		throw new RuntimeException ("Function expected");
	}
	public String slices = "24", portions = "60", segments = "20";
	public String divergenceFilterEnabled = "FALSE";
	public String featureName = "Zplot";
	public String rasterSize = "800";
	public String title = null;

	/*
	 * 
		init "net.myorb.math.expressions.charting.multidimensional.Zplot" named "HIDEF" filter "TRUE" slices "96"

		MAPZ myfunction HIDEF

	 *
		named		"Zplot"
		filter		"FALSE"
		segments	"20"
		portions	"60"
		slices		"24"
		size		"800"
		title		NULL
	 *
	 */

}

