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

package com.liferay.ide.portlet.ui.navigator;

import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.ui.navigator.AbstractLabelProvider;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
public class PortletResourcesLabelProvider extends AbstractLabelProvider {

	public PortletResourcesLabelProvider() {
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof PortletResourcesRootNode) {
			return getImageRegistry().get(_MODULES);
		}
		else if (element instanceof PortletsNode) {
			return getImageRegistry().get(_PORTLETS);
		}
		else if (element instanceof PortletNode) {
			return getImageRegistry().get(_PORTLET);
		}

		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PortletResourcesRootNode) {
			return Msgs.liferayPortletResources;
		}
		else if (element instanceof PortletsNode) {
			return Msgs.portlets;
		}
		else if (element instanceof PortletNode) {
			PortletNode portletNode = (PortletNode)element;

			return portletNode.getName();
		}

		return null;
	}

	@Override
	protected void initalizeImageRegistry(ImageRegistry imageRegistry) {
		imageRegistry.put(
			_PORTLETS,
			PortletUIPlugin.imageDescriptorFromPlugin(PortletUIPlugin.PLUGIN_ID, "icons/e16/portlets_16x16.png"));
		imageRegistry.put(
			_PORTLET,
			PortletUIPlugin.imageDescriptorFromPlugin(PortletUIPlugin.PLUGIN_ID, "icons/e16/portlet_16x16.png"));
		imageRegistry.put(
			_MODULES,
			PortletUIPlugin.imageDescriptorFromPlugin(PortletUIPlugin.PLUGIN_ID, "icons/liferay_logo_16.png"));
	}

	private static final String _MODULES = "MODULES";

	private static final String _PORTLET = "PORTLET";

	private static final String _PORTLETS = "PORTLETS";

	private static class Msgs extends NLS {

		public static String liferayPortletResources;
		public static String portlets;

		static {
			initializeMessages(PortletResourcesLabelProvider.class.getName(), Msgs.class);
		}

	}

}