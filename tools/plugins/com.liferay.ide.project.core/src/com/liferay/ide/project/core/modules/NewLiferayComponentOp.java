/*******************************************************************************
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
 *
 *******************************************************************************/

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
@Service( impl = IJavaProjectConversionService.class )
public interface NewLiferayComponentOp extends ExecutableElement
{

    ElementType TYPE = new ElementType( NewLiferayComponentOp.class );

    @Label( standard = "project name" )
    @Required
    @Service( impl = NewLiferayComponentProjectNameDefaultValueService.class )
    @Service( impl = NewLiferayComponentProjectNamePossibleService.class )
    ValueProperty PROP_PROJECT_NAME = new ValueProperty( TYPE, "ProjectName" );

    Value<String> getProjectName();
    void setProjectName( String value );

    // *** Package ***

    @Type( base = JavaPackageName.class )
    @Required
    @Service( impl = JavaPackageNameDefaultValueService.class )
    @Service( impl = JavaPackageNameValidationService.class )
    ValueProperty PROP_PACKAGE_NAME = new ValueProperty( TYPE, "PackageName" );

    Value<JavaPackageName> getPackageName();
    void setPackageName( String value );
    void setPackageName( JavaPackageName value );

    // *** ComponentClassName ***

    @Label( standard = "Component Class Name" )
    @Service( impl = NewLiferayComponentDefaultValueService.class )
    @Service( impl = NewLiferayComponentValidationService.class )
    ValueProperty PROP_COMPONENT_CLASS_NAME = new ValueProperty( TYPE, "ComponentClassName" );

    Value<String> getComponentClassName();
    void setComponentClassName( String value );

    // *** Component Class Template Name ***

    @Type( base = IComponentTemplate.class )
    @DefaultValue( text = "Portlet" )
    @Label( standard = "Component Class Template" )
    @Service( impl = NewLiferayComponentTemplatePossibleValuesService.class )
    ValueProperty PROP_COMPONENT_CLASS_TEMPLATE_NAME = new ValueProperty( TYPE, "ComponentClassTemplateName" );

    Value<IComponentTemplate<NewLiferayComponentOp>> getComponentClassTemplateName();
    void setComponentClassTemplateName( IComponentTemplate<NewLiferayComponentOp> value );
    void setComponentClassTemplateName( String value );


    // *** ModleClass ***
    @Label( standard = "Model Class" )
    @Required
    @Service( impl = NewLiferayComponentModelClassPossibleValuesService.class )
    ValueProperty PROP_MODEL_CLASS = new ValueProperty( TYPE, "ModelClass" );

    Value<String> getModelClass();
    void setModelClass( String value );

    // *** ServiceName ***
    @Label( standard = "Service Name" )
    @Required
    @Service( impl = NewLiferayComponentServicePossibleValuesService.class )
    ValueProperty PROP_SERVICE_NAME = new ValueProperty( TYPE, "ServiceName" );

    Value<String> getServiceName();
    void setServiceName( String value );

    // *** PropertyKeys ***
    @Type( base = PropertyKey.class )
    @Label( standard = "Properties" )
    ListProperty PROP_PROPERTYKEYS = new ListProperty( TYPE, "PropertyKeys" );

    ElementList<PropertyKey> getPropertyKeys();

    // *** Method: execute ***

    @Override
    @DelegateImplementation( NewLiferayComponentOpMethods.class )
    Status execute( ProgressMonitor monitor );
}