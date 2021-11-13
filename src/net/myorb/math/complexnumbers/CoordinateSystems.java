
package net.myorb.math.complexnumbers;

import net.myorb.math.SpaceManager;

/**
 * manage conversion between cartesian and polar coordinate systems
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class CoordinateSystems<T>
{

	/**
	 * based on a type manager
	 * @param manager the type manager
	 */
	public CoordinateSystems (SpaceManager<T> manager)
	{
		this (manager, new ComplexLibrary<T> (manager, null));
	}
	public CoordinateSystems (SpaceManager<T> manager, ComplexLibrary<T> lib)
	{
		this.manager = manager;
		this.lib = lib;
	}
	SpaceManager<T> manager;
	ComplexLibrary<T> lib;

	/**
	 * two dimensions mapped along 2 orthogonal axis
	 * @param <T> type on which operations are to be executed
	 */
	public interface Rectangular<T>
	{
		/**
		 * real part
		 * @return the x-axis offset
		 */
		T Re ();

		/**
		 * imaginary part
		 * @return the y-axis offset
		 */
		T Im ();

		/**
		 * convert to polar
		 * @return the polar equivalent
		 */
		Polar<T> toPolar ();

		/**
		 * convert to complex value
		 * @return the equivalent complex value
		 */
		ComplexValue<T> toComplexValue ();
	}

	/**
	 * two dimensions mapped with distance and angle
	 * @param <T> type on which operations are to be executed
	 */
	public interface Polar<T>
	{
		/**
		 * get distance from origin
		 * @return distance from the origin
		 */
		T getR ();

		/**
		 * get angle off the x-axis
		 * @return angle off the x-axis
		 */
		T getTheta ();

		/**
		 * convert to rectangular
		 * @return the rectangular equivalent
		 */
		Rectangular<T> toRectangular ();

		/**
		 * convert to complex value
		 * @return the equivalent complex value
		 */
		ComplexValue<T> toComplexValue ();
	}

	/**
	 * rectangular representation from complex value
	 * @param z a complex value representing the point
	 * @return a rectangular representation
	 */
	public Rectangular<T> newRectangularInstance (ComplexValue<T> z)
	{ return new RectangularValue<T> (z, this); }

	/**
	 * polar representation from complex value
	 * @param z a complex value representing the point
	 * @return a polar representation
	 */
	public Polar<T> newPolarInstance (ComplexValue<T> z)
	{ return new RectangularValue<T> (z, this).toPolar (); }

	/**
	 * rectangular representation from axis coordinates
	 * @param x distance along the x axis to the point
	 * @param y distance along the y axis to the point
	 * @return a rectangular representation
	 */
	public Rectangular<T> newRectangularInstance (T x, T y)
	{ return new RectangularValue<T> (x, y, this); }

	/**
	 * polar representation using r and theta
	 * @param r distance from origin to the point
	 * @param theta angle off the x-axis
	 * @return a polar representation
	 */
	public Polar<T> newPolarInstance (T r, T theta)
	{ return new PolarValue<T> (r, theta, this); }


	/**
	 * three independent orthoganol axis coordinates
	 * @param <T> type on which operations are to be executed
	 */
	public interface Cartesian<T>
	{

		/**
		 * distance along x-axis
		 * @return value of x
		 */
		T getX ();

		/**
		 * distance along y-axis
		 * @return value of y
		 */
		T getY ();

		/**
		 * distance along z-axis
		 * @return value of z
		 */
		T getZ ();

		/**
		 * convert to spherical
		 * @return spherical descriptor
		 */
		Spherical<T> toSpherical ();

	}

	/**
	 * 3D polar equivalent, a distance coordinate and angles for elevation and azimuth
	 * @param <T> type on which operations are to be executed
	 */
	public interface Spherical<T>
	{

		/**
		 * distance from 0
		 * @return the value of R
		 */
		T getR ();

		/**
		 * the angle on the horizontal plane
		 * @return the azimuth angle
		 */
		T getAzimuth ();

		/**
		 * the angle off the horizontal plane
		 * @return the elevation angle
		 */
		T getElevation ();

		
		/**
		 * convert to Cartesian
		 * @return Cartesian descriptor
		 */
		Cartesian<T> toCartesian ();
	}

	public Cartesian<T> newCartesianInstance (T x, T y, T z)
	{ return new CartesianValue<T>(x, y, z, this); }

	public Spherical<T> newSphericalInstance (T r, T azimuth, T elevation)
	{ return null; }

}


/**
 * represent a point with polar coordinates
 * @param <T> type on which operations are to be executed
 */
class CartesianValue<T> extends Arithmetic<T> implements CoordinateSystems.Cartesian<T>
{

	/**
	 * construct a Cartesian represented value
	 * @param right the distance from origin of the point
	 * @param theta the angle off the x-axis
	 * @param sys a coordinate object
	 */
	public CartesianValue (T x, T y, T z, CoordinateSystems<T> sys)
	{
		super (sys.manager);
		this.x = x; this.y = y; this.z = z;
		this.sys = sys;
	}
	CoordinateSystems<T> sys;
	T x, y, z;

