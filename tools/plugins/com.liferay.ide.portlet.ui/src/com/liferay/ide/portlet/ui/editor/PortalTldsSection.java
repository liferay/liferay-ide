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

package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.core.model.IBaseModel;
import com.liferay.ide.core.model.IModelChangedEvent;
import com.liferay.ide.core.model.IModelChangedListener;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.IPluginPackageModel;
import com.liferay.ide.portlet.core.PluginPackageModel;
import com.liferay.ide.ui.form.DefaultContentProvider;
import com.liferay.ide.ui.form.FormLayoutFactory;
import com.liferay.ide.ui.form.IDEFormEditor;
import com.liferay.ide.ui.form.IDEFormPage;
import com.liferay.ide.ui.form.TablePart;
import com.liferay.ide.ui.form.TableSection;
import com.liferay.ide.ui.wizard.ExternalFileSelectionDialog;

import java.io.File;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Greg Amerson
 */
public class PortalTldsSection extends TableSection implements IModelChangedListener, IPropertyChangeListener {

	public PortalTldsSection(IDEFormPage page, Composite parent, String[] labels) {
		super(page, parent, Section.DESCRIPTION, labels);
		Section section = getSection();

		section.setText(Msgs.portalDependencyTlds);
		section.setDescription(Msgs.specifyTLDs);

		Composite composite = section.getTextClient().getParent();

		composite.layout(true);

		getTablePart().setEditable(true);
	}

	public void createClient(Section section, FormToolkit toolkit) {
		Composite container = createClientContainer(section, 2, toolkit);

		createViewerPartControl(container, SWT.MULTI, 2, toolkit);

		TablePart tablePart = getTablePart();

		_fViewer = tablePart.getTableViewer();

		_fViewer.setContentProvider(new PortalTldsContentProvider());
		_fViewer.setLabelProvider(new PortalTldsLabelProvider());

		toolkit.paintBordersFor(container);
		_makeActions();
		section.setClient(container);
		GridData gd = new GridData(GridData.FILL_BOTH);

		gd.minimumWidth = 250;
		gd.grabExcessVerticalSpace = true;

		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(gd);
		section.setText(Msgs.portalDependencyTlds);
		_createSectionToolbar(section);
		initialize();
	}

	public void dispose() {
		IBaseModel model = getPage().getModel();

		if (model != null) {
			model.dispose();
		}

		super.dispose();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.PDESection#doGlobalAction(java.lang.String)
	 */
	public boolean doGlobalAction(String actionId) {
		if (!isEditable()) {
			return false;
		}

		if (actionId.equals(ActionFactory.DELETE.getId())) {
			_handleRemove();

			return true;
		}

		if (actionId.equals(ActionFactory.CUT.getId())) {

			// delete here and let the editor transfer
			// the selection to the clipboard

			_handleRemove();

			return false;
		}

		if (actionId.equals(ActionFactory.PASTE.getId())) {
			doPaste();

			return true;
		}

		return false;
	}

	public void initialize() {
		PluginPackageModel model = (PluginPackageModel)getPage().getModel();

		if (model == null) {
			return;
		}

		_fViewer.setInput(model);

		_updateButtons();
		model.addModelChangedListener(this);
		_fAddAction.setEnabled(model.isEditable());

		_fRemoveAction.setEnabled(model.isEditable());
	}

	public void modelChanged(IModelChangedEvent event) {
		if (event.getChangeType() == IModelChangedEvent.WORLD_CHANGED) {
			markStale();
			return;
		}

		if (event.getChangedProperty() == IPluginPackageModel.PROPERTY_PORTAL_DEPENDENCY_TLDS) {
			refresh();
			_updateButtons();
			return;
		}
	}

	/**
	public void modelsChanged(PluginModelDelta delta) {
		fImports = null;
		final Control control = fImportViewer.getControl();

		if (!control.isDisposed()) {
		Runable run = new Runnable() {

				public void run() {
					if (!control.isDisposed()) {
						fImportViewer.refresh();
					}
				}

		};

		control.getDisplay().asyncExec(run);
		}
	}

	 */
	private Action _fAddAction;

	public void propertyChange(PropertyChangeEvent event) {

		// if (fSortAction.equals(event.getSource()) && IAction.RESULT.equals(event.getProperty())) {
		// updateUpDownButtons();
		// }

	}

	// private Action fSortAction;

	public void refresh() {
		_fTlds = null;
		_fViewer.refresh();
		super.refresh();
	}

	public void setFocus() {
		if (_fViewer != null) {
			_fViewer.getTable().setFocus();
		}
	}

	public boolean setFormInput(Object object) {
		//		if (object instanceof IPluginImport) {
		//			ImportObject iobj = new ImportObject((IPluginImport) object);
		//			fImportViewer.setSelection(new StructuredSelection(iobj), true);
		//			return true;
		//		}

		return false;
	}

	public void swap(int index1, int index2) {
	}

	public class PortalTldsContentProvider extends DefaultContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object parent) {
			if (_fTlds == null) {
				createTldsArray();
			}

