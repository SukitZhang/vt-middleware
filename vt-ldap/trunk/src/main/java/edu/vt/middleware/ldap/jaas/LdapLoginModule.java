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
package edu.vt.middleware.ldap.jaas;

import java.security.Principal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import com.sun.security.auth.callback.TextCallbackHandler;
import edu.vt.middleware.ldap.Credential;
import edu.vt.middleware.ldap.LdapEntry;
import edu.vt.middleware.ldap.LdapException;
import edu.vt.middleware.ldap.auth.AuthenticationRequest;
import edu.vt.middleware.ldap.auth.AuthenticationResponse;
import edu.vt.middleware.ldap.auth.Authenticator;

/**
 * Provides a JAAS authentication hook for LDAP authentication.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class LdapLoginModule extends AbstractLoginModule implements LoginModule
{

  /** User attribute to add to role data. */
  private String[] userRoleAttribute = new String[0];

  /** Factory for creating authenticators with JAAS options. */
  private AuthenticatorFactory authenticatorFactory;

  /** Authenticator to use against the LDAP. */
  private Authenticator auth;

  /** Authentication request to use for authentication. */
  private AuthenticationRequest authRequest;


  /** {@inheritDoc} */
  @Override
  public void initialize(
    final Subject subject,
    final CallbackHandler callbackHandler,
    final Map<String, ?> sharedState,
    final Map<String, ?> options)
  {
    setLdapPrincipal = true;
    setLdapCredential = true;

    super.initialize(subject, callbackHandler, sharedState, options);

    final Iterator<String> i = options.keySet().iterator();
    while (i.hasNext()) {
      final String key = i.next();
      final String value = (String) options.get(key);
      if ("userRoleAttribute".equalsIgnoreCase(key)) {
        if ("*".equals(value)) {
          userRoleAttribute = null;
        } else {
          userRoleAttribute = value.split(",");
        }
      } else if ("authenticatorFactory".equalsIgnoreCase(key)) {
        try {
          authenticatorFactory =
            (AuthenticatorFactory) Class.forName(value).newInstance();
        } catch (ClassNotFoundException e) {
          throw new IllegalArgumentException(e);
        } catch (InstantiationException e) {
          throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
          throw new IllegalArgumentException(e);
        }
      }
    }

    if (authenticatorFactory == null) {
      authenticatorFactory = new PropertiesAuthenticatorFactory();
    }

    logger.trace(
      "authenticatorFactory = {}, userRoleAttribute = {}",
      authenticatorFactory,
      Arrays.toString(userRoleAttribute));

    auth = authenticatorFactory.createAuthenticator(options);
    logger.debug("Retrieved authenticator from factory: {}", auth);

    authRequest = authenticatorFactory.createAuthenticationRequest(options);
    authRequest.setReturnAttributes(userRoleAttribute);
    logger.debug(
      "Retrieved authentication request from factory: {}",
      authRequest);
  }


  /** {@inheritDoc} */
  @Override
  public boolean login()
    throws LoginException
  {
    try {
      final NameCallback nameCb = new NameCallback("Enter user: ");
      final PasswordCallback passCb = new PasswordCallback(
        "Enter user password: ",
        false);
      getCredentials(nameCb, passCb, false);
      authRequest.setUser(nameCb.getName());
      authRequest.setCredential(new Credential(passCb.getPassword()));

      AuthenticationResponse response = auth.authenticate(authRequest);
      LdapEntry entry = null;
      if (response.getResult()) {
        entry = response.getLdapEntry();
        if (entry != null) {
          roles.addAll(LdapRole.toRoles(entry));
          if (defaultRole != null && !defaultRole.isEmpty()) {
            roles.addAll(defaultRole);
          }
        }
        loginSuccess = true;
      } else {
        if (tryFirstPass) {
          getCredentials(nameCb, passCb, true);
          response = auth.authenticate(authRequest);
          if (response.getResult()) {
            entry = response.getLdapEntry();
            if (entry != null) {
              roles.addAll(LdapRole.toRoles(entry));
            }
            if (defaultRole != null && !defaultRole.isEmpty()) {
              roles.addAll(defaultRole);
            }
            loginSuccess = true;
          } else {
            loginSuccess = false;
          }
        } else {
          loginSuccess = false;
        }
      }

      if (!loginSuccess) {
        logger.debug("Authentication failed: " + response);
        throw new LoginException("Authentication failed: " + response);
      } else {
        if (setLdapPrincipal) {
          final LdapPrincipal lp = new LdapPrincipal(nameCb.getName(), entry);
          principals.add(lp);
        }

        final String loginDn = auth.resolveDn(nameCb.getName());
        if (loginDn != null && setLdapDnPrincipal) {
          final LdapDnPrincipal lp = new LdapDnPrincipal(loginDn, entry);
          principals.add(lp);
        }
        if (setLdapCredential) {
          credentials.add(new LdapCredential(passCb.getPassword()));
        }
        storeCredentials(nameCb, passCb, loginDn);
      }
    } catch (LdapException e) {
      logger.debug("Error occured attempting authentication", e);
      loginSuccess = false;
      throw new LoginException(
        e != null ? e.getMessage() : "Authentication Error");
    }
    return true;
  }


  /**
   * This provides command line access to this JAAS module.
   *
   * @param  args  command line arguments
   *
   * @throws  Exception  if an error occurs
   */
  public static void main(final String[] args)
    throws Exception
  {
    String name = "vt-ldap";
    if (args.length > 0) {
      name = args[0];
    }

    final LoginContext lc = new LoginContext(name, new TextCallbackHandler());
    lc.login();
    System.out.println("Authentication/Authorization succeeded");

    final Set<Principal> principals = lc.getSubject().getPrincipals();
    System.out.println("Subject Principal(s): ");

    final Iterator<Principal> i = principals.iterator();
    while (i.hasNext()) {
      final Principal p = i.next();
      System.out.println("  " + p);
    }
    lc.logout();
  }
}
