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

package com.liferay.ide.alloy.core.jsp;

import com.liferay.ide.core.LiferayNature;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import java.lang.reflect.Field;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;
import org.eclipse.jst.jsp.core.internal.contenttype.ContentDescriberForJSP;
import org.eclipse.jst.jsp.core.internal.provisional.contenttype.IContentDescriptionForJSP;
import org.eclipse.wst.sse.core.internal.encoding.IContentDescriptionExtended;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class AlloyContentDescriberForJSP implements ITextContentDescriber {

	@Override
	public int describe(InputStream contents, IContentDescription description) throws IOException {
		int retval = new ContentDescriberForJSP().describe(contents, description);

		try {
			Class<?> contentsClass = contents.getClass();

			Field inputStreamField = contentsClass.getDeclaredField("in");

			inputStreamField.setAccessible(true);

			try (InputStream inputStream = (InputStream)inputStreamField.get(contents)) {
				Class<?> ipStream = inputStream.getClass();

				Field fileStoreField = ipStream.getDeclaredField("target");

				fileStoreField.setAccessible(true);

				IFileStore fileStore = (IFileStore)fileStoreField.get(inputStream);

				if (fileStore != null) {
					IFile[] files = CoreUtil.getWorkspaceRoot().findFilesForLocationURI(fileStore.toURI());

					for (IFile file : files) {
						if (ProjectUtil.isPortletProject(file.getProject()) ||
							LiferayNature.hasNature(file.getProject())) {

							return VALID;
						}
					}
				}
			}
		}
		catch (Exception e) {
		}

		return retval;
	}

	@Override
	public int describe(Reader contents, IContentDescription description) throws IOException {
		int retval = new ContentDescriberForJSP().describe(contents, description);

		try {
			Class<?> reader = contents.getClass();

			Field documentReaderField = reader.getDeclaredField("in");

			documentReaderField.setAccessible(true);

			Object documentReader = documentReaderField.get(contents);

			Class<?> docReader = documentReader.getClass();

			Field fDocumentField = docReader.getDeclaredField("fDocument");

			fDocumentField.setAccessible(true);

			Object fDocument = fDocumentField.get(documentReader);

			Class<?> documentClass = fDocument.getClass();

			Class<?> documentSuperClass = documentClass.getSuperclass();

			Class<?> superDocument = documentSuperClass.getSuperclass();

			Field fDocumentListenersField = superDocument.getDeclaredField("fDocumentListeners");

			fDocumentListenersField.setAccessible(true);

			ListenerList<?> fDocumentListeners = (ListenerList<?>)fDocumentListenersField.get(fDocument);

			Object[] listeners = fDocumentListeners.getListeners();

			for (Object listener : listeners) {
				try {
					Class<?> listenerClass = listener.getClass();

					Class<?> enclosingClass = listenerClass.getEnclosingClass();

					Class<?> superClass = enclosingClass.getSuperclass();

					Field fFileField = superClass.getDeclaredField("fFile");

					fFileField.setAccessible(true);

					// get enclosing instance of listener

					Field thisField = listenerClass.getDeclaredField("this$0");

					thisField.setAccessible(true);

					Object enclosingObject = thisField.get(listener);

					Object fFile = fFileField.get(enclosingObject);

					if (fFile instanceof IFile) {
						IFile file = (IFile)fFile;

						if (ProjectUtil.isPortletProject(file.getProject()) ||
							LiferayNature.hasNature(file.getProject())) {

							return VALID;
						}
					}
				}
				catch (Exception e) {
				}
			}
		}
		catch (Exception e) {
		}

		return retval;
	}

	@Override
	public QualifiedName[] getSupportedOptions() {
		return _SUPPORTED_OPTIONS;
	}

	private static final QualifiedName[] _SUPPORTED_OPTIONS = {
		IContentDescription.CHARSET, IContentDescription.BYTE_ORDER_MARK, IContentDescriptionExtended.DETECTED_CHARSET,
		IContentDescriptionExtended.UNSUPPORTED_CHARSET, IContentDescriptionExtended.APPROPRIATE_DEFAULT,
		IContentDescriptionForJSP.CONTENT_TYPE_ATTRIBUTE, IContentDescriptionForJSP.LANGUAGE_ATTRIBUTE,
		IContentDescriptionForJSP.CONTENT_FAMILY_ATTRIBUTE
	};

}