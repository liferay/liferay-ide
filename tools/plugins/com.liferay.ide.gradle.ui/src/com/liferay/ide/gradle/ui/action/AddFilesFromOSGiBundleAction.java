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

package com.liferay.ide.gradle.ui.action;

import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.gradle.core.modules.NewModuleFragmentOp;
import com.liferay.ide.gradle.core.modules.OverrideFilePath;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 */
public class AddFilesFromOSGiBundleAction extends SapphireActionHandler
{

    @Override
    protected Object run( Presentation context )
    {
        final NewModuleFragmentOp op = context.part().getModelElement().nearest( NewModuleFragmentOp.class );

        final ElementList<OverrideFilePath> currentFiles = op.getOverrideFiles();

        final String projectName = op.getProjectName().content();

        final OSGiBundleFileSelectionDialog dialog = new OSGiBundleFileSelectionDialog( null, currentFiles, projectName );

        final String runtimeName = op.getLiferayRuntimeName().content();

        final IRuntime runtime = ServerUtil.getRuntime( runtimeName );

        final IPath temp = GradleCore.getDefault().getStateLocation();

        dialog.setTitle( "Add files from OSGi bundle to override" );

        final PortalBundle portalBundle = LiferayServerCore.newPortalBundle( runtime.getLocation() );
        String currentOSGiBundle = op.getHostOsgiBundle().content();

        if( !currentOSGiBundle.endsWith( "jar" ) )
        {
            currentOSGiBundle = currentOSGiBundle + ".jar";
        }

        ServerUtil.getModuleFileFrom70Server( runtime, currentOSGiBundle, temp );

        if( portalBundle != null )
        {
            try
            {
                File module = portalBundle.getOSGiBundlesDir().append( "modules" ).append( currentOSGiBundle ).toFile();

                if( !module.exists() )
                {
                    module = GradleCore.getDefault().getStateLocation().append( currentOSGiBundle ).toFile();
                }

                dialog.setInput( module );
            }
            catch( Exception e )
            {
            }
        }

        if( dialog.open() == Window.OK )
        {
            Object[] selected = dialog.getResult();

            for( int i = 0; i < selected.length; i++ )
            {
                OverrideFilePath file = op.getOverrideFiles().insert();
                file.setValue( selected[i].toString() );
            }
        }

        return Status.createOkStatus();
    }

    public AddFilesFromOSGiBundleAction()
    {
        super();
    }
}
