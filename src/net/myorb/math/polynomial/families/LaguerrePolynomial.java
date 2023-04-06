
package net.myorb.math.polynomial.families;

import net.myorb.math.polynomial.InitialConditions;
import net.myorb.math.polynomial.InitialConditionsProcessor;

import net.myorb.math.polynomial.NamingConventions;
import net.myorb.math.polynomial.PolynomialFamilyManager;
import net.myorb.math.polynomial.PolynomialFamily;

import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.polynomial.GeneralRecurrence;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.specialfunctions.Gamma;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

/**
 * support for Laguerre polynomial based algorithms
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class LaguerrePolynomial <T> extends Polynomial <T>
			implements PolynomialFamily <T>, NamingConventions,
				InitialConditionsProcessor.Calculator
{

	public LaguerrePolynomial
	(SpaceManager <T> manager) { super (manager); init (); }
	public LaguerrePolynomial () { super (null); }

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#init(net.myorb.math.SpaceManager)
	 */
	public void init (SpaceManager <T> manager)	{ this.manager = manager; init (); }
	public void init () { this.psm = new LaguerrePolynomialSpaceManager <T> (manager); }
	protected PolynomialSpaceManager<T> psm;

	/**
	 * compute Initial Conditions for solution
	 * @param degree the degree of the solution polynomial
	 * @param alpha the value of alpha for the solution
	 * @return the Initial Conditions object
	 */
	public InitialConditions <T> getInitialConditions (int degree, double alpha)
	{
		return new LaguerreInitialConditions <T> (degree, alpha, (ExpressionSpaceManager <T>) manager);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.InitialConditionsProcessor.Calculator#computeCoefficients(net.myorb.math.polynomial.InitialConditionsProcessor.SymbolTranslator)
	 */
	public void computeCoefficients (InitialConditionsProcessor.SymbolTranslator coefficientManager)
	{
		Double  N = coefficientManager.valueFor ("n"),
			alpha = coefficientManager.valueFor ("alpha") ;
		LaguerreInitialConditions <T> LIC = (LaguerreInitialConditions <T>)
			getInitialConditions (N.intValue (), alpha);
		LIC.setCoefficients (coefficientManager);
	}

	/**
	 * use function inter-dependencies to generate series L
	 * @param upTo highest order of functions to be generated
	 * @return the list of generated functions
	 */
	public PolynomialFamilyManager.PowerFunctionList<T> recurrence (int upTo)
	{
		return new LaguerreRecurrenceFormula<T> (psm).constructFuntions (upTo);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getName()
	 */
	public String getName () { return "Laguerre"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getFunctions(java.lang.String, int)
	 */
	public PolynomialFamilyManager.PowerFunctionList<T> getPolynomialFunctions (String identifier, int upTo)
	{
		return recurrence (upTo);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.PolynomialFamily#getIdentifier(java.lang.String)
	 */
	public String getIdentifier (String kind) { return "L"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.NamingConventions#getPolynomialNameConvention()
	 */
	public String getPolynomialNameConvention () { return "L"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.NamingConventions#getCoefficientNameConvention()
	 */
	public String getCoefficientNameConvention () { return "l"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.NamingConventions#getParameterNameConvention()
	 */
	public String getParameterNameConvention () { return "x"; }

	/**
	 * unit test
	 * @param args not used
	 */
	public static void main (String[] args)
	{
		Polynomial.RealNumbers mgr = new Polynomial.RealNumbers ();
		LaguerrePolynomial<Double> L = new LaguerrePolynomial<Double> (mgr);
		PolynomialFamilyManager.dump (L.recurrence (10), mgr);
	}

}

/**
 * Recurrence Formula specific to Laguerre
 * @param <T> data type description
 */
class LaguerreRecurrenceFormula <T> extends GeneralRecurrence <T>
{
	// (n+1) * L[n+1](x) = ( (2n+1)*L[n](x) - x*L[n](x) - nL[n-1](x) )

	public LaguerreRecurrenceFormula (PolynomialSpaceManager<T> psm)
	{
		super (psm);
	}
	public void seedRecurrence ()
	{
		add (ONE); add (add (ONE, neg (variable)));
	}
	public Polynomial.PowerFunction<T> functionOfN (int n)
	{
		return add (con (2*n + 1), neg (variable));
	}
	public Polynomial.PowerFunction<T> functionOfNminus1 (int n)
	{
		return con (-n);
	}
	public Polynomial.PowerFunction<T> functionOfNplus1 (int n)
	{
		return con (n + 1);
	}
	private static final long serialVersionUID = 1L;
}

/**
 * Polynomial Space Manager specific to Laguerre
 * @param <T> data type description
 */
class LaguerrePolynomialSpaceManager <T> extends PolynomialSpaceManager <T>
{

	/**
	 * @param manager data type manager is required
	 */
	public LaguerrePolynomialSpaceManager
		(SpaceManager<T> manager)
	{
		super (manager);
	}

//	/* (non-Javadoc)
//	 * @see net.myorb.math.PolynomialSpaceManager#formatTerm(int, java.lang.Object, java.lang.StringBuffer)
//	 */
//	public void formatTerm (int termNo, T c, StringBuffer buffer)
//	{
//		// the constant for the term is displayed for c ~= 1
//		if (!formatTermOperation (c, termNo, buffer)) buffer.append (" * ");
//
//		// then the Laguerre L function of (x) replaces the traditional x^n
//		buffer.append ("L[").append (termNo).append ("](x)");
//	}

}

/**
 * algorithms for computation of Initial Conditions for polynomial solutions
 * @param <T> data type description
 */
class LaguerreInitialConditions <T> implements InitialConditions <T>
{

	LaguerreInitialConditions (int degree, double alpha, ExpressionSpaceManager<T> manager)
	{
		this.initGamma (); this.initAlpha (alpha); this.initDegree (degree);
		this.derivative = manager.convertFromDouble ( -  gammaNplusA1 / ( gammaN * GammaAPlus2 ) );
		this.valueAtZero = gammaNplusA1 / ( degree * gammaN * GammaAPlus1 );
		this.constant = manager.convertFromDouble ( this.valueAtZero );
	}
	protected T constant, derivative;
	protected Double valueAtZero;

	/**
	 * get access to Gamma function
	 */
	protected void initGamma () { this.gamma = new Gamma (); }
	protected double GAMMA (double x) { return gamma.eval (x); }
	protected Gamma gamma;

	/**
	 * compute GAMMA (alpha + c)
	 * @param alpha real value for alpha
	 */
	protected void initAlpha (double alpha)
	{
		this.alphaPlus1 = alpha + 1;
		this.GammaAPlus1 = GAMMA (alphaPlus1);
		this.GammaAPlus2 = alphaPlus1 * GammaAPlus1;
	}
	protected double alphaPlus1, GammaAPlus1, GammaAPlus2;

	/**
	 * compute GAMMA (N + c)
	 * @param degree the degree of the solution polynomial
	 */
	protected void initDegree (double degree)
	{
		this.gammaNplusA1 =
			GAMMA ( degree + alphaPlus1 );
		this.gammaN = GAMMA ( degree );
	}
	protected double gammaNplusA1, gammaN;

	/**
	 * @param coefficientManager set initial conditions for polynomial solution
	 */
	public void setCoefficients (InitialConditionsProcessor.SymbolTranslator coefficientManager)
	{
		coefficientManager.set ("l_0", valueAtZero);
		// setting l_1 causes solution to degenerate even when computed correctly and set here
		// coefficientManager.set ("l_1", manager.convertToDouble (getFirstDerivativeTerm ()));
		// results of solution are correct without setting this ???
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.InitialConditions#getFirstDerivativeTerm()
	 */
	public T getFirstDerivativeTerm () { return derivative; }

	/* (non-Javadoc)
	 * @see net.myorb.math.polynomial.InitialConditions#getConstantTerm()
	 */
	public T getConstantTerm () { return constant; }

}
