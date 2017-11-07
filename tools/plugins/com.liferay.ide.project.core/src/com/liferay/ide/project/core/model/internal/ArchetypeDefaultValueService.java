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

import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Simon Jiang
 */
public class ArchetypeDefaultValueService extends DefaultValueService {

	@Override
	public void dispose() {
		NewLiferayPluginProjectOp op = _op();

		op.property(NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK).detach(_listener);
		op.property(NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK_ADVANCED).detach(_listener);

		super.dispose();
	}

	@Override
	protected String compute() {
		NewLiferayPluginProjectOp op = _op();

		PluginType pluginType = op.getPluginType().content();

		String frameworkType = null;

		if (pluginType.equals(PluginType.portlet)) {
			IPortletFramework portletFramework = op.getPortletFramework().content();

			if (portletFramework.isRequiresAdvanced()) {
				IPortletFramework framework = op.getPortletFrameworkAdvanced().content();

				frameworkType = framework.getShortName();
			}
			else {
				frameworkType = portletFramework.getShortName();
			}
		}
		else {
			frameworkType = pluginType.name();
		}

		frameworkType = frameworkType.replaceAll("_", "-");

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = op.getProjectProvider().content();

		return provider.getData("archetypeGAV", String.class, frameworkType).get(0);
	}

	@Override
	protected void initDefaultValueService() {
		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayPluginProjectOp op = _op();

		op.property(NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK).attach(_listener);
		op.property(NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK_ADVANCED).attach(_listener);
	}

	private NewLiferayPluginProjectOp _op() {
		return context(NewLiferayPluginProjectOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}