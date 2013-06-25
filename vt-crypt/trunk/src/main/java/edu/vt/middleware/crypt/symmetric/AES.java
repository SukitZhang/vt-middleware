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
package edu.vt.middleware.crypt.symmetric;

/**
 * Provider of symmetric encryption/decryption operations using AES cipher.
 *
 * @author  Middleware Services
 * @version  $Revision$
 */

public class AES extends SymmetricAlgorithm
{

  /** Algorithm name. */
  public static final String ALGORITHM = "AES";

  /** Available key lengths in bits. */
  public static final int[] KEY_LENGTHS = new int[] {
    256,
    192,
    128,
  };


  /**
   * Creates a default AES symmetric encryption algorithm using CBC mode and
   * PKCS5 padding.
   */
  public AES()
  {
    this(DEFAULT_MODE, DEFAULT_PADDING);
  }


  /**
   * Creates a default AES symmetric encryption algorithm using the given mode
   * and padding style.
   *
   * @param  mode  Cipher mode name.
   * @param  padding  Cipher padding style name.
   */
  public AES(final String mode, final String padding)
  {
    super(ALGORITHM, mode, padding);
  }


  /** {@inheritDoc} */
  public int[] getAllowedKeyLengths()
  {
    return KEY_LENGTHS;
  }
}
