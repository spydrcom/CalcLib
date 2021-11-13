
package net.myorb.math.expressions.tree;

import net.myorb.math.expressions.ValueManager.GenericValue;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

/**
 * track node calculations
 * @author Michael Druckman
 */
public class CalculationCache
{

	/**
	 * identify cache capabilities
	 * @param <T> data type
	 */
	public interface NodeCache<T>
	{
		void evalSubExpressions ();
		GenericValue evalFullExpression ();
		GenericValue eval (SubExpression<T> expr);
	}

	/**
	 * calculator for expression nodes
	 * @param <T> data type
	 */
	public interface NodeCalculator<T>
	{
		GenericValue evaluate (SubExpression<T> expression);
		void useCache (NodeCache<T> cache);
	}

	/**
	 * node value cache
	 */
	public static class NodeEvaluation
	{
		GenericValue value = null;
		boolean evaluated = false;
	}

	/**
	 * cache manipulation supporting evaluation
	 * @param <T> data type
	 */
	public static class ExpressionEvaluation<T> implements NodeCache<T>
	{
		
		/**
		 * provide access to object that can compute node values
		 * @param nodeCalculator the computation object
		 */
		public void useNodeCalculator
		(NodeCalculator<T> nodeCalculator)
		{ this.nodeCalculator = nodeCalculator; }
		protected NodeCalculator<T> nodeCalculator;

		/**
		 * construct value cache for sub-expressions of tree
		 * @param expression the full expression being evaluated
		 * @return an ExpressionEvaluation object for the calculation
		 * @param <T> data type
		 */
		public static <T> ExpressionEvaluation<T> init (Expression<T> expression)
		{
			ExpressionEvaluation<T> eval = new ExpressionEvaluation<T>();
			eval.add (expression.invocations); eval.add (expression.components);
			return eval;
		}

		/**
		 * construct value cache for sub-expressions
		 * @param subs the sub-expressions to be considered
		 */
		public void add (List<SubExpression<T>> subs)
		{
			for (SubExpression<T> subEx : subs)
			{
				nodeEvaluations.put (subEx, new NodeEvaluation ());
				subExpressions.add (subEx);
			}
		}

		/**
		 * calculate value of sub-expression
		 * @param expr reference to sub-expression to evaluate
		 * @param node the cache for the result of the evaluation
		 * @return the calculated value
		 */
		public GenericValue eval (SubExpression<T> expr, NodeEvaluation node)
		{
			node.value = nodeCalculator.evaluate (expr);
			node.evaluated = true;
			return node.value;
		}

		/**
		 * check for prior evaluation
		 * @param expr sub-expression node to check
		 * @return value of calculated sub-expression
		 */
		public GenericValue eval (SubExpression<T> expr)
		{
			if (expr == null) return null;
			NodeEvaluation eval = nodeEvaluations.get (expr);
			if (eval == null) return nodeCalculator.evaluate (expr);
			if (eval.evaluated) return eval.value;
			else return eval (expr, eval);
		}

		/**
		 * evaluate all sub-expression nodes
		 */
		public void evalSubExpressions ()
		{
			for (SubExpression<T> subEx : subExpressions) eval (subEx);
		}

		/**
		 * @return evaluation of full expression
		 */
		public GenericValue evalFullExpression ()
		{
			return eval (subExpressions.get (subExpressions.size () - 1));
		}

		Map<SubExpression<T>,NodeEvaluation> nodeEvaluations = new HashMap<SubExpression<T>,NodeEvaluation>();
		List<SubExpression<T>> subExpressions = new ArrayList<SubExpression<T>>();

	}

	/**
	 * construct cache for evaluation
	 * @param expression the expression being calculated
	 * @param nodeCalculator the engine for node evaluations
	 * @param <T> data type
	 */
	public static <T> void construct
	(Expression<T> expression, NodeCalculator<T> nodeCalculator)
	{
		ExpressionEvaluation<T> cache;
		nodeCalculator.useCache (cache = ExpressionEvaluation.init (expression));
		cache.useNodeCalculator (nodeCalculator);
		cache.evalSubExpressions ();
	}
	public static <T> void construct
	(SubExpression<T> subExpression, NodeCalculator<T> nodeCalculator)
	{ construct (subExpression.getRoot (), nodeCalculator); }

}

