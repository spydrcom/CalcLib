
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

/**
 * support for describing Bessel j (Spherical First Kind) functions
 * @author Michael Druckman
 */
public class SphericalFirstKind extends UnderlyingOperators
{


	// J#p = SUMMATION [ 0 <= k <= INFINITY ] ( (-1)^k * (x/2)^(2*k+p) / ( k! * GAMMA(k+p+1) ) )

	// j#n = sqrt(pi/2x) * J#(n+1/2) (x)

	// Riccati: S#n(x) = x j#n(x)


	/**
	 * describe a Bessel function jn where n is an integer
	 * @param n the integer order of the function to be used
	 * @param termCount the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @return a function description for j
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
			getj (int n, int termCount, PolynomialSpaceManager<T> psm)
	{
		return new SphericalFirstKindFunction<T>(n, false, termCount, getExpressionManager (psm));
	}


	/**
	 * describe a Bessel function Sn where n is an integer
	 * @param n the integer order of the function to be used
	 * @param termCount the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @return a function description for S
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getS (int n, int termCount, PolynomialSpaceManager<T> psm)
	{
		return new SphericalFirstKindFunction<T>(n, true, termCount, getExpressionManager (psm));
	}


	/**
	 * @param n the order of the polynomial
	 * @param r additional power of X for Riccati formula
	 * @param termCount the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @return the polynomial power function
	 * @param <T> data type manager
	 */
	public static <T> Polynomial.PowerFunction<T>
		getSphericalFirstKindPolynomial (int n, int r, int termCount, PolynomialSpaceManager<T> psm)
	{
		double twoPow = Math.pow (2, n + 0.5);
		T constant = getExpressionManager (psm).convertFromDouble (Math.sqrt (Math.PI / 2) / twoPow);
		Polynomial.PowerFunction<T> poly = sumOfTerms (0, 0, termCount, n + 1.5, psm, false, getGammaSum ());
		Polynomial.PowerFunction<T> xToN = psm.pow (psm.newVariable (), n + r);
		return psm.times (constant, psm.multiply (xToN, poly));
	}


	/**
	 * function class for SphericalFirstKind formulas
	 * @param <T> type on which operations are to be executed
	 */
	public static class SphericalFirstKindFunction<T>
		implements SpecialFunctionFamilyManager.FunctionDescription<T>
	{

		SphericalFirstKindFunction
			(
				int n, boolean riccati, int termCount, ExpressionSpaceManager<T> sm
			)
		{
			int r = riccati ? 1 : 0;
			identifier = riccati ? "S" : "j";
			sphericalPolynomial = getSphericalFirstKindPolynomial (n, r, termCount, new PolynomialSpaceManager<T>(sm));
			processParameter (n, sm);
		}
		protected Polynomial.PowerFunction<T> sphericalPolynomial;
		protected String identifier;

		/**
		 * @param n the order for the function
		 * @param sm expression manager for data type
		 */
		void processParameter (int n, ExpressionSpaceManager<T> sm)
		{
			this.parameterValue = n;
			this.sm = sm;
			
		}
		protected int parameterValue;

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public T eval (T x)
		{
			return sphericalPolynomial.eval (x);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
		 */
		public StringBuffer getFunctionDescription ()
		{
			return new StringBuffer ("Bessel: ").append (identifier).append (" (n=").append (parameterValue).append (")");
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
		 */
		public String getFunctionName ()
		{
			return identifier + formatParameterDisplay ((double) parameterValue);
		}

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
		SpaceManager<T> sm = psm.getSpaceDescription ();
		return getj (sm.toNumber (parameter).intValue (), terms, psm);
	}


}

