
package net.myorb.math.expressions.gui.rendering.implementations;

// CalcLib
import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.math.expressions.PrettyPrinter;

// JmTeX
import be.ugent.caagt.jmathtex.mathml.MathMLParser;
import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXFormula;
import be.ugent.caagt.jmathtex.TeXIcon;

// IOLIB abstractions
import net.myorb.data.abstractions.SimplePropertiesManager;

// JRE
import java.io.StringReader;

/**
 * linkage layer to the CalcLib Pretty-Printer for MathML render requests.
 *   alternate version supports parameterization of display properties.
 * @author Michael Druckman
 */
public class JmtexRenderLib extends SimpleScreenIO
	implements PrettyPrinter.MathMarkupParameterizedRendering
{


	/**
	 * names of properties available for configuration
	 */
	public enum Properties
	{
		STYLE,		// the styles identified in TeXConstants
		POINTSIZE	// the point-size of the font being rendered
	}


	/**
	 * the styles identified in TeXConstants
	 */
	public enum Styles
	{

		TEXT (TeXConstants.STYLE_TEXT),
		SCRIPT (TeXConstants.STYLE_SCRIPT),
		DISPLAY (TeXConstants.STYLE_DISPLAY);

		Styles (int value) { styleValue = value; }
		public int getStyle () { return styleValue; }
		int styleValue;

	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.PrettyPrinter.MathMarkupRendering#render(java.lang.String)
	 */
	public Widget render (String mathMl) throws Exception
	{
		TeXFormula formula = MathMLParser.parse (new StringReader (mathMl), false);
		TeXIcon icon = formula.createTeXIcon (styleProperty, pointSizeProperty.floatValue ());
		return new Image (icon);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.PrettyPrinter.MathMarkupParameterizedRendering#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty (String named, Object to)
	{
		switch (Properties.valueOf (named.toUpperCase ()))
		{
			case STYLE: setStyle (to.toString ()); break;
			case POINTSIZE: setSize (to); break;
		}
	}


	/**
	 * set Style property of expression rendering engine
	 * @param to the name of the style
	 */
	public void setStyle (String to)
	{
		try { styleProperty = Styles.valueOf (to.toString ().toUpperCase ()).getStyle (); }
		catch (Exception e) { throw new RuntimeException (STYLE, e); }
	}
	static final String STYLE = "Unable to set Style property of expression rendering engine";


	/**
	 * set Point Size property of expression rendering engine
	 * @param to the value to use for Point Size
	 */
	public void setSize (Object to)
	{
		try { pointSizeProperty = (Number) to; }
		catch (Exception e) { throw new RuntimeException (SIZE, e); }
	}
	static final String SIZE = "Unable to set Point Size property of expression rendering engine";


	int styleProperty = TeXConstants.STYLE_DISPLAY;
	Number pointSizeProperty = 20;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.PrettyPrinter.MathMarkupRendering#configureWith(net.myorb.data.abstractions.SimplePropertiesManager.PropertiesMap)
	 */
	@Override
	public void configureWith (SimplePropertiesManager.PropertiesMap properties)
	{
		SimplePropertiesManager.PropertyValueList list;
		if ((list = properties.get ("Configuration")) == null) return;
		setStyle (list.get (0).getTokenImage ());
		setSize (list.get (1).getTokenValue ());
	}


}

