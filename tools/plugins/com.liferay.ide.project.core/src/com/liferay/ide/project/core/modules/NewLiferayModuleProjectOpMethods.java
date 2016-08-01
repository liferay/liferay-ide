/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.core.portal.PortalServer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Simon Jiang
 */
public class NewLiferayModuleProjectOpMethods
{
    public static final Status execute( final NewLiferayModuleProjectOp op, final ProgressMonitor pm )
    {
        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay module project (this process may take several minutes)", 100 ); //$NON-NLS-1$

        Status retval = null;

        try
        {
            final NewLiferayProjectProvider<NewLiferayModuleProjectOp> projectProvider = op.getProjectProvider().content( true );

            final IStatus status = projectProvider.createNewProject( op, monitor );

            retval = StatusBridge.create( status );
        }
        catch( Exception e )
        {
            final String msg = "Error creating Liferay module project."; //$NON-NLS-1$
            ProjectCore.logError( msg, e );

            return Status.createErrorStatus( msg + " Please see Eclipse error log for more details.", e );
        }

        return retval;
    }

    public static String getMavenParentPomGroupId( NewLiferayModuleProjectOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> groupId =
                op.getProjectProvider().content().getData( "parentGroupId", String.class, parentProjectDir );

            if( ! groupId.isEmpty() )
            {
                retval = groupId.get( 0 );
            }
        }

        return retval;
    }

    public static String getMavenParentPomVersion( NewLiferayModuleProjectOp op, String projectName, IPath path )
    {
        String retval = null;

        final File parentProjectDir = path.toFile();
        final IStatus locationStatus = op.getProjectProvider().content().validateProjectLocation( projectName, path );

        if( locationStatus.isOK() && parentProjectDir.exists() && parentProjectDir.list().length > 0 )
        {
            List<String> version =
                op.getProjectProvider().content().getData( "parentVersion", String.class, parentProjectDir );

            if( !version.isEmpty() )
            {
                retval = version.get( 0 );
            }
        }

        return retval;
    }

    public static void addProperties( File dest, List<String> properties ) throws Exception
    {
        try
        {
            if( properties == null || properties.size() < 1 )
            {
                return;
            }

            FileInputStream fis = new FileInputStream( dest );

            String content = new String( FileUtil.readContents( fis ) );

            fis.close();

            String fontString = content.substring( 0, content.indexOf( "property" ) );

            String endString = content.substring( content.indexOf( "}," ) + 2 );

            String property = content.substring( content.indexOf( "property" ), content.indexOf( "}," ) );

            property = property.substring( property.indexOf( "{" ) + 1 );

            StringBuilder sb = new StringBuilder();

            sb.append( "property = {\n" );

            if( !CoreUtil.isNullOrEmpty( property ) )
            {
                property = property.substring( 1 );
                property = property.substring( 0, property.lastIndexOf( "\t" ) - 1 );
                property += ",\n";
                sb.append( property );
            }

            for( String str : properties )
            {
                sb.append( "\t\t\"" + str + "\",\n" );
            }

            sb.deleteCharAt( sb.toString().length() - 2 );

            sb.append( "\t}," );

            StringBuilder all = new StringBuilder();

            all.append( fontString );
            all.append( sb.toString() );
            all.append( endString );

            String newContent = all.toString();

            if( !content.equals( newContent ) )
            {
                FileUtil.writeFileFromStream( dest, new ByteArrayInputStream( newContent.getBytes() ) );
            }
        }
        catch(Exception e)
        {
            ProjectCore.logError( "error when adding properties to "+dest.getAbsolutePath(), e );
        }
    }

    public static void addDependencies( File file, String bundleId )
    {
        IServer runningServer = null;
        final IServer[] servers = ServerCore.getServers();

        for( IServer server : servers )
        {
            if( server.getServerState() == IServer.STATE_STARTED &&
                server.getServerType().getId().equals( PortalServer.ID ) )
            {
                runningServer = server;
                break;
            }
        }

        final ServiceCommand serviceCommand = new ServiceCommand( runningServer, bundleId );

        try
        {
            final ServiceContainer osgiService = serviceCommand.execute();

            if( osgiService != null )
            {
                setDenpendencies( file, osgiService.getBundleName(), osgiService.getBundleVersion() );
            }
        }
        catch( Exception e )
        {
            ProjectCore.logError( "Can't update project denpendencies. ", e );
        }
    }

    private static void setDenpendencies(File file , String bundleId , String bundleVersion) throws Exception
    {
       String content = new String( FileUtil.readContents( file, true ) );

       String head = content.substring( 0 , content.lastIndexOf( "dependencies" ) );

       String end = content.substring( content.lastIndexOf( "}" )+1 , content.length() );

       String dependencies = content.substring( content.lastIndexOf( "{" )+2 , content.lastIndexOf( "}" ) );

       String appended = "\tcompile 'com.liferay:"+bundleId+":"+bundleVersion+"'\n";

       StringBuilder preNewContent = new StringBuilder();

       preNewContent.append(head);
       preNewContent.append("dependencies {\n");
       preNewContent.append(dependencies+appended);
       preNewContent.append("}");
       preNewContent.append(end);

       String newContent = preNewContent.toString();

       if (!content.equals(newContent))
       {
           FileUtil.writeFileFromStream( file, new ByteArrayInputStream( newContent.getBytes() ) );
       }
    }
}
