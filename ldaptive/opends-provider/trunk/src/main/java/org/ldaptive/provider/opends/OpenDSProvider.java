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
package org.ldaptive.provider.opends;

import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.LdapURL;
import org.ldaptive.provider.ConnectionFactory;
import org.ldaptive.provider.Provider;
import org.ldaptive.ssl.CredentialConfig;
import org.ldaptive.ssl.DefaultHostnameVerifier;
import org.ldaptive.ssl.DefaultSSLContextInitializer;
import org.ldaptive.ssl.HostnameVerifyingTrustManager;
import org.ldaptive.ssl.SSLContextInitializer;
import org.opends.sdk.LDAPOptions;

/**
 * OpenDS provider implementation. Provides connection factories for clear,
 * SSL, and TLS connections.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class OpenDSProvider implements Provider<OpenDSProviderConfig>
{

  /** Provider configuration. */
  private OpenDSProviderConfig config = new OpenDSProviderConfig();


  /** {@inheritDoc} */
  @Override
  public ConnectionFactory<OpenDSProviderConfig> getConnectionFactory(
    final ConnectionConfig cc)
  {
    config.makeImmutable();
    SSLContext sslContext = null;
    if (cc.getUseStartTLS() || cc.getUseSSL()) {
      SSLContextInitializer contextInit = null;
      if (cc.getSslConfig() != null &&
          cc.getSslConfig().getCredentialConfig() != null) {
        try {
          final CredentialConfig credConfig =
            cc.getSslConfig().getCredentialConfig();
          contextInit = credConfig.createSSLContextInitializer();
        } catch (GeneralSecurityException e) {
          throw new IllegalArgumentException(e);
        }
      } else {
        contextInit = new DefaultSSLContextInitializer();
      }
      if (cc.getSslConfig() != null &&
          cc.getSslConfig().getTrustManagers() != null) {
        contextInit.setTrustManagers(cc.getSslConfig().getTrustManagers());
      } else {
        final LdapURL ldapUrl = new LdapURL(cc.getLdapUrl());
        contextInit.setTrustManagers(
          new TrustManager[]{
            new HostnameVerifyingTrustManager(
              new DefaultHostnameVerifier(), ldapUrl.getEntriesAsString()), });
      }
      try {
        sslContext = contextInit.initSSLContext("TLS");
      } catch (GeneralSecurityException e) {
        throw new IllegalArgumentException(e);
      }
    }
    final LDAPOptions options = new LDAPOptions();
    if (cc.getUseStartTLS()) {
      options.setUseStartTLS(true);
    } else if (cc.getUseSSL()) {
      options.setUseStartTLS(false);
    }
    if (cc.getSslConfig() != null &&
        cc.getSslConfig().getEnabledCipherSuites() != null) {
      options.addEnabledCipherSuite(cc.getSslConfig().getEnabledCipherSuites());
    }
    if (cc.getSslConfig() != null &&
        cc.getSslConfig().getEnabledProtocols() != null) {
      options.addEnabledProtocol(cc.getSslConfig().getEnabledProtocols());
    }
    if (sslContext != null) {
      options.setSSLContext(sslContext);
    }
    options.setTimeout(cc.getResponseTimeout(), TimeUnit.MILLISECONDS);
    final ConnectionFactory<OpenDSProviderConfig> cf =
      new OpenDSConnectionFactory(cc.getLdapUrl(), options);
    cf.setProviderConfig(config);
    return cf;
  }


  /** {@inheritDoc} */
  @Override
  public OpenDSProviderConfig getProviderConfig()
  {
    return config;
  }


  /** {@inheritDoc} */
  @Override
  public void setProviderConfig(final OpenDSProviderConfig pc)
  {
    config = pc;
  }


  /** {@inheritDoc} */
  @Override
  public OpenDSProvider newInstance()
  {
    return new OpenDSProvider();
  }
}
