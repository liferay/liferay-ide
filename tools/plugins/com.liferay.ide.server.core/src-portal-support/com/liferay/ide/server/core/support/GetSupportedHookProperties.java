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

import com.liferay.portal.deploy.hot.HookHotDeployListener;

import java.io.FileWriter;
import java.io.IOException;


public class GetSupportedHookProperties extends PortalSupport {

	@Override
	void writeOutput(FileWriter writer)
		throws IOException {

		for (String supportedProperty : HookHotDeployListener.SUPPORTED_PROPERTIES) {
			writer.write(supportedProperty);
			writer.write('\n');
		}
	}

	// public static void main(String[] args) {
	// if (args != null && args[0] != null && !args[0].isEmpty()) {
	// // arg 0 is a file to write out the properties that are returned
	// Properties properties = new Properties();
	// for (String supportProperty : HookHotDeployListener.SUPPORTED_PROPERTIES)
	// {
	// properties.put(supportProperty, "");
	// System.out.println(supportProperty);
	// }
	// try {
	// properties.store(new FileWriter(new File(args[0])), "");
	// }
	// catch (IOException e) {
	// e.printStackTrace();
	// System.exit(1);
	// }
	// System.out.println("Supported hook properties written to: " + args[0]);
	// }
	// }

}
