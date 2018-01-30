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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.server.util.JavaUtil;
import com.liferay.ide.server.util.LayeredModulePathFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Simon Jiang
 * @author Charles Wu
 */
public class PortalWildFlyBundleFactory extends PortalJBossBundleFactory
{
    private static final String WF_100_RELEASE_MANIFEST_KEY = "JBoss-Product-Release-Version";

    @Override
    public PortalBundle create( Map<String, String> appServerProperties )
    {
        return new PortalWildFlyBundle( appServerProperties );
    }

    @Override
    public PortalBundle create( IPath location )
    {
        return new PortalWildFlyBundle( location );
    }

    @Override
    protected boolean detectBundleDir( IPath path )
    {
        if( !path.toFile().exists() )
        {
            return false;
        }

        if( path.append( "modules" ).toFile().exists() && path.append( "standalone" ).toFile().exists() &&
            path.append( "bin" ).toFile().exists() )
        {
            String vers =
                getManifestPropFromJBossModulesFolder(
                    new File[] { new File( path.toPortableString(), "modules" ) }, "org.jboss.as.product",
                    "wildfly-full/dir/META-INF", WF_100_RELEASE_MANIFEST_KEY );

            if( vers != null && vers.startsWith( "10." ) )
            {
                return true;
            }
            else
            {
                return super.detectBundleDir( path );
            }
        }

        return false;
    }

    protected String getManifestPropFromJBossModulesFolder(
        File[] moduleRoots, String moduleId, String slot, String property )
    {
        File[] layeredRoots = LayeredModulePathFactory.resolveLayeredModulePath( moduleRoots );

        for( int i = 0; i < layeredRoots.length; i++ )
        {
            IPath[] manifests = getFilesForModule( layeredRoots[i], moduleId, slot, manifestFilter() );

            if( manifests.length > 0 )
            {
                String value = JavaUtil.getManifestProperty( manifests[0].toFile(), property );

                if( value != null )
                    return value;
                return null;
            }
        }
        return null;
    }

    private static FileFilter manifestFilter()
    {
        return new FileFilter()
        {

            @Override
            public boolean accept( File pathname )
            {
                if( pathname.isFile() && pathname.getName().toLowerCase().equals( "manifest.mf" ) )
                {
                    return true;
                }
                return false;
            }
        };
    }

    private static IPath[] getFilesForModule( File modulesFolder, String moduleName, String slot, FileFilter filter )
    {
        String slashed = moduleName.replaceAll( "\\.", "/" );
        slot = ( slot == null ? "main" : slot );

        return getFiles( modulesFolder, new Path( slashed ).append( slot ), filter );

    }

    private static IPath[] getFiles( File modulesFolder, IPath moduleRelativePath, FileFilter filter )
    {
        File[] layeredPaths = LayeredModulePathFactory.resolveLayeredModulePath( modulesFolder );

        for( int i = 0; i < layeredPaths.length; i++ )
        {
            IPath lay = new Path( layeredPaths[i].getAbsolutePath() );
            File layeredPath = new File( lay.append( moduleRelativePath ).toOSString() );

            if( layeredPath.exists() )
            {
                return getFilesFrom( layeredPath, filter );
            }
        }
        return new IPath[0];
    }

    private static IPath[] getFilesFrom( File layeredPath, FileFilter filter )
    {
        ArrayList<IPath> list = new ArrayList<IPath>();
        File[] children = layeredPath.listFiles();

        for( int i = 0; i < children.length; i++ )
        {
            if( filter.accept( children[i] ) )
            {
                list.add( new Path( children[i].getAbsolutePath() ) );
            }
        }

        return list.toArray( new IPath[list.size()] );
    }

}
