
package net.myorb.math.expressions.charting.fractals;

import net.myorb.charting.DisplayGraphTypes.Point;

import net.myorb.math.expressions.charting.colormappings.ContourColorSchemeRequest;
import net.myorb.charting.ColorSelection;

import net.myorb.math.complexnumbers.ComplexValue;

import net.myorb.math.Polynomial;
import net.myorb.math.Function;

import java.util.HashSet;
import java.util.Set;

/**
 * display plots of the Newton fractals
 * @author Michael Druckman
 */
public class Newton extends Fractal implements Fractal.Descriptor
{


	/**
	 * TRUE implies use of OrdinaryPolynomialCalculus.
	 *  otherwise brute force calculation of iterations is done
	 *  (more efficient, less exercise of abstract frameworks)
	 */
	protected boolean usingCalculus = true;


	/**
	 * limits that indicate convergence / divergence
	 */
	static final float
		CONVERGENCE_LIMIT = 1.0E-8f, DIVERGENCE_LIMIT = 10.0f;
	static final int DEFAULT_ITERATION_LIMIT = 100;


	/**
	 * full view coordinates
	 */
	static final Point FULL_VIEW_LOW_CORNER = new Point (-5f, -5f);
	static final float FULL_VIEW_EDGE_SIZE = 10;


	/**
	 * view selected using low corner and edge size
	 * @param lowCorner the low corner is x-min/y-min
	 * @param edgeSize distance along each axis from low corner
	 * @param maxResult the largest value produced by the transform
	 */
	public Newton (Point lowCorner, float edgeSize, int maxResult) { super (lowCorner, edgeSize, maxResult); }
	public Newton (int maxIterations) { super (FULL_VIEW_LOW_CORNER, FULL_VIEW_EDGE_SIZE, maxIterations); }
	public Newton () { this (DEFAULT_ITERATION_LIMIT); }


	/**
	 * compute the iteration
	 *  count for specified point
	 * @param x0 the X coordinate of the point
	 * @param y0 the Y coordinate of the point
	 * @return the iteration count
	 */
	public int computeIterationsFor (double x0, double y0)
	{
		int iteration = 0;
		ComplexValue<Double> z = new ComplexConstant (x0, y0);

		while (iteration < getMaxResult ())
		{
			ComplexValue<Double> iterationOffset = computeIterationOffset (z);
			double iterationOffsetMagnitude = iterationOffset.modSquared ();
			if (iterationOffsetMagnitude < CONVERGENCE_LIMIT) break;

			ComplexValue<Double> zNplus1 = z.minus (iterationOffset);
			double zNplus1Magnitude = zNplus1.modSquared ();
			if (zNplus1Magnitude > DIVERGENCE_LIMIT) break;

			z = zNplus1; iteration++;
		}

		return iteration;
	}


	/**
	 * compute the offset term for an iteration.
	 *  algorithm is a*f(z)/f'(z), standard form f(z) = z^3 - 1
	 * @param z the location in the complex plane for the evaluation
	 * @return the computed offset
	 */
	protected ComplexValue<Double>
		computeIterationOffset (ComplexValue<Double> z)
	{ return a.times (iterationFunction.eval (z)); }
	protected ComplexFunction iterationFunction;
	protected ComplexConstant a;


	/**
	 * get collection of Newton fractals
	 * @return map of included fractals
	 */
	public static FractalMap getFractalMap ()
	{
		FractalMap map = new FractalMap ();
		map.addNamed (new ZcubeHalf ());
		map.addNamed (new Zcube2 ());
		map.addNamed (new Zcube ());
		map.addNamed (new Zsq ());
		return map;
	}


