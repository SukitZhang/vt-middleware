/*
  $Id$

  Copyright (C) 2003-2010 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package edu.vt.middleware.ldap;

/**
 * Provides a wrapper class for testing {@link #operationRetry()}.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class RetrySearchOperation extends SearchOperation
{

  /** serial version uid. */
  private static final long serialVersionUID = 4247614583961731974L;

  /** retry counter. */
  private int retryCount;

  /** run time counter. */
  private long runTime;

  /** stop counter. */
  private int stopCount;


  /**
   * Creates a new retry search operation.
   *
   * @param  lc  ldap connection
   */
  public RetrySearchOperation(final LdapConnection lc)
  {
    super(lc);
  }


  /**
   * Returns the retry count.
   *
   * @return  retry count
   */
  public int getRetryCount()
  {
    return this.retryCount;
  }


  /**
   * Returns the run time counter.
   *
   * @return  run time
   */
  public long getRunTime()
  {
    return this.runTime;
  }


  /**
   * Sets the count at which to stop retries.
   *
   * @param  i  stop count
   */
  public void setStopCount(final int i)
  {
    this.stopCount = i;
  }


  /** Resets all the counters. */
  public void reset()
  {
    this.retryCount = 0;
    this.runTime = 0;
    this.stopCount = 0;
  }


  /** {@inheritDoc} */
  protected void operationRetry(
    final LdapException e,
    final int count)
    throws LdapException
  {
    this.retryCount = count;

    final long t = System.currentTimeMillis();
    super.operationRetry(e, count);
    this.runTime += System.currentTimeMillis() - t;
    if (this.stopCount > 0 && this.retryCount == this.stopCount) {
      throw e;
    }
  }
}