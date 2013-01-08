/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.core.model.IBaseModel;
import com.liferay.ide.core.model.IModelChangedEvent;
import com.liferay.ide.core.model.IModelChangedListener;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.IPluginPackageModel;
import com.liferay.ide.portlet.core.PluginPackageModel;
import com.liferay.ide.project.ui.dialog.LiferayProjectSelectionDialog;
import com.liferay.ide.ui.form.DefaultContentProvider;
import com.liferay.ide.ui.form.FormLayoutFactory;
import com.liferay.ide.ui.form.IDEFormPage;
import com.liferay.ide.ui.form.TablePart;
import com.liferay.ide.ui.form.TableSection;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.ide.IDE.SharedImages;

public class RequiredDeploymentContextsSection extends TableSection implements IModelChangedListener, IPropertyChangeListener {

	class ContextsContentProvider extends DefaultContentProvider implements IStructuredContentProvider {
		public Object[] getElements(Object parent) {
			if (contexts == null) {
				createServiceDepsArray();
			}
			return contexts.toArray();
		}
	}
	class ContextsLabelProvider extends LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(SharedImages.IMG_OBJ_PROJECT);
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof String) {
				return element.toString();
			}

			return StringPool.EMPTY;
		}
		
	}
	private static final int ADD_INDEX = 0;
	private static final int DOWN_INDEX = 3;

	private static final int REMOVE_INDEX = 1;
	private static final int UP_INDEX = 2;
	private Action fAddAction;
	private Action fRemoveAction;
	// private Action fSortAction;

	private TableViewer fViewer;
	
	private Vector<String> contexts;
	
	public RequiredDeploymentContextsSection(IDEFormPage page, Composite parent, String[] labels) {
		super(page, parent, Section.DESCRIPTION, labels);
		getSection().setText(Msgs.requiredDeploymentContexts);
		getSection().setDescription(Msgs.specifyPlugins);
		getSection().getTextClient().getParent().layout(true);
		getTablePart().setEditable(true);
	}

	public void createClient(Section section, FormToolkit toolkit) {
		Composite container = createClientContainer(section, 2, toolkit);
		createViewerPartControl(container, SWT.MULTI, 2, toolkit);
		TablePart tablePart = getTablePart();
		fViewer = tablePart.getTableViewer();

		fViewer.setContentProvider(new ContextsContentProvider());
		fViewer.setLabelProvider(new ContextsLabelProvider());
		toolkit.paintBordersFor(container);
		makeActions();
		section.setClient(container);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.minimumWidth = 250;
		gd.grabExcessVerticalSpace = true;
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(gd);
		section.setText(Msgs.serviceDependencies);
		createSectionToolbar(section, toolkit);
		initialize();
	}

	public void dispose() {
		
		IBaseModel model =  getPage().getModel();
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
			handleRemove();
			return true;
		}
		if (actionId.equals(ActionFactory.CUT.getId())) {
			// delete here and let the editor transfer
			// the selection to the clipboard
			handleRemove();
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
		if (model == null)
			return;
		fViewer.setInput(model);
		updateButtons();
		model.addModelChangedListener(this);
		fAddAction.setEnabled(model.isEditable());
		fRemoveAction.setEnabled(model.isEditable());
	}

	public void modelChanged(IModelChangedEvent event) {
		if (event.getChangeType() == IModelChangedEvent.WORLD_CHANGED) {
			markStale();
			return;
		}
		
		if (event.getChangedProperty() == IPluginPackageModel.PROPERTY_PORTAL_DEPENDENCY_JARS ||
			event.getChangedProperty() == IPluginPackageModel.PROPERTY_PORTAL_DEPENDENCY_TLDS ||
			event.getChangedProperty() == IPluginPackageModel.PROPERTY_REQUIRED_DEPLOYMENT_CONTEXTS) {

			refresh();
			updateButtons();
			return;
		}

	}

	public void propertyChange(PropertyChangeEvent event) {
		// if (fSortAction.equals(event.getSource()) && IAction.RESULT.equals(event.getProperty())) {
		// updateUpDownButtons();
		// }
	}

	public void refresh() {
		contexts = null;
		fViewer.refresh();
		super.refresh();
	}

	public void setFocus() {
		if (fViewer != null)
			fViewer.getTable().setFocus();
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
		Table table = getTablePart().getTableViewer().getTable();
		String dep1 = (String) table.getItem(index1).getData();
		String dep2 = (String) table.getItem(index2).getData();

		PluginPackageModel model = (PluginPackageModel) getPage().getModel();
		model.swapDependencies(PluginPackageModel.PROPERTY_REQUIRED_DEPLOYMENT_CONTEXTS, dep1, dep2);
	}

	private void createSectionToolbar(Section section, FormToolkit toolkit) {
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = toolBarManager.createControl(section);
		final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
		toolbar.setCursor(handCursor);
		// Cursor needs to be explicitly disposed
		toolbar.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if ((handCursor != null) && (handCursor.isDisposed() == false)) {
					handCursor.dispose();
				}
			}
		});

		// Add sort action to the tool bar
		// fSortAction = new SortAction(fViewer, "Sort alphabetically", null, null, this);
		// toolBarManager.add(fSortAction);

		toolBarManager.update(true);

		section.setTextClient(toolbar);
	}

	private void handleAdd() {
		PluginPackageModel model = (PluginPackageModel) getPage().getModel();
		final String[] existingServiceDeps = model.getRequiredDeploymentContexts();

		ViewerFilter filter = new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IJavaProject) {
					IProject project = ((IJavaProject) element).getProject();

					for (String existingDep : existingServiceDeps) {
						if (project.getName().equals(existingDep)) {
							return false;
						}
					}

					if (project.equals(getPage().getLiferayFormEditor().getCommonProject())) {
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
				for (int i = 0; i < selectedProjects.length; i++) {
					IJavaProject project = (IJavaProject) selectedProjects[i];
					if (project.exists()) {
						model.addRequiredDeploymentContext(project.getProject().getName());
					}
				}
			} catch (Exception e) {
			}
		}
	}

	private void handleDown() {
		Table table = getTablePart().getTableViewer().getTable();
		int index = table.getSelectionIndex();
		if (index == table.getItemCount() - 1)
			return;
		swap(index, index + 1);
	}

	@SuppressWarnings("rawtypes")
	private void handleRemove() {
		IStructuredSelection ssel = (IStructuredSelection) fViewer.getSelection();
		PluginPackageModel model = (PluginPackageModel)getPage().getModel();
		int i = 0;
		String[] removedServiceDeps = new String[ssel.size()];
		for (Iterator iter = ssel.iterator(); iter.hasNext(); i++) {
			removedServiceDeps[i] = iter.next().toString();
		}
		
		model.removeRequiredDeploymentContexts(removedServiceDeps);
		updateButtons();
	}

	private void handleUp() {
		int index = getTablePart().getTableViewer().getTable().getSelectionIndex();
		if (index < 1)
			return;
		swap(index, index - 1);
	}

	private void makeActions() {
		fAddAction = new Action(Msgs.add) {
			public void run() {
				handleAdd();
			}
		};
		
		fRemoveAction = new Action(Msgs.remove) {
			public void run() {
				handleRemove();
			}
		};
	}
	
	private void updateButtons() {
		Table table = getTablePart().getTableViewer().getTable();
		TableItem[] selection = table.getSelection();
		boolean hasSelection = selection.length > 0;
		TablePart tablePart = getTablePart();
		tablePart.setButtonEnabled(ADD_INDEX, isEditable());
		updateUpDownButtons();
		tablePart.setButtonEnabled(REMOVE_INDEX, isEditable() && hasSelection);
	}

	private void updateUpDownButtons() {
		TablePart tablePart = getTablePart();
		// if (fSortAction.isChecked()) {
		// tablePart.setButtonEnabled(UP_INDEX, false);
		// tablePart.setButtonEnabled(DOWN_INDEX, false);
		// return;
		// }
		Table table = getTablePart().getTableViewer().getTable();
		TableItem[] selection = table.getSelection();
		boolean hasSelection = selection.length > 0;
		boolean canMove = table.getItemCount() > 1 && selection.length == 1;

		tablePart.setButtonEnabled(UP_INDEX, canMove && isEditable() && hasSelection && table.getSelectionIndex() > 0);
		tablePart.setButtonEnabled(DOWN_INDEX, canMove && hasSelection && isEditable() && table.getSelectionIndex() < table.getItemCount() - 1);
	}

	protected void buttonSelected(int index) {
		switch (index) {
			case ADD_INDEX :
				handleAdd();
				break;
			case REMOVE_INDEX :
				handleRemove();
				break;
			case UP_INDEX :
				handleUp();
				break;
			case DOWN_INDEX :
				handleDown();
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

	protected void createServiceDepsArray() {
		contexts = new Vector<String>();
		PluginPackageModel model = (PluginPackageModel) getPage().getModel();
		String[] requiredDeploymentContexts = model.getRequiredDeploymentContexts();
		Collections.addAll(contexts, requiredDeploymentContexts);
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
		getPage().getFormEditor().setSelection(sel);
		updateButtons();
	}

	// private boolean isTreeViewerSorted() {
	// if (fSortAction == null) {
	// return false;
	// }
	// return fSortAction.isChecked();
	// }

    private static class Msgs extends NLS
    {
        public static String add;
        public static String remove;
        public static String requiredDeploymentContexts;
        public static String serviceDependencies;
        public static String specifyPlugins;

        static
        {
            initializeMessages( RequiredDeploymentContextsSection.class.getName(), Msgs.class );
        }
    }
}
