
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;
import net.myorb.math.computational.TanhSinhQuadratureAlgorithms;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

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
			this.parameterValue = sm.convertToDouble (a);
			this.parameter = integerOrderCheck (a, sm); this.sm = sm;
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
			this.Ina = ModifiedFirstKind.getI (sm.negate (parameter), termCount, lib, psm);
			this.Ia = ModifiedFirstKind.getI (parameter, termCount, lib, psm);
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


	/**
	 * special case for using integral
	 * @param parameter the alpha value order
	 * @param terms the count of terms for the series
	 * @param precision target value for approximation error
	 * @param psm the manager for the polynomial space
	 * @return the function description
	 */
	public <T> SpecialFunctionFamilyManager.FunctionDescription<T> getSpecialCase
		(T parameter, int terms, double precision, PolynomialSpaceManager<T> psm)
	{
		ExpressionSpaceManager<T> sm = getExpressionManager (psm);
		return getK (parameter, terms, precision, sm);
	}


	/**
	 * an alternative to the LIM [ a -> n ]...
	 *  the function evaluation algorithm from integral
	 * @param a the value of alpha which is integer/real
	 * @param termCount the count of terms for the series
	 * @param precision target value for approximation error
	 * @param sm the manager for the data type
	 * @return the function description
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getK (T a, int termCount, double precision, ExpressionSpaceManager<T> sm)
	{
		return new KaFunctionDescription<T> (a, termCount, precision, sm);
	}


}


/**
 * Function Description for special case of Ka implementation.
 *  domain of real numbers, integral form is: exp ( - x * cosh (t) ) * cosh (a * t)
 * @param <T> data type in use
 */
class KaFunctionDescription<T> implements SpecialFunctionFamilyManager.FunctionDescription<T>
{

	KaFunctionDescription (T a, int termCount, double precision, ExpressionSpaceManager<T> sm)
	{ this.K = new Ka (sm.convertToDouble (a), termCount, precision); this.sm = sm; this.a = a; }
	ExpressionSpaceManager<T> sm; Ka K; T a;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		return sm.convertFromDouble ( K.integral ( sm.convertToDouble (x) ) );
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
	 */
	public StringBuffer getFunctionDescription ()
	{
		return new StringBuffer ("Bessel: K(a=").append (a).append (")");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getRenderIdentifier()
	 */
	public String getRenderIdentifier () { return "K"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
	 */
	public String getFunctionName () { return "K_" + a; }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<T> getSpaceDescription () { return sm; }
	public SpaceManager<T> getSpaceManager () { return sm; }

}


/**
 * provide real version of Ka using integral algorithm.
 * TanhSinh Quadrature is used to privide numerical integration
 */
class Ka
{

	Ka (double a, int infinity, double prec)
	{
		this.a = a; this.infinity = infinity;
		this.targetAbsoluteError = prec;
	}
	double targetAbsoluteError;
	double a; int infinity;

	/**
	 * Integral form of Ka:
	 * exp ( - x * cosh (t) ) * cosh (a * t)
	 * @param x parameter to Ka function
	 * @return calculated result
	 */
	double integral (double x)
	{
		Function<Double> f = new KalphaIntegrand (x, a);
		return TanhSinhQuadratureAlgorithms.Integrate
		(f, 0, infinity, targetAbsoluteError, null);
	}

/*
	!! kap (x,a,t) = exp ( - x * cosh (t) ) * cosh (a * t)
	!! ka (x,a) = INTEGRAL [0 <= t <= INFINITY <> dt] (kap (x, a, t) * <*> t)
 */

}


/**
 * integral form of Ka
 */
class KalphaIntegrand implements Function<Double>
{

	KalphaIntegrand (double x, double a)
	{ this.a = a; this.x = x; }
	double x, a;

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public Double eval (Double t)
	{
		return Math.exp ( - x * Math.cosh (t) ) * Math.cosh (a * t);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<Double> getSpaceManager () { return sm; }
	ExpressionSpaceManager<Double> sm = new ExpressionFloatingFieldManager ();
	public SpaceManager<Double> getSpaceDescription () { return sm; }

}

