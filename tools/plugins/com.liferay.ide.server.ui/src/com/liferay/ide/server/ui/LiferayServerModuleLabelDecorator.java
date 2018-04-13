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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.ModuleLabelDecorator;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class LiferayServerModuleLabelDecorator extends ModuleLabelDecorator {

	public LiferayServerModuleLabelDecorator() {
	}

	@Override
	public Image decorateImage(Image image, Object element) {
		try {
			IModule module = null;
			Image bundleImage = image;

			if (element instanceof IModule) {
				module = (IModule)element;
			}
			else if (element instanceof ModuleServer) {
				IModule[] modules = ((ModuleServer)element).module;

				module = modules[modules.length - 1];

				ModuleServer portalServer = (ModuleServer)element;

				IServer server = portalServer.getServer();

				IServerType serverType = server.getServerType();

				IRuntimeType runtimeType = serverType.getRuntimeType();

				String runtimeId = runtimeType.getId();

				if (runtimeId.equals("com.liferay.ide.server.portal.runtime")) {
					bundleImage = _getBundleModuleImage();
				}
			}

			if (module == null) {
				return null;
			}

			IProject project = module.getProject();

			if (project == null) {
				return null;
			}

			IMarker[] markers = project.findMarkers(LiferayServerCore.BUNDLE_OUTPUT_ERROR_MARKER_TYPE, false, 0);

			if (ListUtil.isNotEmpty(markers)) {
				bundleImage = BundlesImages.IMG_BUNDLE_ERROR;
			}

			IWorkbench workbench = PlatformUI.getWorkbench();

			IDecoratorManager decoratorManager = workbench.getDecoratorManager();

			return decoratorManager.decorateImage(bundleImage, project);
		}
		catch (Exception e) {
			return null;
		}
	}

	private Image _getBundleModuleImage() {
		String typeId = "liferay.bundle";

		Image image = ImageResource.getImage(typeId);
		int ind = typeId.indexOf(".");

		while ((image == null) && (ind >= 0)) {
			typeId = typeId.substring(0, ind);

			image = ImageResource.getImage(typeId);
		}

		return image;
	}

}