	public T getX () { return x; }
	public T getY () { return y; }
	public T getZ () { return z; }

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CoordinateSystems.Cartesian#toSpherical()
	 */
	public CoordinateSystems.Spherical<T> toSpherical ()
	{
//		Value<T> azimuth = atan2 (forValue (y), forValue (x));
//		Value<T> xsq = forValue (x).squared (), ysq = forValue (y).squared (), zsq = forValue (z).squared ();
//		Value<T> elevation = atan2 (forValue (z), xsq.plus (ysq).sqrt ()), r = xsq.plus (ysq).plus (zsq).sqrt ();
//		return new SphericalValue<T>(r.getUnderlying (), azimuth.getUnderlying (), elevation.getUnderlying (), sys);
		throw new RuntimeException ("Not Implemented");
	}

	Value<T> atan2 (Value<T> y, Value<T> x) { return null; }
}

class SphericalValue<T> implements CoordinateSystems.Spherical<T>
{

	public SphericalValue (T r, T azimuth, T elevation, CoordinateSystems<T> sys)
	{
		this.r = r;
		this.azimuth = azimuth;
		this.elevation = elevation;
		this.sys = sys;
	}
	CoordinateSystems<T> sys;
	T r, azimuth, elevation;

	public T getAzimuth() { return azimuth; }
	public T getElevation() { return elevation; }
	public T getR() { return r; }

	public CoordinateSystems.Cartesian<T> toCartesian()
	{
		throw new RuntimeException ("Not Implemented");
	}
}

/**
 * represent a point with cartesian coordinates
 * @param <T> type on which operations are to be executed
 */
class RectangularValue<T> extends ComplexValue<T>
	implements CoordinateSystems.Rectangular<T>
{

	/**
	 * build from a complex value
	 * @param value the complex value
	 * @param sys a coordinate object
	 */
	public RectangularValue
	(ComplexValue<T> value, CoordinateSystems<T> sys)
	{ this (value.Re(), value.Im(), sys); }

	/**
	 * build from X/Y values
	 * @param realPart the x-axis coordinate
	 * @param imagPart the y-axis coordinate
	 * @param sys a coordinate object
	 */
	public RectangularValue (T realPart, T imagPart, CoordinateSystems<T> sys)
	{
		super (realPart, imagPart, sys.manager);
		this.sys = sys;
	}
	CoordinateSystems<T> sys;

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CoordinateSystems.Rectangular#toPolar()
	 */
	public CoordinateSystems.Polar<T> toPolar ()
	{
		T r = magnitude ();
		T theta = sys.lib.arg (this);
		return new PolarValue<T> (r, theta, sys);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CoordinateSystems.Rectangular#toComplexValue()
	 */
	public ComplexValue<T> toComplexValue () { return this; }

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.ComplexValue#toString()
	 */
	public String toString () { return super.toString (); }

}

/**
 * represent a point with polar coordinates
 * @param <T> type on which operations are to be executed
 */
class PolarValue<T> implements CoordinateSystems.Polar<T>
{

	/**
	 * construct a polar represented value
	 * @param r the distance from origin of the point
	 * @param theta the angle off the x-axis
	 * @param sys a coordinate object
	 */
	public PolarValue (T r, T theta, CoordinateSystems<T> sys)
	{
		this.r = r;
		this.theta = theta;
		this.sys = sys;
	}
	CoordinateSystems<T> sys;

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CoordinateSystems.Polar#getR()
	 */
	public T getR () { return r; }
	T r;

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CoordinateSystems.Polar#getTheta()
	 */
	public T getTheta () { return theta; }
	T theta;
	
	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CoordinateSystems.Polar#toRectangular()
	 */
	public CoordinateSystems.Rectangular<T> toRectangular ()
	{
		ComplexValue<T> cis = sys.lib.cis (theta);
		return new RectangularValue<T>
		(
			sys.manager.multiply (r, cis.Re ()),
			sys.manager.multiply (r, cis.Im ()),
			sys
		);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.complexnumbers.CoordinateSystems.Polar#toComplexValue()
	 */
	public ComplexValue<T> toComplexValue ()
	{
		return toRectangular ().toComplexValue ();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		T piInverted = sys.manager.invert (sys.manager.getPi ());
		T piMultiple = sys.manager.multiply (theta, piInverted);

		String sign = "";
		if (sys.manager.isNegative (piMultiple))
		{
			piMultiple = sys.manager.negate (piMultiple);
			sign = "-";
		}

		if (sys.manager.isZero (piMultiple))
		{
			return r.toString ();
		}
		else if (sys.manager.lessThan (piMultiple, sys.manager.getOne ()))
		{
			piMultiple = sys.manager.invert (piMultiple);
			return r + " CIS (" + sign + "PI / " +piMultiple + ")";
		}
		else
		{
			return r + " CIS (" + sign + "PI * " + piMultiple + ")";
		}
	}

}

