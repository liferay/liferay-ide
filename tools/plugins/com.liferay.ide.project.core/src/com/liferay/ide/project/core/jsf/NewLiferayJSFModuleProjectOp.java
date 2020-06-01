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

package com.liferay.ide.project.core.jsf;

import com.liferay.ide.project.core.modules.BaseModuleOp;
import com.liferay.ide.project.core.modules.ModuleProjectNameValidationService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Simon Jiang
 */
public interface NewLiferayJSFModuleProjectOp extends BaseModuleOp {

	public ElementType TYPE = new ElementType(NewLiferayJSFModuleProjectOp.class);

	@DelegateImplementation(NewLiferayJSFModuleProjectOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getArchetype();

	public Value<String> getTemplateName();

	public void setArchetype(String value);

	public void setTemplateName(String value);

	@Service(impl = JSFModuleProjectArchetypeDefaultValueService.class)
	public ValueProperty PROP_ARCHETYPE = new ValueProperty(TYPE, "Archetype");

	@Service(impl = JSFModuleProjectLocationValidationService.class)
	public ValueProperty PROP_LOCATION = new ValueProperty(TYPE, BaseModuleOp.PROP_LOCATION);

	@Listeners(JSFModuleProjectNameListener.class)
	@Service(
		impl = ModuleProjectNameValidationService.class,
		params = {@Service.Param(name = "requiredLiferayWorkspace", value = "false")}
	)
	public ValueProperty PROP_PROJECT_NAME = new ValueProperty(TYPE, BaseModuleOp.PROP_PROJECT_NAME);

	@Label(standard = "Build type")
	@Listeners(JSFModuleProjectNameListener.class)
	@Service(impl = JSFModuleProjectProviderDefaultValueService.class)
	@Service(impl = JSFModuleProjectProviderPossibleValuesService.class)
	public ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty(TYPE, BaseModuleOp.PROP_PROJECT_PROVIDER);

	@DefaultValue(text = "jsf")
	@Label(standard = "Component Suite")
	@Listeners(JSFModuleProjectNameListener.class)
	@PossibleValues(values = {"jsf", "alloy", "icefaces", "primefaces", "richfaces"})
	public ValueProperty PROP_TEMPLATE_NAME = new ValueProperty(TYPE, "TemplateName");

	@Listeners(JSFModuleProjectUseDefaultLocationListener.class)
	public ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty(TYPE, BaseModuleOp.PROP_USE_DEFAULT_LOCATION);

}