/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *    Greg Amerson <gregory.amerson@liferay.com>
 *******************************************************************************/
package com.liferay.ide.eclipse.server.tomcat.core;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.core.util.FileUtil;
import com.liferay.ide.eclipse.server.core.ILiferayServerBehavior;
import com.liferay.ide.eclipse.server.tomcat.core.util.LiferayPublishHelper;
import com.liferay.ide.eclipse.server.tomcat.core.util.LiferayTomcatUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jst.server.tomcat.core.internal.Messages;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServerBehaviour;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.XMLUtil;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Host;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Server;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.w3c.dom.Document;

/**
 * @author gregory.amerson@liferay.com
 */
@SuppressWarnings("restriction")
public class LiferayTomcatServerBehavior extends TomcatServerBehaviour implements ILiferayServerBehavior {

	public LiferayTomcatServerBehavior() {
		super();
	}
	
	@Override
	protected void publishModule(int kind, int deltaKind, IModule[] moduleTree, IProgressMonitor monitor)
			throws CoreException {
		
		boolean shouldPublishModule =
			LiferayPublishHelper.prePublishModule(
				this, kind, deltaKind, moduleTree, getPublishedResourceDelta(moduleTree), monitor);

		if (shouldPublishModule) {
			if (getServer().getServerState() != IServer.STATE_STOPPED) {
				if (deltaKind == ServerBehaviourDelegate.ADDED || deltaKind == ServerBehaviourDelegate.REMOVED)
					setServerRestartState(true);
				}

			setModulePublishState(moduleTree, IServer.PUBLISH_STATE_NONE);
		}
		else {
			// wasn't able to publish module, should set to needs full publish
			setModulePublishState(moduleTree, IServer.PUBLISH_STATE_FULL);
		}
	}
	
	@Override
	protected void publishFinish(IProgressMonitor monitor) throws CoreException {
		IStatus status = Status.OK_STATUS;
		IPath baseDir = getRuntimeBaseDirectory();
		TomcatServer ts = getTomcatServer();
		// ITomcatVersionHandler tvh = getTomcatVersionHandler();
		// Include or remove loader jar depending on state of serving directly 

		// TODO uncomment following line once we have serve directly enabled
		// status = tvh.prepareForServingDirectly(baseDir, getTomcatServer());

		if (status.isOK()) {
			// If serving modules directly, update server.xml accordingly (includes project context.xmls)
			if (ts.isServeModulesWithoutPublish()) {
				// TODO uncomment the following once we have serve direct enabled
				// status = getLiferayTomcatConfiguration().updateContextsToServeDirectly(
				// baseDir, tvh.getSharedLoader(baseDir), monitor);
			}
			// Else serving normally. Add project context.xmls to server.xml
			else {
				// Publish context configuration for servers that support META-INF/context.xml
				status =
					getLiferayTomcatConfiguration().publishContextConfig(
						baseDir, getServerDeployDirectory(), monitor);
			}
			// Determine if context's path attribute should be removed
			String id = getServer().getServerType().getId();
			boolean noPath = id.indexOf("60") > 0;
			boolean serverStopped = getServer().getServerState() == IServer.STATE_STOPPED;
			if (status.isOK() && ts.isSaveSeparateContextFiles()) {
//				TomcatVersionHelper.moveContextsToSeparateFiles(baseDir, noPath, serverStopped, null);
				status = moveContextsToAutoDeployDir(baseDir, new Path(getLiferayTomcatServer().getAutoDeployDirectory()), noPath, serverStopped);
			} else if (status.isOK()) {
				status = copyContextsToAutoDeployDir(baseDir, new Path(getLiferayTomcatServer().getAutoDeployDirectory()), noPath, serverStopped);
			}
		}
		if (!status.isOK())
			throw new CoreException(status);
	}

	public LiferayTomcatServer getLiferayTomcatServer() {
		return (LiferayTomcatServer) getServer().loadAdapter(LiferayTomcatServer.class, null);
	}
	
	public IModuleResourceDelta[] getPublishedResourceDelta(IModule[] module) {
		return super.getPublishedResourceDelta(module);
	}

	public IModuleResource[] getResources(IModule[] module) {
		return super.getResources(module);
	}

