
package net.myorb.math.expressions.tree;

import net.myorb.math.computational.splines.FittedFunction;
import net.myorb.math.computational.splines.SplineMechanisms;
import net.myorb.math.computational.Spline;
import net.myorb.math.computational.integration.RealDomainIntegration;

import net.myorb.math.expressions.symbols.AbstractParameterizedFunction;
import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.math.expressions.ValueManager.GenericValue;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.expressions.SymbolMap;

import net.myorb.data.notations.json.*;

/**
 * wrapper of factory for spline function
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class SectionedSpline <T>
{


	public SectionedSpline
		(
			SplineMechanisms spline,
			ExpressionComponentSpaceManager <T> mgr
		)
	{
		this.function = new FittedFunction <> (mgr, spline);
		this.spline = spline;
		this.mgr = mgr;
	}
	protected ExpressionComponentSpaceManager <T> mgr;
	protected FittedFunction <T> function;
	protected SplineMechanisms spline;


	/**
	 * get access to the representation of the spline
	 * @return the spline operations descriptor
	 */
	public Spline.Operations <T> getSplineWrapper ()
	{
		return function;
	}


	/**
	 * @param value JSON tree of spline description nodes
	 */
	public void constructFrom (JsonSemantics.JsonValue value)
	{
		function.processSplineSegments ((JsonSemantics.JsonArray) value);
	}


	/**
	 * @param profile the JSON node holding profile information
	 * @return a symbol map named object that implements the spline
	 */
	public SymbolMap.Named getFuntion (JsonSemantics.JsonObject profile)
	{
		String name = profile.getMemberString ("Name"),
				parameter = profile.getMemberString ("Parameter");
		return new AbstractSectionedSpline<T> (name, parameter, function);
	}


}


/**
 * an Abstract Parameterized Function wrapper for the spline
 * @param <T> type on which operations are to be executed
 */
class AbstractSectionedSpline<T> extends AbstractParameterizedFunction implements RealDomainIntegration<T>
{


	public AbstractSectionedSpline
	(String name, String parameterName, FittedFunction<T> function)
	{ super (name); this.parameterName = parameterName; this.vm = new ValueManager<T>(); this.function = function; }
	protected FittedFunction<T> function;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolMap.ExecutableUnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
	 */
	public GenericValue execute (GenericValue parameter)
	{
		return vm.newDiscreteValue
		(
			function.eval
			(
				vm.toDiscrete (parameter)
			)
		);
	}
	protected ValueManager<T> vm;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.integration.RealDomainIntegration#evalIntegralOver(double, double)
	 */
	public T evalIntegralOver
		(
			double lo, double hi
		)
	{
		return function.evalIntegralOver (lo, hi);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.symbols.AbstractParameterizedFunction#getParameterList()
	 */
	public String getParameterList () { return parameterName; }
	protected String parameterName;


}


