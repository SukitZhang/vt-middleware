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

import java.util.Arrays;
import edu.vt.middleware.ldap.handler.LdapResultHandler;

/**
 * Contains the data required to perform an ldap search operation.
 *
 * @author  Middleware Services
 * @version  $Revision: 1330 $ $Date: 2010-05-23 18:10:53 -0400 (Sun, 23 May 2010) $
 */
public class SearchRequest extends AbstractRequest
{

  /** hash code seed. */
  private static final int HASH_CODE_SEED = 93;

  /** DN to search. */
  private String baseDn = "";

  /** Search filter to execute. */
  private SearchFilter searchFilter;

  /** Attributes to return. */
  private String[] retAttrs;

  /** Search scope.*/
  private SearchScope searchScope = SearchScope.SUBTREE;

  /** Time search operation will block. */
  private long timeLimit;

  /** Number of entries to return. */
  private long sizeLimit;

  /** How to handle aliases. */
  private DerefAliases derefAliases;

  /** How to handle referrals. */
  private ReferralBehavior referralBehavior;

  /** Whether to return only attribute types. */
  private boolean typesOnly;

  /** Binary attribute names. */
  private String[] binaryAttrs;

  /** Sort behavior of results. */
  private SortBehavior sortBehavior = SortBehavior.getDefaultSortBehavior();

  /** Ldap result handlers. */
  private LdapResultHandler[] resultHandlers;


  /** Default constructor. */
  public SearchRequest() {}


  /**
   * Creates a new search request.
   *
   * @param  filter  search filter
   */
  public SearchRequest(final SearchFilter filter)
  {
    setSearchFilter(filter);
  }


  /**
   * Creates a new search request.
   *
   * @param  filter  search filter
   * @param  attrs  to return
   */
  public SearchRequest(final SearchFilter filter, final String[] attrs)
  {
    setSearchFilter(filter);
    setReturnAttributes(attrs);
  }


  /**
   * Creates a new search request.
   *
   * @param  filter  search filter
   * @param  attrs  to return
   * @param  handlers  ldap result handlers
   */
  public SearchRequest(
    final SearchFilter filter,
    final String[] attrs,
    final LdapResultHandler[] handlers)
  {
    setSearchFilter(filter);
    setReturnAttributes(attrs);
    setLdapResultHandlers(handlers);
  }


  /**
   * Creates a new search request.
   *
   * @param  dn  to search
   */
  public SearchRequest(final String dn)
  {
    setBaseDn(dn);
  }


  /**
   * Creates a new search request.
   *
   * @param  dn  to search
   * @param  filter  search filter
   */
  public SearchRequest(final String dn, final SearchFilter filter)
  {
    setBaseDn(dn);
    setSearchFilter(filter);
  }


  /**
   * Creates a new search request.
   *
   * @param  dn  to search
   * @param  filter  search filter
   * @param  attrs  to return
   */
  public SearchRequest(
    final String dn, final SearchFilter filter, final String[] attrs)
  {
    setBaseDn(dn);
    setSearchFilter(filter);
    setReturnAttributes(attrs);
  }


  /**
   * Creates a new search request.
   *
   * @param  dn  to search
   * @param  filter  search filter
   * @param  attrs  to return
   * @param  handlers  ldap result handlers
   */
  public SearchRequest(
    final String dn,
    final SearchFilter filter,
    final String[] attrs,
    final LdapResultHandler[] handlers)
  {
    setBaseDn(dn);
    setSearchFilter(filter);
    setReturnAttributes(attrs);
    setLdapResultHandlers(handlers);
  }


  /**
   * Returns the base DN.
   *
   * @return  base DN
   */
  public String getBaseDn()
  {
    return baseDn;
  }


  /**
   * Sets the base DN.
   *
   * @param  dn base DN
   */
  public void setBaseDn(final String dn)
  {
    baseDn = dn;
  }


  /**
   * Returns the search filter.
   *
   * @return  search filter
   */
  public SearchFilter getSearchFilter()
  {
    return searchFilter;
  }


  /**
   * Sets the search filter.
   *
   * @param  filter  search filter
   */
  public void setSearchFilter(final SearchFilter filter)
  {
    searchFilter = filter;
  }


