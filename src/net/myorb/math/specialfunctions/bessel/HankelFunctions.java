
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionList;
import net.myorb.math.specialfunctions.SpecialFunctionFamilyManager;

import net.myorb.data.abstractions.CommonCommandParser.TokenType;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.ExtendedPowerLibrary;
import net.myorb.math.SpaceManager;

/**
 * support for describing Bessel H (Hankel) functions.
 *  first and second kinds are conjugate pairs. both are returned in lists
 * @author Michael Druckman
 */
public class HankelFunctions extends UnderlyingOperators
{


	// H1#a(x) = J#a(x) + i * Y#a(x)
	// H2#a(x) = J#a(x) - i * Y#a(x)


	/**
	 * describe a Bessel function Ha where a is a real number
	 * @param a real number identifying the order of the Ha description
	 * @param kind the kind of Hankel function; first kind = 1, second kind = 2
	 * @param termCount the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @return a function description for Ha
	 * @param <T> data type manager
	 */
	public static <T> SpecialFunctionFamilyManager.FunctionDescription<T>
		getH (T a, int kind, int termCount, PolynomialSpaceManager<T> psm)
	{
		ExpressionSpaceManager<T> sm =
				(ExpressionSpaceManager<T>) psm.getSpaceDescription ();
		return new HaFunction<T>(a, kind, termCount, sm);
	}

	public static class HaFunction<T>
		implements SpecialFunctionFamilyManager.FunctionDescription<T>
	{
	
		HaFunction
			(
				T a, int kind, int termCount, ExpressionSpaceManager<T> sm
			)
		{
			PolynomialSpaceManager<T>
				psm = new PolynomialSpaceManager<T>(sm);
			this.Ja = OrdinaryFirstKind.getJ (a, termCount, psm);
			this.Ya = OrdinarySecondKind.getY (a, termCount, psm);
			processParameter (a, kind, sm);
		}
		protected SpecialFunctionFamilyManager.FunctionDescription<T> Ja, Ya;
	
		void processParameter (T a, int kind, ExpressionSpaceManager<T> sm)
		{
			this.plusOrMinusI = getI ();
			this.parameterValue = sm.convertToDouble (a);
			if (kind == 2) plusOrMinusI = sm.negate (plusOrMinusI);
			this.parameter = a;
			this.sm = sm;
		}
		protected T plusOrMinusI;
		protected Double parameterValue;
		protected T parameter;
		protected int kind;
	
		T getI ()
		{
			return sm.parseValueToken (TokenType.NUM, "(0 + i)");
		}

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public T eval (T x)
		{
			return sm.add
			(
				Ja.eval (x),
				sm.multiply (plusOrMinusI, Ya.eval (x))
			);
		}
	
		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionDescription()
		 */
		public StringBuffer getFunctionDescription ()
		{
			return new StringBuffer ("Bessel: H"+ kind + "(a=").append (parameterValue).append (")");
		}
	
		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getFunctionName()
		 */
		public String getFunctionName ()
		{
			return "H" + kind + formatParameterDisplay (parameterValue);
		}
	
		/* (non-Javadoc)
		 * @see net.myorb.math.specialfunctions.SpecialFunctionFamilyManager.FunctionDescription#getRenderIdentifier()
		 */
		public String getRenderIdentifier () { return "H"; }

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
		return getH (parameter, kind, terms, psm);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators#getFunctions(java.lang.String, int, net.myorb.math.polynomial.PolynomialSpaceManager)
	 */
	public <T> FunctionList<T> getFunctions
	(String order, int count, PolynomialSpaceManager<T> psm)
	{
		FunctionList<T> list = new FunctionList<T>();
		this.kind = 1; list.addAll (super.getFunctions (order, count, psm));		// first kind
		this.kind = 2; list.addAll (super.getFunctions (order, count, psm));		// second kind
		return list;																// list is conjugate pairs
	}
	int kind;


	/* (non-Javadoc)
	 * @see net.myorb.math.specialfunctions.bessel.UnderlyingOperators#getFunction(java.lang.Object, int, net.myorb.math.ExtendedPowerLibrary, net.myorb.math.polynomial.PolynomialSpaceManager)
	 */
	@Override
	public <T> FunctionDescription<T> getFunction
	(T parameter, int termCount, ExtendedPowerLibrary<T> lib, PolynomialSpaceManager<T> psm)
	{
		throw new RuntimeException ("Unimplemented");
	}


}

