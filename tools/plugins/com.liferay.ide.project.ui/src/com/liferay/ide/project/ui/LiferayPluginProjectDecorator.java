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

package com.liferay.ide.project.ui;

import com.liferay.ide.core.LiferayNature;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class LiferayPluginProjectDecorator extends LabelProvider implements ILightweightLabelDecorator {

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof IProject) {
			IProject project = (IProject)element;

			ImageDescriptor overlay = null;

			if (_hasFacet(project, _PORTLET_FACET)) {
				overlay = _getLiferay();
			}
			else if (_hasFacet(project, _HOOK_FACET)) {
				overlay = _getLiferay();
			}
			else if (_hasFacet(project, _EXT_FACET)) {
				overlay = _getLiferay();
			}
			else if (_hasFacet(project, _LAYOUTTPL_FACET)) {
				overlay = _getLiferay();
			}
			else if (_hasFacet(project, _THEME_FACET)) {
				overlay = _getLiferay();
			}
			else if (_hasFacet(project, _WEB_FACET)) {
				overlay = _getLiferay();
			}
			else if (LiferayNature.hasNature(project)) {
				overlay = _getLiferay();
			}

			if (overlay != null) {

				// next two lines dangerous!
				// DecorationContext ctx = (DecorationContext)
				// decoration.getDecorationContext();
				// ctx.putProperty( IDecoration.ENABLE_REPLACE, true );

				decoration.addOverlay(overlay, IDecoration.TOP_RIGHT);
			}
		}
	}

	/**
	 * This gets a .gif from the icons folder.
	 */
	private static ImageDescriptor _getImageDescriptor(String key) {
		ImageDescriptor imageDescriptor = null;

		if (key != null) {
			String gif = "/" + key + ".png";

			IPath path = new Path(_ICON_DIR).append(gif);

			URL gifImageURL = FileLocator.find(Platform.getBundle(ProjectUI.PLUGIN_ID), path, null);

			if (gifImageURL != null) {
				imageDescriptor = ImageDescriptor.createFromURL(gifImageURL);
			}
		}

		return imageDescriptor;
	}

	private static ImageDescriptor _getLiferay() {
		if (_liferay == null) {
			_liferay = _getImageDescriptor("liferay_decoration");
		}

		return _liferay;
	}

	private boolean _hasFacet(IProject project, String facet) {
		try {
			return FacetedProjectFramework.hasProjectFacet(project, facet);
		}
		catch (CoreException ce) {
			ProjectUI.logError(ce);

			return false;
		}
	}

	private static final String _EXT_FACET = "liferay.ext";

	/* The constants are duplicated here to avoid plugin loading. */
	private static final String _HOOK_FACET = "liferay.hook";

	private static final String _ICON_DIR = "icons/ovr";

	private static final String _LAYOUTTPL_FACET = "liferay.layouttpl";

	private static final String _PORTLET_FACET = "liferay.portlet";

	private static final String _THEME_FACET = "liferay.theme";

	private static final String _WEB_FACET = "liferay.web";

	private static ImageDescriptor _liferay;

}