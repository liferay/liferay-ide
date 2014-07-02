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
package com.liferay.ide.adt.core.model;

import com.liferay.ide.adt.core.model.internal.JavaPackageNameDefaultValueService;
import com.liferay.ide.adt.core.model.internal.JavaProjectConversionService;
import com.liferay.ide.adt.core.model.internal.GenerateCustomServicesOpMethods;
import com.liferay.ide.adt.core.model.internal.StatusDerivedValueService;
import com.liferay.ide.adt.core.model.internal.SummaryDerivedValueService;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaPackageName;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Derived;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.ReadOnly;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;


/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
@Service( impl = JavaProjectConversionService.class )
public interface GenerateCustomServicesOp extends ServerInstance, ExecutableElement
{

    ElementType TYPE = new ElementType( GenerateCustomServicesOp.class );

    // *** ProjectName ***

    ValueProperty PROP_PROJECT_NAME = new ValueProperty( TYPE, "ProjectName" );

    Value<String> getProjectName();
    void setProjectName( String value );


    // *** Summary ***

    @Derived
    @ReadOnly
    @Service( impl = SummaryDerivedValueService.class )
    ValueProperty PROP_SUMMARY = new ValueProperty( TYPE, ServerInstance.PROP_SUMMARY );


    // *** Status ***

    @Label( standard = "status" )
    @Derived
    @Service( impl = StatusDerivedValueService.class )
    ValueProperty PROP_STATUS = new ValueProperty( TYPE, "Status" );

    Value<String> getStatus();
    void setStatus( String value );

    // *** PreviousServerInstances ***

    @Type( base = ServerInstance.class )
    ListProperty PROP_PREVIOUS_SERVER_INSTANCES = new ListProperty( TYPE, "PreviousServerInstances" );

    ElementList<ServerInstance> getPreviousServerInstances();


    // *** Libraries ***

    @Type( base = Library.class )
    ListProperty PROP_LIBRARIES = new ListProperty( TYPE, "Libraries" );

    ElementList<Library> getLibraries();


    // *** Package ***

    @Type( base = JavaPackageName.class )
    @Service( impl = JavaPackageNameDefaultValueService.class )
    ValueProperty PROP_PACKAGE = new ValueProperty( TYPE, "Package" );

    Value<JavaPackageName> getPackage();
    void setPackage( String value );
    void setPackage( JavaPackageName value );


    // *** AddSampleCode ***

    @Type( base = Boolean.class )
    @Label( standard = "add sample code for CRUD on Liferay Contacts" )
    ValueProperty PROP_ADD_SAMPLE_CODE = new ValueProperty( TYPE, "AddSampleCode" );

    Value<Boolean> getAddSampleCode();
    void setAddSampleCode( String value );
    void setAddSampleCode( Boolean value );


    // *** Method: execute ***

    @DelegateImplementation( GenerateCustomServicesOpMethods.class )
    Status execute( ProgressMonitor monitor );

    @DelegateImplementation( GenerateCustomServicesOpMethods.class )
    void updateServerStatus();
}
