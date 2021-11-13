
package net.myorb.math.matrices;

import net.myorb.math.*;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

/**
 * provide description of matrices as types for algebraic transform
 * @param <T> the types of coefficients in the polynomial terms
 * @author Michael Druckman
 */
public class MatrixSpaceManager<T> extends MatrixOperations<T>
	implements SpaceManager <Matrix<T>>
{

	/**
	 * build a management object based on type manager for real/imag fields
	 * @param manager the manager for the type being manipulated
	 */
	public MatrixSpaceManager
		(SpaceManager<T> manager)
	{
		super (manager);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getName()
	 */
	public String getName () { return "Matrices"; }


	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getDataType()
	 */
	public DataType getDataType () { return DataType.Matrix; }


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getComponentManager()
	 */
	public SpaceManager<T> getComponentManager () { return manager; }


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isZero(java.lang.Object)
	 */
	public boolean isZero (Matrix<T> m)
	{
		return isConstant (m.cells, discrete (0));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#newScalar(int)
	 */
	public Matrix<T> newScalar (int x) { return null; }
	public Matrix<T> newConstantMatrix (int rows, int cols, int x) // replaces non-dimensional version
	{
		Matrix<T> m = new Matrix<T> (rows, cols, manager);
		fillList (m.cells, discrete (x));
		return m;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getZero()
	 */
	public Matrix<T> getZero () { return null; }  // replaces non-dimensional version
	public Matrix<T> getZero (int rows, int cols) { return newConstantMatrix (rows, cols, 0); }


	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getOne()
	 */
	public Matrix<T> getOne () { return null; }
	public Matrix<T> getOne (int rows, int cols)
	{ return newConstantMatrix (rows, cols, 1); } // replaces non-dimensional version, probably not useful
	public Matrix<T> getOne (int size) { return identity (size); } // square multiplicative identity


	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getFieldStructure()
	 */
	public FieldStructure<Matrix<T>> getFieldStructure ()
	{ raiseException ("Matrix multiplication is not commutative"); return null; }


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#conjugate(java.lang.Object)
	 */
	public Matrix<T> conjugate (Matrix<T> x) { return x; }


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#negate(java.lang.Object)
	 */
	public Matrix<T> negate (Matrix<T> x) 
	{ return times (discrete (-1), x); }


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#add(java.lang.Object, java.lang.Object)
	 */
	public Matrix<T> add (Matrix<T> x, Matrix<T> y)
	{
		return sum (x, y);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#multiply(java.lang.Object, java.lang.Object)
	 */
	public Matrix<T> multiply (Matrix<T> x, Matrix<T> y)
	{
		return product (x, y);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#invert(java.lang.Object)
	 */
	public Matrix<T> invert (Matrix<T> x) { return inv (x); }
	

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#isNegative(java.lang.Object)
	 */
	public boolean isNegative (Matrix<T> x) { return false; } // order not meaningful

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#lessThan(java.lang.Object, java.lang.Object)
	 */
	public boolean lessThan (Matrix<T> x, Matrix<T> y)
	{ throw new RuntimeException ("lessThan requested for matrix"); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toDecimalString(java.lang.Object)
	 */
	public String toDecimalString (Matrix<T> x) { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#setDisplayPrecision(int)
	 */
	public void setDisplayPrecision (int digits) {}

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#resetDisplayPrecision()
	 */
	public void resetDisplayPrecision () {}

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toInternalString(java.lang.Object)
	 */
	public String toInternalString (Matrix<T> x) { return null; }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#getPi()
	 */
	public Matrix<T> getPi ()
	{ throw new RuntimeException ("PI requested for matrix space"); }

	/* (non-Javadoc)
	 * @see net.myorb.math.FieldManager#toNumber(java.lang.Object)
	 */
	public Number toNumber (Matrix<T> x)
	{ throw new RuntimeException ("matrix not representable as Number"); }

	/**
	 * build a basic matrix manager based on double floating real components
	 * @return a matrix field manager based on Double components
	 */
	public static MatrixSpaceManager<Double> newInstance ()
	{ return new MatrixSpaceManager<Double> (new DoubleFloatingFieldManager ()); }

	/* (non-Javadoc)
	 * @see net.myorb.math.SpaceManager#getEmptyArray()
	 */
	@SuppressWarnings("unchecked")
	public Matrix<T>[] getEmptyArray ()
	{
		return new Matrix[]{};
	}

}

