/*
 * Copyright (c) 2014, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Yatta Solutions - [466264] Enhance UX in simple installer
 */
package com.liferay.ide.project.ui.upgrade.animated.util;

import org.eclipse.emf.common.util.URI;

import java.util.ArrayList;
import java.util.List;

/**
 * Various static helper methods for dealing with strings.
 *
 * @author Eike Stepper
 */
public final class StringUtil
{
  public static final String EMPTY = ""; //$NON-NLS-1$

  public static final String NL = PropertiesUtil.getProperty("line.separator"); //$NON-NLS-1$

  public static String HORIZONTAL_ELLIPSIS = "\u2026";

  private StringUtil()
  {
  }

  public static String escape(String str)
  {
    if (str == null)
    {
      return null;
    }

    int len = str.length();
    StringBuilder builder = new StringBuilder(len);

    for (int i = 0; i < len; i++)
    {
      char c = str.charAt(i);

      if (c > 0xfff)
      {
        builder.append("\\u" + HexUtil.charToHex(c));
      }
      else if (c > 0xff)
      {
        builder.append("\\u0" + HexUtil.charToHex(c));
      }
      else if (c > 0x7f)
      {
        builder.append("\\u00" + HexUtil.charToHex(c));
      }
      else if (c < 32)
      {
        switch (c)
        {
          case '\r':
            builder.append('\\');
            builder.append('r');
            break;

          case '\n':
            builder.append('\\');
            builder.append('n');
            break;

          case '\t':
            builder.append('\\');
            builder.append('t');
            break;

          case '\f':
            builder.append('\\');
            builder.append('f');
            break;

          case '\b':
            builder.append('\\');
            builder.append('b');
            break;

          default:
            if (c > 0xf)
            {
              builder.append("\\u00" + HexUtil.charToHex(c));
            }
            else
            {
              builder.append("\\u000" + HexUtil.charToHex(c));
            }
        }
      }
      else if (c == '\\')
      {
        builder.append('\\');
        builder.append('\\');
      }
      else
      {
        builder.append(c);
      }
    }

    return builder.toString();
  }

  public static String unescape(String str)
  {
    if (str == null)
    {
      return null;
    }

    int len = str.length();
    StringBuilder builder = new StringBuilder(len);

    StringBuilder unicodeBuilder = new StringBuilder(4);
    boolean unicode = false;
    boolean slash = false;

    for (int i = 0; i < len; i++)
    {
      char c = str.charAt(i);
      if (unicode)
      {
        unicodeBuilder.append(c);
        if (unicodeBuilder.length() == 4)
        {
          try
          {
            char value = HexUtil.hexToChar(unicodeBuilder.toString());
            builder.append(value);
            unicodeBuilder.setLength(0);
            unicode = false;
            slash = false;
          }
          catch (NumberFormatException ex)
          {
            builder.append('\\');
            builder.append('u');
            builder.append(unicodeBuilder);
          }
        }

        continue;
      }

      if (slash)
      {
        slash = false;

        switch (c)
        {
          case '\\':
            builder.append('\\');
            break;

          case 'r':
            builder.append('\r');
            break;

          case 'n':
            builder.append('\n');
            break;

          case 't':
            builder.append('\t');
            break;

          case 'f':
            builder.append('\f');
            break;

          case 'b':
            builder.append('\b');
            break;

          case 'u':
            unicode = true;
            break;

          case '0':
          case '1':
          case '2':
          case '3':
            if (i + 2 < len && str.charAt(i + 1) >= '0' && str.charAt(i + 1) <= '7' && str.charAt(i + 2) >= '0' && str.charAt(i + 2) <= '7')
            {
              builder.append((char)Integer.parseInt(str.substring(i, i + 3), 8));
              i += 2;
              continue;
            }

            //$FALL-THROUGH$
          default:
          {
            builder.append(c);
          }
        }

        continue;
      }
      else if (c == '\\')
      {
        slash = true;
        continue;
      }

      builder.append(c);
    }

    if (slash)
    {
      builder.append('\\');
    }

    return builder.toString();
  }

