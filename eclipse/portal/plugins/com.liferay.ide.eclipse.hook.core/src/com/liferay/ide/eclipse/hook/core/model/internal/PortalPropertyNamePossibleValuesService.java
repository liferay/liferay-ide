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

import com.liferay.ide.eclipse.server.core.ILiferayRuntime;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.SortedSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.services.PossibleValuesService;


/**
 * @author Gregory Amerson
 */
public class PortalPropertyNamePossibleValuesService extends PossibleValuesService
{

	final FileFilter jspfilter = new FileFilter()
	{

		public boolean accept( File pathname )
		{
			return pathname.isDirectory() || pathname.getName().endsWith( ".jsp" );
		}
	};

	private IPath portalDir;

	@Override
	protected void fillPossibleValues( SortedSet<String> values )
	{
		File[] files = portalDir.toFile().listFiles( jspfilter );
		findJSPFiles( files, values );
		
	}

	private void findJSPFiles( File[] files, SortedSet<String> values )
	{
		for ( File file : files )
		{
			if ( file.isDirectory() )
			{
				findJSPFiles( file.listFiles( jspfilter ), values );
			}
			else
			{
				values.add( new Path( file.getAbsolutePath() ).removeFirstSegments( 8 ).toPortableString() );
			}
		}
	}

	@Override
	protected void init()
	{
		super.init();
		IProject project = context( IModelElement.class ).root().adapt( IFile.class ).getProject();

		try
		{
			ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( project );
			this.portalDir = liferayRuntime.getPortalDir();
		}
		catch ( CoreException e )
		{
		}
	}

}
