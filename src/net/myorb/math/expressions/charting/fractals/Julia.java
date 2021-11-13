
package net.myorb.math.expressions.charting.fractals;

import net.myorb.math.expressions.charting.DisplayGraph3D;
import net.myorb.math.expressions.charting.colormappings.ContourColorSchemeRequest;
import net.myorb.charting.ColorSelection;

import net.myorb.math.complexnumbers.ComplexValue;

import java.util.HashSet;
import java.util.Set;

/**
 * display of Julia sets
 * @author Michael Druckman
 */
public class Julia extends Fractal implements Fractal.Descriptor
{


	/**
	 * limits that indicate divergence
	 */
	static final float DIVERGENCE_LIMIT = 50.0f;
	static final int DEFAULT_ITERATION_LIMIT = 1000;


	/**
	 * full view coordinates
	 */
	static final DisplayGraph3D.Point FULL_VIEW_LOW_CORNER = new DisplayGraph3D.Point (-5f, -5f);
	static final float FULL_VIEW_EDGE_SIZE = 10;


	/**
	 * view selected using low corner and edge size
	 * @param lowCorner the low corner is x-min/y-min
	 * @param edgeSize distance along each axis from low corner
	 * @param maxResult the largest value produced by the transform
	 */
	public Julia (DisplayGraph3D.Point lowCorner, float edgeSize, int maxResult)
	{ super (lowCorner, edgeSize, maxResult); setCurrentLimit (DIVERGENCE_LIMIT); }
	public Julia (int maxIterations) { this (FULL_VIEW_LOW_CORNER, FULL_VIEW_EDGE_SIZE, maxIterations); }
	public Julia () { this (DEFAULT_ITERATION_LIMIT); }


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
			// z[n+1] = z[n]^2 + c
			ComplexValue<Double> zNplus1 = z.squared ().plus (c);
			double zNplus1Magnitude = zNplus1.modSquared ();
			if (zNplus1Magnitude > currentLimit) break;
			z = zNplus1; iteration++;
		}

		return iteration;
	}


	/*
	 * set the constant term of the iteration
	 */

	protected void setC (double r) { setC (r, 0); }
	protected void setC (double r, double i) { setC (new ComplexConstant (r, i)); }
	protected void setC (ComplexConstant c) { this.c = c; }

	/**
	 * @return the iteration constant term
	 */
	protected ComplexConstant getC () { return c; }
	private ComplexConstant c;


	/**
	 * @param nickName conventional name
	 * @return nick name with associated value
	 */
	public String titleFor (String nickName)
	{ return "Julia " + nickName + " : c=" + c; }


	/**
	 * full image tends to be seen at (-1.5, -1.5) : 3
	 * @return Fractal with standard view area
	 */
	protected Fractal setStandardView () { return setViewArea (-1.5, -1.5, 3); }


	/**
	 * get collection of Julia sets
	 * @return map of included sets
	 */
	public static FractalMap getFractalMap ()
	{
		FractalMap map = new FractalMap ();

		map.addNamed (Drunkard.favorite ());
		map.addNamed (SanMarco.favorite ());
		map.addNamed (Dendrite.favorite ());
		map.addNamed (Siegel.favorite ());
		map.addNamed (Rabbit.favorite ());
		map.addNamed (Dragon.favorite ());
		map.addNamed (Phi.favorite ());

		map.addNamed (new SimpleOne ());
		map.addNamed (new SimpleTwo ());
		map.addNamed (new SimpleThree ());
		map.addNamed (new SimpleNegOne ());
		map.addNamed (new SimpleNegTwo ());
		map.addNamed (new SimpleIminusOne ());

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

			choices.add (new SanMarco ());			choices.add (new Dendrite ()); 
			choices.add (new Siegel ());			choices.add (new Drunkard ());   		choices.add (new Rabbit ());
			choices.add (new Dragon ());			choices.add (new Phi ());

			choices.add (new SimpleIminusOne ());
			choices.add (new SimpleNegTwo ());		choices.add (new SimpleNegOne ());
			choices.add (new SimpleOne ());			choices.add (new SimpleTwo ());
			choices.add (new SimpleThree ());

			return choices;
		}
	}


	/*
	 * rebuild mechanism
	 */

	/**
	 * identifier for each kind of Julia fractal
	 */
	public enum JuliaFractals 
	{
		SimpleOne, SimpleTwo, SimpleThree,
		SimpleNegOne, SimpleNegTwo, SimpleIminusOne,
		Dendrite, Rabbit, Marco, Siegel, Phi, Dragon, Drunkard
	}

	/**
	 * @param name the identity of a transform
	 * @return TRUE for match with Julia nickname
	 */
	public static boolean isMember (String name)
	{ return members.contains (name); }
	static Set<String> members;

	static
	{
		members = new HashSet<String>();
		for (JuliaFractals f : JuliaFractals.values ())
		{ members.add (f.name ()); }
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Fractal#reconstitute(java.lang.String)
	 */
	public Fractal reconstitute (String fractalName)
	{
		switch (JuliaFractals.valueOf (fractalName))
		{
			case Phi: return new Phi ();
			case Rabbit: return new Rabbit ();
			case Siegel: return new Siegel ();
			case Drunkard: return new Drunkard ();
			case SimpleOne: return new SimpleOne ();
			case SimpleTwo: return new SimpleTwo ();
			case SimpleThree: return new SimpleThree ();
			case SimpleNegOne: return new SimpleNegOne ();
			case SimpleNegTwo: return new SimpleNegTwo ();
			case SimpleIminusOne: return new SimpleIminusOne ();
			case Dendrite: return new Dendrite ();
			case Marco: return new SanMarco ();
			case Dragon: return new Dragon ();
			default:
		}
		throw new RuntimeException ("Unrecognized Fractal");
	}


	private static final long serialVersionUID = 3399103224774765096L;
}


