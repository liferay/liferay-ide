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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.ValuePropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class LocationListener extends FilteredListener<ValuePropertyContentEvent> implements SapphireContentAccessor {

	@Override
	protected void handleTypedEvent(ValuePropertyContentEvent event) {
		NewLiferayPluginProjectOp op = op(event);

		boolean useDefaultLocation = get(op.getUseDefaultLocation());

		if (useDefaultLocation) {
			return;
		}

		String afterValue = event.after();

		String beforeValue = event.before();

		if ((beforeValue == null) && (afterValue != null)) {
			NewLiferayPluginProjectOpMethods.updateLocation(op, new Path(afterValue));
		}
	}

	protected NewLiferayPluginProjectOp op(PropertyContentEvent event) {
		Element element = SapphireUtil.getElement(event);

		return element.nearest(NewLiferayPluginProjectOp.class);
	}

}