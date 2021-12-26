
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.specialfunctions.Library;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;
import net.myorb.math.specialfunctions.bessel.BesselDescription.OrderTypes;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.GeneratingFunctions.Coefficients;

import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;
import net.myorb.math.Function;

import net.myorb.data.abstractions.SpaceDescription;

import java.util.Map;

/**
 * support for describing Bessel Y (Ordinary Second Kind) functions
 * @author Michael Druckman
 */
public class OrdinarySecondKind extends UnderlyingOperators
{


	// Y#n(z) = -2^n / ( pi * z^n ) * SIGMA [0 <= k <= n-1] ( (z^2 / 4)^k * (n - k - 1)! / k! ) -
	//                  ( z^n / ( 2^n * pi ) * SIGMA [0 <= k <= INFINITY] ( (psi(k+1) + psi(n+k+1)) * ( - z^2 / 4)^k / ( k! * (n+k)! ) ) ) +
	//                  ( 2/pi * J#n(z) * ln (z/2) )

	// Y#n(z) = 1/pi * INTEGRAL [0 <= theta <= pi] ( sin (x*sin theta - n*theta) * <*> theta ) -
	//          1/pi * INTEGRAL [0 <= t <= INFINITY] ( exp (-x*sinh t) * ( exp (n*t) + -1^n * exp (-n*t) ) ) * <*> t) -

	// Y#a(i*z) = exp ((a+1)*i*pi/2) * I#a(z) - 2/pi * exp (-a*i*pi/2) * K#a(z)

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
	{ return new YaFunction<T>(a, termCount, getExpressionManager (psm)); }


	/**
	 * function class for Ya formula
	 * @param <T> type on which operations are to be executed
	 */
	public static class YaFunction<T> extends BesselDescription<T>
	{

		YaFunction
			(
				T a, int termCount, ExpressionSpaceManager<T> sm
			)
		{
			super (a,OrderTypes.LIM, "Y", "a", sm);
			processParameter (a, sm);
			createJ (termCount);
		}

		/**
		 * @param termCount approximation of infinity for infinite series
		 */
		protected void createJ (int termCount)
		{
			T p = parameter;
			PolynomialSpaceManager<T> psm = new PolynomialSpaceManager<T>(sm);
			this.Jna = OrdinaryFirstKind.getJ (sm.negate (p), termCount, psm);
			this.Ja = OrdinaryFirstKind.getJ (p, termCount, psm);
		}
		protected SpecialFunctionFamilyManager.FunctionDescription<T> Ja, Jna;

		/**
		 * check for special needs of -n order
		 * @param a the order for the function (can be integer or real)
		 * @param sm expression manager for data type
		 */
		void processParameter (T a, ExpressionSpaceManager<T> sm)
		{
			Number n; // Y#-n = -1^n * Y#n
			if (sm.isNegative(a) && Library.isInteger (n = sm.toNumber (a)))
			{ a = sm.negate (a); setNegate (n.intValue ()); }
			useParameter (integerOrderCheck (a, sm));
		}

		/**
		 * the order has been adjusted as necessary
		 *  to avoid GAMMA(-n) and COT(n*PI) in the Yn formulas
		 * @param p the value of the order of the function
		 */
		void useParameter (T p)
		{
			this.computeTrigConstants
			(
				sm.convertToDouble (this.parameter = p)
			);
		}
		protected T parameter;

