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

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.LiferayCore;

import org.eclipse.sapphire.services.ValueLabelService;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class ProjectProviderValueLabelService extends ValueLabelService {

	@Override
	public String provide(String value) {
		ILiferayProjectProvider provider = LiferayCore.getProvider(value);

		if (provider != null) {
			return provider.getDisplayName();
		}

		return value;
	}

}