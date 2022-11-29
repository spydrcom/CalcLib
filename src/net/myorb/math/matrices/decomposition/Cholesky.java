
package net.myorb.math.matrices.decomposition;

public class Cholesky
{

	/*
	 * taken from https://en.wikipedia.org/wiki/Cholesky_decomposition
	 */

	public double[][] Banachiewicz (double[][] A)
	{
		int N = A.length;
		double[][] L = new double[N][N];
		for (int i = 0; i < N; i++) {
		    for (int j = 0; j <= i; j++) {
		        float sum = 0;
		        for (int k = 0; k < j; k++)
		            sum += L[i][k] * L[j][k];

		        if (i == j)
		            L[i][j] = Math.sqrt(A[i][i] - sum);
		        else
		            L[i][j] = (1.0 / L[j][j] * (A[i][j] - sum));
		    }
		}
		return L;
	}

	public double[][] Crout (double[][] A)
	{
		int N = A.length;
		double[][] L = new double[N][N];
		for (int j = 0; j < N; j++) {
		    float sum = 0;
		    for (int k = 0; k < j; k++) {
		        sum += L[j][k] * L[j][k];
		    }
		    L[j][j] = Math.sqrt(A[j][j] - sum);

		    for (int i = j + 1; i < N; i++) {
		        sum = 0;
		        for (int k = 0; k < j; k++) {
		            sum += L[i][k] * L[j][k];
		        }
		        L[i][j] = (1.0 / L[j][j] * (A[i][j] - sum));
		    }
		}
		return L;
	}

	public void update (double[][] L, double[] x)
	{
	    int n = x.length;
	    for (int k = 0; k < n; k++)
	    {
	    	double r = Math.sqrt (Math.pow (L[k][k], 2) + Math.pow (x[k], 2));
	    	double c = r / L[k][k], s = x[k] / L[k][k];

	    	if (k < n)
	    	{
	    	    for (int kk = k+1; kk < n; kk++)
	    	    {
		            L[kk][k] = (L[kk][k] + s * x[kk]) / c;
		            x[kk] = c * x[kk] - s * L[kk][k];
	    	    }
	    	}
	    }

	}

	/*
		function [L] = cholupdate(L, x)
		    n = length(x);
		    for k = 1:n
		        r = sqrt(L(k, k)^2 + x(k)^2);
		        c = r / L(k, k);
		        s = x(k) / L(k, k);
		        L(k, k) = r;
		        if k < n
		            L((k+1):n, k) = (L((k+1):n, k) + s * x((k+1):n)) / c;
		            x((k+1):n) = c * x((k+1):n) - s * L((k+1):n, k);
		        end
		    end
		end
	 */

}
