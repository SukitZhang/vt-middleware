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
package org.ldaptive.provider.jldap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPModification;
import com.novell.ldap.controls.LDAPSortKey;
import com.novell.ldap.util.Base64;
import org.ldaptive.AttributeModification;
import org.ldaptive.AttributeModificationType;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.OperationException;
import org.ldaptive.ResultCode;
import org.ldaptive.SortBehavior;
import org.ldaptive.control.SortKey;

/**
 * Provides methods for converting between JLDAP specific objects and ldaptive
 * specific objects.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class JLdapUtil
{

  /** Ldap result sort behavior. */
  private SortBehavior sortBehavior;

  /** Attributes that should be treated as binary. */
  private List<String> binaryAttrs;


  /** Default constructor. */
  public JLdapUtil()
  {
    sortBehavior = SortBehavior.getDefaultSortBehavior();
  }


  /**
   * Creates a new jldap util.
   *
   * @param  sb  sort behavior
   */
  public JLdapUtil(final SortBehavior sb)
  {
    sortBehavior = sb;
  }


  /**
   * Returns the list of binary attributes.
   *
   * @return  list of binary attributes
   */
  public List<String> getBinaryAttributes()
  {
    return binaryAttrs;
  }


  /**
   * Sets the list of binary attributes.
   *
   * @param  s  binary attributes
   */
  public void setBinaryAttributes(final String[] s)
  {
    if (s != null) {
      binaryAttrs = Arrays.asList(s);
    }
  }


  /**
   * Returns a jldap attribute that represents the values in the supplied ldap
   * attribute.
   *
   * @param  la  ldap attribute
   *
   * @return  jldap attribute
   */
  public LDAPAttribute fromLdapAttribute(final LdapAttribute la)
  {
    final LDAPAttribute attr = new LDAPAttribute(la.getName());
    if (la.isBinary()) {
      for (byte[] value : la.getBinaryValues()) {
        attr.addValue(value);
      }
    } else {
      for (String value : la.getStringValues()) {
        attr.addValue(value);
      }
    }
    return attr;
  }


  /**
   * Returns an ldap attribute using the supplied jldap attribute.
   *
   * @param  a  jldap attribute
   *
   * @return  ldap attribute
   */
  public LdapAttribute toLdapAttribute(final LDAPAttribute a)
  {
    boolean isBinary = false;
    if (a.getName().indexOf(";binary") != -1) {
      isBinary = true;
    } else if (binaryAttrs != null && binaryAttrs.contains(a.getName())) {
      isBinary = true;
    }

    if (!isBinary) {
      // check if first value needs to be encoded
      isBinary = !Base64.isLDIFSafe(a.getStringValue());
    }

    LdapAttribute la = null;
    if (isBinary) {
      la = new LdapAttribute(sortBehavior, true);
      la.setName(a.getName());
      for (byte[] b : a.getByteValueArray()) {
        la.addBinaryValue(b);
      }
    } else {
      la = new LdapAttribute(sortBehavior, false);
      la.setName(a.getName());
      for (String s : a.getStringValueArray()) {
        la.addStringValue(s);
      }
    }
    return la;
  }


  /**
   * Returns a jldap attribute set that represents the values in the supplied
   * ldap attributes.
   *
   * @param  c  ldap attributes
   *
   * @return  jldap attributes
   */
  public LDAPAttributeSet fromLdapAttributes(final Collection<LdapAttribute> c)
  {
    final LDAPAttributeSet attributes = new LDAPAttributeSet();
    for (LdapAttribute a : c) {
      attributes.add(fromLdapAttribute(a));
    }
    return attributes;
  }


  /**
   * Returns a jldap ldap entry that represents the supplied ldap entry.
   *
   * @param  le  ldap entry
   *
   * @return  jldap ldap entry
   */
  public LDAPEntry fromLdapEntry(final LdapEntry le)
  {
    return new LDAPEntry(le.getDn(), fromLdapAttributes(le.getAttributes()));
  }


  /**
   * Returns an ldap entry using the supplied jldap ldap entry.
   *
   * @param  entry  jldap ldap entry
   *
   * @return  ldap entry
   */
  @SuppressWarnings("unchecked")
  public LdapEntry toLdapEntry(final LDAPEntry entry)
  {
    final LdapEntry le = new LdapEntry(sortBehavior);
    le.setDn(entry.getDN());

    final Iterator<LDAPAttribute> i = entry.getAttributeSet().iterator();
    while (i.hasNext()) {
      le.addAttribute(toLdapAttribute(i.next()));
    }
    return le;
  }


  /**
   * Returns jldap ldap modifications using the supplied attribute
   * modifications.
   *
   * @param  am  attribute modifications
   *
   * @return  jldap ldap modifications
   */
  public LDAPModification[] fromAttributeModification(
    final AttributeModification[] am)
  {
    final LDAPModification[] mods = new LDAPModification[am.length];
    for (int i = 0; i < am.length; i++) {
      mods[i] = new LDAPModification(
        getAttributeModification(am[i].getAttributeModificationType()),
        fromLdapAttribute(am[i].getAttribute()));
    }
    return mods;
  }


  /**
   * Determines whether to throw operation exception or ldap exception. If
   * operation exception is thrown, the operation will be retried. Otherwise the
   * exception is propagated out.
   *
   * @param  operationRetryResultCodes  to compare result code against
   * @param  e  ldap exception to examine
   *
   * @throws  OperationException  if the operation should be retried
   * @throws  LdapException  to propagate the exception out
   */
  public static void throwOperationException(
    final ResultCode[] operationRetryResultCodes,
    final LDAPException e)
    throws LdapException
  {
    if (
      operationRetryResultCodes != null &&
        operationRetryResultCodes.length > 0) {
      for (ResultCode rc : operationRetryResultCodes) {
        if (rc.value() == e.getResultCode()) {
          throw new OperationException(
            e,
            ResultCode.valueOf(e.getResultCode()));
        }
      }
    }
    throw new LdapException(e, ResultCode.valueOf(e.getResultCode()));
  }


  /**
   * Returns jldap sort keys using the supplied sort keys.
   *
   * @param  sk  sort keys
   *
   * @return  jldap sort keys
   */
  public static LDAPSortKey[] fromSortKey(final SortKey[] sk)
  {
    LDAPSortKey[] keys = null;
    if (sk != null) {
      keys = new LDAPSortKey[sk.length];
      for (int i = 0; i < sk.length; i++) {
        keys[i] = new LDAPSortKey(
          sk[i].getAttributeDescription(),
          sk[i].getReverseOrder(),
          sk[i].getMatchingRuleId());
      }
    }
    return keys;
  }


  /**
   * Returns the jldap modification integer constant for the supplied attribute
   * modification type.
   *
   * @param  am  attribute modification type
   *
   * @return  integer constant
   */
  protected static int getAttributeModification(
    final AttributeModificationType am)
  {
    int op = -1;
    if (am == AttributeModificationType.ADD) {
      op = LDAPModification.ADD;
    } else if (am == AttributeModificationType.REMOVE) {
      op = LDAPModification.DELETE;
    } else if (am == AttributeModificationType.REPLACE) {
      op = LDAPModification.REPLACE;
    }
    return op;
  }
}