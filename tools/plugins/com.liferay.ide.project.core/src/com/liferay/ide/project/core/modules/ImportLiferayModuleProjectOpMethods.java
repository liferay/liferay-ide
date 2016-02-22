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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;

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
            IStatus status = getBuildType( location );
            ILiferayProjectImporter importer = LiferayCore.getImporter( status.getMessage() );

            importer.importProject( location, monitor );

            retval = Status.createOkStatus();
        }
        catch( IllegalStateException e )
        {
            // TODO need to show the user so they know what happened and perhaps how to fix it.

            final String msg = "import module project error";

            ProjectCore.logError( msg, e );

            retval = Status.createErrorStatus( msg, e );
        }
        catch( Exception e )
        {

        }

        return retval;
    }

    public static IStatus getBuildType( String location )
    {
        IStatus retval = StatusBridge.create( Status.createStatus( Severity.ERROR, "no importers found" ) );

        ILiferayProjectImporter[] importers = LiferayCore.getImporters();

        for( ILiferayProjectImporter importer : importers )
        {
            IStatus status = importer.canImport( location );

            if( status.isOK() )
            {
                retval = StatusBridge.create( Status.createStatus( Severity.OK, importer.getBuildType() ) );
                break;
            }
            else if( status.getSeverity() == IStatus.WARNING )
            {
                retval = StatusBridge.create( Status.createStatus( Severity.ERROR, status.getMessage() ) );
                break;
            }
            else
            {
                retval = status;
            }
        }

        return retval;
    }

}
