
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.ConventionalNotations;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.charting.DisplayGraph;

import net.myorb.charting.DisplayGraphTypes.PlotCollection;
import net.myorb.charting.DisplayGraphTypes.Colors;

import net.myorb.charting.PlotLegend;

import java.util.ArrayList;

/**
 * segment breaks being treated as components
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MultiSegmentUtilities <T> extends MultiComponentUtilities <T>
{


	/*
	 * UNIT TEST:
	 *  complex numbers
	 *  !! Gamma(z) = GAMMA z
	 *  Function Plot from function menu
	 *  [ -5 < z < 5 <> 0.01 ] using LIM 25 and SEGMENT 3
	 */


	/**
	 * manage display components of multi-segment plots
	 */
	public static class SegmentManager
	{

		/**
		 * establish defaults to use
		 * when something other that Simple Legend is used
		 */
		public SegmentManager ()
		{
			func = "f"; var = "x";
			exprs = new String[]{"f"};
		}
		String func, var, exprs[];

		public String[] getExprs () { return exprs; }
		public String getFunc () { return func; }
		public String getVar () { return var; }

		/**
		 * @return a formatted Y-axis display tag
		 */
		public String getAxisDisplay ()
		{
			return func + " (" + var + ")";
		}

		/**
		 * evaluate data in the sample trigger for display content
		 * @param trigger the mouse event trigger built for the display
		 */
		public void examine (MouseSampleTrigger <?> trigger)
		{
			if (trigger != null)
			{
				if (trigger instanceof DisplayGraph.SimpleLegend)
				{
					String name = ((DisplayGraph.SimpleLegend <?>) trigger)
							.getPrimarySegmentName ();
					if (name != null) func = name;
				}
	
				PlotLegend.SampleDisplay display = trigger.getDisplay ();
				var = ConventionalNotations.determineNotationFor
						(display.getVariable ());
				exprs = display.getPlotExpressions ();
			}
		}

	}


	public MultiSegmentUtilities
	(PlotCollection plots, Environment <T> environment)
	{
		super (new SegmentedPlotContext (plots.size ()), environment);
		this.plots = plots;
	}
	protected PlotCollection plots;

	/**
	 * build a segment plot
	 * @param functionName the name of the function
	 * @param parameter the formal parameter for the function
	 */
	public void multiSegmentPlot
	(String functionName, String parameter)
	{
		setPrimaryFunction (functionName);
		this.primarySegment = functionName;

		multiComponentPlot
		(
			functionName, parameter,
			this.plots
		);
	}

	/**
	 * identify the function in the legend
	 * @param functionName the name of the function being plotted
	 */
	void setPrimaryFunction (String functionName)
	{
		/*
		 * this will replace 1 in legend
		 * with real function name to be more specific.
		 * Y-axis name is set in each ChartLibSupport extension
		 */
		this.contextProperties
			.componentIdentifiers()[0] =
				functionName;
	}

}

/**
 * plot context for segment breaks being treated as components
 */
class SegmentedPlotContext implements MultiComponentUtilities.ContextProperties
{

	SegmentedPlotContext (int plotCount)
	{ this.plotCount = plotCount; initialSegmentNames (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MultiComponentUtilities.ContextProperties#getComponentCount()
	 */
	public int getComponentCount ()
	{
		return this.plotCount;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MultiComponentUtilities.ContextProperties#assignColors(net.myorb.charting.DisplayGraphTypes.Colors)
	 */
	public void assignColors (Colors colors)
	{
		MultiComponentUtilities.assignStandardColors
				(colors, this.plotCount);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MultiComponentUtilities.ContextProperties#componentIdentifiers()
	 */
	public String[] componentIdentifiers ()
	{
		return this.segmentsNameList;
	}

	/**
	 * construct names for the segments
	 */
	void initialSegmentNames ()
	{
		ArrayList<String> segmentsNames =
			new ArrayList<String> ();
		for (int i = plotCount; i > 0; i--)
		{ segmentsNames.add ("#" + Integer.toString (plotCount - i + 1)); }
		this.segmentsNameList = segmentsNames.toArray (new String[]{});
	}
	protected String[] segmentsNameList;
	protected int plotCount;

}
