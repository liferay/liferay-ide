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

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.core.ILiferayServerBehavior;
import com.liferay.ide.server.tomcat.core.util.LiferayTomcatUtil;
import com.liferay.ide.server.util.LiferayPublishHelper;

import java.io.File;
import java.io.InputStream;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jst.server.tomcat.core.internal.Messages;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServerBehaviour;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.XMLUtil;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Server;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;

import org.w3c.dom.Document;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings({"restriction", "rawtypes"})
public class LiferayTomcatServerBehavior extends TomcatServerBehaviour implements ILiferayServerBehavior {

	public LiferayTomcatServerBehavior() {
	}

	@Override
	public IPath getDeployedPath(IModule[] module) {
		return getModuleDeployDirectory(module[0]);
	}

	public ILiferayTomcatConfiguration getLiferayTomcatConfiguration() throws CoreException {
		return getLiferayTomcatServer().getLiferayTomcatConfiguration();
	}

	public LiferayTomcatServer getLiferayTomcatServer() {
		return (LiferayTomcatServer)getServer().loadAdapter(LiferayTomcatServer.class, null);
	}

	@Override
	public IPath getModuleDeployDirectory(IModule module) {
		IPath defaultPath = super.getModuleDeployDirectory(module);

		IPath updatedPath = null;

		if ((defaultPath != null) && (defaultPath.lastSegment() != null)) {
			IProject project = module.getProject();

			String requiredSuffix = ProjectUtil.getRequiredSuffix(project);

			String defaultPathLastSegment = defaultPath.lastSegment();

			if ((requiredSuffix != null) && !defaultPathLastSegment.endsWith(requiredSuffix)) {
				String lastSegment = defaultPath.lastSegment();

				IPath defaultPathWithoutLastSeg = defaultPath.removeLastSegments(1);

				updatedPath = defaultPathWithoutLastSeg.append(lastSegment + requiredSuffix);
			}
		}

		if (updatedPath == null) {
			return defaultPath;
		}

		return updatedPath;
	}

	@Override
	public IModuleResourceDelta[] getPublishedResourceDelta(IModule[] module) {
		return super.getPublishedResourceDelta(module);
	}

	public List<IModule[]> getRedeployModules() {
		return _redeployModules;
	}

	@Override
	public IModuleResource[] getResources(IModule[] module) {
		return super.getResources(module);
	}

	@Override
	public String[] getRuntimeVMArguments() {
		return super.getRuntimeVMArguments();
	}

	public IStatus moveContextToAutoDeployDir(
		IModule module, IPath deployDir, IPath baseDir, IPath autoDeployDir, boolean noPath, boolean serverStopped) {

		IPath confDir = baseDir.append("conf");

		IPath serverXml = confDir.append("server.xml");

		File serverXmlFile = serverXml.toFile();

		try (InputStream newInputStream = Files.newInputStream(serverXmlFile.toPath())) {
			Factory factory = new Factory();

			factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");

			Server publishedServer = (Server)factory.loadDocument(newInputStream);

			ServerInstance publishedInstance = new ServerInstance(publishedServer, null, null);

			IPath contextPath = null;

			if (autoDeployDir.isAbsolute()) {
				contextPath = autoDeployDir;
			}
			else {
				contextPath = baseDir.append(autoDeployDir);
			}

			File contextDir = contextPath.toFile();

			if (FileUtil.notExists(contextDir)) {
				contextDir.mkdirs();
			}

			Context context = publishedInstance.createContext(-1);

			context.setReloadable("true");

			String moduleName = module.getName();
			String requiredSuffix = ProjectUtil.getRequiredSuffix(module.getProject());

			String contextName = moduleName;

			if (!moduleName.endsWith(requiredSuffix)) {
				contextName = moduleName + requiredSuffix;
			}

			context.setSource("org.eclipse.jst.jee.server:" + contextName);

			String antiResourceLockingValue = context.getAttributeValue("antiResourceLocking");

			Boolean antiResourceLocking = Boolean.valueOf(antiResourceLockingValue);

			if (antiResourceLocking.booleanValue()) {
				context.setAttributeValue("antiResourceLocking", "false");
			}

			File contextFile = new File(contextDir, contextName + ".xml");

			if (!LiferayTomcatUtil.isExtProjectContext(context)) {

				// If requested, remove path attribute

				if (noPath) {
					context.removeAttribute("path");
				}

				// need to fix the doc base to contain entire path to help autoDeployer for Liferay

				context.setDocBase(deployDir.toOSString());

				DocumentBuilder builder = XMLUtil.getDocumentBuilder();

				Document contextDoc = builder.newDocument();

				contextDoc.appendChild(contextDoc.importNode(context.getElementNode(), true));

				XMLUtil.save(contextFile.getAbsolutePath(), contextDoc);
			}
		}
		catch (Exception e) {
			return new Status(
				IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0,
				NLS.bind(Messages.errorPublishConfiguration, new String[] {e.getLocalizedMessage()}), e);
		}
		finally {
		}

		return Status.OK_STATUS;
	}

