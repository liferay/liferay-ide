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

package com.liferay.ide.project.core.spring;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Simon Jiang
 */
public class SpringPackageNameDefaultValueService extends DefaultValueService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		NewLiferaySpringProjectOp op = _op();

		if (op != null) {
			SapphireUtil.detachListener(op.property(NewLiferaySpringProjectOp.PROP_PROJECT_NAME), _listener);
		}

		super.dispose();
	}

	@Override
	protected String compute() {
		String retVal = "";

		NewLiferaySpringProjectOp op = _op();

		String projectName = get(op.getProjectName());

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

		NewLiferaySpringProjectOp op = _op();

		SapphireUtil.attachListener(op.property(NewLiferaySpringProjectOp.PROP_PROJECT_NAME), _listener);
	}

	private NewLiferaySpringProjectOp _op() {
		return context(NewLiferaySpringProjectOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}