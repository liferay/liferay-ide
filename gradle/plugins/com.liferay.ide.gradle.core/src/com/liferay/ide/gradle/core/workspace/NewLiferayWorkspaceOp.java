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
package com.liferay.ide.gradle.core.workspace;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Andy Wu
 */
public interface NewLiferayWorkspaceOp extends ExecutableElement
{
    ElementType TYPE = new ElementType( NewLiferayWorkspaceOp.class );

    // *** WorkspaceName ***

    @Label( standard = "workspace name" )
    @Listeners( WorkspaceNameListener.class )
    @Service( impl = WorkspaceNameValidationService.class )
    @Required
    ValueProperty PROP_WORKSPACE_NAME = new ValueProperty( TYPE, "WorkspaceName" );

    Value<String> getWorkspaceName();
    void setWorkspaceName( String value );

    // *** UseDefaultLocation ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "true" )
    @Label( standard = "use default location" )
    @Listeners( WorkspaceUseDefaultLocationListener.class )
    ValueProperty PROP_USE_DEFAULT_LOCATION = new ValueProperty( TYPE, "UseDefaultLocation" );

    Value<Boolean> getUseDefaultLocation();
    void setUseDefaultLocation( String value );
    void setUseDefaultLocation( Boolean value );

    // *** ProjectLocation ***

    @Type( base = Path.class )
    @AbsolutePath
    @Enablement( expr = "${ UseDefaultLocation == 'false' }" )
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Label( standard = "location" )
    @Service( impl = WorkspaceLocationValidationService.class )
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" );

    Value<Path> getLocation();
    void setLocation( String value );
    void setLocation( Path value );

    // *** run ininBundle command ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    @Label( standard = "run initBundle command" )
    @Listeners( RunInitBundleCommandListener2.class )
    ValueProperty PROP_RUN_INITBUNDLE_COMMAND = new ValueProperty( TYPE, "runInitBundleCommand" );

    Value<Boolean> getRunInitBundleCommand();

    void setRunInitBundleCommand( String value );

    void setRunInitBundleCommand( Boolean value );

    // *** add Server ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    @Enablement( expr = "${ RunInitBundleCommand == 'true' }" )
    @Label( standard = "add server" )
    ValueProperty PROP_ADD_SERVER = new ValueProperty( TYPE, "addServer" );

    Value<Boolean> getAddServer();

    void setAddServer( String value );

    void setAddServer( Boolean value );

    // *** serverName ***
    @Type( base = String.class )
    @Enablement( expr = "${ AddServer == 'true' }" )
    @Services
    (
        value =
            {
                @Service( impl = ServerNameDefaultValueService2.class ),
                @Service( impl = ServerNameValidationService2.class ),
            }
    )
    ValueProperty PROP_SERVER_NAME = new ValueProperty( TYPE, "serverName" );

    Value<String> getServerName();
    void setServerName( String value );

    // *** Method: execute ***

    @Override
    @DelegateImplementation( NewLiferayWorkspaceOpMethods.class )
    Status execute( ProgressMonitor monitor );
}
