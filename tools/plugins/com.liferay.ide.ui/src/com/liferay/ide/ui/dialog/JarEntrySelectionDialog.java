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

package com.liferay.ide.ui.dialog;

import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.internal.wizards.datatransfer.ZipLeveledStructureProvider;

/**
 * @author Charles Wu
 */
@SuppressWarnings("restriction")
public class JarEntrySelectionDialog extends ElementTreeSelectionDialog {

	public JarEntrySelectionDialog(Shell parent) {
		super(parent, new ZipEntryLabelProvider(), new JarEntryContentProvider());
	}

	/**
	 * Provide a tree-structured view for the zip file
	 */
	public static class JarEntryContentProvider implements ITreeContentProvider {

		@Override
		public Object[] getChildren(Object parentElement) {
			List<?> chirldren = _structureProvider.getChildren(parentElement);

			return chirldren.toArray();
		}

		@Override
		public Object[] getElements(Object inputElement) {
			ZipFile zipFile = (ZipFile)inputElement;

			if (zipFile == null) {
				return new Object[0];
			}

			if (_structureProvider == null) {
				_structureProvider = new ZipLeveledStructureProvider(zipFile);
			}

			List<?> children = _structureProvider.getChildren(_structureProvider.getRoot());

			return children.toArray();
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return ((ZipEntry)element).isDirectory();
		}

		private ZipLeveledStructureProvider _structureProvider;

	}

	public static class ZipEntryLabelProvider extends LabelProvider {

		public ZipEntryLabelProvider() {
		

			IWorkbench workbench = PlatformUI.getWorkbench();

			ISharedImages sharedImages = workbench.getSharedImages();

			_imgFile = sharedImages.getImage(ISharedImages.IMG_OBJ_FILE);
			_imgFolder = sharedImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
		}

		@Override
		public Image getImage(Object element) {
			ZipEntry entry = (ZipEntry)element;

			if (entry.isDirectory()) {
				return _imgFolder;
			}
			else {
				return _imgFile;
			}
		}

		@Override
		public String getText(Object element) {
			ZipEntry entry = (ZipEntry)element;

			String entryName = entry.getName();

			String[] strings = entryName.split("/");

			return strings[strings.length - 1];
		}

		private final Image _imgFile;
		private final Image _imgFolder;

	}

}