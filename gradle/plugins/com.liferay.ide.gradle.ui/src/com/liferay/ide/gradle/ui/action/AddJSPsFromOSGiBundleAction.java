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

import com.liferay.ide.gradle.core.modules.NewJSPHookModuleOp;
import com.liferay.ide.gradle.core.modules.OSGiCustomJSP;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;

import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 */
public class AddJSPsFromOSGiBundleAction extends SapphireActionHandler
{

    @Override
    protected Object run( Presentation context )
    {
        NewJSPHookModuleOp op = context.part().getModelElement().nearest( NewJSPHookModuleOp.class );

        ElementList<OSGiCustomJSP> currentJSPs = op.getCustomJSPs();

        OSGiBundleFileSelectionDialog dialog = new OSGiBundleFileSelectionDialog( null, currentJSPs );

        final String runtimeName = op.getBundleName().content();

        IRuntime runtime = ServerUtil.getRuntime( runtimeName );

        dialog.setTitle( "Add jsps from OSGi Bundle" );

        PortalBundle portalBundle = LiferayServerCore.newPortalBundle( runtime.getLocation() );
        String currentOSGiBundle = op.getCustomOSGiBundle().content();

        if( portalBundle != null )
        {
            try
            {
                File modules =
                    portalBundle.getOSGiBundlesDir().append( "modules" ).append( currentOSGiBundle ).toFile();

                dialog.setInput( modules );
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
                OSGiCustomJSP jsp = op.getCustomJSPs().insert();
                jsp.setValue( selected[i].toString() );
            }
        }

        return Status.createOkStatus();
    }

    public AddJSPsFromOSGiBundleAction()
    {
        super();
    }
}
