
package net.myorb.math.expressions;

// CalcLib math
import net.myorb.data.abstractions.Function;
import net.myorb.math.Polynomial;

// CalcLib Matrix
import net.myorb.math.matrices.Matrix;
import net.myorb.math.matrices.MatrixOperations;

// CalcLib expressions
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.MathML;

// JRE imports
import java.io.PrintStream;
import java.util.List;

/**
 * pretty printer for renders done with formatted text
 * @param <T> type of menu items
 * @author Michael Druckman
 */
public class PrettyFormatter<T>
{


	public PrettyFormatter (Environment<T> environment)
	{
		setEnvironment (environment);
	}


	/**
	 * @param environment the environment properties collection object
	 */
	public void setEnvironment (Environment<T> environment)
	{
		this.valueManager = (this.environment = environment).getValueManager ();
		this.spaceManager = environment.getSpaceManager ();
		this.out = environment.getOutStream ();
	}
	protected ExpressionSpaceManager<T> spaceManager;
	protected ValueManager<T> valueManager;
	protected Environment<T> environment;
	protected PrintStream out;


	/**
	 * @return symbols held in environment
	 */
	public SymbolMap getSymbolMap () { return environment.getSymbolMap (); }


	/**
	 * @param value the value to be formatted
	 */
	public void showValue (ValueManager.GenericValue value) 
	{
		ValueManager.setFormatter (value, spaceManager);
		out.println (value);
	}


	/**
	 * @param value the value to display
	 * @param radix the radix for the display
	 */
	public void showWithRadix (ValueManager.GenericValue value, int radix) 
	{
		int result = valueManager.toInt (value, spaceManager);
		String prefix = radix==2? "b": radix==8? "o": radix==16? "x": null;
		if (prefix == null) throw new RuntimeException ("Illegal Radix");
		String digits = Integer.toString (result, radix).toUpperCase ();
		out.println ("0" + prefix + digits);
	}


	/**
	 * display a generic value
	 * @param value a generically wrapped value object
	 * @param precision the digit level to display
	 */
	public void display (ValueManager.GenericValue value, String precision)
	{
		if (precision != null)
			spaceManager.setDisplayPrecision
					(Integer.parseInt (precision));
		if (valueManager.isMatrix (value))
		{
			header (value);
			Matrix<T> m = valueManager.toMatrix (value);
			new MatrixOperations<T> (spaceManager).show (out, m);
			out.println ();
		}
		else if (valueManager.isArray (value))
		{
			header (value);
			List<T> items = valueManager.toArray (value); out.println ("[");
			for (T v : items) show (v); out.println ("]");
			out.println ();
		}
		else if (valueManager.isDiscrete (value))
		{
			header (value);
			show (valueManager.toDiscrete (value));
			out.println ();
		}
		else if (valueManager.isUndefinedValue (value))
		{
			header (value);
			out.println ("*** Value is undefined *********************");
			out.println ();
		}
		else
		{
			header (value);
			out.println (value.toString ());
			out.println ();
		}
		spaceManager.resetDisplayPrecision ();
	}


	/**
	 * format the display of a value
	 * @param value a value of the data type being processed
	 */
	public void show (T value)
	{
		String decimal = spaceManager.format (value);
		out.print ("\t" + decimal);
		out.println ();
	}


	/**
	 * @param sym a function to be displayed
	 * @param name the name of a symbol to display
	 * @param precision the precision for the numeric display
	 */
	public void display (SymbolMap.Named sym, String name, String precision)
	{
		if (sym instanceof SymbolMap.ParameterizedFunction) out.println (sym.toString ());
		else display (environment.getValue (name), precision);
	}


	/**
	 * @param transform the symbol to display
	 */
	public void formatSymbol (Function<T> transform)
	{
		if (transform instanceof Polynomial.PowerFunction)
		{
			Polynomial.PowerFunction<T> poly = (Polynomial.PowerFunction<T>) transform;
			out.println (poly.getPolynomialSpaceManager ().toString (poly));
		}
		else throw new RuntimeException ("Unrecognized transform");
	}


	/**
	 * pretty print a polynomial from an array value
	 * @param name the name of the array holding coefficients
	 * @param poly the coefficient array of the polynomial
	 */
	public void formatPolynomial (String name, Polynomial.Coefficients<T> poly)
	{
		out.println ();
		out.println (name + " = ");
		out.println (); out.print ("\t");
		out.println (getFunction (poly).toString ());
		out.println ();
	}
	Polynomial.PowerFunction<T> getFunction (Polynomial.Coefficients<T> poly)
	{
		return new Polynomial<T> (spaceManager).getPolynomialFunction (poly);
	}


	/**
	 * @param name symbol name of an array value
	 * @return the raw value list
	 */
	public ValueManager.RawValueList<T> arrayForName (String name)
	{
		return valueManager.toArray (getSymbolMap ().getValue (name));
	}


	/**
	 * display the variable name
	 * @param value the wrapper for the value
	 */
	public void header (ValueManager.GenericValue value)
	{
		out.println ();
		out.println (value.getName () + " = ");
		out.println ();
	}


	/** convert token stream to MML
	 * @param tokens the sequence to render using MML
	 * @return the rendered text formatted as MML
	 * @throws Exception for any errors
	 */
	public String render
	(List<TokenParser.TokenDescriptor> tokens)
	throws Exception
	{
		return new MathML (getSymbolMap ()).render (tokens);
	}


}

