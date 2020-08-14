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

package com.liferay.ide.project.ui.action;

import com.liferay.ide.core.Artifact;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IProjectBuilder;
import com.liferay.ide.core.IWorkspaceProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.core.portal.PortalBundle;
import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.action.AbstractObjectAction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.InvocationTargetException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Ethan Sun
 */
public class CompareOriginalImplementationAction extends AbstractObjectAction {

	@Override
	public void run(IAction action) {
		try (ZipFile zipFile = new ZipFile(_sourceFile)) {
			String entryCanonicalName = _sourceEntry.getName();

			String entryName = entryCanonicalName.substring(entryCanonicalName.lastIndexOf("/") + 1);

			Path sourceEntryPath = Files.createTempFile(entryName, ".tmp");

			File sourceEntryFile = sourceEntryPath.toFile();

			FileUtils.copyInputStreamToFile(zipFile.getInputStream(_sourceEntry), sourceEntryFile);

			CompareItem sourceCompareItem = new CompareItem(sourceEntryFile);

			CompareItem targetCompareItem = new CompareItem(FileUtil.getFile(_selectedFile));

			CompareConfiguration compareConfiguration = new CompareConfiguration();

			compareConfiguration.setLeftLabel("Updated File");

			compareConfiguration.setRightLabel("Original File");

			CompareEditorInput compareEditorInput = new CompareEditorInput(compareConfiguration) {

				public Viewer findStructureViewer(Viewer oldViewer, ICompareInput input, Composite parent) {
					return null;
				}

				@Override
				protected Object prepareInput(IProgressMonitor monitor)
					throws InterruptedException, InvocationTargetException {

					return new DiffNode(targetCompareItem, sourceCompareItem);
				}

			};

			compareEditorInput.setTitle("Compare ('" + _selectedFile.getName() + "'-'" + _sourceFile.getName() + "')");

			CompareUI.openCompareEditor(compareEditorInput);
		}
		catch (IOException ioe) {
			ProjectUI.logError("Unable to open compare editor.", ioe);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (!selection.isEmpty() && (selection instanceof IStructuredSelection)) {
			IStructuredSelection structureSelection = (IStructuredSelection)selection;

			Object obj = structureSelection.getFirstElement();

			if (obj instanceof IFile) {
				_selectedFile = (IFile)obj;

				IPath projectRelativePath = _selectedFile.getProjectRelativePath();

				String projectRelativePathStr = projectRelativePath.toString();

				if (!projectRelativePathStr.startsWith("src/main/")) {
					action.setEnabled(false);

					return;
				}

				_project = _selectedFile.getProject();

				try {
					if (ProjectUtil.isFragmentProject(_project)) {
						Map<String, String> fragmentProjectInfo = ProjectUtil.getFragmentProjectInfo(_project);

						String hostBundleName = fragmentProjectInfo.get("HostOSGiBundleName");

						String portalBundleVersion = fragmentProjectInfo.get("Portal-Bundle-Version");

						if (Objects.isNull(portalBundleVersion) || Objects.isNull(portalBundleVersion)) {
							action.setEnabled(false);

							return;
						}

						ProjectCore projectCore = ProjectCore.getDefault();

						IPath projectCoreLocation = projectCore.getStateLocation();

						String hostOsgiJar = hostBundleName + ".jar";

						IWorkspaceProject liferayWorkspaceProject = LiferayWorkspaceUtil.getLiferayWorkspaceProject();

						if (liferayWorkspaceProject != null) {
							IPath bundleHomePath = LiferayWorkspaceUtil.getBundleHomePath(
								liferayWorkspaceProject.getProject());

							if (Objects.isNull(bundleHomePath)) {
								action.setEnabled(false);

								return;
							}

							PortalBundle portalBundle = LiferayServerCore.newPortalBundle(bundleHomePath);

							if (Objects.isNull(portalBundle)) {
								action.setEnabled(false);

								return;
							}

							IRuntime fragmentRuntime = null;

							IRuntime[] runtimes = ServerCore.getRuntimes();

							for (IRuntime runtime : runtimes) {
								PortalRuntime portalRuntime = (PortalRuntime)runtime.loadAdapter(
									PortalRuntime.class, new NullProgressMonitor());

								if (Objects.equals(portalBundleVersion, portalRuntime.getPortalVersion()) &&
									Objects.equals(runtime.getLocation(), portalBundle.getLiferayHome())) {

									fragmentRuntime = runtime;

									_sourceFile = ServerUtil.getModuleFileFrom70Server(
										fragmentRuntime, hostOsgiJar, projectCoreLocation);

									break;
								}
							}
						}
					}
					else if (ProjectUtil.isModuleExtProject(_project)) {
						ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, _project);

						IProjectBuilder projectBuilder = liferayProject.adapt(IProjectBuilder.class);

						if (projectBuilder == null) {
							ProjectCore.logWarning("Please wait for synchronized jobs to finish.");
							action.setEnabled(false);

							return;
						}

						List<Artifact> dependencies = projectBuilder.getDependencies("originalModule");

						if (!dependencies.isEmpty()) {
							Artifact artifact = dependencies.get(0);

							_sourceFile = artifact.getSource();
						}
					}
				}
				catch (Exception e) {
					action.setEnabled(false);

					return;
				}

				_sourceEntry = _searchTargetFile(_sourceFile);

				if (Objects.isNull(_sourceEntry)) {
					action.setEnabled(false);

					return;
				}

				action.setEnabled(true);
			}
		}
	}

	private ZipEntry _searchTargetFile(File sourceFile) {
		ZipEntry targetEntry = null;

		if (FileUtil.exists(sourceFile)) {
			try (ZipFile zipFile = new ZipFile(sourceFile)) {
				Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

				while (enumeration.hasMoreElements()) {
					ZipEntry entry = enumeration.nextElement();

					String entryCanonicalName = entry.getName();

					IPath relativePath = _selectedFile.getProjectRelativePath();

					if (!entry.isDirectory() && StringUtil.contains(relativePath.toString(), entryCanonicalName)) {
						targetEntry = entry;

						break;
					}
				}
			}
			catch (IOException ioe) {
				ProjectUI.logError("Failed to compare with original file for project " + _project.getName(), ioe);
			}
		}

		return targetEntry;
	}

	private IProject _project;
	private IFile _selectedFile;
	private ZipEntry _sourceEntry;
	private File _sourceFile;

	private class CompareItem implements ITypedElement, IStreamContentAccessor, IModificationDate, IEditableContent {

		public CompareItem(File file) {
			_file = file;
		}

		@Override
		public InputStream getContents() throws CoreException {
			try {
				return Files.newInputStream(_file.toPath());
			}
			catch (Exception e) {
			}

			return null;
		}

		@Override
		public Image getImage() {
			return null;
		}

		@Override
		public long getModificationDate() {
			return 0;
		}

		@Override
		public String getName() {
			return _file.getName();
		}

		@Override
		public String getType() {
			return TEXT_TYPE;
		}

		@Override
		public boolean isEditable() {
			return false;
		}

		@Override
		public ITypedElement replace(ITypedElement dest, ITypedElement src) {
			return null;
		}

		@Override
		public void setContent(byte[] newContent) {
		}

		private File _file;

	}

}