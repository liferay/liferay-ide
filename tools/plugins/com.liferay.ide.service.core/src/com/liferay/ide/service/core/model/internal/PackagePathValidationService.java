/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.service.core.model.internal;

import com.liferay.ide.service.core.model.ServiceBuilder;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jst.j2ee.internal.common.J2EECommonMessages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Cindy Li
 */
@SuppressWarnings( "restriction" )
public class PackagePathValidationService extends ValidationService
{
    @Override
    public Status validate()
    {
        final Value<String> packagePath = context().find( ServiceBuilder.class ).getPackagePath();
        final String label = ( packagePath.getProperty() ).getLabel( true, CapitalizationType.NO_CAPS, false );

        String packPathVal = packagePath.getContent();

        String msg = null;

        if( packPathVal == null )
        {
            msg = NLS.bind( Msgs.packagePathNotEmpty, label );

            return Status.createErrorStatus( msg );
        }
        else
        {
            // Use standard java conventions to validate the package name
            IStatus javaStatus =
                JavaConventions.validatePackageName(
                    packPathVal, CompilerOptions.VERSION_1_5, CompilerOptions.VERSION_1_5 );

            if( javaStatus.getSeverity() == IStatus.ERROR )
            {
                msg = NLS.bind( J2EECommonMessages.ERR_JAVA_PACAKGE_NAME_INVALID + javaStatus.getMessage(), label );

                return Status.createErrorStatus( msg );
            }
            else if( javaStatus.getSeverity() == IStatus.WARNING )
            {
                msg = NLS.bind( J2EECommonMessages.ERR_JAVA_PACKAGE_NAME_WARNING + javaStatus.getMessage(), label );

                return Status.createWarningStatus( msg );
            }
        }

        return Status.createOkStatus();
    }

    private static class Msgs extends NLS
    {
        public static String packagePathNotEmpty;

        static
        {
            initializeMessages( PackagePathValidationService.class.getName(), Msgs.class );
        }
    }
}
