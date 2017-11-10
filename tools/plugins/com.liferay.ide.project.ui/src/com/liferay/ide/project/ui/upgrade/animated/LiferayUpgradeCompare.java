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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.project.ui.ProjectUI;

import java.io.File;
import java.io.InputStream;

import java.lang.reflect.InvocationTargetException;

import java.nio.file.Files;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IModificationDate;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Simon Jiang
 */
public class LiferayUpgradeCompare {

	public LiferayUpgradeCompare(final IPath soruceFile, final IPath targetFile, String fileName) {
		_soruceFile = soruceFile;
		_targetFile = targetFile;
		_fileName = fileName;
	}

	public void openCompareEditor() {
		try {
			final ITypedElement left = new CompareItem(getSourceFile(), _fileName);
			final ITypedElement right = new CompareItem(getTargetFile(), _fileName + "_preview");

			final CompareConfiguration configuration = new CompareConfiguration();

			configuration.setLeftLabel("Original File");
			configuration.setRightLabel("Upgraded File");

			CompareEditorInput editorInput = new CompareEditorInput(configuration) {

				@Override
				protected Object prepareInput(final IProgressMonitor monitor)
					throws InterruptedException, InvocationTargetException {

					DiffNode diffNode = new DiffNode(left, right);

					return diffNode;
				}

			};

			StringBuilder titleBuilder = new StringBuilder("Compare ('");

			titleBuilder.append(_fileName);
			titleBuilder.append("'-'");
			titleBuilder.append(_fileName);
			titleBuilder.append("_preview");
			titleBuilder.append("')");

			editorInput.setTitle(titleBuilder.toString());

			CompareUI.openCompareEditor(editorInput);
		}
		catch (Exception e) {
			ProjectUI.logError(e);
		}
	}

	protected File getSourceFile() {
		return _soruceFile.toFile();
	}

	protected File getTargetFile() {
		return _targetFile.toFile();
	}

	private String _fileName;
	private final IPath _soruceFile;
	private final IPath _targetFile;

	private class CompareItem implements ITypedElement, IStreamContentAccessor, IModificationDate, IEditableContent {

		public CompareItem(File file, String name) {
			_file = file;
			_name = name;
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
			return _name;
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
		private String _name;

	}

}