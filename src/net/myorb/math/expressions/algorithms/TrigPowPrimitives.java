
package net.myorb.math.expressions.algorithms;

import net.myorb.math.TrigPowImplementation;

import net.myorb.math.expressions.symbols.AbstractUnaryOperator;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager;

import net.myorb.data.abstractions.NameValueHash;

import java.util.Map;

/**
 * implementations of algorithms in the TrigPow library
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class TrigPowPrimitives<T> extends AlgorithmCore<T>
{


	public TrigPowPrimitives
	(Environment<T> environment, TrigPowImplementation<T> impl)
	{ super (environment); this.impl = impl; }

	public TrigPowPrimitives
		(
			ExpressionSpaceManager<T> spaceManager, ValueManager<T> valueManager,
			TrigPowImplementation<T> impl
		)
	{
		super (spaceManager, valueManager); this.impl = impl;
	}
	protected TrigPowImplementation<T> impl;


	/**
	 * @param symbol the name of the operator
	 * @param precedence the precedence to be applied
	 * @return the operator object
	 */
	public AbstractUnaryOperator getTrigPowAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.SymbolMap.ExecutableUnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
			 */
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				T result = impl.trigPow
					(op, valueManager.toDiscrete (parameter), n);
				return valueManager.newDiscreteValue (result);
			}

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.symbols.AbstractUnaryOperator#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
			 */
			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{
				return formatIdRef (using) + using.formatParenthetical (operand);
			}

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.symbols.OperationObject#addParameterization(java.util.Map)
			 */
			public void addParameterization (Map<String,Object> options)
			{
				Object opName = options.get ("op"), pow = options.get ("pow");
				if (opName == null) throw new RuntimeException ("No operation identified from TrigPow implementation");
				if (pow != null) n = ((Number) pow).intValue ();
				setParameters (opName.toString (), n);
			}

			/**
			 * construct MML for ID reference
			 * @param using the node formatter
			 * @return the formatted text
			 */
			String formatIdRef (NodeFormatting using)
			{
				String id = using.formatIdentifierReference (opText);
				if (n != 1)
				{
					return using.formatSuperScript
					(
						id, using.formatNumericReference (powText)
					);
				}
				return id;
			}

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.symbols.OperationObject#addParameterization(java.lang.String)
			 */
			public void addParameterization (String options)
			{
				NameValueHash nv = new NameValueHash (options);
				setParameters (nv.get ("op"), nv.get ("pow"));
			}

			/**
			 * @param opName the name of the operator represented
			 * @param pow the exponent the computed value should be raised to
			 */
			void setParameters (String opName, String pow)
			{
				if (pow != null)
				{ setParameters (opName, Integer.parseInt (pow)); }
				else setParameters (opName, 1);
			}
			void setParameters (String opName, int pow)
			{
				powText = Integer.toString (n = pow);
				op = impl.getOperation (opName);
				opText = op.toString ();
			}
			TrigPowImplementation.Operations op;
			String opText, powText;
			int n = 1;

		};
	}


}

