
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.*;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.symbols.AbstractParameterizedFunction;
import net.myorb.math.expressions.symbols.AbstractBinaryOperator;

/**
 * implementations of algorithms computing polynomial operations
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class PolynomialPrimitives<T> extends VectorPrimitives<T>
{


	/**
	 * type manager used to evaluate computations
	 * @param environment access to the evaluation environment
	 */
	public PolynomialPrimitives (Environment<T> environment)
	{
		super (environment);
		this.poly = abstractions.getPolynomialLibrary ();
	}
	protected BuiltInPolynomialFunctions<T> poly = null;


	/**
	 * implement operator - @#
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getArrayEvalAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return abstractions.arrayFunctionEval (left, right); }
		};
	}


	/**
	 * implement operator - +*^
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getPolyEvalAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return poly.eval (left, right); }
		};
	}


	/**
	 * implement operator - +*^'
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getPolyPrimeAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return poly.evalPrime (left, right, 1); }
		};
	}


	/**
	 * implement operator - +*^''
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getPolyDprimeAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return poly.evalPrime (left, right, 2); }
		};
	}


	/**
	 * implement operator - #*^
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getEulerEvalAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return poly.eulerEval (left, right); }
		};
	}


	/**
	 * implement operator - @*^
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getClenshawEvalAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return poly.clenshawEval (left, right); }
		};
	}


	/**
	 * implement operator - @*^'
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getClenshawPrimeAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return poly.clenshawDerivativeEval (left, right, 1); }
		};
	}


	/**
	 * implement operator - @*^''
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getClenshawDprimeAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return poly.clenshawDerivativeEval (left, right, 2); }
		};
	}


	/**
	 * implement operator - *^#
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getExpEvalAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return poly.expEval (left, right); }
		};
	}


	/**
	 * implement operator - +#*
	 * @param symbol the symbol associated with this object
	 * @param precedence the associated precedence
	 * @return operation implementation object
	 */
	public AbstractBinaryOperator getHarEvalAlgorithm (String symbol, int precedence)
	{
		return new AbstractBinaryOperator (symbol, precedence)
		{
			public ValueManager.GenericValue execute
			(ValueManager.GenericValue left, ValueManager.GenericValue right)
			{ return valueManager.newDiscreteValue (abstractions.evalharmonic (left, right)); }
		};
	}


	/**
	 * implement operator - ARRAYINT
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getArrayIntAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return abstractions.arrayIntegral (parameters); }
		};
	}



	/**
	 * implement operator - ARRAYDER
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getArrayDerAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return abstractions.arrayDerivative (parameters); }
		};
	}


	/**
	 * implement operator - POLYHG
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getPolyHgAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return poly.hypergeometric (parameters); }
		};
	}


	/**
	 * implement operator - POLYINT
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getPolyIntAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return poly.integral (parameters); }
		};
	}


	/**
	 * implement operator - POLYDER
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getPolyDerAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return poly.derivative (parameters); }
		};
	}


	/**
	 * implement operator - CHEBDER
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getChebDerAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return poly.derivativeOfChebyshevT (parameters); }
		};
	}


	/**
	 * implement operator - CHEBPTS
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getChebPointsAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return poly.enumerateChebyshevPoints (parameters); }
		};
	}


	/**
	 * implement operator - CHEBDOM
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getChebDomainAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return poly.enumerateChebyshevDomainPoints (parameters); }
		};
	}


	/**
	 * implement operator - INTERPOLATE
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getInterpolateAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return abstractions.arrayInterpolation (parameters); }
		};
	}


	/**
	 * implement operator - CHEBINTERP
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getChebInterpAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return abstractions.chebyshevInterpolation (parameters); }
		};
	}


	/**
	 * implement operator - CLENQUAD
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getClenQuadAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return poly.clenshawQuadrature (parameters); }
		};
	}


	/**
	 * implement operator - GAUSSQUAD
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getGaussQuadAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return poly.gaussQuadrature (parameters); }
		};
	}


	/**
	 * implement operator - CONV
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getConvAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return poly.conv (parameters); }
		};
	}


	/**
	 * implement operator - DECONV
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getDeconvAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return poly.deconv (parameters); }
		};
	}


	/**
	 * implement operator - ROOTS
	 * @param symbol the symbol associated with this object
	 * @return operation implementation object
	 */
	public AbstractParameterizedFunction getRootsAlgorithm (String symbol)
	{
		return new AbstractParameterizedFunction (symbol)
		{
			public ValueManager.GenericValue execute (ValueManager.GenericValue parameters)
			{ return poly.roots (parameters); }
		};
	}


}

