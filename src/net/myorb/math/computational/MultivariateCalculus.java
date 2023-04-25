
package net.myorb.math.computational;

import net.myorb.math.computational.multivariate.*;

import net.myorb.math.expressions.SymbolMap.Operation;
import net.myorb.math.expressions.SymbolMap.SymbolType;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.SymbolMap.MultivariateOperator;
import net.myorb.math.expressions.symbols.AbstractUnaryOperator;

import net.myorb.math.expressions.gui.rendering.MathMarkupNodes;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;

import net.myorb.math.expressions.ValueManager.GenericValue;

/**
 * implementations of algorithms computing Multivariate calculus operations
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class MultivariateCalculus <T> extends VectorOperations <T>
{


	/**
	 * abstract base class for operator implementation objects
	 */
	public static abstract class VectorOperator extends AbstractUnaryOperator
			implements CalculusMarkers.CalculusMetadata
	{
		public VectorOperator
		(
			String name, int precedence,
			CalculusMarkers.CalculusMarkerTypes type
		)
		{ super (name, precedence); this.type = type; }

		/* (non-Javadoc)
		 * @see net.myorb.math.computational.CalculusMarkers.CalculusMetadata#typeOfOperation()
		 */
		public CalculusMarkers.CalculusMarkerTypes typeOfOperation () { return type; }
		public Environment <?> getEnvironment () { return null; }
		protected CalculusMarkers.CalculusMarkerTypes type;
	}


	public static Operation processVectorOperation (VectorOperator VO, Operation function)
	{
		return new MultivariateOperator ()
		{
			OperationContext context = new OperationContext
				(
					function.getName (), new OperationMetadata (VO, function)
				);

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.SymbolMap.ParameterizedFunction#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
			 */
			public String markupForDisplay
			(String operator, String parameters, NodeFormatting using)
			{ return null; }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.SymbolMap.ExecutableUnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
			 */
			public GenericValue execute (GenericValue parameter)
			{
//				System.out.println (parameter);
//				System.out.println ( "DATA " + parameter.getClass ().getCanonicalName () );

				context.dump (parameter);
				context.setEvaluationPoint (parameter);
				return context.getFunction ().execute
				( context.getEvaluationPoint () );
			}

			public String getName () { return context.getFunction ().getName (); }
			public SymbolType getSymbolType () { return SymbolType.PARAMETERIZED; }
			
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.SymbolMap.ParameterizedFunction#getParameterList()
			 */
			public String getParameterList () { return null; }
			public String formatPretty () { return null; }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.SymbolMap.Operation#getPrecedence()
			 */
			public int getPrecedence () { return 99; }	// must be highest possible

		};
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


	public MultivariateCalculus (Environment <T> environment) { super (environment); }


}

