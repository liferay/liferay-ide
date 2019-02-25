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

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
public class PortletFrameworkValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		NewLiferayPluginProjectOp op = _op();

		SapphireUtil.detachListener(op.property(NewLiferayPluginProjectOp.PROP_PROJECT_PROVIDER), _listener);

		super.dispose();
	}

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		NewLiferayPluginProjectOp op = _op();

		ILiferayProjectProvider projectProvider = get(op.getProjectProvider());
		IPortletFramework portletFramework = get(op.getPortletFramework());

		if (!portletFramework.supports(projectProvider)) {
			return Status.createErrorStatus(
				"Selected portlet framework is not supported with " + projectProvider.getDisplayName());
		}

		try {
			if ("ant".equals(projectProvider.getShortName())) {
				SDK sdk = SDKUtil.getWorkspaceSDK();

				if (sdk != null) {
					Version requiredVersion = Version.parseVersion(portletFramework.getRequiredSDKVersion());
					Version sdkVersion = Version.parseVersion(sdk.getVersion());

					if (CoreUtil.compareVersions(requiredVersion, sdkVersion) > 0) {
						retval = Status.createErrorStatus(
							"Selected portlet framework requires SDK version at least " + requiredVersion);
					}
				}
			}
		}
		catch (CoreException ce) {
		}

		return retval;
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayPluginProjectOp op = _op();

		SapphireUtil.attachListener(op.property(NewLiferayPluginProjectOp.PROP_PROJECT_PROVIDER), _listener);
	}

	private NewLiferayPluginProjectOp _op() {
		return context(NewLiferayPluginProjectOp.class);
	}

	private FilteredListener<PropertyContentEvent> _listener;

}