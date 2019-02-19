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

package com.liferay.ide.upgrade.plan.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.Result;
import org.eclipse.sapphire.services.ValueImageService;

/**
 * @author Simon Jiang
 */
public final class UpgradeCategoryValueImageService extends ValueImageService {

	@Override
	public ImageData provide(final String value)
	{

		ImageData imageData = _imageMap.get(value);

		if (imageData == null) {
			Result<ImageData> imageResult = ImageData.readFromClassLoader(
				UpgradeCategoryValueImageService.class, _img_folder + _img_prefix + value + _img_suffix);

			imageData = imageResult.required();

			_imageMap.put(value, imageData);
		}

		return imageData;
	}

	private static Map<String, ImageData> _imageMap = new HashMap<>();
	private static String _img_folder = "icons/";
	private static String _img_prefix = "category_";
	private static String _img_suffix = ".png";

}