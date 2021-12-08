
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

/**
 * support for describing Struve M (Modified Second Kind) functions
 * @author Michael Druckman
 */
public class ModifiedSecondKindStruve extends UnderlyingOperators
{


	// L#a = -i * e ^ ( -pi/2 * a * i ) * H#a(x)		M#a = L#a(x) - I#a(x)


	/**
	 * describe a Struve function Ma where a is a real number
	 * @param a real number identifying the order of the Ma description
	 * @param termCount the number of terms to include in the polynomials
	 * @param psm a space manager for polynomial management
	 * @return a function description for Ma
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
			getM (T a, int termCount, PolynomialSpaceManager<T> psm)
	{ return new MaFunction<T>(a, termCount, null, getExpressionManager (psm)); }


	/**
	 * function class for Ma formula
	 * @param <T> type on which operations are to be executed
	 */
	public static class MaFunction<T>
		implements SpecialFunctionFamilyManager.FunctionDescription<T>
	{

		MaFunction
			(
				T a, int termCount, ExtendedPowerLibrary<T> lib, ExpressionSpaceManager<T> sm
			)
		{
			this.lib = lib;
			processParameter (a, sm);
			createM (termCount);
		}
		protected ExtendedPowerLibrary<T> lib;

		protected void createM (int termCount)
		{
			PolynomialSpaceManager<T> psm = new PolynomialSpaceManager<T>(sm);
			this.La = ModifiedFirstKindStruve.getL (parameter, termCount, psm);
			this.Ia = ModifiedFirstKind.getI (parameter, termCount, psm);
		}
		protected SpecialFunctionFamilyManager.FunctionDescription<T> La, Ia;

		/**
		 * @param a the order for the function
		 * @param sm expression manager for data type
		 */
		void processParameter (T a, ExpressionSpaceManager<T> sm)
		{
			this.parameterValue = sm.convertToDouble (a);
			this.parameter = a;
			this.sm = sm;
		}
		protected Double parameterValue;
		protected T parameter;

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public T eval (T x)
		{
			return sm.add
			(
				La.eval (x), sm.negate (Ia.eval (x))
			);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
		 */
		public StringBuffer getFunctionDescription ()
		{
			return new StringBuffer ("Struve: M(a=").append (parameterValue).append (")");
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
		 */
		public String getFunctionName ()
		{
			return "MA" + formatParameterDisplay (parameterValue);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getRenderIdentifier()
		 */
		public String getRenderIdentifier () { return "M"; }

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
		return getM (parameter, terms, psm);
	}


	/**
	 * extended to domain beyond Real
	 * @param <T> the data type
	 */
	public static class MaExtendedFunction<T> extends MaFunction<T>
	{
	
		MaExtendedFunction
			(
				T a, int termCount, ExtendedPowerLibrary<T> lib, ExpressionSpaceManager<T> sm
			)
		{
			super (a, termCount, lib, sm);
		}
	
	}


	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getM (T a, int termCount, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{ return new MaExtendedFunction<T>(a, termCount, lib, getExpressionManager (psm)); }


	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getFunction
		(T parameter, int terms, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		return getM (parameter, terms, lib, psm);
	}


}

