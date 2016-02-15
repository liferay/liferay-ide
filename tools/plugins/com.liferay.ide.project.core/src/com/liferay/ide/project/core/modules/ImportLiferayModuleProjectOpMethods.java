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

import com.liferay.ide.core.ILiferayProjectImporter;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

/**
 * @author Andy Wu
 */
public class ImportLiferayModuleProjectOpMethods
{

    public static final Status execute( final ImportLiferayModuleProjectOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Importing Module project...", 100 );

        Status retval = null;

        try
        {
            String location = op.getLocation().content().toOSString();
            String buildType = getBuildType( location );
            ILiferayProjectImporter importer = LiferayCore.getImporter( buildType );

            importer.importProject( location , monitor );

            retval = Status.createOkStatus();
        }
        catch( Exception e )
        {
            final String msg = "import module project error";

            ProjectCore.logError( msg, e );

            retval = Status.createErrorStatus( msg, e );
        }

        return retval;
    }

    public static String getBuildType( String location )
    {
        String buildType = null;

        ILiferayProjectImporter[] importers = LiferayCore.getImporters();

        for( ILiferayProjectImporter importer : importers )
        {
            if( importer.canImport( location ) )
            {
                buildType = importer.getBuildType();
            }
        }

        return buildType;
    }

}
