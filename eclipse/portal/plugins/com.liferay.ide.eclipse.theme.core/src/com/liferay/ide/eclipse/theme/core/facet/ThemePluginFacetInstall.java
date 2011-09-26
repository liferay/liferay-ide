/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc and Accenture Services Pvt., Ltd.,. All rights reserved.
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

package com.liferay.ide.eclipse.theme.core.facet;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.core.util.FileUtil;
import com.liferay.ide.eclipse.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.eclipse.project.core.facet.PluginFacetInstall;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.theme.core.ThemeCSSBuilder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * @author Greg Amerson
 * @author kamesh.sampath [IDE-450]
 */
public class ThemePluginFacetInstall extends PluginFacetInstall {

	@Override
	public void execute( IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor )
		throws CoreException {

		super.execute( project, fv, config, monitor );

		IDataModel model = (IDataModel) config;

		IDataModel masterModel = (IDataModel) model.getProperty( FacetInstallDataModelProvider.MASTER_PROJECT_DM );

		if ( masterModel != null && masterModel.getBooleanProperty( CREATE_PROJECT_OPERATION ) ) {
			// get the template zip for theme and extract into the project
			SDK sdk = getSDK();

			String themeName = this.masterModel.getStringProperty( THEME_NAME );

			// FIX IDE-450
			if ( themeName.endsWith( ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX ) ) {
				themeName = themeName.substring( 0, themeName.indexOf( ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX ) );
			}
			// END FIX IDE-450

			String displayName = this.masterModel.getStringProperty( DISPLAY_NAME );

			IPath newThemePath = sdk.createNewThemeProject( themeName, displayName );

			processNewFiles( newThemePath.append( themeName + ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX ), false );

			// cleanup files
			FileUtil.deleteDir( newThemePath.toFile(), true );

			// delete WEB-INF/lib and META-INF
			CoreUtil.deleteResource( project.findMember( "docroot/META-INF" ) );
		}
		else {
			setupDefaultOutputLocation();
		}

		removeUnneededClasspathEntries();

		IResource libRes = project.findMember( "docroot/WEB-INF/lib" );

		if ( libRes != null && libRes.exists() ) {
			IFolder libFolder = (IFolder) libRes;
			IResource[] libFiles = libFolder.members( true );
			if ( CoreUtil.isNullOrEmpty( libFiles ) ) {
				libRes.delete( true, monitor );
			}
		}

		installThemeBuilder( this.project );

		this.project.refreshLocal( IResource.DEPTH_INFINITE, monitor );
	}

	protected void installThemeBuilder( IProject project ) throws CoreException {

		if ( project == null ) {
			return;
		}

		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();

		for ( ICommand command : commands ) {
			if ( ThemeCSSBuilder.ID.equals( command.getBuilderName() ) ) {
				return;
			}
		}

		// add builder to project
		ICommand command = desc.newCommand();
		command.setBuilderName( ThemeCSSBuilder.ID );
		ICommand[] nc = new ICommand[commands.length + 1];

		// Add it before other builders.
		System.arraycopy( commands, 0, nc, 1, commands.length );
		nc[0] = command;
		desc.setBuildSpec( nc );

		project.setDescription( desc, null );
	}

	protected void removeUnneededClasspathEntries() {
		IFacetedProjectWorkingCopy facetedProject = getFacetedProject();
		IJavaProject javaProject = JavaCore.create( facetedProject.getProject() );

		try {
			IClasspathEntry[] existingClasspath = javaProject.getRawClasspath();
			List<IClasspathEntry> newClasspath = new ArrayList<IClasspathEntry>();

			for ( IClasspathEntry entry : existingClasspath ) {
				if ( entry.getEntryKind() == IClasspathEntry.CPE_SOURCE ) {
					continue;
				}
				else if ( entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER ) {
					String path = entry.getPath().toPortableString();
					if ( path.contains( "org.eclipse.jdt.launching.JRE_CONTAINER" ) ||
						path.contains( "org.eclipse.jst.j2ee.internal.web.container" ) ||
						path.contains( "org.eclipse.jst.j2ee.internal.module.container" ) ) {
						continue;
					}
				}

				newClasspath.add( entry );
			}

			javaProject.setRawClasspath( newClasspath.toArray( new IClasspathEntry[0] ), null );

			IResource sourceFolder =
				javaProject.getProject().findMember( IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER );

			if ( sourceFolder.exists() ) {
				sourceFolder.delete( true, null );
			}
		}
		catch ( Exception e ) {

		}
	}

	@Override
	protected boolean shouldInstallPluginLibraryDelegate() {
		return false;
	}

}
