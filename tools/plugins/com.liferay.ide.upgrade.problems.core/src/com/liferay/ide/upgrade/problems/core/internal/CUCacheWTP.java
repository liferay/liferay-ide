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

package com.liferay.ide.upgrade.problems.core.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.upgrade.problems.core.CUCache;
import com.liferay.ide.upgrade.problems.core.FileMigration;

import java.io.File;
import java.io.InputStream;

import java.lang.ref.WeakReference;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslator;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@Component(property = "type=jsp", service = CUCache.class)
@SuppressWarnings("restriction")
public class CUCacheWTP implements CUCache<JSPTranslationPrime> {

	@Override
	public JSPTranslationPrime getCU(File file, Supplier<char[]> javavSource) {
		JSPTranslationPrime retval = null;

		try {
			synchronized (_lock) {
				Long lastModified = _fileModifiedTimeMap.get(file);

				if ((lastModified != null) && lastModified.equals(file.lastModified())) {
					WeakReference<JSPTranslationPrime> reference = _jspTranslationMap.get(file);

					retval = reference.get();
				}

				if (retval == null) {
					JSPTranslationPrime newTranslation = _createJSPTranslation(file);

					_fileModifiedTimeMap.put(file, file.lastModified());
					_jspTranslationMap.put(file, new WeakReference<JSPTranslationPrime>(newTranslation));

					retval = newTranslation;
				}
			}
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		return retval;
	}

	@Override
	public void unget(File file) {
		synchronized (_lock) {
			_fileModifiedTimeMap.remove(file);
			_jspTranslationMap.remove(file);
		}
	}

	private void _addNaturesToProject(IProject proj, String[] natureIds, IProgressMonitor monitor)
		throws CoreException {

		IProjectDescription description = proj.getDescription();

		String[] prevNatures = description.getNatureIds();

		String[] newNatures = new String[prevNatures.length + natureIds.length];

		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);

		for (int i = prevNatures.length; i < newNatures.length; i++) {
			newNatures[i] = natureIds[i - prevNatures.length];
		}

		description.setNatureIds(newNatures);

		proj.setDescription(description, monitor);
	}

	@SuppressWarnings("deprecation")
	private JSPTranslationPrime _createJSPTranslation(File file) {
		IDOMModel jspModel = null;

		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();

			try (InputStream input = Files.newInputStream(Paths.get(file.toURI()), StandardOpenOption.READ)) {
				jspModel = (IDOMModel)modelManager.getModelForRead(file.getAbsolutePath(), input, null);
			}

			IDOMDocument domDocument = jspModel.getDocument();

			IDOMNode domNode = (IDOMNode)domDocument.getDocumentElement();

			IProgressMonitor npm = new NullProgressMonitor();
			JSPTranslator translator = new JSPTranslatorPrime();

			if (domNode != null) {
				translator.reset((IDOMNode)domDocument.getDocumentElement(), npm);
			}
			else {
				translator.reset((IDOMNode)domDocument.getFirstChild(), npm);
			}

			translator.translate();

			IProject project = CoreUtil.getProject(file);

			IJavaProject javaProject = null;

			if (project != null) {
				javaProject = _getJavaProject(project.getName());
			}
			else {
				javaProject = _getJavaProject(FileMigration.HELPER_PROJECT_NAME);
			}

			return new JSPTranslationPrime(javaProject, translator, file);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (jspModel != null) {
				jspModel.releaseFromRead();
			}
		}

		return null;
	}

	private IJavaProject _getJavaProject(String projectName) throws CoreException {
		IProject javaProject = CoreUtil.getProject(projectName);

		IProgressMonitor monitor = new NullProgressMonitor();

		if (!javaProject.exists()) {
			IWorkspace workspace = CoreUtil.getWorkspace();

			IProjectDescription description = workspace.newProjectDescription(projectName);

			javaProject.create(monitor);
			javaProject.open(monitor);
			javaProject.setDescription(description, monitor);
		}

		javaProject.open(monitor);

		_addNaturesToProject(javaProject, new String[] {JavaCore.NATURE_ID}, monitor);

		return JavaCore.create(javaProject);
	}

	private static final Map<File, Long> _fileModifiedTimeMap = new HashMap<>();
	private static final Map<File, WeakReference<JSPTranslationPrime>> _jspTranslationMap = new HashMap<>();
	private static final Object _lock = new Object();

}