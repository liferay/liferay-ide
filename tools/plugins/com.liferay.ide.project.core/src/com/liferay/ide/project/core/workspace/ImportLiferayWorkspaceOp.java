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

package com.liferay.ide.project.core.workspace;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Derived;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Andy Wu
 */
public interface ImportLiferayWorkspaceOp extends BaseLiferayWorkspaceOp
{

    ElementType TYPE = new ElementType( ImportLiferayWorkspaceOp.class );

    // *** WorkspaceLocation ***

    @Label( standard = "workspace location" )
    @Type( base = Path.class )
    @AbsolutePath
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Service( impl = ImportWorkspaceLocationValidationService.class )
    @Required
    ValueProperty PROP_WORKSPACE_LOCATION = new ValueProperty( TYPE, "WorkspaceLocation" );

    Value<Path> getWorkspaceLocation();
    void setWorkspaceLocation( String value );
    void setWorkspaceLocation( Path value );

    // *** hasBundlesDir ***

    @Derived
    @Service( impl = HasBundlesDirDerivedValueService.class )
    @Type( base = Boolean.class )
    ValueProperty PROP_HAS_BUNDLES_DIR = new ValueProperty( TYPE, "hasBundlesDir" );

    Value<Boolean> getHasBundlesDir();
    void setHasBundlesDir( String value );
    void setHasBundlesDir( Boolean value );

    // *** serverName ***

    @Service( impl = ImportLiferayWorkspaceServerNameService.class )
    ValueProperty PROP_SERVER_NAME = new ValueProperty( TYPE, BaseLiferayWorkspaceOp.PROP_SERVER_NAME );

    // *** Method: execute ***

    @Override
    @DelegateImplementation( ImportLiferayWorkspaceOpMethods.class )
    Status execute( ProgressMonitor monitor );
}