  /**
   * Returns the search return attributes.
   *
   * @return  search return attributes
   */
  public String[] getReturnAttributes()
  {
    return retAttrs;
  }


  /**
   * Sets the search return attributes.
   *
   * @param  attrs  search return attributes
   */
  public void setReturnAttributes(final String[] attrs)
  {
    retAttrs = attrs;
  }


  /**
   * Gets the search scope.
   *
   * @return  search scope
   */
  public SearchScope getSearchScope()
  {
    return searchScope;
  }


  /**
   * Sets the search scope.
   *
   * @param  scope  search scope
   */
  public void setSearchScope(final SearchScope scope)
  {
    searchScope = scope;
  }


  /**
   * Returns the time limit.
   *
   * @return  time limit
   */
  public long getTimeLimit()
  {
    return timeLimit;
  }


  /**
   * Sets the time limit.
   *
   * @param  limit  time limit
   */
  public void setTimeLimit(final long limit)
  {
    timeLimit = limit;
  }


  /**
   * Returns the size limit.
   *
   * @return  size limit
   */
  public long getSizeLimit()
  {
    return sizeLimit;
  }


  /**
   * Sets the size limit.
   *
   * @param  limit  size limit
   */
  public void setSizeLimit(final long limit)
  {
    sizeLimit = limit;
  }


  /**
   * Returns how to dereference aliases.
   *
   * @return  how to dereference aliases
   */
  public DerefAliases getDerefAliases()
  {
    return derefAliases;
  }


  /**
   * Sets how to dereference aliases.
   *
   * @param  da  how to dereference aliases
   */
  public void setDerefAliases(final DerefAliases da)
  {
    derefAliases = da;
  }


  /**
   * Returns how to handle referrals.
   *
   * @return  how to handle referrals
   */
  public ReferralBehavior getReferralBehavior()
  {
    return referralBehavior;
  }


  /**
   * Sets how to handle referrals.
   *
   * @param  rb  how to handle referrals
   */
  public void setReferralBehavior(final ReferralBehavior rb)
  {
    referralBehavior = rb;
  }


  /**
   * Returns whether to return only attribute types.
   *
   * @return  whether to return only attribute types
   */
  public boolean getTypesOnly()
  {
    return typesOnly;
  }


  /**
   * Sets whether to return only attribute types.
   *
   * @param  b  whether to return only attribute types
   */
  public void setTypesOnly(final boolean b)
  {
    typesOnly = b;
  }


  /**
   * Returns names of binary attributes.
   *
   * @return  binary attribute names
   */
  public String[] getBinaryAttributes()
  {
    return binaryAttrs;
  }


  /**
   * Sets names of binary attributes.
   *
   * @param  attrs  binary attribute names
   */
  public void setBinaryAttributes(final String[] attrs)
  {
    binaryAttrs = attrs;
  }


  /**
   * Returns the sort behavior.
   *
   * @return  sort behavior
   */
  public SortBehavior getSortBehavior()
  {
    return sortBehavior;
  }


  /**
   * Sets the sort behavior.
   *
   * @param  sb  sort behavior
   */
  public void setSortBehavior(final SortBehavior sb)
  {
    sortBehavior = sb;
  }


  /**
   * Returns the ldap result handlers.
   *
   * @return  ldap result handlers
   */
  public LdapResultHandler[] getLdapResultHandlers()
  {
    return resultHandlers;
  }


  /**
   * Sets the ldap result handlers.
   *
   * @param  handlers  ldap result handlers
   */
  public void setLdapResultHandlers(final LdapResultHandler[] handlers)
  {
    resultHandlers = handlers;
  }


  /**
   * Returns a search request initialized with the supplied request.
   *
   * @param  request  search request to read properties from
   * @return  search request
   */
  public static SearchRequest newSearchRequest(final SearchRequest request)
  {
    final SearchRequest sr = new SearchRequest();
    sr.setBaseDn(request.getBaseDn());
    sr.setBinaryAttributes(request.getBinaryAttributes());
    sr.setDerefAliases(request.getDerefAliases());
    sr.setLdapResultHandlers(request.getLdapResultHandlers());
    sr.setReferralBehavior(request.getReferralBehavior());
    sr.setReturnAttributes(request.getReturnAttributes());
    sr.setSearchFilter(
      request.getSearchFilter() != null ?
        SearchFilter.newSearchFilter(request.getSearchFilter()) :
        null);
    sr.setSearchScope(request.getSearchScope());
    sr.setSizeLimit(request.getSizeLimit());
    sr.setSortBehavior(request.getSortBehavior());
    sr.setTimeLimit(request.getTimeLimit());
    sr.setTypesOnly(request.getTypesOnly());
    sr.setControls(request.getControls());
    return sr;
  }