		/**
		 * COT (alpha PI) and CSC (alpha PI)
		 *  computed to be used as constants in formulas
		 * @param p the value of the order of the function
		 */
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
			T sum = sm.add
			(
				sm.multiply (cotAlphaPi, Ja.eval (x)),
				sm.multiply (negCscAlphaPi, Jna.eval (x))
			);
			return negate? sm.negate (sum): sum;
		}
		void setNegate (int n) { negate = Library.alternatingSign (n) < 0; }
		protected boolean negate = false;

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
			super (a, termCount, sm); createJ (termCount, lib);
		}
	
		protected void createJ (int termCount, ExtendedPowerLibrary<T> lib)
		{
			T p = parameter;
			PolynomialSpaceManager<T> psm = new PolynomialSpaceManager<T>(sm);
			this.Jna = OrdinaryFirstKind.getJ (sm.negate (p), termCount, lib, psm);
			this.Ja = OrdinaryFirstKind.getJ (p, termCount, lib, psm);
		}
	
	}


	/**
	 * Ya extended to domain beyond Real
	 * @param a the alpha order allowing real numbers
	 * @param termCount the number of terms for the series
	 * @param lib an extended library of primitive functions
	 * @param psm the manager for the polynomial space
	 * @return the function description
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getY (T a, int termCount, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{ return new YaExtendedFunction<T>(a, termCount, lib, getExpressionManager (psm)); }


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators#getFunction(java.lang.Object, int, net.myorb.math.ExtendedPowerLibrary, net.myorb.math.polynomial.PolynomialSpaceManager)
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getFunction
		(T parameter, int terms, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		return getY (parameter, terms, lib, psm);
	}


	/**
	 * special case for integer order.
	 *  LIM [ a -&gt; n ] to avoid GAMMA(-n)
	 * @param parameter the alpha value order
	 * @param terms the count of terms for the series
	 * @param lib an extended library of primitive functions
	 * @param parameters a hash of name/value pairs passed from configuration
	 * @param psm the manager for the polynomial space
	 * @return the function description
	 * @param <T> data type manager
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getSpecialCase
		(
			T parameter, int terms, ExtendedPowerLibrary<T> lib,
			Map<String,Object> parameters, PolynomialSpaceManager<T> psm
		)
	{
		ExpressionSpaceManager<T>
			sm = getExpressionManager (psm);
		int n = sm.toNumber (parameter).intValue ();
		return getY (n, terms, lib, parameters, sm);
	}


	/**
	 * Yn algorithm using integrals
	 * @param order the value of (alpha) order which is integer
	 * @param infinity the approximation to be used for infinity
	 * @param parameters a hash of name/value pairs passed from configuration
	 * @param psm the manager for polynomial processing
	 * @return the function description
	 * @param <T> data type manager
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getSpecialCase
		(
			T order, int infinity, Map<String,Object> parameters,
			PolynomialSpaceManager<T> psm
		)
	{
		ExpressionSpaceManager<T>
			sm = getExpressionManager (psm);
		int n = sm.toNumber (order).intValue ();
		return getY (n, infinity, parameters, sm);
	}


	/**
	 * an alternative to the LIM [ a -&gt; n ]...
	 *  the function evaluation algorithm uses digamma
	 * @param n the value of (alpha) order which is integer
	 * @param termCount the count of terms for the series
	 * @param lib an extended library of primitive functions
	 * @param parameters a hash of name/value pairs passed from configuration
	 * @param sm the manager for the data type
	 * @return the function description
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T> getY
		(
			int n, int termCount, ExtendedPowerLibrary<T> lib,
			Map<String,Object> parameters, ExpressionSpaceManager<T> sm
		)
	{
		return new BesselDescription<T> (sm.newScalar (n), OrderTypes.INT, "Y", "n", sm)
		{
			/* (non-Javadoc)
			 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
			 */
			public T eval (T x) { return Y.eval (x); }
			Yn<T> Y = new Yn<T> (n, termCount, lib, parameters, sm);
		};
	}


	/**
	 * Yn algorithm using integrals
	 * @param n the value of (alpha) order which is integer
	 * @param infinity the approximation to be used for infinity
	 * @param parameters a hash of name/value pairs passed from configuration
	 * @param sm the manager for the data type
	 * @return the function description
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T> getY
		(
			int n, int infinity,
			Map<String,Object> parameters,
			ExpressionSpaceManager<T> sm
		)
	{
		return new BesselDescription<T> (sm.newScalar (n), OrderTypes.INT, "Y", "n", sm)
		{
			/* (non-Javadoc)
			 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
			 */
			public T eval (T x) { return Y.eval (x); }
			YnIntegral<T> Y = new YnIntegral<T>
			(n, infinity, parameters, sm);
		};
	}


}


/**
 * generate terms of series that compute Yn
 * @param <T> data type of terms
 */
