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
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.IPluginPackageModel;
import com.liferay.ide.portlet.core.PluginPackageModel;
import com.liferay.ide.project.ui.dialog.LiferayProjectSelectionDialog;
import com.liferay.ide.ui.form.DefaultContentProvider;
import com.liferay.ide.ui.form.FormLayoutFactory;
import com.liferay.ide.ui.form.IDEFormEditor;
import com.liferay.ide.ui.form.IDEFormPage;
import com.liferay.ide.ui.form.TablePart;
import com.liferay.ide.ui.form.TableSection;
import com.liferay.ide.ui.util.UIUtil;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.ide.IDE.SharedImages;

/**
 * @author Gregory Amerson
 */
public class RequiredDeploymentContextsSection
	extends TableSection implements IModelChangedListener, IPropertyChangeListener {

	public RequiredDeploymentContextsSection(IDEFormPage page, Composite parent, String[] labels) {
		super(page, parent, Section.DESCRIPTION, labels);

		getSection().setText(Msgs.requiredDeploymentContexts);
		getSection().setDescription(Msgs.specifyPlugins);

		Control textClient = getSection().getTextClient();

		Composite composite = textClient.getParent();

		composite.layout(true);

		getTablePart().setEditable(true);
	}

	public void createClient(Section section, FormToolkit toolkit) {
		Composite container = createClientContainer(section, 2, toolkit);

		createViewerPartControl(container, SWT.MULTI, 2, toolkit);

		TablePart tablePart = getTablePart();

		_fViewer = tablePart.getTableViewer();

		_fViewer.setContentProvider(new ContextsContentProvider());
		_fViewer.setLabelProvider(new ContextsLabelProvider());

		toolkit.paintBordersFor(container);
		_makeActions();
		section.setClient(container);
		GridData gd = new GridData(GridData.FILL_BOTH);

		gd.minimumWidth = 250;
		gd.grabExcessVerticalSpace = true;

		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(gd);
		section.setText(Msgs.serviceDependencies);

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
	 * @see org.eclipse.pde.internal.ui.editor.PDESection#doGlobalAction(String)
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

		if ((event.getChangedProperty() == IPluginPackageModel.PROPERTY_PORTAL_DEPENDENCY_JARS) ||
			(event.getChangedProperty() == IPluginPackageModel.PROPERTY_DEPLOY_EXCLUDE) ||
			(event.getChangedProperty() == IPluginPackageModel.PROPERTY_PORTAL_DEPENDENCY_TLDS) ||
			(event.getChangedProperty() == IPluginPackageModel.PROPERTY_REQUIRED_DEPLOYMENT_CONTEXTS)) {

			refresh();
			_updateButtons();

			return;
		}
	}

	public void propertyChange(PropertyChangeEvent event) {

		// if (fSortAction.equals(event.getSource()) && IAction.RESULT.equals(event.getProperty())) {
		// updateUpDownButtons();
		// }

	}

	public void refresh() {
		_contexts = null;
		_fViewer.refresh();

		super.refresh();
	}

	// private Action fSortAction;

	public void setFocus() {
		if (_fViewer != null) {
			Table table = _fViewer.getTable();

			table.setFocus();
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
		TableViewer viewer = getTablePart().getTableViewer();

		Table table = viewer.getTable();

		TableItem item1 = table.getItem(index1);

		String dep1 = (String)item1.getData();

		TableItem item2 = table.getItem(index2);

		String dep2 = (String)item2.getData();

		PluginPackageModel model = (PluginPackageModel)getPage().getModel();

		model.swapDependencies(PluginPackageModel.PROPERTY_REQUIRED_DEPLOYMENT_CONTEXTS, dep1, dep2);
	}

	public class ContextsContentProvider extends DefaultContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object parent) {
			if (_contexts == null) {
				createServiceDepsArray();
			}

			return _contexts.toArray();
		}

	}

	public class ContextsLabelProvider extends LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			ISharedImages images = UIUtil.getSharedImages();

			return images.getImage(SharedImages.IMG_OBJ_PROJECT);
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof String) {
				return element.toString();
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
	 * @see org.eclipse.pde.internal.ui.editor.StructuredViewerSection#canPaste(Object, Object[])
	 */
	protected boolean canPaste(Object targetObject, Object[] sourceObjects) {
		return false;
	}

	protected boolean createCount() {
		return true;
	}

	protected void createServiceDepsArray() {
		_contexts = new Vector<>();
		PluginPackageModel model = (PluginPackageModel)getPage().getModel();

		String[] requiredDeploymentContexts = model.getRequiredDeploymentContexts();

		Collections.addAll(_contexts, requiredDeploymentContexts);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.StructuredViewerSection#doPaste(Object, Object[])
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

		ToolBar toolBar = toolBarManager.createControl(section);

		Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);

		toolBar.setCursor(handCursor);

		// Cursor needs to be explicitly disposed

		DisposeListener listener = new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				if ((handCursor != null) && (handCursor.isDisposed() == false)) {
					handCursor.dispose();
				}
			}

		};

		toolBar.addDisposeListener(listener);

		// Add sort action to the tool bar
		// fSortAction = new SortAction(fViewer, "Sort alphabetically", null, null, this);
		// toolBarManager.add(fSortAction);

		toolBarManager.update(true);

		section.setTextClient(toolBar);
	}

	private void _handleAdd() {
		PluginPackageModel model = (PluginPackageModel)getPage().getModel();

		String[] existingServiceDeps = model.getRequiredDeploymentContexts();

		ViewerFilter filter = new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IJavaProject) {
					IProject project = ((IJavaProject)element).getProject();

					for (String existingDep : existingServiceDeps) {
						if (FileUtil.nameEquals(project, existingDep)) {
							return false;
						}
					}

					IDEFormEditor formEditor = getPage().getLiferayFormEditor();

					if (project.equals(formEditor.getCommonProject())) {
						return false;
					}

					return true;
				}
				else {
					return false;
				}
			}

		};

		LiferayProjectSelectionDialog dialog = new LiferayProjectSelectionDialog(getPage().getShell(), filter);

		dialog.create();

		if (dialog.open() == Window.OK) {
			Object[] selectedProjects = dialog.getResult();

			try {
				for (Object o : selectedProjects) {
					IJavaProject javaProject = (IJavaProject)o;

					if (javaProject.exists()) {
						IProject project = javaProject.getProject();

						model.addRequiredDeploymentContext(project.getName());
					}
				}
			}
			catch (Exception e) {
			}
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
		String[] removedServiceDeps = new String[ssel.size()];

		for (Iterator iter = ssel.iterator(); iter.hasNext(); i++) {
			Object o = iter.next();

			removedServiceDeps[i] = o.toString();
		}

		model.removeRequiredDeploymentContexts(removedServiceDeps);
		_updateButtons();
	}

	private void _handleUp() {
		TableViewer viewer = getTablePart().getTableViewer();

		Table table = viewer.getTable();

		int index = table.getSelectionIndex();

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
		TableViewer viewer = getTablePart().getTableViewer();

		Table table = viewer.getTable();

		TableItem[] selection = table.getSelection();

		boolean hasSelection = false;

		if (ListUtil.isNotEmpty(selection)) {
			hasSelection = true;
		}

		TablePart tablePart = getTablePart();

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

		TableViewer tableViewer = tablePart.getTableViewer();

		Table table = tableViewer.getTable();

		TableItem[] selection = table.getSelection();

		boolean hasSelection = false;

		if (ListUtil.isNotEmpty(selection)) {
			hasSelection = true;
		}

		boolean canMove = false;

		if ((table.getItemCount() > 1) && (selection.length == 1)) {
			canMove = true;
		}

		tablePart.setButtonEnabled(
			_UP_INDEX, canMove && isEditable() && hasSelection && (table.getSelectionIndex() > 0));
		tablePart.setButtonEnabled(
			_DOWN_INDEX,
			canMove && hasSelection && isEditable() && (table.getSelectionIndex() < table.getItemCount() - 1));
	}

	private static final int _ADD_INDEX = 0;

	private static final int _DOWN_INDEX = 3;

	private static final int _REMOVE_INDEX = 1;

	private static final int _UP_INDEX = 2;

	private Vector<String> _contexts;
	private Action _fAddAction;
	private Action _fRemoveAction;
	private TableViewer _fViewer;

	// private boolean isTreeViewerSorted() {
	// if (fSortAction == null) {
	// return false;
	// }
	// return fSortAction.isChecked();
	// }

	private static class Msgs extends NLS {

		public static String add;
		public static String remove;
		public static String requiredDeploymentContexts;
		public static String serviceDependencies;
		public static String specifyPlugins;

		static {
			initializeMessages(RequiredDeploymentContextsSection.class.getName(), Msgs.class);
		}

	}

}