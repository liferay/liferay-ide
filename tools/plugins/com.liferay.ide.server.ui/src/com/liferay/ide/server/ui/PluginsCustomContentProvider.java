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
 * Contributors:
 *      Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.server.ui;

import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.navigator.AbstractNavigatorContentProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.ui.navigator.INavigatorContentExtension;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class PluginsCustomContentProvider extends AbstractNavigatorContentProvider
{

    protected final static Object[] EMPTY = new Object[] {};

    private PluginsContent pluginsContentNode = null;

    public void dispose()
    {
    }

    public Object[] getChildren( Object parentElement )
    {
        if( parentElement instanceof PluginsContent )
        {
            return ( (PluginsContent) parentElement ).getChildren();
        }

        if( !( parentElement instanceof IServer ) )
        {
            return EMPTY;
        }

        IServer server = (IServer) parentElement;

        if( !ServerUtil.isLiferayRuntime( server ) )
        {
            return EMPTY;
        }

        List<IModule> liferayPlugins = new ArrayList<IModule>();

        for( IModule module : server.getModules() )
        {
            if( ProjectUtil.isLiferayFacetedProject( module.getProject() ) )
            {
                liferayPlugins.add( module );
            }
        }

        return EMPTY;
        // return new Object[] {new PluginsContent(liferayPlugins,
        // parentElement)};
    }

    public Object getParent( Object element )
    {
        if( element instanceof IWorkspaceRoot )
        {
            return null;
        }

        return null;
    }

    public void getPipelinedChildren( Object aParent, Set theCurrentChildren )
    {
        List<ModuleServer> redirectedModules = new ArrayList<ModuleServer>();

        // if a portlet module is going to be displayed, don't show it
        for( Object pipelinedChild : theCurrentChildren )
        {
            if( pipelinedChild instanceof ModuleServer )
            {
                ModuleServer module = (ModuleServer) pipelinedChild;

                if( ProjectUtil.isLiferayFacetedProject( module.getModule()[0].getProject() ) )
                {
                    redirectedModules.add( module );
                }
            }
        }

        for( ModuleServer redirectedModule : redirectedModules )
        {
            theCurrentChildren.remove( redirectedModule );
        }

        // add portlet contents if there are any liferay plugins
        if( redirectedModules.size() > 0 )
        {
            this.pluginsContentNode = new PluginsContent( redirectedModules, aParent );

            theCurrentChildren.add( this.pluginsContentNode );
        }
    }

    public Object getPipelinedParent( Object anObject, Object aSuggestedParent )
    {
        if( anObject instanceof ModuleServer )
        {
            IProject project = ( (ModuleServer) anObject ).getModule()[0].getProject();

            if( ProjectUtil.isLiferayFacetedProject( project ) && this.pluginsContentNode != null )
            {
                return this.pluginsContentNode;
            }
        }
        else if( anObject instanceof PluginsContent && anObject.equals( this.pluginsContentNode ) )
        {
            return this.pluginsContentNode.getParent();
        }

        return null;
    }

    public boolean hasChildren( Object element, boolean currentHasChildren)
    {
        if( element instanceof ModuleServer )
        {
            INavigatorContentExtension serverContent =
                getConfig().getService().getContentExtensionById(
                    getConfig().getExtension().getDescriptor().getSuppressedExtensionId() );

            return serverContent.getContentProvider().hasChildren( element );
        }
        else if( element instanceof PluginsContent )
        {
            return ( (PluginsContent) element ).getSize() > 0;
        }

        return false;
    }

    @Override
    public boolean hasPipelinedChildren( Object element, boolean currentHasChildren )
    {
        return hasChildren( element, currentHasChildren );
    }

    public boolean interceptRefresh( PipelinedViewerUpdate aRefreshSynchronization )
    {
        boolean needToExpandPluginsNode = false;

        Object obj = aRefreshSynchronization.getRefreshTargets().toArray()[0];

        if( obj instanceof ModuleServer )
        {
            ModuleServer module = (ModuleServer) obj;

            IModule[] modules = module.getServer().getModules();

            for( IModule m : modules )
            {
                if( module.getModule()[0].equals( m ) )
                {
                    needToExpandPluginsNode = true;
                }
            }
        }

        return false;
    }

    public boolean interceptUpdate( PipelinedViewerUpdate anUpdateSynchronization )
    {
        // Set refreshTargets = anUpdateSynchronization.getRefreshTargets();
        // for (Object refreshTarget : refreshTargets) {
        // if (refreshTarget instanceof IServer) {
        // IServer server = (IServer)refreshTarget;
        // }
        // }

        return false;
    }

}
