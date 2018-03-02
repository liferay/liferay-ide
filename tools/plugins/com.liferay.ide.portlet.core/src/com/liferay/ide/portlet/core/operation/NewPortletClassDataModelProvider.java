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

package com.liferay.ide.portlet.core.operation;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.portlet.core.dd.LiferayDisplayDescriptorHelper;
import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;
import com.liferay.ide.project.core.IPluginWizardFragmentProperties;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.server.util.ServerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.core.search.BasicSearchEngine;
import org.eclipse.jst.j2ee.common.CommonFactory;
import org.eclipse.jst.j2ee.common.ParamValue;
import org.eclipse.jst.j2ee.internal.common.operations.JavaModelUtil;
import org.eclipse.jst.j2ee.internal.web.operations.NewWebClassDataModelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.server.core.IRuntime;

import org.osgi.framework.Version;
import org.osgi.service.prefs.Preferences;

/**
 * @author Greg Amerson
 * @author Cindy Li
 * @author Tao Tao
 * @author Terry Jia
 * @author Simon Jiang
 */
@SuppressWarnings({"restriction", "unchecked", "rawtypes"})
public class NewPortletClassDataModelProvider
	extends NewWebClassDataModelProvider
	implements INewPortletClassDataModelProperties, IPluginWizardFragmentProperties {

	public NewPortletClassDataModelProvider() {
	}

	public NewPortletClassDataModelProvider(boolean fragment) {
		this.fragment = fragment;
	}

	public NewPortletClassDataModelProvider(boolean fragment, IProject initialProject) {
		this(fragment);

		this.initialProject = initialProject;
	}

	@Override
	public Object getDefaultProperty(String propertyName) {
		if (SUPERCLASS.equals(propertyName)) {
			return QUALIFIED_MVC_PORTLET;
		}
		else if (CONSTRUCTOR.equals(propertyName)) {
			return false;
		}
		else if (INIT_OVERRIDE.equals(propertyName) || DOVIEW_OVERRIDE.equals(propertyName)) {
			return true;
		}
		else if (DESTROY_OVERRIDE.equals(propertyName)) {
			return false;
		}
		else if (JAVA_PACKAGE.equals(propertyName)) {
			return "com.test";
		}
		else if (CLASS_NAME.equals(propertyName)) {
			return "NewPortlet";
		}
		else if (PORTLET_NAME.equals(propertyName) || LIFERAY_PORTLET_NAME.equals(propertyName)) {
			return getPortletNameFromClassName(getStringProperty(CLASS_NAME));
		}
		else if (DISPLAY_NAME.equals(propertyName) || TITLE.equals(propertyName) || SHORT_TITLE.equals(propertyName)) {
			return _getDisplayNameFromPortletName(getStringProperty(PORTLET_NAME));
		}
		else if (KEYWORDS.equals(propertyName)) {
			return StringPool.EMPTY;
		}
		else if (INIT_PARAMS.equals(propertyName)) {
			return getInitParams();
		}
		else if (VIEW_MODE.equals(propertyName)) {
			return true;
		}
		else if (CREATE_JSPS.equals(propertyName)) {
			return true;
		}
		else if (CREATE_JSPS_FOLDER.equals(propertyName)) {
			if (getBooleanProperty(CREATE_NEW_PORTLET_CLASS)) {
				String tempStr = getProperty(CLASS_NAME).toString();

				String property = tempStr.toLowerCase();

				return "/html/" + property.replaceAll(_PORTLET_SUFFIX_PATTERN, "");
			}
			else {
				String tempStr = getProperty(PORTLET_NAME).toString();

				String property = tempStr.toLowerCase();

				return "/html/" + property.replaceAll(_PORTLET_SUFFIX_PATTERN, "");
			}
		}
		else if (ICON_FILE.equals(propertyName)) {
			return "/icon.png";
		}
		else if (CREATE_RESOURCE_BUNDLE_FILE.equals(propertyName)) {
			return false;
		}
		else if (CREATE_RESOURCE_BUNDLE_FILE_PATH.equals(propertyName)) {
			return "content/Language.properties";
		}
		else if (ALLOW_MULTIPLE.equals(propertyName)) {
			return false;
		}
		else if (CSS_FILE.equals(propertyName)) {
			return "/css/main.css";
		}
		else if (JAVASCRIPT_FILE.equals(propertyName)) {
			return "/js/main.js";
		}
		else if (CSS_CLASS_WRAPPER.equals(propertyName)) {
			String property = getProperty(PORTLET_NAME).toString();

			return property.toLowerCase() + "-portlet";
		}
		else if (ID.equals(propertyName)) {
			return getProperty(PORTLET_NAME);
		}
		else if (CATEGORY.equals(propertyName)) {
			return "category.sample";
		}
		else if (ENTRY_CATEGORY.equals(propertyName)) {
			return "category.my";
		}
		else if (ENTRY_WEIGHT.equals(propertyName)) {
			return "1.5";
		}
		else if (ENTRY_CLASS_NAME.equals(propertyName)) {
			return getStringProperty(CLASS_NAME) + "ControlPanelEntry";
		}
		else if (SHOW_NEW_CLASS_OPTION.equals(propertyName)) {
			return true;
		}
		else if (CREATE_NEW_PORTLET_CLASS.equals(propertyName)) {
			return true;
		}
		else if (USE_DEFAULT_PORTLET_CLASS.equals(propertyName)) {
			return false;
		}
		else if (QUALIFIED_CLASS_NAME.equals(propertyName)) {
			if (getBooleanProperty(USE_DEFAULT_PORTLET_CLASS)) {
				return QUALIFIED_MVC_PORTLET;
			}
		}
		else if (PROJECT_NAME.equals(propertyName) && (initialProject != null)) {
			return initialProject.getName();
		}
		else if (INIT_PARAMETER_NAME.equals(propertyName)) {
			String initParameterName = "template";

			ILiferayProject liferayProject = LiferayCore.create(getProject());

			ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

			if (portal != null) {
				String version = portal.getVersion();

				if (version != null) {
					Version portalVersion = Version.parseVersion(version);

					if (CoreUtil.compareVersions(portalVersion, ILiferayConstants.V610) < 0) {
						initParameterName = "jsp";
					}

					return initParameterName;
				}
			}
		}

		return super.getDefaultProperty(propertyName);
	}

	@Override
	public DataModelPropertyDescriptor getPropertyDescriptor(String propertyName) {
		if (VIEW_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "view");
		}
		else if (EDIT_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "edit");
		}
		else if (HELP_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "help");
		}
		else if (ABOUT_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "about");
		}
		else if (CONFIG_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "config");
		}
		else if (EDITDEFAULTS_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "edit_defaults");
		}
		else if (EDITGUEST_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "edit_guest");
		}
		else if (PREVIEW_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "preview");
		}
		else if (PRINT_MODE.equals(propertyName)) {
			return new DataModelPropertyDescriptor(getProperty(propertyName), "print");
		}
		else if (CATEGORY.equals(propertyName)) {
			if (getProperty(CATEGORY).equals("category.sample")) {
				return new DataModelPropertyDescriptor("category.sample", "Sample");
			}
		}
		else if (ENTRY_CATEGORY.equals(propertyName)) {

			// following if block is for modifying portlet class name will resynch the model
			// and lose the user choice
			// check for null is for switching projects of different portal versions

			Object entryCategory = getProperty(ENTRY_CATEGORY);

			if ((entryCategory != null) && (getEntryCategories() != null) &&
				(getEntryCategories().get(entryCategory) != null)) {

				Properties entryCategories = getEntryCategories();

				DataModelPropertyDescriptor descriptor = new DataModelPropertyDescriptor(
					entryCategory, entryCategories.get(entryCategory).toString());

				return descriptor;
			}

			ILiferayProject liferayProject = LiferayCore.create(getProject());

			if (liferayProject == null) {
				try {
					liferayProject = LiferayCore.create(getRuntime());
				}
				catch (CoreException ce) {
					PortletCore.logError(ce);
				}
			}

			ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

			if (portal != null) {
				Version portalVersion = Version.parseVersion(portal.getVersion());

				if (CoreUtil.compareVersions(portalVersion, ILiferayConstants.V620) < 0) {
					return new DataModelPropertyDescriptor("category.my", "My Account Section");
				}
			}

			return new DataModelPropertyDescriptor("category.my", "My Account Administration");
		}

		return super.getPropertyDescriptor(propertyName);
	}

	@Override
	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();

		// propertyNames.add(CREATE_CUSTOM_PORTLET_CLASS);
		// propertyNames.add(PORTLET_CLASS);

		propertyNames.add(TEMPLATE_STORE);
		propertyNames.add(CONTEXT_TYPE);

		propertyNames.add(INIT_OVERRIDE);
		propertyNames.add(DESTROY_OVERRIDE);
		propertyNames.add(DOVIEW_OVERRIDE);
		propertyNames.add(DOEDIT_OVERRIDE);
		propertyNames.add(DOHELP_OVERRIDE);
		propertyNames.add(DOABOUT_OVERRIDE);
		propertyNames.add(DOCONFIG_OVERRIDE);
		propertyNames.add(DOEDITDEFAULTS_OVERRIDE);
		propertyNames.add(DOEDITGUEST_OVERRIDE);
		propertyNames.add(DOPREVIEW_OVERRIDE);
		propertyNames.add(DOPRINT_OVERRIDE);
		propertyNames.add(PROCESSACTION_OVERRIDE);
		propertyNames.add(SERVERESOURCE_OVERRIDE);

		propertyNames.add(PORTLET_NAME);
		propertyNames.add(DISPLAY_NAME);
		propertyNames.add(TITLE);
		propertyNames.add(SHORT_TITLE);
		propertyNames.add(KEYWORDS);
		propertyNames.add(INIT_PARAMETER_NAME);
		propertyNames.add(INIT_PARAMS);

		propertyNames.add(VIEW_MODE);
		propertyNames.add(EDIT_MODE);
		propertyNames.add(HELP_MODE);

		propertyNames.add(ABOUT_MODE);
		propertyNames.add(CONFIG_MODE);
		propertyNames.add(EDITDEFAULTS_MODE);
		propertyNames.add(EDITGUEST_MODE);
		propertyNames.add(PREVIEW_MODE);
		propertyNames.add(PRINT_MODE);

		propertyNames.add(CREATE_RESOURCE_BUNDLE_FILE);
		propertyNames.add(CREATE_RESOURCE_BUNDLE_FILE_PATH);
		propertyNames.add(CREATE_JSPS);
		propertyNames.add(CREATE_JSPS_FOLDER);

		propertyNames.add(LIFERAY_PORTLET_NAME);
		propertyNames.add(ICON_FILE);
		propertyNames.add(ALLOW_MULTIPLE);
		propertyNames.add(CSS_FILE);
		propertyNames.add(JAVASCRIPT_FILE);
		propertyNames.add(CSS_CLASS_WRAPPER);
		propertyNames.add(ID);
		propertyNames.add(CATEGORY);
		propertyNames.add(ADD_TO_CONTROL_PANEL);
		propertyNames.add(ENTRY_CATEGORY);
		propertyNames.add(ENTRY_WEIGHT);
		propertyNames.add(CREATE_ENTRY_CLASS);
		propertyNames.add(ENTRY_CLASS_NAME);

		propertyNames.add(FACET_RUNTIME);
		propertyNames.add(REMOVE_EXISTING_ARTIFACTS);

		propertyNames.add(SHOW_NEW_CLASS_OPTION);
		propertyNames.add(CREATE_NEW_PORTLET_CLASS);
		propertyNames.add(USE_DEFAULT_PORTLET_CLASS);

		return propertyNames;
	}

	@Override
	public DataModelPropertyDescriptor[] getValidPropertyDescriptors(String propertyName) {
		if (SUPERCLASS.equals(propertyName)) {
			String defaults =
				QUALIFIED_MVC_PORTLET + StringPool.COMMA + QUALIFIED_LIFERAY_PORTLET + StringPool.COMMA +
					QUALIFIED_GENERIC_PORTLET;

			String[] defaultVals = defaults.split(StringPool.COMMA);

			try {
				IJavaProject javaProject = JavaCore.create(getProject());

				Preferences preferences = PortletCore.getPreferences();

				String superclasses = preferences.get(PortletCore.PREF_KEY_PORTLET_SUPERCLASSES_USED, null);

				if (superclasses != null) {
					String[] customVals = superclasses.split(StringPool.COMMA);

					List<String> list = new ArrayList<>();

					Collections.addAll(list, defaultVals);

					IType portletType = JavaModelUtil.findType(javaProject, "javax.portlet.Portlet");

					IJavaSearchScope scope = BasicSearchEngine.createStrictHierarchyScope(
						javaProject, portletType, true, true, null);

					for (int i = 0; i < customVals.length; i++) {
						IType type = JavaModelUtil.findType(javaProject, customVals[i]);

						if ((type != null) && scope.encloses(type)) {
							list.add(customVals[i]);
						}
					}

					return DataModelPropertyDescriptor.createDescriptors(list.toArray(), list.toArray(new String[0]));
				}
			}
			catch (JavaModelException jme) {
			}

			return DataModelPropertyDescriptor.createDescriptors(defaultVals, defaultVals);
		}
		else if (CATEGORY.equals(propertyName)) {
			Properties categories = getCategories();

			if ((categories != null) && (categories.size() > 0)) {
				return DataModelPropertyDescriptor.createDescriptors(
					categories.keySet().toArray(new Object[0]), categories.values().toArray(new String[0]));
			}
		}
		else if (ENTRY_CATEGORY.equals(propertyName)) {
			Properties entryCategories = getEntryCategories();

			if ((entryCategories != null) && (entryCategories.size() > 0)) {
				Object[] keys = entryCategories.keySet().toArray();

				Arrays.sort(keys);

				String[] values = new String[keys.length];

				for (int i = 0; i < keys.length; i++) {
					values[i] = entryCategories.getProperty(keys[i].toString());
				}

				return DataModelPropertyDescriptor.createDescriptors(keys, values);
			}
		}

		return super.getValidPropertyDescriptors(propertyName);
	}

	@Override
	public boolean isPropertyEnabled(String propertyName) {
		if (VIEW_MODE.equals(propertyName)) {
			return false;
		}
		else if (INIT_OVERRIDE.equals(propertyName)) {
			return false;
		}
		else if (ABOUT_MODE.equals(propertyName) || CONFIG_MODE.equals(propertyName) ||
				 EDITDEFAULTS_MODE.equals(propertyName) || EDITGUEST_MODE.equals(propertyName) ||
				 PREVIEW_MODE.equals(propertyName) || PRINT_MODE.equals(propertyName)) {

			if (fragment) {
				return true;
			}

			if (getProject() == null) {
				return false;
			}

			return PortletSupertypesValidator.isLiferayPortletSuperclass(getDataModel(), true);
		}
		else if (CLASS_NAME.equals(propertyName) || JAVA_PACKAGE.equals(propertyName) ||
				 SUPERCLASS.equals(propertyName)) {

			return getBooleanProperty(CREATE_NEW_PORTLET_CLASS);
		}
		else if (CREATE_RESOURCE_BUNDLE_FILE_PATH.equals(propertyName)) {
			return getBooleanProperty(CREATE_RESOURCE_BUNDLE_FILE);
		}
		else if (CREATE_JSPS_FOLDER.equals(propertyName)) {
			return getBooleanProperty(CREATE_JSPS);
		}

		return super.isPropertyEnabled(propertyName);
	}

	public boolean isValidPortletClass(String qualifiedClassName) {
		try {
			IJavaProject javaProject = JavaCore.create(getProject());

			if (javaProject != null) {
				IType portletType = JavaModelUtil.findType(javaProject, "javax.portlet.Portlet");

				if (portletType != null) {
					IJavaSearchScope scope = BasicSearchEngine.createStrictHierarchyScope(
						javaProject, portletType, true, true, null);

					IType classType = JavaModelUtil.findType(javaProject, qualifiedClassName);

					if ((classType != null) && scope.encloses(classType)) {
						return true;
					}
				}
			}
		}
		catch (JavaModelException jme) {
			PortletCore.logError(jme);
		}

		return false;
	}

	@Override
	public boolean propertySet(String propertyName, Object propertyValue) {
		if (VIEW_MODE.equals(propertyName)) {
			setProperty(DOVIEW_OVERRIDE, propertyValue);
		}
		else if (EDIT_MODE.equals(propertyName)) {
			setProperty(DOEDIT_OVERRIDE, propertyValue);
		}
		else if (HELP_MODE.equals(propertyName)) {
			setProperty(DOHELP_OVERRIDE, propertyValue);
		}
		else if (ABOUT_MODE.equals(propertyName) &&
				 PortletSupertypesValidator.isLiferayPortletSuperclass(getDataModel())) {

			setProperty(DOABOUT_OVERRIDE, propertyValue);
		}
		else if (CONFIG_MODE.equals(propertyName)) {
			setProperty(DOCONFIG_OVERRIDE, propertyValue);
		}
		else if (EDITDEFAULTS_MODE.equals(propertyName)) {
			setProperty(DOEDITDEFAULTS_OVERRIDE, propertyValue);
		}
		else if (EDITGUEST_MODE.equals(propertyName)) {
			setProperty(DOEDITGUEST_OVERRIDE, propertyValue);
		}
		else if (PREVIEW_MODE.equals(propertyName)) {
			setProperty(DOPREVIEW_OVERRIDE, propertyValue);
		}
		else if (PRINT_MODE.equals(propertyName)) {
			setProperty(DOPRINT_OVERRIDE, propertyValue);
		}
		else if (SUPERCLASS.equals(propertyName)) {
			getDataModel().notifyPropertyChange(VIEW_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(EDIT_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(HELP_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(ABOUT_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(CONFIG_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(EDITDEFAULTS_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(EDITGUEST_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(PREVIEW_MODE, IDataModel.ENABLE_CHG);
			getDataModel().notifyPropertyChange(PRINT_MODE, IDataModel.ENABLE_CHG);
		}
		else if (PORTLET_NAME.equals(propertyName)) {
			String portletName = getStringProperty(propertyName);

			getDataModel().setStringProperty(LIFERAY_PORTLET_NAME, portletName);
		}
		else if (LIFERAY_PORTLET_NAME.equals(propertyName)) {
			String liferayPortletName = getStringProperty(propertyName);

			getDataModel().setStringProperty(PORTLET_NAME, liferayPortletName);
		}
		else if (CATEGORY.equals(propertyName)) {
			String portletCategory = _findExistingCategory(propertyValue.toString());

			if (portletCategory != null) {
				getDataModel().setProperty(CATEGORY, portletCategory);
			}

			return true;
		}

		return super.propertySet(propertyName, propertyValue);
	}

	@Override
	public IStatus validate(String propertyName) {
		if (PORTLET_NAME.equals(propertyName)) {

			// must have a valid portlet name

			String portletName = getStringProperty(PORTLET_NAME);

			if ((portletName == null) || (portletName.length() == 0)) {
				return PortletCore.createErrorStatus(Msgs.portletNameEmpty);
			}

			PortletDescriptorHelper portletDescriptorHelper = new PortletDescriptorHelper(getTargetProject());

			String[] portletNames = portletDescriptorHelper.getAllPortletNames();

			for (String name : portletNames) {
				if (name.equals(portletName)) {
					return PortletCore.createErrorStatus(Msgs.portletNameExists);
				}
			}
		}
		else if (CREATE_RESOURCE_BUNDLE_FILE_PATH.equals(propertyName)) {
			if (!getBooleanProperty(CREATE_RESOURCE_BUNDLE_FILE)) {
				return Status.OK_STATUS;
			}

			boolean validPath = false;
			boolean validFileName = false;

			String val = getStringProperty(propertyName);

			if (CoreUtil.isNullOrEmpty(val)) {
				return PortletCore.createErrorStatus(Msgs.resourceBundleFilePathValid);
			}

			try {
				IPath path = new Path(val);

				validPath = path.isValidPath(val);

				if ("properties".equals(path.getFileExtension())) {
					validFileName = true;
				}
			}
			catch (Exception e) {

				// eat errors

			}

			if (!validPath) {
				return PortletCore.createErrorStatus(Msgs.resourceBundleFilePathValid);
			}

			if (validFileName) {
				return super.validate(propertyName);
			}
			else {
				return PortletCore.createWarningStatus(Msgs.resourceBundleFilePathEndWithProperties);
			}
		}
		else if (CREATE_JSPS_FOLDER.equals(propertyName)) {
			if (!getBooleanProperty(CREATE_JSPS)) {
				return Status.OK_STATUS;
			}

			String folderValue = getStringProperty(propertyName);

			if (CoreUtil.isNullOrEmpty(folderValue)) {
				return PortletCore.createErrorStatus(Msgs.jspFolderNotEmpty);
			}

			IProject targetProject = getTargetProject();

			if (!CoreUtil.isNullOrEmpty(folderValue) && (targetProject != null)) {
				IWebProject webproject = LiferayCore.create(IWebProject.class, targetProject);

				if (webproject != null) {
					IFolder defaultDocroot = webproject.getDefaultDocrootFolder();

					IStatus validation = _validateFolder(defaultDocroot, folderValue);

					if ((validation != null) && !validation.isOK()) {
						return validation;
					}

					// make sure path first segment isn't the same as the portlet name

					String path = new Path(folderValue).segment(0);

					if (!CoreUtil.isNullOrEmpty(path) && path.equals(getStringProperty(PORTLET_NAME))) {
						return PortletCore.createErrorStatus(Msgs.jspFolderNotMatchPortletName);
					}
				}
			}
		}
		else if (SOURCE_FOLDER.equals(propertyName) && fragment) {
			return Status.OK_STATUS;
		}
		else if (SUPERCLASS.equals(propertyName)) {
			String superclass = getStringProperty(propertyName);

			if (CoreUtil.isNullOrEmpty(superclass)) {
				return PortletCore.createErrorStatus(Msgs.specifyPortletSuperclass);
			}

			if (fragment) {
				return JavaConventions.validateJavaTypeName(superclass, JavaCore.VERSION_1_5, JavaCore.VERSION_1_5);
			}

			if (!isValidPortletClass(superclass)) {
				return PortletCore.createErrorStatus(Msgs.portletSuperclassValid);
			}
		}
		else if (CATEGORY.equals(propertyName)) {
			String category = getStringProperty(propertyName);

			if (category.matches("\\s*")) {
				return PortletCore.createErrorStatus(Msgs.categoryNameEmpty);
			}
		}
		else if (ENTRY_WEIGHT.equals(propertyName)) {
			if (!getBooleanProperty(ADD_TO_CONTROL_PANEL)) {
				return Status.OK_STATUS;
			}

			String entryweight = getStringProperty(propertyName);

			if (!CoreUtil.isNumeric(entryweight)) {
				return PortletCore.createErrorStatus(Msgs.specifyValidDouble);
			}

			return Status.OK_STATUS;
		}
		else if (ENTRY_CLASS_NAME.equals(propertyName)) {
			if (!getBooleanProperty(ADD_TO_CONTROL_PANEL) || !getBooleanProperty(CREATE_ENTRY_CLASS)) {
				return Status.OK_STATUS;
			}

			String entryclasswrapper = getStringProperty(propertyName);

			if (validateJavaClassName(entryclasswrapper).getSeverity() != IStatus.ERROR) {
				IStatus existsStatus = canCreateTypeInClasspath(entryclasswrapper);

				if (existsStatus.matches(IStatus.ERROR | IStatus.WARNING)) {
					return existsStatus;
				}
			}

			return validateJavaClassName(entryclasswrapper);
		}
		else if (CLASS_NAME.equals(propertyName)) {
			if (getBooleanProperty(USE_DEFAULT_PORTLET_CLASS)) {
				return Status.OK_STATUS;
			}
		}

		return super.validate(propertyName);
	}

	protected ParamValue[] createDefaultParamValuesForModes(String[] modes, String[] names, String[] values) {
		Assert.isTrue(
			modes != null &&
			names != null &&
			values != null &&
			 (modes.length == names.length) &&
			 (names.length == values.length));

		List<ParamValue> defaultParams = new ArrayList<>();

		// for each path value need to prepend a path that will be specific to
		// the portlet being created

		String prependPath = getDataModel().getStringProperty(CREATE_JSPS_FOLDER);

		for (int i = 0; i < modes.length; i++) {
			if (getBooleanProperty(modes[i])) {
				ParamValue paramValue = CommonFactory.eINSTANCE.createParamValue();

				paramValue.setName(names[i]);

				if (CoreUtil.isNullOrEmpty(prependPath)) {
					paramValue.setValue(values[i]);
				}
				else {
					if (CoreUtil.isNullOrEmpty(prependPath) || !prependPath.startsWith("/")) {
						prependPath = "/" + prependPath;
					}

					paramValue.setValue(prependPath + values[i]);
				}

				defaultParams.add(paramValue);
			}
		}

		return defaultParams.toArray(new ParamValue[0]);
	}

	protected Properties getCategories() {
		if (categories == null) {
			ILiferayProject liferayProject = LiferayCore.create(getProject());

			if (liferayProject == null) {
				try {
					liferayProject = LiferayCore.create(getRuntime());
				}
				catch (CoreException ce) {
					PortletCore.logError(ce);
				}
			}

			ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

			if (portal != null) {
				categories = portal.getPortletCategories();

				IWorkspace workspace = ResourcesPlugin.getWorkspace();

				IProject[] workspaceProjects = workspace.getRoot().getProjects();

				for (IProject workspaceProject : workspaceProjects) {
					if (ProjectUtil.isPortletProject(workspaceProject)) {
						LiferayDisplayDescriptorHelper liferayDisplayDH = new LiferayDisplayDescriptorHelper(
							workspaceProject);

						String[] portletCategories = liferayDisplayDH.getAllPortletCategories();

						if (ListUtil.isNotEmpty(portletCategories)) {
							for (String portletCategory : portletCategories) {
								if (_findExistingCategory(portletCategory) == null) {
									categories.put(portletCategory, portletCategory);
								}
							}
						}
					}
				}
			}
		}

		return categories;
	}

	protected Properties getEntryCategories() {

		// removed if not null return directly, because it won't update when switch
		// projects of different portal versions

		ILiferayProject liferayProject = LiferayCore.create(getProject());

		if (liferayProject == null) {
			try {
				liferayProject = LiferayCore.create(getRuntime());
			}
			catch (CoreException ce) {
				PortletCore.logError(ce);
			}
		}

		ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

		if (portal != null) {
			return portal.getPortletEntryCategories();
		}
		else {
			return null;
		}
	}

	protected Object getInitParams() {
		List<ParamValue> initParams = new ArrayList<>();

		// if the user is using MVCPortlet and creating JSPs then we need to
		// define init-params for each view mode that is checked

		if (/* getStringProperty(SUPERCLASS).equals(QUALIFIED_MVC_PORTLET) && */getBooleanProperty(CREATE_JSPS)) {
			String[] modes = ALL_PORTLET_MODES;

			ParamValue[] paramVals = null;

			try {
				ILiferayProject liferayProject = LiferayCore.create(getProject());

				ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

				String version = portal.getVersion();

				Version portalVersion = Version.parseVersion(version);

				if (CoreUtil.compareVersions(portalVersion, ILiferayConstants.V610) >= 0) {
					paramVals = createDefaultParamValuesForModes(modes, initNames61, initValues);
				}
			}
			catch (Exception e) {
			}

			if (paramVals == null) {
				paramVals = createDefaultParamValuesForModes(modes, initNames60, initValues);
			}

			Collections.addAll(initParams, paramVals);
		}

		return initParams;
	}

	protected String getPortletNameFromClassName(String oldName) {
		/*
		 * Explaination for following regex first part, the rule is
		 * "before this place there is not a line start" then, two options - 1. after
		 * this place is: uppercase followed by lowercase. eg: NewJSF_Portlet or 2.
		 * before this place is lowercase and after this place is uppercase. eg:
		 * New_JSFPortlet
		 */
		String split_pattern = "(?<!^)(?=[A-Z][^A-Z])|(?<=[^A-Z])(?=[A-Z])";

		String[] words = oldName.replaceAll(_PORTLET_SUFFIX_PATTERN, StringPool.EMPTY).split(split_pattern);

		StringBuilder newName = new StringBuilder();

		for (int i = 0; i < words.length; i++) {
			if (i > 0) {
				newName.append(StringPool.DASH);
			}

			newName.append(words[i]);
		}

		return newName.toString().toLowerCase();
	}

	protected IProject getProject() {
		IProject project = (IProject)getProperty(PROJECT);

		if (CoreUtil.isLiferayProject(project)) {
			return project;
		}

		return null;
	}

	protected IRuntime getRuntime() throws CoreException {
		IRuntime runtime = null;

		if (fragment) {
			org.eclipse.wst.common.project.facet.core.runtime.IRuntime bRuntime =
				(org.eclipse.wst.common.project.facet.core.runtime.IRuntime)getDataModel().getProperty(FACET_RUNTIME);

			runtime = ServerUtil.getRuntime(bRuntime);
		}
		else {
			runtime = ServerUtil.getRuntime(getProject());
		}

		return runtime;
	}

	protected IFile getWorkspaceFile(IPath file) {
		IFile retval = null;

		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();

			retval = workspace.getRoot().getFile(file);
		}
		catch (Exception e) {

			// best effort

		}

		return retval;
	}

	protected Properties categories;
	protected Properties entryCategories;
	protected boolean fragment;
	protected IProject initialProject;

	private String _findExistingCategory(String portletCategory) {

		// Check the value and key for each category, if the category's value or key is
		// the same with
		// the original one, return the String value of key.

		Enumeration<?> keys = categories.keys();
		String trimmedCategory = portletCategory.trim();
		String retval = null;

		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();

			Object value = categories.get(key);

			if (trimmedCategory.equals(key) || trimmedCategory.equals(value)) {
				retval = key.toString();

				break;
			}
		}

		return retval;
	}

	private Object _getDisplayNameFromPortletName(String oldName) {
		String[] words = oldName.split("\\s|-|_");
		StringBuilder newName = new StringBuilder();

		for (int i = 0; i < words.length; i++) {
			if (!words[i].isEmpty()) {
				if (words[i].length() > 1) {
					String word = words[i].substring(0, 1).toUpperCase() + words[i].substring(1, words[i].length());

					newName.append(word);

					newName.append(StringPool.SPACE);
				}
				else if (words[i].length() == 1) {
					newName.append(words[i].substring(0, 1).toUpperCase());
					newName.append(StringPool.SPACE);
				}
			}
		}

		return newName.toString().trim();
	}

	private IStatus _validateFolder(IFolder folder, String folderValue) {
		if ((folder == null) || (folderValue == null)) {
			return null;
		}

		if (!Path.ROOT.isValidPath(folderValue)) {
			return LiferayCore.createErrorStatus(Msgs.folderValueInvalid);
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IFolder fold = folder.getFolder(folderValue);

		String path = fold.getFullPath().toString();

		IStatus result = workspace.validatePath(path, IResource.FOLDER);

		if (!result.isOK()) {
			return LiferayCore.createErrorStatus(Msgs.folderValueInvalid);
		}

		if (folder.getFolder(new Path(folderValue)).exists()) {
			List<IFile> viewJspFiles = new SearchFilesVisitor().searchFiles(folder, "view.jsp");

			if (ListUtil.isNotEmpty(viewJspFiles)) {
				return LiferayCore.createWarningStatus(Msgs.viewJspAlreadyExists);
			}
		}

		return null;
	}

	private static final String _PORTLET_SUFFIX_PATTERN = "([Pp][Oo][Rr][Tt][Ll][Ee][Tt])$";

	private static class Msgs extends NLS {

		public static String categoryNameEmpty;
		public static String folderValueInvalid;
		public static String jspFolderNotEmpty;
		public static String jspFolderNotMatchPortletName;
		public static String portletNameEmpty;
		public static String portletNameExists;
		public static String portletSuperclassValid;
		public static String resourceBundleFilePathEndWithProperties;
		public static String resourceBundleFilePathValid;
		public static String specifyPortletSuperclass;
		public static String specifyValidDouble;
		public static String viewJspAlreadyExists;

		static {
			initializeMessages(NewPortletClassDataModelProvider.class.getName(), Msgs.class);
		}

	}

}