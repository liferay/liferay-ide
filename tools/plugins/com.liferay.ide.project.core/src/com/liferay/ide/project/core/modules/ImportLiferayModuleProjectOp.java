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

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Derived;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Andy Wu
 */
public interface ImportLiferayModuleProjectOp extends ExecutableElement
{

    ElementType TYPE = new ElementType( ImportLiferayModuleProjectOp.class );

    // *** Location ***

    @AbsolutePath
    @Required
    @Type( base = Path.class )
    @Service( impl = ImportModuleProjectLocationValidationService.class )
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    ValueProperty PROP_LOCATION = new ValueProperty( TYPE, "Location" );

    Value<Path> getLocation();
    void setLocation( String value );
    void setLocation( Path value );

    // *** Build Type ***

    @Derived
    @Service( impl = ImportModuleProjectBuildTypeDerivedValueService.class )
    ValueProperty PROP_BUILD_TYPE = new ValueProperty( TYPE, "BuildType" );

    Value<String> getBuildType();
    void setBuildType( String value );

    // *** Method: execute ***

    @Override
    @DelegateImplementation( ImportLiferayModuleProjectOpMethods.class )
    Status execute( ProgressMonitor monitor );
}