class YnSeries<T> extends Library
{
	
	/**
	 * @param k the index of the term
	 * @return the ratio of the factorials of the term
	 */
	public double factorialQuotient (int k)
	{
		double num = factorial (n - k - 1),
				den = factorial (k);
		return num / den;
	}

	/**
	 * @param k the index of the term
	 * @return the quotient of digamma to the factorial of the term
	 */
	public double digammaOverFactorial (int k)
	{
		double num = digamma (k + 1) + digamma (n + k + 1);
		double den = factorial (k) * factorial (n + k);
		return num / den;
	}

	/**
	 * compute quotient
	 * @param num numerator
	 * @param den denominator
	 * @return quotient converted to T data type
	 */
	public T ratio (double num, double den)
	{
		return sm.convertFromDouble (num / den);
	}

	/**
	 * compute quotient
	 * @param num numerator
	 * @param den denominator
	 * @param k the index of the term
	 * @return quotient converted to T data type
	 */
	public T alternatingRatio (double num, double den, int k)
	{
		return ratio (alternatingSign (k) * num, den);
	}

	/**
	 * ( (-1)^k * (psi(k+1) + psi(n+k+1)) / ( 4^k * k! * (n+k)! ) )
	 * @param k the loop index value for the summation
	 * @return the coefficient for the k term
	 */
	public T digammaCoefficient (int k)
	{
		double num = digammaOverFactorial (k), den = Math.pow (4, k);
		return alternatingRatio (num, den, k);
	}

	/**
	 * @param k the loop index value for the summation
	 * @return the coefficient for the k term
	 */
	public T infiniteSeriesTermCoefficient  (int k)
	{
		double num = digammaOverFactorial (k),
			den = Math.pow (2, 2*k + n);
		return alternatingRatio (num, den, k);								// common denominator with finite series
	}

	/**
	 * (n - k - 1)! / (4^k * k!)
	 * @param k the loop index value for the summation
	 * @return the coefficient for the k term
	 */
	public T factorialCoefficient (int k)
	{
		return ratio (factorialQuotient (k), Math.pow (4, k));
	}

	/**
	 * @param k the loop index value for the summation
	 * @return the coefficient for the k term
	 */
	public T finiteSeriesTermCoefficient  (int k)
	{
		return ratio (factorialQuotient (k), Math.pow (2, 2 * k - n));		// no negative power in polynomial
	}

	/**
	 * [0 <= k <= n-1] ( (n - k - 1)! / ( 4^k * k! ) )
	 * @return the computed coefficients
	 */
	public Coefficients<Double> getFactorialCoefficients ()
	{
		Coefficients<Double> cs = new Coefficients<Double>();
		for (int k=0; k<=n-1; k++)
		{
			cs.add (factorialQuotient (k) / Math.pow (4, k));
		}
		return cs;
	}

	/**
	 * [0 <= k <= INFINITY] ( (-1)^k * (psi(k+1) + psi(n+k+1)) / ( 4^k * k! * (n+k)! ) )
	 * @param infinity approximation being used for infinity
	 * @return the computed coefficients
	 */
	public Coefficients<Double> getPsiCoefficients (int infinity)
	{
		Coefficients<Double> cs = new Coefficients<Double>();
		for (int k=0; k<=infinity; k++)
		{
			cs.add (alternatingSign (k) * digammaOverFactorial (k) / Math.pow (4, k));
		}
		return cs;
	}

	/**
	 * Y#-n = -1^n * Y#n
	 * @param n the integer order
	 */
	void setOrder (int n)
	{
		if (n < 0)
		{
			this.n = -n;
			this.negate = this.n % 2 == 1;
		} else this.n = n;
	}
	protected boolean negate = false;
	protected int n;

	/**
	 * @param n the order of the series
	 * @param sm a manager object for the data type
	 */
	public YnSeries (int n, ExpressionSpaceManager<T> sm)
	{
		this.sm = sm; setOrder (n);
	}
	protected ExpressionSpaceManager<T> sm;

}


/**
 * straight code representation of the equations for Yn
 * @param <T> data type of terms
 */
class YnEquations<T> extends YnSeries<T>
{

