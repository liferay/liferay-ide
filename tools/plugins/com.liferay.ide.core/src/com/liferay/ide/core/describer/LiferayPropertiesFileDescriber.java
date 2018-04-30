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

package com.liferay.ide.core.describer;

import com.liferay.ide.core.util.CoreUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import java.lang.reflect.Field;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.ITextContentDescriber;

/**
 * @author Kuo Zhang
 * @author Gregory Amerson
 */
@SuppressWarnings({"restriction", "rawtypes"})
public abstract class LiferayPropertiesFileDescriber implements ITextContentDescriber {

	public LiferayPropertiesFileDescriber() {
	}

	public int describe(InputStream contents, IContentDescription description) throws IOException {
		try {
			Class<?> contentClass = contents.getClass();

			Field inputStreamField = contentClass.getDeclaredField("in");

			inputStreamField.setAccessible(true);

			try(InputStream inputStream = (InputStream)inputStreamField.get(contents)){
				Class<?> clazz = inputStream.getClass();

				try {
					Field fileStoreField = clazz.getDeclaredField("target");

					fileStoreField.setAccessible(true);

					IFileStore fileStore = (IFileStore)fileStoreField.get(inputStream);

					if (fileStore == null) {
						return INVALID;
					}

					IFile file = CoreUtil.getWorkspaceRoot().getFileForLocation(FileUtil.toPath(fileStore.toURI()));

					if (isPropertiesFile(file)) {
						return VALID;
					}
				}
				catch (Exception e) {
					Field pathField = clazz.getDeclaredField("path");

					pathField.setAccessible(true);

					String path = (String)pathField.get(inputStream);

					if (isPropertiesFile(path)) {
						return VALID;
					}
				}
			}
		}
		catch (Exception e) {

			// ignore errors

		}

		return INVALID;
	}

	public int describe(Reader contents, IContentDescription description) throws IOException {
		try {
			Class<?> contentClass = contents.getClass();

			Field documentReaderField = contentClass.getDeclaredField("in");

			documentReaderField.setAccessible(true);

			Object documentReader = documentReaderField.get(contents);

			Class<?> documentReaderClass = documentReader.getClass();

			Field fDocumentField = documentReaderClass.getDeclaredField("fDocument");

			fDocumentField.setAccessible(true);

			Object fDocument = fDocumentField.get(documentReader);

			Class<?> documentClass = fDocument.getClass();

			Class<?> clazz = documentClass.getSuperclass();

			Field fDocumentListenersField = clazz.getSuperclass().getDeclaredField("fDocumentListeners");

			fDocumentListenersField.setAccessible(true);

			ListenerList fDocumentListeners = (ListenerList)fDocumentListenersField.get(fDocument);

			Object[] listeners = fDocumentListeners.getListeners();

			for (Object listener : listeners) {
				try {
					Class<?> listenerClass = listener.getClass();

					Class<?> superClass = listenerClass.getEnclosingClass().getSuperclass();

					Field fFileField = superClass.getDeclaredField("fFile");

					fFileField.setAccessible(true);

					// get enclosing instance of listener

					Field thisField = listenerClass.getDeclaredField("this$0");

					thisField.setAccessible(true);

					Object enclosingObject = thisField.get(listener);

					Object fFile = fFileField.get(enclosingObject);

					if (fFile instanceof IFile) {
						IFile file = (IFile)fFile;

						if (isPropertiesFile(file)) {
							return VALID;
						}
					}
				}
				catch (Exception e) {
				}
			}
		}
		catch (Exception e) {

			// ignore errors

		}

		return INVALID;
	}

	public QualifiedName[] getSupportedOptions() {
		return null;
	}

	protected abstract boolean isPropertiesFile(Object file);

}