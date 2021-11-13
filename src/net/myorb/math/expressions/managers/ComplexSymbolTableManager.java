
package net.myorb.math.expressions.managers;

import net.myorb.math.expressions.*;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.symbols.*;
import net.myorb.math.complexnumbers.*;

/**
 * symbols specific to complex functionality
 * @author Michael Druckman
 */
public class ComplexSymbolTableManager
	extends SymbolTableManager<ComplexValue<Double>>
{


	@SuppressWarnings("unchecked")
	public ComplexSymbolTableManager (Environment<ComplexValue<Double>> environment)
	{
		super (environment);
		this.valueManager = new ValueManager<ComplexValue<Double>> ();
		this.complexLibrary = new ComplexLibrary<Double> (spaceManager.getComponentManager(), null);
	}
	protected ValueManager<ComplexValue<Double>> valueManager;
	protected ComplexLibrary<Double> complexLibrary;


	/**
	 * construct new complex value object
	 * @param r the real part of the complex value
	 * @param i the imaginary part of the complex value 
	 * @return the new complex object
	 */
	@SuppressWarnings("unchecked")
	public ComplexValue<Double> C (Double r, Double i)
	{
		return new ComplexValue<Double> (r, i, spaceManager.getComponentManager ());
	}


	/**
	 * cis (theta) = cos (theta) + sin (theta) * i
	 * @param theta the angle from the x-axis
	 * @return complex computed value
	 */
	public ComplexValue<Double> cis (ComplexValue<Double> theta)
	{
		Double angle = theta.Re ();
		return complexLibrary.cis (angle);
	}


	/**
	 * implementation of CIS as an operator.
	 *  r @!# theta = r * cis (theta) = r * (cos(theta) + i*sin(theta))
	 * @param r the distance from origin of the polar coordinate set
	 * @param theta the angle from the x-axis
	 * @return complex computed value
	 */
	public ComplexValue<Double> cis (ComplexValue<Double> r, ComplexValue<Double> theta)
	{
		return spaceManager.multiply (r, cis (theta));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolTableManager#importFromSpaceManager(net.myorb.math.expressions.SymbolMap)
	 */
	public void importFromSpaceManager (SymbolMap into)
	{
		super.importFromSpaceManager (into);			// post super-class symbols, may be overridden by post from this class

		into.add								// sqrt(z) = (sqrt((abs(z) + Re(z)) / 2), sqn (Im(z)) * sqrt((abs(z) - Re(z)) / 2))
		(
			new AbstractUnaryOperator (SQRT_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{
					ComplexValue<Double> z = valueManager.toDiscrete (parameter);
					return valueManager.newDiscreteValue (complexLibrary.sqrt (z));
				}
			}, "SQRT function of parameter"
		);

		into.add														// abs(z) = 2\(Re^2 + Im^2)
		(
			new AbstractUnaryOperator (ABSOLUTE_VALUE_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{
					ComplexValue<Double> z = valueManager.toDiscrete (parameter);
					return valueManager.newDiscreteValue (z.abs ());
				}
			}, "Absolute value function of parameter"
		);

		into.add
		(
			new AbstractUnaryOperator (Re_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{
					ComplexValue<Double> z = valueManager.toDiscrete (parameter);
					return valueManager.newDiscreteValue (C (z.Re (), 0.0));
				}
			}, "Real part of complex value"
		);
		into.add
		(
			new AbstractUnaryOperator (Im_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{
					ComplexValue<Double> z = valueManager.toDiscrete (parameter);
					return valueManager.newDiscreteValue (C (z.Im (), 0.0));
				}
			}, "Imaginary part of complex value"
		);

		into.add
		(
			new AbstractUnaryOperator (ARG_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{
					ComplexValue<Double> z = valueManager.toDiscrete (parameter);
					return valueManager.newDiscreteValue (C (complexLibrary.arg (z), 0.0));
				}
			}, "Phase angle of a complex value"
		);
		into.add
		(
			new AbstractBinaryOperator (CMPLX_CIS_OPERATOR, SymbolMap.ADDITION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					return valueManager.newDiscreteValue (cis (valueManager.toDiscrete (left), valueManager.toDiscrete (right)));
				}
			}, "Complex value (left * CIS right) as a binary (polar) operator"
		);
		into.add
		(
			new AbstractBinaryOperator (CMPLX_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					Double leftDiscrete = valueManager.toDiscrete (left).Re (), rightDiscrete = valueManager.toDiscrete (right).Re ();
					return valueManager.newDiscreteValue (C (leftDiscrete, rightDiscrete));
				}
			}, "Complex value (left + i * right) as a binary operator"
		);
		into.add
		(
			new AbstractBinaryOperator (CMPLX_CONJ_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute
				(ValueManager.GenericValue left, ValueManager.GenericValue right)
				{
					Double leftDiscrete = valueManager.toDiscrete (left).Re (), rightDiscrete = valueManager.toDiscrete (right).Re ();
					return valueManager.newDiscreteValue (C (leftDiscrete, -rightDiscrete));
				}
			}, "Complex value (left - i * right) as a binary operator"
		);
		into.add
		(
			new AbstractUnaryOperator (CIS_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{ return valueManager.newDiscreteValue (cis (valueManager.toDiscrete (parameter))); }
			}, "Complex CIS (cos x + i * sin x) function"
		);
		into.add
		(
			new AbstractUnaryOperator (CONJ_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE)
			{
				public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
				{ return valueManager.newDiscreteValue (spaceManager.conjugate (valueManager.toDiscrete (parameter))); }
			}, "Complex conjugate operator"
		);
		into.add
		(
			new AbstractBuiltinVariableLookup (I_SYMBOL)
			{ public ValueManager.GenericValue getValue () { return namedValue (C (0.0, 1.0), "i"); } },
			"Imaginary vector (i = sqrt(-1)) providing vertical axis of complex coordinates"
		);
	}


}

