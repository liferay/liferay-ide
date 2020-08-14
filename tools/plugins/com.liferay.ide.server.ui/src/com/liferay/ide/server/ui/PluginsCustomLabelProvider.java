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

package com.liferay.ide.server.ui;

import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.Objects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class PluginsCustomLabelProvider extends LabelProvider {

	public PluginsCustomLabelProvider() {
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof PluginsContent) {
			ImageDescriptor imageDescriptor = LiferayServerUI.imageDescriptorFromPlugin(
				LiferayServerUI.PLUGIN_ID, "/icons/e16/plugin.png");

			return imageDescriptor.createImage();
		}
		else if (element instanceof ModuleServer) {
			try {
				ModuleServer server = (ModuleServer)element;

				// IDE-880 check to make sure this element isn't a nested jar

				IModule module = server.getModule()[server.module.length - 1];

				IModuleType moduleType = module.getModuleType();

				if (Objects.equals("jst.web", moduleType.getId())) {
					IProject project = module.getProject();

					IFacetedProject facetedProject = ProjectUtil.getFacetedProject(project);

					String imageKey = null;

					if (facetedProject != null) {
						IProjectFacet liferayFacet = ProjectUtil.getLiferayFacet(facetedProject);

						if (liferayFacet != null) {
							String id = liferayFacet.getId();

							imageKey = id.substring(id.indexOf('.') + 1, id.length());
						}
					}
					else {
						IPath projectLocation = project.getLocation();

						imageKey = ProjectUtil.getLiferayPluginType(projectLocation.toOSString());
					}

					LiferayServerUI serverUIPlugin = LiferayServerUI.getDefault();

					ImageRegistry imageRegistry = serverUIPlugin.getImageRegistry();

					return imageRegistry.get(imageKey);
				}
			}
			catch (Exception ex) {

				// best effort no need to log error

			}
		}

		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PluginsContent) {
			return Msgs.liferayPlugins;
		}

		return null;
	}

	private static class Msgs extends NLS {

		public static String liferayPlugins;

		static {
			initializeMessages(PluginsCustomLabelProvider.class.getName(), Msgs.class);
		}

	}

}