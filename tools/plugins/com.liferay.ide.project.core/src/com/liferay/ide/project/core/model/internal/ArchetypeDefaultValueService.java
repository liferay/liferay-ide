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
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;

import java.util.List;

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

		SapphireUtil.detachListener(op.property(NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK), _listener);
		SapphireUtil.detachListener(op.property(NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK_ADVANCED), _listener);

		super.dispose();
	}

	@Override
	protected String compute() {
		NewLiferayPluginProjectOp op = _op();

		PluginType pluginType = SapphireUtil.getContent(op.getPluginType());

		String frameworkType = null;

		if (pluginType.equals(PluginType.portlet)) {
			IPortletFramework portletFramework = SapphireUtil.getContent(op.getPortletFramework());

			if (portletFramework.isRequiresAdvanced()) {
				IPortletFramework framework = SapphireUtil.getContent(op.getPortletFrameworkAdvanced());

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

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = SapphireUtil.getContent(
			op.getProjectProvider());

		List<String> data = provider.getData("archetypeGAV", String.class, frameworkType);

		return data.get(0);
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

		SapphireUtil.attachListener(op.property(NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK), _listener);
		SapphireUtil.attachListener(op.property(NewLiferayPluginProjectOp.PROP_PORTLET_FRAMEWORK_ADVANCED), _listener);
	}

	private NewLiferayPluginProjectOp _op() {
		return context(NewLiferayPluginProjectOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}