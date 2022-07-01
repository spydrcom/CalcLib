
package net.myorb.math.expressions.commands;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.charting.ComplexPlaneTransform;
import net.myorb.math.expressions.charting.ContourPlotProperties;
import net.myorb.math.expressions.charting.DisplayGraph3D;

import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.SymbolMap;

import net.myorb.gui.components.SimpleScreenIO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * support for commands building complex plots
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Plotting<T> extends Utilities<T>
{


	public interface PlotCommand
	extends KeywordCommand {}


	public Plotting
	(Environment<T> environment)
	{ super (environment); }


	/**
	 * capture parameters for complex plot
	 * @param tokens the tokens found on the command line
	 * @return map of parameter names
	 */
	public static Map<String,Object> complexPlot (List<TokenParser.TokenDescriptor> tokens)
	{
		String
		functionName = tokens.get (1).getTokenImage (),
		outerDomainName = tokens.get (2).getTokenImage (),
		innerDomainName = tokens.get (3).getTokenImage (),
		colorDomainName = tokens.get (4).getTokenImage ();

		HashMap<String,Object> map =
			new HashMap<String,Object>();
		map.put ("functionName", functionName);
		map.put ("outerDomainName", outerDomainName);
		map.put ("innerDomainName", innerDomainName);
		map.put ("colorDomainName", colorDomainName);
		return map;
	}


	/**
	 * create background thread to keep plot from causing GUI freeze
	 * @param parameters the map of parameters for the plot
	 */
	public void backgroundPlot (final Map<String,Object> parameters)
	{
		SimpleScreenIO.startBackgroundTask
		(
			() ->
			{
				SymbolMap symbols = environment.getSymbolMap ();
				ComplexPlaneTransform.constructPlot (parameters, symbols);
				addTrackingRecord (parameters);
			}
		);
	}


	/**
	 * @param parameters add to 3D map
	 */
	public static void addTrackingRecord (Map<String,Object> parameters)
	{
		DisplayGraph3D.addTrackingFor
		(
			parameters, parameters.get ("Title").toString (),
			ContourPlotProperties.POLAR_IDENTITY
		);
	}


	/**
	 * request angular complex plot
	 * @return a keyword command for the POLARANGULAR keyword
	 */
	public PlotCommand constructAngularKeywordCommand ()
	{
		return new PlotCommand ()
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.KeywordMap.KeywordCommand#describe()
			 */
			public String describe ()
			{ return "Display a plot of a complex plane mapping using angular traces at varied origin distances"; }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.KeywordMap.KeywordCommand#execute(java.util.List)
			 */
			public void execute (CommandSequence tokens)
			{
				Map<String,Object>
				parameters = complexPlot (tokens);
				parameters.put ("InnerVariable", "THETA");
				backgroundPlot (parameters);
			}
		};
	}


	/**
	 * request radial complex plot
	 * @return a keyword command for the POLARRADIAL keyword
	 */
	public PlotCommand constructRadialKeywordCommand ()
	{
		return new PlotCommand ()
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.KeywordMap.KeywordCommand#describe()
			 */
			public String describe ()
			{ return "Display a plot of a complex plane mapping using radial traces at varied angles"; }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.KeywordMap.KeywordCommand#execute(java.util.List)
			 */
			public void execute (CommandSequence tokens)
			{
				Map<String,Object>
				parameters = complexPlot (tokens);
				parameters.put ("InnerVariable", "R");
				backgroundPlot (parameters);
			}
		};
	}


}

