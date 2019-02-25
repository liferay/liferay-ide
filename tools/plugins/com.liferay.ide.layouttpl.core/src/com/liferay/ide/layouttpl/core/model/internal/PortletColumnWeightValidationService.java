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

package com.liferay.ide.layouttpl.core.model.internal;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Kuo Zhang
 */
public class PortletColumnWeightValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		PortletColumnElement portletColumn = context(PortletColumnElement.class);

		int weight = get(portletColumn.getWeight());

		LayoutTplElement layoutTpl = portletColumn.nearest(LayoutTplElement.class);

		if (layoutTpl != null) {
			if (get(layoutTpl.getBootstrapStyle())) {
				if ((weight <= 0) || (weight > 12)) {
					retval = Status.createErrorStatus("The weight value is invalid, should be in (0, 12]");
				}
			}
			else {
				if ((weight <= 0) || (weight > 100)) {
					retval = Status.createErrorStatus("The weight value is invalid, should be in (0, 100]");
				}
			}
		}

		return retval;
	}

}