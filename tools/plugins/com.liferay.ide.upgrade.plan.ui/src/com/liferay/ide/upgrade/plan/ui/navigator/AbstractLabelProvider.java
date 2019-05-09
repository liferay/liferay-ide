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

package com.liferay.ide.upgrade.plan.ui.navigator;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractLabelProvider extends LabelProvider {

	public AbstractLabelProvider() {
		_imageRegistry = new ImageRegistry();

		initalizeImageRegistry(_imageRegistry);
	}

	@Override
	public void dispose() {
		_imageRegistry.dispose();
	}

	protected ImageRegistry getImageRegistry() {
		return _imageRegistry;
	}

	protected abstract void initalizeImageRegistry(ImageRegistry registry);

	private final ImageRegistry _imageRegistry;

}