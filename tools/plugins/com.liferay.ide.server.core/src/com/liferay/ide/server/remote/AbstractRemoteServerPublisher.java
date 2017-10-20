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
package com.liferay.ide.server.remote;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;

/**
 * @author Simon Jiang
 */
public abstract class AbstractRemoteServerPublisher implements IRemoteServerPublisher
{
    private IProject project;

    public AbstractRemoteServerPublisher( IProject project )
    {
        this.project = project;
    }

    protected void addRemoveProps(
        IPath deltaPath, IResource deltaResource, ZipOutputStream zip, Map<ZipEntry, String> deleteEntries,
        String deletePrefix ) throws IOException
    {
        String archive = removeArchive( deltaPath.toPortableString() );

        ZipEntry zipEntry = null;

        // check to see if we already have an entry for this archive
        for( ZipEntry entry : deleteEntries.keySet() )
        {
            if( entry.getName().startsWith( archive ) )
            {
                zipEntry = entry;
            }
        }

        if( zipEntry == null )
        {
            zipEntry = new ZipEntry( archive + "META-INF/" + deletePrefix + "-partialapp-delete.props" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        String existingFiles = deleteEntries.get( zipEntry );

        // String file = encodeRemovedPath(deltaPath.toPortableString().substring(archive.length()));
        String file = deltaPath.toPortableString().substring( archive.length() );

        if( deltaResource.getType() == IResource.FOLDER )
        {
            file += "/.*"; //$NON-NLS-1$
        }

        deleteEntries.put( zipEntry, ( existingFiles != null ? existingFiles : StringPool.EMPTY ) + ( file + "\n" ) ); //$NON-NLS-1$
    }

    protected void addToZip( IPath path, IResource resource, ZipOutputStream zip, boolean adjustGMTOffset )
                    throws IOException, CoreException
    {
        switch( resource.getType() )
        {
            case IResource.FILE:
                ZipEntry zipEntry = new ZipEntry( path.toString() );

                zip.putNextEntry( zipEntry );

                InputStream contents = ( (IFile) resource ).getContents();

                if( adjustGMTOffset )
                {
                    TimeZone currentTimeZone = TimeZone.getDefault();
                    Calendar currentDt = new GregorianCalendar( currentTimeZone, Locale.getDefault() );

                    // Get the Offset from GMT taking current TZ into account
                    int gmtOffset =
                        currentTimeZone.getOffset(
                            currentDt.get( Calendar.ERA ), currentDt.get( Calendar.YEAR ),
                            currentDt.get( Calendar.MONTH ), currentDt.get( Calendar.DAY_OF_MONTH ),
                            currentDt.get( Calendar.DAY_OF_WEEK ), currentDt.get( Calendar.MILLISECOND ) );

                    zipEntry.setTime( System.currentTimeMillis() + ( gmtOffset * -1 ) );
                }

                try
                {
                    IOUtils.copy( contents, zip );
                }
                finally
                {
                    contents.close();
                }

                break;

            case IResource.FOLDER:
            case IResource.PROJECT:
                IContainer container = (IContainer) resource;

                IResource[] members = container.members();

                for( IResource res : members )
                {
                    addToZip( path.append( res.getName() ), res, zip, adjustGMTOffset );
                }
        }
    }

    protected IProject getProject()
    {
        return this.project;
    }

    protected void processResourceDeltas(
        IModuleResourceDelta[] deltas, ZipOutputStream zip, Map<ZipEntry, String> deleteEntries, String deletePrefix,
        String deltaPrefix, boolean adjustGMTOffset ) throws IOException, CoreException
    {
        for( IModuleResourceDelta delta : deltas )
        {
            final int deltaKind = delta.getKind();

            final IResource deltaResource = (IResource) delta.getModuleResource().getAdapter( IResource.class );

            final IProject deltaProject = deltaResource.getProject();

            // IDE-110 IDE-648
            final IWebProject lrproject = LiferayCore.create( IWebProject.class, deltaProject );

            if( lrproject != null )
            {
                final IFolder webappRoot = lrproject.getDefaultDocrootFolder();

                IPath deltaPath = null;

                if( webappRoot != null && webappRoot.exists() )
                {
                    final IPath deltaFullPath = deltaResource.getFullPath();
                    final IPath containerFullPath = webappRoot.getFullPath();
                    deltaPath = new Path( deltaPrefix + deltaFullPath.makeRelativeTo( containerFullPath ) );

                    if( deltaPath != null && deltaPath.segmentCount() > 0 )
                    {
                        break;
                    }
                }

                if( deltaKind == IModuleResourceDelta.ADDED || deltaKind == IModuleResourceDelta.CHANGED )
                {
                    addToZip( deltaPath, deltaResource, zip, adjustGMTOffset );
                }
                else if( deltaKind == IModuleResourceDelta.REMOVED )
                {
                    addRemoveProps( deltaPath, deltaResource, zip, deleteEntries, deletePrefix );
                }
                else if( deltaKind == IModuleResourceDelta.NO_CHANGE )
                {
                    IModuleResourceDelta[] children = delta.getAffectedChildren();
                    processResourceDeltas(
                        children, zip, deleteEntries, deletePrefix, deltaPrefix, adjustGMTOffset );
                }
            }
        }
    }

    public IPath publishModuleDelta(
        String archiveName, IModuleResourceDelta[] deltas, String deletePrefix, boolean adjustGMTOffset )
        throws CoreException
    {
        IPath path = LiferayServerCore.getTempLocation( "partial-war", archiveName ); //$NON-NLS-1$
 
        File warfile = path.toFile();

        warfile.getParentFile().mkdirs();

        try(OutputStream outputStream = Files.newOutputStream( warfile.toPath() );
                        ZipOutputStream zip = new ZipOutputStream( outputStream ))
        {
            Map<ZipEntry, String> deleteEntries = new HashMap<ZipEntry, String>();

            processResourceDeltas( deltas, zip, deleteEntries, deletePrefix, StringPool.EMPTY, adjustGMTOffset );

            for( ZipEntry entry : deleteEntries.keySet() )
            {
                zip.putNextEntry( entry );
                zip.write( deleteEntries.get( entry ).getBytes() );
            }

            // if ((removedResources != null) && (removedResources.size() > 0)) {
            // writeRemovedResources(removedResources, zip);
            // }
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }

        return new Path( warfile.getAbsolutePath() );
    }

    private String removeArchive( String archive )
    {
        int index = Math.max( archive.lastIndexOf( ".war" ), archive.lastIndexOf( ".jar" ) ); //$NON-NLS-1$ //$NON-NLS-2$

        if( index >= 0 )
        {
            return archive.substring( 0, index + 5 );
        }

        return StringPool.EMPTY;
    }
}
