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

package com.liferay.ide.eclipse.server.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;

import com.liferay.ide.eclipse.project.core.util.ProjectUtil;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class ServerViewCustomLabelProvider extends LabelProvider {

	public ServerViewCustomLabelProvider() {
		super();
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof PluginsContent) {// || (element instanceof
												// IServer &&
												// ServerUtil.isPortalRuntime(((IServer)element))))
												// {
			return PortalServerUIPlugin.imageDescriptorFromPlugin(
				PortalServerUIPlugin.PLUGIN_ID, "/icons/e16/plugin.png").createImage();
		}
		else if (element instanceof ModuleServer) {			
			ModuleServer server = (ModuleServer) element;
			
			if (ProjectUtil.isPortletProject(server.getModule()[0].getProject())) {
				return PortalServerUIPlugin.imageDescriptorFromPlugin(
					PortalServerUIPlugin.PLUGIN_ID, "/icons/e16/portlet.png").createImage();
			}
			else if (ProjectUtil.isHookProject(server.getModule()[0].getProject())) {
				return PortalServerUIPlugin.imageDescriptorFromPlugin(
					PortalServerUIPlugin.PLUGIN_ID, "/icons/e16/hook.png").createImage();
			}
			else if (ProjectUtil.isExtProject(server.getModule()[0].getProject())) {
				return PortalServerUIPlugin.imageDescriptorFromPlugin(
					PortalServerUIPlugin.PLUGIN_ID, "/icons/e16/ext.png").createImage();
			}
		}
		
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PluginsContent) {
			return "Liferay Plug-ins";
		}
		else {
			return null;
		}
	}

}
