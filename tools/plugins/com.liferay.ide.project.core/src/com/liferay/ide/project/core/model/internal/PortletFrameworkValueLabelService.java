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
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ValueLabelService;

/**
 * @author Gregory Amerson
 */
public class PortletFrameworkValueLabelService extends ValueLabelService {

	@Override
	public String provide(String value) {
		IPortletFramework framework = ProjectCore.getPortletFramework(value);

		if (framework != null) {
			return framework.getDisplayName();
		}

		return value;
	}

	public static class Condition extends ServiceCondition {

		@Override
		public boolean applicable(final ServiceContext context) {
			ValueProperty prop = context.find(ValueProperty.class);

			if ((prop != null) &&
				(prop.equals(NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK) ||
				 prop.equals(NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK_ADVANCED))) {

				return true;
			}

			return false;
		}

	}

}