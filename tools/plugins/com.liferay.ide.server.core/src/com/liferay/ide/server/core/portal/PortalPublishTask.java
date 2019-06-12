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

package com.liferay.ide.server.core.portal;

import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.gogo.GogoBundleDeployer;

import java.lang.reflect.Constructor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.core.model.PublishTaskDelegate;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class PortalPublishTask extends PublishTaskDelegate {

	public PortalPublishTask() {
	}

	@SuppressWarnings("rawtypes")
	public PublishOperation[] getTasks(IServer server, int kind, List modules, List kindList) {
		List<BundlePublishOperation> tasks = new ArrayList<>();

		PortalServerBehavior serverBehavior = (PortalServerBehavior)server.loadAdapter(
			PortalServerBehavior.class, null);

		if (ListUtil.isNotEmpty(modules)) {
			int size = modules.size();

			for (int i = 0; i < size; i++) {
				IModule[] module = (IModule[])modules.get(i);
				Integer deltaKind = (Integer)kindList.get(i);

				boolean needClean = false;

				IModuleResourceDelta[] deltas = ((Server)server).getPublishedResourceDelta(module);

				if ((deltas.length == 1) && (kind == IServer.PUBLISH_AUTO) &&
					(deltaKind == ServerBehaviourDelegate.CHANGED)) {

					IModuleResource moduleResource = deltas[0].getModuleResource();

					if (".classpath".equals(moduleResource.getName())) {
						continue;
					}
				}

				for (IModuleResourceDelta delta : deltas) {
					IModuleResource resource = delta.getModuleResource();

					IFile resourceFile = (IFile)resource.getAdapter(IFile.class);

					if (resourceFile != null) {
						String resourceFileName = resourceFile.getName();

						if (resourceFileName.equals("bnd.bnd")) {
							needClean = true;

							break;
						}
					}
				}

				switch (kind) {
					case IServer.PUBLISH_FULL:
					case IServer.PUBLISH_INCREMENTAL:
					case IServer.PUBLISH_AUTO:
						IProject project = module[0].getProject();

						switch (deltaKind) {
							case ServerBehaviourDelegate.ADDED:
								_addOperation(BundlePublishFullAddCleanBuild.class, tasks, server, module);

								break;

							case ServerBehaviourDelegate.CHANGED:
								if (needClean) {
									_addOperation(BundlePublishFullAddCleanBuild.class, tasks, server, module);
								}
								else {
									_addOperation(BundlePublishFullAdd.class, tasks, server, module);
								}

								break;

							case ServerBehaviourDelegate.REMOVED:
								_addOperation(BundlePublishFullRemove.class, tasks, server, module);

								break;

							case ServerBehaviourDelegate.NO_CHANGE:
								IBundleProject bundleProject = LiferayCore.create(IBundleProject.class, project);

								if (bundleProject != null) {
									try {
										if (_isUserRedeploy(serverBehavior, module[0]) ||
											!_isDeployed(server, bundleProject.getSymbolicName())) {

											_addOperation(BundlePublishFullAddCleanBuild.class, tasks, server, module);
										}
									}
									catch (CoreException ce) {
										LiferayServerCore.logError(
											"Unable to get bsn for project " + project.getName(), ce);
									}
								}

								break;

							default:

								break;
						}

						break;

					default:

						break;
				}
			}
		}

		return tasks.toArray(new PublishOperation[0]);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public PublishOperation[] getTasks(IServer server, List modules) {
		return super.getTasks(server, modules);
	}

	private void _addOperation(
		Class<? extends BundlePublishOperation> opClass, List<BundlePublishOperation> tasks, IServer server,
		IModule[] module) {

		for (BundlePublishOperation task : tasks) {
			Class<? extends BundlePublishOperation> c = task.getClass();

			if (c.equals(opClass)) {
				task.addModule(module);

				return;
			}
		}

		try {
			Constructor<? extends BundlePublishOperation> constructor = opClass.getConstructor(
				IServer.class, IModule[].class);

			BundlePublishOperation op = constructor.newInstance(server, module);

			tasks.add(op);
		}
		catch (Exception e) {
			LiferayServerCore.logError("Unable to add bundle operation", e);
		}
	}

	private boolean _isDeployed(IServer server, String bsn) {
		boolean deployed = false;

		if (server.getServerState() == IServer.STATE_STARTED) {
			try {
				deployed = new GogoBundleDeployer().getBundleId(bsn) > 0;
			}
			catch (Exception e) {
			}
		}

		return deployed;
	}

	private boolean _isUserRedeploy(PortalServerBehavior serverBehavior, IModule module) {
		if (serverBehavior.getInfo() != null) {
			IAdaptable serverBehaviorInfo = serverBehavior.getInfo();

			Object moduleInfo = serverBehaviorInfo.getAdapter(IModule.class);

			return module.equals(moduleInfo);
		}

		return false;
	}

}