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

package com.liferay.ide.upgrade.plan.tasks.core.internal.problem.eclipse.provider;

import com.liferay.ide.upgrade.plan.tasks.core.problem.api.CUCache;

import java.io.File;

import java.lang.ref.WeakReference;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.eclipse.core.resources.IFile;
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
 */
@Component(property = "type=jsp", service = CUCache.class)
@SuppressWarnings("restriction")
public class CUCacheWTP extends BaseCUCache implements CUCache<JSPTranslationPrime> {

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

	private JSPTranslationPrime _createJSPTranslation(File file) {
		IDOMModel jspModel = null;

		try {

			// try to find the file in the current workspace, if it can't find
			// it then fall back to copy

			IFile jspFile = getIFile(file);

			IModelManager modelManager = StructuredModelManager.getModelManager();

			jspModel = (IDOMModel)modelManager.getModelForRead(jspFile);

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

			IJavaProject javaProject = JavaCore.create(jspFile.getProject());

			return new JSPTranslationPrime(javaProject, translator, jspFile);
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

	private static final Map<File, Long> _fileModifiedTimeMap = new HashMap<>();
	private static final Map<File, WeakReference<JSPTranslationPrime>> _jspTranslationMap = new HashMap<>();
	private static final Object _lock = new Object();

}