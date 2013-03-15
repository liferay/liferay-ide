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

package com.liferay.ide.server.core.support;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Greg Amerson
 */
public abstract class PortalSupport {

	public static void main(String[] args) {
		String supportClassName = args[0];

		File outputFile = new File(args[1]);

		File errorFile = new File(args[2]);

		try {
			Class<?> portalSupportClass;

			portalSupportClass = Class.forName(supportClassName);

			Object newClass = portalSupportClass.newInstance();

			if (newClass instanceof PortalSupport) {
				PortalSupport portalSupport = (PortalSupport) newClass;

				outputFile.getParentFile().mkdirs();

				FileWriter writer = null;

				try {
					writer = new FileWriter(outputFile);

					portalSupport.writeOutput(writer);

					writer.flush();
				}
				catch (Exception e) {
					appendError(e, errorFile);
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
		catch (Exception e) {
			try {
				appendError(e, errorFile);
			}
			catch (Exception e1) {
				// best effort no error here
			}
		}

	}

	static void appendError(Exception e, File errorFile)
		throws IOException {

		FileWriter writer = new FileWriter(errorFile);
		writer.append(e.getMessage());
		e.printStackTrace(new PrintWriter(writer));
		writer.flush();
		writer.close();
	}

	abstract void writeOutput(FileWriter writer)
		throws IOException;

}
