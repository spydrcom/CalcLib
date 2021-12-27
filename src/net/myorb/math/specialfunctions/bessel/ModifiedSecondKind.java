
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.computational.integration.Quadrature;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.ExtendedPowerLibrary;

import java.util.Map;

/**
 * support for describing Bessel K (Modified Second Kind) functions
 * @author Michael Druckman
 */
public class ModifiedSecondKind extends UnderlyingOperators
{


	// K#a(x) = pi/2 * ( I#-a(x) - I#a(x) ) / sin(a*pi)

	// INTEGRAL [0 <= t <= INFINITY <> dt] ( exp ( - x * cosh (t) ) * cosh (a * t) * <*> t )

	// K#n(x) = LIM [a -> n] K#a { to avoid GAMMA(-n) and COT(n*PI) }


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
	{ return new KaFunction<T>(a, termCount, null, getExpressionManager (psm)); }


	/**
	 * function class for Ka formula
	 * @param <T> type on which operations are to be executed
	 */
	public static class KaFunction<T> extends KDescription<T>
	{
	
		KaFunction
			(
				T a, int termCount, ExtendedPowerLibrary<T> lib, ExpressionSpaceManager<T> sm
			)
		{
			super (a, OrderTypes.LIM, sm);
			this.lib = lib; this.processParameter (a, sm);
			this.createK (termCount);
		}
		ExtendedPowerLibrary<T> lib;

		protected void createK (int termCount)
		{
			PolynomialSpaceManager<T> psm = new PolynomialSpaceManager<T>(sm);
			this.Ina = ModifiedFirstKind.getI (sm.negate (parameter), termCount, psm);
			this.Ia = ModifiedFirstKind.getI (parameter, termCount, psm);
		}
		protected SpecialFunctionFamilyManager.FunctionDescription<T> Ia, Ina;
	
		void processParameter (T a, ExpressionSpaceManager<T> sm)
		{
			this.parameterValue =
					sm.convertToDouble (a);
			this.parameter = integerOrderCheck (a, sm);
			this.computeTrigConstants (sm.convertToDouble (parameter));
		}
		protected Double parameterValue;
		protected T parameter;

		void computeTrigConstants (double p)
		{
			double pi = Math.PI;
			this.constant = pi / (2 * Math.sin (p * pi));
			this.multiplier = sm.convertFromDouble (constant);
		}
		protected double constant;
		protected T multiplier;

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
		 * @see net.myorb.math.specialfunctions.bessel.BesselDescription#evalReal(double)
		 */
		public double evalReal (double x) { return 0; }
	
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
	
		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.bessel.ModifiedSecondKind.KaFunction#createK(int)
		 */
		protected void createK (int termCount)
		{
			PolynomialSpaceManager<T> psm = new PolynomialSpaceManager<T>(sm);
			this.Ina = ModifiedFirstKind.getI (sm.negate (parameter), termCount, lib, psm);
			this.Ia = ModifiedFirstKind.getI (parameter, termCount, lib, psm);
		}
	
	}


	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getK (T a, int termCount, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{ return new KaExtendedFunction<T>(a, termCount, lib, getExpressionManager (psm)); }


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators#getFunction(java.lang.Object, int, net.myorb.math.ExtendedPowerLibrary, net.myorb.math.polynomial.PolynomialSpaceManager)
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getFunction
		(T parameter, int terms, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		return getK (parameter, terms, lib, psm);
	}


	/**
	 * special case for using integral
	 * @param parameter the alpha value order
	 * @param terms the count of terms for the series
	 * @param parameters a hash of name/value pairs passed from configuration
	 * @param psm the manager for the polynomial space
	 * @return the function description
	 * @param <T> data type manager
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getSpecialCase
		(T parameter, int terms, Map<String,Object> parameters, PolynomialSpaceManager<T> psm)
	{
		ExpressionSpaceManager<T> sm = getExpressionManager (psm);
		return getK (parameter, terms, parameters, sm);
	}


	/**
	 * an alternative to the LIM [ a -&gt; n ]...
	 *  the function evaluation algorithm from integral
	 * @param a the value of alpha which is integer/real
	 * @param termCount the count of terms for the series
	 * @param parameters a hash of name/value pairs passed from configuration
	 * @param sm the manager for the data type
	 * @return the function description
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getK (T a, int termCount, Map<String,Object> parameters, ExpressionSpaceManager<T> sm)
	{
		return new KaFunctionDescription<T> (a, termCount, parameters, sm);
	}


}


/**
 * encapsulation of descriptive text portions of FunctionDescription
 * @param <T> data type in use
 */
abstract class KDescription<T> extends BesselDescription<T>
{
	KDescription
	(T a, OrderTypes orderType, ExpressionSpaceManager<T> sm)
	{ super (a, orderType, "K", "a", sm); }
}


/**
 * Function Description for special case of Ka implementation.
 *  domain of real numbers, integral form is: exp ( - x * cosh (t) ) * cosh (a * t)
 * @param <T> data type in use
 */
class KaFunctionDescription<T> extends KDescription<T>
{

	KaFunctionDescription (T a, int termCount, Map<String,Object> parameters, ExpressionSpaceManager<T> sm)
	{
		super (a, OrderTypes.NON_SPECIFIC, sm);
		this.K = new Ka (sm.convertToDouble (a), termCount, parameters);
		this.parameters = parameters;
	}
	protected Ka K;

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.BesselDescription#evalReal(double)
	 */
	public double evalReal (double x) { return K.integral (x); }

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.KDescription#getElaboration()
	 */
	public String getElaboration () { return "   " + parameters.toString (); }
	protected Map<String,Object> parameters;

}


/**
 * provide real version of Ka using integral algorithm.
 * TanhSinh Quadrature is used to provide numerical integration
 */
class Ka
{

	Ka (double a, int infinity, Map<String,Object> parameters)
	{
		I = new Quadrature (new KalphaIntegrand (a), parameters).getIntegral ();
		this.a = a; this.infinity = infinity;
	}
	protected double a;

	/**
	 * Integral form of Ka:
	 * exp ( - x * cosh (t) ) * cosh (a * t)
	 * @param x parameter to Ka function
	 * @return calculated result
	 */
	double integral (double x)
	{
		return I.eval (x, 0.0, infinity);
	}
	protected Quadrature.Integral I;
	protected int infinity;

/*
	!! kap (x,a,t) = exp ( - x * cosh (t) ) * cosh (a * t)
	!! ka (x,a) = INTEGRAL [0 <= t <= INFINITY <> dt] (kap (x, a, t) * <*> t)
 */

}


/**
 * integral form of Ka
 */
class KalphaIntegrand extends BesselSectionedAlgorithm.BesselSectionIntegrand
{
	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Double eval (Double t)
	{ return Math.exp ( - x * Math.cosh (t) ) * Math.cosh (a * t); }
	KalphaIntegrand (double a) { super (a); }
}

