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

package com.liferay.ide.project.core.util;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.DescriptorHelper;

import org.eclipse.core.resources.IProject;
import org.osgi.framework.Version;

/**
 * @author Cindy Li
 */
public class LiferayDescriptorHelper extends DescriptorHelper
{

    public LiferayDescriptorHelper( IProject project )
    {
        super( project );
    }

    public String getDescriptorVersion()
    {
        String retval = null;

        try
        {
            final ILiferayProject lProject = LiferayCore.create( project );

            if( lProject != null )
            {
                final String versionStr = lProject.getPortalVersion();
                retval = getDescriptorVersionFromPortalVersion( versionStr );
            }
        }
        catch( Exception e )
        {
            LiferayCore.logError( "Could not get liferay runtime.", e ); //$NON-NLS-1$
        }

        if( retval == null )
        {
            retval = "6.0.0"; // use 6.0.0 as the default //$NON-NLS-1$
        }

        return retval;
    }

    protected String getDescriptorVersionFromPortalVersion( String versionStr )
    {
        final Version version = new Version( versionStr );

        final int major = version.getMajor();
        final int minor = version.getMinor();

        return Integer.toString( major ) + "." + Integer.toString( minor ) + ".0"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
