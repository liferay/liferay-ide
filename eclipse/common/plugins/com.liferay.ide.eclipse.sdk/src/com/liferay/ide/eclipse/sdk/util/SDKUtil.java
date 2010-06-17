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

package com.liferay.ide.eclipse.sdk.util;

import com.liferay.ide.eclipse.sdk.ISDKConstants;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.Path;
import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 */
public class SDKUtil {

	// public static String creaetUniqueSDKId() {
	// String id = null;
	// SDK[] currentSDKs = SDKManager.getInstance().getSDKs();
	// do {
	// id = String.valueOf(System.currentTimeMillis());
	// } while (sdksContainId(currentSDKs, id));
	// return id;
	// }

	// private static boolean sdksContainName(SDK[] sdks, String name) {
	// if (name != null) {
	// for (SDK sdk : sdks) {
	// if (name.equals(sdk.getName())) {
	// return true;
	// }
	// }
	// }
	// return false;
	// }

	public static boolean isSDKSupported(String location) {
		boolean retval = false;

		try {
			String version = SDKUtil.readSDKVersion(location);

			retval = new Version(version).compareTo(ISDKConstants.LEAST_SUPPORTED_SDK_VERSION) >= 0;
		}
		catch (Exception e) {
			// best effort we didn't find a valid location
		}

		return retval;

	}

	public static boolean isValidSDKLocation(String loc) {
		boolean retval = false;

		// try to look for build.properties file with property lp.version

		try {
			String version = SDKUtil.readSDKVersion(loc);

			new Version(version);

			retval = true;
		}
		catch (Exception e) {
			// best effort we didn't find a valid location
		}

		return retval;
	}

	public static boolean isValidSDKVersion(String sdkVersion, Version lowestValidVersion) {
		Version sdkVersionValue = null;

		try {
			sdkVersionValue = new Version(sdkVersion);
		}
		catch (Exception ex) {
			// ignore means we don't have valid version
		}

		if (sdkVersionValue != null && sdkVersionValue.compareTo(lowestValidVersion) >= 0) {
			return true;
		}

		return false;
	}

	public static String readSDKVersion(String path)
		throws FileNotFoundException, IOException {

		Properties properties = new Properties();
		properties.load(new FileInputStream(new Path(path).append("build.properties").toFile()));

		return properties.getProperty("lp.version");
	}
}
