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

package com.liferay.ide.gradle.core.modules;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.wst.server.core.IRuntime;

import com.liferay.ide.gradle.core.GradleCore;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;

/**
 * @author Terry Jia
 */
public class OSGiBundleListener extends FilteredListener<PropertyContentEvent>
{

    @Override
    protected void handleTypedEvent( PropertyContentEvent event )
    {
        NewModuleFragmentOp op = op( event );

        final IPath temp = GradleCore.getDefault().getStateLocation();

        final String runtimeName = op.getLiferayRuntimeName().content();
        final String hostOsgiBundle = op.getHostOsgiBundle().content();

        IRuntime runtime = ServerUtil.getRuntime( runtimeName );

        PortalBundle portalBundle = LiferayServerCore.newPortalBundle( runtime.getLocation() );

        File moduleOsgiBundle = portalBundle.getOSGiBundlesDir().append( "modules" ).append( hostOsgiBundle ).toFile();

        if( !moduleOsgiBundle.exists() )
        {
            final File f = new File( temp.toFile(), hostOsgiBundle );

            if( f.exists() )
            {
                return;
            }

            File[] files = ServerUtil.getMarketplaceLpkgFiles( portalBundle );

            InputStream in = null;
            FileOutputStream out = null;

            try
            {
                boolean found = false;

                for( File file : files )
                {
                    JarFile jar = new JarFile( file );
                    Enumeration<JarEntry> enu = jar.entries();

                    while( enu.hasMoreElements() )
                    {
                        JarEntry entry = enu.nextElement();

                        String name = entry.getName();

                        if( name.contains( hostOsgiBundle ) )
                        {
                            in = jar.getInputStream( entry );
                            found = true;

                            break;
                        }
                    }

                    if( found )
                    {
                        break;
                    }
                }

                if( in != null )
                {
                    out = new FileOutputStream( f );

                    final byte[] bytes = new byte[1024];
                    int count = in.read( bytes );

                    while( count != -1 )
                    {
                        out.write( bytes, 0, count );
                        count = in.read( bytes );
                    }

                    out.flush();
                }
            }
            catch( Exception e )
            {
            }
            finally
            {
                if( in != null )
                {
                    try
                    {
                        in.close();
                    }
                    catch( IOException e )
                    {
                    }
                }

                if( out != null )
                {
                    try
                    {
                        out.close();
                    }
                    catch( IOException e )
                    {
                    }
                }
            }
        }

        op.getOverrideFiles().clear();
    }

    protected NewModuleFragmentOp op( PropertyContentEvent event )
    {
        return event.property().element().nearest( NewModuleFragmentOp.class );
    }

}
