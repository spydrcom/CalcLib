
package net.myorb.math.expressions.charting;

import net.myorb.charting.DisplayGraphTypes.Point;

import java.awt.Color;

/**
 * map 2 dimensional plane to 2 dimensional plane
 * @author Michael Druckman
 */
public class Transform2D
{

	/**
	 * describe a 2D to 2D transform
	 */
	public interface Transform
	{
		/**
		 * provide data transformation
		 * @param from a point in the source plane
		 * @return a point in the transformed plane
		 */
		DisplayGraph.Point translate (DisplayGraph.Point from);
	}

	/**
	 * describe a domain
	 */
	public interface Domain
	{
		/**
		 * @return low bound of domain
		 */
		double getLo ();
		/**
		 * @return high bound of domain
		 */
		double getHi ();
		/**
		 * @return increment for domain
		 */
		double getIncrement ();
	}


	/**
	 * chart a 2D to 2D plot
	 * @param title the title to place on the frame
	 * @param transform the 2D to 2D transform to be charted
	 * @param inner the domain description of the inner variable
	 * @param outer the domain description of the outer variable
	 * @param colorDomain the domain description for plot colors
	 */
	public static void plot
	(String title, Transform transform, Domain inner, Domain outer, Domain colorDomain)
	{
		DisplayGraph.Colors colors = new DisplayGraph.Colors ();
		ColorManager colorManager = new ColorManager (colorDomain);
		DisplayGraph.PlotCollection funcPlot = new DisplayGraph.PlotCollection ();

		DisplayGraph.Point source = new DisplayGraph.Point (0, 0);
		for (double o = outer.getLo(); o <= outer.getHi(); o += outer.getIncrement())
		{
			Point.Series points = new Point.Series ();

			for (double i = inner.getLo(); i < inner.getHi(); i += inner.getIncrement())
			{
				source.x = i; source.y = o;
				DisplayGraph.Point computed = transform.translate (source);
				points.add (computed);
			}

			colors.add (colorManager.getColor ());
			colorManager.increment ();
			funcPlot.add (points);
		}

		DisplayGraph.plot (colors, funcPlot, title);
	}


	/**
	 * store the properties of a domain
	 * @param lo the low bound value of the domain
	 * @param hi the high bound value of the domain
	 * @param increment the increment to be used
	 * @return a domain implementation
	 */
	public static Domain getDomain (double lo, double hi, double increment)
	{ return new DomainStorage (lo, hi, increment); }


}


/**
 * manage properties of a domain
 */
class DomainStorage implements Transform2D.Domain
{

	double lo, hi, increment;

	public double getHi () { return hi; }
	public void setHi (double hi) { this.hi = hi; }
	public double getIncrement () { return increment; }
	public void setIncrement (double increment) { this.increment = increment; }
	public void setLo (double lo) { this.lo = lo; }
	public double getLo () { return lo; }

	public String toString ()
	{
		return "[" + lo + ", " + hi + ", " + increment + "]";
	}

	public DomainStorage (double lo, double hi, double increment)
	{
		this.lo = lo;
		this.increment = increment;
		this.hi = hi;
	}
	
}


/**
 * control the sequence of colors
 */
class ColorManager
{
	
	long h, s, b;
	long hMax, sMax, bMax;
	long hInc, sInc, bInc;

	/**
	 * convert the domain to color parameters
	 * @param colorDomain the lo, hi, increment for the color sequence
	 */
	ColorManager (Transform2D.Domain colorDomain)
	{
		long longHash; double hash;
		
		hash = colorDomain.getLo ();
		b = (longHash = (long)hash) % 1000;
		s = (longHash = (longHash - (long)b) / 1000) % 1000;
		h = (longHash = (longHash - (long)s) / 1000) % 1000;
		b*=10; s*=10; h*=10; 

		hash = colorDomain.getHi ();
		bMax = (longHash = (long)hash) % 1000;
		sMax = (longHash = (longHash - (long)bMax) / 1000) % 1000;
		hMax = (longHash = (longHash - (long)sMax) / 1000) % 1000;
		bMax*=10; sMax*=10; hMax*=10; 

		hash = colorDomain.getIncrement ();
		bInc = (longHash = (long)hash) % 10000;
		sInc = (longHash = (longHash - (long)bInc) / 10000) % 10000;
		hInc = (longHash = (longHash - (long)sInc) / 10000) % 10000;

//		System.out.println ("h="+h+"  s="+s+"  b="+b);
//		System.out.println ("hm="+hMax+"  sm="+sMax+"  bm="+bMax);
//		System.out.println ("hi="+hInc+"  si="+sInc+"  bi="+bInc);
	}

	/**
	 * @return the mapped HSB color
	 */
	Color getColor ()
	{
		float hf = (float)h/10000.0f, sf = (float)s/10000.0f, bf = (float)b/10000.0f;
//		System.out.println ("h=" + hf + ", s=" + sf + ", b=" + bf);
		return Color.getHSBColor (hf, sf, bf);
	}

	/**
	 * increment the color parameters
	 */
	void increment ()
	{
		h = h + hInc; if (h > hMax) h -= hMax;
		s = s + sInc; if (s > sMax) s -= sMax;
		b = b + bInc; if (b > bMax) b -= bMax;
	}

}


