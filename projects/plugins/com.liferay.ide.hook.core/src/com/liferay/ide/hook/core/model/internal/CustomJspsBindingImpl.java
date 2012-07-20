/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.hook.core.model.CustomJsp;
import com.liferay.ide.hook.core.model.CustomJspDir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Resource;

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
        IFolder customJspFolder = getCustomJspFolder();

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
        return new CustomJspResource( this.element().resource(), (ObjectValue<String>) obj );
    }

    @Override
    protected Object insertUnderlyingObject( ModelElementType type, int position )
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
            if( member instanceof IFile && "jsp".equals( member.getFileExtension() ) )
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

        IFolder customJspFolder = getCustomJspFolder();

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

    private IFolder getCustomJspFolder()
    {
        CustomJspDir element = this.hook().getCustomJspDir().element();
        IFolder docroot = CoreUtil.getDocroot( project() );

        if( element != null && docroot != null )
        {
            Path customJspDir = element.getValue().getContent();
            IFolder customJspFolder = docroot.getFolder( customJspDir.toPortableString() );
            return customJspFolder;
        }
        else
        {
            return null;
        }
    }

    @Override
    public void init( IModelElement element, ModelProperty property, String[] params )
    {
        super.init( element, property, params );

        try
        {
            ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( project() );

            if( liferayRuntime != null )
            {
                this.portalDir = liferayRuntime.getPortalDir();
            }
        }
        catch( CoreException e )
        {
        }
    }

    @Override
    public void remove( Resource resource )
    {
        ObjectValue<String> customJsp = resource.adapt( CustomJspResource.class ).getCustomJsp();
        this.customJsps.remove( customJsp );
    }

    @Override
    public ModelElementType type( Resource resource )
    {
        if( resource instanceof CustomJspResource )
        {
            return CustomJsp.TYPE;
        }

        return null;
    }

}
