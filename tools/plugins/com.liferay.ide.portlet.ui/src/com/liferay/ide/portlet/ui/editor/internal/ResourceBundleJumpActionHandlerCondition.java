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

package com.liferay.ide.portlet.ui.editor.internal;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.services.RelativePathService;
import org.eclipse.sapphire.ui.forms.PropertyEditorCondition;
import org.eclipse.sapphire.ui.forms.PropertyEditorPart;

/**
 * @author Kamesh Sampath
 */
public class ResourceBundleJumpActionHandlerCondition extends PropertyEditorCondition {

	@Override
	protected boolean evaluate(PropertyEditorPart part) {
		Property property = part.property();

		PropertyDef propertyDef = property.definition();

		if ((propertyDef instanceof ValueProperty) && Path.class.isAssignableFrom(propertyDef.getTypeClass()) &&
			(property.service(RelativePathService.class) != null)) {

			return true;
		}

		return false;
	}

}