	public YnEquations
		(
			int n, int infinity,
			ExtendedPowerLibrary<T> lib,
			ExpressionSpaceManager<T> sm
		)
	{
		super (n, sm);
		this.lib = lib; this.infinity = infinity;
		this.psm = new PolynomialSpaceManager<T>(sm);
		this.TWO = sm.newScalar (2); this.HALF = sm.invert (TWO);
		this.PI_INVERTED = sm.invert (sm.getPi ());
		this.constructJn ();
	}
	protected PolynomialSpaceManager<T> psm;

	/**
	 * get object for calculation of Bessel Jn
	 */
	public void constructJn ()
	{ this.Jn = OrdinaryFirstKind.getJ (sm.newScalar (n), infinity, lib, psm); }
	protected SpecialFunctionFamilyManager.FunctionDescription<T> Jn;

	/**
	 * compute half of parameter
	 * @param z parameter for computation
	 * @return calculated z/2
	 */
	public T half (T z)
	{ return sm.multiply (HALF, z); }
	protected T TWO, HALF;

	/**
	 * calculate (z/2)^n
	 * @param z parameter for exponentiation base
	 * @param n exponent for computation
	 * @return computed value
	 */
	public T halfZto (T z, int n)
	{ return lib.pow (half (z), n); }
	protected ExtendedPowerLibrary<T> lib;

	/**
	 * 2 * BJn(z) * ln (z/2)
	 * @param z the function parameter value
	 * @return the term evaluation
	 */
	public T BYnTerm1 (T z)
	{
		T twoJz = sm.multiply (TWO, Jn.eval (z));
		return sm.multiply (twoJz, lib.ln (half (z)));
	}

	/**
	 * (z / 2)^n * series
	 * @param z the function parameter value
	 * @return the term evaluation
	 */
	public T BYnTerm2 (T z)
	{
		return sm.multiply (halfZto (z, n), digammaSeries (z));
	}

	/**
	 * (2 / z)^n * series
	 * @param z the function parameter value
	 * @return the term evaluation
	 */
	public T BYnTerm3 (T z)
	{
		return sm.multiply (halfZto (z, -n), factorialSeries (z));
	}

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
			}, sm
		);
	}
	protected int infinity;

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
			}, sm
		);
	}

	/**
	 * this encapsulates term2+term3
	 * @param z the function parameter value
	 * @return the sum of terms 2 and 3
	 */
	public T digestSum (T z)
	{
		return sm.add (BYnTerm2 (z), BYnTerm3 (z));
	}

	/**
	 * override hook for digesting terms 
	 *  and calculating terms by polynomial
	 * @param z the function parameter value
	 * @return the sum of the other terms
	 */
	public T otherTerms (T z)
	{
		return sm.negate (digestSum (z));
	}

	/**
	 * evaluate Yn on parameter z
	 * @param z the parameter to the Yn function
	 * @return the computed value Yn(z)
	 */
	public T eval (T z)
	{
		try
		{
			T result = sm.multiply
			(
				PI_INVERTED,
				sm.add
				(
					BYnTerm1 (z),
					otherTerms (z)
				)
			);
			if (negate) return sm.negate (result);
			return result;
		}
		catch (Exception x) { throw new RuntimeException ("Eval error", x); }
	}
	protected T PI_INVERTED;

}


/**
 * construct the formula that will compute Yn
 * @param <T> data type of terms
 */
class Yn<T> extends YnEquations<T>
{


	/**
	 * @param n the order of the Y Bessel function
	 * @param termCount the number of terms to approximate infinity
	 * @param lib the library of functions operating on configured T data type
	 * @param parameters a hash of name/value pairs passed from configuration
	 * @param sm a manager object for the data type
	 */
	public Yn
		(
			int n, int termCount,
			ExtendedPowerLibrary<T> lib,
			Map<String,Object> parameters, 
			ExpressionSpaceManager<T> sm
		)
	{
		super (n, termCount, lib, sm);
		this.constructPolynomial (parameters);
	}

