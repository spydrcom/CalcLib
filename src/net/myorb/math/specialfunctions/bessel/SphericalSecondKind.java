
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.SpaceManager;

/**
 * support for describing Bessel y (Spherical Second Kind) functions
 * @author Michael Druckman
 */
public class SphericalSecondKind extends UnderlyingOperators
{


	// Y#a(x) = ( J#a(x) * cos(a*pi) - J#-a(x) ) / sin(a*pi)

	// y#n = sqrt(pi/2x) * Y#(n+1/2) (x)
	
	// Riccati: C#n(x) = -x y#n(x)


	/**
	 * describe a Bessel function yn where n is an integer
	 * @param n the integer order of the function to be used
	 * @param termCount the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @return a function description for j
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
			gety (int n, int termCount, PolynomialSpaceManager<T> psm)
	{
		return new SphericalSecondKindFunction<T>(n, false, termCount, getExpressionManager (psm));
	}


	/**
	 * describe a Bessel function Cn where n is an integer
	 * @param n the integer order of the function to be used
	 * @param termCount the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @return a function description for C
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getC (int n, int termCount, PolynomialSpaceManager<T> psm)
	{
		return new SphericalSecondKindFunction<T>(n, true, termCount, getExpressionManager (psm));
	}


	/**
	 * function class for SphericalSecondKind formulas
	 * @param <T> type on which operations are to be executed
	 */
	public static class SphericalSecondKindFunction<T>
		implements SpecialFunctionFamilyManager.FunctionDescription<T>
	{

		SphericalSecondKindFunction
			(
				int n, boolean riccati, int termCount, ExpressionSpaceManager<T> sm
			)
		{
			identifier = riccati ? "C" : "y";
			T a = sm.convertFromDouble (n + 0.5);
			int riccatiMultiplier = riccati ? 1 : -1;
			multiplier = sm.convertFromDouble (-riccatiMultiplier * Math.sqrt (Math.PI / 2));
			PolynomialSpaceManager<T> psm = new PolynomialSpaceManager<T>(sm);
			powerOfX = sm.invert (sm.newScalar (2 * riccatiMultiplier));
			Ya = OrdinarySecondKind.getY (a, termCount, psm);
			processParameter (n, sm);
		}
		protected SpecialFunctionFamilyManager.FunctionDescription<T> Ya;
		protected T multiplier, powerOfX;
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
			T expOfX = sm.multiply (multiplier, realPower (x, powerOfX, sm));
			return sm.multiply (expOfX, Ya.eval (x));
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
		return gety (sm.toNumber (parameter).intValue (), terms, psm);
	}


}

