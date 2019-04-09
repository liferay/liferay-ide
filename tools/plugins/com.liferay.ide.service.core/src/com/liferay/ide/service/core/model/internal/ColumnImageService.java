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

package com.liferay.ide.service.core.model.internal;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.service.core.model.Column;

import org.eclipse.sapphire.DisposeEvent;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.ImageService;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.Result;

/**
 * @author Gregory Amerson
 */
public class ColumnImageService extends ImageService implements SapphireContentAccessor {

	@Override
	public ImageData compute() {
		ImageData imageData = _imgColumn;

		Column column = context(Column.class);

		if (get(column.isPrimary())) {
			imageData = _imgColumnPrimary;
		}

		return imageData;
	}

	@Override
	protected void initImageService() {
		_listener = new FilteredListener<PropertyEvent>() {

			@Override
			protected void handleTypedEvent(PropertyEvent event) {
				refresh();
			}

		};

		Element element = context(Element.class);

		element.attach(_listener, Column.PROP_PRIMARY.name());

		attach(
			new Listener() {

				@Override
				public void handle(Event event) {
					if (event instanceof DisposeEvent) {
						element.detach(_listener, Column.PROP_PRIMARY.name());
					}
				}

			});
	}

	private static ImageData _imgColumn;
	{
		Result<ImageData> result = ImageData.readFromClassLoader(ColumnImageService.class, "images/column_16x16.gif");

		_imgColumn = result.required();
	}

	private static ImageData _imgColumnPrimary;
	{
		Result<ImageData> result = ImageData.readFromClassLoader(
			ColumnImageService.class, "images/column_primary_16x16.png");

		_imgColumnPrimary = result.required();
	}

	private Listener _listener;

}