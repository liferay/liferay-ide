/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 * Contributors:
 *      Kamesh Sampath - initial implementation
 *      Gregory Amerson - initial implementation review and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.portlet.ui.navigator;

import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.ui.navigator.AbstractNavigatorContentProvider;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.ui.internal.navigator.NavigatorContentService;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class PortletResourcesContentProvider extends AbstractNavigatorContentProvider
{

    protected final static Object[] EMPTY = new Object[] {};

    public void dispose()
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren( Object element )
    {
        if( element instanceof IProject )
        {
            final IProject project = (IProject) element;

            if( ProjectUtil.isLiferayProject( project ) )
            {
                return new Object[] { new PortletResourcesRootNode( this, project ) };
            }
        }
        else if( element instanceof PortletResourcesRootNode )
        {
            return new Object[] { new PortletsNode( (PortletResourcesRootNode) element ) };
        }
        else if( element instanceof PortletsNode )
        {
            return ( (PortletsNode) element ).getChildren();
        }

        return null;
    }

    public boolean hasChildren( Object element )
    {
        if( element instanceof IProject )
        {
            final IProject project = (IProject) element;

            final IFile portletXmlFile = ProjectUtil.getPortletXmlFile( project );

            if( portletXmlFile != null && portletXmlFile.exists() )
            {
                return true;
            }
        }
        else if( element instanceof PortletResourcesRootNode )
        {
            final PortletResourcesRootNode rootNode = (PortletResourcesRootNode) element;

            return rootNode.hasChildren();
        }
        else if( element instanceof PortletsNode )
        {
            final PortletsNode portletsNode = (PortletsNode) element;

            return portletsNode.hasChildren();
        }

        return false;
    }

    public void refresh()
    {
        final NavigatorContentService s = (NavigatorContentService) getConfig().getService();

        UIUtil.sync( new Runnable()
        {

            public void run()
            {
                try
                {
                    final CommonViewer viewer = (CommonViewer) s.getViewer();
                    TreePath[] paths = viewer.getExpandedTreePaths();
                    viewer.refresh( true );
                    viewer.setExpandedTreePaths( paths );
                }
                catch( Exception e )
                {
                }
            }
        } );

    }

}
