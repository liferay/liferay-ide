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

package com.liferay.ide.project.core;

import com.liferay.ide.core.Event;
import com.liferay.ide.core.EventListener;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.ListenerRegistry;
import com.liferay.ide.core.workspace.ProjectDeletedEvent;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.util.ProjectModuleFactoryDelegate;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class BundleFactoryDelegate extends ProjectModuleFactoryDelegate implements EventListener {

	public BundleFactoryDelegate() {
		ListenerRegistry listenerRegistry = LiferayCore.listenerRegistry();

		listenerRegistry.addEventListener(this);
	}

	public IModule createSimpleModule(IProject project) {
		return createModule(project.getName(), project.getName(), "liferay.bundle", "1.0", project);
	}

	@Override
	public ModuleDelegate getModuleDelegate(IModule module) {
		BundleModulelDelegate md = _moduleDelegates.get(module);

		if (md != null) {
			return md;
		}

		md = new BundleModulelDelegate(module.getProject());

		_moduleDelegates.put(module, md);

		return md;
	}

	@Override
	public void onEvent(Event event) {
		if (event instanceof ProjectDeletedEvent) {
			clearCache(((ProjectDeletedEvent)event).getProject());
		}
	}

	@Override
	protected IModule[] createModules(IProject project) {
		if (!ProjectUtil.is7xServerDeployableProject(project)) {
			return new IModule[0];
		}

		return new IModule[] {createSimpleModule(project)};
	}

	@Override
	protected IPath[] getListenerPaths() {
		return new IPath[] {new Path(".project"), new Path("pom.xml"), new Path("bnd.bnd"), new Path("build.gradle")};
	}

	private Map<IModule, BundleModulelDelegate> _moduleDelegates = new HashMap<>(5);

}