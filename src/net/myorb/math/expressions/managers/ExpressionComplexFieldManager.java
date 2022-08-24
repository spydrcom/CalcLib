
package net.myorb.math.expressions.managers;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.complexnumbers.ComplexPrimitives;

import net.myorb.math.complexnumbers.ComplexFieldManager;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.EvaluationControlI;
import net.myorb.math.expressions.EvaluationEngine;
import net.myorb.math.expressions.DataConversions;
import net.myorb.math.expressions.TokenParser;

import net.myorb.charting.DisplayGraphTypes;

import java.awt.Color;

/**
 * expression manager for complex domains
 * @author Michael Druckman
 */
public class ExpressionComplexFieldManager extends ComplexFieldManager<Double>
	implements ExpressionComponentSpaceManager<ComplexValue<Double>>
{

	public ExpressionComplexFieldManager ()
	{
		super (new ExpressionFloatingFieldManager ());
	}

	/**
	 * establish means of expression evaluation
	 * @param control the evaluation control engine
	 */
	public void setEvaluationControl
	(EvaluationControlI<ComplexValue<Double>> control) { evaluationControl = control; }
	public EvaluationControlI<ComplexValue<Double>> getEvaluationControl () { return evaluationControl; }
	protected EvaluationControlI<ComplexValue<Double>> evaluationControl;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#parseValueToken(net.myorb.math.expressions.TokenParser.TokenType, java.lang.String)
	 */
	public ComplexValue<Double>
		parseValueToken (TokenParser.TokenType type, String image)
	{
		double value;
		if (type == TokenParser.TokenType.RDX)
		{
			int radix;
			if (image.startsWith ("0b")) radix = 2;
			else if (image.startsWith ("0o")) radix = 8;
			else if (image.startsWith ("0x")) radix = 16;
			else throw new RuntimeException ("Illegal radix in numeric literal");
			value = Integer.parseInt (image.substring (2), radix);
		} else value = Double.parseDouble (image);
		return C (value, 0d);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#evaluate(java.lang.String)
	 */
	public ComplexValue<Double> evaluate (String expression)
	{
		if (evaluationControl == null)
			return parseValueToken (TokenParser.TokenType.NUM, expression);
		else return evaluationControl.evaluate (expression);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#providesSupportFor(java.lang.String)
	 */
	public boolean providesSupportFor (String requiredType)
	{
		return EvaluationEngine.supports (requiredType, ValueCharacterization.COMPLEX);
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.SpaceConversion#convertFromDouble(java.lang.Double)
	 */
	public ComplexValue<Double> convertFromDouble (Double value)
	{ return new ComplexValue<Double> (value, manager); }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.SpaceConversion#convertToDouble(java.lang.Object)
	 */
	public Double convertToDouble (ComplexValue<Double> value)
	{
		if (value.Im() == 0) return value.Re();
		return value.modulus ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#characterize(java.lang.Object)
	 */
	public ValueCharacterization characterize (ComplexValue<Double> value)
	{
		if (ComplexPrimitives.isImaginary (value)) return ValueCharacterization.COMPLEX;
		Double realpart = value.Re (); int intRealpart = realpart.intValue ();
		if (realpart == intRealpart) return ValueCharacterization.INTEGER;
		return ValueCharacterization.REAL;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#convertToInteger(java.lang.Object)
	 */
	public Integer convertToInteger (ComplexValue<Double> value)
	{
		ValueCharacterization characterization = characterize (value);
		if (characterization != ValueCharacterization.INTEGER) return null;
		return Integer.valueOf (value.Re ().intValue ());
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.ComplexFieldManager#toNumber(net.myorb.math.complexnumbers.ComplexValue)
	 */
	public Number toNumber (ComplexValue<Double> x)
	{
		if (ComplexPrimitives.isImaginary (x))
			return super.toNumber (x);
		return x.Re ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#getDataConversions()
	 */
	public DataConversions<ComplexValue<Double>> getDataConversions ()
	{
		return new DataConversions<ComplexValue<Double>> (this);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueManager.Formatter#format(java.lang.Object)
	 */
	public String format (ComplexValue<Double> value)
	{
		return value.toString ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#formatByMode(java.lang.Object, java.lang.String)
	 */
	public String formatToMode (ComplexValue<Double> value, String mode) { return format (value); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#getformattingModes()
	 */
	public String[] getformattingModes () { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionComponentSpaceManager#component(java.lang.Object, int)
	 */
	public double component (ComplexValue<Double> value, int componentNumber)
	{
		switch (componentNumber)
		{
			case 0: return value.Re ();
			case 1: return value.Im ();

			default:
				throw new RuntimeException
					(
						"Invalid component number for complex value"
					);
		}
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionComponentSpaceManager#getComponentCount()
	 */
	public int getComponentCount ()
	{
		return 2;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionComponentSpaceManager#magnitude(java.lang.Object)
	 */
	public double magnitude (ComplexValue<Double> value)
	{
		return value.magnitude ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionComponentSpaceManager#construct(double[])
	 */
	public ComplexValue<Double> construct (double... components)
	{
		double real = components[0], imaginary = 0.0;
		if (components.length < 1 || components.length > 2)
		{ throw new RuntimeException (CONSTRUCTION_ERROR); }
		if (components.length > 1) { imaginary = components[1]; }
		return new ComplexValue<Double> (real, imaginary, this.manager);
	}
	static String CONSTRUCTION_ERROR = "Complex values can be constructed from one or two values only";

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionComponentSpaceManager#axisLabels()
	 */
	public String[] axisLabels () { return LABELS; }
	static final String[] LABELS = new String[]{"Re", "Im"};

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionComponentSpaceManager#assignColors(net.myorb.charting.DisplayGraphTypes.Colors)
	 */
	public void assignColors (DisplayGraphTypes.Colors colors)
	{
		colors.add (Color.BLUE); colors.add (Color.RED);
	}

}

