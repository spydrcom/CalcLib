
package net.myorb.math.primenumbers.sieves;

import net.myorb.math.primenumbers.TableManager;

/**
 * 
 * wrapper for Table Manager implementation.
 * sieve implementations can extend this for convenient access to Table Manager operations
 * 
 * @author Michael Druckman
 *
 */
public class SievePrimitives implements TableManager, SieveDriver
{

	/**
	 * construct based on access to Table Manager being wrapped
	 * @param tableManager Table Manager object being wrapped
	 */
	public SievePrimitives (TableManager tableManager)
	{
		this.tableManager = tableManager;
		this.tableSize = tableManager.getTableSize ();
	}
	protected TableManager tableManager;
	protected int tableSize;

  	/* (non-Javadoc)
  	 * @see net.myorb.math.primenumbers.TableManager#markIsPresent(int)
  	 */
  	public boolean markIsPresent (int value) { return tableManager.markIsPresent (value); }
  	
  	/* (non-Javadoc)
  	 * @see net.myorb.math.primenumbers.TableManager#markComposite(int, int, int)
  	 */
  	public void markComposite (int value, int prime, int mul) { tableManager.markComposite (value, prime, mul); }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.TableManager#markCompositesFor(int)
	 */
	public void markCompositesFor (int primeNumber) { tableManager.markCompositesFor (primeNumber); }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.TableManager#markPrimeAndCompositesFor(int)
	 */
	public void markPrimeAndCompositesFor (int primeNumber) { tableManager.markPrimeAndCompositesFor (primeNumber); }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.TableManager#markPrime(int)
	 */
	public void markPrime (int primeNumber) { tableManager.markPrime (primeNumber); }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.TableManager#getTableSize()
	 */
	public int getTableSize () { return tableSize; }

	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.sieves.SieveDriver#generatePrimes()
	 */
	public void generatePrimes () { throw new RuntimeException ("Unimplemented Sieve"); }
	
	/* (non-Javadoc)
	 * @see net.myorb.math.primenumbers.sieves.SieveDriver#getName()
	 */
	public String getName () { return "Not Implemented"; }

}