	/**
	 * unit test the algorithms
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		new ColorSchemeRequest ();
	}
	static class ColorSchemeRequest extends ContourColorSchemeRequest
	{
		public String formatNotificationFor
		(ColorSelection.Factory selectedItem) { return null; }
		public void setSelectedItem (ColorSelection.Factory item)
		{ super.setSelectedItem (item); new FractalMenu (); }
	}
	static class FractalMenu extends Selection
	{
		/* (non-Javadoc)
		 * @see net.myorb.gui.components.SimplePopupRequest#getOptions()
		 */
		public FractalList getOptions ()
		{
			FractalList choices = new FractalList ();
			choices.add (ZcubeHalf.favorite ());
			choices.add (Zcube2.favorite ());
			choices.add (new ZcubeHalf ());
			choices.add (new Zcube2 ());
			choices.add (new Zcube ());
			choices.add (new Zsq ());
			return choices;
		}
	}


	/*
	 * rebuild mechanism
	 */

	/**
	 * identifier for each kind of Newton fractal
	 */
	public enum NewtonFractals 
	{
		Zcubed, TwoZcubed, HalfZcubed, Zsquared
	}

	/**
	 * @param name the identity of a transform
	 * @return TRUE for match with Newton nickname
	 */
	public static boolean isMember (String name)
	{ return members.contains (name); }
	static Set<String> members;

	static
	{
		members = new HashSet<String>();
		for (NewtonFractals f : NewtonFractals.values ())
		{ members.add (f.name ()); }
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Fractal#reconstitute(java.lang.String)
	 */
	public Fractal reconstitute (String fractalName)
	{
		switch (NewtonFractals.valueOf (fractalName))
		{
			case Zcubed: return new Zcube ();
			case TwoZcubed: return new Zcube2 ();
			case HalfZcubed: return new ZcubeHalf ();
			case Zsquared: return new Zsq ();
			default:
		}
		throw new RuntimeException ("Unrecognized Fractal");
	}


	private static final long serialVersionUID = 7319718392199318098L;
}


/**
 * mechanisms for selecting the iteration function
 */
class NewtonFunctions extends Newton
{


	public NewtonFunctions () { super (); }
	public NewtonFunctions (int maxIterations)
	{ super (maxIterations); }


	/**
	 * @param a the constant multiplier coefficient
	 * @param f the function that computes a generation (iteration offset)
	 */
	protected void setFunction
	(ComplexConstant a, ComplexFunction f)
	{ this.a = a; this.iterationFunction = f; }


	/**
	 * OrdinaryPolynomialCalculus is used to construct complex polynomial functions
	 */
	class CalculusImplementation extends Fractal.ComplexFunction
	{

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public ComplexValue<Double> eval (ComplexValue<Double> z)
		{ return function.eval (z).divideBy (derivative.eval (z)); }

		/**
		 * use OrdinaryPolynomialCalculus to establish functions
		 * @param coefficients the polynomial coefficients
		 */
		protected void setFunction (int... coefficients)
		{
			function = polynomial.functionOfX (coefficients);
			derivative = calculus.getFunctionDerivative (function);
		}
		private Polynomial.PowerFunction<ComplexValue<Double>> function;
		private Function<ComplexValue<Double>> derivative;

		CalculusImplementation ()
		{
			polynomial =
				new Polynomial<ComplexValue<Double>> (complexMgr);
			calculus = new Calculus ();
		}
		private Polynomial<ComplexValue<Double>> polynomial;
		private Calculus calculus;

	}