	@Override
	public void redeployModule(IModule[] module) throws CoreException {
		setModulePublishState(module, IServer.PUBLISH_STATE_FULL);

		IAdaptable info = new IAdaptable() {

			@Override
			@SuppressWarnings("unchecked")
			public Object getAdapter(Class adapter) {
				if (String.class.equals(adapter)) {
					return "user";
				}

				return null;
			}

		};

		List<IModule[]> modules = new ArrayList<>();

		modules.add(module);

		_redeployModules = modules;

		try {
			publish(IServer.PUBLISH_FULL, modules, null, info);
		}
		catch (CoreException ce) {
			throw ce;
		}
		finally {
			_redeployModules = null;
		}
	}

	@Override
	public void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy workingCopy, IProgressMonitor monitor)
		throws CoreException {

		super.setupLaunchConfiguration(workingCopy, monitor);

		workingCopy.setAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING, "UTF-8");

		String existingVMArgs = workingCopy.getAttribute(
			IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, (String)null);

		if (null != existingVMArgs) {
			String[] parsedVMArgs = DebugPlugin.parseArguments(existingVMArgs);

			List<String> memoryArgs = new ArrayList<>();

			if (ListUtil.isNotEmpty(parsedVMArgs)) {
				for (String pArg : parsedVMArgs) {
					if (pArg.startsWith("-Xm") || pArg.startsWith("-XX:")) {
						memoryArgs.add(pArg);
					}
				}
			}

			String argsWithoutMem = mergeArguments(
				existingVMArgs, getRuntimeVMArguments(), memoryArgs.toArray(new String[0]), false);

			String fixedArgs = mergeArguments(argsWithoutMem, getRuntimeVMArguments(), null, false);

			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, fixedArgs);
		}
	}

	@Override
	protected MultiStatus executePublishers(
			int kind, List<IModule[]> modules, List<Integer> deltaKinds, IProgressMonitor monitor, IAdaptable info)
		throws CoreException {

		return super.executePublishers(
			kind, (_redeployModules == null) ? modules : _redeployModules, deltaKinds, monitor, info);
	}

	@Override
	protected void publishFinish(IProgressMonitor monitor) throws CoreException {
		super.publishFinish(monitor);

		_redeployModules = null;
	}

	@Override
	protected void publishModule(int kind, int deltaKind, IModule[] moduleTree, IProgressMonitor monitor)
		throws CoreException {

		boolean shouldPublishModule = LiferayPublishHelper.prePublishModule(
			this, kind, deltaKind, moduleTree, getPublishedResourceDelta(moduleTree), monitor);

		if (shouldPublishModule) {
			if (getServer().getServerState() != IServer.STATE_STOPPED) {
				if ((deltaKind == ServerBehaviourDelegate.ADDED) || (deltaKind == ServerBehaviourDelegate.REMOVED)) {
					setServerRestartState(true);
				}
			}

			setModulePublishState(moduleTree, IServer.PUBLISH_STATE_NONE);
		}
		else {

			// wasn't able to publish module, should set to needs full publish

			setModulePublishState(moduleTree, IServer.PUBLISH_STATE_FULL);
		}
	}

	@Override
	protected void publishModules(
		int kind, List modules, List deltaKind2, MultiStatus multi, IProgressMonitor monitor) {

		super.publishModules(kind, (_redeployModules == null) ? modules : _redeployModules, deltaKind2, multi, monitor);
	}

	private List<IModule[]> _redeployModules;

}