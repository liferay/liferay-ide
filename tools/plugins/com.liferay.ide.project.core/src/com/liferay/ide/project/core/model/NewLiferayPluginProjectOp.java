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

package com.liferay.ide.project.core.model;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.model.internal.ActiveProfilesValidationService;
import com.liferay.ide.project.core.model.internal.ArchetypeDefaultValueService;
import com.liferay.ide.project.core.model.internal.ArtifactVersionDefaultValueService;
import com.liferay.ide.project.core.model.internal.CreateNewPortletDefaultValueService;
import com.liferay.ide.project.core.model.internal.DisplayNameDefaultValueService;
import com.liferay.ide.project.core.model.internal.GroupIdDefaultValueService;
import com.liferay.ide.project.core.model.internal.GroupIdValidationService;
import com.liferay.ide.project.core.model.internal.HasWorkspaceSdkDefaultValueService;
import com.liferay.ide.project.core.model.internal.IncludeSampleCodeDefaultValueService;
import com.liferay.ide.project.core.model.internal.LocationValidationService;
import com.liferay.ide.project.core.model.internal.PluginTypeListener;
import com.liferay.ide.project.core.model.internal.PluginTypePossibleValuesService;
import com.liferay.ide.project.core.model.internal.PluginTypeValidationService;
import com.liferay.ide.project.core.model.internal.PortletFrameworkAdvancedPossibleValuesService;
import com.liferay.ide.project.core.model.internal.PortletFrameworkPossibleValuesService;
import com.liferay.ide.project.core.model.internal.PortletFrameworkValidationService;
import com.liferay.ide.project.core.model.internal.ProfileIdPossibleValuesService;
import com.liferay.ide.project.core.model.internal.ProjectNameListener;
import com.liferay.ide.project.core.model.internal.ProjectNameValidationService;
import com.liferay.ide.project.core.model.internal.ProjectProviderDefaultValueService;
import com.liferay.ide.project.core.model.internal.ProjectProviderListener;
import com.liferay.ide.project.core.model.internal.ProjectProviderPossibleValuesService;
import com.liferay.ide.project.core.model.internal.SDKLocationListener;
import com.liferay.ide.project.core.model.internal.SDKLocationValidationService;
import com.liferay.ide.project.core.model.internal.ThemeFrameworkValidationService;
import com.liferay.ide.project.core.model.internal.UseDefaultLocationListener;
import com.liferay.ide.project.core.model.internal.UseDefaultLocationValidationService;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Fact;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Whitespace;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 * @author Tao Tao
 */
public interface NewLiferayPluginProjectOp extends ExecutableElement {

	public ElementType TYPE = new ElementType(NewLiferayPluginProjectOp.class);

