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

package com.liferay.ide.portlet.core.model.internal;

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.portlet.core.model.CustomPortletMode;
import com.liferay.ide.portlet.core.model.PortletMode;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.ImageService;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class PortletModeImageService extends ImageService {

	@Override
	public void dispose() {
		super.dispose();

		context(Element.class).detach(_listener, PortletMode.PROP_PORTLET_MODE.name());
	}

	@Override
	protected ImageData compute() {
		String portletMode = null;
		Element element = context(Element.class);
		ImageData imageData = null;

		if (element instanceof CustomPortletMode) {
			CustomPortletMode mode = (CustomPortletMode)element;

			portletMode = SapphireUtil.getContent(mode.getPortletMode());
		}
		else if (element instanceof PortletMode) {
			PortletMode mode = (PortletMode)element;

			portletMode = SapphireUtil.getContent(mode.getPortletMode());
		}

		if (portletMode != null) {
			if ("VIEW".equalsIgnoreCase(portletMode)) {
				imageData = _IMG_VIEW;
			}
			else if ("EDIT".equalsIgnoreCase(portletMode)) {
				imageData = _IMG_EDIT;
			}
			else if ("HELP".equalsIgnoreCase(portletMode)) {
				imageData = _IMG_HELP;
			}
		}

		if (imageData == null) {
			imageData = _IMG_DEFAULT;
		}

		return imageData;
	}

	@Override
	protected void initImageService() {
		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		context(Element.class).attach(_listener, PortletMode.PROP_PORTLET_MODE.name());
	}

	private static final ImageData _IMG_DEFAULT = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/portlet.png").required();

	private static final ImageData _IMG_EDIT = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/edit.png").required();

	private static final ImageData _IMG_HELP = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/help.png").required();

	private static final ImageData _IMG_VIEW = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/view.png").required();

	private Listener _listener;

}