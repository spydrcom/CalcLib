
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

/**
 * support for describing Struve K (Ordinary Second Kind) functions
 * @author Michael Druckman
 */
public class OrdinarySecondKindStruve extends UnderlyingOperators
{


	// K#a = H#a(x) - Y#a(x)


	/**
	 * describe a Struve function Ka where a is a real number
	 * @param a real number identifying the order of the Ka description
	 * @param termCount the number of terms to include in the polynomials
	 * @param psm a space manager for polynomial management
	 * @return a function description for Ka
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
			getK (T a, int termCount, PolynomialSpaceManager<T> psm)
	{ return new KaFunction<T>(a, termCount, null, getExpressionManager (psm)); }


	/**
	 * function class for Ka formula
	 * @param <T> type on which operations are to be executed
	 */
	public static class KaFunction<T>
		implements SpecialFunctionFamilyManager.FunctionDescription<T>
	{

		KaFunction
			(
				T a, int termCount, ExtendedPowerLibrary<T> lib, ExpressionSpaceManager<T> sm
			)
		{
			this.lib = lib;
			processParameter (a, sm);
			createK (termCount);
		}
		protected ExtendedPowerLibrary<T> lib;

		protected void createK (int termCount)
		{
			PolynomialSpaceManager<T> psm = new PolynomialSpaceManager<T>(sm);
			this.Ha = OrdinaryFirstKindStruve.getH (parameter, termCount, psm);
			this.Ya = OrdinarySecondKind.getY (parameter, termCount, psm);
		}
		protected SpecialFunctionFamilyManager.FunctionDescription<T> Ha, Ya;

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
				Ha.eval (x), sm.negate (Ya.eval (x))
			);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
		 */
		public StringBuffer getFunctionDescription ()
		{
			return new StringBuffer ("Struve: K(a=").append (parameterValue).append (")");
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
		 */
		public String getFunctionName ()
		{
			return "KA" + formatParameterDisplay (parameterValue);
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getRenderIdentifier()
		 */
		public String getRenderIdentifier () { return "K"; }

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
		return getK (parameter, terms, psm);
	}


	/**
	 * extended to domain beyond Real
	 * @param <T> the data type
	 */
	public static class KaExtendedFunction<T> extends KaFunction<T>
	{
	
		KaExtendedFunction
			(
				T a, int termCount, ExtendedPowerLibrary<T> lib, ExpressionSpaceManager<T> sm
			)
		{
			super (a, termCount, lib, sm);
		}
	
		protected void createK (int termCount)
		{
			PolynomialSpaceManager<T> psm = new PolynomialSpaceManager<T>(sm);
			this.Ha = OrdinaryFirstKindStruve.getH (parameter, termCount, psm);
			this.Ya = OrdinarySecondKind.getY (parameter, termCount, psm);
		}
	
	}


	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getK (T a, int termCount, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{ return new KaExtendedFunction<T>(a, termCount, lib, getExpressionManager (psm)); }


	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getFunction
		(T parameter, int terms, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		return getK (parameter, terms, lib, psm);
	}


}

