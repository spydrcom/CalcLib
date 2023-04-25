
package net.myorb.math.computational;

import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.gui.rendering.MathMarkupNodes;

import net.myorb.math.computational.multivariate.VectorOperations;

import net.myorb.math.expressions.symbols.AbstractUnaryOperator;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.ValueManager;

/**
 * implementations of algorithms computing Multivariate calculus operations
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class MultivariateCalculus <T> extends VectorOperations <T>
{


	public MultivariateCalculus
	(Environment <T> environment)
	{ super (environment); }


	/**
	 * abstract base class for operator implementation objects
	 */
	public static abstract class VectorOperator extends AbstractUnaryOperator
			implements CalculusMarkers.CalculusMetadata
	{
		public <T> VectorOperator
		(
			String name, int precedence,
			CalculusMarkers.CalculusMarkerTypes type,
			Environment <T> environment
		)
		{ super (name, precedence); this.type = type; this.environment = environment; }
		protected CalculusMarkers.CalculusMarkerTypes type;

		/* (non-Javadoc)
		 * @see net.myorb.math.expressions.SymbolMap.ExecutableUnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
		 */
		public ValueManager.GenericValue execute (ValueManager.GenericValue parameter) { return null; }

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.CalculusMarkers.CalculusMetadata#typeOfOperation()
		 */
		public CalculusMarkers.CalculusMarkerTypes typeOfOperation () { return type; }

		@SuppressWarnings("unchecked")
		public <T> Environment <T> getEnvironment ()
		{ return (Environment <T>) environment; }
		protected Environment <?> environment;
	}


	/**
	 * format MML for vector operation
	 * @param operator the operator symbol
	 * @param operand the text of the operand
	 * @param using mark-up formatting object for display
	 * @return text of the formatted operation
	 */
	public String markupForDisplay
		(String operator, String operand, NodeFormatting using)
	{
		// NABLA indicates a vector operator
		// - the following operator is DOT or CROSS where appropriate
		// - the following operator is NULL string to indicate GRAD operator
		String op = using.formatOperatorReference (NABLA + operator);
		return op  + MathMarkupNodes.space ("2") + operand;
	}
	static final String NABLA = "\u2207";


}

