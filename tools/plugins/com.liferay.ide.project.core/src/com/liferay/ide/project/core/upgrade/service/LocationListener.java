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
 */
public class LocationListener extends FilteredListener<PropertyContentEvent>
{

    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        CodeUpgradeOp op = op( event );

        IPath location = PathBridge.create( op.getLocation().content() );

        if( location.toFile().exists() )
        {
            SDK sdk = new SDK( location );

            IPath portletFolder = location.append( sdk.getPluginFolder( "portlet" ) );
            IPath hookFolder = location.append( sdk.getPluginFolder( "hook" ) );
            IPath extFolder = location.append( sdk.getPluginFolder( "ext" ) );
            IPath layouttplFolder = location.append( sdk.getPluginFolder( "layouttpl" ) );
            IPath themeFolder = location.append( sdk.getPluginFolder( "theme" ) );
            IPath webFolder = location.append( sdk.getPluginFolder( "web" ) );

            for( File file : portletFolder.toFile().listFiles() )
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

            for( File file : hookFolder.toFile().listFiles() )
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

            for( File file : extFolder.toFile().listFiles() )
            {
                if( file.isDirectory() )
                {
                    op.setHasExt( "true" );
                    break;
                }
            }

            for( File file : layouttplFolder.toFile().listFiles() )
            {
                if( file.isDirectory() )
                {
                    op.setHasLayout( "true" );
                    break;
                }
            }

            for( File file : themeFolder.toFile().listFiles() )
            {
                if( file.isDirectory() )
                {
                    op.setHasTheme( "true" );
                    break;
                }
            }

            for( File file : webFolder.toFile().listFiles() )
            {
                if( file.isDirectory() )
                {
                    op.setHasWeb( "true" );
                    break;
                }
            }

            for( File file : themeFolder.toFile().listFiles() )
            {
                if( file.isDirectory() )
                {
                    op.setHasTheme( "true" );
                    break;
                }
            }
        }
    }

    protected CodeUpgradeOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( CodeUpgradeOp.class );
    }

}
