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

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.server.util.ServerUtil;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.services.PossibleValuesService;

/**
 * @author Gregory Amerson
 */
public class PortalFilterNamesPossibleValuesService extends PossibleValuesService
{

    private String[] servletFilterNames;

    @Override
    protected void fillPossibleValues( Set<String> values )
    {
        if( this.servletFilterNames == null )
        {
            final IFile hookFile = this.context().find( Element.class ).adapt( IFile.class );

            if( hookFile != null )
            {
                try
                {
                    final ILiferayProject liferayProject = LiferayCore.create( hookFile.getProject() );

                    if( liferayProject != null )
                    {
                        final IPath appServerPortalDir = liferayProject.getAppServerPortalDir();

                        this.servletFilterNames = ServerUtil.getServletFilterNames( appServerPortalDir );
                    }
                }
                catch( Exception e )
                {
                }
            }
        }

        if( this.servletFilterNames != null )
        {
            values.addAll( Arrays.asList( this.servletFilterNames ) );
        }
    }

}
