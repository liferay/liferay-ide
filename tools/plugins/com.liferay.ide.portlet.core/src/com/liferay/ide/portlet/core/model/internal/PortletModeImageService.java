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

		context(Element.class).detach(this.listener, PortletMode.PROP_PORTLET_MODE.name());
	}

	@Override
	protected ImageData compute() {
		String portletMode = null;
		Element element = context(Element.class);
		ImageData imageData = null;

		if (element instanceof CustomPortletMode) {
			CustomPortletMode iCustomPortletMode = (CustomPortletMode)element;

			portletMode = String.valueOf(iCustomPortletMode.getPortletMode().content());
		}
		else if (element instanceof PortletMode) {
			PortletMode iPortletMode = (PortletMode)element;

			portletMode = iPortletMode.getPortletMode().content();
		}

		if (portletMode != null) {
			if ("VIEW".equalsIgnoreCase(portletMode)) {
				imageData = IMG_VIEW;
			}
			else if ("EDIT".equalsIgnoreCase(portletMode)) {
				imageData = IMG_EDIT;
			}
			else if ("HELP".equalsIgnoreCase(portletMode)) {
				imageData = IMG_HELP;
			}
		}

		if (imageData == null) {
			imageData = IMG_DEFAULT;
		}

		return imageData;
	}

	@Override
	protected void initImageService() {
		this.listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(final PropertyContentEvent event) {
				refresh();
			}

		};

		context(Element.class).attach(this.listener, PortletMode.PROP_PORTLET_MODE.name());
	}

	private static final ImageData IMG_DEFAULT = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/portlet.png").required();

	private static final ImageData IMG_EDIT = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/edit.png").required();

	private static final ImageData IMG_HELP = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/help.png").required();

	private static final ImageData IMG_VIEW = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/view.png").required();

	private Listener listener;

}