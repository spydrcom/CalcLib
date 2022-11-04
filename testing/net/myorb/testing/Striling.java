
package net.myorb.testing;

public class Striling
{

	//  s(n+1,k)=-n s(n,k)+s(n,k-1)}.
	
	public static double s (int n, int k)
	{
		if (n == 0 && k == 0) return 1;
		if (n == 0 || k == 0) return 0;
		return (n-1) * s(n-1,k) + s(n-1,k-1);
	}

	public static void main (String[] args)
	{
		for (int n = 0; n < 10; n++)
			for (int k = 0; k < 10; k++)
				System.out.println ("n="+n + " k="+k + " : " + (s(n,k)));
	}

	static double F (double n)
	{
		double res = n;
		if (n < 2) return 1;
		for (int f = (int)n-1; f>1; f--) res *= f;
		return res;
	}
	/**
	 * binomial coefficients
	 * - specifically for integer operands
	 * @param n the upper number of the set
	 * @param k the lower number of the set
	 * @return n! / ( k! * (n - k)! )
	 */
	public static double binomialCoefficient (int n, int k)
	{
		if (k < 0 || k > n) return 0;
		if (k == 0 || k == n) return 1;

		double kf = 1, nf = 1, nmk = n - k;

		for (int i = 1; i <= n; i++)
		{
			if (i > nmk) nf *= i;
			if (i <= k) kf *= i;
		}

		return nf / kf;
	}


	/**
	 * stirlingNumbers second kind { n / k }
	 * @param n the upper number of the set
	 * @param k the lower number of the set
	 * @return S ( n, k )
	 */
	public static double stirlingNumbers (int n, int k)
	{
		if (k > n) return 0;
		double F = 1.0, S = -1;
		// 1/k! * SUM [i=0:k] (-1)^i * BC(k/i) * (k - i)^n
		double number = Math.pow (k, n);
		for (int i=1; i<=k; i++, S=-S)
		{
			number += S * Math.pow (k-i, n) *
				binomialCoefficient (k, i);
			F *= i;
		}
		return number / F;
	}

	public static void mainx (String[] args)
	{
		for (int n = 0; n <= 10; n++)
		{
			for (int k = 0; k <= 10; k++)
			{
				double S = stirlingNumbers (n,k);
				if (S == 0) continue;
				System.out.println ("n="+n + "\tk="+k +" S=" + S);
			}
		}
	}

}
