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

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.hook.core.model.CustomJsp;
import com.liferay.ide.hook.core.util.HookUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Resource;

/**
 * @author Gregory Amerson
 */
public class CustomJspsBindingImpl extends HookListBindingImpl
{
    private List<ObjectValue<String>> customJsps;
    private IPath lastCustomJspDirPath;
    private IPath portalDir;

    @Override
    protected List<?> readUnderlyingList()
    {
        IFolder customJspFolder = HookUtil.getCustomJspFolder( this.hook(), project() );

        if( customJspFolder == null || this.portalDir == null )
        {
            this.lastCustomJspDirPath = null;
            return Collections.emptyList();
        }

        IPath customJspDirPath = customJspFolder.getProjectRelativePath();

        if( customJspDirPath != null && customJspDirPath.equals( lastCustomJspDirPath ) )
        {
            return this.customJsps;
        }

        this.customJsps = new ArrayList<ObjectValue<String>>();

        this.lastCustomJspDirPath = customJspDirPath;

        IFile[] customJspFiles = getCustomJspFiles();

        for( IFile customJspFile : customJspFiles )
        {
            IPath customJspFilePath = customJspFile.getProjectRelativePath();

            IPath customJspPath =
                customJspFilePath.removeFirstSegments( customJspFilePath.matchingFirstSegments( customJspDirPath ) );

            this.customJsps.add( new ObjectValue<String>( customJspPath.toPortableString() ) );
        }

        return this.customJsps;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    protected Resource resource( Object obj )
    {
        return new CustomJspResource( this.property().element().resource(), (ObjectValue<String>) obj );
    }

    @Override
    protected Object insertUnderlyingObject( ElementType type, int position )
    {
        ObjectValue<String> retval = null;

        if( type.equals( CustomJsp.TYPE ) )
        {
            retval = new ObjectValue<String>();
            this.customJsps.add( retval );
        }

        return retval;
    }

    private void findJspFiles( IFolder folder, List<IFile> jspFiles ) throws CoreException
    {
        if( folder == null || !folder.exists() )
        {
            return;
        }

        IResource[] members = folder.members( IResource.FOLDER | IResource.FILE );

        for( IResource member : members )
        {
            if( member instanceof IFile && "jsp".equals( member.getFileExtension() ) ) //$NON-NLS-1$
            {
                jspFiles.add( (IFile) member );
            }
            else if( member instanceof IFolder )
            {
                findJspFiles( (IFolder) member, jspFiles );
            }
        }

    }

    private IFile[] getCustomJspFiles()
    {
        List<IFile> customJspFiles = new ArrayList<IFile>();

        IFolder customJspFolder = HookUtil.getCustomJspFolder( hook(), project() );

        try
        {
            findJspFiles( customJspFolder, customJspFiles );
        }
        catch( CoreException e )
        {
            e.printStackTrace();
        }

        return customJspFiles.toArray( new IFile[0] );
    }

    @Override
    public void init( Property property )
    {
        super.init( property );

        final ILiferayProject liferayProject = LiferayCore.create( project() );

        if( liferayProject != null )
        {
            this.portalDir = liferayProject.getAppServerPortalDir();
        }
    }

    @Override
    public void remove( Resource resource )
    {
        ObjectValue<String> customJsp = resource.adapt( CustomJspResource.class ).getCustomJsp();
        this.customJsps.remove( customJsp );
    }

    @Override
    public ElementType type( Resource resource )
    {
        if( resource instanceof CustomJspResource )
        {
            return CustomJsp.TYPE;
        }

        return null;
    }

}
