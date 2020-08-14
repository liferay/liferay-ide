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

package com.liferay.ide.project.ui.handlers;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.ProjectCore;

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
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Lovett Li
 * @author Terry Jia
 */
public abstract class AbstractCompareFileHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);

		IStatus retval = Status.OK_STATUS;

		IFile currentFile = null;

		if (selection instanceof ITreeSelection) {
			ITreeSelection treeSelection = (ITreeSelection)selection;

			Object firstElement = treeSelection.getFirstElement();

			if (firstElement instanceof IFile) {
				currentFile = (IFile)firstElement;
			}
		}
		else if (selection instanceof TextSelection) {
			IWorkbenchPage activePage = window.getActivePage();

			IEditorPart editor = activePage.getActiveEditor();

			IEditorInput editorInput = editor.getEditorInput();

			currentFile = editorInput.getAdapter(IFile.class);
		}

		retval = _openCompareEditor(currentFile);

		return retval;
	}

	protected abstract File getTemplateFile(IFile currentFile) throws Exception;

	private IStatus _openCompareEditor(IFile currentFile) {
		ITypedElement left = null;
		ITypedElement right = null;
		IStatus retval = Status.OK_STATUS;

		try {
			File tempFile = getTemplateFile(currentFile);

			if (tempFile == null) {
				return ProjectCore.createErrorStatus("Can not find the original file.");
			}

			left = new CompareItem(tempFile);
			right = new CompareItem(FileUtil.getFile(currentFile));

			_openInCompare(left, right);
		}
		catch (Exception e) {
			retval = ProjectCore.createErrorStatus(e);
		}

		return retval;
	}

	private void _openInCompare(final ITypedElement left, final ITypedElement right) {
		final CompareConfiguration configuration = new CompareConfiguration();

		configuration.setLeftLabel("Template");

		CompareItem rightItem = (CompareItem)right;

		File file = rightItem.getFile();

		configuration.setRightLabel(file.getAbsolutePath());

		CompareUI.openCompareEditor(
			new CompareEditorInput(configuration) {

				@Override
				protected Object prepareInput(final IProgressMonitor monitor)
					throws InterruptedException, InvocationTargetException {

					return new DiffNode(left, right);
				}

			});
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

		public File getFile() {
			return _file;
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
			return null;
		}

		@Override
		public String getType() {
			return null;
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