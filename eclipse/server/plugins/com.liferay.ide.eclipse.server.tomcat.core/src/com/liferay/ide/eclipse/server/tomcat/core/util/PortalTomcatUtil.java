/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *    Greg Amerson <gregory.amerson@liferay.com>
 *******************************************************************************/
package com.liferay.ide.eclipse.server.tomcat.core.util;


import com.liferay.ide.eclipse.core.util.CoreUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;

public class PortalTomcatUtil {

	public static Context loadContextFile(File contextFile) {
		FileInputStream fis = null;
		Context context = null;
		if (contextFile != null && contextFile.exists()) {
			try {
				Factory factory = new Factory();
				factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
				fis = new FileInputStream(contextFile);
				context = (Context)factory.loadDocument(fis);
				if (context != null) {
					String path = context.getPath();
					// If path attribute is not set, derive from file name
					if (path == null) {
						String fileName = contextFile.getName();
						path = fileName.substring(0, fileName.length() - ".xml".length());
						if ("ROOT".equals(path))
							path = "";
						context.setPath("/" + path);
					}
				}
			} catch (Exception e) {
				// may be a spurious xml file in the host dir?

			} finally {
				try {
					fis.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return context;
	}

	public static boolean isExtProjectContext(Context context) {
		String path = context.getPath();
		String docBase = context.getDocBase();
		
		return false;
	}

	public static IPath modifyLocationForBundle(IPath currentLocation) {
		IPath modifiedLocation = null;
	
		if (currentLocation == null || CoreUtil.isNullOrEmpty(currentLocation.toOSString())) {
			return null;
		}
	
		File location = currentLocation.toFile();
	
		if (location.exists() && location.isDirectory()) {
			// check to see if this location contains 3 dirs
			// data, deploy, and tomcat-*
			File[] files = location.listFiles();
	
			boolean[] matches = new boolean[3];
	
			String[] patterns = new String[] {
				"data", "deploy", "^tomcat-.*"
			};
	
			File tomcatDir = null;
	
			for (File file : files) {
				for (int i = 0; i < patterns.length; i++) {
					if (file.isDirectory() && file.getName().matches(patterns[i])) {
						matches[i] = true;
	
						if (i == 2) { // tomcat
							tomcatDir = file;
						}
	
						break;
					}
				}
			}
	
			if (matches[0] && matches[1] && matches[2] && tomcatDir != null) {
				modifiedLocation = new Path(tomcatDir.getPath());
			}
		}
	
		return modifiedLocation;
	}

}
