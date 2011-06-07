/*******************************************************************************
 * Copyright (c) 2010-2011 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IColumn;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.ImageService;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;


public class ColumnImageService extends ImageService {

	private static final ImageData IMG_COLUMN = ImageData.readFromClassLoader(
		ColumnImageService.class, "images/column_16x16.gif");
	private static final ImageData IMG_COLUMN_PRIMARY = ImageData.readFromClassLoader(
		ColumnImageService.class, "images/column_primary_16x16.png");

	private ModelPropertyListener listener;

	@Override
	public void init(final IModelElement element, final String[] params) {
		super.init(element, params);

		this.listener = new ModelPropertyListener() {

			@Override
			public void handlePropertyChangedEvent(final ModelPropertyChangeEvent event) {
				notifyListeners(new ImageChangedEvent(ColumnImageService.this));
			}
		};

		element.addListener(this.listener, IColumn.PROP_PRIMARY.getName());
	}

	@Override
	public ImageData provide() {
		if (((IColumn) element()).isPrimary().getContent()) {
			return IMG_COLUMN_PRIMARY;
		}
		else {
			return IMG_COLUMN;
		}
	}

	@Override
	public void dispose() {
		super.dispose();

		element().removeListener(this.listener, IColumn.PROP_PRIMARY.getName());
	}

}