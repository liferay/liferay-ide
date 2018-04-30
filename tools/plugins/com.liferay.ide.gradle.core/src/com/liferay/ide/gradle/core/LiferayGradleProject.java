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

package com.liferay.ide.gradle.core;

import aQute.bnd.osgi.Jar;

import com.liferay.blade.gradle.model.CustomModel;
import com.liferay.ide.core.BaseLiferayProject;
import com.liferay.ide.core.IBundleProject;
import com.liferay.ide.core.IResourceBundleProject;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.PropertiesUtil;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Andy Wu
 */
public class LiferayGradleProject extends BaseLiferayProject implements IBundleProject, IResourceBundleProject {

	public LiferayGradleProject(IProject project) {
		super(project);
	}

	@Override
	public <T> T adapt(Class<T> adapterType) {
		T adapter = super.adapt(adapterType);

		if (adapter != null) {
			return adapter;
		}

		if (IProjectBuilder.class.equals(adapterType)) {
			final IProjectBuilder projectBuilder = new GradleProjectBuilder(getProject());

			return adapterType.cast(projectBuilder);
		}

		return null;
	}

	@Override
	public boolean filterResource(IPath resourcePath) {
		if (filterResource(resourcePath, _ignorePaths)) {
			return true;
		}

		return false;
	}

	@Override
	public String getBundleShape() {
		return "jar";
	}

	@Override
	public List<IFile> getDefaultLanguageProperties() {
		return PropertiesUtil.getDefaultLanguagePropertiesFromModuleProject(getProject());
	}

	@Override
	public IPath getOutputBundle(boolean cleanBuild, IProgressMonitor monitor) throws CoreException {
		ProjectConnection connection = null;

		try {
			IPath projectLocation = getProject().getLocation();

			File projectFile = projectLocation.toFile();

			GradleConnector connector = GradleConnector.newConnector().forProjectDirectory(projectFile);

			connection = connector.connect();

			BuildLauncher launcher = connection.newBuild();

			BlockingResultHandler<Object> handler = new BlockingResultHandler<>(Object.class);

			if (cleanBuild) {
				launcher.forTasks("clean", "assemble").run(handler);
			}
			else {
				launcher.forTasks("assemble").run(handler);
			}

			handler.getResult();
		}
		catch (Exception e) {
			GradleCore.logError("Project " + getProject().getName() + " build output error", e);

			return null;
		}
		finally {
			if (connection != null) {
				connection.close();
			}
		}

		IPath outputBundlePath = getOutputBundlePath();

		if (FileUtil.exists(outputBundlePath)) {
			return outputBundlePath;
		}

		return null;
	}

	@Override
	public IPath getOutputBundlePath() {
		IProject gradleProject = getProject();

		IPath buildLocation = gradleProject.getLocation().append("build/libs");

		File buildFolder = buildLocation.toFile();

		String[] fileNames = buildFolder.list();

		// find the only file

		if ((fileNames != null) && (fileNames.length == 1)) {
			File outputFile = new File(buildFolder, fileNames[0]);

			return new Path(outputFile.getAbsolutePath());
		}
		else {
			IPath gradleProjectLocation = gradleProject.getLocation();

			File nodeThemeOutput = gradleProjectLocation.append("dist/" + gradleProject.getName() + ".war").toFile();

			if (FileUtil.exists(nodeThemeOutput)) {
				return new Path(nodeThemeOutput.getAbsolutePath());
			}
			else {

				// using CustomModel if can't find output

				IPath retval = null;

				CustomModel model = GradleCore.getToolingModel(CustomModel.class, gradleProject);

				Set<File> outputFiles = model.getOutputFiles();

				if (ListUtil.isNotEmpty(outputFiles)) {

					// first check to see if there are any outputfiles that are wars, if so use that
					// one.

					File bundleFile = null;

					for (File outputFile : outputFiles) {
						if (outputFile.getName().endsWith(".war")) {
							bundleFile = outputFile;
							break;
						}
					}

					if (bundleFile == null) {
						for (File outputFile : outputFiles) {
							final String name = outputFile.getName();

							if (name.endsWith("javadoc.jar") || name.endsWith("jspc.jar") ||
								name.endsWith("sources.jar")) {

								continue;
							}

							if (name.endsWith(".jar")) {
								bundleFile = outputFile;

								break;
							}
						}
					}

					if (bundleFile != null) {
						retval = new Path(bundleFile.getAbsolutePath());
					}
				}
				else if (model.hasPlugin("com.liferay.gradle.plugins.gulp.GulpPlugin")) {
					retval = gradleProject.getLocation().append("dist/" + gradleProject.getName() + ".war");
				}

				return retval;
			}
		}
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return null;
	}

