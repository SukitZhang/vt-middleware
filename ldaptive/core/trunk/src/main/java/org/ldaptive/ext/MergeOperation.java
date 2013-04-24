/*
  $Id$

  Copyright (C) 2003-2013 Virginia Tech.
  All rights reserved.

  SEE LICENSE FOR MORE INFORMATION

  Author:  Middleware Services
  Email:   middleware@vt.edu
  Version: $Revision$
  Updated: $Date$
*/
package org.ldaptive.ext;

import java.util.Arrays;
import java.util.List;
import org.ldaptive.AbstractOperation;
import org.ldaptive.AddOperation;
import org.ldaptive.AddRequest;
import org.ldaptive.AttributeModification;
import org.ldaptive.Connection;
import org.ldaptive.DeleteOperation;
import org.ldaptive.DeleteRequest;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ModifyOperation;
import org.ldaptive.ModifyRequest;
import org.ldaptive.Response;
import org.ldaptive.ResultCode;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;

/**
 * The merge operation performs the LDAP operations necessary to synchronize the
 * data in an {@link LdapEntry} with it's corresponding entry in the LDAP. The
 * following logic is executed:
 *
 * <ul>
 *   <li>if the entry does not exist in the LDAP, execute an add</li>
 *   <li>if the request is for a delete, execute a delete</li>
 *   <li>if the entry exists in the LDAP, execute a modify</li>
 * </ul>
 * <p>{@link LdapEntry#computeModifications(LdapEntry, LdapEntry)} is used to
 * determine the list of attribute modifications that are necessary to perform
 * the merge.</p>
 *
 * @author  Middleware Services
 * @version  $Revision$ $Date$
 */
public class MergeOperation extends AbstractOperation<MergeRequest, Void>
{


  /**
   * Creates a new merge operation.
   *
   * @param  conn  connection
   */
  public MergeOperation(final Connection conn)
  {
    super(conn);
  }


  /**
   * Executes the ldap operation necessary to perform a merge. One of {@link
   * AddOperation}, {@link ModifyOperation}, or {@link DeleteOperation}.
   *
   * @param  request  merge request
   *
   * @return  response associated with whatever underlying operation was
   * performed by the merge
   *
   * @throws  LdapException  if the invocation fails
   */
  @Override
  protected Response<Void> invoke(final MergeRequest request)
    throws LdapException
  {
    final LdapEntry sourceEntry = request.getEntry();
    Response<Void> response = null;

    // search for existing entry
    Response<SearchResult> searchResponse = null;
    try {
      final SearchOperation search = new SearchOperation(getConnection());
      searchResponse = search.execute(
        SearchRequest.newObjectScopeSearchRequest(sourceEntry.getDn()));
    } catch (LdapException e) {
      if (e.getResultCode() != ResultCode.NO_SUCH_OBJECT) {
        throw e;
      }
    }
    if (searchResponse != null &&
        searchResponse.getResultCode() != ResultCode.SUCCESS) {
      throw new LdapException(
        String.format(
          "Error searching for entry: %s, response did not return success: %s",
          sourceEntry,
          searchResponse));
    }

    if (searchResponse == null || searchResponse.getResult().size() == 0) {
      if (request.getDeleteEntry()) {
        logger.info(
          "target entry does not exist, no delete performed for request {}",
          request);
        response = new Response<Void>(null, ResultCode.SUCCESS);
      } else {
        // entry does not exist, add it
        response = add(request, sourceEntry);
      }
    } else if (request.getDeleteEntry()) {
      // delete entry
      response = delete(request, sourceEntry);
    } else {
      // entry exists, merge attributes
      response = modify(
        request,
        sourceEntry,
        searchResponse.getResult().getEntry());
    }
    return response;
  }


  /**
   * Retrieves the attribute modifications from {@link
   * LdapEntry#computeModifications(LdapEntry, LdapEntry)} and executes a {@link
   * ModifyOperation} with those results. If no modifications are necessary, no
   * operation is performed.
   *
   * @param  request  merge request
   * @param  source  ldap entry to merge into the LDAP
   * @param  target  ldap entry that exists in the LDAP
   *
   * @return  response of the modify operation or an empty response if no
   * operation is necessary
   *
   * @throws  LdapException  if an error occurs executing the modify operation
   */
  protected Response<Void> modify(
    final MergeRequest request,
    final LdapEntry source,
    final LdapEntry target)
    throws LdapException
  {
    Response<Void> response = null;
    final AttributeModification[] modifications =
      LdapEntry.computeModifications(source, target);
    if (modifications != null && modifications.length > 0) {
      boolean doModify = false;
      final String[] ignoreAttrs = request.getIgnoreAttributes();
      if (ignoreAttrs != null && ignoreAttrs.length > 0) {
        final List<String> l = Arrays.asList(ignoreAttrs);
        for (AttributeModification am : modifications) {
          if (!l.contains(am.getAttribute().getName())) {
            doModify = true;
            break;
          }
        }
      } else {
        doModify = true;
      }
      if (doModify) {
        final ModifyOperation modify = new ModifyOperation(getConnection());
        response = modify.execute(
          new ModifyRequest(target.getDn(), modifications));
        logger.info(
          "modified target entry {} with modifications {} from source entry " +
          "{} for request {}",
          target,
          modifications,
          source,
          request);
        return response;
      }
    }
    response = new Response<Void>(null, ResultCode.SUCCESS);
    logger.info(
      "target entry {} equals source entry {}, no modification performed for " +
      "request {}",
      target,
      source,
      request);
    return response;
  }


  /**
   * Executes an {@link AddOperation} for the supplied entry.
   *
   * @param  request  merge request
   * @param  entry  to add to the LDAP
   *
   * @return  response of the add operation
   *
   * @throws  LdapException  if an error occurs executing the add operation
   */
  protected Response<Void> add(
    final MergeRequest request,
    final LdapEntry entry)
    throws LdapException
  {
    Response<Void> response = null;
    final AddOperation add = new AddOperation(getConnection());
    response = add.execute(
      new AddRequest(entry.getDn(), entry.getAttributes()));
    logger.info("added entry {} for request {}", entry, request);
    return response;
  }


  /**
   * Executes a {@link DeleteOperation} for the supplied entry.
   *
   * @param  request  merge request
   * @param  entry  to delete from the LDAP
   *
   * @return  response of the delete operation
   *
   * @throws  LdapException  if an error occurs executing the deleting operation
   */
  protected Response<Void> delete(
    final MergeRequest request,
    final LdapEntry entry)
    throws LdapException
  {
    Response<Void> response = null;
    final DeleteOperation delete = new DeleteOperation(getConnection());
    response = delete.execute(new DeleteRequest(entry.getDn()));
    logger.info("delete entry {} for request {}", entry, request);
    return response;
  }
}