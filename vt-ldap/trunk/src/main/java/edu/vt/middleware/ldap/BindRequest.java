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

import edu.vt.middleware.ldap.sasl.SaslConfig;

/**
 * Contains the data required to perform an ldap bind operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class BindRequest implements Request
{

  /** DN to bind as before performing operations. */
  private String bindDn;

  /** Credential for the bind DN. */
  private Credential bindCredential;

  /** Configuration for SASL authentication. */
  private SaslConfig saslConfig;


  /**
   * Default constructor.
   */
  public BindRequest() {}


  /**
   * Creates a new bind request.
   *
   * @param  dn  to bind as
   * @param  credential  to bind with
   */
  public BindRequest(final String dn, final Credential credential)
  {
    setBindDn(dn);
    setBindCredential(credential);
  }


  /**
   * Creates a new bind request.
   *
   * @param  dn  to bind as
   * @param  credential  to bind with
   * @param  config  sasl configuration
   */
  public BindRequest(
    final String dn, final Credential credential, final SaslConfig config)
  {
    setBindDn(dn);
    setBindCredential(credential);
    setSaslConfig(config);
  }


  /**
   * Returns the bind DN.
   *
   * @return  DN to bind as
   */
  public String getBindDn()
  {
    return bindDn;
  }


  /**
   * Sets the bind DN to authenticate as before performing operations.
   *
   * @param  dn  to bind as
   */
  public void setBindDn(final String dn)
  {
    bindDn = dn;
  }


  /**
   * Returns the credential used with the bind DN.
   *
   * @return  bind DN credential
   */
  public Credential getBindCredential()
  {
    return bindCredential;
  }


  /**
   * Sets the credential of the bind DN.
   *
   * @param  credential  to use with bind DN
   */
  public void setBindCredential(final Credential credential)
  {
    bindCredential = credential;
  }


  /**
   * Returns the sasl config.
   *
   * @return  sasl config
   */
  public SaslConfig getSaslConfig()
  {
    return saslConfig;
  }


  /**
   * Sets the sasl config.
   *
   * @param  config  sasl config
   */
  public void setSaslConfig(final SaslConfig config)
  {
    saslConfig = config;
  }


  /**
   * Returns whether this request contains a sasl config.
   *
   * @return  whether this request contains a sasl config
   */
  public boolean isSaslRequest()
  {
    return saslConfig != null;
  }


  /**
   * Provides a descriptive string representation of this instance.
   *
   * @return  string representation
   */
  @Override
  public String toString()
  {
    return
      String.format(
        "[%s@%d::bindDn=%s, saslConfig=%s]",
        getClass().getName(),
        hashCode(),
        bindDn,
        saslConfig);
  }
}