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

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaPackageName;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 */
@Service(impl = IJavaProjectConversionService.class)
public interface NewLiferayComponentOp extends ExecutableElement {

	public ElementType TYPE = new ElementType(NewLiferayComponentOp.class);

	@DelegateImplementation(NewLiferayComponentOpMethods.class)
	@Override
	public Status execute(ProgressMonitor monitor);

	public Value<String> getComponentClassName();

	public Value<IComponentTemplate<NewLiferayComponentOp>> getComponentClassTemplateName();

	public Value<String> getModelClass();

	public Value<JavaPackageName> getPackageName();

	public Value<String> getProjectName();

	public ElementList<PropertyKey> getPropertyKeys();

	public Value<String> getServiceName();

	public void setComponentClassName(String value);

	public void setComponentClassTemplateName(IComponentTemplate<NewLiferayComponentOp> value);

	public void setComponentClassTemplateName(String value);

	public void setModelClass(String value);

	public void setPackageName(JavaPackageName value);

	public void setPackageName(String value);

	public void setProjectName(String value);

	public void setServiceName(String value);

	@Label(standard = "Component Class Name")
	@Service(impl = NewLiferayComponentDefaultValueService.class)
	@Service(impl = NewLiferayComponentValidationService.class)
	public ValueProperty PROP_COMPONENT_CLASS_NAME = new ValueProperty(TYPE, "ComponentClassName");

	@DefaultValue(text = "Portlet")
	@Label(standard = "Component Class Template")
	@Service(impl = NewLiferayComponentTemplatePossibleValuesService.class)
	@Type(base = IComponentTemplate.class)
	public ValueProperty PROP_COMPONENT_CLASS_TEMPLATE_NAME = new ValueProperty(TYPE, "ComponentClassTemplateName");

	@Label(standard = "Model Class")
	@Required
	@Service(impl = NewLiferayComponentModelClassPossibleValuesService.class)
	public ValueProperty PROP_MODEL_CLASS = new ValueProperty(TYPE, "ModelClass");

	@Required
	@Service(impl = JavaPackageNameDefaultValueService.class)
	@Service(impl = JavaPackageNameValidationService.class)
	@Type(base = JavaPackageName.class)
	public ValueProperty PROP_PACKAGE_NAME = new ValueProperty(TYPE, "PackageName");

	@Label(standard = "project name")
	@Required
	@Service(impl = NewLiferayComponentProjectNameDefaultValueService.class)
	@Service(impl = NewLiferayComponentProjectNamePossibleService.class)
	@Service(impl = NewLiferayComponentProjectValidationService.class)
	public ValueProperty PROP_PROJECT_NAME = new ValueProperty(TYPE, "ProjectName");

	@Label(standard = "Properties")
	@Type(base = PropertyKey.class)
	public ListProperty PROP_PROPERTYKEYS = new ListProperty(TYPE, "PropertyKeys");

	@Label(standard = "Service Name")
	@Required
	@Service(impl = NewLiferayComponentServicePossibleValuesService.class)
	public ValueProperty PROP_SERVICE_NAME = new ValueProperty(TYPE, "ServiceName");

}