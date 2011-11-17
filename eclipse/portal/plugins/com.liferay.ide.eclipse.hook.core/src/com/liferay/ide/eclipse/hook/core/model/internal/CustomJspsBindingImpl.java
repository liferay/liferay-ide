/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.hook.core.model.internal;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.hook.core.model.ICustomJsp;
import com.liferay.ide.eclipse.hook.core.model.ICustomJspDir;
import com.liferay.ide.eclipse.hook.core.model.IHook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.ListBindingImpl;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Resource;


/**
 * @author Gregory Amerson
 */
public class CustomJspsBindingImpl extends ListBindingImpl
{

	private List<Resource> customJspsResources;
	private IPath lastCustomJspDirPath;

	@Override
	public Resource add( ModelElementType type )
	{
		if ( type.equals( ICustomJsp.TYPE ) )
		{
			CustomJspResource newCustomJspResource = new CustomJspResource( this.element().resource() );

			this.customJspsResources.add( newCustomJspResource );

			this.element().notifyPropertyChangeListeners( this.property() );

			return newCustomJspResource;
		}
		else
		{
			return null;
		}
	}

	private void findJspFiles( IFolder folder, List<IFile> jspFiles ) throws CoreException
	{
		IResource[] members = folder.members( IResource.FOLDER | IResource.FILE );

		for ( IResource member : members )
		{
			if ( member instanceof IFile && "jsp".equals( member.getFileExtension() ) )
			{
				jspFiles.add( (IFile) member );
			}
			else if ( member instanceof IFolder )
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
		catch ( CoreException e )
		{
			e.printStackTrace();
		}

		return customJspFiles.toArray( new IFile[0] );
	}

	private IFolder getCustomJspFolder()
	{
		ICustomJspDir element = this.hook().getCustomJspDir().element();

		if ( element != null )
		{
			Path customJspDir = element.getValue().getContent();
			IFolder docroot = CoreUtil.getDocroot( project() );
			IFolder customJspFolder = docroot.getFolder( customJspDir.toPortableString() );
			return customJspFolder;
		}
		else
		{
			return null;
		}
	}

	private IHook hook()
	{
		return this.element().nearest( IHook.class );
	}

	private IProject project()
	{
		return this.hook().adapt( IProject.class );
	}

	@Override
	public List<Resource> read()
	{
		IFolder customJspFolder = getCustomJspFolder();

		if ( customJspFolder == null )
		{
			this.lastCustomJspDirPath = null;
			return Collections.emptyList();
		}

		IPath customJspDirPath = customJspFolder.getProjectRelativePath();

		if ( customJspDirPath != null && customJspDirPath.equals( lastCustomJspDirPath ) )
		{
			return this.customJspsResources;
		}

		this.customJspsResources = new ArrayList<Resource>();

		this.lastCustomJspDirPath = customJspDirPath;

		IFile[] customJspFiles = getCustomJspFiles();

		for ( IFile customJspFile : customJspFiles )
		{
			IPath customJspFilePath = customJspFile.getProjectRelativePath();

			IPath customJspPath =
				customJspFilePath.removeFirstSegments( customJspFilePath.matchingFirstSegments( customJspDirPath ) );

			CustomJspResource resource =
				new CustomJspResource( this.element().resource(), customJspPath.toPortableString() );

			this.customJspsResources.add( resource );
		}

		return this.customJspsResources;
	}

	@Override
	public void remove( Resource resource )
	{
		this.customJspsResources.remove( resource );
		this.element().notifyPropertyChangeListeners( this.property() );
	}

	@Override
	public ModelElementType type( Resource resource )
	{
		if ( resource instanceof CustomJspResource )
		{
			return ICustomJsp.TYPE;
		}

		return null;
	}

}
