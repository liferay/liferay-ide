/**
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
 */

package com.liferay.ide.maven.core;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.LaunchHelper;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.server.remote.AbstractRemoteServerPublisher;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 */
public class MavenProjectRemoteServerPublisher extends AbstractRemoteServerPublisher {

	public MavenProjectRemoteServerPublisher(IProject project) {
		super(project);
	}

	@Override
	public void processResourceDeltas(
			IModuleResourceDelta[] deltas, ZipOutputStream zip, Map<ZipEntry, String> deleteEntries,
			String deletePrefix, String deltaPrefix, boolean adjustGMTOffset)
		throws CoreException, IOException {

		for (IModuleResourceDelta delta : deltas) {
			IModuleResource moduleResource = delta.getModuleResource();

			IResource deltaResource = (IResource)moduleResource.getAdapter(IResource.class);

			IProject deltaProject = deltaResource.getProject();

			IWebProject lrproject = LiferayCore.create(IWebProject.class, deltaProject);

			if ((lrproject == null) || (lrproject.getDefaultDocrootFolder() == null)) {
				continue;
			}

			IFolder webappRoot = lrproject.getDefaultDocrootFolder();
			int deltaKind = delta.getKind();
			IPath deltaFullPath = deltaResource.getFullPath();

			boolean deltaZip = false;
			IPath deltaPath = null;

			if (FileUtil.exists(webappRoot)) {
				IPath containerFullPath = webappRoot.getFullPath();

				if (containerFullPath.isPrefixOf(deltaFullPath)) {
					deltaZip = true;

					deltaPath = new Path(deltaPrefix + deltaFullPath.makeRelativeTo(containerFullPath));
				}
			}

			if (!deltaZip && new Path("WEB-INF").isPrefixOf(delta.getModuleRelativePath())) {
				List<IFolder> folders = CoreUtil.getSourceFolders(JavaCore.create(deltaProject));

				for (IFolder folder : folders) {
					IPath folderPath = folder.getFullPath();

					if (folderPath.isPrefixOf(deltaFullPath)) {
						deltaZip = true;

						break;
					}
				}
			}

			if (!deltaZip &&
				((deltaKind == IModuleResourceDelta.ADDED) || (deltaKind == IModuleResourceDelta.CHANGED) ||
				 (deltaKind == IModuleResourceDelta.REMOVED))) {

				IJavaProject javaProject = JavaCore.create(deltaProject);

				IPath targetPath = javaProject.getOutputLocation();

				deltaZip = true;
				deltaPath = new Path(
					"WEB-INF/classes"
				).append(
					deltaFullPath.makeRelativeTo(targetPath)
				);
			}

			if (deltaZip) {
				if ((deltaKind == IModuleResourceDelta.ADDED) || (deltaKind == IModuleResourceDelta.CHANGED)) {
					addToZip(deltaPath, deltaResource, zip, adjustGMTOffset);
				}
				else if (deltaKind == IModuleResourceDelta.REMOVED) {
					addRemoveProps(deltaPath, deltaResource, zip, deleteEntries, deletePrefix);
				}
				else if (deltaKind == IModuleResourceDelta.NO_CHANGE) {
					IModuleResourceDelta[] children = delta.getAffectedChildren();

					processResourceDeltas(children, zip, deleteEntries, deletePrefix, deltaPrefix, adjustGMTOffset);
				}
			}
		}
	}

	public IPath publishModuleFull(IProgressMonitor monitor) throws CoreException {
		IPath retval = null;

		if (_runMavenGoal(getProject(), monitor)) {
			IMavenProjectFacade projectFacade = MavenUtil.getProjectFacade(getProject(), monitor);

			MavenProject mavenProject = projectFacade.getMavenProject(monitor);

			Build build = mavenProject.getBuild();

			String targetFolder = build.getDirectory();
			String targetWar = build.getFinalName() + "." + mavenProject.getPackaging();

			retval = new Path(
				targetFolder
			).append(
				targetWar
			);
		}

		return retval;
	}

	private boolean _execMavenLaunch(
			IProject project, String goal, IMavenProjectFacade facade, IProgressMonitor monitor)
		throws CoreException {

		DebugPlugin debugPlugin = DebugPlugin.getDefault();

		ILaunchManager launchManager = debugPlugin.getLaunchManager();

		ILaunchConfigurationType launchConfigurationType = launchManager.getLaunchConfigurationType(
			_launchConfigurationTypeId);

		IPath basedirLocation = project.getLocation();

		String newName = launchManager.generateLaunchConfigurationName(basedirLocation.lastSegment());

		ILaunchConfigurationWorkingCopy workingCopy = launchConfigurationType.newInstance(null, newName);

		workingCopy.setAttribute(_attrGoals, goal);
		workingCopy.setAttribute(_attrPomDir, basedirLocation.toString());
		workingCopy.setAttribute(_attrSkipTests, Boolean.TRUE);
		workingCopy.setAttribute(_attrUpdateSnapshots, Boolean.TRUE);
		workingCopy.setAttribute(_attrWorkspaceResolution, Boolean.TRUE);
		workingCopy.setAttribute(
			IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-Dmaven.multiModuleProjectDirectory");

		if (facade != null) {
			ResolverConfiguration configuration = facade.getResolverConfiguration();

			String selectedProfiles = configuration.getSelectedProfiles();

			if ((selectedProfiles != null) && (selectedProfiles.length() > 0)) {
				workingCopy.setAttribute(_attrProfiles, selectedProfiles);
			}

			new LaunchHelper().launch(workingCopy, "run", monitor);

			return true;
		}

		return false;
	}

	private String _getMavenDeployGoals() {
		return "package war:war";
	}

	private boolean _isServiceBuilderProject(IProject project, String pluginType, MavenProject parentProject) {
		List<IFile> serviceXmls = new SearchFilesVisitor().searchFiles(project, "service.xml");

		if (ListUtil.isNotEmpty(serviceXmls) &&
			pluginType.equalsIgnoreCase(ILiferayMavenConstants.DEFAULT_PLUGIN_TYPE) && (parentProject != null)) {

			return true;
		}

		return false;
	}

	private boolean _runMavenGoal(IProject project, IProgressMonitor monitor) throws CoreException {
		boolean retval = false;

		IMavenProjectFacade facade = MavenUtil.getProjectFacade(project, monitor);

		String pluginType = MavenUtil.getLiferayMavenPluginType(facade.getMavenProject(monitor));

		if (pluginType == null) {
			pluginType = ILiferayMavenConstants.DEFAULT_PLUGIN_TYPE;
		}

		MavenProject mavenProject = facade.getMavenProject(monitor);

		MavenProject parentProject = mavenProject.getParent();

		String goal = _getMavenDeployGoals();

		if (_isServiceBuilderProject(project, pluginType, parentProject)) {
			retval = _execMavenLaunch(
				ProjectUtil.getProject(parentProject.getName()), " package -am -pl " + project.getName(),
				MavenUtil.getProjectFacade(project, monitor), monitor);
		}
		else {
			retval = _execMavenLaunch(project, goal, facade, monitor);
		}

		return retval;
	}

	private String _attrGoals = "M2_GOALS";
	private String _attrPomDir = IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY;
	private String _attrProfiles = "M2_PROFILES";
	private String _attrSkipTests = "M2_SKIP_TESTS";
	private String _attrUpdateSnapshots = "M2_UPDATE_SNAPSHOTS";
	private String _attrWorkspaceResolution = "M2_WORKSPACE_RESOLUTION";
	private String _launchConfigurationTypeId = "org.eclipse.m2e.Maven2LaunchConfigurationType";

}