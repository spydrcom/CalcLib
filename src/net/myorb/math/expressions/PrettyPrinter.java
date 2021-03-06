
package net.myorb.math.expressions;

// CalcLib imports
import net.myorb.math.Polynomial;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.MmlDisplayFormatter;
import net.myorb.math.expressions.symbols.DefinedTransform;

// IOlib imports
import net.myorb.data.abstractions.SimplePropertiesManager;
import net.myorb.data.abstractions.SimpleUtilities;

import net.myorb.gui.components.RenderingDisplay;
import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.SimpleMenu;

// JRE imports
import java.util.ArrayList;
import java.util.List;

/**
 * display processing for data values
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class PrettyPrinter<T> extends RenderingDisplay
{


	/**
	 * interface to the equation rendering library
	 */
	public interface MathMarkupRendering
	{
		/**
		 * render markup text for display
		 * @param mathMl the text of the markup language for the expression
		 * @return a swing component holding the render of the expression
		 * @throws Exception for any errors
		 */
		Widget render (String mathMl) throws Exception;

		/**
		 * @param properties the properties to apply
		 */
		void configureWith (SimplePropertiesManager.PropertiesMap properties);
	}


	/**
	 * extended interface allows setting of properties
	 */
	public interface MathMarkupParameterizedRendering
	extends MathMarkupRendering, SimpleUtilities.PropertySetting
	{}


	/**
	 * PrettyPrinter that will
	 *  use RenderingDisplay features
	 * @param title a title for the frame
	 * @param width the width for the initial display
	 * @param height the height for the initial display
	 * @param environment the environment properties collection object
	 */
	public PrettyPrinter (String title, int width, int height, Environment<T> environment)
	{ super (title, width, height); allocateFormatter (environment); setGrid (0, 1); }

