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

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.server.util.ComponentUtil;
import com.liferay.ide.server.util.ServerUtil;

import java.io.File;
import java.io.FilenameFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.core.IJ2EEModule;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.jst.server.tomcat.core.internal.TomcatRuntime;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.TomcatVersionHelper;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.eclipse.wst.server.core.util.ModuleFile;
import org.eclipse.wst.server.core.util.PublishHelper;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class LiferayPublishOperation extends PublishOperation {

	public LiferayPublishOperation(LiferayTomcatServerBehavior server, int kind, IModule[] module, int deltaKind) {
		super(Msgs.publishServer, Msgs.publishWebModule);

		this.server = server;
		this.module = module;
		this.kind = kind;
		this.deltaKind = deltaKind;

		IPath base = server.getRuntimeBaseDirectory();

		if (base != null) {
			IPath temp = base.append("temp");

			_helper = new PublishHelper(temp.toFile());
		}
		else {

			/**
			 * We are doomed without a base directory. However, allow the catastrophe
			 * to occur elsewhere and hope for a useful error message.
			 */
			_helper = new PublishHelper(null);
		}
	}

	public void execute(IProgressMonitor monitor, IAdaptable info) throws CoreException {
		List<IStatus> status = new ArrayList<>();

		// If parent web module

		if (module.length == 1) {
			if (!ServerUtil.isExtProject(module[0].getProject())) {
				_publishDir(module[0], status, monitor);
			}
		}

		// Else a child module

		else {
			Properties p = server.loadModulePublishLocations();

			// Try to determine the URI for the child module

			IWebModule webModule = (IWebModule)module[0].loadAdapter(IWebModule.class, monitor);
			String childURI = null;

			if (webModule != null) {
				childURI = webModule.getURI(module[1]);
			}

			// Try to determine if child is binary

			IJ2EEModule childModule = (IJ2EEModule)module[1].loadAdapter(IJ2EEModule.class, monitor);
			boolean binary = false;

			if (childModule != null) {
				binary = childModule.isBinary();
			}

			if (binary) {
				_publishArchiveModule(childURI, p, status, monitor);
			}
			else {
				_publishJar(childURI, p, status, monitor);
			}

			server.saveModulePublishLocations(p);
		}

		throwException(status);
		server.setModulePublishState2(module, IServer.PUBLISH_STATE_NONE);
	}

	public int getKind() {
		return REQUIRED;
	}

	public int getOrder() {
		return 0;
	}

	protected static void addArrayToList(List<IStatus> list, IStatus[] a) {
		if ((list == null) || ListUtil.isEmpty(a)) {
			return;
		}

		int size = a.length;

		for (int i = 0; i < size; i++) {
			list.add(a[i]);
		}
	}

	protected static void throwException(List<IStatus> status) throws CoreException {
		if ((status == null) || status.isEmpty()) {
			return;
		}

		if (status.size() == 1) {
			IStatus status2 = status.get(0);

			throw new CoreException(status2);
		}

		IStatus[] children = new IStatus[status.size()];

		status.toArray(children);

		String message = Msgs.errorPublish;

		MultiStatus status2 = new MultiStatus(TomcatPlugin.PLUGIN_ID, 0, children, message, null);

		throw new CoreException(status2);
	}

	protected int deltaKind;
	protected int kind;
	protected IModule[] module;
	protected LiferayTomcatServerBehavior server;

	private void _clearWebXmlDescriptors(IProject project, IPath path, IProgressMonitor monitor) {

		// copy over web.xml so the liferay deployer doesn't copy web.xml filters incorrectly

		IModuleResource webXmlRes = _getWebXmlFile(project, path);

		if (webXmlRes != null) {
			_helper.publishToPath(new IModuleResource[] {webXmlRes}, path.append(_WEB_XML_PATH), monitor);
		}
		else {
			IPath webXmlPath = path.append(_WEB_XML_PATH);
			IPath liferayWebXmlPath = path.append(_LIFERAY_WEB_XML_PATH);

			File webXmlFile = webXmlPath.toFile();
			File liferayWebXmlFile = liferayWebXmlPath.toFile();

			if (FileUtil.exists(webXmlFile) && !webXmlFile.delete()) {
				ProjectUtil.createDefaultWebXml(webXmlFile, project.getName());
			}

			if (FileUtil.exists(liferayWebXmlFile) && !liferayWebXmlFile.delete()) {
				ProjectUtil.createDefaultWebXml(liferayWebXmlFile, project.getName());
			}
		}
	}

	private IModuleResource _getWebXmlFile(IProject project, IPath modelDeployDirectory) {

		// IDE-110 IDE-648

		IWebProject lrproject = LiferayCore.create(IWebProject.class, project);

		if (lrproject != null) {
			IFolder webappRoot = lrproject.getDefaultDocrootFolder();

			if (FileUtil.exists(webappRoot)) {
				IFile webXml = webappRoot.getFile(new Path(_WEB_XML_PATH));

				if (FileUtil.exists(webXml)) {
					return new ModuleFile(webXml, webXml.getName(), modelDeployDirectory.append(_WEB_XML_PATH));
				}
			}
		}

		return null;
	}

	private boolean _isHookProjectDelta(IModuleResourceDelta del) {
		IModuleResource moduleResource = del.getModuleResource();

		IResource resource = (IResource)moduleResource.getAdapter(IResource.class);

		if (resource != null) {
			return ProjectUtil.isHookProject(resource.getProject());
		}

		return false;
	}

	private void _publishArchiveModule(String jarURI, Properties p, List<IStatus> status, IProgressMonitor monitor) {
		IPath path = server.getModuleDeployDirectory(module[0]);
		boolean moving = false;

		// Get URI used for previous publish, if known

		String oldURI = (String)p.get(module[1].getId());

		if (oldURI != null) {

			// If old URI found, detect if jar is moving or changing its name

			if (jarURI != null) {
				moving = !oldURI.equals(jarURI);
			}
		}

		// If we don't have a jar URI, make a guess so we have one if we need it

		if (jarURI == null) {
			jarURI = "WEB-INF/lib/" + module[1].getName();
		}

		IPath jarPath = path.append(jarURI);

		// Make our best determination of the path to the old jar

		IPath oldJarPath = jarPath;

		if (oldURI != null) {
			oldJarPath = path.append(oldURI);
		}

		// Establish the destination directory

		path = jarPath.removeLastSegments(1);

		// Remove if requested or if previously published and are now serving without publishing

		TomcatServer tomcatServer = server.getTomcatServer();

		if (moving || (kind == IServer.PUBLISH_CLEAN) || (deltaKind == ServerBehaviourDelegate.REMOVED) ||
			tomcatServer.isServeModulesWithoutPublish()) {

			File file = oldJarPath.toFile();

			if (FileUtil.exists(file)) {
				file.delete();
			}

			p.remove(module[1].getId());

			if ((deltaKind == ServerBehaviourDelegate.REMOVED) || tomcatServer.isServeModulesWithoutPublish()) {
				return;
			}
		}

		if (!moving && (kind != IServer.PUBLISH_CLEAN) && (kind != IServer.PUBLISH_FULL)) {

			// avoid changes if no changes to module since last publish

			IModuleResourceDelta[] delta = server.getPublishedResourceDelta(module);

			if (ListUtil.isEmpty(delta)) {
				return;
			}
		}

		// make directory if it doesn't exist

		File file = path.toFile();

		if (FileUtil.notExists(file)) {
			file.mkdirs();
		}

		IModuleResource[] mr = server.getResources(module);

		IStatus[] stat = _helper.publishToPath(mr, jarPath, monitor);

		addArrayToList(status, stat);

		p.put(module[1].getId(), jarURI);
	}

	private void _publishDir(IModule module2, List<IStatus> status, IProgressMonitor monitor) throws CoreException {
		IPath path = server.getModuleDeployDirectory(module2);
		TomcatServer tomcatServer = server.getTomcatServer();
		LiferayTomcatServer liferayTomcatServer = server.getLiferayTomcatServer();

		/**
		 * Remove if requested or if previously published and are now serving
		 * without publishing
		 */
		if ((kind == IServer.PUBLISH_CLEAN) || (deltaKind == ServerBehaviourDelegate.REMOVED) ||
			tomcatServer.isServeModulesWithoutPublish()) {

			File f = path.toFile();

			if (FileUtil.exists(f)) {
				try {
					IPath baseDir = server.getRuntimeBaseDirectory();

					IPath conf = baseDir.append("conf");

					IPath serverXml = conf.append("server.xml");

					ServerInstance oldInstance = TomcatVersionHelper.getCatalinaServerInstance(serverXml, null, null);

					IPath contextDir = oldInstance.getContextXmlDirectory(baseDir.append("conf"));

					String contextFileName = path.lastSegment() + ".xml";

					IPath contextPath = contextDir.append(contextFileName);

					File contextFile = contextPath.toFile();

					if (FileUtil.exists(contextFile)) {
						contextFile.delete();
					}

					IPath autoDeployPath = baseDir.append(liferayTomcatServer.getAutoDeployDirectory());

					File autoDeployDir = autoDeployPath.toFile();

					File autoDeployFile = new File(autoDeployDir, contextFileName);

					if (FileUtil.exists(autoDeployFile)) {
						autoDeployFile.delete();
					}
				}
				catch (Exception e) {
					LiferayTomcatPlugin.logError("Could not delete context xml file.", e);
				}

				IStatus[] stat = PublishHelper.deleteDirectory(f, monitor);

				addArrayToList(status, stat);
			}

			if ((deltaKind == ServerBehaviourDelegate.REMOVED) || tomcatServer.isServeModulesWithoutPublish()) {
				return;
			}
		}

		IPath baseDir = tomcatServer.getRuntimeBaseDirectory();
		IPath autoDeployDir = new Path(liferayTomcatServer.getAutoDeployDirectory());

		boolean serverStopped = false;

		IServer iServer = server.getServer();

		if (iServer.getServerState() == IServer.STATE_STOPPED) {
			serverStopped = true;
		}

		if ((kind == IServer.PUBLISH_CLEAN) || (kind == IServer.PUBLISH_FULL)) {
			IModuleResource[] mr = server.getResources(module);

			IStatus[] stat = _helper.publishFull(mr, path, monitor);

			addArrayToList(status, stat);

			_clearWebXmlDescriptors(module2.getProject(), path, monitor);

			server.moveContextToAutoDeployDir(module2, path, baseDir, autoDeployDir, true, serverStopped);

			return;
		}

		IModuleResourceDelta[] delta = server.getPublishedResourceDelta(module);

		// check if we have a anti*Locking directory temp files and copy the resources out there as well

		File[] antiDirs = new File[0];

		try {
			TomcatRuntime tomcatRuntime = tomcatServer.getTomcatRuntime();

			IRuntime runtime = tomcatRuntime.getRuntime();

			IPath tomcatRuntimeLocation = runtime.getLocation();

			IPath temp = tomcatRuntimeLocation.append("temp");

			File tempDir = temp.toFile();

			antiDirs = tempDir.listFiles(
				new FilenameFilter() {

					public boolean accept(File dir, String name) {
						return name.endsWith(path.lastSegment());
					}

				});
		}
		catch (Exception e) {
		}

		int size = delta.length;

		for (int i = 0; i < size; i++) {
			IStatus[] stat = _helper.publishDelta(delta[i], path, monitor);

			for (File antiDir : antiDirs) {
				if (FileUtil.exists(antiDir)) {
					try {
						_helper.publishDelta(delta[i], new Path(antiDir.getCanonicalPath()), monitor);
					}
					catch (Exception e) {

						// best effort

					}
				}
			}

			addArrayToList(status, stat);
		}

		// check to see if we need to re-invoke the liferay plugin deployer

		String[] paths = {
			_WEB_XML_PATH, "WEB-INF/portlet.xml", "WEB-INF/liferay-portlet.xml", "WEB-INF/liferay-display.xml",
			"WEB-INF/liferay-look-and-feel.xml", "WEB-INF/liferay-hook.xml", "WEB-INF/liferay-layout-templates.xml",
			"WEB-INF/liferay-plugin-package.properties", "WEB-INF/liferay-plugin-package.xml",
			"WEB-INF/server-config.wsdd"
		};

		for (IModuleResourceDelta del : delta) {
			if (ComponentUtil.containsMember(del, paths) || _isHookProjectDelta(del)) {
				_clearWebXmlDescriptors(module2.getProject(), path, monitor);

				server.moveContextToAutoDeployDir(module2, path, baseDir, autoDeployDir, true, serverStopped);

				break;
			}
		}
	}

	private void _publishJar(String jarURI, Properties p, List<IStatus> status, IProgressMonitor monitor)
		throws CoreException {

		IPath path = server.getModuleDeployDirectory(module[0]);
		boolean moving = false;

		// Get URI used for previous publish, if known

		String oldURI = (String)p.get(module[1].getId());

		if (oldURI != null) {

			// If old URI found, detect if jar is moving or changing its name

			if (jarURI != null) {
				moving = !oldURI.equals(jarURI);
			}
		}

		// If we don't have a jar URI, make a guess so we have one if we need it

		if (jarURI == null) {
			jarURI = "WEB-INF/lib/" + module[1].getName() + ".jar";
		}

		IPath jarPath = path.append(jarURI);

		// Make our best determination of the path to the old jar

		IPath oldJarPath = jarPath;

		if (oldURI != null) {
			oldJarPath = path.append(oldURI);
		}

		// Establish the destination directory

		path = jarPath.removeLastSegments(1);

		// Remove if requested or if previously published and are now serving without publishing

		TomcatServer tomcatServer = server.getTomcatServer();

		if (moving || (kind == IServer.PUBLISH_CLEAN) || (deltaKind == ServerBehaviourDelegate.REMOVED) ||
			tomcatServer.isServeModulesWithoutPublish()) {

			File file = oldJarPath.toFile();

			if (FileUtil.exists(file)) {
				file.delete();
			}

			p.remove(module[1].getId());

			if ((deltaKind == ServerBehaviourDelegate.REMOVED) || tomcatServer.isServeModulesWithoutPublish()) {
				return;
			}
		}

		if (!moving && (kind != IServer.PUBLISH_CLEAN) && (kind != IServer.PUBLISH_FULL)) {

			// avoid changes if no changes to module since last publish

			IModuleResourceDelta[] delta = server.getPublishedResourceDelta(module);

			if (ListUtil.isEmpty(delta)) {
				return;
			}
		}

		// make directory if it doesn't exist

		File file = path.toFile();

		if (FileUtil.notExists(file)) {
			file.mkdirs();
		}

		IModuleResource[] mr = server.getResources(module);

		IStatus[] stat = _helper.publishZip(mr, jarPath, monitor);

		addArrayToList(status, stat);

		p.put(module[1].getId(), jarURI);
	}

	private static final String _LIFERAY_WEB_XML_PATH = "WEB-INF/liferay-web.xml";

	private static final String _WEB_XML_PATH = "WEB-INF/web.xml";

	private PublishHelper _helper;

	private static class Msgs extends NLS {

		public static String errorPublish;
		public static String publishServer;
		public static String publishWebModule;

		static {
			initializeMessages(LiferayPublishOperation.class.getName(), Msgs.class);
		}

	}

}