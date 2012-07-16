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
package org.ldaptive;

import org.ldaptive.cache.Cache;
import org.ldaptive.provider.SearchIterator;

/**
 * Executes an ldap search operation.
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class SearchOperation extends AbstractSearchOperation<SearchRequest>
{


  /**
   * Creates a new search operation.
   *
   * @param  conn  connection
   */
  public SearchOperation(final Connection conn)
  {
    super(conn, null);
  }


  /**
   * Creates a new search operation.
   *
   * @param  conn  connection
   * @param  c  cache
   */
  public SearchOperation(final Connection conn, final Cache<SearchRequest> c)
  {
    super(conn, c);
  }


  /** {@inheritDoc} */
  @Override
  protected Response<SearchResult> executeSearch(final SearchRequest request)
    throws LdapException
  {
    final SearchIterator si = getConnection().getProviderConnection().search(
      request);
    final SearchResult result = readResult(request, si);
    final Response<Void> response = si.getResponse();
    return
      new Response<SearchResult>(
        result,
        response.getResultCode(),
        response.getMessage(),
        response.getMatchedDn(),
        response.getControls(),
        response.getReferralURLs(),
        response.getMessageId());
  }
}
