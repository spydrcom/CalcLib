
package net.myorb.math.expressions.algorithms;

import net.myorb.math.*;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.*;

/**
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class SymbolTableManager<T> extends AlgorithmCore<T> implements SymbolTableManagerI<T>
{


	/**
	 * construct object based on type manager.
	 *  also included are function that can be derived from type management primitives
	 * @param environment access to the evaluation environment
	 */
	public SymbolTableManager (Environment<T> environment)
	{
		super (environment);
	}


	/*
	 * algorithm imports
	 */


	public void addArithmeticAlgorithms (SymbolMap into)
	{
		ArithmeticPrimitives<T> arithmeticPrimitives = new ArithmeticPrimitives<T> (environment);
		into.add (arithmeticPrimitives.getAdditionAlgorithm (ADDITION_OPERATOR, SymbolMap.ADDITION_PRECEDENCE), "Arithmetic addition operator");
		into.add (arithmeticPrimitives.getSubtractionAlgorithm (SUBTRACTION_OPERATOR, SymbolMap.ADDITION_PRECEDENCE), "Arithmetic subtraction operator");
		into.add (arithmeticPrimitives.getMultiplicationAlgorithm (MULTIPLICATION_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE), "Arithmetic multiplication operator");
		into.add (arithmeticPrimitives.getDivisionAlgorithm (DIVISION_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE), "Arithmetic division operator, displayed as over");
		into.add (arithmeticPrimitives.getDivisionAlgorithm (FRACTION_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE), "Arithmetic division operator");
	}


	public void addBooleanAlgorithms (SymbolMap into)
	{
		//originally BuiltInFunctions.importFromSpaceManager (into);
		BooleanPrimitives<T> booleanPrimitives = new BooleanPrimitives<T> (environment);

		into.add (booleanPrimitives.getFalseValue (FALSE_SYMBOL), "Symbol for logical FALSE, translated to 0");
		into.add (booleanPrimitives.getTrueValue (TRUE_SYMBOL), "Symbol for logical TRUE, translated to 1");

		into.add (booleanPrimitives.getLtAlgorithm (LT_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Less than");
		into.add (booleanPrimitives.getGtAlgorithm (GT_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Greater than");
		into.add (booleanPrimitives.getLeAlgorithm (LE_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Less than or equal to");
		into.add (booleanPrimitives.getGeAlgorithm (GE_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Greater than or equal to");
		into.add (booleanPrimitives.getLtAbsAlgorithm (LT_ABS_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Less than absolute value of");
		into.add (booleanPrimitives.getGtAbsAlgorithm (GT_ABS_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Greater than absolute value of");
		into.add (booleanPrimitives.getNeAlgorithm (NE_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Not equal to");
		into.add (booleanPrimitives.getEqAlgorithm (EQ_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Equal to");

		into.add (booleanPrimitives.getNotAlgorithm (NOT_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Logical NOT");
		into.add (booleanPrimitives.getAndAlgorithm (AND_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Logical AND");
		into.add (booleanPrimitives.getNandAlgorithm (NAND_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Logical NAND");
		into.add (booleanPrimitives.getOrAlgorithm (OR_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Logical OR");
		into.add (booleanPrimitives.getNorAlgorithm (NOR_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Logical NOR");
		into.add (booleanPrimitives.getXorAlgorithm (XOR_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Logical XOR");
		into.add (booleanPrimitives.getNxorAlgorithm (NXOR_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Logical NOT XOR");
		into.add (booleanPrimitives.getImpliesAlgorithm (IMPLIES_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Logical IMPLIES");
		into.add (booleanPrimitives.getNimpliesAlgorithm (NOT_IMPLIES_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Logical NOT IMPLIES");
		into.add (booleanPrimitives.getNimpliedByAlgorithm (NOT_IMPLIED_BY_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Logical NOT IMPLIED BY");
		into.add (booleanPrimitives.getImpliedByAlgorithm (IMPLIED_BY_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Logical IMPLIED BY");

		into.add (booleanPrimitives.getConditionCodeAlgorithm (SET_CONDITION_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE), "Logical condition code set");
		into.add (booleanPrimitives.getChooseAlgorithm (CHOOSE_OPERATOR, SymbolMap.ADDITION_PRECEDENCE), "Choice based on condition code");
	}


	public void addAlgebraicAlgorithms (SymbolMap into)
	{
		AlgebraicPrimitives<T> algebraicPrimitives = new AlgebraicPrimitives<T> (environment);
		into.add (algebraicPrimitives.getPlusMinusAlgorithm (PLUS_OR_MINUS_OPERATOR, SymbolMap.ADDITION_PRECEDENCE), "Plus or Minus operator");
		into.add (algebraicPrimitives.getMinusPlusAlgorithm (MINUS_OR_PLUS_OPERATOR, SymbolMap.ADDITION_PRECEDENCE), "Minus or Plus operator");
		into.add (algebraicPrimitives.getAbsAlgorithm (ABSOLUTE_VALUE_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE), "Absolute value function of parameter");
		into.add (algebraicPrimitives.getSgnAlgorithm (SIGN_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE), "Sign (SGN) function of parameter");
		into.add (algebraicPrimitives.getPiValue (PI_SYMBOL), "Symbol for the irrational value of pi");
		into.add (algebraicPrimitives.getEValue (E_SYMBOL), "Symbol for the irrational value of e");
	}


	public void addCalculusAlgorithms (SymbolMap into)
	{
		CalculusPrimitives<T> calculusPrimitives = new CalculusPrimitives<T> (environment);
		into.add (calculusPrimitives.getDeltaAlgorithm (DELTA_INCREMENT_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE),
					"Derivative approximation evaluated at left parameter using delta value in right parameter");
		into.add (calculusPrimitives.getPrimeAlgorithm (PRIME_OPERATOR, SymbolMap.CALCULUS_PRECEDENCE), "Mark function call for first derivative approximation");
		into.add (calculusPrimitives.getDPrimeAlgorithm (DPRIME_OPERATOR, SymbolMap.CALCULUS_PRECEDENCE), "Mark function call for second derivative approximation");
		into.add (calculusPrimitives.getDCTQuadAlgorithm (DCTQUAD_OPERATOR, SymbolMap.CALCULUS_PRECEDENCE), "Mark function call for Clenshaw-Curtis integral approximation");
		into.add (calculusPrimitives.getTSQuadAlgorithm (TSQUAD_OPERATOR, SymbolMap.CALCULUS_PRECEDENCE), "Mark function call for Tanh-Sinh integral approximation");
		into.add (calculusPrimitives.getIntervalAlgorithm (INTERVAL_EVAL_OPERATOR, SymbolMap.CALCULUS_PRECEDENCE), "Mark function call for interval evaluation");
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.BuiltInArrayFunctions#importFromSpaceManager(net.myorb.math.expressions.SymbolMap)
	 */
	public void importFromSpaceManager (SymbolMap into)
	{
		addArithmeticAlgorithms (into);
		addAlgebraicAlgorithms (into);
		addCalculusAlgorithms (into);
		addBooleanAlgorithms (into);

		//super.importFromSpaceManager (into); // refactored

		importArrayLibrary (into);
		importPrimeLibrary (into);
		importStatLibrary (into);
	}


	public void importArrayLibrary (SymbolMap into)
	{
		//BuiltInArrayFunctions.importFromSpaceManager (into);
		BuiltInArrayFunctions<T> bif = new BuiltInArrayFunctions<T> (environment);
		bif.importFromPowerLibrary (environment.getLibrary (), into);
		bif.importFromSpaceManager (into);
	}

	public void importPrimeLibrary (SymbolMap into)
	{
		//this.importPrimeFunctions (into);
	}

	public void importStatLibrary (SymbolMap into)
	{
		//this.importStatFunctions (into);
	}

	public void importPolynomialLibrary (SymbolMap into)
	{
		//importFromPolynomialLibrary (abstractions.getPolynomialLibrary (), into);
	}

	public void importMatrixLibrary (SymbolMap into)
	{
		//importMatrixFunctions (abstractions.getMatrixLibrary (), into);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.SymbolTableManagerI#importFromPowerLibrary(net.myorb.math.expressions.SymbolMap)
	 */
	public void importFromPowerLibrary (SymbolMap into)
	{

		//super.importFromPowerLibrary (into); // refactored
		importPolynomialLibrary (into);
		importMatrixLibrary (into);

		ComboPrimitives<T> powerPrimitives = new ComboPrimitives<T> (environment);
		into.add (powerPrimitives.getDshAlgorithm (DECIMAL_SHIFT_OPERATOR), "Scientific notation");
		into.add (powerPrimitives.getLshAlgorithm (LEFT_SHIFT_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE), "Left shift operator");
		into.add (powerPrimitives.getRshAlgorithm (RIGHT_SHIFT_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE), "Right shift operator");
		into.add (powerPrimitives.getSqrtAlgorithm (SQRT_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE), "Unary conventional SQRT function");
		into.add (powerPrimitives.getFactorialAlgorithm (FACTORIAL_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE), "Unary conventional factorial operator");
		into.add (powerPrimitives.getRemAlgorithm (REMAINDER_OPERATOR, SymbolMap.MULTIPLICATION_PRECEDENCE), "Binary conventional remainder operator n%m, integer only");
		into.add (powerPrimitives.getExponentiationAlgorithm (EXPONENTIATION_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Binary conventional exponentiation operator x^y");
		into.add (powerPrimitives.getRadicalAlgorithm (RADICAL_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Binary conventional root operator n\\x, intended for small integer roots");
		into.add (powerPrimitives.getRootAlgorithm (ROOT_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Conventional root operator a *\\ b, a * SQRT(b), read as a RADICAL b");
		into.add (powerPrimitives.getLogAlgorithm (LOG_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE), "Unary conventional natural logarithm function");
		into.add (powerPrimitives.getExpAlgorithm (EXP_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE), "Unary conventional EXP function e^x");
		into.add (powerPrimitives.getHypotAlgorithm (HYPOT_FUNCTION), "SQRT of sum of squares of array elements");
		importFromCombinatorics (powerPrimitives, into);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.BuiltInArrayFunctions#importFromPowerLibrary(net.myorb.math.PowerLibrary, net.myorb.math.expressions.SymbolMap)
	 */
	public void importFromPowerLibrary (final PowerLibrary<T> powerLibrary, SymbolMap into)
	{
		// deprecated
	}


	public void importFromCombinatorics (ComboPrimitives<T> comboPrimitives, SymbolMap into)
	{
		into.add (comboPrimitives.getFrisAlgorithm (FACTORIAL_RISING_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Factorial rising operator");
		into.add (comboPrimitives.getFfalAlgorithm (FACTORIAL_FALLING_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Factorial falling operator");
		into.add (comboPrimitives.getBernoulliAlgorithm (BERNOULLI_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE), "Bernoulli function B(m) for second (n=1) Bernoulli numbers");
		into.add (comboPrimitives.getBCAlgorithm (BINOMIAL_OPERATOR, SymbolMap.EXPONENTIATION_PRECEDENCE), "Binomial coefficient operator (n ## k)");
		into.add (comboPrimitives.getHarmonicAlgorithm (HARMONIC_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE), "Harmonic function H(x)");
		into.add (comboPrimitives.getLogGammaAlgorithm (LOGGAMMA_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE), "LogGamma function");
		into.add (comboPrimitives.getGammaAlgorithm (GAMMA_OPERATOR, SymbolMap.FUNCTTION_PRECEDENCE), "Gamma function");
		into.add (comboPrimitives.getZetaAlgorithm (ZETA_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE), "Zeta function");
	}


	/**
	 * use optimized library when type is Double float
	 * @param speedLibrary a library object optimized for speed and precision
	 * @param into the symbol map object collecting imported symbols
	 */
	public void importFromSpeedLibrary (HighSpeedMathLibrary speedLibrary, SymbolMap into)
	{
		TrigHSPrimitives<T> trigPrimitives = new TrigHSPrimitives<T> (environment, null, speedLibrary);
		into.add (trigPrimitives.getAsinAlgorithm (ASIN_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE), "Trigonometric ARC SIN function");
		into.add (trigPrimitives.getAtanAlgorithm (ATAN_FUNCTION), "Trigonometric ARC TAN function");
	}


	/**
	 * provide access to trig function using identity equations
	 * @param trigLibrary the implementer of TrigIdentities provides plethora of functions
	 * @param into the symbol map object collecting imported symbols
	 */
	public void importFromTrigLibrary (final TrigIdentities<T> trigLibrary, SymbolMap into)
	{
		TrigPrimitives<T> trigPrimitives = new TrigPrimitives<T> (environment, trigLibrary);
		into.add (trigPrimitives.getSinAlgorithm (SIN_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE), "Trigonometric SIN function");
		into.add (trigPrimitives.getCosAlgorithm (COS_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE), "Trigonometric COS function");
		into.add (trigPrimitives.getTanAlgorithm (TAN_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE), "Trigonometric TAN function");
		into.add (trigPrimitives.getAsinAlgorithm (ASIN_FUNCTION, SymbolMap.FUNCTTION_PRECEDENCE), "Trigonometric ARC SIN function");
		into.add (trigPrimitives.getAtanAlgorithm (ATAN_FUNCTION), "Trigonometric ARC TAN function");
	}


}

