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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.services.ServiceCondition;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ValueLabelService;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("rawtypes")
public class LiferayComponentTemplateValueLabelService extends ValueLabelService {

	@Override
	public String provide(String value) {
		IComponentTemplate componentTemplate = ProjectCore.getComponentTemplate(value);

		if (componentTemplate != null) {
			return componentTemplate.getDisplayName();
		}

		return value;
	}

	public static class Condition extends ServiceCondition {

		@Override
		public boolean applicable(ServiceContext context) {
			boolean retval = false;

			ValueProperty prop = context.find(ValueProperty.class);

			if ((prop != null) && prop.equals(NewLiferayComponentOp.PROP_COMPONENT_CLASS_TEMPLATE_NAME)) {
				retval = true;
			}

			return retval;
		}

	}

}