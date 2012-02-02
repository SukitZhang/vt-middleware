/*
  $Id$

  Copyright (C) 2003-2012 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.provider.netscape;

import java.io.IOException;
import java.net.Socket;
import javax.net.SocketFactory;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPSocketFactory;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.LdapURL;
import org.ldaptive.provider.ConnectionFactory;
import org.ldaptive.provider.Provider;
import org.ldaptive.ssl.TLSSocketFactory;

/**
 * Netscape provider implementation. Provides connection factories for clear and
 * SSL connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class NetscapeProvider implements Provider<NetscapeProviderConfig>
{

  /** Provider configuration. */
  private NetscapeProviderConfig config = new NetscapeProviderConfig();


  /** {@inheritDoc} */
  @Override
  public ConnectionFactory<NetscapeProviderConfig> getConnectionFactory(
    final ConnectionConfig cc)
  {
    if (cc.getUseStartTLS()) {
      throw new UnsupportedOperationException("startTLS is not supported");
    }

    config.makeImmutable();
    LDAPSocketFactory factory = config.getLDAPSocketFactory();
    if (cc.getUseSSL() && factory == null) {
      factory = getHostnameVerifierSocketFactory(cc);
    }
    final ConnectionFactory<NetscapeProviderConfig> cf =
      new NetscapeConnectionFactory(
        cc.getLdapUrl(),
        factory,
        (int) cc.getConnectTimeout(),
        (int) cc.getResponseTimeout());
    cf.setProviderConfig(config);
    return cf;
  }


  /**
   * Returns an SSL socket factory configured with a default hostname verifier.
   *
   * @param  cc  connection configuration
   *
   * @return  SSL socket factory
   */
  protected LDAPSocketFactory getHostnameVerifierSocketFactory(
    final ConnectionConfig cc)
  {
    // Netscape does not do hostname verification by default
    // set a default hostname verifier
    final LdapURL ldapUrl = new LdapURL(cc.getLdapUrl());
    return new NetscapeLDAPSocketFactory(
      TLSSocketFactory.getHostnameVerifierFactory(
        cc.getSslConfig(), ldapUrl.getEntriesAsString()));
  }


  /** {@inheritDoc} */
  @Override
  public NetscapeProviderConfig getProviderConfig()
  {
    return config;
  }


  /** {@inheritDoc} */
  @Override
  public void setProviderConfig(final NetscapeProviderConfig npc)
  {
    config = npc;
  }


  /** {@inheritDoc} */
  @Override
  public NetscapeProvider newInstance()
  {
    return new NetscapeProvider();
  }


  /** Implementation of netscape specific LDAPSocketFactory. */
  private static class NetscapeLDAPSocketFactory implements LDAPSocketFactory
  {

    /** SSL socket factory to delegate to. */
    private SocketFactory factory;


    /**
     * Creates a new netscape ldap socket factory.
     *
     * @param  sf  ssl socket factory
     */
    public NetscapeLDAPSocketFactory(final SocketFactory sf)
    {
      factory = sf;
    }


    /** {@inheritDoc} */
    @Override
    public Socket makeSocket(final String host, final int port)
      throws LDAPException
    {
      try {
        return factory.createSocket(host, port);
      } catch (IOException e) {
        throw new LDAPException(e.getMessage());
      }
    }
  }
}