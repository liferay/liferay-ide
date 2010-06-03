/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.server.core.support;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Greg Amerson
 */
public abstract class PortalSupport {

	public static void main(String[] args) {
		String supportClassName = args[0];

		try {
			Class<?> portalSupportClass;

			portalSupportClass = Class.forName(supportClassName);

			Object newClass = portalSupportClass.newInstance();

			if (newClass instanceof PortalSupport) {
				PortalSupport portalSupport = (PortalSupport) newClass;

				File outputFile = new File(args[1]);

				outputFile.getParentFile().mkdirs();

				FileWriter writer = null;

				try {
					writer = new FileWriter(outputFile);

					portalSupport.writeOutput(writer);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if (writer != null) {
						try {
							writer.close();
						}
						catch (IOException e) {
							// best effort
						}
					}
				}
			}
		}
		catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	abstract void writeOutput(FileWriter writer)
		throws IOException;

}
