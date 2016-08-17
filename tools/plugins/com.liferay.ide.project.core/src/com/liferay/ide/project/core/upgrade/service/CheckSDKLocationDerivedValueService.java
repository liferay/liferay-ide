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

package com.liferay.ide.project.core.upgrade.service;

import com.liferay.ide.project.core.upgrade.CodeUpgradeOp;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;

/**
 * @author Andy Wu
 */
public class CheckSDKLocationDerivedValueService extends DerivedValueService
{
    private FilteredListener<PropertyContentEvent> listener;

    private void checkProjects( CodeUpgradeOp op, SDK sdk )
    {
        File[] portlets = getFiles( sdk, "portlet" );

        if( portlets != null )
        {
            for( File file : portlets )
            {
                if( file.isDirectory() )
                {
                    op.setHasPortlet( "true" );

                    File serviceXml = new Path( file.getPath() ).append( "docroot/WEB-INF/service.xml" ).toFile();

                    if( serviceXml.exists() )
                    {
                        op.setHasServiceBuilder( "true" );
                    }
                }
            }
        }

        File[] hooks = getFiles( sdk, "hook" );

        if( hooks != null )
        {
            for( File file : hooks )
            {
                if( file.isDirectory() )
                {
                    op.setHasHook( "true" );

                    File serviceXml = new Path( file.getPath() ).append( "docroot/WEB-INF/service.xml" ).toFile();

                    if( serviceXml.exists() )
                    {
                        op.setHasServiceBuilder( "true" );
                    }
                }
            }
        }

        File[] exts = getFiles( sdk, "ext" );

        if( exts != null )
        {
            for( File file : exts )
            {
                if( file.isDirectory() )
                {
                    op.setHasExt( "true" );
                    break;
                }
            }
        }

        File[] layouttpls = getFiles( sdk, "layouttpl" );

        if( layouttpls != null )
        {
            for( File file : layouttpls )
            {
                if( file.isDirectory() )
                {
                    op.setHasLayout( "true" );
                    break;
                }
            }
        }

        File[] themes = getFiles( sdk, "theme" );

        if( themes != null )
        {
            for( File file : themes )
            {
                if( file.isDirectory() )
                {
                    op.setHasTheme( "true" );
                    break;
                }
            }
        }

        File[] webs = getFiles( sdk, "web" );

        if( webs != null )
        {
            for( File file : webs )
            {
                if( file.isDirectory() )
                {
                    op.setHasWeb( "true" );
                    break;
                }
            }
        }
    }

    @Override
    protected String compute()
    {
        CodeUpgradeOp op = op();

        final Path path = op.getSdkLocation().content();

        SDK sdk = SDKUtil.createSDKFromLocation( PathBridge.create( path ) );

        String liferay62ServerLocation = null;

        try
        {
            if( sdk != null )
            {
                liferay62ServerLocation =
                    (String) ( sdk.getBuildProperties( true ).get( ISDKConstants.PROPERTY_APP_SERVER_PARENT_DIR ) );

                checkProjects( op, sdk );
            }
            else
            {
                setAllStateFalse( op );
            }
        }
        catch( CoreException e )
        {
        }

        return liferay62ServerLocation;
    }

    private File[] getFiles( SDK sdk, String projectType )
    {
        IPath folderPath = sdk.getLocation().append( sdk.getPluginFolder( projectType ) );

        File folder = folderPath.toFile();

        if( !folder.exists() )
        {
            return null;
        }

        return folder.listFiles();
    }

    @Override
    protected void initDerivedValueService()
    {
        super.initDerivedValueService();

        this.listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( CodeUpgradeOp.PROP_SDK_LOCATION ).attach( this.listener );
    }

    private CodeUpgradeOp op()
    {
        return context( CodeUpgradeOp.class );
    }

    private void setAllStateFalse( CodeUpgradeOp op )
    {
        op.setHasPortlet( false );
        op.setHasServiceBuilder( false );
        op.setHasHook( false );
        op.setHasExt( false );
        op.setHasLayout( false );
        op.setHasTheme( false );
        op.setHasWeb( false );
    }

}
