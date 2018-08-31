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

import static com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods.supportsTypePlugin;

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
public class PluginTypeValidationService extends ValidationService {

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		NewLiferayPluginProjectOp op = _op();

		try {
			SDK sdk = SDKUtil.getWorkspaceSDK();

			if (sdk == null) {
				Path sdkLocation = SapphireUtil.getContent(op.getSdkLocation());

				if (sdkLocation != null) {
					sdk = SDKUtil.createSDKFromLocation(PathBridge.create(sdkLocation));
				}
			}

			PluginType pluginType = SapphireUtil.getContent(op.getPluginType());

			if (sdk != null) {
				if (pluginType.equals(PluginType.web) && !supportsTypePlugin(op, "web")) {
					StringBuilder sb = new StringBuilder();

					sb.append("The selected Plugins SDK does not support creating new web type plugins. ");
					sb.append("");
					sb.append("Please configure version 7.0 or greater.");

					return Status.createErrorStatus(sb.toString());
				}
				else if (pluginType.equals(PluginType.theme) && !supportsTypePlugin(op, "theme")) {
					StringBuilder sb = new StringBuilder();

					sb.append("The selected Plugins SDK does not support creating theme type plugins. ");
					sb.append("");
					sb.append("Please configure version 6.2 or less or using gulp way.");

					return Status.createErrorStatus(sb.toString());
				}
				else if (pluginType.equals(PluginType.ext) && !supportsTypePlugin(op, "ext")) {
					StringBuilder sb = new StringBuilder();

					sb.append("The selected Plugins SDK does not support creating ext type plugins. ");
					sb.append("");
					sb.append("Please try to confirm whether sdk has ext folder.");

					return Status.createErrorStatus(sb.toString());
				}
			}
			else if (pluginType.equals(PluginType.ext) && !supportsTypePlugin(op, "ext")) {
				retval = Status.createErrorStatus("New ext plugins with maven build type are no longer supported.");
			}
		}
		catch (CoreException ce) {
		}

		return retval;
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		Listener listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayPluginProjectOp op = _op();

		SapphireUtil.attachListener(op.getProjectProvider(), listener);
	}

	private NewLiferayPluginProjectOp _op() {
		return context(NewLiferayPluginProjectOp.class);
	}

}