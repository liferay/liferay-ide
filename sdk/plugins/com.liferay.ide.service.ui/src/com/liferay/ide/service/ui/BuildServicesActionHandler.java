/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.service.ui;

import com.liferay.ide.service.core.ServiceCore;
import com.liferay.ide.service.core.job.BuildServiceJob;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireRenderingContext;

/**
 * @author Gregory Amerson
 */
public class BuildServicesActionHandler extends SapphireActionHandler
{

    @Override
    protected Object run( SapphireRenderingContext context )
    {
        IFile file = context.getPart().getModelElement().adapt( IFile.class );

        if( file != null && file.exists() )
        {
            BuildServiceJob job = ServiceCore.createBuildServiceJob( file );

            job.schedule();
        }
        else
        {
            MessageDialog.openWarning( context.getShell(), Msgs.buildServices, Msgs.ActionUnavailableImportProject );
        }

        return null;
    }

    private static class Msgs extends NLS
    {
        public static String ActionUnavailableImportProject;
        public static String buildServices;

        static
        {
            initializeMessages( BuildServicesActionHandler.class.getName(), Msgs.class );
        }
    }
}
