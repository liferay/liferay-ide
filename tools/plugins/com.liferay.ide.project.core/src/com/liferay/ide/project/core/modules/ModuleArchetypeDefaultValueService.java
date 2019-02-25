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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;

import java.util.List;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Simon Jiang
 */
public class ModuleArchetypeDefaultValueService extends DefaultValueService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		NewLiferayModuleProjectOp op = _op();

		SapphireUtil.detachListener(op.property(NewLiferayModuleProjectOp.PROP_PROJECT_TEMPLATE_NAME), _listener);

		super.dispose();
	}

	@Override
	protected String compute() {
		String templateName = get(_op().getProjectTemplateName());

		NewLiferayProjectProvider<BaseModuleOp> provider = get(_op().getProjectProvider());

		List<String> data = provider.getData("archetypeGAV", String.class, templateName);

		return data.get(0);
	}

	@Override
	protected void initDefaultValueService() {
		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(final PropertyContentEvent event) {
				refresh();
			}

		};

		SapphireUtil.attachListener(_op().property(NewLiferayModuleProjectOp.PROP_PROJECT_TEMPLATE_NAME), _listener);
	}

	private NewLiferayModuleProjectOp _op() {
		return context(NewLiferayModuleProjectOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}