  public static boolean isEmpty(String str)
  {
    return str == null || str.length() == 0;
  }

  public static String safe(String str)
  {
    return safe(str, EMPTY);
  }

  private static String safe(String str, String def)
  {
    if (str == null)
    {
      return def;
    }

    return str;
  }

  public static String cap(String str)
  {
    if (str == null || str.length() == 0)
    {
      return str;
    }

    char first = str.charAt(0);
    if (Character.isUpperCase(first))
    {
      return str;
    }

    if (str.length() == 1)
    {
      return str.toUpperCase();
    }

    StringBuilder builder = new StringBuilder(str);
    builder.setCharAt(0, Character.toUpperCase(first));
    return builder.toString();
  }

  public static String capAll(String str)
  {
    if (str == null || str.length() == 0)
    {
      return str;
    }

    boolean inWhiteSpace = true;
    StringBuilder builder = new StringBuilder(str);
    for (int i = 0; i < builder.length(); i++)
    {
      char c = builder.charAt(i);
      boolean isWhiteSpace = Character.isWhitespace(c);
      if (!isWhiteSpace && inWhiteSpace)
      {
        builder.setCharAt(i, Character.toUpperCase(c));
      }

      inWhiteSpace = isWhiteSpace;
    }

    return builder.toString();
  }

  public static String uncap(String str)
  {
    if (str == null || str.length() == 0)
    {
      return str;
    }

    char first = str.charAt(0);
    if (Character.isLowerCase(first))
    {
      return str;
    }

    if (str.length() == 1)
    {
      return str.toLowerCase();
    }

    StringBuilder builder = new StringBuilder(str);
    builder.setCharAt(0, Character.toLowerCase(first));
    return builder.toString();
  }

  public static String uncapAll(String str)
  {
    if (str == null || str.length() == 0)
    {
      return str;
    }

    boolean inWhiteSpace = true;
    StringBuilder builder = new StringBuilder(str);
    for (int i = 0; i < builder.length(); i++)
    {
      char c = builder.charAt(i);
      boolean isWhiteSpace = Character.isWhitespace(c);
      if (!isWhiteSpace && inWhiteSpace)
      {
        builder.setCharAt(i, Character.toLowerCase(c));
      }

      inWhiteSpace = isWhiteSpace;
    }

    return builder.toString();
  }

  public static List<String> explode(String string, String separators)
  {
    return explode(string, separators, '\\');
  }

  public static List<String> explode(String string, String separators, char escapeCharacter)
  {
    List<String> tokens = new ArrayList<String>();

    StringBuilder builder = new StringBuilder();
    boolean separator = false;
    boolean escape = false;

    for (int i = 0; i < string.length(); i++)
    {
      separator = false;
      char c = string.charAt(i);
      if (!escape && c == escapeCharacter)
      {
        escape = true;
      }
      else
      {
        if (!escape && separators.indexOf(c) != -1)
        {
          tokens.add(builder.toString());
          builder.setLength(0);
          separator = true;
        }
        else
        {
          builder.append(c);
        }

        escape = false;
      }
    }

    if (separator || builder.length() != 0)
    {
      tokens.add(builder.toString());
    }

    return tokens;
  }

  public static String implode(List<String> tokens, char separator)
  {
    return implode(tokens, separator, '\\');
  }

  public static String implode(List<String> tokens, char separator, char escapeCharacter)
  {
    String escapeString = Character.toString(escapeCharacter);
    String escapeString2 = escapeString + escapeString;

    String separatorString = Character.toString(separator);
    String separatorString2 = escapeString + separatorString;

    StringBuilder builder = new StringBuilder();
    boolean firstTime = true;

    for (String token : tokens)
    {
      if (firstTime)
      {
        firstTime = false;
      }
      else
      {
        builder.append(separator);
      }

      if (token != null)
      {
        token = token.replace(escapeString, escapeString2);
        token = token.replace(separatorString, separatorString2);
        builder.append(token);
      }
    }

    return builder.toString();
  }