//	this is the original column display constructor, variable row count and fixed column count
//	public PrettyPrinter (int columns, Environment<T> environment)
//	{ setEnvironment (environment); setGrid (0, columns); }

	public PrettyPrinter (Environment<T> environment) { allocateFormatter (environment); }


	/**
	 * @param environment the environment properties collection object
	 */
	public void allocateFormatter (Environment<T> environment)
	{
		if (environment == null) return;
		this.formatter = new PrettyFormatter<T>(environment);
	}
	protected PrettyFormatter<T> formatter;


	/**
	 * establish grid for panel layout
	 * @param rows count of rows in grid, 0 = unlimited
	 * @param cols count of columns in grid
	 */
	public void setGrid (int rows, int cols)
	{
		p.setLayout (new Grid (rows, cols));
	}


	/**
	 * pretty print a symbol value
	 * @param name the name of the symbol
	 * @param precision the digit level to display
	 */
	public void formatSymbol (String name, String precision)
	{
		SymbolMap.Named sym = formatter.getSymbolMap ().lookup (name);
		DefinedTransform<T> definedTransform = DefinedTransform.checkForTransform (sym);
		if (definedTransform == null) formatter.display (sym, name, precision);
		else formatter.formatSymbol (definedTransform.getTransform ());
	}


	/**
	 * pretty print a polynomial from an array value
	 * @param name the name of the array holding coefficients
	 */
	public void formatPolynomial (String name)
	{
		Polynomial.Coefficients<T> poly = new Polynomial.Coefficients<T> ();
		poly.addAll (formatter.arrayForName (name));
		formatter.formatPolynomial (name, poly);
	}


	/**
	 * add a formatted text line to render display
	 * @param tokens the tokens from the command line
	 * @throws Exception for any errors
	 */
	public void display (List<TokenParser.TokenDescriptor> tokens) throws Exception
	{
		String comment = tokens.get (0).getTokenImage ();
		Label label = new Label (comment.substring (1, comment.length () - 1), Label.CENTER);
		for (int i = 1; i < tokens.size (); i++) apply (tokens.get (i).getTokenImage (), label);
		addWidget (label);
	}
	static void apply (String option, Label label)
	{
		switch (option.charAt (0))
		{
			case 'T': label.setVerticalAlignment (Label.TOP); return;
			case 'C': label.setVerticalAlignment (Label.CENTER); return;
			case 'B': label.setVerticalAlignment (Label.BOTTOM); return;
	
			case 'o': label.setForeground (SimpleScreenIO.Colour.ORANGE); return;
			case 'g': label.setForeground (SimpleScreenIO.Colour.GRAY); return;
			case 'b': label.setForeground (SimpleScreenIO.Colour.BLUE); return;
			case 'r': label.setForeground (SimpleScreenIO.Colour.RED); return;
	
			default: label.setForeground (SimpleScreenIO.Colour.getColor (option));
		}
	}


	/**
	 * add rendered MML to display frame
	 * @param mathMl the text of the mark-up for the expression
	 * @param ttText tool tip text for this section of the display
	 * @throws Exception for any errors
	 */
	public void display (String mathMl, String ttText) throws Exception
	{
		Widget widget;
		if ((widget = getMathMarkupRenderer ().render (mathMl)) != null)
		{
			addWidget (widget);
			if (ttText != null) SimpleScreenIO.setToolTipText (widget, ttText);
			new MmlPrintMenu (mathMl, widget).addAsPopupToWidget (widget);
		}
	}
	static MathMarkupRendering renderer = null;
	public static MathMarkupRendering getMathMarkupRenderer ()
	{ return renderer != null? modified (renderer): new MmlDisplayFormatter (); }
	public static void setMathMarkupRenderer (MathMarkupRendering markupRenderer)
	{ renderer = markupRenderer; }

	/**
	 * @param renderer the rendering implementation
	 * @return the renderer with properties applied
	 */
	public static MathMarkupRendering modified (MathMarkupRendering renderer)
	{
		SimplePropertiesManager.PropertiesMap
			properties = SimplePropertiesManager.pget ("RENDER");
		if (properties != null) renderer.configureWith (properties);
		return renderer;
	}

	/**
	 * add an entry to the display of rendered expressions
	 * @param tokens the token list that comprises the expression to be rendered
	 * @param ttText tool tip text for this section of the display
	 * @throws Exception for any errors
	 */
	public void render (List<TokenParser.TokenDescriptor> tokens, String ttText) throws Exception
	{
		if (renderer == null) throw new RuntimeException ("Expression renderer is not configured");
		if (tokens.get (0).getTokenType() == TokenParser.TokenType.QOT) display (tokens);
		else display (toMML (tokens), ttText);
	}

	/**
	 * construct MML description from token stream
	 * @param tokens the token list that comprises the expression to be rendered
	 * @return the MML text equivalent for the token stream specified
	 * @throws Exception for any errors
	 */
	public String toMML
	(List<TokenParser.TokenDescriptor> tokens) throws Exception
	{ return formatter.render (tokens); }

	/**
	 * @param commands the document covering commands
	 * @param operators the document covering operators
	 */
	public static void showHelp
	(RenderingDisplay commands, RenderingDisplay operators)
	{
		TabbedPanel tabs = new TabbedPanel ();
		tabs.addWidget ("Commands", commands.getRenderingPanel ());
		tabs.addWidget ("Operators", operators.getRenderingPanel ());
		new WidgetFrame (tabs, "CalcLib Help").showOrHide (1200, 500);
	}


}


/**
 * pop-up menu for MML render labels
 */
class MmlPrintMenu extends SimpleMenu<Runnable>
	implements SimpleMenu.SelectionSetting<Runnable>
{
	MmlPrintMenu (String mathMl, Object widget)
	{
		super (new MmlPrintMenuItems (mathMl).getItemList ());
		this.setConsumer (this); this.widget = widget;
	}
	protected Object widget; public Object getAssociatedComponent () { return widget; }
	public void acceptSelection (Runnable selected) throws Exception { selected.run (); }
	public String getTitleForErrorDialog () { return "Unable to print"; }
	private static final long serialVersionUID = 8182517370848406352L;
}

class MmlPrintMenuItems
{
	List<Runnable> getItemList ()
	{
		List<Runnable> list = new ArrayList<Runnable>();
		list.add (new MmlPrintToIExplore ());
		list.add (new MmlPrintToSysOut ());
		list.add (new MmlPrintToFile ());
		return list;
	}
	class MmlPrintToSysOut implements Runnable
	{
		public void run() { System.out.println (mathMl); }
		public String toString () { return "Print MMl To SysOut"; }
	}
	class MmlPrintToIExplore implements Runnable
	{
		public void run()
		{ try { MmlDisplayFormatter.renderAsXml (mathMl); } catch (Exception e) {} }
		public String toString () { return "Show MMl As XML"; }
	}
	class MmlPrintToFile implements Runnable
	{
		//TODO: finish implementation
		public void run() { System.out.println (mathMl); }
		public String toString () { return "Print MMl To File"; }
	}
	MmlPrintMenuItems (String mathMl) { this.mathMl = mathMl; }
	String mathMl;
}

