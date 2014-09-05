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
 *******************************************************************************/

package com.liferay.ide.portlet.core.lfportlet.model.internal;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;

import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;

/**
 * @author Simon Jiang
 */
public class LiferayScriptPossibleValuesService extends PossibleValuesService
{

    private String type;

    @Override
    protected void initPossibleValuesService()
    {
        super.initPossibleValuesService();

        this.type = this.param( "type" );
    }

    @Override
    protected void compute( Set<String> values )
    {
        final IProject project = context( Element.class ).adapt( IProject.class );

        if( project != null )
        {
            final IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

            if( webappRoot != null )
            {
                for( IContainer container : webappRoot.getUnderlyingFolders() )
                {
                    final IPath location = container.getLocation();

                    if( location != null )
                    {
                        if( location.toFile().exists())
                        {
                            values.addAll( new PropertiesVisitor().visitScriptFiles( container, type, values ) );
                        }     
                    }
                }
            }
        }
    }

    private static class PropertiesVisitor implements IResourceProxyVisitor
    {
        IResource entryResource = null;
        String type = null;
        Set<String> values = null;

        public boolean visit( IResourceProxy resourceProxy )
        {
            if( resourceProxy.getType() == IResource.FILE && resourceProxy.getName().endsWith( type ) )
            {
                IResource resource = resourceProxy.requestResource();

                if( resource.exists() )
                {
                    String relativePath =
                        resource.getLocation().makeRelativeTo( entryResource.getLocation() ).toString();

                    try
                    {
                        if( !relativePath.startsWith( "/" ) )
                        {
                            values.add( "/" + relativePath );
                        }

                    }
                    catch( Exception e )
                    {
                        return true;
                    }
                }
            }

            return true;
        }

        public Set<String> visitScriptFiles( final IResource container, final String type, final Set<String> values )
        {
            this.entryResource = container;
            this.type = type;
            this.values = values;
            try
            {
                container.accept( this, IContainer.EXCLUDE_DERIVED );
            }
            catch( CoreException e )
            {
                LiferayCore.logError( e );
            }

            return values;
        }
    }

}