	@Override
	public IFolder getSourceFolder(String classification) {
		IFolder retval = null;
		IFolder[] sourceFolders = getSourceFolders();

		for (IFolder folder : sourceFolders) {
			if (folder.getName().equals(classification)) {
				retval = folder;

				break;
			}
		}

		if (classification.equals("resources")) {
			if (retval == null) {
				retval = _createResorcesFolder(getProject());
			}
		}

		return retval;
	}

	@Override
	public IFolder[] getSourceFolders() {
		IFile gulpFile = getProject().getFile("gulpfile.js");

		if (FileUtil.exists(gulpFile)) {
			return new IFolder[] {getProject().getFolder("src")};
		}

		return super.getSourceFolders();
	}

	@Override
	public String getSymbolicName() throws CoreException {
		String bsn = ProjectUtil.getBundleSymbolicNameFromBND(getProject());

		if (!CoreUtil.empty(bsn)) {
			return bsn;
		}

		String retval = null;

		IPath outputBundle = getOutputBundlePath();

		if ((outputBundle == null) || outputBundle.lastSegment().endsWith(".war")) {
			return getProject().getName();
		}
		else if (FileUtil.exists(outputBundle)) {
			try (final Jar jar = new Jar(outputBundle.toFile())) {
				retval = jar.getBsn();
			}
			catch (Exception e) {
			}
		}

		return retval;
	}

	@Override
	public boolean isFragmentBundle() {
		IFile bndFile = getProject().getFile("bnd.bnd");

		if (FileUtil.notExists(bndFile)) {
			return false;
		}

		try(InputStream inputStream = bndFile.getContents()) {

			String content = FileUtil.readContents(inputStream);

			if (content.contains("Fragment-Host")) {
				return true;
			}
		}
		catch (Exception e) {
		}

		return false;
	}

	private IFolder _createResorcesFolder(IProject project) {
		try {
			IJavaProject javaProject = JavaCore.create(project);

			List<IClasspathEntry> existingRawClasspath;

			existingRawClasspath = Arrays.asList(javaProject.getRawClasspath());

			List<IClasspathEntry> newRawClasspath = new ArrayList<>();

			IClasspathAttribute[] attributes = {JavaCore.newClasspathAttribute("FROM_GRADLE_MODEL", "true")};

			IClasspathEntry resourcesEntry = JavaCore.newSourceEntry(
				project.getFullPath().append("src/main/resources"), new IPath[0], new IPath[0], null, attributes);

			for (IClasspathEntry entry : existingRawClasspath) {
				newRawClasspath.add(entry);
			}

			if (!existingRawClasspath.contains(resourcesEntry)) {
				newRawClasspath.add(resourcesEntry);
			}

			javaProject.setRawClasspath(newRawClasspath.toArray(new IClasspathEntry[0]), new NullProgressMonitor());

			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

			IFolder[] sourceFolders = getSourceFolders();

			for (IFolder folder : sourceFolders) {
				if (folder.getName().equals("resources")) {
					return folder;
				}
			}
		}
		catch (CoreException ce) {
			GradleCore.logError(ce);
		}

		return null;
	}

	private static final String[] _ignorePaths = {".gradle", "build", "dist", "liferay-theme.json"};

}