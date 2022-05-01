
package net.myorb.math.expressions;

import net.myorb.math.computational.CalculusMarkers;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.computational.Combinatorics;
import net.myorb.math.computational.CalculusMarkers.CalculusMarkerTypes;
import net.myorb.math.expressions.symbols.*;
import net.myorb.math.*;

import java.util.ArrayList;
import java.util.List;

/**
 * provide methods for importing interface symbols into maps
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class SymbolTableManager<T> extends DimensionedValueProcessing<T> implements SymbolTableManagerI<T>
{


	public static final Double e = 2.7182818284590452353602874713527;


	/**
	 * construct object based on type manager.
	 *  also included are function that can be derived from type management primitives
	 * @param environment access to the evaluation environment
	 */
	public SymbolTableManager (Environment<T> environment)
	{
		super (environment);
	}


	/**
	 * use power library to
	 *  compute x^(1/y) = exp (ln (x) / y)
	 * @param x the base of the requested computation
	 * @param y the exponent (inverted) of the requested computation
	 * @param powerLibrary the library to be used
	 * @return x^(1/y) (the y root)
	 */
	public T computeXrootY (T x, T y, final PowerLibrary<T> powerLibrary)
	{
		return computeXtoY (x, spaceManager.invert (y), powerLibrary);
	}


	/**
	 * use power library to
	 *  compute x^y = exp (y * ln (x))
	 * @param x the base of the requested computation
	 * @param y the exponent of the requested computation
	 * @param powerLibrary the library to be used
	 * @return x^y
	 */
	public T computeXtoY (T x, T y, final PowerLibrary<T> powerLibrary)
	{
		return powerLibrary.exp ( spaceManager.multiply ( y, powerLibrary.ln (x) ) );
	}


	/**
	 * compute left^right
	 *  appropriately relative to data type present
	 * @param left the base of the exponentiation operator
	 * @param right the exponent of the exponentiation operator
	 * @param powerLibrary the library to be used
	 * @return left^right
	 */
	public ValueManager.GenericValue power
	(ValueManager.GenericValue left, ValueManager.GenericValue right, PowerLibrary<T> powerLibrary)
	{
		T base = valueManager.toDiscrete (left), exponent, result;
		Integer iExponent = spaceManager.convertToInteger (exponent = valueManager.toDiscrete (right));
		if (iExponent != null) result = powerLibrary.pow (base, iExponent);
		else result = computeXtoY (base, exponent, powerLibrary);
		return valueManager.newDiscreteValue (result);
	}


	/**
	 * compute left\right
	 *  appropriately relative to data type present
	 * @param left the base of the exponentiation operator
	 * @param right the (inverted) exponent of the exponentiation operator
	 * @param powerLibrary the library to be used
	 * @return left\right
	 */
	public ValueManager.GenericValue root
	(ValueManager.GenericValue left, ValueManager.GenericValue right, PowerLibrary<T> powerLibrary)
	{
		T leftDiscrete, rightDiscrete = valueManager.toDiscrete (right), result;
		Integer intRoot = spaceManager.convertToInteger (leftDiscrete = valueManager.toDiscrete (left));
		if (intRoot == null || intRoot < 0) result = computeXrootY (leftDiscrete, rightDiscrete, powerLibrary);
		else if (spaceManager.isNegative (rightDiscrete)) result = computeXrootY (rightDiscrete, leftDiscrete, powerLibrary);
		else result = new PowerPrimitives<T> (spaceManager).nThRoot (rightDiscrete, intRoot);
		return valueManager.newDiscreteValue (result);
	}


	/**
	 * compute left * SQRT(right)
	 *  appropriately relative to data type present
	 * @param left the left side multiplier value operand
	 * @param right the right side operand subject to SQRT
	 * @param powerLibrary the library to be used
	 * @return left\right
	 */
	public ValueManager.GenericValue radical
	(ValueManager.GenericValue left, ValueManager.GenericValue right, PowerLibrary<T> powerLibrary)
	{
		T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
		return valueManager.newDiscreteValue (spaceManager.multiply (leftDiscrete, powerLibrary.sqrt (rightDiscrete)));
	}


	/**
	 * root of sum of squares
	 * @param values the array of values to sum
	 * @param powerLibrary the library to be used for SQRT
	 * @return the computed result
	 */
	public ValueManager.GenericValue hypot (ValueManager.GenericValue values, PowerLibrary<T> powerLibrary)
	{
		T sum = spaceManager.getZero ();
		List<T> array = valueManager.toArray (values);
		for (T v : array) sum = spaceManager.add (sum, spaceManager.multiply (v, v));
		return valueManager.newDiscreteValue (powerLibrary.sqrt (sum));
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
		wrapper.setMetadata (meta); // add metadata holding delta
		return wrapper;
	}
	@SuppressWarnings("unchecked")
	public ValueManager.GenericValue approximateDerivative (ValueManager.GenericValue data, int order)
	{
		DerivativeMetadataStorage<T> dms; ValueManager.Metadata m;
		
		if ((m = data.getMetadata ()) == null)
		{
			data.setMetadata (dms = new DerivativeMetadataStorage<T> ());		// add descriptor for non-approximation
		}
		else if (m instanceof DerivativeMetadataStorage)					// must find a metadata block if one is present
		{
			dms = ((DerivativeMetadataStorage<T>)m);
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
		{ throw new RuntimeException ("Interval parameter must be 2 element array"); }
		data.setMetadata (new CalculusMarkers.IntervalEvaluationMarker ());
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
		{ throw new RuntimeException ("Interval parameter must be 3 element array"); }
		data.setMetadata (new CalculusMarkers.TanhSinhEvaluationMarker ());
		return data;
	}


	/**
	 * add metadata marker for Tanh-Sinh integral approximation request
	 * @param data the generic value on the stack that will be passed to interval evaluation
	 * @return the data with the new metadata
	 */
	public ValueManager.GenericValue useClenshawCurtisApproximation (ValueManager.GenericValue data)
	{
		if (data == null) throw new RuntimeException ("Empty array must be referenced as parameter");
		data.setMetadata (new CalculusMarkers.ClenshawCurtisEvaluationMarker ());
		return data;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.BuiltInArrayFunctions#importFromSpaceManager(net.myorb.math.expressions.SymbolMap)
	 */
	public void importFromSpaceManager (SymbolMap into)
	{
		into.add
		(
			new AbstractBinaryOperator (ADDITION_OPERATOR, SymbolMap.ADDITION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					if (dimensionedParameterPresent (left, right)) return dimensionedAdd (left, right);
					T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
					return valueManager.newDiscreteValue (spaceManager.add (leftDiscrete, rightDiscrete));
				}
			}, "Arithmetic addition operator"
		);
		into.add
		(
			new AbstractBinaryOperator (SUBTRACTION_OPERATOR, SymbolMap.ADDITION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					if (dimensionedParameterPresent (left, right)) return dimensionedSubtract (left, right);
					T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
					return valueManager.newDiscreteValue (spaceManager.add (leftDiscrete, spaceManager.negate (rightDiscrete)));
				}
			}, "Arithmetic subtraction operator"
		);
		into.add
		(
			new AbstractBinaryOperator (MULTIPLICATION_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					if (dimensionedParameterPresent (left, right)) return dimensionedMultiply (left, right);
					T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
					return valueManager.newDiscreteValue (spaceManager.multiply (leftDiscrete, rightDiscrete));
				}
			}, "Arithmetic multiplication operator"
		);
		into.add
		(
			new AbstractBinaryOperator (DIVISION_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
					return valueManager.newDiscreteValue (spaceManager.multiply (leftDiscrete, spaceManager.invert (rightDiscrete)));
				}
			}, "Arithmetic division operator (displayed as a over b)"
		);
		into.add
		(
			new AbstractBinaryOperator (FRACTION_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
					return valueManager.newDiscreteValue (spaceManager.multiply (leftDiscrete, spaceManager.invert (rightDiscrete)));
				}
			}, "Arithmetic division operator (displayed as a / b)"
		);
		into.add
		(
			new AbstractBinaryOperator (PLUS_OR_MINUS_OPERATOR, SymbolMap.ADDITION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
					T minus = spaceManager.add (leftDiscrete, spaceManager.negate (rightDiscrete)),
							plus = spaceManager.add (leftDiscrete, rightDiscrete);
					ValueManager.RawValueList<T> values = new ValueManager.RawValueList<T> (); values.add (plus); values.add (minus);
					return valueManager.newDimensionedValue (values);
				}
			}, "Plus or Minus operator"
		);
		into.add
		(
			new AbstractBinaryOperator (MINUS_OR_PLUS_OPERATOR, SymbolMap.ADDITION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
					T minus = spaceManager.add (leftDiscrete, spaceManager.negate (rightDiscrete)),
							plus = spaceManager.add (leftDiscrete, rightDiscrete);
					ValueManager.RawValueList<T> values = new ValueManager.RawValueList<T> (); values.add (minus); values.add (plus);
					return valueManager.newDimensionedValue (values);
				}
			}, "Minus or Plus operator"
		);
		into.add
		(
			new AbstractBinaryOperator (DELTA_INCREMENT_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					T evaluationPoint = valueManager.toDiscrete (left), delta = valueManager.toDiscrete (right);
					return approximateDerivative (evaluationPoint, delta);
				}
			}, "Derivative approximation evaluated at left parameter using delta value in right parameter"
		);
		into.add
		(
			new AbstractUnaryOperator (PRIME_OPERATOR, SymbolMap.CALCULUS_PRECEDENCE)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameter) { return approximateDerivative (parameter, 1); }
			}, "Mark function call for first derivative approximation"
		);
		into.add
		(
			new AbstractUnaryOperator (DPRIME_OPERATOR, SymbolMap.CALCULUS_PRECEDENCE)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameter) { return approximateDerivative (parameter, 2); }
			}, "Mark function call for second derivative approximation"
		);
		into.add
		(
			new AbstractUnaryOperator (INTERVAL_EVAL_OPERATOR, SymbolMap.CALCULUS_PRECEDENCE)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameter) { return intervalEvaluation (parameter); }
			}, "Mark function call for interval evaluation"
		);
		into.add
		(
			new AbstractUnaryOperator (TSQUAD_OPERATOR, SymbolMap.CALCULUS_PRECEDENCE)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameter) { return tanhSinhApproximation (parameter); }
			}, "Mark function call for Tanh-Sinh integral approximation"
		);
		into.add
		(
			new AbstractUnaryOperator (DCTQUAD_OPERATOR, SymbolMap.CALCULUS_PRECEDENCE)
			{
				public ValueManager.GenericValue
				execute (ValueManager.GenericValue parameter) { return useClenshawCurtisApproximation (parameter); }
			}, "Mark function call for Clenshaw-Curtis integral approximation"
		);
		into.add
		(
			new AbstractBuiltinVariableLookup (PI_SYMBOL)
			{ public ValueManager.GenericValue getValue ()
			{ return namedValue (spaceManager.getPi (), "pi"); } },
			"Symbol for the irrational value of pi"
		);
		into.add
		(
			new AbstractBuiltinVariableLookup (E_SYMBOL)
			{ public ValueManager.GenericValue getValue ()
			{ return namedValue (spaceManager.convertFromDouble (e), "e"); } },
			"Symbol for the irrational value of e"
		);
		into.add
		(
			new AbstractBuiltinVariableLookup (TRUE_SYMBOL)
			{ public ValueManager.GenericValue getValue () { return logicalResult (true); } },
			"Symbol for logical TRUE, translated to 1"
		);
		into.add
		(
			new AbstractBuiltinVariableLookup (FALSE_SYMBOL)
			{ public ValueManager.GenericValue getValue () { return logicalResult (false); } },
			"Symbol for logical FALSE, translated to 0"
		);
		super.importFromSpaceManager (into);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.BuiltInArrayFunctions#importFromPowerLibrary(net.myorb.math.PowerLibrary, net.myorb.math.expressions.SymbolMap)
	 */
	public void importFromPowerLibrary (final ExtendedPowerLibrary<T> powerLibrary, SymbolMap into)
	{
		super.importFromPowerLibrary (powerLibrary, into);

		into.add
		(
			new AbstractParameterizedFunction (DECIMAL_SHIFT_OPERATOR)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
				{
					ValueManager.ValueList parameterList = (ValueManager.ValueList)parameters;
					List<ValueManager.GenericValue> parameterListValues = parameterList.getValues();
					int exponent = valueManager.toInt (parameterListValues.get (0), spaceManager);
					ValueManager.GenericValue pl1 = parameterListValues.get (1), pl2 = parameterListValues.get (2);
					T radix = valueManager.toDiscrete (pl1), value = valueManager.toDiscrete (pl2);
					T result = spaceManager.multiply (value, powerLibrary.pow (radix, exponent));
					return valueManager.newDiscreteValue (result);
				}
			}, "Scientific notation"
		);
		into.add
		(
			new AbstractUnaryPostfixOperator (FACTORIAL_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{ return valueManager.newDiscreteValue (powerLibrary.factorial (valueManager.toDiscrete (parameter))); }
			}, "Unary conventional factorial operator"
		);
		into.add
		(
			new AbstractUnaryOperator (SQRT_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{ return valueManager.newDiscreteValue (powerLibrary.sqrt (valueManager.toDiscrete (parameter))); }
			}, "Unary conventional SQRT function"
		);
		into.add
		(
			new AbstractUnaryOperator (EXP_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{ return valueManager.newDiscreteValue (powerLibrary.exp (valueManager.toDiscrete (parameter))); }
			}, "Unary conventional EXP function e^x"
		);
		into.add
		(
			new AbstractUnaryOperator (LOG_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{ return valueManager.newDiscreteValue (powerLibrary.ln (valueManager.toDiscrete (parameter))); }
			}, "Unary conventional natural logarithm function"
		);
		into.add
		(
			new AbstractBinaryOperator (EXPONENTIATION_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right)
				{ return valueManager.newDiscreteValue (computeXtoY (valueManager.toDiscrete (left), valueManager.toDiscrete (right), powerLibrary)); }
			}, "Binary conventional exponentiation operator x^y"
		);
		into.add
		(
			new AbstractBinaryOperator (POW_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					if (dimensionedParameterPresent (left, right))
						return dimensionedPow (left, right);
					else return power (left, right, powerLibrary);
				}
			}, "Binary conventional exponentiation operator x^n, intended for small integer exponents"
		);
		into.add
		(
			new AbstractParameterizedFunction (HYPOT_FUNCTION)
			{ public ValueManager.GenericValue execute (ValueManager.GenericValue parameters) { return hypot (parameters, powerLibrary); } },
			"SQRT of sum of squares of array elements"
		);
		into.add
		(
			new AbstractBinaryOperator (LEFT_SHIFT_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					T leftDiscrete = valueManager.toDiscrete (left),
						rightDiscrete = valueManager.toDiscrete (right);
					Integer rightInteger = spaceManager.convertToInteger (rightDiscrete);
					T factor = powerLibrary.pow (spaceManager.newScalar (2), rightInteger);
					return valueManager.newDiscreteValue (spaceManager.multiply (leftDiscrete, factor));
				}
			}, "Left shift operator"
		);
		into.add
		(
			new AbstractBinaryOperator (RIGHT_SHIFT_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					T leftDiscrete = valueManager.toDiscrete (left),
						rightDiscrete = valueManager.toDiscrete (right);
					Integer rightInteger = spaceManager.convertToInteger (rightDiscrete);
					T factor = powerLibrary.pow (spaceManager.newScalar (2), -rightInteger);
					return valueManager.newDiscreteValue (spaceManager.multiply (leftDiscrete, factor));
				}
			}, "Right shift operator"
		);
		into.add
		(
			new AbstractBinaryOperator (ROOT_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right) { return root (left, right, powerLibrary); }
			}, "Binary conventional root operator n\\x, intended for small integer roots"
		);
		into.add
		(
			new AbstractBinaryOperator (RADICAL_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right) { return radical (left, right, powerLibrary); }
			}, "Conventional root operator a *\\ b, a * SQRT(b), read as a RADICAL b"
		);
		into.add
		(
			new AbstractBinaryOperator (REMAINDER_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					Integer leftDiscrete = spaceManager.convertToInteger (valueManager.toDiscrete (left)),
								rightDiscrete = spaceManager.convertToInteger (valueManager.toDiscrete (right));
					if (leftDiscrete == null || rightDiscrete == null) throw new RuntimeException ("REM requested for non-integer data");
					return valueManager.newDiscreteValue (spaceManager.newScalar (leftDiscrete % rightDiscrete));
				}
			}, "Binary conventional remainder operator n%m, integer only"
		);
		importFromCombinatorics (powerLibrary, into);
	}
	public void importFromPowerLibrary (SymbolMap into) {} // replaces deprecated method in refactor


	public void importFromCombinatorics (final PowerLibrary<T> powerLibrary, SymbolMap into)
	{
		final Combinatorics<T> combinatorics = new Combinatorics<T>(spaceManager, powerLibrary);

		into.add
		(
			new AbstractBinaryOperator (FACTORIAL_RISING_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
					return valueManager.newDiscreteValue (combinatorics.raisingFactorial (leftDiscrete, rightDiscrete));
				}
			}, "Factorial rising operator"
		);
		into.add
		(
			new AbstractBinaryOperator (FACTORIAL_FALLING_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
					return valueManager.newDiscreteValue (combinatorics.fallingFactorial (leftDiscrete, rightDiscrete));
				}
			}, "Factorial falling operator"
		);
		into.add
		(
			new AbstractUnaryOperator (GAMMA_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{
					T functionParameter = valueManager.toDiscrete (parameter), result;
					if (!valueManager.isInt (parameter, spaceManager)) result = combinatorics.gamma (functionParameter);
					else { result = combinatorics.factorial (spaceManager.add (functionParameter, spaceManager.newScalar (-1))); }
					return valueManager.newDiscreteValue (result);
				}
			}, "Gamma function"
		);
		into.add
		(
			new AbstractUnaryOperator (LOGGAMMA_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{ return valueManager.newDiscreteValue (combinatorics.logGamma (valueManager.toDiscrete (parameter))); }
			}, "LogGamma function"
		);
		into.add
		(
			new AbstractUnaryOperator (ZETA_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{ return valueManager.newDiscreteValue (combinatorics.zeta (valueManager.toDiscrete (parameter))); }
			}, "Zeta function"
		);
		into.add
		(
			new AbstractUnaryOperator (HARMONIC_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{
					T functionParameter = valueManager.toDiscrete (parameter), result;
					if (!valueManager.isInt (parameter, spaceManager)) result = combinatorics.H (functionParameter);
					else result = combinatorics.H (valueManager.toInt (parameter, spaceManager));
					return valueManager.newDiscreteValue (result);
				}
			}, "Harmonic function H(x)"
		);
		into.add
		(
			new AbstractUnaryOperator (BERNOULLI_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{
					Integer n = 1, m = 0; T mt = null;

					if (!valueManager.isArray (parameter))
						mt = valueManager.toDiscrete (parameter);
					else
					{
						List<T> values = valueManager.toDiscreteValues (parameter);
						if (values.size () == 1)
							mt = values.get (0);
						else
						{
							n = spaceManager.convertToInteger (values.get (0));
							mt = values.get (1);
						}
					}

					m = spaceManager.convertToInteger (mt);
					T b = combinatorics.optimizedBernoulli (m);
					if (n == 0 && m == 1) b = spaceManager.negate (b);
					return valueManager.newDiscreteValue (b);
				}
			}, "Bernoulli function B(m) for second (n=1) Bernoulli numbers"
		);
		into.add
		(
			new AbstractBinaryOperator (BINOMIAL_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					T leftDiscrete = valueManager.toDiscrete (left), rightDiscrete = valueManager.toDiscrete (right);
//					T leftDiscrete = spaceManager.newScalar (spaceManager.toNumber (valueManager.toDiscrete (left)).intValue ()),
//						rightDiscrete = spaceManager.newScalar (spaceManager.toNumber (valueManager.toDiscrete (right)).intValue ());
					return valueManager.newDiscreteValue (combinatorics.binomialCoefficient (leftDiscrete, rightDiscrete));
				}
			}, "Binomial coefficient operator (n ## k)"
		);
	}


	/**
	 * use optimized library when type is Double float
	 * @param speedLibrary a library object optimized for speed and precision
	 * @param into the symbol map object collecting imported symbols
	 */
	public void importFromSpeedLibrary (final HighSpeedMathLibrary speedLibrary, SymbolMap into)
	{
		into.add
		(
			new AbstractUnaryOperator (ASIN_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{
					Double p = spaceManager.toNumber (valueManager.toDiscrete (parameter)).doubleValue ();
					return valueManager.newDiscreteValue (spaceManager.convertFromDouble (speedLibrary.asin (p))); }
				}, "Trigonometric ARC SIN function"
		);
		into.add
		(
			new AbstractParameterizedFunction (ATAN_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{
					List<Double> doubleArray = new ArrayList<Double>();
					List<T> parameterArray = valueManager.toArray (parameter);
					for (T item : parameterArray) { doubleArray.add (spaceManager.convertToDouble (item)); }
					return valueManager.newDiscreteValue (spaceManager.convertFromDouble (speedLibrary.atan (doubleArray)));
				}
			}, "Trigonometric ARC TAN function"
		);
	}


	/**
	 * provide access to trig function using identity equations
	 * @param trigLibrary the implementer of TrigIdentities provides plethora of functions
	 * @param into the symbol map object collecting imported symbols
	 */
	public void importFromTrigLibrary (final TrigIdentities<T> trigLibrary, SymbolMap into)
	{
		into.add
		(
			new AbstractUnaryOperator (SIN_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{ return valueManager.newDiscreteValue (trigLibrary.sin (valueManager.toDiscrete (parameter))); }
			}, "Trigonometric SIN function"
		);
		into.add
		(
			new AbstractUnaryOperator (COS_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{ return valueManager.newDiscreteValue (trigLibrary.cos (valueManager.toDiscrete (parameter))); }
			}, "Trigonometric COS function"
		);
		into.add
		(
			new AbstractUnaryOperator (TAN_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{ return valueManager.newDiscreteValue (trigLibrary.tan (valueManager.toDiscrete (parameter))); }
			}, "Trigonometric TAN function"
		);
		into.add
		(
			new AbstractUnaryOperator (ASIN_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{ return valueManager.newDiscreteValue (trigLibrary.asin (valueManager.toDiscrete (parameter))); }
			}, "Trigonometric ARC SIN function"
		);
		into.add
		(
			new AbstractParameterizedFunction (ATAN_FUNCTION)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{ return valueManager.newDiscreteValue (trigLibrary.atan (valueManager.toArray  (parameter))); }
			}, "Trigonometric ARC TAN function"
		);
	}


}


/**
 * annotate parameter to derivative
 *  with count of derivatives to be applied
 *  with delta to use (if approximation used)
 * @param <T> type on which operations are to be executed
 */
class DerivativeMetadataStorage<T>
	implements CalculusMarkers.DerivativeMetadata<T>
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
	 * @see net.myorb.math.expressions.SymbolTableManager.DerivativeMetadata#usesApproximation()
	 */
	public boolean usesApproximation () { return useApproximation; }
	boolean useApproximation = true;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolTableManager.DerivativeMetadata#getDelta()
	 */
	public T getDelta () { return delta; }
	T delta;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolTableManager.DerivativeMetadata#setCount(int)
	 */
	public void setCount (int order) { count = order; }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolTableManager.DerivativeMetadata#getCount()
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

}


