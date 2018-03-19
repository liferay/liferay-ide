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

import com.liferay.ide.project.core.service.CommonProjectLocationInitialValueService;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Whitespace;

/**
 * @author Simon Jiang
 */
public interface NewLiferayModuleProjectOp extends BaseModuleOp {

	public ElementType TYPE = new ElementType(NewLiferayModuleProjectOp.class);

	@DelegateImplementation(NewLiferayModuleProjectOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getArchetype();

	public Value<String> getArtifactVersion();

	public Value<String> getComponentName();

	public Value<String> getGroupId();

	public Value<String> getLiferayVersion();

	public Value<String> getPackageName();

	public Value<String> getProjectTemplateName();

	public ElementList<PropertyKey> getPropertyKeys();

	public Value<String> getServiceName();

	public void setArchetype(String value);

	public void setArtifactVersion(String value);

	public void setComponentName(String value);

	public void setGroupId(String value);

	public void setLiferayVersion(String value);

	public void setPackageName(String value);

	public void setProjectTemplateName(String value);

	public void setServiceName(String value);

	@Service(impl = ModuleArchetypeDefaultValueService.class)
	public ValueProperty PROP_ARCHETYPE = new ValueProperty(TYPE, "Archetype");

	@Label(standard = "artifact version")
	@Service(impl = ModuleProjectArtifactVersionDefaultValueService.class)
	public ValueProperty PROP_ARTIFACT_VERSION = new ValueProperty(TYPE, "ArtifactVersion");

	@Label(standard = "Component Class Name")
	@Service(impl = ComponentNameDefaultValueService.class)
	@Service(impl = ComponentNameValidationService.class)
	public ValueProperty PROP_COMPONENT_NAME = new ValueProperty(TYPE, "ComponentName");

	@Label(standard = "group id")
	@Service(impl = ModuleProjectGroupIdDefaultValueService.class)
	@Service(impl = ModuleProjectGroupIdValidationService.class)
	@Whitespace(trim = false)
	public ValueProperty PROP_GROUP_ID = new ValueProperty(TYPE, "GroupId");

	@Label(standard = "liferay version")
	@Service(impl = TargetLiferayVersionDefaultValueService.class)
	@Service(impl = TargetLiferayVersionPossibleValuesService.class)
	public ValueProperty PROP_LIFERAY_VERSION = new ValueProperty(TYPE, "LiferayVersion");

	@Service(impl = CommonProjectLocationInitialValueService.class)
	@Service(impl = ModuleProjectLocationValidationService.class)
	public ValueProperty PROP_LOCATION = new ValueProperty(TYPE, BaseModuleOp.PROP_LOCATION);

	@Label(standard = "Package name")
	@Service(impl = PackageNameDefaultValueService.class)
	@Service(impl = PackageNameValidationService.class)
	public ValueProperty PROP_PACKAGE_NAME = new ValueProperty(TYPE, "PackageName");

	@Listeners(ModuleProjectNameListener.class)
	@Service(impl = ModuleProjectNameValidationService.class)
	public ValueProperty PROP_PROJECT_NAME = new ValueProperty(TYPE, BaseModuleOp.PROP_PROJECT_NAME);

	@Label(standard = "build type")
	@Listeners(ModuleProjectNameListener.class)
	@Service(impl = ModuleProjectProviderDefaultValueService.class)
	@Service(impl = ModuleProjectProviderPossibleValuesService.class)
	public ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty(TYPE, BaseModuleOp.PROP_PROJECT_PROVIDER);

	@DefaultValue(text = "mvc-portlet")
	@Label(standard = "Project Template Name")
	@Listeners(ModuleProjectNameListener.class)
	@Service(impl = ProjectTemplateNamePossibleValuesService.class)
	public ValueProperty PROP_PROJECT_TEMPLATE_NAME = new ValueProperty(TYPE, "ProjectTemplateName");

	@Label(standard = "Properties")
	@Type(base = PropertyKey.class)
	public ListProperty PROP_PROPERTYKEYS = new ListProperty(TYPE, "PropertyKeys");

	@Label(standard = "Service Name")
	@Service(impl = ServiceDefaultValuesService.class)
	@Service(impl = ServiceNameValidataionService.class)
	@Service(impl = ServicePossibleValuesService.class)
	public ValueProperty PROP_SERVICE_NAME = new ValueProperty(TYPE, "ServiceName");

	@Listeners(ModuleProjectUseDefaultLocationListener.class)
	public ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty(TYPE, BaseModuleOp.PROP_USE_DEFAULT_LOCATION);

}