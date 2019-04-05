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
import com.liferay.ide.portlet.core.model.CustomWindowState;
import com.liferay.ide.portlet.core.model.WindowState;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.ImageService;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.Result;

/**
 * @author Kamesh Sampath
 */
public class WindowStateImageService extends ImageService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		super.dispose();

		Element element = context(Element.class);

		element.detach(_listener, WindowState.PROP_WINDOW_STATE.name());
	}

	@Override
	protected ImageData compute() {
		String strWindowState = null;
		Element element = context(Element.class);
		ImageData imageData = null;

		if (element instanceof CustomWindowState) {
			CustomWindowState customWindowState = (CustomWindowState)element;

			strWindowState = get(customWindowState.getWindowState());
		}
		else if (element instanceof WindowState) {
			WindowState windowState = (WindowState)element;

			strWindowState = get(windowState.getWindowState());
		}

		if ("MAXIMIZED".equalsIgnoreCase(strWindowState)) {
			imageData = _imgMaximized;
		}
		else if ("MINIMIZED".equalsIgnoreCase(strWindowState)) {
			imageData = _imgMinimized;
		}

		if (imageData == null) {
			imageData = _imgDefault;
		}

		return imageData;
	}

	@Override
	protected void initImageService() {
		_listener = new FilteredListener<PropertyEvent>() {

			@Override
			public void handleTypedEvent(final PropertyEvent event) {
				refresh();
			}

		};

		Element element = context(Element.class);

		element.attach(_listener, WindowState.PROP_WINDOW_STATE.name());
	}

	private static ImageData _imgDefault;
	private static ImageData _imgMaximized;
	private static ImageData _imgMinimized;

	static {
		Result<ImageData> result =
			ImageData.readFromClassLoader(WindowStateImageService.class, "images/window_states.png");

		_imgDefault = result.required();

		Result<ImageData> result2 = ImageData.readFromClassLoader(WindowStateImageService.class, "images/maximize.png");

		_imgMaximized = result2.required();

		Result<ImageData> result3 = ImageData.readFromClassLoader(WindowStateImageService.class, "images/minimize.png");

		_imgMinimized = result3.required();
	}

	private Listener _listener;

}