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

package com.liferay.ide.project.core.springmvcportlet;

import com.liferay.ide.project.core.modules.BaseModuleOp;
import com.liferay.ide.project.core.modules.ModuleProjectNameValidationService;
import com.liferay.ide.project.core.service.CommonProjectLocationInitialValueService;
import com.liferay.ide.project.core.service.TargetLiferayVersionDefaultValueService;
import com.liferay.ide.project.core.service.TargetLiferayVersionPossibleValuesService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.InitialValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Simon Jiang
 */
public interface NewSpringMVCPortletProjectOp extends BaseModuleOp {

	@DelegateImplementation(NewSpringMVCPortletProjectOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getComponentName();

	public Value<String> getDependencyInjector();

	public Value<String> getFramework();

	public Value<String> getFrameworkDependencies();

	public Value<String> getLiferayVersion();

	public Value<String> getPackageName();

	public Value<String> getProjectTemplateName();

	public Value<String> getViewType();

	public void setComponentName(String value);

	public void setDependencyInjector(String value);

	public void setFramework(String value);

	public void setFrameworkDependencies(String value);

	public void setLiferayVersion(String value);

	public void setPackageName(String value);

	public void setProjectTemplateName(String value);

	public void setViewType(String value);

	@Label(standard = "Component Class Name")
	@Service(impl = SpringMVCPortletComponentNameDefaultValueService.class)
	@Service(impl = SpringMVCPortletComponentNameValidationService.class)
	public ValueProperty PROP_COMPONENT_NAME = new ValueProperty(TYPE, "ComponentName");

	public ValueProperty PROP_DEPENDENCY_INJECTOR = new ValueProperty(TYPE, "DependencyInjector");

	public ValueProperty PROP_FRAMEWORK = new ValueProperty(TYPE, "Framework");

	public ValueProperty PROP_FRAMEWORK_DEPENDENCIES = new ValueProperty(TYPE, "FrameworkDependencies");

	@Label(standard = "liferay version")
	@Listeners(SpringMVCPortletProjectNameListener.class)
	@Service(impl = TargetLiferayVersionDefaultValueService.class)
	@Service(impl = TargetLiferayVersionPossibleValuesService.class)
	public ValueProperty PROP_LIFERAY_VERSION = new ValueProperty(TYPE, "LiferayVersion");

	@Service(impl = CommonProjectLocationInitialValueService.class)
	@Service(impl = SpringMVCPortletProjectLocationValidationService.class)
	public ValueProperty PROP_LOCATION = new ValueProperty(TYPE, BaseModuleOp.PROP_LOCATION);

	@Label(standard = "Package name")
	@Service(impl = SpringMVCPortletPackageNameDefaultValueService.class)
	public ValueProperty PROP_PACKAGE_NAME = new ValueProperty(TYPE, "PackageName");

	@Listeners(SpringMVCPortletProjectNameListener.class)
	@Service(impl = ModuleProjectNameValidationService.class)
	public ValueProperty PROP_PROJECT_NAME = new ValueProperty(TYPE, BaseModuleOp.PROP_PROJECT_NAME);

	@Label(standard = "build type")
	@Listeners(SpringMVCPortletProjectNameListener.class)
	@Service(impl = SpringMVCPortletProjectProviderDefaultValueService.class)
	@Service(impl = SpringMVCPortletProjectProviderPossibleValuesService.class)
	public ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty(TYPE, BaseModuleOp.PROP_PROJECT_PROVIDER);

	@InitialValue(text = "spring-mvc-portlet")
	@Label(standard = "Project Template Name")
	@Listeners(SpringMVCPortletProjectNameListener.class)
	public ValueProperty PROP_PROJECT_TEMPLATE_NAME = new ValueProperty(TYPE, "ProjectTemplateName");

	@Listeners(SpringMVCPortletProjectUseDefaultLocationListener.class)
	public ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty(TYPE, BaseModuleOp.PROP_USE_DEFAULT_LOCATION);

	public ValueProperty PROP_VIEW_TYPE = new ValueProperty(TYPE, "ViewType");

	public ElementType TYPE = new ElementType(NewSpringMVCPortletProjectOp.class);

}