/*
 * Fractal classes identified by nicknames
 */


class Dendrite extends Julia
{

	public Dendrite () { setC (0, 1); }

	/**
	 * @return area of interest in this fractal
	 */
	public static Julia favorite ()
	{
		return (Julia) new Dendrite ().setViewArea (-0.16176468133926392, -0.5882352590560913, 0.720588207244873);
		//return (Julia) new Dendrite ().setStandardView ();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return titleFor (getFractalName ()); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Fractal#getFractalName()
	 */
	public String getFractalName () { return fractalNameFor (JuliaFractals.Dendrite); }

	private static final long serialVersionUID = 6520903110638869362L;
}


class Rabbit extends Julia
{

	public Rabbit () { setC (-0.123d, 0.745d); }

	/*
	 * 
		ZOOM  (-0.8088235259056091, -0.8382352590560913, 1.7647058963775635)	// rabbit close up
		ZOOM  (-0.4221453070640564, -0.4333909749984741, 0.059688568115234375)
		ZOOM  (0.26057523488998413, -0.5719723105430603, 0.17517301440238953)
	 *
	 */

	/**
	 * @return area of interest in this fractal
	 */
	public static Julia favorite ()
	{
		return (Julia) new Rabbit ().setViewArea (-0.8088235259056091, -0.8382352590560913, 1.7647058963775635);
		//return (Julia) new Rabbit ().setStandardView ();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return titleFor (getFractalName ()); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Fractal#getFractalName()
	 */
	public String getFractalName () { return fractalNameFor (JuliaFractals.Rabbit); }

	private static final long serialVersionUID = 2791367191371143435L;
}


class SanMarco extends Julia
{

	public SanMarco () { setC (-0.75d); }

	/**
	 * @return area of interest in this fractal
	 */
	public static Julia favorite ()
	{
		return (Julia) new SanMarco ().setViewArea (-0.19117645919322968, -1.0, 0.3235294222831726);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return titleFor (getFractalName ()); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Fractal#getFractalName()
	 */
	public String getFractalName () { return fractalNameFor (JuliaFractals.Marco); }

	private static final long serialVersionUID = -250117133869701735L;
}


class Siegel extends Julia
{

	public Siegel () { setC (-0.391, -0.587); }

	/**
	 * @return area of interest in this fractal
	 */
	public static Julia favorite ()
	{
		return (Julia) new Siegel ().setViewArea (-0.09359859675168991, -0.9944636225700378, 0.011764705181121826);
		//return (Julia) new Siegel ().setStandardView ();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return titleFor (getFractalName ()); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Fractal#getFractalName()
	 */
	public String getFractalName () { return fractalNameFor (JuliaFractals.Siegel); }

	private static final long serialVersionUID = -5675100533367537889L;
}


class Phi extends Julia
{

