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

package com.liferay.ide.server.tomcat.core;

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
 * @author gregory.amerson@liferay.com
 * @author Simon Jiang
 */
@SuppressWarnings( { "restriction", "rawtypes" } )
public class LiferayTomcatServerBehavior extends TomcatServerBehaviour implements ILiferayServerBehavior
{

    private List<IModule[]> redeployModules;

    public LiferayTomcatServerBehavior()
    {
        super();
    }

    @Override
    protected MultiStatus executePublishers(
        int kind, List<IModule[]> modules, List<Integer> deltaKinds, IProgressMonitor monitor, IAdaptable info )
        throws CoreException
    {
        return super.executePublishers(
            kind, ( redeployModules == null ) ? modules : redeployModules, deltaKinds, monitor, info );
    }

    @Override
	public IPath getDeployedPath( IModule[] module )
    {
        return getModuleDeployDirectory( module[0] );
    }

    @Override
    public IPath getModuleDeployDirectory( IModule module )
    {
        final IPath defaultPath = super.getModuleDeployDirectory( module );

        IPath updatedPath = null;

        if( defaultPath != null && defaultPath.lastSegment() != null )
        {
            final IProject project = module.getProject();
            final String requiredSuffix = ProjectUtil.getRequiredSuffix( project );

            if( requiredSuffix != null && ! defaultPath.lastSegment().endsWith( requiredSuffix ) )
            {
                String lastSegment = defaultPath.lastSegment();
                updatedPath = defaultPath.removeLastSegments( 1 ).append( lastSegment + requiredSuffix );
            }
        }

        return updatedPath == null ? defaultPath : updatedPath;
    }

    public LiferayTomcatServer getLiferayTomcatServer()
    {
        return (LiferayTomcatServer) getServer().loadAdapter( LiferayTomcatServer.class, null );
    }

    @Override
	public IModuleResourceDelta[] getPublishedResourceDelta( IModule[] module )
    {
        return super.getPublishedResourceDelta( module );
    }

    @Override
	public IModuleResource[] getResources( IModule[] module )
    {
        return super.getResources( module );
    }

    public List<IModule[]> getRedeployModules()
    {
        return redeployModules;
    }

    @Override
    public String[] getRuntimeVMArguments()
    {
        return super.getRuntimeVMArguments();
    }

    public IStatus moveContextToAutoDeployDir(
        IModule module, IPath deployDir, IPath baseDir, IPath autoDeployDir, boolean noPath, boolean serverStopped )
    {
        IPath confDir = baseDir.append( "conf" ); //$NON-NLS-1$
        IPath serverXml = confDir.append( "server.xml" ); //$NON-NLS-1$

        try( InputStream newInputStream = Files.newInputStream( serverXml.toFile().toPath() ))
        {
            Factory factory = new Factory();
            factory.setPackageName( "org.eclipse.jst.server.tomcat.core.internal.xml.server40" ); //$NON-NLS-1$

            Server publishedServer = (Server) factory.loadDocument( newInputStream );
            ServerInstance publishedInstance = new ServerInstance( publishedServer, null, null );

            IPath contextPath = null;

            if( autoDeployDir.isAbsolute() )
            {
                contextPath = autoDeployDir;
            }
            else
            {
                contextPath = baseDir.append( autoDeployDir );
            }

            File contextDir = contextPath.toFile();

            if( !contextDir.exists() )
            {
                contextDir.mkdirs();
            }

            Context context = publishedInstance.createContext( -1 );
            context.setReloadable( "true" ); //$NON-NLS-1$

            final String moduleName = module.getName();
            final String requiredSuffix = ProjectUtil.getRequiredSuffix( module.getProject() );

            String contextName = moduleName;

            if( ! moduleName.endsWith( requiredSuffix ) )
            {
                contextName = moduleName + requiredSuffix;
            }

            context.setSource( "org.eclipse.jst.jee.server:" + contextName ); //$NON-NLS-1$

            if( Boolean.valueOf( context.getAttributeValue( "antiResourceLocking" ) ).booleanValue() ) //$NON-NLS-1$
            {
                context.setAttributeValue( "antiResourceLocking", "false" ); //$NON-NLS-1$ //$NON-NLS-2$
            }

            File contextFile = new File( contextDir, contextName + ".xml" ); //$NON-NLS-1$

            if( !LiferayTomcatUtil.isExtProjectContext( context ) )
            {
                // If requested, remove path attribute
                if( noPath )
                {
                    context.removeAttribute( "path" ); //$NON-NLS-1$
                }

                // need to fix the doc base to contain entire path to help autoDeployer for Liferay
                context.setDocBase( deployDir.toOSString() );
                // context.setAttributeValue("antiJARLocking", "true");

                // check to see if we need to move from conf folder
                // IPath existingContextPath = confDir.append("Catalina/localhost").append(contextFile.getName());
                // if (existingContextPath.toFile().exists()) {
                // existingContextPath.toFile().delete();
                // }

                DocumentBuilder builder = XMLUtil.getDocumentBuilder();
                Document contextDoc = builder.newDocument();
                contextDoc.appendChild( contextDoc.importNode( context.getElementNode(), true ) );
                XMLUtil.save( contextFile.getAbsolutePath(), contextDoc );
            }
        }
        catch( Exception e )
        {
            // Trace.trace(Trace.SEVERE,
            // "Could not modify context configurations to serve directly for Tomcat configuration " +
            // confDir.toOSString() + ": " + e.getMessage());
            return new Status( IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(
                Messages.errorPublishConfiguration, new String[] { e.getLocalizedMessage() } ), e );
        }
        finally
        {
            // monitor.done();
        }

        return Status.OK_STATUS;
    }

