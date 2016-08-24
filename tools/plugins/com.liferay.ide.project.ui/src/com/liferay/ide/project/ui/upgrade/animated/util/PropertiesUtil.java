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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

/**
 * @author Eike Stepper
 */
public final class PropertiesUtil
{
  public static final String[] EXPERT_FILTER = { "org.eclipse.ui.views.properties.expert" };

  private static final String TRUE = Boolean.TRUE.toString();

  private PropertiesUtil()
  {
  }

  public static Boolean getBoolean(String key)
  {
    String property = getProperty(key);
    if (property != null)
    {
      return TRUE.equalsIgnoreCase(property);
    }

    return null;
  }

  public static boolean isProperty(String key)
  {
    String property = getProperty(key);
    if (property != null)
    {
      return TRUE.equalsIgnoreCase(property);
    }

    return false;
  }

  public static String getProperty(String key)
  {
    return getProperty(key, null);
  }

  public static String getProperty(String key, String defaultValue)
  {
    String value = System.getProperty(key);
    if (value == null)
    {
      value = System.getenv(key);
      if (value == null && key.indexOf('.') != -1)
      {
        key = key.replace('.', '_');
        value = getProperty(key, defaultValue);
      }
    }

    if (value == null)
    {
      value = defaultValue;
    }

    return value;
  }

  public static int getProperty(String key, int defaultValue)
  {
    try
    {
      String property = getProperty(key);
      return Integer.parseInt(property);
    }
    catch (Exception ex)
    {
      return defaultValue;
    }
  }

  public static String getUserHome()
  {
    return getProperty("user.home", ".");
  }

  public static String getTmpDir()
  {
    return getProperty("java.io.tmpdir", ".");
  }

  // public static void main(String[] args)
  // {
  // Map<String, String> properties = loadProperties(new File("config.ini"));
  // dump(properties);
  //
  // properties.put("test.property", "René Hentschke" + StringUtil.NL + "Eike Stepper");
  // saveProperties(new File("config.properties"), properties, false, true,
  // "This configuration file was written by: org.eclipse.equinox.internal.frameworkadmin.equinox.EquinoxFwConfigFileParser");
  // }
  //
  // private static void dump(Map<String, String> properties)
  // {
  // for (Map.Entry<String, String> entry : properties.entrySet())
  // {
  // System.out.println(entry.getKey() + "=" + entry.getValue());
  // }
  // }

  public static void saveProperties(File file, final Map<String, String> properties, final boolean sort)
  {
    saveProperties(file, properties, sort, false, null);
  }

  public static void saveProperties(File file, final Map<String, String> properties, final boolean sort, boolean withDate, String comment)
  {
    OutputStream stream = null;

    try
    {
      File folder = file.getParentFile();
      folder.mkdirs();

      // Buffering is done inside java.lang.Properties.
      stream = new FileOutputStream(file);

      Properties converter = new Properties()
      {
        private static final long serialVersionUID = 1L;

        @Override
        public synchronized Enumeration<Object> keys()
        {
          Collection<String> keys = properties.keySet();
          if (sort)
          {
            List<String> keyList = new ArrayList<String>(keys);
            Collections.sort(keyList);
            keys = keyList;
          }

          Vector<Object> keyVector = new Vector<Object>();
          for (String key : keys)
          {
            keyVector.add(key);
          }

          return keyVector.elements();
        }

        @Override
        public synchronized Object get(Object key)
        {
          return properties.get(key);
        }
      };

      if (withDate)
      {
        converter.store(stream, comment);
      }
      else
      {
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        converter.store(temp, comment);

        String string = temp.toString("8859_1");
        int firstNL = string.indexOf(StringUtil.NL);
        if (firstNL != -1)
        {
          if (comment == null)
          {
            string = string.substring(firstNL + StringUtil.NL.length());
          }
          else
          {
            // Skip the comment line.
            int secondNL = string.indexOf(StringUtil.NL, firstNL + StringUtil.NL.length());
            String commentLine = string.substring(0, firstNL + StringUtil.NL.length());
            string = commentLine + string.substring(secondNL + StringUtil.NL.length());
          }
        }

        IOUtil.copy(new ByteArrayInputStream(string.getBytes("8859_1")), stream);
      }
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.closeSilent(stream);
    }
  }

  public static Map<String, String> loadProperties(File file)
  {
    InputStream stream = null;

    try
    {
      // Buffering is done inside java.lang.Properties.
      stream = new FileInputStream(file);

      final Map<String, String> properties = new LinkedHashMap<String, String>();
      Properties converter = new Properties()
      {
        private static final long serialVersionUID = 1L;

        @Override
        public synchronized Object put(Object key, Object value)
        {
          return properties.put((String)key, (String)value);
        }
      };

      converter.load(stream);
      return properties;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.closeSilent(stream);
    }
  }

  public static Map<String, String> getProperties(File file)
  {
    if (file.isFile())
    {
      return loadProperties(file);
    }

    return new LinkedHashMap<String, String>();
  }
}
