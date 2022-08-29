
package net.myorb.math.expressions;

import net.myorb.charting.DisplayGraphTypes;
import net.myorb.charting.DisplayGraphTypes.Point.Series;

import net.myorb.math.expressions.TypedRangeDescription.TypedRangeProperties;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.symbols.DefinedFunction;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.data.abstractions.Function;

import java.util.List;

/**
 * build series from components of values
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ExpressionAnalysis <T>
{


	/*
	 * enable vector mechanisms for transforms
	 */


	/**
	 * force a symbol to be enabled for Vector Plot conventions
	 * @param functionSymbol the symbol for the function being plotted
	 * @return the symbol as an implementer of VectorPlotEnabled
	 * @param <T> type on which operations are to be executed
	 */
	@SuppressWarnings("unchecked")
	public static <T> VectorPlotEnabled <T>
		forceEnabled (SymbolMap.Named functionSymbol)
	{
		return
		functionSymbol instanceof VectorPlotEnabled ?
		( VectorPlotEnabled <T> ) functionSymbol : enable (functionSymbol);
	}


	/**
	 * wrap a function in ExpressionAnalysis to enable Vector Plot conventions
	 * @param functionSymbol the symbol for the function being plotted
	 * @return the symbol wrapped in ExpressionAnalysis transform
	 * @param <T> type on which operations are to be executed
	 */
	public static <T> VectorPlotEnabled <T> enable (SymbolMap.Named functionSymbol)
	{
		return new ExpressionAnalysis <T> ().getTransformEngine
		(
			DefinedFunction.verifyDefinedFunction (functionSymbol)
		);
	}


	/*
	 * ExpressionComponentElaboration wrapper for function
	 */


	/**
	 * construct graph point series
	 *  for values that have multiple components
	 * @param f function being wrapped used to calculate series
	 * @param domainDescription the parameters of the real domain
	 * @param series the series of points per component to be constructed
	 * @param spaceManager data type management object
	 */
	public void evaluateSeries
		(
			Function <T> f,
			TypedRangeDescription.TypedRangeProperties <T> domainDescription,
			List <DisplayGraphTypes.Point.Series> series, 
			SpaceDescription <T> spaceManager
		)
	{
		ExpressionComponentSpaceManager <T>
			componentManager = getComponentManager (spaceManager);
		ExpressionComponentElaboration.evaluateSeries
		(
			(x, s) ->
			{
				T y = f.eval (x);
				double domain = componentManager.component (x, 0);

				for (int n = 0; n < s.size (); n++)
				{
					s.get (n).add
					(
						new DisplayGraphTypes.Point
						(domain, componentManager.component (y, n))
					);
				}
			},
			domainDescription, series, componentManager
		);
	}


	/*
	 * utility methods
	 */


	/**
	 * convert between space managers
	 * @param mgr a space description object
	 * @return the space description treated as a component manager
	 * @param <T> type on which operations are to be executed
	 */
	public static final <T> ExpressionComponentSpaceManager <T>
		getComponentManager (SpaceDescription <T> mgr)
	{
		return ( ExpressionComponentSpaceManager <T> ) mgr;
	}


	/**
	 * construct a plot object
	 * @param f the function that requires a wrapper
	 * @return a transform engine that enables vector plot functionality
	 */
	public VectorPlotEnabled<T> getTransformEngine (Function <T> f)
	{
		return new TransformEngine <T> (f, this);
	}


}


/**
 * provide vector enabled plot functionality for generic functions
 * - wrapper class for simple functions using ExpressionAnalysis implementation
 * @param <T> type used for calculations
 */
class TransformEngine <T> implements VectorPlotEnabled <T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.VectorPlotEnabled#evaluateSeries(net.myorb.math.expressions.TypedRangeDescription.TypedRangeProperties, java.util.List, net.myorb.math.expressions.evaluationstates.Environment)
	 */
	public void evaluateSeries
		(
			TypedRangeProperties <T> domainDescription,
			List <Series> series, Environment <T> environment
		)
	{
		ea.evaluateSeries
		(
			f, domainDescription, series,
			environment.getSpaceManager ()
		);
	}

	/**
	 * represent a function
	 * @param f the function to enable
	 * @param ea the Expression Analysis object to use
	 */
	public TransformEngine (Function <T> f, ExpressionAnalysis <T> ea)
	{
		this.ea = ea;
		this.f = f;
	}
	ExpressionAnalysis <T> ea;
	Function <T> f;

}

