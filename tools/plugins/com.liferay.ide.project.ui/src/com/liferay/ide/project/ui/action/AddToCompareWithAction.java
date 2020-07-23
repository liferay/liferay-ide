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
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.action.AbstractObjectAction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.InvocationTargetException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Enumeration;
import java.util.List;
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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Ethan Sun
 */
public class AddToCompareWithAction extends AbstractObjectAction {

	@Override
	public void run(IAction action) {
		if (fSelection instanceof IStructuredSelection) {
			Object selectedObject = ((IStructuredSelection)fSelection).getFirstElement();

			IFile targetFile = null;

			if (selectedObject instanceof IFile) {
				targetFile = (IFile)selectedObject;
			}

			IProject project = targetFile.getProject();

			IPath projectRelativePath = targetFile.getProjectRelativePath();

			ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, project);

			IProjectBuilder projectBuilder = liferayProject.adapt(IProjectBuilder.class);

			if (projectBuilder == null) {
				ProjectCore.logWarning("Please wait for synchronized jobs to finish.");

				return;
			}

			List<Artifact> dependencies = projectBuilder.getDependencies("originalModule");

			if (!dependencies.isEmpty()) {
				Artifact artifact = dependencies.get(0);

				File sourceFile = artifact.getSource();

				if (FileUtil.exists(sourceFile) && Objects.nonNull(targetFile)) {
					try (ZipFile zipFile = new ZipFile(sourceFile)) {
						ZipEntry entry = null;

						Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

						while (enumeration.hasMoreElements()) {
							entry = enumeration.nextElement();

							String entryCanonicalName = entry.getName();

							String relativePath = projectRelativePath.toString();

							if (!entry.isDirectory() && relativePath.contains(entryCanonicalName)) {
								String entryName = entryCanonicalName.substring(
									entryCanonicalName.lastIndexOf("/") + 1);

								Path sourceEntryPath = Files.createTempFile(entryName, ".tmp");

								File sourceEntryFile = sourceEntryPath.toFile();

								FileUtils.copyInputStreamToFile(zipFile.getInputStream(entry), sourceEntryFile);

								CompareItem sourceCompareItem = new CompareItem(sourceEntryFile);

								CompareItem targetCompareItem = new CompareItem(FileUtil.getFile(targetFile));

								CompareConfiguration compareConfiguration = new CompareConfiguration();

								compareConfiguration.setLeftLabel("Updated File");
								compareConfiguration.setRightLabel("Original File");

								CompareEditorInput compareEditorInput = new CompareEditorInput(compareConfiguration) {

									public Viewer findStructureViewer(
										Viewer oldViewer, ICompareInput input, Composite parent) {

										return null;
									}

									@Override
									protected Object prepareInput(IProgressMonitor monitor)
										throws InterruptedException, InvocationTargetException {

										return new DiffNode(targetCompareItem, sourceCompareItem);
									}

								};

								compareEditorInput.setTitle(
									"Compare ('" + targetFile.getName() + "'-'" + sourceFile.getName() + "')");

								CompareUI.openCompareEditor(compareEditorInput);
							}
						}
					}
					catch (IOException ioe) {
						ProjectUI.logError(
							"Failed to compare with original file for project " + project.getName(), ioe);
					}
				}
			}
		}
	}

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