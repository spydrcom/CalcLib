
package net.myorb.math.matrices.optimization;

public interface MinorMatrixNodeFactory<T>
{
	MinorMatrixWrapper<T> buildMinorMatrixNode (MinorMatrixWrapper<T> m, int column);
}
