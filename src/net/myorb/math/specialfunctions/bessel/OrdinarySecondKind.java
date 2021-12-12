
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.specialfunctions.Library;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.data.abstractions.SpaceDescription;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

/**
 * support for describing Bessel Y (Ordinary Second Kind) functions
 * @author Michael Druckman
 */
public class OrdinarySecondKind extends UnderlyingOperators
{


	// Y#n(z) = -2^n / ( pi * z^n ) * SIGMA [0 <= k <= n-1] ( (z^2 / 4)^k * (n - k - 1)! / k! ) -
	//                  ( z^n / ( 2^n * pi ) * SIGMA [0 <= k <= INFINITY] ( (psi(k+1) + psi(n+k+1)) * ( - z^2 / 4)^k / ( k! * (n+k)! ) ) ) +
	//                  ( 2/pi * J#n(z) * ln (z/2) )

	// Y#a(x) = ( J#a(x) * cos(a*pi) - J#-a(x) ) / sin(a*pi)
	
	// Y#n(x) = LIM [a -> n] Y#a { to avoid GAMMA(-n) }


	/**
	 * describe a Bessel function Ya where a is a real number
	 * @param a real number identifying the order of the Ya description
	 * @param termCount the number of terms to include in the polynomials
	 * @param psm a space manager for polynomial management
	 * @return a function description for Ya
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
			getY (T a, int termCount, PolynomialSpaceManager<T> psm)
	{ return new YaFunction<T>(a, termCount, null, getExpressionManager (psm)); }


	/**
	 * function class for Ya formula
	 * @param <T> type on which operations are to be executed
	 */
	public static class YaFunction<T>
		implements SpecialFunctionFamilyManager.FunctionDescription<T>
	{

		YaFunction
			(
				T a, int termCount, ExtendedPowerLibrary<T> lib, ExpressionSpaceManager<T> sm
			)
		{
			this.lib = lib;
			processParameter (a, sm);
			createJ (termCount);
		}
		protected ExtendedPowerLibrary<T> lib;

		protected void createJ (int termCount)
		{
			T p = parameter;
			PolynomialSpaceManager<T> psm = new PolynomialSpaceManager<T>(sm);
			this.Jna = OrdinaryFirstKind.getJ (sm.negate (p), termCount, psm);
			this.Ja = OrdinaryFirstKind.getJ (p, termCount, psm);
		}
		protected SpecialFunctionFamilyManager.FunctionDescription<T> Ja, Jna;

		/**
		 * @param a the order for the function
		 * @param sm expression manager for data type
		 */
		void processParameter (T a, ExpressionSpaceManager<T> sm)
		{
			this.parameterValue = sm.convertToDouble (a);
			this.parameter = integerOrderCheck (a, sm); this.sm = sm;
			this.computeTrigConstants (sm.convertToDouble (parameter));
		}
		protected Double parameterValue;
		protected T parameter;

		void computeTrigConstants (double p)
		{
			double alphaPi = p * Math.PI;
			double cosAlphaPi = Math.cos (alphaPi), sinAlphaPi = Math.sin (alphaPi);
			this.cotAlphaPi = sm.convertFromDouble (cosAlphaPi / sinAlphaPi);
			this.negCscAlphaPi = sm.convertFromDouble (-1 / sinAlphaPi);
		}
		protected T cotAlphaPi, negCscAlphaPi;

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public T eval (T x)
		{
			return sm.add
			(
				sm.multiply (cotAlphaPi, Ja.eval (x)),
				sm.multiply (negCscAlphaPi, Jna.eval (x))
			);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
		 */
		public StringBuffer getFunctionDescription ()
		{
			return new StringBuffer ("Bessel: Y(a=").append (parameterValue).append (")");
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
		 */
		public String getFunctionName ()
		{
			return "YA" + formatParameterDisplay (parameterValue);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getRenderIdentifier()
		 */
		public String getRenderIdentifier () { return "Y"; }

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#getSpaceManager()
		 */
		public SpaceManager<T> getSpaceDescription () { return sm; }
		public SpaceManager<T> getSpaceManager () { return sm; }
		protected ExpressionSpaceManager<T> sm;

	}


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators#getFunction(java.lang.Object, int, net.myorb.math.polynomial.PolynomialSpaceManager)
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getFunction (T parameter, int terms, PolynomialSpaceManager<T> psm)
	{
		return getY (parameter, terms, psm);
	}


	/**
	 * extended to domain beyond Real
	 * @param <T> the data type
	 */
	public static class YaExtendedFunction<T> extends YaFunction<T>
	{
	
		YaExtendedFunction
			(
				T a, int termCount, ExtendedPowerLibrary<T> lib, ExpressionSpaceManager<T> sm
			)
		{
			super (a, termCount, lib, sm);
		}
	
		protected void createJ (int termCount)
		{
			T p = parameter;
			PolynomialSpaceManager<T> psm = new PolynomialSpaceManager<T>(sm);
			this.Jna = OrdinaryFirstKind.getJ (sm.negate (p), termCount, lib, psm);
			this.Ja = OrdinaryFirstKind.getJ (p, termCount, lib, psm);
		}
	
	}


	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getY (T a, int termCount, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{ return new YaExtendedFunction<T>(a, termCount, lib, getExpressionManager (psm)); }


	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getFunction
		(T parameter, int terms, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		return getY (parameter, terms, lib, psm);
	}


	/**
	 * special case for integer order.
	 *  LIM [ a -> n ] to avoid GAMMA(-n)
	 * @param parameter the alpha value order
	 * @param terms the count of terms for the series
	 * @param lib an extended library of primitive functions
	 * @param psm the manager for the polynomial space
	 * @return the function description
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getSpecialCase
		(T parameter, int terms, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		ExpressionSpaceManager<T> sm = getExpressionManager (psm);
		int n = sm.toNumber (parameter).intValue ();
		return getY (n, terms, lib, sm);
	}


	/**
	 * an alternative to the LIM [ a -> n ]...
	 *  the function evaluation algorithm uses digamma
	 * @param n the value of (alpha) order which is integer
	 * @param termCount the count of terms for the series
	 * @param lib an extended library of primitive functions
	 * @param sm the manager for the data type
	 * @return the function description
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getY (int n, int termCount, ExtendedPowerLibrary<T> lib, ExpressionSpaceManager<T> sm)
	{
		return new SpecialFunctionFamilyManager.FunctionDescription<T>()
		{

			/* (non-Javadoc)
			 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
			 */
			public T eval (T x) { return Y.eval (x); }
			Yn<T> Y = new Yn<T> (n, termCount, lib, sm);

			/* (non-Javadoc)
			 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
			 */
			public StringBuffer getFunctionDescription ()
			{
				return new StringBuffer ("Bessel: Y(n=").append (n).append (")");
			}

			/* (non-Javadoc)
			 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getRenderIdentifier()
			 */
			public String getRenderIdentifier () { return "Y"; }

			/* (non-Javadoc)
			 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
			 */
			public String getFunctionName () { return "Y_" + n; }

			/* (non-Javadoc)
			 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
			 */
			public SpaceDescription<T> getSpaceDescription () { return sm; }
			public SpaceManager<T> getSpaceManager () { return sm; }

		};
	}

}


class Yn<T>
{

	interface Term<T>
	{
		T eval (int k, T z);
	}

	Yn (int n, int termCount, ExtendedPowerLibrary<T> lib, ExpressionSpaceManager<T> sm)
	{
		this.J = OrdinaryFirstKind.getJ
			(sm.newScalar (n), termCount, lib, new PolynomialSpaceManager<T>(sm));
		this.TWO = sm.newScalar (2); this.HALF = sm.invert (TWO);
		this.PI_INVERTED = sm.invert (sm.getPi ());
		this.lib = lib; this.sm = sm;
		this.infinity = termCount;
		this.n = n;
	}
	ExtendedPowerLibrary<T> lib; ExpressionSpaceManager<T> sm;
	SpecialFunctionFamilyManager.FunctionDescription<T> J;
	T TWO, HALF, PI_INVERTED; int n, infinity;

	public T eval (T z)
	{
		try
		{
			T sum = BYnTerm1 (z);
			sum = sm.add (sum, sm.negate (BYnTerm2 (z)));
			sum = sm.add (sum, sm.negate (BYnTerm3 (z)));
			return sm.multiply (PI_INVERTED, sum);
		}
		catch (Exception x) { throw new RuntimeException ("Eval error", x); }
	}

	/**
	 * 2 * BJn(z) * ln (z/2)
	 * @param z the function parameter value
	 * @return the term evaluation
	 */
	public T BYnTerm1 (T z)
	{
		T twoJz = sm.multiply (TWO, J.eval (z));
		return sm.multiply (twoJz, lib.ln (sm.multiply (z, HALF)));
	}

	/**
	 * (z / 2)^n * series
	 * @param z the function parameter value
	 * @return the term evaluation
	 */
	public T BYnTerm2 (T z)
	{
		return sm.multiply (lib.pow (sm.multiply (HALF, z), n), digammaSeries (z));
	}

	/**
	 * (2 / z)^n * series
	 * @param z the function parameter value
	 * @return the term evaluation
	 */
	public T BYnTerm3 (T z)
	{
		return sm.multiply (lib.pow (sm.multiply (TWO, sm.invert (z)), n), factorialSeries (z));
	}

	/**
	 * ( (-1)^k * (psi(k+1) + psi(n+k+1)) / ( 4^k * k! * (n+k)! ) )
	 * @param k the loop index value for the summation
	 * @return the coefficient for the k term
	 */
	public T digammaCoefficient (int k)
	{
		double num = Library.digamma (k + 1) * Library.digamma (n + k + 1),
			den = Math.pow (4, k) * factorial (k) * factorial (n + k);
		return sm.convertFromDouble (sgn (k) * num / den);
	}
	int sgn (int k) { return k%2 == 0? 1: -1; }

	/**
	 * SUMMATION [0 <= k <= INFINITY]
	 *   ( (-1)^k * (psi(k+1) + psi(n+k+1)) * z^(2*k) / ( 4^k * k! * (n+k)! ) )
	 * @param z the function parameter value
	 * @return the series evaluation
	 */
	public T digammaSeries (T z)
	{
		return summation
		(
			0, infinity, z,
			new Term<T>()
			{
				public T eval (int k, T z)
				{
					return sm.multiply
					(
						digammaCoefficient (k),
						lib.pow (z, 2*k)
					);
				}
			}
		);
	}

	/**
	 * (n - k - 1)! / (4^k * k!)
	 * @param k the loop index value for the summation
	 * @return the coefficient for the k term
	 */
	public T factorialCoefficient (int k)
	{
		double num = factorial (n - k - 1),
				den = Math.pow (4, k) * factorial (k);
		return sm.convertFromDouble (num / den);
	}

	/**
	 * SUMMATION [0 <= k <= n-1] ( (z^2 / 4)^k * ( (n - k - 1)! / k! ) )
	 * @param z the function parameter value
	 * @return the series evaluation
	 */
	public T factorialSeries (T z)
	{
		return summation
		(
			0, n-1, z,
			new Term<T>()
			{
				public T eval (int k, T z)
				{
					return sm.multiply
					(
						factorialCoefficient (k),
						lib.pow (z, 2*k)
					);
				}
			}
		);
	}

	/**
	 * @param lo the starting value
	 * @param hi the largest to be evaluated
	 * @param z the function parameter value
	 * @param t a Term object for the summation
	 * @return the sum of the terms
	 */
	T summation (int lo, int hi, T z, Term<T> t)
	{
		T sum = sm.getZero ();
		for (int k = lo; k <= hi; k++)
		{ sum = sm.add (sum, t.eval (k, z)); }
		return sum;
	}

	/**
	 * common factorial
	 * @param x integer parameter
	 * @return result as double
	 */
	public double factorial (int x)
	{
		return Library.factorial (x).doubleValue ();
	}

	/*
!! BYnTerm1(z) = 2 * BJn(z) * ln (z/2)

!! BYnTerm2(z) = (z / 2)^n * BYnSeries(z)

!! BYnSeries(z) = SUMMATION [0 <= k <= INFINITY]
	( (-1)^k * (psi(k+1) + psi(n+k+1)) * z^(2*k) / ( 4^k * k! * (n+k)! ) )

!! BYnTerm3(z) = (2 / z)^n * SUMMATION [0 <= k <= n-1] ( (z^2 / 4)^k * ( (n - k - 1)! / k! ) )

!! BYn(z) = 1/pi * (BYnTerm1(z) - BYnTerm2(z) - BYnTerm3(z))
	 */

}

