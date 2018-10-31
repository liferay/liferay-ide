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

package com.liferay.ide.portlet.ui.editor.internal;

import com.liferay.ide.core.model.internal.GenericResourceBundlePathService;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.portlet.core.util.PortletUtil;
import com.liferay.ide.portlet.ui.PortletUIPlugin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.InvocationTargetException;

import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.forms.PropertyEditorActionHandler;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;

/**
 * @author Kamesh Sampath
 */
public abstract class AbstractResourceBundleActionHandler extends PropertyEditorActionHandler {

	public IWorkspaceRoot wroot = CoreUtil.getWorkspaceRoot();

	/**
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.sapphire.ui.SapphirePropertyEditorActionHandler#
	 * computeEnablementState()
	 */
	@Override
	protected boolean computeEnablementState() {
		boolean enabled = super.computeEnablementState();

		if (enabled) {
			Element element = getModelElement();
			Property property = property();
			IProject project = element.adapt(IProject.class);

			String rbFile = SapphireUtil.getText(element.property((ValueProperty)property.definition()));

			if (rbFile != null) {
				String ioFileName = PortletUtil.convertJavaToIoFileName(
					rbFile, GenericResourceBundlePathService.RB_FILE_EXTENSION);

				enabled = !getFileFromClasspath(project, ioFileName);
			}
			else {
				enabled = false;
			}
		}

		return enabled;
	}

	/**
	 * @param packageName
	 * @param rbFiles
	 * @param rbFileBuffer
	 */
	protected void createFiles(
		Presentation context, IProject project, String packageName, List<IFile> rbFiles, StringBuilder rbFileBuffer) {

		if (ListUtil.isEmpty(rbFiles)) {
			return;
		}

		int workUnit = rbFiles.size() + 2;

		IRunnableWithProgress rbCreationProc = new IRunnableWithProgress() {

			public void run(IProgressMonitor monitor) throws InterruptedException, InvocationTargetException {
				monitor.beginTask(StringPool.EMPTY, workUnit);

				try {
					IJavaProject javaProject = JavaCore.create(project);

					IPackageFragmentRoot pkgSrc = PortletUtil.getSourceFolder(javaProject);

					IPackageFragment rbPackageFragment = pkgSrc.getPackageFragment(packageName);

					if ((rbPackageFragment != null) && !rbPackageFragment.exists()) {
						pkgSrc.createPackageFragment(packageName, true, monitor);
					}

					monitor.worked(1);
					ListIterator<IFile> rbFilesIterator = rbFiles.listIterator();

					while (rbFilesIterator.hasNext()) {
						IFile rbFile = rbFilesIterator.next();

						try (InputStream inputStream =
								new ByteArrayInputStream(StringUtil.getBytes(rbFileBuffer.toString()))) {

							rbFile.create(inputStream, true, monitor);
						}
						catch (IOException ioe) {
							throw new CoreException(PortletUIPlugin.createErrorStatus(ioe));
						}

						monitor.worked(1);
					}

					project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
				}
				catch (CoreException ce) {
					PortletUIPlugin.logError(ce);
				}
				finally {
					monitor.done();
				}
			}

		};

		try {
			(new ProgressMonitorDialog(((SwtPresentation)context).shell())).run(false, false, rbCreationProc);

			rbFiles.clear();
		}
		catch (InvocationTargetException ite) {
			PortletUIPlugin.logError(ite);
		}
		catch (InterruptedException ie) {
			PortletUIPlugin.logError(ie);
		}
	}

	/**
	 * @param project
	 * @param ioFileName
	 * @return
	 */
	protected boolean getFileFromClasspath(IProject project, String ioFileName) {
		IClasspathEntry[] cpEntries = CoreUtil.getClasspathEntries(project);

		for (IClasspathEntry iClasspathEntry : cpEntries) {
			if (IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind()) {
				IFolder folder = wroot.getFolder(iClasspathEntry.getPath());

				IPath entryPath = folder.getLocation();

				entryPath = entryPath.append(ioFileName);

				IFile resourceBundleFile = wroot.getFileForLocation(entryPath);

				if (FileUtil.exists(resourceBundleFile)) {
					return true;
				}
				else {
					return false;
				}
			}
		}

		return false;
	}

	/**
	 * @param project
	 * @param ioFileName
	 * @return
	 */
	protected IFolder getResourceBundleFolderLocation(IProject project, String ioFileName) {
		IClasspathEntry[] cpEntries = CoreUtil.getClasspathEntries(project);

		for (IClasspathEntry iClasspathEntry : cpEntries) {
			if (IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind()) {
				IFolder srcFolder = wroot.getFolder(iClasspathEntry.getPath());

				IPath rbSourcePath = srcFolder.getLocation();

				rbSourcePath = rbSourcePath.append(ioFileName);

				IFile resourceBundleFile = wroot.getFileForLocation(rbSourcePath);

				if (resourceBundleFile != null) {
					return srcFolder;
				}
			}
		}

		return null;
	}

	protected Listener listener;

}