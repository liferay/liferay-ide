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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 * @author Terry Jia
 * @author Simon Jiang
 */
public class ProjectNameValidationService extends ValidationService {

	@Override
	public void dispose() {
		super.dispose();

		NewLiferayPluginProjectOp op = _op();

		op.detach(_listener, "*");
	}

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		NewLiferayPluginProjectOp op = _op();

		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = SapphireUtil.getContent(
			op.getProjectProvider());

		if ("ant".equals(provider.getShortName())) {
			SDK sdk = null;

			try {
				sdk = SDKUtil.getWorkspaceSDK();

				if (sdk != null) {
					IStatus sdkStatus = sdk.validate();

					if (!sdkStatus.isOK()) {
						IStatus status = sdkStatus.getChildren()[0];

						retval = Status.createErrorStatus(status.getMessage());
					}
				}
			}
			catch (CoreException ce) {
				retval = Status.createErrorStatus(ce);
			}
		}

		String currentProjectName = SapphireUtil.getContent(op.getProjectName());

		if (currentProjectName != null) {
			IStatus nameStatus = CoreUtil.validateName(currentProjectName, IResource.PROJECT);

			if (!nameStatus.isOK()) {
				retval = StatusBridge.create(nameStatus);
			}
			else if (_isInvalidProjectName(op)) {
				Boolean projectImported = SapphireUtil.getContent(op.getImportProjectStatus());

				if (!projectImported) {
					retval = Status.createErrorStatus("A project with that name already exists.");
				}
			}
			else if (_isAntProject(op) && _isSuffixOnly(currentProjectName)) {
				retval = Status.createErrorStatus("A project name cannot only be a type suffix.");
			}
			else if (!_hasValidDisplayName(currentProjectName)) {
				retval = Status.createErrorStatus("The project name is invalid.");
			}
			else if (_isMavenProject(op) && !_isValidMavenProjectName(currentProjectName)) {
				retval = Status.createErrorStatus("The project name is invalid for a maven project");
			}
			else {
				Path currentProjectLocation = SapphireUtil.getContent(op.getLocation());

				// double check to make sure this project wont overlap with existing dir

				if ((currentProjectName != null) && (currentProjectLocation != null)) {
					String currentPath = currentProjectLocation.toOSString();

					IPath osPath = org.eclipse.core.runtime.Path.fromOSString(currentPath);

					IStatus projectStatus = provider.validateProjectLocation(currentProjectName, osPath);

					if (!projectStatus.isOK()) {
						retval = StatusBridge.create(projectStatus);
					}
				}
			}
		}

		SapphireUtil.refresh(op.getSdkLocation());

		return retval;
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				PropertyDef def = SapphireUtil.getPropertyDef(event);

				if (!def.equals(NewLiferayPluginProjectOp.PROP_DISPLAY_NAME) &&
					!def.equals(NewLiferayPluginProjectOp.PROP_FINAL_PROJECT_NAME) &&
					!def.equals(NewLiferayPluginProjectOp.PROP_PORTLET_NAME) &&
					!def.equals(NewLiferayPluginProjectOp.PROP_PROJECT_NAMES) &&
					!def.equals(NewLiferayPluginProjectOp.PROP_PROJECT_NAME)) {

					try {
						refresh();
					}
					catch (Exception e) {
						ProjectCore.logError(e);
					}
				}
			}

		};

		NewLiferayPluginProjectOp op = _op();

		op.attach(_listener, "*");
	}

	private boolean _hasValidDisplayName(String currentProjectName) {
		String currentDisplayName = ProjectUtil.convertToDisplayName(currentProjectName);

		return !CoreUtil.isNullOrEmpty(currentDisplayName);
	}

	private boolean _isAntProject(NewLiferayPluginProjectOp op) {
		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = SapphireUtil.getContent(
			op.getProjectProvider());

		return "ant".equals(provider.getShortName());
	}

	private boolean _isInvalidProjectName(NewLiferayPluginProjectOp op) {
		String projectName = SapphireUtil.getContent(op.getProjectName());

		if (FileUtil.exists(CoreUtil.getProject(projectName))) {
			return true;
		}

		if (!_isAntProject(op)) {
			return false;
		}

		String pluginTypeValue;

		switch (SapphireUtil.getContent(op.getPluginType())) {
			case servicebuilder:
			case portlet:
				pluginTypeValue = ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;

				break;

			case hook:
				pluginTypeValue = ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX;

				break;

			case ext:
				pluginTypeValue = ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX;

				break;

			case layouttpl:
				pluginTypeValue = ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;

				break;

			case theme:
				pluginTypeValue = ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX;

				break;

			case web:
				pluginTypeValue = ISDKConstants.WEB_PLUGIN_PROJECT_SUFFIX;

				break;

			default:
				pluginTypeValue = ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;
		}

		if (!projectName.endsWith(pluginTypeValue)) {
			return FileUtil.exists(CoreUtil.getProject(projectName + pluginTypeValue));
		}

		return false;
	}

	private boolean _isMavenProject(NewLiferayPluginProjectOp op) {
		NewLiferayProjectProvider<NewLiferayPluginProjectOp> provider = SapphireUtil.getContent(
			op.getProjectProvider());

		return "maven".equals(provider.getShortName());
	}

	private boolean _isSuffixOnly(String currentProjectName) {
		for (PluginType type : PluginType.values()) {
			String name = "-" + type.name();

			if (!type.equals(PluginType.servicebuilder) && name.equals(currentProjectName)) {
				return true;
			}
		}

		return false;
	}

	private boolean _isValidMavenProjectName(String currentProjectName) {

		/**
		 *  IDE-1349, use the same logic as maven uses to validate artifactId to validate maven project name.
		 *  See org.apache.maven.model.validation.DefaultModelValidator.validateId();
		 */
		return currentProjectName.matches(_MAVEN_PROJECT_NAME_REGEX);
	}

	private NewLiferayPluginProjectOp _op() {
		return context(NewLiferayPluginProjectOp.class);
	}

	private static final String _MAVEN_PROJECT_NAME_REGEX = "[A-Za-z0-9_\\-.]+";

	private FilteredListener<PropertyContentEvent> _listener;

}