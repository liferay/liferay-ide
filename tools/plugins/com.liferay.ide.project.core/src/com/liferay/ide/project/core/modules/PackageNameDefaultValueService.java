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

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Andy Wu
 * @author Gregory Amerson
 */
public class PackageNameDefaultValueService extends DefaultValueService {

	@Override
	public void dispose() {
		NewLiferayModuleProjectOp op = _op();

		if (op != null) {
			op.property(NewLiferayModuleProjectOp.PROP_PROJECT_NAME).detach(_listener);
		}

		super.dispose();
	}

	@Override
	protected String compute() {
		String retVal = "";

		NewLiferayModuleProjectOp op = _op();

		String projectName = op.getProjectName().content(true);

		if (projectName != null) {
			String packageName = projectName.replace('-', '.');

			retVal = packageName.replace(' ', '.');
		}

		return retVal;
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

		NewLiferayModuleProjectOp op = _op();

		op.property(NewLiferayModuleProjectOp.PROP_PROJECT_NAME).attach(_listener);
	}

	private NewLiferayModuleProjectOp _op() {
		return context(NewLiferayModuleProjectOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}