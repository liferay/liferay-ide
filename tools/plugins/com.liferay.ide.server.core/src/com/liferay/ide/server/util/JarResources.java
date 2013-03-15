/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.server.util;

import com.liferay.ide.core.util.StringPool;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * JarResources: JarResources maps all resources included in a
 * Zip or Jar file. Additionaly, it provides a method to extract one
 * as a blob.
 */
@SuppressWarnings( "rawtypes" )
public final class JarResources {

   // external debug flag
   public boolean debugOn=false;

   // jar resource mapping tables
   private Hashtable htSizes=new Hashtable();  
   private Hashtable htJarContents=new Hashtable();

   // a jar file
   private String jarFileName;

   /**
    * creates a JarResources. It extracts all resources from a Jar
    * into an internal hashtable, keyed by resource names.
    * @param jarFileName a jar or zip file
    */
   public JarResources(String jarFileName) {
      this.jarFileName=jarFileName;
      init();
   }

   /**
    * Extracts a jar resource as a blob.
    * @param name a resource name.
    */
   public byte[] getResource(String name) {
      return (byte[])htJarContents.get(name);
   }

   /**
    * initializes internal hash tables with Jar file resources.
    */
   private void init() {
      try {
          // extracts just sizes only. 
          ZipFile zf=new ZipFile(jarFileName);
          Enumeration e=zf.entries();
          while (e.hasMoreElements()) {
              ZipEntry ze=(ZipEntry)e.nextElement();
              if (debugOn) {
                 System.out.println(dumpZipEntry(ze));
              }
              htSizes.put(ze.getName(),new Integer((int)ze.getSize()));
          }
          zf.close();

          // extract resources and put them into the hashtable.
          FileInputStream fis=new FileInputStream(jarFileName);
          BufferedInputStream bis=new BufferedInputStream(fis);
          ZipInputStream zis=new ZipInputStream(bis);
          ZipEntry ze=null;
          while ((ze=zis.getNextEntry())!=null) {
             if (ze.isDirectory()) {
                continue;
             }
             if (debugOn) {
                System.out.println(
                   "ze.getName()="+ze.getName()+ StringPool.COMMA +"getSize()="+ze.getSize() //$NON-NLS-1$ //$NON-NLS-2$
                   );
             }
             int size=(int)ze.getSize();
             // -1 means unknown size. 
             if (size==-1) {
                size=((Integer)htSizes.get(ze.getName())).intValue();
             }
             byte[] b=new byte[(int)size];
             int rb=0;
             int chunk=0;
             while (((int)size - rb) > 0) {
                 chunk=zis.read(b,rb,(int)size - rb);
                 if (chunk==-1) {
                    break;
                 }
                 rb+=chunk;
             }
             // add to internal resource hashtable
             htJarContents.put(ze.getName(),b);
             if (debugOn) {
                System.out.println(
                   ze.getName()+"  rb="+rb+ //$NON-NLS-1$
                   ",size="+size+ //$NON-NLS-1$
                   ",csize="+ze.getCompressedSize() //$NON-NLS-1$
                   );
             }
          }
       } catch (NullPointerException e) {
          System.out.println("done."); //$NON-NLS-1$
       } catch (FileNotFoundException e) {
          e.printStackTrace();
       } catch (IOException e) {
          e.printStackTrace();
       }
   }

   /**
    * Dumps a zip entry into a string.
    * @param ze a ZipEntry
    */
   private String dumpZipEntry(ZipEntry ze) {
       StringBuffer sb=new StringBuffer();
       if (ze.isDirectory()) {
          sb.append("d ");  //$NON-NLS-1$
       } else {
          sb.append("f ");  //$NON-NLS-1$
       }
       if (ze.getMethod()==ZipEntry.STORED) {
          sb.append("stored   ");  //$NON-NLS-1$
       } else {
          sb.append("defalted "); //$NON-NLS-1$
       }
       sb.append(ze.getName());
       sb.append("\t"); //$NON-NLS-1$
       sb.append(StringPool.EMPTY+ze.getSize());
       if (ze.getMethod()==ZipEntry.DEFLATED) {
          sb.append("/"+ze.getCompressedSize()); //$NON-NLS-1$
       }
       return (sb.toString());
   }

   /**
    * Is a test driver. Given a jar file and a resource name, it trys to
    * extract the resource and then tells us whether it could or not.
    *
    * <strong>Example</strong>
    * Let's say you have a JAR file which jarred up a bunch of gif image
    * files. Now, by using JarResources, you could extract, create, and display
    * those images on-the-fly.
    * <pre>
    *     ...
    *     JarResources JR=new JarResources("GifBundle.jar");
    *     Image image=Toolkit.createImage(JR.getResource("logo.gif");
    *     Image logo=Toolkit.getDefaultToolkit().createImage(
    *                   JR.getResources("logo.gif")
    *                   );
    *     ...
    * </pre>
    */
   public static void main(String[] args) throws IOException {
       if (args.length!=2) {
          System.err.println(
             "usage: java JarResources <jar file name> <resource name>" //$NON-NLS-1$
             );
          System.exit(1);
       }
       JarResources jr=new JarResources(args[0]);
       byte[] buff=jr.getResource(args[1]);
       if (buff==null) {
          System.out.println("Could not find "+args[1]+"."); //$NON-NLS-1$ //$NON-NLS-2$
       } else {
          System.out.println("Found "+args[1]+ " (length="+buff.length+")."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
       }
   }

}	// End of JarResources class.
