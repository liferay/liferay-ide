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

package com.liferay.ide.project.core.jsf;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
public class JSFModuleProjectNameListener
	extends FilteredListener<PropertyContentEvent> implements SapphireContentAccessor {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		_updateLocation(op(event));
	}

	protected NewLiferayJSFModuleProjectOp op(PropertyContentEvent event) {
		Property property = event.property();

		Element element = property.element();

		return element.nearest(NewLiferayJSFModuleProjectOp.class);
	}

	private void _updateLocation(NewLiferayJSFModuleProjectOp op) {
		String currentProjectName = get(op.getProjectName());

		Path newLocationBase = null;

		if ((currentProjectName == null) || CoreUtil.isNullOrEmpty(currentProjectName.trim())) {
			return;
		}

		boolean useDefaultLocation = get(op.getUseDefaultLocation());

		if (useDefaultLocation) {
			newLocationBase = PathBridge.create(CoreUtil.getWorkspaceRootLocation());
		}
		else {
			newLocationBase = get(op.getLocation());
		}

		if (newLocationBase != null) {
			op.setLocation(newLocationBase);
		}
	}

}