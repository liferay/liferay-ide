/**
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
 */

package com.liferay.ide.server.ui;

import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.navigator.AbstractNavigatorContentProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.navigator.INavigatorContentDescriptor;
import org.eclipse.ui.navigator.INavigatorContentExtension;
import org.eclipse.ui.navigator.INavigatorContentService;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class PluginsCustomContentProvider extends AbstractNavigatorContentProvider {

	public void dispose() {
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof PluginsContent) {
			return ((PluginsContent)parentElement).getChildren();
		}

		if (!(parentElement instanceof IServer)) {
			return EMPTY;
		}

		IServer server = (IServer)parentElement;

		if (!ServerUtil.isLiferayRuntime(server)) {
			return EMPTY;
		}

		List<IModule> liferayPlugins = new ArrayList<>();

		for (IModule module : server.getModules()) {
			if (ProjectUtil.isLiferayFacetedProject(module.getProject())) {
				liferayPlugins.add(module);
			}
		}

		return EMPTY;
	}

	public Object getParent(Object element) {
		if (element instanceof IWorkspaceRoot) {
			return null;
		}

		return null;
	}

	public void getPipelinedChildren(Object aParent, Set theCurrentChildren) {
		List<ModuleServer> redirectedModules = new ArrayList<>();

		// if a portlet module is going to be displayed, don't show it

		for (Object pipelinedChild : theCurrentChildren) {
			if (pipelinedChild instanceof ModuleServer) {
				ModuleServer module = (ModuleServer)pipelinedChild;

				IModule m = module.getModule()[0];

				IProject project = m.getProject();

				if (ProjectUtil.isLiferayFacetedProject(project)) {
					redirectedModules.add(module);
				}
			}
		}

		for (ModuleServer redirectedModule : redirectedModules) {
			theCurrentChildren.remove(redirectedModule);
		}

		// add portlet contents if there are any liferay plugins

		if (!redirectedModules.isEmpty()) {
			_pluginsContentNode = new PluginsContent(redirectedModules, aParent);

			theCurrentChildren.add(_pluginsContentNode);
		}
	}

	public Object getPipelinedParent(Object anObject, Object aSuggestedParent) {
		if (anObject instanceof ModuleServer) {
			IModule m = ((ModuleServer)anObject).getModule()[0];

			IProject project = m.getProject();

			if (ProjectUtil.isLiferayFacetedProject(project) && (_pluginsContentNode != null)) {
				return _pluginsContentNode;
			}
		}
		else if (anObject instanceof PluginsContent && anObject.equals(_pluginsContentNode)) {
			return _pluginsContentNode.getParent();
		}

		return null;
	}

	public boolean hasChildren(Object element, boolean currentHasChildren) {
		if (element instanceof ModuleServer) {
			INavigatorContentService service = getConfig().getService();
			INavigatorContentExtension extension = getConfig().getExtension();

			INavigatorContentDescriptor descriptor = extension.getDescriptor();

			INavigatorContentExtension serverContent = service.getContentExtensionById(
				descriptor.getSuppressedExtensionId());

			ITreeContentProvider contentProvider = serverContent.getContentProvider();

			return contentProvider.hasChildren(element);
		}
		else if (element instanceof PluginsContent) {
			if (((PluginsContent)element).getSize() > 0) {
				return true;
			}

			return false;
		}

		return false;
	}

	@Override
	public boolean hasPipelinedChildren(Object element, boolean currentHasChildren) {
		return hasChildren(element, currentHasChildren);
	}

	public boolean interceptRefresh(PipelinedViewerUpdate aRefreshSynchronization) {
		boolean needToExpandPluginsNode = false;

		Set<?> targets = aRefreshSynchronization.getRefreshTargets();

		Object obj = targets.toArray()[0];

		if (obj instanceof ModuleServer) {
			ModuleServer module = (ModuleServer)obj;

			IServer server = module.getServer();

			IModule[] modules = server.getModules();

			for (IModule m : modules) {
				IModule m1 = module.getModule()[0];

				if (m1.equals(m)) {
					needToExpandPluginsNode = true;
				}
			}
		}

		return needToExpandPluginsNode;
	}

	public boolean interceptUpdate(PipelinedViewerUpdate anUpdateSynchronization) {
		return false;
	}

	protected static final Object[] EMPTY = {};

	private PluginsContent _pluginsContentNode = null;

}