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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.portlet.core.model.CustomPortletMode;
import com.liferay.ide.portlet.core.model.PortletMode;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.ImageService;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Result;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class PortletModeImageService extends ImageService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		super.dispose();

		Element element = context(Element.class);

		element.detach(_listener, PortletMode.PROP_PORTLET_MODE.name());
	}

	@Override
	protected ImageData compute() {
		String portletMode = null;
		Element element = context(Element.class);
		ImageData imageData = null;

		if (element instanceof CustomPortletMode) {
			CustomPortletMode mode = (CustomPortletMode)element;

			portletMode = get(mode.getPortletMode());
		}
		else if (element instanceof PortletMode) {
			PortletMode mode = (PortletMode)element;

			portletMode = get(mode.getPortletMode());
		}

		if (portletMode != null) {
			if ("VIEW".equalsIgnoreCase(portletMode)) {
				imageData = _imgView;
			}
			else if ("EDIT".equalsIgnoreCase(portletMode)) {
				imageData = _imgEdit;
			}
			else if ("HELP".equalsIgnoreCase(portletMode)) {
				imageData = _imgHelp;
			}
		}

		if (imageData == null) {
			imageData = _imgDefault;
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

		Element element = context(Element.class);

		element.attach(_listener, PortletMode.PROP_PORTLET_MODE.name());
	}

	private static ImageData _imgDefault;
	private static ImageData _imgEdit;
	private static ImageData _imgHelp;
	private static ImageData _imgView;

	static {
		Result<ImageData> result = ImageData.readFromClassLoader(PortletModeImageService.class, "images/portlet.png");

		_imgDefault = result.required();

		result = ImageData.readFromClassLoader(PortletModeImageService.class, "images/edit.png");

		_imgEdit = result.required();

		result = ImageData.readFromClassLoader(PortletModeImageService.class, "images/help.png");

		_imgHelp = result.required();

		result = ImageData.readFromClassLoader(PortletModeImageService.class, "images/view.png");

		_imgView = result.required();
	}

	private Listener _listener;

}