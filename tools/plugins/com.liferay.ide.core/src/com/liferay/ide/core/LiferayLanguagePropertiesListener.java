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

package com.liferay.ide.core;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.PropertiesUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Kuo Zhang
 */
public class LiferayLanguagePropertiesListener implements IResourceChangeListener, IResourceDeltaVisitor {

	public LiferayLanguagePropertiesListener() {
		new WorkspaceJob("clear abondoned liferay language properties markers") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				LiferayLanguagePropertiesValidator.clearAbandonedMarkers();

				return Status.OK_STATUS;
			}

		}.schedule();
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event == null) {
			return;
		}

		try {
			event.getDelta().accept(this);
		}
		catch (CoreException ce) {
		}
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		switch (delta.getResource().getType()) {
			case IResource.ROOT:
			case IResource.PROJECT:
			case IResource.FOLDER:
				return true;

			case IResource.FILE:
				processFile((IFile)delta.getResource());

				return false;
		}

		return false;
	}

	protected void processFile(IFile file) throws CoreException {
		if (FileUtil.notExists(file)) {
			return;
		}

		if (PropertiesUtil.isLanguagePropertiesFile(file)) {
			_validateLanguagePropertiesEncoding(new IFile[] {file}, null);

			return;
		}

		String filename = file.getName();

		if (filename.equals(ILiferayConstants.PORTLET_XML_FILE)) {
			IWebProject webProject = LiferayCore.create(IWebProject.class, CoreUtil.getLiferayProject(file));

			if (webProject != null) {
				IFile portletXml = webProject.getDescriptorFile(ILiferayConstants.PORTLET_XML_FILE);

				if ((portletXml != null) && file.equals(portletXml)) {
					IFile[] files = PropertiesUtil.getLanguagePropertiesFromPortletXml(portletXml);

					_validateLanguagePropertiesEncoding(files, CoreUtil.getLiferayProject(file));

					return;
				}
			}
		}

		if (filename.equals(ILiferayConstants.LIFERAY_HOOK_XML_FILE)) {
			IWebProject webProject = LiferayCore.create(IWebProject.class, CoreUtil.getLiferayProject(file));

			if (webProject != null) {
				IFile liferayHookXml = webProject.getDescriptorFile(ILiferayConstants.LIFERAY_HOOK_XML_FILE);

				if (file.equals(liferayHookXml)) {
					IFile[] files = PropertiesUtil.getLanguagePropertiesFromLiferayHookXml(liferayHookXml);

					_validateLanguagePropertiesEncoding(files, CoreUtil.getLiferayProject(file));

					return;
				}
			}
		}
	}

	private void _validateLanguagePropertiesEncoding(IFile[] files, IProject project) {
		Job job = new WorkspaceJob("Validate Liferay language properties encoding...") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				if (ListUtil.isNotEmpty(files)) {
					for (IFile file : files) {
						LiferayLanguagePropertiesValidator.getValidator(file).validateEncoding();
					}
				}

				if (project != null) {
					LiferayLanguagePropertiesValidator.clearUnusedValidatorsAndMarkers(project);
				}

				return Status.OK_STATUS;
			}

		};

		job.setRule(CoreUtil.getWorkspaceRoot());

		job.schedule();
	}

}