/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
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
import org.eclipse.jst.server.tomcat.core.internal.TomcatVersionHelper;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.PublishOperation;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.eclipse.wst.server.core.util.ModuleFile;
import org.eclipse.wst.server.core.util.PublishHelper;
/**
 * Tomcat publish helper.
 */
@SuppressWarnings( "restriction" )
public class LiferayPublishOperation extends PublishOperation {

	private static final String LIFERAY_WEB_XML_PATH = "WEB-INF/liferay-web.xml"; //$NON-NLS-1$

	private static final String WEB_XML_PATH = "WEB-INF/web.xml"; //$NON-NLS-1$

	protected LiferayTomcatServerBehavior server;
	protected IModule[] module;
	protected int kind;
	protected int deltaKind;
	private PublishHelper helper;

	/**
	 * Construct the operation object to publish the specified module
	 * to the specified server.
	 *
	 * @param server server to which the module will be published
	 * @param kind kind of publish
	 * @param module module to publish
	 * @param deltaKind kind of change
	 */
	public LiferayPublishOperation(LiferayTomcatServerBehavior server, int kind, IModule[] module, int deltaKind) {
		super(Msgs.publishServer, Msgs.publishWebModule);
		this.server = server;
		this.module = module;
		this.kind = kind;
		this.deltaKind = deltaKind;
		IPath base = server.getRuntimeBaseDirectory();
		if (base != null) {
			helper = new PublishHelper(base.append("temp").toFile()); //$NON-NLS-1$
		}
		else {
			// We are doomed without a base directory.  However, allow the catastrophe
			// to occur elsewhere and hope for a useful error message.
			helper = new PublishHelper(null);
		}
	}

	/**
	 * @see PublishOperation#getOrder()
	 */
	public int getOrder() {
		return 0;
	}

	/**
	 * @see PublishOperation#getKind()
	 */
	public int getKind() {
		return REQUIRED;
	}