  /**
   * Returns a search request initialized for use with an object level search
   * scope.
   *
   * @param  dn  of an ldap entry
   * @return  search request
   */
  public static SearchRequest newObjectScopeSearchRequest(final String dn)
  {
    return newObjectScopeSearchRequest(dn, null);
  }


  /**
   * Returns a search request initialized for use with an object level search
   * scope.
   *
   * @param  dn  of an ldap entry
   * @param attrs  to return
   * @return  search request
   */
  public static SearchRequest newObjectScopeSearchRequest(
    final String dn, final String[] attrs)
  {
    return newObjectScopeSearchRequest(
      dn, attrs, new SearchFilter("(objectClass=*)"));
  }


  /**
   * Returns a search request initialized for use with an object level search
   * scope.
   *
   * @param  dn  of an ldap entry
   * @param attrs  to return
   * @param filter  to execute on the ldap entry
   * @return  search request
   */
  public static SearchRequest newObjectScopeSearchRequest(
    final String dn, final String[] attrs, final SearchFilter filter)
  {
    final SearchRequest request = new SearchRequest();
    request.setBaseDn(dn);
    request.setSearchFilter(filter);
    request.setReturnAttributes(attrs);
    request.setSearchScope(SearchScope.OBJECT);
    return request;
  }


  /**
   * Returns whether the supplied object contains the same data as this request.
   * Delegates to {@link #hashCode()} implementation.
   *
   * @param  o  to compare for equality
   *
   * @return  equality result
   */
  public boolean equals(final Object o)
  {
    if (o == null) {
      return false;
    }
    return
      o == this ||
        (getClass() == o.getClass() && o.hashCode() == hashCode());
  }


  /** {@inheritDoc} */
  @Override
  public int hashCode()
  {
    int hc = HASH_CODE_SEED;
    hc += baseDn != null ? baseDn.hashCode() : 0;
    hc += binaryAttrs != null ? Arrays.hashCode(binaryAttrs) : 0;
    hc += derefAliases != null ? derefAliases.hashCode() : 0;
    hc += resultHandlers != null ? Arrays.hashCode(resultHandlers) : 0;
    hc += referralBehavior != null ? referralBehavior.hashCode() : 0;
    hc += retAttrs != null ? Arrays.hashCode(retAttrs) : 0;
    hc += searchFilter != null ? searchFilter.hashCode() : 0;
    hc += searchScope != null ? searchScope.hashCode() : 0;
    hc += sizeLimit;
    hc += sortBehavior != null ? sortBehavior.hashCode() : 0;
    hc += timeLimit;
    hc += Boolean.valueOf(typesOnly).hashCode();
    hc += getControls() != null ? Arrays.hashCode(getControls()) : 0;
    return hc;
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
        "[%s@%d::baseDn=%s, searchFilter=%s, returnAttributes=%s, " +
        "searchScope=%s, timeLimit=%s, sizeLimit=%s, derefAliases=%s, " +
        "referralBehavior=%s, typesOnly=%s, binaryAttributes=%s, " +
        "sortBehavior=%s, ldapResultHandlers=%s, controls=%s]",
        getClass().getName(),
        hashCode(),
        baseDn,
        searchFilter,
        retAttrs != null ? Arrays.asList(retAttrs) : null,
        searchScope,
        timeLimit,
        sizeLimit,
        derefAliases,
        referralBehavior,
        typesOnly,
        binaryAttrs != null ? Arrays.asList(binaryAttrs) : null,
        sortBehavior,
        resultHandlers != null ? Arrays.asList(resultHandlers) : null,
        getControls() != null ? Arrays.asList(getControls()) : null);
  }
}
