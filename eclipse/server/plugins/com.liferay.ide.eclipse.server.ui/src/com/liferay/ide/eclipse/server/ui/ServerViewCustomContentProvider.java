/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.server.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.INavigatorContentExtension;
import org.eclipse.ui.navigator.IPipelinedTreeContentProvider;
import org.eclipse.ui.navigator.PipelinedShapeModification;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;
import org.eclipse.wst.server.ui.internal.viewers.BaseContentProvider;

import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.server.util.ServerUtil;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class ServerViewCustomContentProvider extends BaseContentProvider
	implements ITreeContentProvider, IPipelinedTreeContentProvider {

	protected final static Object[] EMPTY = new Object[] {};
	
	private ICommonContentExtensionSite config;
	
	private PluginsContent pluginsContentNode = null;

	public void dispose() {
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof PluginsContent) {
			return ((PluginsContent) parentElement).getChildren();
		}

		if (!(parentElement instanceof IServer)) {
			return EMPTY;
		}

		IServer server = (IServer) parentElement;

		if (!ServerUtil.isPortalRuntime(server)) {
			return EMPTY;
		}

		List<IModule> liferayPlugins = new ArrayList<IModule>();
		
		for (IModule module : server.getModules()) {
			if (ProjectUtil.isLiferayProject(module.getProject())) {
				liferayPlugins.add(module);
			}
		}
		
		return EMPTY;
		// return new Object[] {new PluginsContent(liferayPlugins,
		// parentElement)};
	}

	public Object[] getElements(Object inputElement) {
		return null;
	}

	public Object getParent(Object element) {
		if (element instanceof IWorkspaceRoot) {
			return null;
		}
		
		return null;
	}

	public void getPipelinedChildren(Object aParent, Set theCurrentChildren) {
		List<ModuleServer> redirectedModules = new ArrayList<ModuleServer>();
		
		// if a portlet module is going to be displayed, don't show it
		for (Object pipelinedChild : theCurrentChildren) {
			if (pipelinedChild instanceof ModuleServer) {
				ModuleServer module = (ModuleServer) pipelinedChild;
				
				if (ProjectUtil.isLiferayProject(module.getModule()[0].getProject())) {
					redirectedModules.add(module);
				}
			}
		}

		for (ModuleServer redirectedModule : redirectedModules) {
			theCurrentChildren.remove(redirectedModule);
		}

		// add portlet contents if there are any liferay plugins
		if (redirectedModules.size() > 0) {
			this.pluginsContentNode = new PluginsContent(redirectedModules, aParent);
			
			theCurrentChildren.add(this.pluginsContentNode);
		}
	}

	public void getPipelinedElements(Object anInput, Set theCurrentElements) {
	}

	public Object getPipelinedParent(Object anObject, Object aSuggestedParent) {
		if (anObject instanceof ModuleServer) {
			IProject project = ((ModuleServer) anObject).getModule()[0].getProject();
			
			if (ProjectUtil.isLiferayProject(project) && this.pluginsContentNode != null) {
				return this.pluginsContentNode;
			}
		}
		else if (anObject instanceof PluginsContent && anObject.equals(this.pluginsContentNode)) {
			return this.pluginsContentNode.getParent();
		}
		
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof ModuleServer) {
			INavigatorContentExtension serverContent =
				config.getService().getContentExtensionById(
					config.getExtension().getDescriptor().getSuppressedExtensionId());
			
			return serverContent.getContentProvider().hasChildren(element);
		}
		else if (element instanceof PluginsContent) {
			return ((PluginsContent) element).getSize() > 0;
		}
		
		return false;
	}

	public void init(ICommonContentExtensionSite aConfig) {
		this.config = aConfig;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public PipelinedShapeModification interceptAdd(PipelinedShapeModification anAddModification) {
		return null;
	}

	public boolean interceptRefresh(PipelinedViewerUpdate aRefreshSynchronization) {
		boolean needToExpandPluginsNode = false;
		
		Object obj = aRefreshSynchronization.getRefreshTargets().toArray()[0];
		
		if (obj instanceof ModuleServer) {
			ModuleServer module = (ModuleServer) obj;
			
			IModule[] modules = module.getServer().getModules();
			
			for (IModule m : modules) {
				if (module.getModule()[0].equals(m)) {
					needToExpandPluginsNode = true;
				}
			}
		}
		
		return false;
	}

	public PipelinedShapeModification interceptRemove(PipelinedShapeModification aRemoveModification) {
		return null;
	}

	public boolean interceptUpdate(PipelinedViewerUpdate anUpdateSynchronization) {
		// Set refreshTargets = anUpdateSynchronization.getRefreshTargets();
		// for (Object refreshTarget : refreshTargets) {
		// if (refreshTarget instanceof IServer) {
		// IServer server = (IServer)refreshTarget;
		// }
		// }

		return false;
	}

	public void restoreState(IMemento aMemento) {
	}

	public void saveState(IMemento aMemento) {
	}

}
