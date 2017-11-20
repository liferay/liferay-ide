/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
 */

package com.liferay.ide.kaleo.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Gregory Amerson
 */
public class KaleoImages {

	public static final Image IMG_LOADING = _createImage("e16/waiting_16x16.gif");

	public static final Image IMG_USER_TEMPLATE = _createImage("template_obj.gif");

	public static final Image IMG_WORKFLOW_DEFINITION = _createImage("e16/workflow_16x16.png");

	public static final Image IMG_WORKFLOW_DEFINITION_WIZBAN = _createImage("wizban/workflow_definition_16x16.png");

	public static final Image IMG_WORKFLOW_DEFINITIONS_FOLDER = _createImage("e16/definitions_16x16.png");

	private static ImageDescriptor _create(String key) {
		try {
			ImageDescriptor imageDescriptor = _createDescriptor(key);
			ImageRegistry imageRegistry = _getImageRegistry();

			if (imageRegistry != null) {
				imageRegistry.put(key, imageDescriptor);
			}

			return imageDescriptor;
		}
		catch (Exception ex) {
			KaleoUI.logError(ex);

			return null;
		}
	}

	private static ImageDescriptor _createDescriptor(String image) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(KaleoUI.PLUGIN_ID, "icons/" + image);
	}

	private static Image _createImage(String key) {
		_create(key);
		ImageRegistry imageRegistry = _getImageRegistry();

		if (imageRegistry == null) {
			return null;
		}

		return imageRegistry.get(key);
	}

	private static ImageRegistry _getImageRegistry() {
		KaleoUI plugin = KaleoUI.getDefault();

		if (plugin == null) {
			return null;
		}

		return plugin.getImageRegistry();
	}

}