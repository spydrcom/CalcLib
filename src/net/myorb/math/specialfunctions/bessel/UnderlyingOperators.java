
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionList;
import net.myorb.math.specialfunctions.Library;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;
import net.myorb.math.Function;

/**
 * common math formulas for Bessel functions
 * @author Michael Druckman
 */
public abstract class UnderlyingOperators extends Library
{



	/**
	 * calculation of denominator of polynomial terms
	 * @param <T> data type used
	 */
	public interface Denominator<T>
	{
		/**
		 * @param idx the loop index of the term
		 * @param order the alpha value for the function
		 * @param esm a manager for the number space in use
		 * @return the calculated value
		 */
		T eval (int idx, Number order, ExpressionSpaceManager<T> esm);
	}


	/**
	 * @param a the alpha value for the function
	 * @param modified the alternating sign of the formula is removed
	 * @param termCount the number of term to generate for the polynomial
	 * @param psm a manager for the polynomial space (arithmetic for polynomials)
	 * @param denominator a calculation engine for the term denominator (Gamma product)
	 * @param sm a manager for the number space in use
	 * @return a polynomial power function
	 * @param <T> data type manager
	 */
	public static <T> Polynomial.PowerFunction<T> getPoly
			(
				T a, boolean modified, int termCount, PolynomialSpaceManager<T> psm,
				Denominator<T> denominator, ExpressionSpaceManager<T> sm
			)
	{ return sumOfTerms (0, 0, termCount, sm.toNumber (a), psm, modified, denominator); }


	/**
	 * @return products of GAMMA function that calculate the Bessel series denominator
	 * @param <T> data type being used
	 */
	public static <T> Denominator<T> getBesselDenominator ()
	{
		return new Denominator<T>()
		{
			public T eval (int idx, Number order, ExpressionSpaceManager<T> sm)
			{
				double gammaSum = gamma (idx + order.doubleValue () + 1);
				double product = gammaSum * factorial (idx).doubleValue ();
				return sm.convertFromDouble (product);
			}
		};
	}


	/**
	 * @return products of GAMMA function that calculate the Struve series denominator
	 * @param <T> data type being used
	 */
	public static <T> Denominator<T> getStruveDenominator ()
	{
		return new Denominator<T>()
		{
			double ratio32 = 3.0/2.0;
			public T eval (int idx, Number order, ExpressionSpaceManager<T> sm)
			{
				double gammaIndex = gamma (idx + ratio32);
				double gammaOrder = gamma (idx + order.doubleValue () + ratio32);
				return sm.convertFromDouble (gammaIndex * gammaOrder);
			}
		};
	}


	/**
	 * loop for term construction of polynomial
	 * @param initialPowX the power of X in the first term
	 * @param initialPow2 the power of 2 in the first denominator
	 * @param termCount the number of terms to include in the polynomial
	 * @param polynomialOrder the highest exponent value of the polynomial
	 * @param psm a space manager for polynomial management
	 * @param modified negation term removed
	 * @param denominator Gamma computation
	 * @return the constructed polynomial
	 * @param <T> data type manager
	 */
	public static <T> Polynomial.PowerFunction<T> sumOfTerms
		(
			int initialPowX, int initialPow2,
			int termCount, Number polynomialOrder,
			PolynomialSpaceManager<T> psm, boolean modified,
			Denominator<T> denominator
		)
	{
		ExpressionSpaceManager<T> sm = (ExpressionSpaceManager<T>) psm.getSpaceDescription ();

		Polynomial.PowerFunction<T> x = psm.newVariable ();
		Polynomial.PowerFunction<T> xsq = psm.multiply (x, x);
		Polynomial.PowerFunction<T> polynomial = psm.getZero ();

		T denom, twoSq = sm.newScalar (4),
			pow2 = sm.newScalar (1 << initialPow2);
		Polynomial.PowerFunction<T> xpow = psm.getOne ();
		if (initialPowX != 0) xpow = psm.pow (x, initialPowX);
		Polynomial.PowerFunction<T> term;

		for (int k = 0; k < termCount; k++)
		{
			denom = sm.multiply
				(
					pow2,
					denominator.eval (k, polynomialOrder, sm)
				);
			term = psm.times (sm.invert (denom), xpow);
			if (!modified) term = signT (term, k, psm);

			pow2 = sm.multiply (pow2, twoSq);
			xpow = psm.multiply (xpow, xsq);

			polynomial = psm.add (polynomial, term);
		}

		return polynomial;
	}


/*

		NOTE: series has the form (x/2)^(2*k+alpha) / D(k)

		so we use algebra and identify alpha being constant (allowing integer and real values)

		we can separate (x/2)^(2*k)/D(k) * (x/2)^(alpha), useful since k is integer and alpha can be real
		//TODO: alpha can be complex notably in Struve and perhaps for other Bessel function requirements

		giving the even polynomial P(x) = SIGMA [k in ...] ( x^(2*k) / ( 2^(2*k) * D(k) ) )
		this part of the series can be reduced to simple polynomial with even exponents
		coefficients for the polynomial can be computed 2^(-2*k)/D(k)

		and the factored out exponential x^alpha / 2^alpha

 */


