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

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.gradle.core.modules.NewModuleFragmentOp;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 */
public class HostOSGiBundlePossibleValuesService extends PossibleValuesService
{

    private List<String> bundles = null;

    @Override
    protected void compute( Set<String> values )
    {
        if( this.bundles != null )
        {
            values.addAll( this.bundles );
        }
        else
        {
            bundles = new ArrayList<String>();

            final NewModuleFragmentOp op = op();

            if( !op.disposed() )
            {
                final String runtimeName = op.getLiferayRuntimeName().content();

                IRuntime runtime = ServerUtil.getRuntime( runtimeName );

                PortalBundle portalBundle = LiferayServerCore.newPortalBundle( runtime.getLocation() );

                if( portalBundle != null )
                {
                    try
                    {
                        File modules = portalBundle.getOSGiBundlesDir().append( "modules" ).toFile();

                        File[] files = modules.listFiles( new FilenameFilter()
                        {
                            @Override
                            public boolean accept( File dir, String name )
                            {
                                return name.matches( ".*\\.web\\.jar" );
                            }
                        });

                        for( File file : files )
                        {
                            bundles.add( file.getName() );
                        }
                    }
                    catch( Exception e )
                    {
                        ProjectCore.logError( "Could not determine possible files.", e );
                    }
                }
            }

            values.addAll( bundles );
        }
    }

    private NewModuleFragmentOp op()
    {
        return context( NewModuleFragmentOp.class );
    }

    @Override
    public boolean ordered()
    {
        return true;
    }

    @Override
    public org.eclipse.sapphire.modeling.Status problem( Value<?> value )
    {
        return org.eclipse.sapphire.modeling.Status.createOkStatus();
    }

}
