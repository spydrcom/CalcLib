
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

/**
 * support for describing Bessel Y (Ordinary Second Kind) functions
 * @author Michael Druckman
 */
public class OrdinarySecondKind extends UnderlyingOperators
{


	// Y#a(x) = ( J#a(x) * cos(a*pi) - J#-a(x) ) / sin(a*pi)


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
			PolynomialSpaceManager<T> psm = new PolynomialSpaceManager<T>(sm);
			this.Jna = OrdinaryFirstKind.getJ (sm.negate (parameter), termCount, psm);
			this.Ja = OrdinaryFirstKind.getJ (parameter, termCount, psm);
		}
		protected SpecialFunctionFamilyManager.FunctionDescription<T> Ja, Jna;

		/**
		 * @param a the order for the function
		 * @param sm expression manager for data type
		 */
		void processParameter (T a, ExpressionSpaceManager<T> sm)
		{
			this.parameterValue =
					sm.convertToDouble (a);
			double alphaPi = parameterValue * Math.PI;
			double cosAlphaPi = Math.cos (alphaPi), sinAlphaPi = Math.sin (alphaPi);
			this.cotAlphaPi = sm.convertFromDouble (cosAlphaPi / sinAlphaPi);
			this.negCscAlphaPi = sm.convertFromDouble (-1 / sinAlphaPi);
			this.parameter = a;
			this.sm = sm;
		}
		protected T cotAlphaPi, negCscAlphaPi;
		protected Double parameterValue;
		protected T parameter;

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
			PolynomialSpaceManager<T> psm = new PolynomialSpaceManager<T>(sm);
			this.Jna = OrdinaryFirstKind.getJ (sm.negate (parameter), termCount, lib, psm);
			this.Ja = OrdinaryFirstKind.getJ (parameter, termCount, lib, psm);
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


}

