/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.core;

import com.liferay.ide.core.util.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElementDelta;

/**
 * @author Kuo Zhang
 */
public class LiferayLanguagePropertiesListener implements IElementChangedListener
{

    public void elementChanged( ElementChangedEvent event )
    {
        List<IFile> changedFiles = new ArrayList<IFile>();

        getChangedFiles( event.getDelta(), changedFiles );

        for( IFile changedFile : changedFiles )
        {
            if( PropertiesUtil.isLanguagePropertiesFile( changedFile ) )
            {
                validateLanguagePropertiesEncoding( changedFile );
            }
        }
    }

    private void getChangedFiles( IJavaElementDelta delta, List<IFile> changedFiles )
    {

        if( delta.getFlags() == IJavaElementDelta.F_CONTENT )
        {
            IResourceDelta[] resourceDeltas = delta.getResourceDeltas();

            for( IResourceDelta resourceDelta : resourceDeltas )
            {
                if( resourceDelta.getResource().getType() == IResource.FILE )
                {
                    changedFiles.add( (IFile) resourceDelta.getResource() );
                }
            }
        }
        else
        {
            IJavaElementDelta[] deltas = delta.getAffectedChildren();

            for( IJavaElementDelta childDelta : deltas )
            {
                getChangedFiles( childDelta, changedFiles );
            }
        }

    }

    private void validateLanguagePropertiesEncoding( final IFile file )
    {

        final LiferayLanguagePropertiesValidator validator =
            LiferayLanguagePropertiesValidator.getValidator( file );

        new WorkspaceJob( "Valiting the encoding of liferay language properties." )
        {
            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
            {
                validator.validateEncoding();

                return Status.OK_STATUS;
            }
        }.schedule();
    }
}
