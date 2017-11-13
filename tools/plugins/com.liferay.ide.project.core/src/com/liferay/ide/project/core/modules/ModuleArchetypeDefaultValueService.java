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

import com.liferay.ide.project.core.NewLiferayProjectProvider;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Simon Jiang
 */
public class ModuleArchetypeDefaultValueService extends DefaultValueService {

	@Override
	public void dispose() {
		NewLiferayModuleProjectOp op = _op();

		op.property(NewLiferayModuleProjectOp.PROP_PROJECT_TEMPLATE_NAME).detach(_listener);

		super.dispose();
	}

	@Override
	protected String compute() {
		NewLiferayModuleProjectOp op = _op();

		String templateName = op.getProjectTemplateName().content();

		NewLiferayProjectProvider<BaseModuleOp> provider = op.getProjectProvider().content();

		return provider.getData("archetypeGAV", String.class, templateName).get(0);
	}

	@Override
	protected void initDefaultValueService() {
		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(final PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayModuleProjectOp op = _op();

		op.property(NewLiferayModuleProjectOp.PROP_PROJECT_TEMPLATE_NAME).attach(_listener);
	}

	private NewLiferayModuleProjectOp _op() {
		return context(NewLiferayModuleProjectOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}