			return _fTlds.toArray();
		}

	}

	public class PortalTldsLabelProvider extends LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			IWorkbench workbench = PlatformUI.getWorkbench();

			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof File) {
				File file = (File)element;

				return file.getName();
			}

			return StringPool.EMPTY;
		}

	}

	protected void buttonSelected(int index) {
		switch (index) {
			case _ADD_INDEX:
				_handleAdd();
				break;
			case _REMOVE_INDEX:
				_handleRemove();
				break;
			case _UP_INDEX:
				_handleUp();
				break;
			case _DOWN_INDEX:
				_handleDown();
				break;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.StructuredViewerSection#canPaste(java.lang.Object, java.lang.Object[])
	 */
	protected boolean canPaste(Object targetObject, Object[] sourceObjects) {
		return false;
	}

	protected boolean createCount() {
		return true;
	}

	protected void createTldsArray() {
		_fTlds = new Vector<>();
		PluginPackageModel model = (PluginPackageModel)getPage().getModel();

		String[] portalTlds = model.getPortalDependencyTlds();

		IPath portalDir = ((PluginPackageEditor)getPage().getEditor()).getPortalDir();

		if (portalDir != null) {
			for (String portalTld : portalTlds) {
				File tldFile = new File(portalDir.append("WEB-INF/tld").toFile(), portalTld.trim());

				if (tldFile.isFile() && tldFile.exists()) {
					_fTlds.add(tldFile);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.StructuredViewerSection#doPaste(java.lang.Object, java.lang.Object[])
	 */
	protected void doPaste(Object targetObject, Object[] sourceObjects) {

		// Get the model

	}

	protected void fillContextMenu(IMenuManager manager) {
	}

	protected void handleDoubleClick(IStructuredSelection sel) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.StructuredViewerSection#isDragAndDropEnabled()
	 */
	protected boolean isDragAndDropEnabled() {
		return false;
	}

	protected void selectionChanged(IStructuredSelection sel) {
		IDEFormEditor formEditor = getPage().getFormEditor();

		formEditor.setSelection(sel);

		_updateButtons();
	}

	private void _createSectionToolbar(Section section) {
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

		ToolBar toolbar = toolBarManager.createControl(section);

		Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);

		toolbar.setCursor(handCursor);

		// Cursor needs to be explicitly disposed

		DisposeListener listener = new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				if ((handCursor != null) && (handCursor.isDisposed() == false)) {
					handCursor.dispose();
				}
			}

		};

		toolbar.addDisposeListener(listener);

		// Add sort action to the tool bar
		// fSortAction = new SortAction(fViewer, "Sort alphabetically", null, null, this);
		// toolBarManager.add(fSortAction);

		toolBarManager.update(true);

		section.setTextClient(toolbar);
	}

	private void _handleAdd() {
		PluginPackageModel model = (PluginPackageModel)getPage().getModel();

		String[] existingTlds = model.getPortalDependencyTlds();

		PluginPackageEditor editor = (PluginPackageEditor)getPage().getEditor();

		IPath portalDir = editor.getPortalDir();

		if (portalDir != null) {
			Shell shell = getPage().getShell();
			PortalTldViewerFilter filter = new PortalTldViewerFilter(
				portalDir.toFile(), new String[] {"WEB-INF", "WEB-INF/tld"}, existingTlds);

			ExternalFileSelectionDialog dialog = new ExternalFileSelectionDialog(shell, filter, true, false);

			dialog.setInput(portalDir.toFile());
			dialog.create();

			if (dialog.open() == Window.OK) {
				Object[] selectedFiles = dialog.getResult();

				try {
					for (int i = 0; i < selectedFiles.length; i++) {
						File tld = (File)selectedFiles[i];

						if (tld.exists()) {
							model.addPortalDependencyTld(tld.getName());
						}
					}
				}
				catch (Exception e) {
				}
			}
		}
		else {
			MessageDialog.openInformation(
				getPage().getShell(), Msgs.liferayPluginPackageEditor, Msgs.notDeterminePortalDirectory);
		}
	}

	private void _handleDown() {
		TableViewer viewer = getTablePart().getTableViewer();

		Table table = viewer.getTable();

		int index = table.getSelectionIndex();

		if (index == (table.getItemCount() - 1)) {
			return;
		}

		swap(index, index + 1);
	}

	@SuppressWarnings("rawtypes")
	private void _handleRemove() {
		IStructuredSelection ssel = (IStructuredSelection)_fViewer.getSelection();
		PluginPackageModel model = (PluginPackageModel)getPage().getModel();
		int i = 0;
		String[] removedFiles = new String[ssel.size()];

		for (Iterator iter = ssel.iterator(); iter.hasNext(); i++) {
			removedFiles[i] = ((File)iter.next()).getName();
		}

		model.removePortalDependencyTlds(removedFiles);

		_updateButtons();
	}

	private void _handleUp() {
		TableViewer viewer = getTablePart().getTableViewer();

		int index = viewer.getTable().getSelectionIndex();

		if (index < 1) {
			return;
		}

		swap(index, index - 1);
	}

	private void _makeActions() {
		_fAddAction = new Action(Msgs.add) {

			public void run() {
				_handleAdd();
			}

		};

		_fRemoveAction = new Action(Msgs.remove) {

			public void run() {
				_handleRemove();
			}

		};
	}

	private void _updateButtons() {
		TablePart tablePart = getTablePart();

		Table table = tablePart.getTableViewer().getTable();

		TableItem[] selection = table.getSelection();

		boolean hasSelection = false;

		if (ListUtil.isNotEmpty(selection)) {
			hasSelection = true;
		}

		tablePart.setButtonEnabled(_ADD_INDEX, isEditable());

		_updateUpDownButtons();
		tablePart.setButtonEnabled(_REMOVE_INDEX, isEditable() && hasSelection);
	}

	private void _updateUpDownButtons() {
		TablePart tablePart = getTablePart();

		// if (fSortAction.isChecked()) {
		// tablePart.setButtonEnabled(UP_INDEX, false);
		// tablePart.setButtonEnabled(DOWN_INDEX, false);
		// return;
		// }

		TableViewer viewer = getTablePart().getTableViewer();

		Table table = viewer.getTable();

		TableItem[] selection = table.getSelection();

		boolean hasSelection = false;

		if (ListUtil.isNotEmpty(selection)) {
			hasSelection = true;
		}

		boolean canMove = false;

		if ((table.getItemCount() > 1) && (selection.length == 1)) {
			canMove = true;
		}

		tablePart.setButtonEnabled(_UP_INDEX, canMove && isEditable() && hasSelection && table.getSelectionIndex() > 0);
		tablePart.setButtonEnabled(
			_DOWN_INDEX,
			canMove && hasSelection && isEditable() && table.getSelectionIndex() < table.getItemCount() - 1);
	}

	private static final int _ADD_INDEX = 0;

	private static final int _DOWN_INDEX = 3;

	private static final int _REMOVE_INDEX = 1;

	private static final int _UP_INDEX = 2;

	private Action _fRemoveAction;
	private Vector<File> _fTlds;
	private TableViewer _fViewer;

	// private boolean isTreeViewerSorted() {
	// if (fSortAction == null) {
	// return false;
	// }
	// return fSortAction.isChecked();
	// }

	private static class Msgs extends NLS {

		public static String add;
		public static String liferayPluginPackageEditor;
		public static String notDeterminePortalDirectory;
		public static String portalDependencyTlds;
		public static String remove;
		public static String specifyTLDs;

		static {
			initializeMessages(PortalTldsSection.class.getName(), Msgs.class);
		}

	}

}