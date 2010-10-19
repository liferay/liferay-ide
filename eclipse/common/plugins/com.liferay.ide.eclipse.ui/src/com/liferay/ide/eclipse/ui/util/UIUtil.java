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

package com.liferay.ide.eclipse.ui.util;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;

/**
 * @author Greg Amerson
 */
public class UIUtil {

	public static ImageDescriptor getPluginImageDescriptor(String symbolicName, String imagePath) {
		Bundle bundle = Platform.getBundle(symbolicName);

		if (bundle != null) {
			URL entry = bundle.getEntry(imagePath);

			if (entry != null) {
				return ImageDescriptor.createFromURL(entry);
			}
		}

		return null;
	}

}
