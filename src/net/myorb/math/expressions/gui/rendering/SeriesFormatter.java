
package net.myorb.math.expressions.gui.rendering;

import net.myorb.math.polynomial.algebra.Elements;
import net.myorb.math.polynomial.algebra.SeriesExpansion;

import net.myorb.math.expressions.commands.CommandSequence;
import net.myorb.math.expressions.commands.Rendering;

import net.myorb.data.abstractions.CommonDataStructures.ItemList;

import net.myorb.math.expressions.TokenParser;

/**
 * implementation of render functionality for Polynomial Series Expansion
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class SeriesFormatter <T>
{


	public SeriesFormatter
		(SeriesExpansion <T> expansionProcessor)
	{  this.expansionProcessor  =  expansionProcessor;  }
	protected SeriesExpansion <T> expansionProcessor;


	/**
	 * rendering implementation for series expansion
	 * @param functionName the name of the function being expanded
	 * @param renderer the Rendering engine to use
	 */
	public void renderExpandedSeries (String functionName, Rendering <T> renderer)
	{
		String title = "Expanded series from " + functionName;
		try { renderExpandedSeries (title, functionName, renderer); }
		catch (Exception e) { throw new RuntimeException ( "Render failed", e ); }
	}


	/**
	 * perform series expansion
	 * - and request multiple line rendering
	 * @param title the title to display for the render
	 * @param functionName the name of the function being rendered
	 * @param renderer the rendering object to use for this operation
	 * @throws Exception for error conditions
	 */
	public void renderExpandedSeries
		(
			String title, String functionName,
			Rendering <T> renderer
		)
	throws Exception
	{
		renderExpandedSeriesMultipleLine
		(
			expandedSeries (functionName),
			renderer, title
		);
	}


	/**
	 * render full equation term-by-term multiple-line
	 * @param terms the list of terms making up the equation
	 * @param renderer the rendering object to use for this operation
	 * @param title the title to display for the render of this equation
	 * @throws Exception for error conditions
	 */
	public void renderExpandedSeriesMultipleLine
		(
			ItemList < CommandSequence > terms,
			Rendering <T> renderer, String title
		)
	throws Exception
	{
		for (CommandSequence term : terms)
		{
			renderer.render
			(
				term, expansionProcessor.parameterList (), title
			);
		}
	}


	/**
	 * render full equation on single line
	 * @param title the title to display for the render
	 * @param functionName the name of the function being rendered
	 * @param renderer the rendering object to use for this operation
	 * @throws Exception for error conditions
	 */
	public void renderExpandedSeriesSingleLine
		(
			String title, String functionName,
			Rendering <T> renderer
		)
	throws Exception
	{
		renderer.render
		(
			expandSequence ( functionName, expansionProcessor ),
			expansionProcessor.parameterList (), title
		);
		System.out.println (expandSequence ( functionName, expansionProcessor ));
	}


	/**
	 * produce expanded version of function sequence
	 * @param functionName the name of the function in the symbol table
	 * @param processor the expansion processor object being used
	 * @return the expanded sequence
	 */
	public static CommandSequence expandSequence
	(String functionName, SeriesExpansion <?> processor)
	{
		return parseSequence
		(
			expandedDescription (functionName, processor).toString ()
		);
	}


	/**
	 * parse content into command sequence
	 * @param source the text of command segment to parse
	 * @return the parsed sequence
	 */
	public static CommandSequence parseSequence (String source)
	{
		return new CommandSequence
		(
			TokenParser.parse
			(
				new StringBuffer (source)
			)
		);
	}


	/**
	 * buffer the text of expanded version of equation
	 * @param functionName the name of the function in the symbol table
	 * @param processor the expansion processor object being used
	 * @return text of expanded equation
	 */
	public static String expandedDescription
		(String functionName, SeriesExpansion <?> processor)
	{
		return processor.expandedTree (functionName).toString ();
	}


	/**
	 * expand a series and return description as sequence of terms
	 * @param functionName the name of the function in the symbol table
	 * @return a list of descriptions of the terms of the equation
	 */
	public ItemList < CommandSequence > expandedSeries (String functionName)
	{
		ItemList < CommandSequence > terms = new ItemList <> ();
		Elements.Factor expanded = expansionProcessor.expandedTree (functionName);
		for (Elements.Factor factor : (Elements.Sum) expanded)
		{ terms.add (parseSequence (factor.toString ())); }
		return terms;
	}


}

