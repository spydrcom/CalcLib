
package net.myorb.math.expressions.algorithms;

import net.myorb.math.computational.CalculusMarkers;
import net.myorb.math.computational.MultivariateCalculus;
import net.myorb.math.computational.CalculusMarkers.CalculusMarkerTypes;

import net.myorb.math.expressions.symbols.AbstractUnaryOperator;
import net.myorb.math.expressions.symbols.AbstractBinaryOperator;
import net.myorb.math.expressions.symbols.AbstractCalculusOperator;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.OperatorNomenclature;

import net.myorb.math.expressions.ConventionalNotations;
import net.myorb.math.expressions.ValueManager;

/**
 * implementations of algorithms computing calculus operations
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class CalculusPrimitives<T> extends CalculusMarkers
{


	/**
	 * object depends on data structures of the environment
	 * @param environment the environment object holding value management objects
	 */
	public CalculusPrimitives (Environment<T> environment)
	{ this.valueManager = environment.getValueManager (); this.environment = environment; }
	protected ValueManager<T> valueManager = null;
	protected Environment<T> environment = null;


	/*
	 * operator and function algorithm implementations
	 */


	/**
	 * implement operator - DELTA (derivative approximation, LIM interval GOESTO 0)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getDeltaAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.SymbolMap.BinaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue, net.myorb.math.expressions.ValueManager.GenericValue)
			 */
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				T evaluationPoint = valueManager.toDiscrete (left), delta = valueManager.toDiscrete (right);
				return approximateDerivative (evaluationPoint, delta);
			}

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.symbols.AbstractBinaryOperator#markupForDisplay(java.lang.String, java.lang.String, java.lang.String, boolean, boolean, net.myorb.math.expressions.gui.rendering.NodeFormatting)
			 */
			public String markupForDisplay
				(
					String operator,
					String firstOperand, String secondOperand,
					boolean lfence, boolean rfence,
					NodeFormatting using
				)
			{
				return using.formatParenthetical (firstOperand);
			}

		};
	}


	/**
	 * implement operator - IntegrationDelta (integral dx)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getIntegrationDeltaAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.SymbolMap.ExecutableUnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
			 */
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				String symbolDelta = ConventionalNotations.DELTA_JAVA_ESCAPE + parameter.getName ();
				return environment.getSymbolMap ().getValue (symbolDelta);
			}

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.symbols.AbstractUnaryOperator#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
			 */
			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{
				return "<mrow><mi>" + ConventionalNotations.DELTA_XML_ESCAPE + "</mi><mo/>" + operand + "</mrow>";
			}

		};
	}


	/**
	 * implement function - GRAD
	 * @param symbol the name of the operator
	 * @param precedence the precedence to be applied
	 * @return the operator object
	 */
	public AbstractUnaryOperator getGradAlgorithm (String symbol, int precedence)
	{
		return new MultivariateCalculus.VectorOperator (symbol, precedence, GRAD, environment)
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.symbols.AbstractUnaryOperator#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
			 */
			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{ return new MultivariateCalculus <> (environment).markupForDisplay ( "", operand, using ); }
		};
	}
	static final CalculusMarkers.CalculusMarkerTypes GRAD = CalculusMarkers.CalculusMarkerTypes.VECTOR_GRAD;


	/**
	 * implement function - DIV
	 * @param symbol the name of the operator
	 * @param precedence the precedence to be applied
	 * @return the operator object
	 */
	public AbstractUnaryOperator getDivAlgorithm (String symbol, int precedence)
	{
		return new MultivariateCalculus.VectorOperator (symbol, precedence, DIV, environment)
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.symbols.AbstractUnaryOperator#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
			 */
			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{ return new MultivariateCalculus <> (environment).markupForDisplay ( DOT, operand, using ); }
			static final String DOT = OperatorNomenclature.DOT_PRODUCT_RENDER;
		};
	}
	static final CalculusMarkers.CalculusMarkerTypes DIV = CalculusMarkers.CalculusMarkerTypes.VECTOR_DIV;


	/**
	 * implement function - CURL
	 * @param symbol the name of the operator
	 * @param precedence the precedence to be applied
	 * @return the operator object
	 */
	public AbstractUnaryOperator getCurlAlgorithm (String symbol, int precedence)
	{
		return new MultivariateCalculus.VectorOperator (symbol, precedence, CURL, environment)
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.symbols.AbstractUnaryOperator#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
			 */
			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{ return new MultivariateCalculus <> (environment).markupForDisplay ( CROSS, operand, using ); }
			static final String CROSS = OperatorNomenclature.CROSS_PRODUCT_RENDER;
		};
	}
	static final CalculusMarkers.CalculusMarkerTypes CURL = CalculusMarkers.CalculusMarkerTypes.VECTOR_CURL;



	/**
	 * implement operator -  ' (prime, first derivative)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractCalculusOperator getPrimeAlgorithm (String symbol, int precedence)
	{
		return new AbstractCalculusOperator (symbol, precedence)
		{

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.SymbolMap.ExecutableUnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
			 */
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{ return approximateDerivative (parameter, 1); }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.symbols.AbstractUnaryOperator#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
			 */
			public String markupForDisplay
			(String operator, String operand, NodeFormatting using)
			{ return operand + operator; }

		};
	}


	/**
	 * implement operator - '' (second derivative)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractCalculusOperator getDPrimeAlgorithm (String symbol, int precedence)
	{
		return new AbstractCalculusOperator (symbol, precedence)
		{

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.SymbolMap.ExecutableUnaryOperator#execute(net.myorb.math.expressions.ValueManager.GenericValue)
			 */
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{ return approximateDerivative (parameter, 2); }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.symbols.AbstractUnaryOperator#markupForDisplay(java.lang.String, java.lang.String, net.myorb.math.expressions.gui.rendering.NodeFormatting)
			 */
			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{ return operand + operator; }

		};
	}


	/**
	 * implement operator - | (integration interval)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractCalculusOperator getIntervalAlgorithm (String symbol, int precedence)
	{
		return new AbstractCalculusOperator (symbol, precedence)
		{
			public ValueManager.GenericValue
			execute (ValueManager.GenericValue parameter)
			{ return intervalEvaluation (parameter); }
		};
	}


	/**
	 * implement operator - Tanh Sinh Quadrature
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractCalculusOperator getTSQuadAlgorithm (String symbol, int precedence)
	{
		return new AbstractCalculusOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{ return tanhSinhApproximation (parameter); }
		};
	}


	/**
	 * implement operator - Direct Cosine Transform
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractCalculusOperator getDCTQuadAlgorithm (String symbol, int precedence)
	{
		return new AbstractCalculusOperator (symbol, precedence)
		{
			public ValueManager.GenericValue
			execute (ValueManager.GenericValue parameter)
			{ return useClenshawCurtisApproximation (parameter); }
		};
	}


	/**
	 * implement operator - Trapezoidal Integral Approximation
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractCalculusOperator getTrapQuadAlgorithm (String symbol, int precedence)
	{
		return new AbstractCalculusOperator (symbol, precedence)
		{
			public ValueManager.GenericValue
			execute (ValueManager.GenericValue parameter)
			{ return useTrapezoidalApproximation (parameter); }
		};
	}


	/**
	 * implement operator - Trapezoidal Approximation Adjustment
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractCalculusOperator getTrapAdjustAlgorithm (String symbol, int precedence)
	{
		return new AbstractCalculusOperator (symbol, precedence)
		{
			public ValueManager.GenericValue
			execute (ValueManager.GenericValue parameter)
			{ return useTrapezoidalAdjustment (parameter); }
		};
	}


	/**
	 * compute approximation of function derivative
	 * @param evaluationPoint the point on the x-axis to evaluate the derivative at
	 * @param delta the offset from the evaluation point to use to evaluate the rise
	 * @return the approximate value of the function derivative at specified point
	 */
	public ValueManager.GenericValue approximateDerivative (T evaluationPoint, T delta)
	{
		ValueManager.GenericValue wrapper = valueManager.newDiscreteValue (evaluationPoint);
		DerivativeMetadataStorage<T> meta = new DerivativeMetadataStorage<T> (delta);
		wrapper.setMetadata (meta); // add meta-data holding delta
		return wrapper;
	}
	@SuppressWarnings("unchecked")
	public ValueManager.GenericValue approximateDerivative (ValueManager.GenericValue data, int order)
	{
		DerivativeMetadataStorage<T> dms; ValueManager.Metadata m;
		
		if ((m = data.getMetadata ()) == null)
		{
			data.setMetadata (dms = new DerivativeMetadataStorage<T> ());	// add descriptor for non-approximation
		}
		else if (m instanceof DerivativeMetadataStorage)					// must find a metadata block if one is present
		{
			dms = (DerivativeMetadataStorage<T>) m;
		}
		else
		{
			throw new RuntimeException
			(
				"Metadata Error"
			);
		}

		dms.setCount (order);												// indicate first or second derivative
		return data;
	}


	/**
	 * add metadata marker for interval evaluation request
	 * @param data the generic value on the stack that will be passed to interval evaluation
	 * @return the data with the new metadata
	 */
	public ValueManager.GenericValue intervalEvaluation (ValueManager.GenericValue data)
	{
		if (!valueManager.isArray (data))
		{ throw new RuntimeException ("Interval parameter must be 2 element (lo,hi) array"); }
		data.setMetadata (new IntervalEvaluationMarker ());
		return data;
	}


	/**
	 * add metadata marker for Tanh-Sinh integral approximation request
	 * @param data the generic value on the stack that will be passed to interval evaluation
	 * @return the data with the new metadata
	 */
	public ValueManager.GenericValue tanhSinhApproximation (ValueManager.GenericValue data)
	{
		if (!valueManager.isArray (data))
		{ throw new RuntimeException ("Interval parameter must be 3 element (lo, hi, error limit) array"); }
		data.setMetadata (new TanhSinhEvaluationMarker ());
		return data;
	}


	/**
	 * add metadata marker for Trapezoidal integral approximation request
	 * @param data the generic value on the stack that will be passed to interval evaluation
	 * @return the data with the new metadata
	 */
	public ValueManager.GenericValue useTrapezoidalApproximation (ValueManager.GenericValue data)
	{
		if (!valueManager.isArray (data))
		{ throw new RuntimeException ("Interval parameter must be 3 element (lo,hi,delta) array"); }
		data.setMetadata (new TrapezoidalEvaluationMarker ());
		return data;
	}


	/**
	 * add metadata marker for Trapezoidal integral adjustment request
	 * @param data the generic value on the stack that will be passed to interval evaluation
	 * @return the data with the new metadata
	 */
	public ValueManager.GenericValue useTrapezoidalAdjustment (ValueManager.GenericValue data)
	{
		if (!valueManager.isArray (data))
		{ throw new RuntimeException ("Interval parameter must be 3 element (lo,hi,delta) array"); }
		data.setMetadata (new TrapezoidalAdjustmentMarker ());
		return data;
	}


	/**
	 * add meta-data marker for Clenshaw-Curtis integral approximation request
	 * @param data the generic value on the stack that will be passed to interval evaluation
	 * @return the data with the new metadata
	 */
	public ValueManager.GenericValue useClenshawCurtisApproximation (ValueManager.GenericValue data)
	{
		if (data == null) throw new RuntimeException ("Empty array () must be referenced as parameter");
		data.setMetadata (new ClenshawCurtisEvaluationMarker ());
		return data;
	}


}