    public ILiferayTomcatConfiguration getLiferayTomcatConfiguration() throws CoreException
    {
        return getLiferayTomcatServer().getLiferayTomcatConfiguration();
    }

    @Override
    protected void publishFinish( IProgressMonitor monitor ) throws CoreException
    {
        super.publishFinish( monitor );

        this.redeployModules = null;
    }

    @Override
    protected void publishModule( int kind, int deltaKind, IModule[] moduleTree, IProgressMonitor monitor )
        throws CoreException
    {
        boolean shouldPublishModule =
            LiferayPublishHelper.prePublishModule(
                this, kind, deltaKind, moduleTree, getPublishedResourceDelta( moduleTree ), monitor );

        if( shouldPublishModule )
        {
            if( getServer().getServerState() != IServer.STATE_STOPPED )
            {
                if( deltaKind == ServerBehaviourDelegate.ADDED || deltaKind == ServerBehaviourDelegate.REMOVED )
                {
                    setServerRestartState( true );
                }
            }

            setModulePublishState( moduleTree, IServer.PUBLISH_STATE_NONE );
        }
        else
        {
            // wasn't able to publish module, should set to needs full publish
            setModulePublishState( moduleTree, IServer.PUBLISH_STATE_FULL );
        }
    }

    @Override
    protected void publishModules( int kind, List modules, List deltaKind2, MultiStatus multi, IProgressMonitor monitor )
    {
        super.publishModules( kind, ( redeployModules == null ) ? modules : redeployModules, deltaKind2, multi, monitor );
    }

    @Override
	public void redeployModule( IModule[] module ) throws CoreException
    {
        setModulePublishState( module, IServer.PUBLISH_STATE_FULL );

        IAdaptable info = new IAdaptable()
        {
            @Override
			@SuppressWarnings( "unchecked" )
            public Object getAdapter( Class adapter )
            {
                if( String.class.equals( adapter ) )
                {
                    return "user"; //$NON-NLS-1$
                }

                return null;
            }
        };

        final List<IModule[]> modules = new ArrayList<IModule[]>();
        modules.add( module );

        redeployModules = modules;
        try
        {
            publish( IServer.PUBLISH_FULL, modules, null, info );
        }
        catch( CoreException e )
        {
            throw e;
        }
        finally
        {
            redeployModules = null;
        }
    }

    @Override
    public void setupLaunchConfiguration( ILaunchConfigurationWorkingCopy workingCopy, IProgressMonitor monitor )
        throws CoreException
    {

        super.setupLaunchConfiguration( workingCopy, monitor );

        workingCopy.setAttribute( DebugPlugin.ATTR_CONSOLE_ENCODING, "UTF-8" );

        String existingVMArgs =
            workingCopy.getAttribute( IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, (String) null );

        if( null != existingVMArgs )
        {
            String[] parsedVMArgs = DebugPlugin.parseArguments( existingVMArgs );

            List<String> memoryArgs = new ArrayList<String>();

            if( ListUtil.isNotEmpty(parsedVMArgs) )
            {
                for( String pArg : parsedVMArgs )
                {
                    if( pArg.startsWith( "-Xm" ) || pArg.startsWith( "-XX:" ) )
                    {
                        memoryArgs.add( pArg );
                    }
                }
            }

            String argsWithoutMem =
                mergeArguments( existingVMArgs, getRuntimeVMArguments(), memoryArgs.toArray( new String[0] ), false );
            String fixedArgs = mergeArguments( argsWithoutMem, getRuntimeVMArguments(), null, false );

            workingCopy.setAttribute( IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, fixedArgs );
        }
    }

}