	/**
	 * @param parameters a hash of name/value pairs passed from configuration
	 * @return the method of calculation configured for the function
	 */
	public Methods identifyMethod (Map<String,Object> parameters)
	{
		Object specified;
		if ((specified = parameters.get ("method")) != null)
		{
			try
			{
				String name = specified.toString ();
				return Methods.valueOf (name.toUpperCase ());
			}
			catch (Exception e) {}
		}
		return Methods.SECTIONED;
	}
	public enum Methods
	{
		STRAIGHT,		// calculate every term for every function call
		POLYNOMIAL,		// construct a polynomial with all coefficients calculated once
		SECTIONED		// calculate the coefficients for each section and treat as 2 series
	}

	/**
	 * construct the polynomial representation for the equations
	 * @param parameters
	 */
	public void constructPolynomial (Map<String,Object> parameters)
	{
		switch (identifyMethod (parameters))
		{
			case POLYNOMIAL:
				this.yPoly = new YnPolynomial<T> (n, infinity, psm, sm);
				break;
			case SECTIONED:
				this.longForm = new YnLongForm<T> (n, infinity, sm);
				break;
			case STRAIGHT: break;
		}
	}
	protected YnLongForm<T> longForm = null;
	protected YnPolynomial<T> yPoly = null;

	/**
	 * yPoly(z) / z^n
	 *  this encapsulates term2+term3
	 * @param z the function parameter value
	 * @return the polynomial digest evaluation
	 */
	public T polynomialDigest (T z)
	{
		return sm.multiply (yPoly.eval (z), lib.pow (z, -n));
	}