	/**
	 * f(z) = z^3 - 1
	 */
	class ZcubedMinus1NonCalculus extends Fractal.ComplexFunction
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public ComplexValue<Double> eval (ComplexValue<Double> z)
		{ return z.toThe (3).minus (ONE).divideBy (z.squared ().times (THREE)); }
		private Fractal.ComplexConstant THREE = new Fractal.ComplexConstant (3);
	}
	class ZcubedMinus1UsingCalculus extends CalculusImplementation
	{ ZcubedMinus1UsingCalculus () { setFunction (-1, 0, 0, 1); } }


	/**
	 * f(z) = z^2 - 1
	 */
	class ZsquaredMinus1NonCalculus extends Fractal.ComplexFunction
	{
		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public ComplexValue<Double> eval (ComplexValue<Double> z)
		{ return z.squared ().minus (ONE).divideBy (z.times (TWO)); }
		private Fractal.ComplexConstant TWO = new Fractal.ComplexConstant (2);
	}
	class ZsquaredMinus1UsingCalculus extends CalculusImplementation
	{ ZsquaredMinus1UsingCalculus () { setFunction (-1, 0, 1); } }


	/**
	 * @return f(z) = z^2 - 1
	 */
	protected ComplexFunction zSquaredMinus1 ()
	{
		return usingCalculus?
			new ZsquaredMinus1UsingCalculus ():
			new ZsquaredMinus1NonCalculus ();
	}


	/**
	 * @return f(z) = z^3 - 1
	 */
	protected ComplexFunction zCubedMinus1 ()
	{
		return usingCalculus?
			new ZcubedMinus1UsingCalculus ():
			new ZcubedMinus1NonCalculus ();
	}


	private static final long serialVersionUID = 6915701117871847628L;
}


/*
 * Fractal classes identified by algorithm
 */


/**
 * a*f(z)/f'(z), standard form f(z) = z^3 - 1, a=1
 */
class Zcube extends NewtonFunctions
{

	public Zcube () { setFunction (null, zCubedMinus1 ()); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Newton#computeIterationOffset(net.myorb.math.complexnumbers.ComplexValue)
	 */
	protected ComplexValue<Double>
		computeIterationOffset (ComplexValue<Double> z)
	{ return iterationFunction.eval (z); }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Newton : P=z^3-1, a=1"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Fractal#getFractalName()
	 */
	public String getFractalName () { return fractalNameFor (NewtonFractals.Zcubed); }

	private static final long serialVersionUID = -1405019305179790443L;
}


/**
 * a*f(z)/f'(z), standard form f(z) = z^3 - 1, a=2
 */
class Zcube2 extends NewtonFunctions
{

	public Zcube2 () { setFunction (a, zCubedMinus1 ()); }
	private final ComplexConstant a = new ComplexConstant (2);

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Zcube#toString()
	 */
	public String toString () { return "Newton : P=z^3-1, a=2"; }

	/**
	 * @return area of interest in this fractal
	 */
	public static Newton favorite ()
	{
		return (Newton) new Zcube2 ().setViewArea (-0.044117629528045654, 0.17647060751914978, 0.7352941334247589);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Fractal#getFractalName()
	 */
	public String getFractalName () { return fractalNameFor (NewtonFractals.TwoZcubed); }

	private static final long serialVersionUID = 9054036100943502824L;
}


/**
 * a*f(z)/f'(z), standard form f(z) = z^3 - 1, a=0.5
 */
class ZcubeHalf extends NewtonFunctions
{

	public ZcubeHalf () { super (40); setFunction (a, zCubedMinus1 ()); }
	private final ComplexConstant a = new ComplexConstant (0.5);

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Newton : P=z^3-1, a=0.5"; }

	/**
	 * @return area of interest in this fractal
	 */
	public static Newton favorite ()
	{
		return (Newton) new ZcubeHalf ().setViewArea (0.22058825194835663, -0.8382352590560913, 0.5882352739572525);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Fractal#getFractalName()
	 */
	public String getFractalName () { return fractalNameFor (NewtonFractals.HalfZcubed); }

	private static final long serialVersionUID = 1585702141355909246L;
}


/**
 * a*f(z)/f'(z), standard form f(z) = z^2 - 1, a=1+i
 */
class Zsq extends NewtonFunctions
{

	/**
	 * default full set view
	 */
	public Zsq () { setFunction (a, zSquaredMinus1 ()); }
	private final ComplexConstant a = new ComplexConstant (1, 1);

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Newton : P=z^2-1, a=1+i"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Fractal#getFractalName()
	 */
	public String getFractalName () { return fractalNameFor (NewtonFractals.Zsquared); }

	private static final long serialVersionUID = -8198866785596308655L;
}

