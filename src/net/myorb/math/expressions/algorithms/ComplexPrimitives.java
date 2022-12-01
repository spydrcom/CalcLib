
package net.myorb.math.expressions.algorithms;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.complexnumbers.ComplexLibrary;
import net.myorb.math.complexnumbers.ColtEVDAccess;
import net.myorb.math.complexnumbers.ComplexAlgorithmAccess;
import net.myorb.math.complexnumbers.ComplexSupportLibrary;

import net.myorb.math.matrices.Matrix;

import net.myorb.math.expressions.symbols.AbstractBinaryOperator;
import net.myorb.math.expressions.symbols.AbstractBuiltinVariableLookup;
import net.myorb.math.expressions.symbols.AbstractUnaryOperator;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.NodeFormatting;
import net.myorb.math.expressions.ValueManager;

/**
 * implementations of algorithms computing complex operations
 * @author Michael Druckman
 */
public class ComplexPrimitives extends AlgorithmCore<ComplexValue<Double>>
{


	/**
	 * type manager used to evaluate computations
	 * @param environment access to the evaluation environment
	 */
	public ComplexPrimitives (Environment<ComplexValue<Double>> environment)
	{
		super (environment);
		this.complexLibrary = ComplexAlgorithmAccess.getComplexLibrary ();
		this.jreLib = ComplexAlgorithmAccess.getComplexSupportLibrary ();
		this.complexLibrary.setMathLib (jreLib);
	}
	protected ComplexLibrary<Double> complexLibrary;
	protected ComplexSupportLibrary<Double> jreLib;



	/**
	 * construct new complex value object
	 * @param r the real part of the complex value
	 * @param i the imaginary part of the complex value 
	 * @return the new complex object
	 */
	public ComplexValue<Double> C (Double r, Double i)
	{
		return new ComplexValue<Double> (r, i, ComplexAlgorithmAccess.getComponentSpaceManager ());
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


	/*
	 * symbol definition for "i"
	 */


	/**
	 * identify value - i
	 * @param symbol the symbol associated with this object
	 * @return variable object referencing value
	 */
	public AbstractBuiltinVariableLookup getIValue (String symbol)
	{
		return new AbstractBuiltinVariableLookup (symbol)
		{
			public ValueManager.GenericValue getValue ()
			{ return namedValue (C (0d, 1d), "i"); }
		};
	}


	/*
	 * algorithms for complex unary/binary functions (Re, Im, ...)
	 */


	/**
	 * implement operator - EVD
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getEVDAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				Matrix <ComplexValue<Double>> inOut = valueManager.toMatrix (parameter);
				Matrix <ComplexValue<Double>> result = ColtEVDAccess.getEigenVals (inOut);
				return valueManager.newMatrix (result);
			}
		};
	}


	/**
	 * implement operator - Sqrt
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getSqrtAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (complexLibrary.sqrt (z));
			}
		};
	}


	/**
	 * implement operator - modSq
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getModSquaredAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (C (z.modSquared (), 0d));
			}
			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{
				return using.formatSuperScript
					(
						using.formatOperatorReference ("|") + operand + using.formatOperatorReference ("|"),
						using.formatNumericReference ("2")
					);
			}
		};
	}


	/**
	 * implement operator - abs
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getAbsAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (z.abs ());
			}
			public String markupForDisplay (String operator, String operand, NodeFormatting using)
			{
				return using.formatOperatorReference ("|") + operand + using.formatOperatorReference ("|");
			}
		};
	}


	/**
	 * implement operator - Re
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getReAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (C (z.Re (), 0.0));
			}

//			public String markupForDisplay (String operator, String operand, NodeFormatting using)
//			{
//				return using.formatUnaryPrefixOperation ("\u00DE", operand);
//			}
		};
	}


	/**
	 * implement operator - Im
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getImAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (C (z.Im (), 0.0));
			}

//			public String markupForDisplay (String operator, String operand, NodeFormatting using)
//			{
//				return using.formatUnaryPrefixOperation ("\u00EE", operand);
//			}
		};
	}


	/**
	 * implement operator - ARG
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getArgAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (C (complexLibrary.arg (z), 0.0));
			}
		};
	}


	/**
	 * implement operator - sin
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getSinAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (complexLibrary.sin (z));
			}
		};
	}


	/**
	 * implement operator - cos
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getCosAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (complexLibrary.cos (z));
			}
		};
	}


	/**
	 * implement operator - asin
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getAsinAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (complexLibrary.asin (z));
			}
		};
	}


	/**
	 * implement operator - acos
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getAcosAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (complexLibrary.acos (z));
			}
		};
	}


	/**
	 * implement operator - sinh
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getSinhAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (complexLibrary.sinh (z));
			}
		};
	}


	/**
	 * implement operator - cosh
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getCoshAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (complexLibrary.cosh (z));
			}
		};
	}


	/**
	 * implement operator - arsinh
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getArsinhAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (complexLibrary.arsinh (z));
			}
		};
	}


	/**
	 * implement operator - arcosh
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getArcoshAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (complexLibrary.arcosh (z));
			}
		};
	}


	/**
	 * implement operator - exp
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getExpAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (complexLibrary.exp (z));
			}
		};
	}


	/**
	 * implement operator - exp (native calculations)
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getNativeExpAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (complexLibrary.nativeExp (z));
			}
		};
	}


	/**
	 * implement operator - GAMMA
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getGammaAlgorithm (String symbol, int precedence)
	{
		complexLibrary.initializeGamma ();
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{
				ComplexValue<Double> z = valueManager.toDiscrete (parameter);
				return valueManager.newDiscreteValue (complexLibrary.gamma (z));
			}
		};
	}


	/**
	 * implement operator - CONJ
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getConjAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{ return valueManager.newDiscreteValue (spaceManager.conjugate (valueManager.toDiscrete (parameter))); }
		};
	}


	/**
	 * implement operator - cos x + i*sin x
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractUnaryOperator getCisAlgorithm (String symbol, int precedence)
	{
		return new AbstractUnaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameter)
			{ return valueManager.newDiscreteValue (cis (valueManager.toDiscrete (parameter))); }
		};
	}


	/**
	 * implement operator - left + i * right
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getCmplxAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				Double leftDiscrete = valueManager.toDiscrete (left).Re (), rightDiscrete = valueManager.toDiscrete (right).Re ();
				return valueManager.newDiscreteValue (C (leftDiscrete, rightDiscrete));
			}
		};
	}


	/**
	 * implement operator - left - i * right
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getCmplxConjAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				Double leftDiscrete = valueManager.toDiscrete (left).Re (), rightDiscrete = valueManager.toDiscrete (right).Re ();
				return valueManager.newDiscreteValue (C (leftDiscrete, -rightDiscrete));
			}
		};
	}


	/**
	 * implement operator - left CIS right
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getPolarAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{
				Double leftDiscrete = valueManager.toDiscrete (left).Re (), rightDiscrete = valueManager.toDiscrete (right).Re ();
				return valueManager.newDiscreteValue (C (leftDiscrete, rightDiscrete));
			}
		};
	}


}

