
package net.myorb.math.computational;

import net.myorb.math.expressions.*;

import net.myorb.data.abstractions.Function;
import net.myorb.math.*;

import java.util.List;

/**
 * calculus operations on Fourier series
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
public class FourierSeriesCalculus<T> extends Fourier implements TransformCalculus<T>
{


	/**
	 * control translation of T to double values
	 * @param manager an expression manager with T/double conversions
	 */
	public FourierSeriesCalculus
	(ExpressionSpaceManager<T> manager) { this.manager = manager; }
	ExpressionSpaceManager<T> manager;


	/**
	 * copy lists of values into coefficient objects
	 * @param values the list of values to add to coefficient object
	 * @return the coefficient object, null for empty
	 */
	SeriesCoefficients copyCoefficients (List<T> values)
	{
		if (values == null) return null;
		SeriesCoefficients c = new SeriesCoefficients ();
		for (T t : values) c.add (manager.convertToDouble (t));
		return c;
	}


	/**
	 * scale coefficients of new transform
	 * @param byValue the value to multiply into coefficients
	 * @param c the coefficients from the source of the transform
	 * @param inverted TRUE => invert the scalar for integration
	 * @return the scaled coefficients
	 */
	SeriesCoefficients scale (double byValue, SeriesCoefficients c, boolean inverted)
	{
		if (c == null) return null;
		SeriesCoefficients scaled = new SeriesCoefficients ();
		scaled.addAll (c);
		double coef;
		
		for (int i=0; i<scaled.size(); i++)
		{
			coef = scaled.get (i);
			if (coef == 0.0) continue;
			double m = inverted? 1.0/(i+1): (i+1);
			scaled.set (i, coef * byValue * m);
		}
		return scaled;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.TransformCalculus#getFunctionDerivative(net.myorb.math.Function)
	 */
	public Function<T> getFunctionDerivative (Function<T> function)
	{
		Transform
		t = (Transform)function;
		double omega = t.getOmega ();
		SeriesCoefficients cos = scale (omega, t.getSinCoefficients (), false);			// sin' = cos
		SeriesCoefficients sin = scale (-omega, t.getCosCoefficients (), false);		// cos' = -sin
		Series s = buildMixedPhaseSeries (omega, cos, sin);
		return new TransformedSeries<T>(s, manager);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.TransformCalculus#getFunctionIntegral(net.myorb.math.Function)
	 */
	public Function<T> getFunctionIntegral (Function<T> function)
	{
		Transform
		t = (Transform)function;
		double omega = t.getOmega ();
		SeriesCoefficients cos = scale (-1.0/omega, t.getSinCoefficients (), true);		// sin' = cos
		SeriesCoefficients sin = scale (1.0/omega, t.getCosCoefficients (), true);		// cos' = -sin
		Series s = buildMixedPhaseSeries (omega, cos, sin);
		return new TransformedSeries<T>(s, manager);
	}


	/**
	 * build function for a harmonic series
	 * @param omega the period multiplier for the cyclic function terms
	 * @param cos the coefficients of cos for the series
	 * @param sin the coefficients of sin for the series
	 * @return a function that evaluates the series
	 */
	public Function<T> newFunctionInstance (T omega, List<T> cos, List<T> sin)
	{
		Series s = buildMixedPhaseSeries
			(manager.convertToDouble (omega), copyCoefficients (cos), copyCoefficients (sin));
		return new TransformedSeries<T>(s, manager);
	}


}


/**
 * wrapper for Fourier series object exposing function of T type
 * @param <T> type of component values on which operations are to be executed
 * @author Michael Druckman
 */
class TransformedSeries<T> implements Fourier.Transform, Function<T>, TransformCalculus<T>
{

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#f(java.lang.Object)
	 */
	public T eval (T x)
	{
		double parameter = sm.convertToDouble (x);
		double result = t.eval (parameter);
		return sm.convertFromDouble (result);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<T> getSpaceDescription () { return sm; }
	public SpaceManager<T> getSpaceManager () { return sm; }
	ExpressionSpaceManager<T> sm;

	/* (non-Javadoc)
	 * @see net.myorb.math.TransformCalculus#getFunctionDerivative(net.myorb.math.Function)
	 */
	public Function<T> getFunctionDerivative (Function<T> function)
	{
		return new FourierSeriesCalculus<T> (sm).getFunctionDerivative (function);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.TransformCalculus#getFunctionIntegral(net.myorb.math.Function)
	 */
	public Function<T> getFunctionIntegral (Function<T> function)
	{
		return new FourierSeriesCalculus<T> (sm).getFunctionIntegral (function);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Fourier.Transform#getCosCoefficients()
	 */
	public Fourier.SeriesCoefficients getCosCoefficients () { return t.getCosCoefficients (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Fourier.Transform#getSinCoefficients()
	 */
	public Fourier.SeriesCoefficients getSinCoefficients () { return t.getSinCoefficients (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Fourier.Transform#getFourier()
	 */
	public Fourier getFourier () { return t.getFourier (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Fourier.Transform#getOmega()
	 */
	public Double getOmega () { return t.getOmega (); }

	/**
	 * construct wrapper
	 * @param t the series to be wrapped
	 * @param manager the type manager for T/double conversions
	 */
	TransformedSeries (Fourier.Series t, ExpressionSpaceManager<T> manager)
	{ this.t = t; sm = manager; }
	Fourier.Series t;

}

