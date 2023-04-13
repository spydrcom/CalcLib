
package net.myorb.math.expressions;

// CalcLib Polynomial math
import net.myorb.math.Polynomial;
import net.myorb.math.polynomial.algebra.SeriesExpansion;

// CalcLib Matrix math
import net.myorb.math.matrices.MatrixOperations;
import net.myorb.math.matrices.Matrix;

// CalcLib expressions
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.rendering.MathML;
import net.myorb.math.expressions.commands.Rendering;

// IOlib abstractions
import net.myorb.data.abstractions.Function;

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


	/**
	 * get the polynomial function for a coefficient array
	 * @param poly a coefficient array
	 * @return the power function
	 */
	public Polynomial.PowerFunction<T> getFunction (Polynomial.Coefficients<T> poly)
	{
		return new Polynomial<T> (spaceManager).getPolynomialFunction (poly);
	}


	/**
	 * get a coefficient array given the symbol name
	 * @param arrayName name of a coefficient array
	 * @return the coefficient array
	 */
	public Polynomial.Coefficients<T> getCoefficients (String arrayName)
	{
		Polynomial.Coefficients<T> poly = new Polynomial.Coefficients<T> ();
		poly.addAll (arrayForName (arrayName));
		return poly;
	}


	/**
	 * get the polynomial function for a coefficient array
	 * @param arrayName name of a coefficient array
	 * @return the power function
	 */
	public Polynomial.PowerFunction<T> getFunction (String arrayName)
	{
		return getFunction ( getCoefficients (arrayName) );
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


	/**
	 * convert token stream to MML
	 * @param tokens the sequence to render using MML
	 * @param parameterNames a list of the names of the parameters of function profiles
	 * @return the rendered text formatted as MML
	 * @throws Exception for any errors
	 */
	public String render
		(
			List<TokenParser.TokenDescriptor> tokens,
			List<String> parameterNames
		)
	throws Exception
	{
		SymbolMap s =
			getContextSpecificSymbolMap (parameterNames);
		return new MathML (s).render (tokens);
	}


	/**
	 * rendering implementation for series expansion
	 * @param functionName the name of the function being expanded
	 * @param renderer the Rendering engine to use
	 */
	public void renderExpandedSeries (String functionName, Rendering <T> renderer)
	{
		try { renderExpandedSeries (functionName, renderer, new SeriesExpansion <T> (environment)); }
		catch (Exception e) { throw new RuntimeException ( "Render failed", e ); }
	}
	public void renderExpandedSeries
		(String functionName, Rendering <T> renderer, SeriesExpansion <T> processor)
	throws Exception
	{
//		System.out.println (processor.expandSequence ( functionName ));

		renderer.render
		(
			processor.expandSequence ( functionName ),
			processor.parameterList (), "Expanded series from " + functionName
		);
	}


	/**
	 * this is a bug fix (8/20/2022)
	 *  - for rendering function parameters
	 * @param parameterNames the list of names from a function profile,
	 * 			this is NULL if the object being rendered is not a function.
	 * @return a copy of the symbol map with the named parameters removed
	 */
	SymbolMap getContextSpecificSymbolMap (List<String> parameterNames)
	{
		SymbolMap s = getSymbolMap ();
		if (parameterNames == null) return s;
		SymbolMap context = new SymbolMap (); context.putAll (s);
		return getReducedSymbolMap (context, parameterNames);
	}


	/**
	 * removing the parameter names from the symbol table,
	 *  - this eliminates confusion for parameters that have
	 *  - names matching functions already posted to the symbol map
	 * @param context a copy of the system symbol table to be modified
	 * @param parameterNames the list of names from a function profile
	 * @return a copy of the symbol map with the named parameters removed
	 */
	SymbolMap getReducedSymbolMap
	(SymbolMap context, List<String> parameterNames)
	{
		for (String p : parameterNames)
		{
			context.remove (p);
		}
		return context;
	}


}

