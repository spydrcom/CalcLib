
package net.myorb.math.expressions.managers;

import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.charting.DisplayGraphTypes;
import net.myorb.data.abstractions.ValueDisplayProperties;

import net.myorb.math.expressions.*;

import java.awt.Color;
import java.util.List;

/**
 * extend field manager to include value parser for expression analysis
 * @author Michael Druckman
 */
public class ExpressionFloatingFieldManager extends DoubleFloatingFieldManager
	implements ExpressionComponentSpaceManager<Double> 
{

	public void setEvaluationControl
	(EvaluationControlI<Double> control) { this.control = control; }
	public EvaluationControlI<Double> getEvaluationControl () { return control; }
	protected EvaluationControlI<Double> control;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#evaluate(java.lang.String)
	 */
	public Double evaluate (String expression)
	{
		if (control == null)
			return parseValueToken (TokenParser.TokenType.NUM, expression);
		else return control.evaluate (expression);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#parseValueToken(net.myorb.math.expressions.Parser.TokenType, java.lang.String)
	 */
	public Double parseValueToken (TokenParser.TokenType type, String image)
	{
		if (type == TokenParser.TokenType.RDX)
		{
			int radix;
			if (image.startsWith ("0b")) radix = 2;
			else if (image.startsWith ("0o")) radix = 8;
			else if (image.startsWith ("0x")) radix = 16;
			else throw new RuntimeException ("Illegal radix in numeric literal");
			return newScalar (Integer.parseInt (image.substring (2), radix));
		}
		if (type == TokenParser.TokenType.INT)
		{
			int iValue = Integer.parseInt (image);
			return newScalar (iValue);
		}
		return Double.parseDouble (image);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#providesSupportFor(java.lang.String)
	 */
	public boolean providesSupportFor (String requiredType)
	{
		return EvaluationEngine.supports (requiredType, ValueCharacterization.REAL);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#convertFromDouble(java.lang.Double)
	 */
	public Double convertFromDouble (Double value)
	{
		return value;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#convertToDouble(java.lang.Object)
	 */
	public Double convertToDouble (Double value)
	{
		return value;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#characterize(java.lang.Object)
	 */
	public ValueCharacterization characterize (Double value)
	{
		int intValue = value.intValue ();
		if (value.doubleValue () == intValue) return ValueCharacterization.INTEGER;
		return ValueCharacterization.REAL;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#convertToInteger(java.lang.Object)
	 */
	public Integer convertToInteger (Double value)
	{
		ValueCharacterization characterization = characterize (value);
		if (characterization != ValueCharacterization.INTEGER) return null;
		return Integer.valueOf (value.intValue ());
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.realnumbers.DoubleFloatingFieldManager#toNumber(java.lang.Double)
	 */
	public Number toNumber (Double x)
	{
		Integer i = convertToInteger (x);
		return i == null ? x : i;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#getDataConversions()
	 */
	public DataConversions<Double> getDataConversions ()
	{
		return new DataConversions<Double> (this);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#convertToStructure(java.util.List, java.util.List, java.util.List)
	 */
	public void convertToStructure (List<Double> sequence, List<Double> x, List<Double> y)
	{ throw new RuntimeException ("No structure in this data"); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ValueManager.Formatter#format(java.lang.Object)
	 */
	public String format (Double value)
	{
		Integer integerRepresentation;
		if ((integerRepresentation = convertToInteger (value)) != null) return integerRepresentation.toString ();
		return ValueDisplayProperties.formatDecimalString (value, displayPrecision);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#formatByMode(java.lang.Object, java.lang.String)
	 */
	public String formatToMode (Double value, String mode) { return format (value); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionComponentSpaceManager#component(java.lang.Object, int)
	 */
	public double component (Double value, int componentNumber) { return value; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionComponentSpaceManager#construct(double[])
	 */
	public Double construct (double... components) { return components[0]; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionComponentSpaceManager#magnitude(java.lang.Object)
	 */
	public double magnitude (Double value) { return value; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionSpaceManager#getformattingModes()
	 */
	public String[] getformattingModes () { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionComponentSpaceManager#getComponentCount()
	 */
	public int getComponentCount () { return 1; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionComponentSpaceManager#componentIdentifiers()
	 */
	public String[] componentIdentifiers () { return LABEL; }
	static final String[] LABEL = new String[]{"Y"};

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.ExpressionComponentSpaceManager#assignColors(net.myorb.charting.DisplayGraphTypes.Colors)
	 */
	public void assignColors (DisplayGraphTypes.Colors colors)
	{
		colors.add (Color.BLACK);
	}

}
