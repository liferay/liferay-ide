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
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Whitespace;

/**
 * @author Simon Jiang
 */
public interface NewLiferayModuleProjectOp extends BaseModuleOp
{
    ElementType TYPE = new ElementType( NewLiferayModuleProjectOp.class );

    // *** Archetype ***

    //@Service( impl = ArchetypeDefaultValueService.class )
    ValueProperty PROP_ARCHETYPE = new ValueProperty( TYPE, "Archetype" );

    Value<String> getArchetype();
    void setArchetype( String value );


    // *** Project Template ***

    @DefaultValue( text = "mvcportlet" )
    @Label( standard = "Project Template Name" )
    @Listeners( ModuleProjectNameListener.class )
    @Service( impl = ProjectTemplateNamePossibleValuesService.class )
    @Service( impl = ProjectTemplateNameValidationService.class )
    ValueProperty PROP_PROJECT_TEMPLATE_NAME = new ValueProperty( TYPE, "ProjectTemplateName" );

    Value<String> getProjectTemplateName();
    void setProjectTemplateName( String value );

    // *** Maven settings ***
    // *** ArtifactVersion ***

    @Label( standard = "artifact version" )
    @Service( impl = ModuleProjectArtifactVersionDefaultValueService.class )
    ValueProperty PROP_ARTIFACT_VERSION = new ValueProperty( TYPE, "ArtifactVersion" );

    Value<String> getArtifactVersion();
    void setArtifactVersion( String value );


    // *** GroupId ***

    @Label( standard = "group id" )
    @Service( impl = ModuleProjectGroupIdValidationService.class )
    @Service( impl = ModuleProjectGroupIdDefaultValueService.class )
    @Whitespace( trim = false )
    ValueProperty PROP_GROUP_ID = new ValueProperty( TYPE, "GroupId" );

    Value<String> getGroupId();
    void setGroupId( String value );

    // *** FinalProjectName ***

    // *** ComponentName ***
    @Label( standard = "Component Class Name" )
    @Service( impl = ComponentNameValidationService.class )
    @Service( impl = ComponentNameDefaultValueService.class )
    ValueProperty PROP_COMPONENT_NAME = new ValueProperty( TYPE, "ComponentName" );

    Value<String> getComponentName();
    void setComponentName( String value );

    // *** ServiceName ***
    @Label( standard = "Service Name" )
    @Required
    @Service( impl = ServicePossibleValuesService.class )
    @Service( impl = ServiceDefaultValuesService.class )
    @Service( impl = ServiceNameValidataionService.class )
    ValueProperty PROP_SERVICE_NAME = new ValueProperty( TYPE, "ServiceName" );

    Value<String> getServiceName();
    void setServiceName( String value );

    // *** PackageeName ***

    @DefaultValue( text = "com.example" )
    @Label( standard = "Package Name" )
    @Service( impl = PackageNameValidationService.class )
    @Service( impl = PackageNameDefaultValueService.class )
    ValueProperty PROP_PACKAGE_NAME = new ValueProperty( TYPE, "PackageName" );

    Value<String> getPackageName();
    void setPackageName( String value );

    // *** PropertyKeys ***
    @Type( base = PropertyKey.class )
    @Label( standard = "Properties" )
    ListProperty PROP_PROPERTYKEYS = new ListProperty( TYPE, "PropertyKeys" );
    ElementList<PropertyKey> getPropertyKeys();

    // *** Method: execute ***

    @Override
    @DelegateImplementation( NewLiferayModuleProjectOpMethods.class )
    Status execute( ProgressMonitor monitor );
}