	@DelegateImplementation(NewLiferayPluginProjectOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getActiveProfilesValue();

	public Value<String> getArchetype();

	public Value<String> getArtifactVersion();

	public Value<Boolean> getCreateNewPortlet();

	public Value<String> getDisplayName();

	public Value<String> getFinalProjectName();

	public Value<String> getGroupId();

	public Value<Boolean> getImportProjectStatus();

	public Value<Boolean> getIncludeSampleCode();

	public Value<Path> getLocation();

	public ElementList<NewLiferayProfile> getNewLiferayProfiles();

	public Value<PluginType> getPluginType();

	public Value<IPortletFramework> getPortletFramework();

	public Value<IPortletFramework> getPortletFrameworkAdvanced();

	public Value<String> getPortletName();

	public Value<String> getProjectName();

	public ElementList<ProjectName> getProjectNames();

	public Value<NewLiferayProjectProvider<NewLiferayPluginProjectOp>> getProjectProvider();

	public Value<Path> getSdkLocation();

	public ElementList<Profile> getSelectedProfiles();

	public Value<String> getThemeFramework();

	public Value<String> getThemeParent();

	public Value<Boolean> getUseDefaultLocation();

	public void setActiveProfilesValue(String value);

	public void setArchetype(String value);

	public void setArtifactVersion(String value);

	public void setCreateNewPortlet(Boolean value);

	public void setCreateNewPortlet(String value);

	public void setDisplayName(String value);

	public void setFinalProjectName(String value);

	public void setGroupId(String value);

	public void setImportProjectStatus(Boolean value);

	public void setImportProjectStatus(String value);

	public void setIncludeSampleCode(Boolean value);

	public void setIncludeSampleCode(String value);

	public void setLocation(Path value);

	public void setLocation(String value);

	public void setPluginType(PluginType value);

	public void setPluginType(String value);

	public void setPortletFramework(IPortletFramework value);

	public void setPortletFramework(String value);

	public void setPortletFrameworkAdvanced(IPortletFramework value);

	public void setPortletFrameworkAdvanced(String value);

	public void setPortletName(String value);

	public void setProjectName(String value);

	public void setProjectProvider(NewLiferayProjectProvider<NewLiferayPluginProjectOp> value);

	public void setProjectProvider(String value);

	public void setSdkLocation(Path value);

	public void setSdkLocation(String value);

	public void setThemeFramework(String value);

	public void setThemeParent(String value);

	public void setUseDefaultLocation(Boolean value);

	public void setUseDefaultLocation(String value);

	@Fact(statement = "Supports comma separated list of active profiles")
	@Label(standard = "active profiles")
	@Service(impl = ActiveProfilesValidationService.class)
	@Whitespace(trim = false)
	public ValueProperty PROP_ACTIVE_PROFILES_VALUE = new ValueProperty(TYPE, "ActiveProfilesValue");

	@Service(impl = ArchetypeDefaultValueService.class)
	public ValueProperty PROP_ARCHETYPE = new ValueProperty(TYPE, "Archetype");

	@Label(standard = "artifact version")
	@Service(impl = ArtifactVersionDefaultValueService.class)
	public ValueProperty PROP_ARTIFACT_VERSION = new ValueProperty(TYPE, "ArtifactVersion");

	@Service(impl = CreateNewPortletDefaultValueService.class)
	@Type(base = Boolean.class)
	public ValueProperty PROP_CREATE_NEW_PORTLET = new ValueProperty(TYPE, "CreateNewPortlet");

	@Label(standard = "display name")
	@Service(impl = DisplayNameDefaultValueService.class)
	public ValueProperty PROP_DISPLAY_NAME = new ValueProperty(TYPE, "DisplayName");

	@DefaultValue(text = "${ProjectName}")
	public ValueProperty PROP_FINAL_PROJECT_NAME = new ValueProperty(TYPE, "FinalProjectName");

	@Label(standard = "group id")
	@Services(
		value = {@Service(impl = GroupIdValidationService.class), @Service(impl = GroupIdDefaultValueService.class)}
	)
	@Whitespace(trim = false)
	public ValueProperty PROP_GROUP_ID = new ValueProperty(TYPE, "GroupId");

	@Service(impl = HasWorkspaceSdkDefaultValueService.class)
	@Type(base = Boolean.class)
	public ValueProperty PROP_HAS_WORKSPACE_SDK = new ValueProperty(TYPE, "HasWorkspaceSDK");

	@DefaultValue(text = "false")
	@Type(base = Boolean.class)
	public ValueProperty PROP_IMPORT_PROJECT_STATUS = new ValueProperty(TYPE, "ImportProjectStatus");

	@Service(impl = IncludeSampleCodeDefaultValueService.class)
	@Type(base = Boolean.class)
	public ValueProperty PROP_INCLUDE_SAMPLE_CODE = new ValueProperty(TYPE, "IncludeSampleCode");

	@AbsolutePath
	@Enablement(expr = "${ UseDefaultLocation == 'false' && ProjectProvider != 'ant' }")
	@Label(standard = "location")
	@Service(impl = LocationValidationService.class)
	@Type(base = Path.class)
	@ValidFileSystemResourceType(FileSystemResourceType.FOLDER)
	public ValueProperty PROP_LOCATION = new ValueProperty(TYPE, "Location");

	@Type(base = NewLiferayProfile.class)
	public ListProperty PROP_NEW_LIFERAY_PROFILES = new ListProperty(TYPE, "NewLiferayProfiles");

	@DefaultValue(text = "portlet")
	@Label(standard = "plugin type")
	@Listeners(PluginTypeListener.class)
	@Services(
		value = {
			@Service(impl = PluginTypePossibleValuesService.class), @Service(impl = PluginTypeValidationService.class)
		}
	)
	@Type(base = PluginType.class)
	public ValueProperty PROP_PLUGIN_TYPE = new ValueProperty(TYPE, "PluginType");

	@DefaultValue(text = "mvc")
	@Label(standard = "portlet framework")
	@Services(
		value = {
			@Service(impl = PortletFrameworkValidationService.class),
			@Service(impl = PortletFrameworkPossibleValuesService.class)
		}
	)
	@Type(base = IPortletFramework.class)
	public ValueProperty PROP_PORTLET_FRAMEWORK = new ValueProperty(TYPE, "PortletFramework");

	@DefaultValue(text = "jsf")
	@Service(impl = PortletFrameworkAdvancedPossibleValuesService.class)
	@Type(base = IPortletFramework.class)
	public ValueProperty PROP_PORTLET_FRAMEWORK_ADVANCED = new ValueProperty(TYPE, "PortletFrameworkAdvanced");

	@DefaultValue(text = "${ProjectName}")
	@Label(standard = "portlet name")
	public ValueProperty PROP_PORTLET_NAME = new ValueProperty(TYPE, "PortletName");

	@Label(standard = "project name")
	@Listeners(ProjectNameListener.class)
	@Required
	@Service(impl = ProjectNameValidationService.class)
	public ValueProperty PROP_PROJECT_NAME = new ValueProperty(TYPE, "ProjectName");

	@Type(base = ProjectName.class)
	public ListProperty PROP_PROJECT_NAMES = new ListProperty(TYPE, "ProjectNames");

	@Label(standard = "build type")
	@Listeners(ProjectProviderListener.class)
	@Services(
		value = {
			@Service(impl = ProjectProviderPossibleValuesService.class),
			@Service(impl = ProjectProviderDefaultValueService.class)
		}
	)
	@Type(base = ILiferayProjectProvider.class)
	public ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty(TYPE, "ProjectProvider");

	@AbsolutePath
	@Label(standard = "SDK Location")
	@Listeners(SDKLocationListener.class)
	@Service(impl = SDKLocationValidationService.class)
	@Type(base = Path.class)
	@ValidFileSystemResourceType(FileSystemResourceType.FOLDER)
	public ValueProperty PROP_SDK_LOCATION = new ValueProperty(TYPE, "SdkLocation");

	@Label(standard = "selected profiles")
	@Service(impl = ProfileIdPossibleValuesService.class)
	@Type(base = Profile.class)
	public ListProperty PROP_SELECTED_PROFILES = new ListProperty(TYPE, "SelectedProfiles");

	@DefaultValue(text = "Freemarker")
	@Label(standard = "theme framework")
	@PossibleValues(ordered = true, values = {"Velocity", "Freemarker", "JSP"})
	@Service(impl = ThemeFrameworkValidationService.class)
	public ValueProperty PROP_THEME_FRAMEWORK = new ValueProperty(TYPE, "ThemeFramework");

	@DefaultValue(text = "_styled")
	@Label(standard = "theme parent")
	@PossibleValues(ordered = true, values = {"_unstyled", "_styled", "classic"})
	public ValueProperty PROP_THEME_PARENT = new ValueProperty(TYPE, "ThemeParent");

	@DefaultValue(text = "true")
	@Enablement(expr = "${ ProjectProvider != 'ant' }")
	@Label(standard = "use default location")
	@Listeners(UseDefaultLocationListener.class)
	@Service(impl = UseDefaultLocationValidationService.class)
	@Type(base = Boolean.class)
	public ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty(TYPE, "UseDefaultLocation");

}