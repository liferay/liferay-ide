/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.LiferayDescriptorHelperReader;
import com.liferay.ide.project.core.LiferayProjectCore;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentType;


/**
 * This manager can obtain the corresponding descriptor helper via Eclipse extension-point,
 * When you cann't access them because of the package dependency.
 * 
 * @author Kuo Zhang
 */
public class LiferayDescriptorHelperManager implements ILiferayConstants
{
    private static LiferayDescriptorHelperManager instance;

    public static LiferayDescriptorHelperManager getInstance()
    {
        if( instance == null )
        {
            instance = new LiferayDescriptorHelperManager();
        }

        return instance;
    }

    private LiferayDescriptorHelperManager()
    {
    }

    private IContentType getContentType( IFile descriptorFile )
    {
        IContentType retval = null;

        try
        {
            retval = descriptorFile.getContentDescription().getContentType();
        }
        catch( CoreException e )
        {
            LiferayProjectCore.logError( "Error finding content type.", e );
        }

        return retval;
    }

    // return all descriptor helpers, if corresponding descriptor files exist.
    public LiferayDescriptorHelper[] getDescriptorHelpers( IProject project )
    {
        final String[] fileNames = { PORTLET_XML_FILE, 
                                     LIFERAY_PORTLET_XML_FILE,
                                     LIFERAY_DISPLAY_XML_FILE,
                                     LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE,
                                     LIFERAY_SERVICE_BUILDER_XML_FILE,
                                     LIFERAY_HOOK_XML_FILE,
                                     LIFERAY_LAYOUTTPL_XML_FILE,
                                     LIFERAY_LOOK_AND_FEEL_XML_FILE }; 

        List<LiferayDescriptorHelper> retval = new ArrayList<LiferayDescriptorHelper>();

        if( ! CoreUtil.isLiferayProject( project ) )
        {
            project = CoreUtil.getLiferayProject( project );

            if( project == null || ! project.exists() )
            {
                return null;
            }
        }

        final LiferayDescriptorHelper[] allHelpers = LiferayDescriptorHelperReader.getInstance().getAllHelpers();

        for( String fileName : fileNames )
        {
            final IFile descriptorFile = CoreUtil.getDescriptorFile( project, fileName );

            if( descriptorFile != null && descriptorFile.exists() )
            {
                for( LiferayDescriptorHelper helper : allHelpers )
                {
                    if( helper.getContentType().equals( getContentType( descriptorFile ) ) )
                    {
                        helper.setProject( project );

                        retval.add( helper );
                    }
                }
            }
        }

        return retval.toArray( new LiferayDescriptorHelper[0] );
    }

    private LiferayDescriptorHelper[] getDescriptorHelpers( IProject project, String fileName )
    {
        if( ! CoreUtil.isLiferayProject( project ) )
        {
            project = CoreUtil.getLiferayProject( project );

            if( project == null || !project.exists() )
            {
                return null;
            }
        }

        final IFile descriptorFile = CoreUtil.getDescriptorFile( project, fileName );

        if( descriptorFile != null && descriptorFile.exists() )
        {
            IContentType contentType = getContentType( descriptorFile );

            final LiferayDescriptorHelper[] helpers = LiferayDescriptorHelperReader.getInstance().getHelpers( contentType );

            if( ! CoreUtil.isNullOrEmpty( helpers ) )
            {
                for( LiferayDescriptorHelper helper : helpers )
                {
                    helper.setProject( project );
                }

                return helpers;
            }
        }

        return null;
    }

    // return descriptor helper of "liferay-portlet.xml" file
    public LiferayDescriptorHelper getLiferayDisplayDescriptorHelper( IProject project )
    {
        final LiferayDescriptorHelper[] helpers = getDescriptorHelpers( project, LIFERAY_DISPLAY_XML_FILE );

        if( !CoreUtil.isNullOrEmpty( helpers ) )
        {
            return helpers[0];
        }

        return null;
    }

    // return descriptor helpers of "liferay-portlet.xml" file, there helpers, LiferayPortletDescriptorHelper,
    // JSFLiferayPortletDescriptorHelper, and VaadinLiferayPortletDescriptorHelper.
    public LiferayDescriptorHelper[] getLiferayPortletDescriptorHelpers( IProject project )
    {
        final LiferayDescriptorHelper[] helpers = getDescriptorHelpers( project, LIFERAY_PORTLET_XML_FILE );

        if( ! CoreUtil.isNullOrEmpty( helpers ) )
        {
            return helpers;
        }

        return null;
    }

    // return descriptor helper of "liferay-plugin-package.properties" file
    public LiferayDescriptorHelper getPluginPackageDescriptorHelper( IProject project )
    {
        final LiferayDescriptorHelper[] helpers =
            getDescriptorHelpers( project, LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE );

        if( ! CoreUtil.isNullOrEmpty( helpers ) )
        {
            return helpers[0];
        }

        return null;
    }

    // return descriptor helpers of "portlet.xml" file, three helpers, PorletDescriptorHelper, JSFPortletDescriptorHelper
    // and VaadinPortletHelper. Carefully handle them when using.
    public LiferayDescriptorHelper[] getPortletDescriptorHelpers( IProject project )
    {
        final LiferayDescriptorHelper[] helpers = getDescriptorHelpers( project, PORTLET_XML_FILE );

        if( ! CoreUtil.isNullOrEmpty( helpers ) )
        {
            return helpers;
        }

        return null;
    }

    // return descriptor helper of "service.xml" file.
    public LiferayDescriptorHelper getServiceBuilderDescriptorHelper( IProject project )
    {
        final LiferayDescriptorHelper[] helpers = getDescriptorHelpers( project, LIFERAY_SERVICE_BUILDER_XML_FILE );

        if( ! CoreUtil.isNullOrEmpty( helpers ) )
        {
            return helpers[0];
        }

        return null;
    }

    // TODO, get helpers of other type
}
