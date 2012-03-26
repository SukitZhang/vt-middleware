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
package org.ldaptive.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.ldaptive.AbstractTest;
import org.ldaptive.TestUtils;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit test for ldap operation cli classes.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */
public class OperationCliTest extends AbstractTest
{


  /**
   * @param  args  list of delimited arguments to pass to the CLI
   *
   * @throws  Exception  On test failure
   */
  @Parameters("cliAddArgs")
  @BeforeClass(groups = {"ldapcli"})
  public void createLdapEntry(final String args)
    throws Exception
  {
    final PrintStream oldStdOut = System.out;
    try {
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outStream));

      AddOperationCli.main(args.split("\\|"));
    } finally {
      // Restore STDOUT
      System.setOut(oldStdOut);
    }
  }


  /**
   * @param  args  list of delimited arguments to pass to the CLI
   *
   * @throws  Exception  On test failure
   */
  @Parameters("cliDeleteArgs")
  @AfterClass(groups = {"ldapcli"})
  public void deleteLdapEntry(final String args)
    throws Exception
  {
    final PrintStream oldStdOut = System.out;
    try {
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outStream));

      DeleteOperationCli.main(args.split("\\|"));
    } finally {
      // Restore STDOUT
      System.setOut(oldStdOut);
    }
  }


  /**
   * @param  args  list of delimited arguments to pass to the CLI
   * @param  ldifFile  to compare with
   *
   * @throws  Exception  On test failure
   */
  @Parameters({ "cliSearchArgs", "cliSearchResults" })
  @Test(groups = {"ldapcli"})
  public void search(final String args, final String ldifFile)
    throws Exception
  {
    final String ldif = TestUtils.readFileIntoString(ldifFile);
    final PrintStream oldStdOut = System.out;
    try {
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outStream));

      SearchOperationCli.main(args.split("\\|"));
      AssertJUnit.assertEquals(
        TestUtils.convertLdifToResult(ldif),
        TestUtils.convertLdifToResult(outStream.toString()));
    } finally {
      // Restore STDOUT
      System.setOut(oldStdOut);
    }
  }


  /**
   * @param  args  list of delimited arguments to pass to the CLI
   *
   * @throws  Exception  On test failure.
   */
  @Parameters("cliCompareArgs")
  @Test(groups = {"ldapcli"})
  public void compare(final String args)
    throws Exception
  {
    final PrintStream oldStdOut = System.out;
    try {
      final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      System.setOut(new PrintStream(outStream));

      CompareOperationCli.main(args.split("\\|"));
      AssertJUnit.assertEquals(
        "true",
        outStream.toString().trim());
    } finally {
      // Restore STDOUT
      System.setOut(oldStdOut);
    }
  }
}
