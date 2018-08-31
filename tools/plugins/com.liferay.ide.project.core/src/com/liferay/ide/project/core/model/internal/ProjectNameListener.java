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

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Kuo Zhang
 * @author Simon Jiang
 */
public class ProjectNameListener extends FilteredListener<PropertyContentEvent> {

	@Override
	protected void handleTypedEvent(PropertyContentEvent event) {
		NewLiferayPluginProjectOp op = op(event);

		op.setImportProjectStatus(false);

		NewLiferayPluginProjectOpMethods.updateLocation(op);
	}

	protected NewLiferayPluginProjectOp op(PropertyContentEvent event) {
		Element element = SapphireUtil.getElement(event);

		return element.nearest(NewLiferayPluginProjectOp.class);
	}

}