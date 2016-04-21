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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.sapphire.java.JavaPackageName;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class JavaPackageNameValidationService extends ValidationService
{

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final JavaPackageName packageName = op().getPackageName().content( true );

        if( packageName != null )
        {
            int packageNameStatus = JavaConventions.validatePackageName(
                packageName.toString(), CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7 ).getSeverity();

            if( packageNameStatus == IStatus.ERROR )
            {
                retval = Status.createErrorStatus( "Invalid package name" );
            }
        }

        return retval;
    }

    private NewLiferayComponentOp op()
    {
        return context( NewLiferayComponentOp.class );
    }
}