  public static String toOSString(String uri)
  {
    if (!isEmpty(uri))
    {
      URI emfURI = URI.createURI(uri);
      if (emfURI.isFile())
      {
        uri = emfURI.toFileString();
      }
    }

    return uri;
  }

  /**
   * Shortens the given text to be as long as the given length (including the
   * appended ellipsis).
   *
   * @param input The text to shorten.
   * @param length The maximum length of the resulting text, ellipsis included.
   * @param wholeWord Whether to take care for splitting the text at word
   * boundaries only.
   */
  public static String shorten(String input, int length, boolean wholeWord)
  {
    if (input == null)
    {
      return EMPTY;
    }

    if (length <= HORIZONTAL_ELLIPSIS.length())
    {
      throw new IllegalArgumentException("Length must at least " + HORIZONTAL_ELLIPSIS.length() + 1);
    }

    if (input.length() <= length)
    {
      return input;
    }

    int ellipsisPos = length - HORIZONTAL_ELLIPSIS.length() - 1;

    if (wholeWord)
    {
      ellipsisPos = findLastSpaceBetween(input, 0, ellipsisPos);
    }

    String result = input.substring(0, ellipsisPos);
    result += HORIZONTAL_ELLIPSIS;
    return result;
  }

  public static String wrapText(String input, int maxCharactersPerLine, boolean wholeWord)
  {
    int startIndex = 0;
    int endIndex = startIndex + maxCharactersPerLine;
    boolean finished = false;

    StringBuilder builder = new StringBuilder();

    do
    {
      if (endIndex >= input.length())
      {
        endIndex = input.length();
        finished = true;
      }

      if (!finished && wholeWord)
      {
        int spaceIndex = findLastSpaceBetween(input, startIndex, endIndex);
        if (spaceIndex > 0)
        {
          endIndex = spaceIndex;
        }
        else
        {
          // No more spaces till end.
          endIndex = input.length();
          finished = true;
        }
      }

      builder.append(input.substring(startIndex, endIndex));

      if (!finished)
      {
        builder.append(StringUtil.NL);
      }

      startIndex = wholeWord ? endIndex + 1 : endIndex;
      endIndex += maxCharactersPerLine;

    } while (!finished);

    return builder.toString();
  }

  private static int findLastSpaceBetween(String text, int startPosition, int endPosition)
  {
    int index = endPosition;
    char lastBeforeEllipsis = text.charAt(index);

    while (lastBeforeEllipsis != ' ')
    {
      index--;
      if (index <= startPosition)
      {
        index = -1;
        break;
      }

      lastBeforeEllipsis = text.charAt(index);
    }

    return index;
  }

  public static String removePrefix(String string, String... prefixes)
  {
    for (int i = 0; i < prefixes.length; i++)
    {
      String prefix = prefixes[i];
      if (string.startsWith(prefix))
      {
        return string.substring(prefix.length());
      }
    }

    return string;
  }

  public static String removeSuffix(String string, String... suffixes)
  {
    for (int i = 0; i < suffixes.length; i++)
    {
      String suffix = suffixes[i];
      if (string.endsWith(suffix))
      {
        return string.substring(0, string.length() - suffix.length());
      }
    }

    return string;
  }

  public static String trimLeft(String str)
  {
    int len = str.length();
    int st = 0;

    while (st < len && str.charAt(st) <= ' ')
    {
      st++;
    }

    if (st == 0)
    {
      return str;
    }

    return str.substring(st, len);
  }

  public static String trimRight(String str)
  {
    int len = str.length();
    int end = len;

    while (0 < end && str.charAt(end - 1) <= ' ')
    {
      end--;
    }

    if (end == len)
    {
      return str;
    }

    return str.substring(0, end);
  }

  public static String trimTrailingSlashes(String str)
  {
    int len = str.length();
    int end = len;

    while (end > 0)
    {
      char c = str.charAt(end - 1);
      if (c != '/' && c != '\\')
      {
        break;
      }

      --end;
    }

    if (end == len)
    {
      return str;
    }

    return str.substring(0, end);
  }
}
