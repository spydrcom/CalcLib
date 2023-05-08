
package net.myorb.math.computational;

import net.myorb.math.computational.DerivativeApproximation;
import net.myorb.math.computational.DerivativeApproximationMultiDim.Point;

import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.data.abstractions.CommonDataStructures;
import net.myorb.data.abstractions.SpaceDescription;

import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * computational algorithms for Derivative Approximation in multiple dimensions
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class DerivativeApproximationMultiDim <T>
{


	/**
	 * @param f function to be evaluated
	 */
	public DerivativeApproximationMultiDim ( Subroutine <T> f )
	{
		this.approx = new DerivativeApproximation <>
			( this.manager = (ExpressionSpaceManager <T>) f.getSpaceManager () );
		this.wrapper = new FunctionWrapper <> ( this.target = f );
	}
	protected DerivativeApproximation <T> approx;
	protected ExpressionSpaceManager <T> manager;
	protected FunctionWrapper <T> wrapper;
	protected Subroutine <T> target;


	/**
	 * simple description of points in multiple dimensions
	 * @param <T> type of component values
	 */
	@SuppressWarnings("serial")
	public static class Point <T>
		extends CommonDataStructures.Vector <T>
	{
		public Point () {}
		public Point (java.util.List <T> elements)
		{ super (elements); }
	}


	/**
	 * @param coordinates real coordinate values listed against axis index
	 * @return the point representation of the coordinates
	 */
	public Point <T> fromDouble (java.util.List <Double> coordinates)
	{
		Point <T> P =
			new Point <T> ();
		for (Double V : coordinates)
		{ P.add ( manager.convertFromDouble (V) ); }
		return P;
	}


	/**
	 * get the partial derivatives
	 * @param order the order of derivatives
	 * @param evaluatedAt the point at which to evaluate function
	 * @return the vector of derivatives in Point representation
	 */
	public Point <T> getPartialDerivatives
		(
			int order, Point <T> evaluatedAt
		)
	{
		Point <T> partials = new Point <> ();
		for (int n = 0; n < evaluatedAt.size (); n++)
		{ partials.add ( getPartialDerivative (order, n, evaluatedAt) ); }
		return partials;
	}


	/**
	 * query the function of the delta for a given variable
	 * @param relativeTo the relative index of a given variable
	 * @return the delta to use for the variable
	 */
	public T deltaFor (int relativeTo)
	{
		return manager.convertFromDouble
		(
			target.getPartialDerivativeDelta (relativeTo)
		);
	}


	/**
	 * @param order the order of derivative
	 * @param relativeTo the relative index of a given variable
	 * @param evaluatedAt the point at which to evaluate function
	 * @return the value of the partial derivative
	 */
	public T getPartialDerivative
		(
			int order, int relativeTo,
			Point <T> evaluatedAt
		)
	{
		wrapper.setRelativeVariableIndex (relativeTo);
		wrapper.setEvaluationPoint (evaluatedAt);

		return getPartialDerivative
		(
			order,
			evaluatedAt.get ( relativeTo ),
			deltaFor (relativeTo)
		);
	}


	/**
	 * @param order the order of derivative
	 * @param evaluatedAt the point at which to evaluate function
	 * @param delta the delta to use for the variable in play
	 * @return the value of the partial derivative
	 */
	public T getPartialDerivative
		(
			int order,
			T evaluatedAt,
			T delta
		)
	{
		return derivativeAt
		(
			wrapper, order,
			evaluatedAt, delta
		);
	}


	/**
	 * @param f the function wrapper being evaluated
	 * @param order the order of derivative requested
	 * @param x the value of the independent variable in play
	 * @param delta the delta to use for the variable in play
	 * @return the derivative relative to variable specified
	 */
	public T derivativeAt (Function <T> f, int order, T x, T delta)
	{
		switch (order)
		{
			case 1:		return approx.firstOrderDerivative (f, x, delta);
			case 2:		return approx.secondOrderDerivative (f, x, delta);
			default:	throw new RuntimeException ("Order must be 1 or 2");
		}
	}


}


/**
 * a control layer for functions being evaluated
 * @param <T> type of component values on which operations are to be executed
 */
class FunctionWrapper <T> implements Function <T>
{

	FunctionWrapper
	(Subroutine <T> f) { this.target = f; }
	protected Subroutine <T> target;

	/**
	 * @param evaluationPoint the point at which the derivative are computed
	 */
	public void setEvaluationPoint (Point <T> evaluationPoint)
	{ this.evaluationPoint = evaluationPoint; }
	protected Point <T> evaluationPoint;

	/**
	 * @param relativeVariableIndex the index of the variable of current interest
	 */
	public void setRelativeVariableIndex (int relativeVariableIndex)
	{ this.relativeVariableIndex = relativeVariableIndex; }
	protected int relativeVariableIndex;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		Point <T> P = new Point <> (evaluationPoint);
		P.set (relativeVariableIndex, x);
		return target.f (P);
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<T> getSpaceDescription ()
	{ return target.getSpaceDescription (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager <T> getSpaceManager ()
	{ return target.getSpaceDescription (); }

}

