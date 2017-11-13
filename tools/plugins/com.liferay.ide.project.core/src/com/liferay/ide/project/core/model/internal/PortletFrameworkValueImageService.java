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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.Result;
import org.eclipse.sapphire.services.ValueImageService;

/**
 * @author Gregory Amerson
 */
public class PortletFrameworkValueImageService extends ValueImageService {

	@Override
	public ImageData provide(String value) {
		ImageData data = null;

		IPortletFramework framework = ProjectCore.getPortletFramework(value);

		if (framework != null) {
			String name = framework.getShortName();

			Result<ImageData> result = ImageData.readFromClassLoader(framework.getClass(), "images/" + name + ".png");

			data = result.optional();
		}

		return data;
	}

	public static class Condition extends PortletFrameworkValueLabelService.Condition {
	}

}