	public IStatus moveContextsToAutoDeployDir(IPath baseDir, IPath autoDeployDir, boolean noPath, boolean serverStopped) {
		IPath confDir = baseDir.append("conf");
		IPath serverXml = confDir.append("server.xml");
		try {
			
			Factory factory = new Factory();
			factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
			Server publishedServer = (Server) factory.loadDocument(new FileInputStream(serverXml.toFile()));
			ServerInstance publishedInstance = new ServerInstance(publishedServer, null, null);
			
			boolean modified = false;

			Host host = publishedInstance.getHost();
			Context[] wtpContexts = publishedInstance.getContexts();
			if (wtpContexts != null && wtpContexts.length > 0) {
				IPath tomcatContextPath = publishedInstance.getContextXmlDirectory(serverXml.removeLastSegments(1));
				IPath contextPath = null;
				if (autoDeployDir.isAbsolute()) {
					contextPath = autoDeployDir;
				} else {
					contextPath = baseDir.append(autoDeployDir);
				}
				File contextDir = contextPath.toFile();
				if (!contextDir.exists()) {
					contextDir.mkdirs();
				}
				// Process in reverse order, since contexts may be removed
				for (int i = wtpContexts.length - 1; i >= 0; i--) {
					Context context = wtpContexts[i];
					// TODO Handle non-project contexts when their removal can be addressed
					if (context.getSource() == null)
						continue;
					
					String name = context.getPath();
					if (name.startsWith("/")) {
						name = name.substring(1);
					}
					// If the default context, adjust the file name
					if (name.length() == 0) {
						name = "ROOT";
					}
					
					// TODO Determine circumstances, if any, where setting antiResourceLocking true can cause the original docBase content to be deleted.
					if (Boolean.valueOf(context.getAttributeValue("antiResourceLocking")).booleanValue())
						context.setAttributeValue("antiResourceLocking", "false");
					
					File contextFile = new File(contextDir, name + ".xml");
					File tomcatContextFile = new File(tomcatContextPath.toFile(), name + ".xml");
					Context existingContext = LiferayTomcatUtil.loadContextFile(contextFile);
					// If server is stopped or if contexts are not the equivalent, write the context file
					if (!LiferayTomcatUtil.isExtProjectContext(context) && (serverStopped || !context.isEquivalent(existingContext))) {
						// If requested, remove path attribute
						if (noPath)
							context.removeAttribute("path");
						
						//need to fix the doc base to contain entire path to help autoDeployer for Liferay
						if (!(new Path(context.getDocBase()).isAbsolute())) {
							context.setDocBase(getServerDeployDirectory().append(new Path(context.getDocBase())).toOSString());
						}
						
						if (!tomcatContextFile.exists()) {
							DocumentBuilder builder = XMLUtil.getDocumentBuilder();
							Document contextDoc = builder.newDocument();
							contextDoc.appendChild(contextDoc.importNode(context.getElementNode(), true));
							XMLUtil.save(contextFile.getAbsolutePath(), contextDoc);
						}
					}

					host.removeElement("Context", i);

					if (Boolean.parseBoolean(host.getAttributeValue("deployXML"))) {
						host.setAttributeValue("deployXML", "false");
					}

					modified = true;
				}
			}
//			monitor.worked(100);
			if (modified) {
//				monitor.subTask(Messages.savingContextConfigTask);
				factory.save(serverXml.toOSString());
			}
//			monitor.worked(100);
//			if (Trace.isTraceEnabled())
//				Trace.trace(Trace.FINER, "Context docBase settings updated in server.xml.");
		} catch (Exception e) {
//			Trace.trace(Trace.SEVERE, "Could not modify context configurations to serve directly for Tomcat configuration " + confDir.toOSString() + ": " + e.getMessage());
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorPublishConfiguration, new String[] {e.getLocalizedMessage()}), e);
		}
		finally {
//			monitor.done();
		}
		return Status.OK_STATUS;
	}
	
	protected IStatus copyContextsToAutoDeployDir(IPath baseDir, IPath autoDeployDir, boolean noPath, boolean serverStopped) {
		IPath confDir = baseDir.append("conf");
		IPath serverXml = confDir.append("server.xml");
		try {
			
			Factory factory = new Factory();
			factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
			Server publishedServer = (Server) factory.loadDocument(new FileInputStream(serverXml.toFile()));
			ServerInstance publishedInstance = new ServerInstance(publishedServer, null, null);
			
			Context[] wtpContexts = publishedInstance.getContexts();
			if (wtpContexts != null && wtpContexts.length > 0) {
//				IPath contextPath = publishedInstance.getContextXmlDirectory(serverXml.removeLastSegments(1));
				IPath contextPath = null;
				if (autoDeployDir.isAbsolute()) {
					contextPath = autoDeployDir;
				} else {
					contextPath = baseDir.append(autoDeployDir);
				}
				File contextDir = contextPath.toFile();
				if (!contextDir.exists()) {
					contextDir.mkdirs();
				}
				// Process in reverse order, since contexts may be removed
				for (int i = wtpContexts.length - 1; i >= 0; i--) {
					Context context = wtpContexts[i];
					// TODO Handle non-project contexts when their removal can be addressed
					if (context.getSource() == null)
						continue;
					
					String name = context.getPath();
					if (name.startsWith("/")) {
						name = name.substring(1);
					}
					// If the default context, adjust the file name
					if (name.length() == 0) {
						name = "ROOT";
					}
					
					// TODO Determine circumstances, if any, where setting antiResourceLocking true can cause the original docBase content to be deleted.
					if (Boolean.valueOf(context.getAttributeValue("antiResourceLocking")).booleanValue())
						context.setAttributeValue("antiResourceLocking", "false");
					
					File contextFile = new File(contextDir, name + ".xml");
					Context existingContext = LiferayTomcatUtil.loadContextFile(contextFile);
					// If server is stopped or if contexts are not the equivalent, write the context file
					if (serverStopped || !context.isEquivalent(existingContext)) {
						// If requested, remove path attribute
						if (noPath)
							context.removeAttribute("path");
						
						//need to fix the doc base to contain entire path to help autoDeployer for Liferay
						if (!(new Path(context.getDocBase()).isAbsolute())) {
							context.setDocBase(getServerDeployDirectory().append(new Path(context.getDocBase())).toOSString());
						}
						
						DocumentBuilder builder = XMLUtil.getDocumentBuilder();
						Document contextDoc = builder.newDocument();
						contextDoc.appendChild(contextDoc.importNode(context.getElementNode(), true));
						XMLUtil.save(contextFile.getAbsolutePath(), contextDoc);
					}
				}
			}
		} catch (Exception e) {
//			Trace.trace(Trace.SEVERE, "Could not modify context configurations to serve directly for Tomcat configuration " + confDir.toOSString() + ": " + e.getMessage());
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorPublishConfiguration, new String[] {e.getLocalizedMessage()}), e);
		}
		finally {
//			monitor.done();
		}
		return Status.OK_STATUS;
	}	
	
	public ILiferayTomcatConfiguration getLiferayTomcatConfiguration() throws CoreException {
		return getLiferayTomcatServer().getLiferayTomcatConfiguration();
	}

	@Override
	public void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy workingCopy, IProgressMonitor monitor)
		throws CoreException {

		super.setupLaunchConfiguration(workingCopy, monitor);

		workingCopy.setAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING, "UTF-8");

		String existingVMArgs =
			workingCopy.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, (String) null);

		if (null != existingVMArgs) {
			String[] parsedVMArgs = DebugPlugin.parseArguments(existingVMArgs);

			List<String> memoryArgs = new ArrayList<String>();

			if (!CoreUtil.isNullOrEmpty(parsedVMArgs)) {
				for (String pArg : parsedVMArgs) {
					if (pArg.startsWith("-Xm") || pArg.startsWith("-XX:")) {
						memoryArgs.add(pArg);
					}
				}
			}

			String argsWithoutMem =
				mergeArguments(existingVMArgs, getRuntimeVMArguments(), memoryArgs.toArray(new String[0]), false);
			String fixedArgs = mergeArguments(argsWithoutMem, getRuntimeVMArguments(), null, false);

			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, fixedArgs);
		}
	}

	public void redeployContext(String contextName) {
		IPath baseDir = getRuntimeBaseDirectory();
		IPath confDir = baseDir.append("conf");
		IPath contextPath = confDir.append("Catalina/localhost/" + contextName + ".xml");

		if (contextPath.toFile().exists()) {
			IPath autoDeployDir = new Path(getLiferayTomcatServer().getAutoDeployDirectory());
			if (!autoDeployDir.isAbsolute()) {
				autoDeployDir = baseDir.append(autoDeployDir);
			}
			FileUtil.copyFileToDir(contextPath.toFile(), autoDeployDir.toFile());
		}

	}

	public void redeployModule(IModule[] module) {
		IServerWorkingCopy wc = getServer().createWorkingCopy();
		try {
			wc.modifyModules(null, module, null);
			wc.save(true, null);

			IAdaptable info = new IAdaptable() {

				@SuppressWarnings("rawtypes")
				public Object getAdapter(Class adapter) {
					if (String.class.equals(adapter))
						return "user";
					return null;
				}
			};

			getServer().publish(IServer.PUBLISH_FULL, null, info, null);

			wc = getServer().createWorkingCopy();
			wc.modifyModules(module, null, null);
			wc.save(true, null);

			getServer().publish(IServer.PUBLISH_FULL, null, info, null);
		}
		catch (Exception e) {
			LiferayTomcatPlugin.logError(e);
		}

		// setModulePublishState(module, IServer.PUBLISH_STATE_FULL);
	
	}

}
