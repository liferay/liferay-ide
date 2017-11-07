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

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Gregory Amerson
 */
public class DisplayNameDefaultValueService extends DefaultValueService {

	@Override
	public void dispose() {
		NewLiferayPluginProjectOp op = _op();

		if ((op != null) && !op.disposed()) {
			op.property(NewLiferayPluginProjectOp.PROP_PROJECT_NAME).detach(_listener);
		}

		super.dispose();
	}

	@Override
	protected String compute() {
		NewLiferayPluginProjectOp op = _op();

		return ProjectUtil.convertToDisplayName(op.getProjectName().content());
	}

	@Override
	protected void initDefaultValueService() {
		super.initDefaultValueService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayPluginProjectOp op = _op();

		op.property(NewLiferayPluginProjectOp.PROP_PROJECT_NAME).attach(_listener);
	}

	private NewLiferayPluginProjectOp _op() {
		return context(NewLiferayPluginProjectOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}