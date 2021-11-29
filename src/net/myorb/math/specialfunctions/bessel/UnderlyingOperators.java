
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionList;
import net.myorb.math.specialfunctions.Library;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;
import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.Function;

/**
 * common math formulas for Bessel functions
 * @author Michael Druckman
 */
public abstract class UnderlyingOperators extends Library
{


	public static <T> Polynomial.PowerFunction<T> getPoly 
	(T p, boolean modified, int termCount, PolynomialSpaceManager<T> psm, ExpressionSpaceManager<T> sm)
	{ return sumOfTerms (0, 0, termCount, plusOne (p, sm), psm, modified, getGammaSum ()); }


	/**
	 * compute Gamma (left, right) as appropriate for data type
	 * @param <T> data type being used
	 */
	public interface GammaSum<T>
	{
		/**
		 * @param left the left side term
		 * @param right the right left side term
		 * @param sm manager for data type
		 * @return Gamma (left + right)
		 */
		T computeGammaOfSum (Number left, Number right, SpaceManager<T> sm);
	}


	/**
	 * @param value added to ONE and treated as number
	 * @param esm the expression manager for the data type
	 * @return value incremented as raw number
	 * @param <T> data type being used
	 */
	public static <T> Number plusOne (T value, ExpressionSpaceManager<T> esm)
	{
		return esm.toNumber (esm.add (value, esm.getOne ()));
	}


	/**
	 * loop for term construction of polynomial
	 * @param initialPowX the power of X in the first term
	 * @param initialPow2 the power of 2 in the first denominator
	 * @param termCount the number of terms to include in the polynomial
	 * @param polynomialOrder the highest exponent value of the polynomial
	 * @param psm a space manager for polynomial management
	 * @param gamma the function to use for Gamma
	 * @param modified negation term removed
	 * @return the constructed polynomial
	 * @param <T> data type manager
	 */
	public static <T> Polynomial.PowerFunction<T> sumOfTerms
		(
			int initialPowX, int initialPow2,
			int termCount, Number polynomialOrder,
			PolynomialSpaceManager<T> psm, boolean modified,
			GammaSum<T> gamma
		)
	{
		SpaceManager<T> sm = psm.getSpaceDescription ();

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
			denom = pow2;
			denom = sm.multiply (denom, factorialT (k, sm));
			denom = sm.multiply (denom, gamma.computeGammaOfSum (k, polynomialOrder, sm));

			term = psm.times (sm.invert (denom), xpow);
			if (!modified) term = signT (term, k, psm);

			pow2 = sm.multiply (pow2, twoSq);
			xpow = psm.multiply (xpow, xsq);

			polynomial = psm.add (polynomial, term);
		}

		return polynomial;
	}


	/**
	 * connect polynomial part with exponential part of function
	 * @param <T> type on which operations are to be executed
	 */
	public static abstract class ExponentialFunction<T>
		implements SpecialFunctionFamilyManager.FunctionDescription<T>
	{

		public ExponentialFunction
			(
				T p,
				Function<T> polynomial,
				ExpressionSpaceManager<T> sm
			)
		{
			this.polynomial = polynomial;
			this.HALF = sm.invert (sm.newScalar (2));
			this.parameterValue = sm.convertToDouble (p);
			this.parameter = p;
			this.sm = sm;
		}
		protected Double parameterValue;
		protected T parameter;
		protected T HALF;

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public T eval (T x)
		{
			T xOver2 = sm.multiply (x, HALF);
			T exponentiation = TraisedToT (xOver2, parameter);
			return sm.multiply (exponentiation, polynomial.eval (x));
		}
		protected Function<T> polynomial;

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
	 * @return GammaSum for whole number parameters
	 * @param <T> data type manager
	 */
	public static <T> GammaSum<T> getFactorialSum ()
	{
		return new GammaSum<T>()
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.specialfunctions.Bessel.GammaSum#computeGammaOfSum(java.lang.Number, java.lang.Number, net.myorb.math.SpaceManager)
			 */
			public T computeGammaOfSum (Number left, Number right, SpaceManager<T> sm)
			{
				return factorialT (left.intValue () + right.intValue (), sm);
			}
		};
	}


	/**
	 * @return GammaSum for real number parameters
	 * @param <T> data type manager
	 */
	public static <T> GammaSum<T> getGammaSum ()
	{
		return new GammaSum<T>()
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.specialfunctions.Bessel.GammaSum#computeGammaOfSum(java.lang.Number, java.lang.Number, net.myorb.math.SpaceManager)
			 */
			public T computeGammaOfSum (Number left, Number right, SpaceManager<T> sm)
			{
				ExpressionSpaceManager<T> esm = (ExpressionSpaceManager<T>) sm;
				return gammaT (left.doubleValue () + right.doubleValue (), esm);
			}
		};
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
			list.add (getFunction (esm.add (initialParameter, esm.newScalar (i)), POLYNOMIAL_TERM_COUNT, psm));
		}
		return list;
	}
	public abstract <T> SpecialFunctionFamilyManager.FunctionDescription<T>
	getFunction (T parameter, int termCount, PolynomialSpaceManager<T> psm);

	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getFunction
	(T parameter, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		return getFunction (parameter, POLYNOMIAL_TERM_COUNT, lib, psm);
	}
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getFunction
	(String kind, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		ExpressionSpaceManager<T> esm = getExpressionManager (psm);
		T parameter = esm.convertFromDouble (Double.parseDouble (kind));
		return getFunction (parameter, POLYNOMIAL_TERM_COUNT, lib, psm);
	}
	public abstract <T> SpecialFunctionFamilyManager.FunctionDescription<T>
	getFunction (T parameter, int termCount, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm);

	public static int POLYNOMIAL_TERM_COUNT = 25;

}

