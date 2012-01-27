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
 *******************************************************************************/

package com.liferay.ide.eclipse.server.jboss.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;

import com.liferay.ide.eclipse.core.util.FileListing;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.server.core.ILiferayLocalRuntime;
import com.liferay.ide.eclipse.server.jboss.core.LiferayJBossServerCorePlugin;

/**
 * @author kamesh
 */
public class LiferayJBossUtil
{

	public static IPath[] getAllUserClasspathLibraries( IPath runtimeLocation, IPath portalDir )
	{
		List<IPath> libs = new ArrayList<IPath>();
		IPath webinfLibFolder = portalDir.append( "WEB-INF/lib" );

		try
		{
			List<File> libFiles = FileListing.getFileListing( new File( webinfLibFolder.toOSString() ) );

			for ( File lib : libFiles )
			{
				if ( lib.exists() && lib.getName().endsWith( ".jar" ) )
				{
					libs.add( new Path( lib.getPath() ) );
				}
			}
		}
		catch ( FileNotFoundException e )
		{
			LiferayJBossServerCorePlugin.logError( e );
		}

		return libs.toArray( new IPath[0] );
	}

	/**
	 * @param runtime
	 * @return
	 */
	public static ILiferayLocalRuntime getLiferayLocalRuntime( IRuntime runtime )
	{
		if ( runtime != null )
		{
			return (ILiferayLocalRuntime) runtime.createWorkingCopy();
		}

		return null;
	}

	/*
	 * public static boolean isExtProjectContext(Context context) { return false; }
	 */

	public static boolean isLiferayModule( IModule module )
	{
		boolean retval = false;

		if ( module != null )
		{
			IProject project = module.getProject();

			retval = ProjectUtil.isLiferayProject( project );
		}

		return retval;
	}

}
