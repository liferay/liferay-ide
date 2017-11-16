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

import com.liferay.ide.portlet.core.model.CustomWindowState;
import com.liferay.ide.portlet.core.model.WindowState;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.ImageService;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyEvent;

/**
 * @author Kamesh Sampath
 */
public class WindowStateImageService extends ImageService {

	@Override
	public void dispose() {
		super.dispose();

		context(Element.class).detach(this.listener, WindowState.PROP_WINDOW_STATE.name());
	}

	@Override
	protected ImageData compute() {
		String strWindowState = null;
		Element element = context(Element.class);
		ImageData imageData = null;

		if (element instanceof CustomWindowState) {
			CustomWindowState customWindowState = (CustomWindowState)element;

			strWindowState = String.valueOf(customWindowState.getWindowState().content());
		}
		else if (element instanceof WindowState) {
			WindowState windowState = (WindowState)element;

			strWindowState = windowState.getWindowState().content();
		}

		if ("MAXIMIZED".equalsIgnoreCase(strWindowState)) {
			imageData = IMG_MAXIMIZED;
		}
		else if ("MINIMIZED".equalsIgnoreCase(strWindowState)) {
			imageData = IMG_MINIMIZED;
		}

		if (imageData == null) {
			imageData = IMG_DEFAULT;
		}

		return imageData;
	}

	@Override
	protected void initImageService() {
		this.listener = new FilteredListener<PropertyEvent>() {

			@Override
			public void handleTypedEvent(final PropertyEvent event) {
				refresh();
			}

		};

		context(Element.class).attach(this.listener, WindowState.PROP_WINDOW_STATE.name());
	}

	private static final ImageData IMG_DEFAULT = ImageData.readFromClassLoader(
		WindowStateImageService.class, "images/window_states.png").required();

	private static final ImageData IMG_MAXIMIZED = ImageData.readFromClassLoader(
		WindowStateImageService.class, "images/maximize.png").required();

	private static final ImageData IMG_MINIMIZED = ImageData.readFromClassLoader(
		WindowStateImageService.class, "images/minimize.png").required();

	private Listener listener;

}