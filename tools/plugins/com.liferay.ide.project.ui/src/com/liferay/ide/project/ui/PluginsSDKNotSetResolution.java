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

package com.liferay.ide.project.ui;

import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.LiferayPluginSDKOp;
import com.liferay.ide.project.ui.dialog.LiferayProjectSelectionDialog;
import com.liferay.ide.sdk.core.SDKCorePlugin;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.def.DefinitionLoader.Reference;
import org.eclipse.sapphire.ui.forms.DialogDef;
import org.eclipse.sapphire.ui.forms.swt.SapphireDialog;
import org.eclipse.ui.IMarkerResolution;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Simon Jiang
 */
public class PluginProjectSDKNotSetResolution implements IMarkerResolution
{

    public String getLabel()
    {
        return Msgs.setSDKForProject;
    }

    public void run( IMarker marker )
    {
        if( marker.getResource() instanceof IProject )
        {
            final IProject proj = (IProject) marker.getResource();

            LiferayPluginSDKOp op =
                ( ( LiferayPluginSDKOp ) ( LiferayPluginSDKOp.TYPE.instantiate().initialize() ) );
            final Reference<DialogDef> dialogRef = DefinitionLoader.sdef(
                LiferayProjectSelectionDialog.class ).dialog( "ConfigureLiferaySDK" );
            final SapphireDialog dialog =
                            new SapphireDialog( UIUtil.getActiveShell(), op, dialogRef );

            dialog.setBlockOnOpen( true );
            final int result = dialog.open();
            
            if( result != SapphireDialog.CANCEL )
            {
                String sdkName = op.getPluginsSDKName().content();
                saveSDKSetting( proj, sdkName );
            }

        }
    }

    
    public void saveSDKSetting( IProject project, String sdkName)
    {
        try
        {
            final IEclipsePreferences prefs = new ProjectScope( project ).getNode( SDKCorePlugin.PLUGIN_ID );
            prefs.put( SDKCorePlugin.PREF_KEY_SDK_NAME, sdkName );
            prefs.flush();
        }
        catch( BackingStoreException e )
        {
            LiferayProjectCore.logError( "Unable to persist sdk name to project " + project, e );
        }        
    }

    private static class Msgs extends NLS
    {

        public static String setSDKForProject;

        static
        {
            initializeMessages( PluginProjectSDKNotSetResolution.class.getName(), Msgs.class );
        }
    }
}
