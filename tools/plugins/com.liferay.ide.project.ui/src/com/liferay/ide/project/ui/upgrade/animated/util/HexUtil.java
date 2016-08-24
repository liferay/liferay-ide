/*
 * Copyright (c) 2014, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package com.liferay.ide.project.ui.upgrade.animated.util;

import java.io.IOException;
import java.util.Locale;

/**
 * Provides static methods that convert to and from hexadecimal string formats.
 *
 * @author Eike Stepper
 */
public final class HexUtil
{
  private HexUtil()
  {
  }

  public static String bytesToHex(byte[] bs)
  {
    if (bs == null)
    {
      return null;
    }

    return bytesToHex(bs, 0, bs.length);
  }

  /**
   * Converts a byte array into a string of lower case hex chars.
   *
   * @param bs
   *          A byte array
   * @param off
   *          The index of the first byte to read
   * @param length
   *          The number of bytes to read.
   * @return the string of hex chars.
   */
  private static String bytesToHex(byte[] bs, int off, int length)
  {
    if (bs == null)
    {
      return null;
    }

    if (bs.length <= off || bs.length < off + length)
    {
      throw new IllegalArgumentException();
    }

    StringBuilder sb = new StringBuilder(length * 2);
    bytesToHexAppend(bs, off, length, sb);
    return sb.toString();
  }

  private static void bytesToHexAppend(byte[] bs, int off, int length, Appendable appendable)
  {
    if (bs.length <= off || bs.length < off + length)
    {
      throw new IllegalArgumentException();
    }

    if (appendable instanceof StringBuffer)
    {
      StringBuffer buffer = (StringBuffer)appendable;
      buffer.ensureCapacity(buffer.length() + length * 2);
    }

    try
    {
      for (int i = off; i < off + length; i++)
      {
        appendable.append(Character.forDigit(bs[i] >>> 4 & 0xf, 16));
        appendable.append(Character.forDigit(bs[i] & 0xf, 16));
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  public static byte[] hexToBytes(String s)
  {
    return hexToBytes(s, 0);
  }

  private static byte[] hexToBytes(String s, int off)
  {
    byte[] bs = new byte[off + (1 + s.length()) / 2];
    hexToBytes(s, bs, off);
    return bs;
  }

  /**
   * Converts a String of hex characters into an array of bytes.
   *
   * @param s
   *          A string of hex characters (upper case or lower) of even length.
   * @param out
   *          A byte array of length at least s.length()/2 + off
   * @param off
   *          The first byte to write of the array
   */
  private static void hexToBytes(String s, byte[] out, int off) throws NumberFormatException, IndexOutOfBoundsException
  {
    int slen = s.length();
    if ((slen & 1) == 1)
    {
      s = '0' + s;
    }

    if (out.length < off + (slen >> 1))
    {
      throw new IndexOutOfBoundsException("Output buffer too small for input (" + out.length + '<' + off + (slen >> 1) //$NON-NLS-1$
          + ')');
    }

    // Safe to assume the string is even length
    byte b1, b2;
    for (int i = 0; i < slen; i += 2)
    {
      b1 = (byte)Character.digit(s.charAt(i), 16);
      b2 = (byte)Character.digit(s.charAt(i + 1), 16);
      if (b1 < 0 || b2 < 0)
      {
        throw new NumberFormatException(s);
      }

      out[off + i / 2] = (byte)(b1 << 4 | b2);
    }
  }

  public static String charToHex(char ch)
  {
    return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
  }

  public static char hexToChar(String s)
  {
    return (char)Integer.parseInt(s, 16);
  }
}
