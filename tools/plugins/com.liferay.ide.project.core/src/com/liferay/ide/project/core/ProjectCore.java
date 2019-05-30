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

package com.liferay.ide.project.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.descriptor.IDescriptorOperation;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;
import com.liferay.ide.project.core.modules.IComponentTemplate;
import com.liferay.ide.project.core.modules.LiferayComponentTemplateReader;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plugin life cycle
 *
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 */
@SuppressWarnings("rawtypes")
public class ProjectCore extends Plugin {

	public static final String LIFERAY_PROJECT_MARKER_TYPE = "com.liferay.ide.project.core.LiferayProjectMarker";

	public static final String PLUGIN_ID = "com.liferay.ide.project.core";

	public static final String PREF_CREATE_NEW_PORLET = "create-new-portlet";

	public static final String PREF_DEFAULT_JSF_MODULE_PROJECT_BUILD_TYPE_OPTION =
		"project-jsf-module-default-build-type-option";

	public static final String PREF_DEFAULT_JSF_MODULE_PROJECT_MAVEN_GROUPID =
		"default-jsf-module-project-maven-groupid";

	public static final String PREF_DEFAULT_LIFERAY_VERSION_OPTION = "default-liferay-version-option";

	public static final String PREF_DEFAULT_MODULE_FRAGMENT_PROJECT_BUILD_TYPE_OPTION =
		"project-module-fragment-default-build-type-option";

	public static final String PREF_DEFAULT_MODULE_PROJECT_BUILD_TYPE_OPTION =
		"project-module-default-build-type-option";

	public static final String PREF_DEFAULT_MODULE_PROJECT_MAVEN_GROUPID = "default-module-project-maven-groupid";

	public static final String PREF_DEFAULT_PLUGIN_PROJECT_BUILD_TYPE_OPTION =
		"project-plugin_default-build-type-option";

	// The key of default project build type for creating a new liferay plug in project

	public static final String PREF_DEFAULT_PLUGIN_PROJECT_MAVEN_GROUPID = "default-plugin-project-maven-groupid";

	public static final String PREF_DEFAULT_WORKSPACE_PROJECT_BUILD_TYPE_OPTION =
		"project-workspace-default-build-type-option";

	public static final String PREF_INCLUDE_SAMPLE_CODE = "include-sample-code";

	public static final String USE_PROJECT_SETTINGS = "use-project-settings";

