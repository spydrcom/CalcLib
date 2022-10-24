
package net.myorb.math.computational;

import net.myorb.math.computational.Parameterization;

import net.myorb.math.linalg.Solution;

/**
 * Lower-Upper Decomposition for VanCheNodes-22 algorithm (Vandermonde-Chebychev Nodes)
 * @author Michael Druckman
 */
public class VCN22LUD extends VCNLUD implements Solution
{


	public VCN22LUD
		(
			Parameterization configuration
		)
	{
		super (22, configuration);
	}


}

