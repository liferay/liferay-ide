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

import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.server.core.IPluginPublisher;
import com.liferay.ide.eclipse.server.core.PortalServerCorePlugin;
import com.liferay.ide.eclipse.server.tomcat.core.util.PortalTomcatUtil;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatVersionHandler;
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
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.w3c.dom.Document;

/**
 * @author gregory.amerson@liferay.com
 */
@SuppressWarnings("restriction")
public class PortalTomcatServerBehavior extends TomcatServerBehaviour {

	public PortalTomcatServerBehavior() {
		super();
	}
	
	@Override
	protected void publishModule(int kind, int deltaKind, IModule[] moduleTree, IProgressMonitor monitor)
			throws CoreException {
		boolean shouldPublishModule = true;
		
		if (moduleTree != null && moduleTree.length > 0 && moduleTree[0].getProject() != null) {
			IProject project = moduleTree[0].getProject();

			IFacetedProject facetedProject = ProjectUtil.getFacetedProject(project);

			if (facetedProject != null) {
				IProjectFacet liferayFacet = ProjectUtil.getLiferayFacet(facetedProject);

				if (liferayFacet != null) {
					String facetId = liferayFacet.getId();

					IRuntime runtime = null;

					try {
						runtime = ServerUtil.getRuntime(project);
					}
					catch (CoreException ce) {
						// do nothing
					}

					if (runtime != null) {
						IPluginPublisher pluginPublisher =
							PortalServerCorePlugin.getPluginPublisher(facetId, runtime.getRuntimeType().getId());

						if (pluginPublisher != null) {
							try {
								shouldPublishModule =
									pluginPublisher.prePublishModule(this, kind, deltaKind, moduleTree, monitor);
							}
							catch (Exception e) {
								PortalTomcatPlugin.logError("Plugin deployer failed", e);
							}
						}
					}
				}
			}
		}

		if (shouldPublishModule) {
			try {
				super.publishModule(kind, deltaKind, moduleTree, monitor);
			} catch (CoreException ex) {
				//if we are deleting try to catch errors with locked jars
				if (deltaKind != ADDED) {
					if (ex.getStatus().isMultiStatus()) {
						boolean eatError = true;
						for (IStatus childStatus : ex.getStatus().getChildren()) {
							if (!(childStatus.getMessage().contains("Could not delete") && 
									childStatus.getMessage().contains("WEB-INF"+File.separator+"lib") &&
									childStatus.getMessage().contains(".jar"))) {
								eatError = false;
								break;
							}
						}
						if (eatError) {
							return;
						} else {
							throw ex;
						}
					}
				}
			}
		}
		
		setModulePublishState(moduleTree, IServer.PUBLISH_STATE_NONE);
	}
	
	@Override
	protected void publishFinish(IProgressMonitor monitor) throws CoreException {
		IStatus status;
		IPath baseDir = getRuntimeBaseDirectory();
		TomcatServer ts = getTomcatServer();
		ITomcatVersionHandler tvh = getTomcatVersionHandler();
		// Include or remove loader jar depending on state of serving directly 
		status = tvh.prepareForServingDirectly(baseDir, getTomcatServer());
		if (status.isOK()) {
			// If serving modules directly, update server.xml accordingly (includes project context.xmls)
			if (ts.isServeModulesWithoutPublish()) {
				status = getPortalTomcatConfiguration().updateContextsToServeDirectly(
						baseDir, tvh.getSharedLoader(baseDir), monitor);
			}
			// Else serving normally. Add project context.xmls to server.xml
			else {
				// Publish context configuration for servers that support META-INF/context.xml
				status = getPortalTomcatConfiguration().publishContextConfig(
						baseDir, getServerDeployDirectory(), monitor);
			}
			// Determine if context's path attribute should be removed
			String id = getServer().getServerType().getId();
			boolean noPath = id.indexOf("60") > 0;
			boolean serverStopped = getServer().getServerState() == IServer.STATE_STOPPED;
			if (status.isOK() && ts.isSaveSeparateContextFiles()) {
//				TomcatVersionHelper.moveContextsToSeparateFiles(baseDir, noPath, serverStopped, null);
				status = moveContextsToAutoDeployDir(baseDir, new Path(getPortalTomcatServer().getAutoDeployDirectory()), noPath, serverStopped);
			} else if (status.isOK()) {
				status = copyContextsToAutoDeployDir(baseDir, new Path(getPortalTomcatServer().getAutoDeployDirectory()), noPath, serverStopped);
			}
		}
		if (!status.isOK())
			throw new CoreException(status);
	}

	public PortalTomcatServer getPortalTomcatServer() {
		return (PortalTomcatServer) getServer().loadAdapter(PortalTomcatServer.class, null);
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
					Context existingContext = PortalTomcatUtil.loadContextFile(contextFile);
					// If server is stopped or if contexts are not the equivalent, write the context file
					if (!PortalTomcatUtil.isExtProjectContext(context) && (serverStopped || !context.isEquivalent(existingContext))) {
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
					Context existingContext = PortalTomcatUtil.loadContextFile(contextFile);
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
	
	public IPortalTomcatConfiguration getPortalTomcatConfiguration() throws CoreException {
		return getPortalTomcatServer().getPortalTomcatConfiguration();
	}

}
