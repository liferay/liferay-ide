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

import com.liferay.ide.eclipse.server.tomcat.core.IPortalTomcatConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

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

	public static URL checkForLatestInstallableRuntime(String id) {

		try {
			URL url = new URL(IPortalTomcatConstants.INSTALLABLE_UPDATE_URL);

			InputStream is = url.openStream();

			Properties props = new Properties();

			props.load(is);

			String installableUrl = props.getProperty(id);

			return new URL(installableUrl);
		}
		catch (Exception e) {
			// best effort
		}

		return null;
	}

}
