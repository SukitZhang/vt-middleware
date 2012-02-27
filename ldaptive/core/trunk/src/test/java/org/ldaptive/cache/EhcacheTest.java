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
package org.ldaptive.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapResult;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchRequest;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit test for {@link Ehcache}.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class EhcacheTest
{

  /** Ehcache cache manager. */
  private CacheManager manager = CacheManager.create();

  /** Cache for testing. */
  private Ehcache<SearchRequest> cache;


  /**
   * @throws  Exception  On test failure.
   */
  @BeforeClass(groups = {"cache"})
  public void initialize()
    throws Exception
  {
    final net.sf.ehcache.Cache ehcache = new net.sf.ehcache.Cache(
      new CacheConfiguration("test", 5)
        .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
        .overflowToDisk(false)
        .eternal(false)
        .timeToLiveSeconds(0)
        .timeToIdleSeconds(0)
        .diskPersistent(false)
        .diskExpiryThreadIntervalSeconds(3));
    manager.addCache(ehcache);
    cache = new Ehcache<SearchRequest>(ehcache);
    fillCache();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @AfterClass(groups = {"cache"})
  public void clear()
    throws Exception
  {
    fillCache();
    AssertJUnit.assertEquals(5, cache.size());
    cache.clear();
    AssertJUnit.assertEquals(0, cache.size());
    manager.shutdown();
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(
    groups = {"cache"},
    threadPoolSize = 5,
    invocationCount = 100,
    timeOut = 60000
  )
  public void get()
    throws Exception
  {
    LdapResult lr = cache.get(
      new SearchRequest("dc=ldaptive,dc=org", new SearchFilter("uid=3")));
    AssertJUnit.assertEquals(
      new LdapResult(new LdapEntry("uid=3,ou=test,dc=ldaptive,dc=org")), lr);
    lr = cache.get(
      new SearchRequest("dc=ldaptive,dc=org", new SearchFilter("uid=4")));
    AssertJUnit.assertEquals(
      new LdapResult(new LdapEntry("uid=4,ou=test,dc=ldaptive,dc=org")), lr);
    lr = cache.get(
      new SearchRequest("dc=ldaptive,dc=org", new SearchFilter("uid=5")));
    AssertJUnit.assertEquals(
      new LdapResult(new LdapEntry("uid=5,ou=test,dc=ldaptive,dc=org")), lr);
  }


  /**
   * @throws  Exception  On test failure.
   */
  @Test(groups = {"cache"})
  public void put()
    throws Exception
  {
    AssertJUnit.assertEquals(5, cache.size());
    cache.put(
      new SearchRequest(
        "dc=ldaptive,dc=org", new SearchFilter("uid=%s", new Object[]{"101"})),
      new LdapResult(new LdapEntry("uid=101,ou=test,dc=ldaptive,dc=org")));
    cache.put(
      new SearchRequest("dc=ldaptive,dc=org", new SearchFilter("uid=102")),
      new LdapResult(new LdapEntry("uid=102,ou=test,dc=ldaptive,dc=org")));
    AssertJUnit.assertEquals(5, cache.size());

    LdapResult lr = cache.get(
      new SearchRequest(
        "dc=ldaptive,dc=org", new SearchFilter("uid=%s", new Object[]{"101"})));
    AssertJUnit.assertEquals(
      new LdapResult(new LdapEntry("uid=101,ou=test,dc=ldaptive,dc=org")), lr);
    lr = cache.get(
      new SearchRequest("dc=ldaptive,dc=org", new SearchFilter("uid=102")));
    AssertJUnit.assertEquals(
      new LdapResult(new LdapEntry("uid=102,ou=test,dc=ldaptive,dc=org")), lr);
    AssertJUnit.assertNull(
      cache.get(
        new SearchRequest("dc=ldaptive,dc=org", new SearchFilter("uid=1"))));
  }


  /**
   * Fills the cache with data.
   */
  private void fillCache()
  {
    cache.put(
      new SearchRequest("dc=ldaptive,dc=org", new SearchFilter("uid=1")),
      new LdapResult(new LdapEntry("uid=1,ou=test,dc=ldaptive,dc=org")));
    cache.put(
      new SearchRequest("dc=ldaptive,dc=org", new SearchFilter("uid=2")),
      new LdapResult(new LdapEntry("uid=2,ou=test,dc=ldaptive,dc=org")));
    cache.put(
      new SearchRequest("dc=ldaptive,dc=org", new SearchFilter("uid=3")),
      new LdapResult(new LdapEntry("uid=3,ou=test,dc=ldaptive,dc=org")));
    cache.put(
      new SearchRequest("dc=ldaptive,dc=org", new SearchFilter("uid=4")),
      new LdapResult(new LdapEntry("uid=4,ou=test,dc=ldaptive,dc=org")));
    cache.put(
      new SearchRequest("dc=ldaptive,dc=org", new SearchFilter("uid=5")),
      new LdapResult(new LdapEntry("uid=5,ou=test,dc=ldaptive,dc=org")));
    // ensure uid=1 and uid=2 get evicted first
    cache.get(
      new SearchRequest("dc=ldaptive,dc=org", new SearchFilter("uid=3")));
    cache.get(
      new SearchRequest("dc=ldaptive,dc=org", new SearchFilter("uid=4")));
    cache.get(
      new SearchRequest("dc=ldaptive,dc=org", new SearchFilter("uid=5")));
  }
}