	public static IStatus createErrorStatus(Exception e) {
		return createErrorStatus(PLUGIN_ID, e);
	}

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(PLUGIN_ID, msg);
	}

	public static IStatus createErrorStatus(String pluginId, String msg) {
		return new Status(IStatus.ERROR, pluginId, msg);
	}

	public static IStatus createErrorStatus(String pluginId, String msg, Throwable e) {
		return new Status(IStatus.ERROR, pluginId, msg, e);
	}

	public static IStatus createErrorStatus(String pluginId, Throwable t) {
		return new Status(IStatus.ERROR, pluginId, t.getMessage(), t);
	}

	public static IStatus createWarningStatus(String message) {
		return new Status(IStatus.WARNING, PLUGIN_ID, message);
	}

	public static IStatus createWarningStatus(String message, String id) {
		return new Status(IStatus.WARNING, id, message);
	}

	public static IStatus createWarningStatus(String message, String id, Exception e) {
		return new Status(IStatus.WARNING, id, message, e);
	}

	public static IComponentTemplate getComponentTemplate(String templateName) {
		for (IComponentTemplate template : getComponentTemplates()) {
			if (templateName.equals(template.getShortName())) {
				return template;
			}
		}

		return null;
	}

	public static IComponentTemplate[] getComponentTemplates() {
		if (_componentTemplateReader == null) {
			_componentTemplateReader = new LiferayComponentTemplateReader();
		}

		return _componentTemplateReader.getComponentTemplates();
	}

	public static ProjectCore getDefault() {
		return _plugin;
	}

	public static IPortletFramework getPortletFramework(String name) {
		for (IPortletFramework framework : getPortletFrameworks()) {
			if (name.equals(framework.getShortName())) {
				return framework;
			}
		}

		return null;
	}

	public static synchronized IPortletFramework[] getPortletFrameworks() {
		if (_portletFrameworks != null) {
			return _portletFrameworks;
		}

		IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();

		IConfigurationElement[] elements = extensionRegistry.getConfigurationElementsFor(
			IPortletFramework.EXTENSION_ID);

		if (ListUtil.isEmpty(elements)) {
			return _portletFrameworks;
		}

		List<IPortletFramework> frameworks = new ArrayList<>();

		for (IConfigurationElement element : elements) {
			String id = element.getAttribute(IPortletFramework.ID);
			String shortName = element.getAttribute(IPortletFramework.SHORT_NAME);
			String displayName = element.getAttribute(IPortletFramework.DISPLAY_NAME);
			String description = element.getAttribute(IPortletFramework.DESCRIPTION);
			String requiredSDKVersion = element.getAttribute(IPortletFramework.REQUIRED_SDK_VERSION);

			boolean isDefault = Boolean.parseBoolean(element.getAttribute(IPortletFramework.DEFAULT));

			boolean advanced = Boolean.parseBoolean(element.getAttribute(IPortletFramework.ADVANCED));

			boolean requiresAdvanced = Boolean.parseBoolean(element.getAttribute(IPortletFramework.REQUIRES_ADVANCED));

			URL helpUrl = null;

			try {
				helpUrl = new URL(element.getAttribute(IPortletFramework.HELP_URL));
			}
			catch (Exception e1) {
			}

			try {
				AbstractPortletFramework framework = (AbstractPortletFramework)element.createExecutableExtension(
					"class");

				framework.setId(id);
				framework.setShortName(shortName);
				framework.setDisplayName(displayName);
				framework.setDescription(description);
				framework.setRequiredSDKVersion(requiredSDKVersion);
				framework.setHelpUrl(helpUrl);
				framework.setDefault(isDefault);
				framework.setAdvanced(advanced);
				framework.setRequiresAdvanced(requiresAdvanced);

				IContributor contributor = element.getContributor();

				framework.setBundleId(contributor.getName());

				frameworks.add(framework);
			}
			catch (Exception e) {
				logError("Could not create portlet framework.", e);
			}
		}

		_portletFrameworks = frameworks.toArray(new IPortletFramework[0]);

		// sort the array so that the default template is first

		Arrays.sort(
			_portletFrameworks, 0, _portletFrameworks.length,
			new Comparator<IPortletFramework>() {

				@Override
				public int compare(IPortletFramework o1, IPortletFramework o2) {
					if (o1.isDefault() && !o2.isDefault()) {
						return -1;
					}
					else if (!o1.isDefault() && o2.isDefault()) {
						return 1;
					}

					String shortName1 = o1.getShortName();

					return shortName1.compareTo(o2.getShortName());
				}

			});

		return _portletFrameworks;
	}

	public static void logError(IStatus status) {
		ILog log = getDefault().getLog();

		log.log(status);
	}

	public static void logError(String msg) {
		logError(createErrorStatus(msg));
	}

	public static void logError(String msg, Exception e) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(PLUGIN_ID, msg, e));
	}

	public static void logError(String msg, Throwable t) {
		ILog log = getDefault().getLog();

		log.log(createErrorStatus(PLUGIN_ID, msg, t));
	}

	public static void logError(Throwable t) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, t.getMessage(), t));
	}

	public static void logWarning(String msg) {
		logError(createWarningStatus(msg));
	}

	public static IStatus operate(IProject project, Class<? extends IDescriptorOperation> type, Object... params) {
		IStatus status = Status.OK_STATUS;

		LiferayDescriptorHelper[] helpers = _getDescriptorHelpers(project, type);

		for (LiferayDescriptorHelper helper : helpers) {
			IDescriptorOperation descriptorOperation = helper.getDescriptorOperation(type);

			status = descriptorOperation.execute(params);

			if (!status.isOK()) {
				return status;
			}
		}

		return status;
	}

	public ProjectCore() {
		_pluginPackageResourceListener = new PluginPackageResourceListener();
		_sdkBuildPropertiesResourceListener = new SDKBuildPropertiesResourceListener();
		_sdkProjectDeleteListener = new SDKProjectDeleteListener();
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		_plugin = this;

		IWorkspace workspace = CoreUtil.getWorkspace();

		workspace.addResourceChangeListener(_pluginPackageResourceListener, IResourceChangeEvent.POST_CHANGE);

		workspace.addResourceChangeListener(_sdkBuildPropertiesResourceListener, IResourceChangeEvent.POST_CHANGE);

		workspace.addResourceChangeListener(_sdkProjectDeleteListener, IResourceChangeEvent.PRE_DELETE);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		_plugin = null;

		super.stop(context);

		IWorkspace workspace = CoreUtil.getWorkspace();

		if (_pluginPackageResourceListener != null) {
			workspace.removeResourceChangeListener(_pluginPackageResourceListener);
		}

		if (_sdkBuildPropertiesResourceListener != null) {
			workspace.removeResourceChangeListener(_sdkBuildPropertiesResourceListener);
		}

		if (_sdkProjectDeleteListener != null) {
			workspace.removeResourceChangeListener(_sdkProjectDeleteListener);
		}
	}

	private static LiferayDescriptorHelper[] _getDescriptorHelpers(
		IProject project, Class<? extends IDescriptorOperation> type) {

		List<LiferayDescriptorHelper> retval = new ArrayList<>();

		project = CoreUtil.getLiferayProject(project);

		if (FileUtil.notExists(project)) {
			return null;
		}

		LiferayDescriptorHelperReader helperReader = LiferayDescriptorHelperReader.getInstance();

		LiferayDescriptorHelper[] allHelpers = helperReader.getAllHelpers();

		for (LiferayDescriptorHelper helper : allHelpers) {
			helper.setProject(project);

			IFile descriptorFile = helper.getDescriptorFile();

			if (FileUtil.exists(descriptorFile) && (helper.getDescriptorOperation(type) != null)) {
				retval.add(helper);
			}
		}

		return retval.toArray(new LiferayDescriptorHelper[0]);
	}

	private static LiferayComponentTemplateReader _componentTemplateReader;
	private static ProjectCore _plugin;
	private static PluginPackageResourceListener _pluginPackageResourceListener;
	private static IPortletFramework[] _portletFrameworks;
	private static SDKBuildPropertiesResourceListener _sdkBuildPropertiesResourceListener;
	private static SDKProjectDeleteListener _sdkProjectDeleteListener;

}