	public Phi () { setC (-0.4, 0.6); }

	/**
	 * @return area of interest in this fractal
	 */
	public static Julia favorite ()
	{
		return (Julia) new Phi ().setViewArea (-0.7058823108673096, -0.2499999850988388, 0.8529412001371384);
		//return (Julia) new Phi ().setStandardView ();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return titleFor (getFractalName ()); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Fractal#getFractalName()
	 */
	public String getFractalName () { return fractalNameFor (JuliaFractals.Phi); }

	private static final long serialVersionUID = -7868032813085351923L;
}


class Dragon extends Julia
{

	public Dragon () { setC (-0.8, 0.156); }

	/**
	 * @return area of interest in this fractal
	 */
	public static Julia favorite ()
	{
		return (Julia) new Dragon ().setViewArea (-0.4411764442920685, -0.8529411554336548, 1.4264706075191498);
		//return (Julia) new Dragon ().setStandardView ();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return titleFor (getFractalName ()); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Fractal#getFractalName()
	 */
	public String getFractalName () { return fractalNameFor (JuliaFractals.Dragon); }

	private static final long serialVersionUID = -415488280821127245L;
}


class Drunkard extends Julia
{

	public Drunkard () { setC (0.285, 0.01); }

	/**
	 * @return area of interest in this fractal
	 */
	public static Julia favorite ()
	{
		return (Julia) new Drunkard ().setViewArea (-0.6911764740943909, -1.1176470518112183, 1.9558823704719543);
		//return (Julia) new Drunkard ().setStandardView ();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return titleFor (getFractalName ()); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.fractals.Fractal#getFractalName()
	 */
	public String getFractalName () { return fractalNameFor (JuliaFractals.Drunkard); }

	private static final long serialVersionUID = 6943859852369783510L;
}


class SimpleInteger extends Julia
{

	public SimpleInteger (int n) { this (n, 0); }
	public SimpleInteger (int n, int m) { setC (n, m); }

	/*
	 * i-1 ZOOM  (-0.14705879986286163, -0.5588235259056091, 0.7352941334247589)
	 * -2  ZOOM  (1.862645149230957E-8, -0.2794117331504822, 0.3235293999314308)
	 * -1  ZOOM  (1.1911765336990356, -0.23529410362243652, 0.6029411852359772)
	 * +1  ZOOM  (-1.029411792755127, -1.8382352590560913, 1.2058824002742767)
	 * +2  ZOOM  (-1.1617647409439087, -2.411764621734619, 2.7205883264541626)
	 * +3  ZOOM  (-0.8970587849617004, -2.4558823108673096, 2.1764705777168274)
	 */

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return titleFor ("Simple"); }

	private static final long serialVersionUID = -5043089360614360124L;
}
class SimpleNegTwo extends SimpleInteger
{
	public String getFractalName ()
	{ return fractalNameFor (JuliaFractals.SimpleNegTwo); }
	private static final long serialVersionUID = 2777654920485574087L;
	public SimpleNegTwo () { super (-2); }
}
class SimpleNegOne extends SimpleInteger
{
	public String getFractalName ()
	{ return fractalNameFor (JuliaFractals.SimpleNegOne); }
	private static final long serialVersionUID = -6637396559281509490L;
	public SimpleNegOne () { super (-1); }
}
class SimpleOne extends SimpleInteger
{
	public String getFractalName ()
	{ return fractalNameFor (JuliaFractals.SimpleOne); }
	private static final long serialVersionUID = 502960394627061539L;
	public SimpleOne () { super (1); }
}
class SimpleTwo extends SimpleInteger
{
	public String getFractalName ()
	{ return fractalNameFor (JuliaFractals.SimpleTwo); }
	private static final long serialVersionUID = 6243461989772970036L;
	public SimpleTwo () { super (2); }
}
class SimpleThree extends SimpleInteger
{
	public String getFractalName ()
	{ return fractalNameFor (JuliaFractals.SimpleThree); }
	private static final long serialVersionUID = 3815902738420349685L;
	public SimpleThree () { super (3); }
}
class SimpleIminusOne extends SimpleInteger
{
	public String getFractalName ()
	{ return fractalNameFor (JuliaFractals.SimpleIminusOne); }
	private static final long serialVersionUID = 7087029940377506546L;
	public SimpleIminusOne () { super (-1, 1); }
}