/**
 * annotate parameter to derivative
 *  with count of derivatives to be applied
 *  with delta to use (if approximation used)
 * @param <T> type on which operations are to be executed
 */
class DerivativeMetadataStorage<T>
	implements CalculusPrimitives.DerivativeMetadata<T>
{

	/**
	 * non-approximation version
	 */
	public DerivativeMetadataStorage ()
	{ this.useApproximation = false; }

	/**
	 * prepare approximation 
	 * @param delta the limit approaching zero (small but not too small)
	 */
	public DerivativeMetadataStorage (T delta)
	{ this.delta = delta; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.operations.CalculusPrimitives.DerivativeMetadata#usesApproximation()
	 */
	public boolean usesApproximation () { return useApproximation; }
	boolean useApproximation = true;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.operations.CalculusPrimitives.DerivativeMetadata#getDelta()
	 */
	public T getDelta () { return delta; }
	T delta;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.operations.CalculusPrimitives.DerivativeMetadata#setCount(int)
	 */
	public void setCount (int order) { count = order; }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.operations.CalculusPrimitives.DerivativeMetadata#getCount()
	 */
	public int getCount () { return count; }
	int count = 0;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.CalculusMarkers.CalculusMetadata#typeOfOperation()
	 */
	public CalculusMarkerTypes typeOfOperation ()
	{
		return CalculusMarkerTypes.DERIVATIVE;
	}

	public String toString ()
	{
		return "delta: " + delta + " count: " + count;
	}

}


