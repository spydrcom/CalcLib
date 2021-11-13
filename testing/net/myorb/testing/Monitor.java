
package net.myorb.testing;

import net.myorb.math.SpaceManager;
import net.myorb.math.fractions.Fraction;
import net.myorb.math.fractions.FractionFieldManager;
import net.myorb.math.realnumbers.IntegerSpaceManager;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.complexnumbers.ComplexFieldManager;
import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.primenumbers.FactorizationFieldManager;
import net.myorb.math.primenumbers.Factorization;

import java.math.BigInteger;

/**
 * 
 * formatter for trace output. provides a cenral funnel for test output allowing single location to change traces
 * 
 * @author Michael Druckman
 *
 */
public class Monitor
{

	/**
	 * most tests need this object, so a static copy is available here within this package
	 */
	static IntegerSpaceManager integerFieldManager = new IntegerSpaceManager ();

	/**
	 * 
	 * a generic version of the trace formatter
	 * 
	 * @author Michael Druckman
	 *
	 * @param <T> the type of data to be displayed
	 */
	public static class Generic<T>
	{

		/**
		 * create the object with options set
		 * @param manager the type manager for the type being displayed
		 * @param string TRUE if the value is to be displayed using simple toString
		 * @param internal TRUE if internal version is to be displayed
		 * @param decimal TRUE if decimal version is to be displayed
		 */
		public Generic
		(SpaceManager<T> manager, boolean string, boolean internal, boolean decimal)
		{ this.manager = manager; this.string = string; this.decimal = decimal; this.internal = internal; }
		protected boolean string = false, decimal = false, internal = false;
		protected SpaceManager<T> manager = null;

		/**
		 * central location to request activity trace display.
		 *  this version provides a display with no error value report
		 * @param captions the text of the caption for the trace
		 * @param value the value to be displayed
		 */
		public void activity (String captions, T value)
		{
			activityWithError (captions, value, null, null);
		}

		/**
		 * show activity report with the computed error
		 * @param captions the text of the caption for the trace
		 * @param value the value to be displayed as the computed result
		 * @param errorCaption a text caption for the error display
		 * @param error the value of the error
		 */
		public void activityWithError (String captions, T value, String errorCaption, Double error)
		{
			System.out.println (captions);
			if (string) System.out.println ("   toString> " + value);
			
//			if (internal)
//			{
//				String display = manager.toInternalString (value);
//				System.out.println ("   toInternalString> " + display);
//			}

			if (decimal)
			{
				String display = manager.toDecimalString (value);
				String formatted = "    toDecimalString> " + display;
				if (error != null) System.out.print (formatted);
				else System.out.println (formatted);
			}

			if (error != null)
			{ System.out.println (errorCaption + abs (error)); }
			System.out.println ("===");
			System.out.println ();
		}
		Double abs (Double x) { return x<0? -x: x; }

		/**
		 * activity report with simple error included
		 * @param captions the text of the caption for the trace
		 * @param value the value to be displayed as the computed result
		 * @param expected the value expected for the computed result
		 */
		public void activity (String captions, T value, Double expected)
		{
			Double error = manager.toNumber (value).doubleValue () - expected;
			activityWithError (captions, value, "  computation error = ", error);
		}

		/**
		 * display error as error^2 to
		 *  eliminate SQRT from expected value
		 * @param captions the text of the caption for the trace
		 * @param value the value to be displayed as the computed result
		 * @param expected the square of the computed value expected
		 */
		public void activityErrorSquared (String captions, T value, Double expected)
		{
			T squared = manager.multiply (value, value);
			Double error = manager.toNumber (squared).doubleValue () - expected;
			activityWithError (captions, value, "  computation error ^2 = ", error);
		}
	}

	/**
	 * an integer version of the Monitor object
	 * @author Michael Druckman
	 */
	public static class Integer extends Generic<BigInteger>
	{
		Integer () { super (integerFieldManager, true, false, false); }
	}

	/**
	 * a floating version of the Monitor object
	 * @author Michael Druckman
	 */
	public static class Float extends Generic<Double>
	{
		Float () { super (new DoubleFloatingFieldManager (), true, false, false); }
	}

	/**
	 * a complex version of the Monitor object
	 * @author Michael Druckman
	 */
	@SuppressWarnings("rawtypes")
	public static class Imaginary extends Generic<ComplexValue<Double>>
	{
		Imaginary () { super (ComplexFieldManager.newInstance (), true, false, false); }
		SpaceManager getComponentManager () { return manager.getComponentManager();}
	}

	/**
	 * an integer fraction version of the Monitor object
	 * @author Michael Druckman
	 */
	@SuppressWarnings("rawtypes")
	public static class IntegerFraction extends Generic<Fraction<BigInteger>>
	{
		IntegerFraction () { super (new FractionFieldManager<BigInteger>(integerFieldManager), true, false, true); }
		SpaceManager getComponentManager () { return manager.getComponentManager();}
	}

	/**
	 * a floating fraction version of the Monitor object
	 * @author Michael Druckman
	 */
	@SuppressWarnings("rawtypes")
	public static class FloatFraction extends Generic<Fraction<Double>>
	{
		FloatFraction () { super (new FractionFieldManager<Double>(new DoubleFloatingFieldManager ()), true, false, true); }
		SpaceManager getComponentManager () { return manager.getComponentManager();}
	}

	/**
	 * a monitor for values based on Factorization
	 * @author Michael Druckman
	 */
	public static class Factored extends Generic<Factorization>
	{
		Factored () { super (new FactorizationFieldManager (), true, true, true); }
	}

}
