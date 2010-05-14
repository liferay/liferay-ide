/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.ui.wizard;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.viewsupport.FilteredElementTreeSelectionDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class PropertiesFilteredDialog extends FilteredElementTreeSelectionDialog {

	public static class PropertiesContentProvider implements ITreeContentProvider {

		protected File propFile;

		public void dispose() {
		}

		public Object[] getChildren(Object parentElement) {
			return null;
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof File && propFile == null) {
				propFile = (File) inputElement;
			}

			Properties p = new Properties();

			try {
				p.load(new FileReader(propFile));

				return p.keySet().toArray();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			return new Object[0];
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return false;
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.propFile = null;
		}

	}

	public static class PropertiesLabelProvider extends LabelProvider {

		@Override
		public Image getImage(Object element) {
			return super.getImage(element);
		}

		@Override
		public String getText(Object element) {
			return super.getText(element);
		}

	}

	public class PropertyFilter extends ViewerFilter {

		protected String fixedPattern;

		public PropertyFilter(String fixedPattern) {
			this.fixedPattern = fixedPattern;
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			return fixedPattern == null ? true : element != null && element.toString().matches(fixedPattern);
		}

	}

	public class PropertySelectionValidator implements ISelectionStatusValidator {

		public PropertySelectionValidator(boolean multiSelect) {
		}

		public IStatus validate(Object[] selection) {
			return Status.OK_STATUS;
		}

	}

	public class PropertyViewerComparator extends ViewerComparator {

	}

	protected static final String DIALOG_SETTINGS =
		"com.liferay.ide.eclipse.portlet.ui.wizard.PropertiesFilteredDialog";

	protected File portalRoot;

	public PropertiesFilteredDialog(Shell shell) {
		this(shell, null);
	}

	public PropertiesFilteredDialog(Shell shell, String fixedPattern) {
		super(shell, new PropertiesLabelProvider(), new PropertiesContentProvider());

		setTitle("Title");

		setAllowMultiple(false);

		setComparator(new PropertyViewerComparator());

		addFilter(new PropertyFilter(fixedPattern));

		setValidator(new PropertySelectionValidator(false));

		setHelpAvailable(false);
	}

	// public void setInput(File portalRoot) {
	// this.portalRoot = portalRoot;
	// }

}
