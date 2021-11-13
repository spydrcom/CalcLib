
package net.myorb.math.polynomial.families.gegenbauer;

import net.myorb.math.polynomial.GeneralRecurrence;
import net.myorb.math.polynomial.PolynomialSpaceManager;

import net.myorb.math.polynomial.families.jacobi.JacobiRecurrenceFormula;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.specialfunctions.Library;

/**
 * Gegenbauer recurrence implemented using Jacobi translation
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class GegenbauerJacobiRecurrenceFormula<T>
	extends JacobiRecurrenceFormula<T>
{

	public GegenbauerJacobiRecurrenceFormula
	(PolynomialSpaceManager<T> psm) { super (psm); }

	static double gamma (double parameter) { return Library.gamma (parameter); }

	/**
	 * @param lambda the Gegenbauer lambda is used to set Jacobi a,b
	 */
	public void setLambda (T lambda)
	{
		T TWO = discrete (2), HALF = inverseOf (TWO);
		T lambdaMinusHalf = sumOf (lambda, negOf (HALF));
		setParameters (lambdaMinusHalf,  lambdaMinusHalf);		// a = lambda - 1/2, b = lambda - 1/2
		setGamma (lambda);
	}

	/**
	 * @param lambda convert to double, gamma is computed in float
	 */
	public void setGamma (T lambda)
	{ setGamma ((esm = Library.getExpressionManager (psm)).convertToDouble (lambda)); }
	protected ExpressionSpaceManager<T> esm;

	/**
	 * @param lambda the translation formula uses lambda + 1/2, and 2 * lambda
	 */
	public void setGamma (double lambda)
	{
		double num = gamma (lambdaPlusHalf = lambda + 0.5);
		double den = gamma (twoLambda = lambda * 2);
		this.constantGammaRatio = num / den;
	}
	protected double lambdaPlusHalf, twoLambda;
	protected double constantGammaRatio;

	/**
	 * @param n the family index being translated
	 * @return the gamma multiplier for the P term n
	 */
	public T gammaFactor (int n)
	{
		double num = gamma (n + twoLambda), den = gamma (n + lambdaPlusHalf);
		return esm.convertFromDouble (constantGammaRatio * num / den);
	}

	/**
	 * @param P the Jacobi function list for the a,b computed from lambda
	 * @return the Gegenbauer list converted from Jacobi
	 */
	public GeneralRecurrence<T> translate (GeneralRecurrence<T> P)
	{
		GeneralRecurrence<T> C = new JacobiRecurrenceFormula<T>(psm);
		// C[lambda,n] = [ GAMMA (lambda+1/2) / GAMMA (2*lambda) ] * [GAMMA (n+2*lambda) / GAMMA (n+lambda+1/2)] * P[lambda-1/2,lambda-1/2,n]
		for (int n = 0; n < P.size (); n++) C.add (times (gammaFactor (n), P.get (n)));
		return C;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.GeneralRecurrence#constructFuntions(int)
	 */
	public GeneralRecurrence<T> constructFuntions (int upTo)
	{
		GeneralRecurrence<T> P = super.constructFuntions (upTo);
		GeneralRecurrence<T> C = translate (P);
		return C;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomials.JacobiRecurrenceFormula#seedRecurrence()
	 */
	public void seedRecurrence () {}

	private static final long serialVersionUID = 3573235208777680818L;
}

