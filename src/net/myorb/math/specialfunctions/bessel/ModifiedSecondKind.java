
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;

import net.myorb.math.SpaceManager;

/**
 * support for describing Bessel K (Modified Second Kind) functions
 * @author Michael Druckman
 */
public class ModifiedSecondKind extends UnderlyingOperators
{


	// K#a(x) = pi/2 * ( I#-a(x) - I#a(x) ) / sin(a*pi)


	/**
	 * describe a Bessel function Ka where a is a real number
	 * @param a real number identifying the order of the Ka description
	 * @param termCount the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @return a function description for Ka
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
			getK (T a, int termCount, PolynomialSpaceManager<T> psm)
	{ return new KaFunction<T>(a, termCount, getExpressionManager (psm)); }


	/**
	 * function class for Ka formula
	 * @param <T> type on which operations are to be executed
	 */
	public static class KaFunction<T>
		implements SpecialFunctionFamilyManager.FunctionDescription<T>
	{
	
		KaFunction
			(
				T a, int termCount, ExpressionSpaceManager<T> sm
			)
		{
			PolynomialSpaceManager<T>
				psm = new PolynomialSpaceManager<T>(sm);
			this.Ia = ModifiedFirstKind.getI (a, termCount, psm);
			this.Ina = ModifiedFirstKind.getI (sm.negate (a), termCount, psm);
			processParameter (a, sm);
		}
		protected SpecialFunctionFamilyManager.FunctionDescription<T> Ia, Ina;
	
		void processParameter (T a, ExpressionSpaceManager<T> sm)
		{
			double pi = Math.PI;
			this.parameterValue = sm.convertToDouble (a);
			constant = pi / (2 * Math.sin (parameterValue * pi));
			multiplier = sm.convertFromDouble (constant);
			this.parameter = a;
			this.sm = sm;
			
		}
		protected Double parameterValue;
		protected double constant;
		protected T multiplier;
		protected T parameter;
	
		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public T eval (T x)
		{
			return sm.multiply
			(
				sm.add
				(
					Ina.eval (x),
					sm.negate (Ia.eval (x))
				), multiplier
			);
		}
	
		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
		 */
		public StringBuffer getFunctionDescription ()
		{
			return new StringBuffer ("Bessel: K(a=").append (parameterValue).append (")");
		}
	
		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
		 */
		public String getFunctionName ()
		{
			return "KA" + formatParameterDisplay (parameterValue);
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
		return getK (parameter, terms, psm);
	}


}