	/**
	 * this encapsulates term2+term3.
	 *  method to be determined by criteria.
	 *  choices are polynomial or straight calculation.
	 *  also provided is long form calculation option.
	 * @param z the function parameter value
	 * @return the sum of terms 2 and 3
	 */
	public T digestLaterTerms (T z)
	{
		T digest;
		if (yPoly != null) { digest = polynomialDigest (z); }
		else if (longForm != null) { digest = longForm.eval (z); }
		else { digest = digestSum (z); }
		return sm.negate (digest);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.YnEquations#otherTerms(java.lang.Object)
	 */
	public T otherTerms (T z)
	{
		return digestLaterTerms (z);
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


/**
 * construct a polynomial representation of the equations that compute Yn(z)
 * @param <T> data type of terms
 */
class YnPolynomial<T> extends YnSeries<T> implements Polynomial.PowerFunction<T>
{


	/**
	 * @param n the order of the Y Bessel function
	 * @param termCount the number of terms to approximate infinity
	 * @param psm a polynomial manager object that will build the representation
	 * @param sm a manager object for the data type
	 */
	public YnPolynomial
		(
			int n, int terms,
			PolynomialSpaceManager<T> psm,
			ExpressionSpaceManager<T> sm
		)
	{
		super (n, sm); this.psm = psm;
		this.constructPolynomial (terms);
		this.showPolynomial (terms);
	}
	protected PolynomialSpaceManager<T> psm;


	/**
	 * ( (-1)^k * (psi(k+1) + psi(n+k+1)) / ( 2^(2*k+n) * k! * (n+k)! ) * z^(2*k+2*n) )
	 * @param infinity the number of terms to approximate infinity
	 * @return a representation of the polynomial
	 */
	public Polynomial.PowerFunction<T> infiniteSeries (int infinity)
	{
		Polynomial.PowerFunction<T> p = psm.getZero ();
		Polynomial.PowerFunction<T> x = psm.newVariable ();

		for (int k = 0; k <= infinity; k++)
		{
			T c = infiniteSeriesTermCoefficient (k);			// common denominator with finite series
			p = psm.addTermFor (c, x, 2*(k+n), p);				// given by extra factor of 'n' 
		}

		// BYnTerm2(z) = SUMMATION [0 <= k <= INFINITY]
		//		( (-1)^k * (psi(k+1) + psi(n+k+1)) / ( 2^(2*k+n) * k! * (n+k)! ) * z^(2*k+2*n) ) { / z^n }
		return p;			// note lib.pow (z, -n) in eval of Yn above
	}


	/**
	 * SUMMATION [0 <= k <= n-1] ( ( 2^n * (n - k - 1)! / (4^k * k!) ) * z^(2*k) )
	 * @return a representation of the polynomial for the finite series
	 */
	public Polynomial.PowerFunction<T> finiteSeries ()
	{
		Polynomial.PowerFunction<T> p = psm.getZero ();
		Polynomial.PowerFunction<T> x = psm.newVariable ();

		for (int k = 0; k <= n-1; k++)
		{																			// no negative power allowed in polynomial
			p = psm.addTermFor (finiteSeriesTermCoefficient (k), x, 2*k, p);		// 	  should be 2*k-n, but k starts at 0
		}

		// BYnTerm3(z) = SUMMATION [0 <= k <= n-1] ( ( 2^n * (n - k - 1)! / (4^k * k!) ) * z^(2*k) ) { / z^n }
		return p;			// note lib.pow (z, -n) in eval of Yn above
	}


	/**
	 * @param infinity the number of terms to approximate infinity
	 */
	public void constructPolynomial (int infinity)
	{
		Polynomial.PowerFunction<T> fs = finiteSeries ();
		Polynomial.PowerFunction<T> is = infiniteSeries (infinity);
		this.pf = psm.add (is, fs);
	}
	public void showPolynomial (int infinity)
	{
		System.out.println ( "Yn polynomial ( n=" + n + ", INFINITY=" + infinity + " )");
		psm.show (pf);
	}
	protected Polynomial.PowerFunction<T> pf;


	/*
	 * 		implementation of Polynomial.PowerFunction<T>
	 * 		  a wrapper for the constructed polynomial
	 */

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x) { return pf.eval (x); }
	public SpaceManager<T> getSpaceManager () { return sm; }
	public SpaceDescription<T> getSpaceDescription () { return sm; }
	public PolynomialSpaceManager<T> getPolynomialSpaceManager () { return psm; }
	public Coefficients<T> getCoefficients () { return pf.getCoefficients (); }
	public Polynomial<T> getPolynomial () { return pf.getPolynomial (); }
	public int getDegree () { return pf.getDegree (); }


	/*
!! BYnTerm1(z) = 2 * BJn(z) * ln (z/2)

!! BYnTerm2(z) = SUMMATION [0 <= k <= INFINITY]
	( (-1)^k * (psi(k+1) + psi(n+k+1)) / ( 2^(2*k+n) * k! * (n+k)! ) * z^(2*k+n) )

// this term causes the difficulty implementing the polynomial representation, k=0 => z^(-n)
!! BYnTerm3(z) = SUMMATION [0 <= k <= n-1] ( ( 2^n * (n - k - 1)! / (4^k * k!) )  * z^(2*k-n) )
// polynomial representation assumes exponent in [ 0 .. degree ] so no -n allowed

!! BYn(z) = 1/pi * ( BYnTerm1(z) - ( BYnTerm2(z) + BYnTerm3(z) ) )
	 */


}


/**
 * construct a long form representation of the equations that compute Yn(z)
 * @param <T> data type of terms
 */
class YnLongForm<T> extends YnSeries<T> implements Function<T>
{


	/**
	 * @param n the order of the Y Bessel function
	 * @param termCount the number of terms to approximate infinity
	 * @param sm a manager object for the data type
	 */
	public YnLongForm
		(
			int n, int terms,
			ExpressionSpaceManager<T> sm
		)
	{
		super (n, sm);
		this.psiCoefficients = getPsiCoefficients (terms);
		this.factorialCoefficients = getFactorialCoefficients ();
	}
	protected Coefficients<Double> factorialCoefficients, psiCoefficients;

	/*
c1 = [0 <= k <= INFINITY] ( (-1)^k * (psi(k+1) + psi(n+k+1)) / ( 4^k * k! * (n+k)! ) )
c2 = [0 <= k <= n-1] ( (n - k - 1)! / ( 4^k * k! ) )

!! BYnTerm1(z) = 2 * BJn(z) * ln (z/2)

!! BYnTerm2(z,z2n) = z2n * SUMMATION [0 <= k <= INFINITY] ( c1#k * z^(2*k) )

!! BYnTerm3(z,z2n) = z2n * SUMMATION [0 <= k <= n-1] ( c2#k * z^(2*k) )

!! digest23(z,z2n) = BYnTerm2(z,z2n) + BYnTerm3(z,1/z2n)

!! BYn(z) = 1/pi * (BYnTerm1(z) - digest23(z,(z/2)^n))
	 */

	/**
	 * @param z parameter to Yn
	 * @param z2n calculation of (z/2)^n
	 * @return summation of terms
	 */
	public double BYnTerm2 (double z, double z2n)
	{
		return z2n * evalPoly (psiCoefficients, z);
	}

	/**
	 * @param z parameter to Yn
	 * @param z2n calculation of (z/2)^(-n)
	 * @return summation of terms
	 */
	public double BYnTerm3 (double z, double z2n)
	{
		return z2n * evalPoly (factorialCoefficients, z);
	}

	/**
	 * @param c the Coefficients of the polynomial terms
	 * @param z the parameter to the Yn function
	 * @return the computed result
	 */
	public double evalPoly (Coefficients<Double> c, double z)
	{
		double
		sum = 0.0, zsq = z * z, zpow = 1.0;
		for (int k=0; k<=c.size()-1; k++)
		{
			sum += c.get (k) * zpow;
			zpow *= zsq;
		}
		return sum;
	}

	/**
	 * @param z parameter to Yn
	 * @param z2n calculation of (z/2)^n
	 * @return summation of terms 2 + 3
	 */
	public double digest23 (double z, double z2n)
	{
		return BYnTerm2 (z, z2n) + BYnTerm3 (z, 1/z2n);
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		double z = sm.convertToDouble (x);
		double digest = digest23 (z, Math.pow (z/2, n));
		return sm.convertFromDouble (digest);
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<T> getSpaceDescription () { return sm; }

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<T> getSpaceManager () { return sm; }

}


/**
 * implement integral form of Yn
 * @param <T> data type used
 */
class YnIntegral<T> implements Function<T>
{

	YnIntegral
		(
			double a, int infinity, Map<String,Object> parameters,
			ExpressionSpaceManager<T> sm
		)
	{
		I1 = new Quadrature (new YnPart1Integrand (a), parameters).getIntegral ();
		I2 = new Quadrature (new YnPart2Integrand (a), parameters).getIntegral ();
		this.a = a; this.infinity = infinity;
		this.sm = sm;
	}
	ExpressionSpaceManager<T> sm;
	protected double a;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		double digest = integral
			(sm.convertToDouble (x));
		return sm.convertFromDouble (digest);
	}

	/**
	 * @param x parameter to Yn function
	 * @return calculated result
	 */
	double integral (double x)
	{
		return I1.eval (x, 0.0, Math.PI) - I2.eval (x, 0.0, infinity);
	}
	protected Quadrature.Integral I1, I2;
	protected int infinity;

	public SpaceDescription<T> getSpaceDescription() { return sm; }
	public SpaceManager<T> getSpaceManager() { return sm; }

	// Y#n(z) = 1/pi * INTEGRAL [0 <= theta <= pi] ( sin (x*sin theta - n*theta) * <*> theta ) -
	//          1/pi * INTEGRAL [0 <= t <= INFINITY] ( exp (-x*sinh t) * ( exp (n*t) + -1^n * exp (-n*t) ) ) * <*> t)

	// !! y(x,a,t) = exp ( - x * sinh (t) ) * ( exp (a*t) + (-1)^a * exp (-a*t) )

}

/**
 * part1 integral form of Yn
 */
class YnPart1Integrand extends RealIntegrandFunctionBase
{
	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Double eval (Double t)
	{ return Math.sin (x * Math.sin (t) - a * t); }
	YnPart1Integrand (double a) { super (a); }
}

/**
 * part2 integral form of Yn
 */
class YnPart2Integrand extends RealIntegrandFunctionBase
{
	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Double eval (Double t)
	{ return Math.exp ( - x * Math.sinh (t) ) * ( Math.exp (a*t) + Math.pow (-1, a) * Math.exp (-a*t) ); }
	YnPart2Integrand (double a) { super (a); }
}