	/**
	 * @see PublishOperation#execute(IProgressMonitor, IAdaptable)
	 */
	public void execute(IProgressMonitor monitor, IAdaptable info) throws CoreException {
		List status = new ArrayList();

		// If parent web module
		if (module.length == 1) {
			if ( !ServerUtil.isExtProject( module[0].getProject() ) )
			{
				publishDir(module[0], status, monitor);
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
			boolean isBinary = false;
			if (childModule != null) {
				isBinary = childModule.isBinary();
			}

			if (isBinary) {
				publishArchiveModule(childURI, p, status, monitor);
			}
			else {
				publishJar(childURI, p, status, monitor);
			}
			server.saveModulePublishLocations(p);
		}
		throwException(status);
		server.setModulePublishState2(module, IServer.PUBLISH_STATE_NONE);
	}

	private void publishDir(IModule module2, List status, IProgressMonitor monitor) throws CoreException {
		final IPath path = server.getModuleDeployDirectory(module2);

		// Remove if requested or if previously published and are now serving without publishing
		if (kind == IServer.PUBLISH_CLEAN || deltaKind == ServerBehaviourDelegate.REMOVED
				|| server.getTomcatServer().isServeModulesWithoutPublish()) {
			File f = path.toFile();
			if (f.exists()) {
				try {
					IPath baseDir = server.getRuntimeBaseDirectory();
					IPath serverXml = baseDir.append("conf").append("server.xml"); //$NON-NLS-1$ //$NON-NLS-2$
					ServerInstance oldInstance = TomcatVersionHelper.getCatalinaServerInstance(serverXml, null, null);
					IPath contextDir = oldInstance.getContextXmlDirectory(baseDir.append("conf")); //$NON-NLS-1$
					String contextFileName = path.lastSegment() + ".xml"; //$NON-NLS-1$
					File contextFile = contextDir.append(contextFileName).toFile();

					if (contextFile.exists()) {
						contextFile.delete();
					}

					File autoDeployDir =
						baseDir.append(server.getLiferayTomcatServer().getAutoDeployDirectory()).toFile();
					File autoDeployFile = new File(autoDeployDir, contextFileName);

					if (autoDeployFile.exists()) {
						autoDeployFile.delete();
					}
				}
				catch (Exception e) {
					LiferayTomcatPlugin.logError("Could not delete context xml file.", e); //$NON-NLS-1$
				}

				IStatus[] stat = PublishHelper.deleteDirectory(f, monitor);
				addArrayToList(status, stat);
			}

			if (deltaKind == ServerBehaviourDelegate.REMOVED
					|| server.getTomcatServer().isServeModulesWithoutPublish())
				return;
		}

		IPath baseDir = server.getTomcatServer().getRuntimeBaseDirectory();
		IPath autoDeployDir = new Path(server.getLiferayTomcatServer().getAutoDeployDirectory());
		boolean serverStopped = server.getServer().getServerState() == IServer.STATE_STOPPED;

		if (kind == IServer.PUBLISH_CLEAN || kind == IServer.PUBLISH_FULL) {
			IModuleResource[] mr = server.getResources(module);
			IStatus[] stat = helper.publishFull(mr, path, monitor);
			addArrayToList(status, stat);

			clearWebXmlDescriptors(module2.getProject(), path, monitor);

			server.moveContextToAutoDeployDir(module2, path, baseDir, autoDeployDir, true, serverStopped);

			return;
		}

		IModuleResourceDelta[] delta = server.getPublishedResourceDelta(module);

		// check if we have a anti*Locking directory temp files and copy the resources out there as well
        File[] antiDirs = new File[0];
		try
        {
            File tempDir =
                server.getLiferayTomcatServer().getTomcatRuntime().getRuntime().getLocation().append( "temp" ).toFile(); //$NON-NLS-1$
            antiDirs = tempDir.listFiles
            (
                new FilenameFilter()
                {
                    public boolean accept( File dir, String name )
                    {
                        return name.endsWith( path.lastSegment() );
                    }
                }
            );
        }
        catch( Exception e )
        {
        }


		int size = delta.length;
		for (int i = 0; i < size; i++) {
			IStatus[] stat = helper.publishDelta(delta[i], path, monitor);

			for( File antiDir : antiDirs )
			{
			    if( antiDir.exists() )
			    {
			        try
	                {
	                    helper.publishDelta(delta[i], new Path( antiDir.getCanonicalPath() ), monitor);
	                }
	                catch( Exception e)
	                {
	                    // best effort
	                }
			    }
			}

			addArrayToList(status, stat);
		}

		// check to see if we need to re-invoke the liferay plugin deployer
		String[] paths =
			new String[] { WEB_XML_PATH, "WEB-INF/portlet.xml", "WEB-INF/liferay-portlet.xml", //$NON-NLS-1$ //$NON-NLS-2$
				"WEB-INF/liferay-display.xml", "WEB-INF/liferay-look-and-feel.xml", "WEB-INF/liferay-hook.xml",  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
				"WEB-INF/liferay-layout-templates.xml", "WEB-INF/liferay-plugin-package.properties",  //$NON-NLS-1$//$NON-NLS-2$
				"WEB-INF/liferay-plugin-package.xml", "WEB-INF/server-config.wsdd", }; //$NON-NLS-1$ //$NON-NLS-2$

		for ( IModuleResourceDelta del : delta )
		{
			if ( ComponentUtil.containsMember( del, paths ) || isHookProjectDelta( del ) )
			{
				clearWebXmlDescriptors(module2.getProject(), path, monitor);

				server.moveContextToAutoDeployDir( module2, path, baseDir, autoDeployDir, true, serverStopped );
				break;
			}
		}

	}

	private void clearWebXmlDescriptors(IProject project, IPath path, IProgressMonitor monitor)
    {
	 // copy over web.xml so the liferay deployer doesn't copy web.xml filters incorrectly
        IModuleResource webXmlRes = getWebXmlFile( project, path );

        if ( webXmlRes != null )
        {
            helper.publishToPath(
                new IModuleResource[] { webXmlRes }, path.append( WEB_XML_PATH ), monitor );
        }
        else
        {
            File webXmlFile = path.append( WEB_XML_PATH ).toFile();
            File liferayWebXmlFile = path.append( LIFERAY_WEB_XML_PATH ).toFile();

            if ( webXmlFile.exists() )
            {
                if ( !webXmlFile.delete() )
                {
                    ProjectUtil.createDefaultWebXml( webXmlFile, project.getName() );
                }
            }

            if ( liferayWebXmlFile.exists() )
            {
                if ( !liferayWebXmlFile.delete() )
                {
                    ProjectUtil.createDefaultWebXml( liferayWebXmlFile, project.getName() );
                }
            }
        }
    }

    private boolean isHookProjectDelta( IModuleResourceDelta del )
    {
        final IResource resource = (IResource) del.getModuleResource().getAdapter( IResource.class );

        if( resource != null )
        {
            return ProjectUtil.isHookProject( resource.getProject() );
        }

        return false;
    }

	private IModuleResource getWebXmlFile( IProject project, IPath modelDeployDirectory )
	{
	    // IDE-110 IDE-648
	    final IWebProject lrproject = LiferayCore.create( IWebProject.class, project );

        if( lrproject != null )
        {
            final IFolder webappRoot = lrproject.getDefaultDocrootFolder();

            if( webappRoot != null && webappRoot.exists() )
            {
                IFile webXml = webappRoot.getFile( new Path( WEB_XML_PATH ) );

                if( webXml.exists() )
                {
                    return new ModuleFile( webXml, webXml.getName(), modelDeployDirectory.append( WEB_XML_PATH ) );
                }
            }
        }

		return null;
	}

	private void publishJar(String jarURI, Properties p, List status, IProgressMonitor monitor) throws CoreException {
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
			jarURI = "WEB-INF/lib/" + module[1].getName() + ".jar";  //$NON-NLS-1$//$NON-NLS-2$
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
		if (moving || kind == IServer.PUBLISH_CLEAN || deltaKind == ServerBehaviourDelegate.REMOVED
				|| server.getTomcatServer().isServeModulesWithoutPublish()) {
			File file = oldJarPath.toFile();
			if (file.exists())
				file.delete();
			p.remove(module[1].getId());

			if (deltaKind == ServerBehaviourDelegate.REMOVED
					|| server.getTomcatServer().isServeModulesWithoutPublish())
				return;
		}
		if (!moving && kind != IServer.PUBLISH_CLEAN && kind != IServer.PUBLISH_FULL) {
			// avoid changes if no changes to module since last publish
			IModuleResourceDelta[] delta = server.getPublishedResourceDelta(module);

			if (ListUtil.isEmpty(delta)) {
				return;
			}
		}

		// make directory if it doesn't exist
		if (!path.toFile().exists())
			path.toFile().mkdirs();

		IModuleResource[] mr = server.getResources(module);
		IStatus[] stat = helper.publishZip(mr, jarPath, monitor);
		addArrayToList(status, stat);
		p.put(module[1].getId(), jarURI);
	}

	private void publishArchiveModule(String jarURI, Properties p, List status, IProgressMonitor monitor) {
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
			jarURI = "WEB-INF/lib/" + module[1].getName(); //$NON-NLS-1$
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
		if (moving || kind == IServer.PUBLISH_CLEAN || deltaKind == ServerBehaviourDelegate.REMOVED
				|| server.getTomcatServer().isServeModulesWithoutPublish()) {
			File file = oldJarPath.toFile();
			if (file.exists()) {
				file.delete();
			}
			p.remove(module[1].getId());

			if (deltaKind == ServerBehaviourDelegate.REMOVED
					|| server.getTomcatServer().isServeModulesWithoutPublish())
				return;
		}
		if (!moving && kind != IServer.PUBLISH_CLEAN && kind != IServer.PUBLISH_FULL) {
			// avoid changes if no changes to module since last publish
			IModuleResourceDelta[] delta = server.getPublishedResourceDelta(module);
			if (ListUtil.isEmpty(delta))
				return;
		}

		// make directory if it doesn't exist
		if (!path.toFile().exists())
			path.toFile().mkdirs();

		IModuleResource[] mr = server.getResources(module);
		IStatus[] stat = helper.publishToPath(mr, jarPath, monitor);
		addArrayToList(status, stat);
		p.put(module[1].getId(), jarURI);
	}

	/**
	 * Utility method to throw a CoreException based on the contents of a list of
	 * error and warning status.
	 *
	 * @param status a List containing error and warning IStatus
	 * @throws CoreException
	 */
	protected static void throwException(List status) throws CoreException {
		if (status == null || status.size() == 0)
			return;

		if (status.size() == 1) {
			IStatus status2 = (IStatus) status.get(0);
			throw new CoreException(status2);
		}
		IStatus[] children = new IStatus[status.size()];
		status.toArray(children);
		String message = Msgs.errorPublish;
		MultiStatus status2 = new MultiStatus(TomcatPlugin.PLUGIN_ID, 0, children, message, null);
		throw new CoreException(status2);
	}

	protected static void addArrayToList(List list, IStatus[] a) {
		if (list == null || ListUtil.isEmpty(a)) {
			return;
		}

		int size = a.length;

		for (int i = 0; i < size; i++) {
			list.add(a[i]);
		}
	}

    private static class Msgs extends NLS
    {
        public static String errorPublish;
        public static String publishServer;
        public static String publishWebModule;

        static
        {
            initializeMessages( LiferayPublishOperation.class.getName(), Msgs.class );
        }
    }
}
