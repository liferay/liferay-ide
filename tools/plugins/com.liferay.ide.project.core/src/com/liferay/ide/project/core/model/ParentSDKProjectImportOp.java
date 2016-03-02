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

package com.liferay.ide.project.core.model;

import com.liferay.ide.project.core.model.internal.SDKImportDerivedValueService;
import com.liferay.ide.project.core.model.internal.SDKImportValidationService;

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
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;

/**
 * @author Simon Jiang
 */
public interface ParentSDKProjectImportOp extends ExecutableElement
{

    ElementType TYPE = new ElementType( ParentSDKProjectImportOp.class );

    // *** Location ***

    @Type( base = Path.class )
    @AbsolutePath
    @Required
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Label( standard = "SDK Directory" )
    @Service( impl = SDKImportValidationService.class )

    ValueProperty PROP_SDK_LOCATION = new ValueProperty( TYPE, "SdkLocation" ); //$NON-NLS-1$

    Value<Path> getSdkLocation();
    void setSdkLocation( String value );
    void setSdkLocation( Path value );

    // *** SDK Version ***

    @Label( standard = "SDK Version" )
    @Derived
    @Service( impl = SDKImportDerivedValueService.class )
    ValueProperty PROP_SDK_VERSION = new ValueProperty( TYPE, "SdkVersion" ); //$NON-NLS-1$

    Value<String> getSdkVersion();
    void setSdkVersion( String value );

    @Override
    @DelegateImplementation( ParentSDKProjectImportOpMethods.class )
    Status execute( ProgressMonitor monitor );
}
