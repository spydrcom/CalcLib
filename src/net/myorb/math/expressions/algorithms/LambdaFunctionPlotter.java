
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.charting.MultiComponentUtilities;
import net.myorb.math.expressions.charting.PlotMatrixForFunctionList;
import net.myorb.math.expressions.charting.DisplayGraph.SimpleLegend;

import net.myorb.math.expressions.evaluationstates.Arrays;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.symbols.DefinedFunction;

import net.myorb.math.expressions.GreekSymbols;
import net.myorb.math.expressions.VectorPlotEnabled;
import net.myorb.math.expressions.ValueManager;

import net.myorb.data.abstractions.DataSequence;

import net.myorb.math.Function;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Plotter functionality for Lambda functions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class LambdaFunctionPlotter <T> extends LambdaExpressions <T>
{


	/**
	 * meta-data collection
	 *  for using the vector enabled plot mechanisms
	 * @param <T> type on which operations are to be executed
	 */
	public interface VectorEnabledContext <T>
	extends MultiComponentUtilities.ContextProperties, VectorPlotEnabled <T>
	{}


	/**
	 * get a copy of the unicode character
	 */
	public static final String LAMBDA;
	static { LAMBDA = GreekSymbols.Symbol_Map.get ("lambda"); }


	/**
	 * produce a plot matrix of lambda functions
	 * @param domainValues the values of the domain
	 * @return the description of the range
	 */
	@Deprecated public ValueManager.ValueList
		computeLambdaRange (DataSequence <T> domainValues)
	{
		return new PlotMatrixForFunctionList <T> ().evaluate
			(domainValues, getSimpleFunctionList ());
	}


	/**
	 * produce a vector enabled wrapper for this plot
	 * @return a context wrapper for the function list
	 */
	public VectorEnabledContext <T> getVectorEnabledContext ()
	{
		return new LambdaFunctionContext <T> (getSimpleFunctionList (), this);
	}


	/*
	 * functions collected for PLOTL processing
	 */


	/**
	 * build a list of the lambda functions
	 *  that have the profile of unary operators
	 * @return list of indexes of functions matching the profile
	 */
	public List<Integer> unaryFunctionProfiles ()
	{
		List<Integer> functions = new ArrayList<Integer>();
		for (int i = 0; i < functionList.size (); i++)
		{
			DefinedFunction <T> f = functionList.get (i);
			if (f.parameterCount () == 1)						// functions with profiles
			{ functions.add (i); }								// matching unary operators
		}
		return functions;
	}


	/**
	 * get the lambda functions that will be in a multi-unary-plot
	 * @return a list of lambda UDFs that have unary function profiles
	 */
	public List < Function <T> > getSimpleFunctionList ()
	{
		List < Function <T> > f =
			new ArrayList < Function <T> > ();
		for (int id : unaryFunctionProfiles ())
		{ f.add (functionList.get (id).toSimpleFunction ()); }
		return f;
	}


	/*
	 * legend construction processing
	 */


	/**
	 * names for lambda functions are generated as lambda#n
	 * @return the list of names for the lambda plot legend
	 */
	public String[] getLambdaList ()
	{
		List <Integer> unary = unaryFunctionProfiles ();
		String [] legendNames = new String [unary.size ()];
		for (int n = 0; n < legendNames.length; n++)
		{
			String ID = Integer.toString (unary.get (n));
			legendNames[n] = LAMBDA + ID;
		}
		return legendNames;
	}


	/**
	 * construct the legend to display for the plot
	 * @param descriptor the descriptor for the domain
	 * @return a simple legend object for the plot
	 */
	public SimpleLegend <T> getSimpleLegend (Arrays.Descriptor <T> descriptor)
	{
		return SimpleLegend.buildLegendFor
			(
				new SimpleLegend.LegendProperties ()
				{

					/* (non-Javadoc)
					 * @see net.myorb.math.expressions.charting.DisplayGraph.SimpleLegend.LegendProperties#getPrimarySegmentName()
					 */
					public String getPrimarySegmentName ()
					{
						return LAMBDA;
					}

					/* (non-Javadoc)
					 * @see net.myorb.math.expressions.charting.DisplayGraph.SimpleLegend.LegendProperties#getPlotSymbols()
					 */
					public String [] getPlotSymbols ()
					{
						return getLambdaList ();
					}

					/* (non-Javadoc)
					 * @see net.myorb.math.expressions.charting.DisplayGraph.SimpleLegend.LegendProperties#getVariable()
					 */
					public String getVariable ()
					{
						return descriptor.getVariable ();
					}

				}
			);
	}


	/*
	 * MML formatting for formal parameter lists
	 * declared by lambda expressions
	 */


	/**
	 * format MML nodes that describe the formal parameters
	 * @param source the MN node that the render has so far constructed
	 * @param using an MML node formatting object to use for this translation
	 * @return MML that describes the formal parameters
	 */
	public String profileFor (String source, NodeFormatting using)
	{
		StringBuffer list;

		formalParameters
		(
			mnContents (source).split (","),
			list = new StringBuffer (), using
		);

		return using.formatParenthetical
		(
			list.toString ()
		);
	}


	/**
	 * construct MML parameter list from an identifier list
	 * @param identifiers the identifiers making up the parameter list
	 * @param theProfile a buffer collecting the parameter list
	 * @param using an MML node formatting object
	 */
	public void formalParameters
		(
			String [] identifiers,
			StringBuffer theProfile,
			NodeFormatting using
		)
	{
		for ( int i = 0; i < identifiers.length; )
		{
			theProfile.append
			(
				using.formatBracket
				(
					using.formatIdentifierReference
					  (identifiers[i++].trim ())
				)
			);
		}
	}


	/*
	 * the rendering system MML encoder
	 * processes the {p0,...,pn} formal parameter list of lambda expressions
	 * into an MML number (MN) node so access to the list requires tag removal
	 */


	/**
	 * get contents of a MN node
	 * @param mnNode the text of the node with contents
	 * @return the content after removal of the tags
	 */
	public String mnContents (String mnNode)
	{
		StringBuffer buffer = new StringBuffer ();
		removeTag (p.matcher (mnNode), buffer);
		return buffer.toString ();
	}
	void removeTag (Matcher pattern, StringBuffer into)
	{
		while (pattern.find ())
			pattern.appendReplacement (into, "");
		pattern.appendTail (into);
	}
	protected Pattern p = Pattern.compile ("<mn>\\S|\\S</mn>");


}

