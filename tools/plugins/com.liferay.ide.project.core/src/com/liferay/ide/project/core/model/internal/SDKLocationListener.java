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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Simon Jiang
 */
public class SDKLocationListener extends FilteredListener<PropertyContentEvent> implements SapphireContentAccessor {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		_updateLocation(_op(event));
	}

	private NewLiferayPluginProjectOp _op(PropertyContentEvent event) {
		Element element = SapphireUtil.getElement(event);

		return element.nearest(NewLiferayPluginProjectOp.class);
	}

	private void _updateLocation(NewLiferayPluginProjectOp op) {
		Path newLocationBase = null;

		Path sdkLocation = get(op.getSdkLocation());

		if (sdkLocation == null) {
			return;
		}

		SDK sdk = SDKUtil.createSDKFromLocation(PathBridge.create(sdkLocation));

		op.setImportProjectStatus(false);

		if (sdk == null) {
			return;
		}

		switch (get(op.getPluginType())) {
			case portlet:
			case servicebuilder:
				newLocationBase = sdkLocation.append("portlets");

				break;
			case ext:
				newLocationBase = sdkLocation.append("ext");

				break;
			case hook:
				newLocationBase = sdkLocation.append("hooks");

				break;
			case layouttpl:
				newLocationBase = sdkLocation.append("layouttpl");

				break;
			case theme:
				newLocationBase = sdkLocation.append("themes");

				break;
			case web:
				newLocationBase = sdkLocation.append("webs");

				break;
		}

		if (newLocationBase != null) {
			NewLiferayPluginProjectOpMethods.updateLocation(op, newLocationBase);
		}
	}

}