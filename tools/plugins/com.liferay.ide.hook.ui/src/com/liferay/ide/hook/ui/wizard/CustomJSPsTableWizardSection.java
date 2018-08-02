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

package com.liferay.ide.hook.ui.wizard;

import com.liferay.ide.core.util.FileListing;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.project.ui.wizard.StringArrayTableWizardSection;
import com.liferay.ide.ui.wizard.ExternalFileSelectionDialog;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Gregory Amerson
 */
public class CustomJSPsTableWizardSection extends StringArrayTableWizardSection {

	public CustomJSPsTableWizardSection(
		Composite parent, String componentLabel, String dialogTitle, String addButtonLabel, String editButtonLabel,
		String removeButtonLabel, String[] columnTitles, String[] fieldLabels, Image labelProviderImage,
		IDataModel model, String propertyName) {

		super(
			parent, componentLabel, dialogTitle, addButtonLabel, editButtonLabel, removeButtonLabel, columnTitles,
			fieldLabels, labelProviderImage, model, propertyName);
	}

	public void setPortalDir(File dir) {
		portalDir = dir;
	}

	@Override
	protected void addButtonsToButtonComposite(
		Composite buttonCompo, String addButtonLabel, String editButtonLabel, String removeButtonLabel) {

		GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);

		addFromPortalButton = new Button(buttonCompo, SWT.PUSH);

		addFromPortalButton.setText(Msgs.addFromLiferay);
		addFromPortalButton.setLayoutData(gridData);
		addFromPortalButton.addSelectionListener(
			new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent event) {
				}

				public void widgetSelected(SelectionEvent event) {
					handleAddFromPortalButtonSelected();
				}

			});

		super.addButtonsToButtonComposite(buttonCompo, addButtonLabel, editButtonLabel, removeButtonLabel);
	}

	protected void handleAddFromPortalButtonSelected() {
		if (FileUtil.notExists(portalDir)) {
			MessageDialog.openWarning(getShell(), Msgs.addJSP, Msgs.couldNotFindPortalRoot);

			return;
		}

		IPath rootPath = new Path(portalDir.getPath());

		JSPFileViewerFilter filter = new JSPFileViewerFilter(portalDir, new String[] {"html"});

		ExternalFileSelectionDialog dialog = new ExternalFileSelectionDialog(getShell(), filter, true, false);

		dialog.setTitle(Msgs.liferayCustomJSP);
		dialog.setMessage(Msgs.selectJSPToCustomize);
		dialog.setInput(portalDir);

		if (dialog.open() == Window.OK) {
			Object[] selected = dialog.getResult();

			for (Object object : selected) {
				IPath filePath = Path.fromOSString(((File)object).getPath());

				addStringArray(new String[] {"/" + FileUtil.toPortableString(filePath.makeRelativeTo(rootPath))});
			}
		}
	}

	protected Button addFromPortalButton;
	protected File portalDir;

	protected static class JSPFileViewerFilter extends ViewerFilter {

		public JSPFileViewerFilter(File base, String[] roots) {
			this.base = base;

			this.roots = roots;

			validRoots = new IPath[roots.length];

			for (int i = 0; i < roots.length; i++) {
				File fileRoot = new File(base, roots[i]);

				if (FileUtil.exists(fileRoot)) {
					validRoots[i] = new Path(fileRoot.getPath());
				}
			}
		}

		@Override
		public boolean select(Viewer viewer, Object parent, Object element) {
			if (element instanceof File) {
				File file = (File)element;

				IPath filePath = new Path(file.getPath());

				boolean validRootFound = false;

				for (IPath validRoot : validRoots) {
					if (validRoot.isPrefixOf(filePath)) {
						validRootFound = true;

						break;
					}
				}

				if (!validRootFound) {
					return false;
				}

				if (cachedDirs.contains(file)) {
					return true;
				}
				else if (file.isDirectory()) {

					// we only want to show the directory if it had children
					// that have jsps

					if (directoryContainsFiles(file, "jsp", viewer)) {
						cachedDirs.add(file);

						return true;
					}
				}
				else {
					if (StringUtil.contains(filePath.getFileExtension(), "jsp")) {
						return true;
					}
				}
			}

			return false;
		}

		protected boolean directoryContainsFiles(File dir, String ext, Viewer viewer) {
			try {
				List<File> files = FileListing.getFileListing(dir);

				for (File file : files) {
					IPath filePath = new Path(file.getPath());

					if (StringUtil.contains(filePath.getFileExtension(), ext)) {
						return true;
					}
				}
			}
			catch (FileNotFoundException fnfe) {

				// do nothing

			}

			return false;
		}

		protected File base;
		protected List<File> cachedDirs = new ArrayList<>();
		protected String[] roots = null;
		protected IPath[] validRoots;

	}

	private static class Msgs extends NLS {

		public static String addFromLiferay;
		public static String addJSP;
		public static String couldNotFindPortalRoot;
		public static String liferayCustomJSP;
		public static String selectJSPToCustomize;

		static {
			initializeMessages(CustomJSPsTableWizardSection.class.getName(), Msgs.class);
		}

	}

}