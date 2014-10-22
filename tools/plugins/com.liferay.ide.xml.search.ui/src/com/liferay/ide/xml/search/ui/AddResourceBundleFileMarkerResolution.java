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

package com.liferay.ide.xml.search.ui;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;

import java.io.ByteArrayInputStream;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Terry Jia
 */
public class AddResourceBundleFileMarkerResolution extends AbstractResourceBundleMarkerResolution
{

    private String resourceBundlePackage = "content";

    private String resourceBundleName = "Language";

    private String portletName = "";

    public AddResourceBundleFileMarkerResolution( IMarker marker, String portletName )
    {
        super( marker );

        this.portletName = portletName;
    }

    private void checkResourceBundleElement( IProject project )
    {
        PortletDescriptorHelper portletDescriptorHelper = new PortletDescriptorHelper( project );

        String[] resouceBundles = portletDescriptorHelper.getAllResourceBundles();

        if( resouceBundles.length == 0 )
        {
            portletDescriptorHelper.addResourceBundle( resourceBundlePackage + "." + resourceBundleName, portletName );
        }
        else
        {
            for( String resouceBundle : resouceBundles )
            {
                if( !CoreUtil.isNullOrEmpty( resouceBundle ) )
                {
                    String[] paths = resouceBundle.split( "\\." );

                    if( paths.length > 2 )
                    {
                        StringBuffer sb = new StringBuffer();

                        for( int i = 0; i < ( paths.length - 1 ); i++ )
                        {
                            sb.append( paths[i] );
                            sb.append( "/" );
                        }

                        resourceBundlePackage = sb.toString();
                        resourceBundleName = paths[paths.length - 1];
                    }
                    else if( paths.length == 2 )
                    {
                        resourceBundlePackage = paths[0];
                        resourceBundleName = paths[1];
                    }
                    else if( paths.length == 1 )
                    {
                        resourceBundlePackage = "";
                        resourceBundleName = paths[0];
                    }

                    break;
                }
            }
        }
    }

    @Override
    public String getLabel()
    {
        return "Create a new default resource bundle add it to " + portletName + " portlet";
    }

    public Image getImage()
    {
        final URL url = LiferayXMLSearchUI.getDefault().getBundle().getEntry( "/icons/resource-bundle-new.png" );

        return ImageDescriptor.createFromURL( url ).createImage();
    }

    @Override
    protected void resolve( IMarker marker )
    {
        final IProject project = marker.getResource().getProject();

        if( getResourceKey(marker) == null || project == null )
        {
            return;
        }

        try
        {
            checkResourceBundleElement( project );

            final ILiferayProject liferayProject = LiferayCore.create( project );

            if( liferayProject == null )
            {
                return;
            }

            final IFolder folder = liferayProject.getSourceFolder( "resources" ).getFolder( resourceBundlePackage );

            if( !folder.exists() )
            {
                CoreUtil.makeFolders( folder );
            }

            final IFile resourceBundle = folder.getFile( resourceBundleName + ".properties" );

            String resourceKey = getResourceKey( marker );

            if( CoreUtil.isNullOrEmpty( resourceKey ) )
            {
                return;
            }

            String resourceValue = getDefaultResourceValue( resourceKey );
            String resourcePropertyLine = resourceKey + "=" + resourceValue + "\n";

            if( !resourceBundle.exists() )
            {
                IFolder parent = (IFolder) resourceBundle.getParent();

                CoreUtil.prepareFolder( parent );

                resourceBundle.create(
                    new ByteArrayInputStream( resourcePropertyLine.getBytes( "UTF-8" ) ), IResource.FORCE, null );
            }
            else
            {
                String contents = CoreUtil.readStreamToString( resourceBundle.getContents() );

                StringBuffer sb = new StringBuffer();

                sb.append( contents );
                sb.append( resourceKey );
                sb.append( "=" );
                sb.append( resourceValue );
                sb.append( "\n" );

                resourceBundle.setContents(
                    new ByteArrayInputStream( sb.toString().getBytes( "UTF-8" ) ), IResource.FORCE,
                    new NullProgressMonitor() );
            }

            openEditor( resourceBundle );
        }
        catch( Exception e )
        {
            LiferayXMLSearchUI.logError( e );
        }
    }

}
