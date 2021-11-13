
package net.myorb.math.computational;

import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.polynomial.OrdinaryPolynomialCalculus;

import net.myorb.math.complexnumbers.*;

import java.util.ArrayList;
import java.util.List;

/**
 * process imaginary roots of polynomials
 * @author Michael Druckman
 */
public class PolynomialComplexFunctionCharacteristics
	extends PolynomialFunctionCharacteristics<ComplexValue<Double>>
{

	public static final HSComplexSupportImplementation hsml = new HSComplexSupportImplementation ();
	public static final OptimizedComplexLibrary ocl = new OptimizedComplexLibrary (hsml);

	public static final DoubleFloatingFieldManager mgr = new DoubleFloatingFieldManager ();
	public static final ComplexFieldManager<Double> cfm = new ComplexFieldManager<Double> (mgr);
	public static final OrdinaryPolynomialCalculus<ComplexValue<Double>> polyMgr =
		new OrdinaryPolynomialCalculus<ComplexValue<Double>> (cfm);

	public static final PolynomialRoots<ComplexValue<Double>>
		genericAnalyzer = new PolynomialRoots<ComplexValue<Double>> (cfm, ocl);
	public static final FunctionAnalyzer<ComplexValue<Double>>
		quarticAnalyzer = new QuarticEquation<Double> (cfm, ocl),
		cubicAnalyzer = new CubicEquation<Double> (cfm, ocl),
		quadraticAnalyzer = genericAnalyzer;

	/**
	 * for internal use, external access uses static entry point
	 */
	protected PolynomialComplexFunctionCharacteristics ()
	{
		super (cfm, ocl);
	}


	/**
	 * compute roots using algorithms based on order
	 * @param polynomial the polynomial function to be analyzed
	 * @return the list of computed zeros
	 */
	public static List<ComplexValue<Double>>
		getRoots (PowerFunction<ComplexValue<Double>> polynomial)
	{
		List<ComplexValue<Double>> roots = null;
		switch (polynomial.getDegree ())
		{
			case 4:  roots = quarticAnalyzer.analyze (polynomial); break;
			case 2:  roots = quadraticAnalyzer.analyze (polynomial); break;
			case 3:  roots = cubicAnalyzer.analyze (polynomial); break;
			default: roots = genericAnalyzer.evaluateEquation
				(polynomial.getCoefficients (), true, 0);
			break;
		}
		return roots;
	}


	/**
	 * find special domain points for a polynomial
	 * @param polynomial the polynomial function to be analyzed
	 * @param functions the functions of derivative functions processed in this evaluation
	 * @return the list of special point for this function 
	 */
	public static List<ComplexValue<Double>>
		getAllRoots (PowerFunction<ComplexValue<Double>> polynomial, List<PowerFunction<ComplexValue<Double>>> functions)
	{
		List<ComplexValue<Double>> roots = new ArrayList<ComplexValue<Double>> ();

		do
		{
			List<ComplexValue<Double>> newRoots = getRoots (polynomial);
			functions.add (polynomial); genericAnalyzer.addToRoots (newRoots, roots);
			polynomial = polyMgr.getFunctionDerivative (polynomial);
		} while (polynomial.getDegree () >= 1);
		return roots;
	}


	/**
	 * characterize a polynomial having complex roots
	 * @param polynomial the polynomial being analyzed
	 * @return list of attributes
	 */
	@SuppressWarnings("rawtypes")
	public static List<CharacteristicAttributes>
		characterize (PowerFunction<Double> polynomial)
	{
		List<CharacteristicAttributes> attributes =
			new ArrayList<CharacteristicAttributes>();
		switch (polynomial.getDegree ())
		{
			case 4:  process (polynomial, quarticAnalyzer, attributes); break;
			case 2:  process (polynomial, quadraticAnalyzer, attributes); break;
			case 3:  process (polynomial, cubicAnalyzer, attributes); break;
			default:
		}
		return attributes;
	}


	/** process the analysis of the polynomial
	 * @param polynomial the polynomial being analyzed
	 * @param analyzer the analysis processing object
	 * @param attributes the list being built
	 */
	public static void process
		(
			PowerFunction<Double> polynomial,
			FunctionAnalyzer<ComplexValue<Double>> analyzer,
			@SuppressWarnings("rawtypes") List<CharacteristicAttributes> attributes
		)
	{
		List<ComplexValue<Double>> iroots = new ArrayList<ComplexValue<Double>>();
		PowerFunction<ComplexValue<Double>> complexPolynomial = ComplexPrimitives.convertToComplex (polynomial);;
		List<ComplexValue<Double>> roots = analyzer.analyze (complexPolynomial);
		
		for (ComplexValue<Double> r : roots)
		{
			if (ComplexPrimitives.isImaginary (r)) iroots.add (r);
		}

		attributes.addAll
		(
			new PolynomialComplexFunctionCharacteristics ()
			.evaluateRoots (complexPolynomial, iroots)
		);
	}


}