	/**
	 * connect polynomial part with exponential part of function
	 * @param <T> type on which operations are to be executed
	 */
	public static abstract class ExponentialFunction<T>
		implements SpecialFunctionFamilyManager.FunctionDescription<T>
	{

		/**
		 * special entry for Struve, exponent must be incremented from presented alpha
		 * @param a the value of the order called alpha by convention
		 * @param polynomial the polynomial expansion of the series
		 * @param sm the type manager for the data type used
		 */
		public ExponentialFunction
		(double a, Function<T> polynomial, ExpressionSpaceManager<T> sm)
		{ this (a + 1, a, polynomial, sm); }

		/**
		 * @param parameter the value of the order called alpha by convention;
		 *                  this constructor is to be used where exponent and display are same
		 * @param polynomial the polynomial expansion of the series
		 * @param sm the type manager for the data type used
		 */
		public ExponentialFunction
		(Number parameter, Function<T> polynomial, ExpressionSpaceManager<T> sm)
		{ this (parameter, parameter, polynomial, sm); }

		/**
		 * @param exponent the value to use for the exponent in the exponential portion of equation
		 * @param displayParameter the value of the alpha order parameter for displays
		 * @param polynomial the polynomial expansion of the series
		 * @param sm the type manager for the data type used
		 */
		public ExponentialFunction
		(Number exponent, Number displayParameter, Function<T> polynomial, ExpressionSpaceManager<T> sm)
		{ this (sm.convertFromDouble (exponent.doubleValue ()), displayParameter, polynomial, sm); }

		/**
		 * this constructor accepts generic exponent
		 *  but most layers provide for real and integer Number parameters
		 * @param exponent the value to use for the exponent in the exponential portion of equation
		 * @param displayParameter the value of the alpha order parameter for displays
		 * @param polynomial the polynomial expansion of the series
		 * @param sm the type manager for the data type used
		 */
		public ExponentialFunction
			(
				T exponent, Number displayParameter,
				Function<T> polynomial, ExpressionSpaceManager<T> sm
			)
		{
			this.polynomial = polynomial;
			this.HALF = sm.invert (sm.newScalar (2));
			this.displayParameter = displayParameter;
			this.exponent = exponent;
			this.sm = sm;
		}
		protected Number displayParameter;
		protected T exponent;
		protected T HALF;

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public T eval (T x)
		{
			T xOver2 = sm.multiply (x, HALF);
			T exponentiation = TraisedToT (xOver2, exponent);
			return sm.multiply (exponentiation, polynomial.eval (x));
		}
		protected Function<T> polynomial;

		/**
		 * changed from simple real ^ integer.
		 *  can be used to implement all forms where T has support.
		 *  typical library implementations use exp(exponent*ln(base))
		 * @param base value to be raised to the power
		 * @param power exponent of the expression
		 * @return calculated base ^ power
		 */
		public T TraisedToT (T base, T power)
		{
			return realPower (base, power, sm);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
		 */
		public abstract StringBuffer getFunctionDescription ();

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
		 */
		public abstract String getFunctionName ();
		
		/* (non-Javadoc)
		 * @see net.myorb.math.Function#getSpaceManager()
		 */
		public SpaceManager<T> getSpaceDescription () { return sm; }
		public SpaceManager<T> getSpaceManager () { return sm; }
		protected ExpressionSpaceManager<T> sm;

	}


	/**
	 * @param parameterValue the parameter identifying kind of function
	 * @return the value formatted for use as part of function name
	 */
	public static String formatParameterDisplay (Double parameterValue)
	{
		boolean negative;
		if (negative = parameterValue < 0) parameterValue = -parameterValue;
		int ch = parameterValue.intValue (), man = (int)(100 * (parameterValue - ch));
		return (negative ? "N": "") + ch + "_" + man;
	}


	/**
	 * @param kind the kind of functions
	 * @param count number of functions to be generated
	 * @param psm a space manager for polynomial management
	 * @return list of functions
	 * @param <T> data type
	 */
	public <T> FunctionList<T> getFunctions
	(String kind, int count, PolynomialSpaceManager<T> psm)
	{
		FunctionList<T> list = new FunctionList<T>();
		ExpressionSpaceManager<T> esm = getExpressionManager (psm);
		T initialParameter = esm.convertFromDouble (Double.parseDouble (kind));
		for (int i = 0; i < count; i++)
		{
			list.add
			(
				getFunction
				(
					esm.add (initialParameter, esm.newScalar (i)),
					DEFAULT_POLYNOMIAL_TERM_COUNT,
					psm
				)
			);
		}
		return list;
	}

	/**
	 * abstraction of function generator
	 * @param parameter the alpha parameter value
	 * @param termCount the number of terms for the polynomial
	 * @param psm a manager for the polynomials of this data type
	 * @return a function description object
	 * @param <T> data type manager
	 */
	public abstract <T> SpecialFunctionFamilyManager.FunctionDescription<T>
	getFunction (T parameter, int termCount, PolynomialSpaceManager<T> psm);

	/**
	 * @param parameter the alpha parameter value
	 * @param lib a library for the data type being used
	 * @param psm a manager for the polynomials of this data type
	 * @return a function description object
	 * @param <T> data type manager
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getFunction
	(T parameter, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		return getFunction (parameter, DEFAULT_POLYNOMIAL_TERM_COUNT, lib, psm);
	}

	/**
	 * @param order the alpha parameter value
	 * @param lib a library for the data type being used
	 * @param psm a manager for the polynomials of this data type
	 * @return a function description object
	 * @param <T> data type manager
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getFunction
	(String order, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		ExpressionSpaceManager<T> esm = getExpressionManager (psm);
		T parameter = esm.convertFromDouble (Double.parseDouble (order));
		return getFunction (parameter, DEFAULT_POLYNOMIAL_TERM_COUNT, lib, psm);
	}

	/**
	 * @param parameter the alpha parameter value
	 * @param termCount the number of terms for the polynomial
	 * @param lib a library of functions for the data type being used
	 * @param psm a manager for the polynomials of this data type
	 * @return a function description object
	 * @param <T> data type manager
	 */
	public abstract <T> SpecialFunctionFamilyManager.FunctionDescription<T>
	getFunction (T parameter, int termCount, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm);


	public static int DEFAULT_POLYNOMIAL_TERM_COUNT = 25;


}

