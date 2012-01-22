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

package com.liferay.ide.eclipse.server.jboss.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.wst.server.core.IRuntime;
import org.jboss.ide.eclipse.as.classpath.core.runtime.ClientAllRuntimeClasspathProvider;
import org.jboss.ide.eclipse.as.classpath.core.runtime.CustomRuntimeClasspathModel;
import org.jboss.ide.eclipse.as.classpath.core.runtime.CustomRuntimeClasspathModel.IDefaultPathProvider;
import org.jboss.ide.eclipse.as.classpath.core.runtime.CustomRuntimeClasspathModel.PathProviderFileset;

/**
 * @author kamesh
 */
public class LiferayBossRuntimeClasspathProvider extends ClientAllRuntimeClasspathProvider
{

	/**
	 * 
	 * @param runtimeId
	 * @return
	 */
	private IDefaultPathProvider[] getRuntimeDefaultPathProvider( String runtimeId )
	{

		if ( runtimeId.contains( "70" ) )
		{
			return CustomRuntimeClasspathModel.getInstance().getDefaultAS70Entries();
		}
		else if ( runtimeId.contains( "71" ) )
		{
			return CustomRuntimeClasspathModel.getInstance().getDefaultAS71Entries();
		}

		return null;

	}

	/**
	 * 
	 * @param runtimeId
	 * @return
	 */
	private IDefaultPathProvider[] getLiferayPortalRuntimeEntries( String runtimeId )
	{
		ArrayList<IDefaultPathProvider> sets = new ArrayList<IDefaultPathProvider>();
		sets.addAll( Arrays.asList( getRuntimeDefaultPathProvider( runtimeId ) ) );
		sets.add( new PathProviderFileset( "modules/com/liferay/portal/main" ) );
		return (IDefaultPathProvider[]) sets.toArray( new IDefaultPathProvider[sets.size()] );
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.jboss.ide.eclipse.as.classpath.core.runtime.ClientAllRuntimeClasspathProvider#getClasspathEntriesForRuntime
	 * (org.eclipse.wst.server.core.IRuntime)
	 */
	@Override
	protected IClasspathEntry[] getClasspathEntriesForRuntime( IRuntime rt )
	{

		// Default AS7x Entries
		IDefaultPathProvider[] jbossLiferayPortalPathProvider = getLiferayPortalRuntimeEntries( rt.getRuntimeType().getId() );

		ArrayList<IPath> as7ModulePaths = new ArrayList<IPath>();

		for ( IDefaultPathProvider iDefaultPathProvider : jbossLiferayPortalPathProvider )
		{
			iDefaultPathProvider.setRuntime( rt );
			IPath[] absPaths = iDefaultPathProvider.getAbsolutePaths();
			for ( IPath iPath : absPaths )
			{
				as7ModulePaths.add( iPath );
			}
		}

		ArrayList<Entry> entries = new ArrayList<Entry>();

		for ( IPath iPath : as7ModulePaths )
		{
			addSinglePath( iPath, entries );
		}

		// TODO add Liferay Entries

		List<IClasspathEntry> ret = convert( entries );
		return (IClasspathEntry[]) ret.toArray( new IClasspathEntry[ret.size()] );
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jst.server.core.RuntimeClasspathProviderDelegate#resolveClasspathContainer(org.eclipse.core.resources
	 * .IProject, org.eclipse.wst.server.core.IRuntime)
	 */
	@Override
	public IClasspathEntry[] resolveClasspathContainer( IProject project, IRuntime runtime )
	{
		return getClasspathEntriesForRuntime( runtime );
	}

}
