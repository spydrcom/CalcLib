
package net.myorb.math.polynomial.families.legendre;

import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.polynomial.InitialConditionsProcessor;
import net.myorb.math.polynomial.InitialConditions;

import net.myorb.math.computational.Combinatorics;

/**
 * algorithms for computation of Initial Conditions for polynomial solutions
 * @param <T> data type description
 * @author Michael Druckman
 */
public class LegendreInitialConditions <T>
	implements InitialConditions <T>, InitialConditionsProcessor.Calculator
{


	public LegendreInitialConditions
		(int degree, double mu, ExpressionSpaceManager<T> manager)
	{
		this.C = new Combinatorics <> (this.manager = manager, null);
		if (mu != 0) throw new RuntimeException ("Conditions only available for mu=0");
		if ( degree % 2 == 1 )  oddFunction  ( degree );  else  evenFunction ( degree );
	}
	protected ExpressionSpaceManager<T> manager;
	protected Combinatorics <T> C;


	// implementation of coefficient calculation formula

	/**
	 * computation of coefficients
	 * @param degree the degree of the polynomial
	 * @param term the index of the term, 0 for constant, 1 for derivative
	 * @return the computed coefficient
	 */
	double formula (int degree, int term)
	{
		double N = degree, sum = N + term - 1, nk2 = BC ( sum/2, N );
		double nk = manager.convertToDouble ( C.binomialCoefficient (degree, term) );
		return nk * nk2 * Math.pow ( 2, degree );
	}

	/**
	 * generalized Binomial Coefficient
	 * - for real domain using Falling Factorial
	 * @param alpha support for a real number given caller using division by 2
	 * @param k an integer degree value treated as double
	 * @return the computed value
	 */
	double BC (double alpha, double k)
	{
		T	Tk = manager.convertFromDouble ( k ),
			Ta = manager.convertFromDouble ( alpha ),
			Fk = C.factorial ( Tk ), FkI = manager.invert ( Fk );
		return manager.convertToDouble
			(
				manager.multiply ( C.fallingFactorial ( Ta, Tk ), FkI )
			);
	}

	/**
	 * treatment for even function
	 * @param degree the degree of the polynomial
	 */
	void evenFunction (int degree)  { this.setComputedValues ( formula (degree, 0), 0.0 ); }

	/**
	 * treatment for odd function
	 * @param degree the degree of the polynomial
	 */
	void oddFunction (int degree) { this.setComputedValues ( 0.0, formula (degree, 1) ); }

	/**
	 * capture computed values
	 * @param valueAtZero function value at 0
	 * @param derivativeValueAtZero function first derivative at 0
	 */
	public void setComputedValues (Double valueAtZero, Double derivativeValueAtZero)
	{
		this.derivative =
			manager.convertFromDouble (this.derivativeValueAtZero = derivativeValueAtZero);
		this.constant = manager.convertFromDouble (this.valueAtZero = valueAtZero);
	}
	protected Double valueAtZero, derivativeValueAtZero;


	// implementation of InitialConditionsProcessor.Calculator interface 

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.InitialConditionsProcessor.Calculator#computeCoefficients(net.myorb.math.polynomial.InitialConditionsProcessor.SymbolTranslator)
	 */
	public void computeCoefficients
		(InitialConditionsProcessor.SymbolTranslator coefficientManager)
	{
		coefficientManager.set ("p_1", this.derivativeValueAtZero);
		coefficientManager.set ("p_0", this.valueAtZero);
	}


	// implementation of InitialConditions interface

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.InitialConditions#getFirstDerivativeTerm()
	 */
	public T getFirstDerivativeTerm () { return this.derivative; }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.InitialConditions#getConstantTerm()
	 */
	public T getConstantTerm () { return this.constant; }

	protected T constant, derivative;


}

