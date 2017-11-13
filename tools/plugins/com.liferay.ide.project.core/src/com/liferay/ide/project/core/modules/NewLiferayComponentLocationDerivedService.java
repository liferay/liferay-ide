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

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;

/**
 * @author Simon Jiang
 */
public class NewLiferayComponentLocationDerivedService extends DerivedValueService {

	@Override
	public void dispose() {
		NewLiferayComponentOp op = _op();

		if (op != null) {
			op.property(NewLiferayComponentOp.PROP_PROJECT_NAME).detach(_listener);
		}

		super.dispose();
	}

	@Override
	protected String compute() {
		String retVal = null;

		Value<String> projectName = _op().getProjectName();

		if (projectName != null) {
			String iProjectName = projectName.content(true);

			if (iProjectName != null) {
				IProject project = CoreUtil.getProject(iProjectName);

				if (project != null) {
					return project.getLocation().toOSString();
				}
			}
		}

		return retVal;
	}

	@Override
	protected void initDerivedValueService() {
		super.initDerivedValueService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayComponentOp op = _op();

		op.property(NewLiferayComponentOp.PROP_PROJECT_NAME).attach(_listener);
	}

	private NewLiferayComponentOp _op() {
		return context(NewLiferayComponentOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}