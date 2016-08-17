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

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.platform.PathBridge;

import com.liferay.ide.project.core.upgrade.CodeUpgradeOp;
import com.liferay.ide.sdk.core.SDK;

/**
 * @author Terry Jia
 * @author Andy Wu
 */
public class SdkLocationListener extends FilteredListener<PropertyContentEvent>
{

    private File[] getFiles( IPath location, SDK sdk, String projectType )
    {
        IPath folderPath = location.append( sdk.getPluginFolder( projectType ) );

        File folder = folderPath.toFile();

        if( !folder.exists() )
        {
            return null;
        }

        return folder.listFiles();
    }

    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        CodeUpgradeOp op = op( event );

        IPath location = PathBridge.create( op.getSdkLocation().content() );

        if( location.toFile().exists() )
        {
            SDK sdk = new SDK( location );

            File[] portlets = getFiles( location, sdk, "portlet" );

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

            File[] hooks = getFiles( location, sdk, "hook" );

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

            File[] exts = getFiles( location, sdk, "ext" );

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

            File[] layouttpls = getFiles( location, sdk, "layouttpl" );

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

            File[] themes = getFiles( location, sdk, "theme" );

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

            File[] webs = getFiles( location, sdk, "web" );

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
    }

    protected CodeUpgradeOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( CodeUpgradeOp.class );
    }

}
