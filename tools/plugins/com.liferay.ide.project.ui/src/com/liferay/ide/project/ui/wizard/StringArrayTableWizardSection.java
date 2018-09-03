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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.ListUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author jialin
 *
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class StringArrayTableWizardSection extends Composite {

	public StringArrayTableWizardSection(
		Composite parent, String componentLabel, String dialogTitle, String addButtonLabel, String editButtonLabel,
		String removeButtonLabel, String[] columnTitles, String[] fieldLabels, Image labelProviderImage,
		IDataModel model, String propertyName) {

		super(parent, SWT.NONE);

		this.dialogTitle = dialogTitle;
		this.fieldLabels = fieldLabels;
		this.labelProviderImage = labelProviderImage;
		this.model = model;
		this.propertyName = propertyName;

		GridLayout layout = new GridLayout(2, false);

		layout.marginHeight = 4;
		layout.marginWidth = 0;
		setLayout(layout);

		setLayoutData(new GridData(GridData.FILL_BOTH));

		Label titleLabel = new Label(this, SWT.LEFT);

		titleLabel.setText(componentLabel);

		GridData data = new GridData();

		data.horizontalSpan = 2;
		titleLabel.setLayoutData(data);

		Table table = new Table(this, SWT.FULL_SELECTION | SWT.BORDER);

		viewer = new TableViewer(table);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		viewer.setContentProvider(new StringArrayListContentProvider());
		viewer.setLabelProvider(new StringArrayListLabelProvider());

		final Composite buttonCompo = new Composite(this, SWT.NULL);
		layout = new GridLayout();

		layout.marginHeight = 0;

		buttonCompo.setLayout(layout);

		buttonCompo.setLayoutData(new GridData(GridData.FILL_VERTICAL | GridData.VERTICAL_ALIGN_BEGINNING));

		addButtonsToButtonComposite(buttonCompo, addButtonLabel, editButtonLabel, removeButtonLabel);

		viewer.addSelectionChangedListener(
			new ISelectionChangedListener() {

				public void selectionChanged(SelectionChangedEvent event) {
					ISelection selection = event.getSelection();

					if (editButton != null) {
						boolean enabled = false;

						if (((IStructuredSelection)selection).size() == 1) {
							enabled = true;
						}

						editButton.setEnabled(enabled);
					}

					removeButton.setEnabled(!selection.isEmpty());
				}

			});

		if (editButton != null) {
			viewer.addDoubleClickListener(
				new IDoubleClickListener() {

					public void doubleClick(DoubleClickEvent event) {
						handleEditButtonSelected();
					}

				});
		}

		if (columnTitles.length > 1) {
			for (String columnTitle : columnTitles) {
				TableColumn tableColumn = new TableColumn(table, SWT.NONE);

				tableColumn.setText(columnTitle);
			}

			table.setHeaderVisible(true);

			addControlListener(
				new ControlAdapter() {

					public void controlResized(ControlEvent e) {
						Table table = viewer.getTable();

						TableColumn[] columns = table.getColumns();

						Point buttonArea = buttonCompo.computeSize(SWT.DEFAULT, SWT.DEFAULT);

						Composite composite = table.getParent();

						Rectangle area = composite.getClientArea();

						Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);

						int width = area.width - 2 * table.getBorderWidth() - buttonArea.x - columns.length * 2;

						if (preferredSize.y > (area.height + table.getHeaderHeight())) {

							// Subtract the scrollbar width from the total column width
							// if a vertical scrollbar will be required

							ScrollBar scrollBar = table.getVerticalBar();

							Point vBarSize = scrollBar.getSize();

							width -= vBarSize.x;
						}

						Point oldSize = table.getSize();
						int consumeWidth = 0;

						for (int i = 0; i < columns.length; i++) {
							if (oldSize.x > area.width) {

								// table is getting smaller so make the columns
								// smaller first and then resize the table to
								// match the client area width

								consumeWidth = _setColumntWidth(width, columns, consumeWidth, i);
								table.setSize(area.width - buttonArea.x - columns.length * 2, area.height);
							}
							else {

								// table is getting bigger so make the table
								// bigger first and then make the columns wider
								// to match the client area width

								table.setSize(area.width - buttonArea.x - columns.length * 2, area.height);
								consumeWidth = _setColumntWidth(width, columns, consumeWidth, i);
							}
						}
					}

					private int _setColumntWidth(int width, TableColumn[] columns, int consumeWidth, int i) {
						if (i < (columns.length - 1)) {
							columns[i].setWidth(width / columns.length);
							consumeWidth += columns[i].getWidth();
						}
						else {
							columns[i].setWidth(width - consumeWidth);
						}

						return consumeWidth;
					}

				});
		}

		callback = new StringArrayDialogCallbackAdapter();
	}

	public StringArrayTableWizardSection(
		Composite parent, String title, String addButtonLabel, String editButtonLabel, String removeButtonLabel,
		String[] labelsForText, Image labelProviderImage, IDataModel model, String propertyName) {

		this(
			parent, title, title, addButtonLabel, editButtonLabel, removeButtonLabel, labelsForText, labelsForText,
			labelProviderImage, model, propertyName);
	}

	public StringArrayTableWizardSection(
		Composite parent, String title, String addButtonLabel, String removeButtonLabel, String[] labelsForText,
		Image labelProviderImage, IDataModel model, String propertyName) {

		this(
			parent, title, addButtonLabel, null, removeButtonLabel, labelsForText, labelProviderImage, model,
			propertyName);
	}

	public void addStringArray(String[] stringArray) {
		if (stringArray == null) {
			return;
		}

		List valueList = (List)viewer.getInput();

		if (valueList == null) {
			valueList = new ArrayList();
		}

		valueList.add(stringArray);

		setInput(valueList);
	}

	public void editStringArray(String[] oldStringArray, String[] newStringArray) {
		if (newStringArray == null) {
			return;
		}

		List valueList = (List)viewer.getInput();

		if (valueList == null) {
			valueList = new ArrayList();
		}

		int index = valueList.indexOf(oldStringArray);

		if (index == -1) {
			valueList.add(newStringArray);
		}
		else {
			valueList.set(index, newStringArray);
		}

		setInput(valueList);
	}

	public Button getAddButton() {
		return addButton;
	}

	public Button getEditButton() {
		return editButton;
	}

	public Button getRemoveButton() {
		return removeButton;
	}

	public TableViewer getTableViewer() {
		return viewer;
	}

	public void removeStringArray(Object selectedStringArray) {
		List valueList = (List)viewer.getInput();

		valueList.remove(selectedStringArray);

		setInput(valueList);
	}

	public void removeStringArrays(Collection selectedStringArrays) {
		List valueList = (List)viewer.getInput();

		valueList.removeAll(selectedStringArrays);

		setInput(valueList);
	}

	/**
	 * Set callback for customizing the preprocessing of the user input.
	 *
	 * @param callback
	 *            an implementation of the callback interface.
	 */
	public void setCallback(StringArrayDialogCallback callback) {
		this.callback = callback;
	}

	public void setInput(List input) {
		viewer.setInput(input);

		// Create a new list to trigger property change

		List newInput = new ArrayList();

		newInput.addAll(input);

		model.setProperty(propertyName, newInput);
	}

	public class AddStringArrayDialog extends Dialog implements ModifyListener {

		/**
		 * CMPFieldDialog constructor comment.
		 */
		public AddStringArrayDialog(Shell shell, String windowTitle, String[] labelsForTextField) {
			super(shell);

			this.windowTitle = windowTitle;
			this.labelsForTextField = labelsForTextField;
		}

		/**
		 * CMPFieldDialog constructor comment.
		 */
		public Control createDialogArea(Composite parent) {
			Composite composite = (Composite)super.createDialogArea(parent);
			getShell().setText(windowTitle);

			GridLayout layout = new GridLayout();

			layout.numColumns = 3;

			composite.setLayout(layout);

			GridData data = new GridData();

			data.verticalAlignment = GridData.FILL;
			data.horizontalAlignment = GridData.FILL;
			data.widthHint = widthHint;
			composite.setLayoutData(data);

			int n = labelsForTextField.length;

			texts = new Text[n];

			for (int i = 0; i < n; i++) {
				texts[i] = createField(composite, i);
			}

			// set focus

			texts[0].setFocus();
			Dialog.applyDialogFont(parent);

			return composite;
		}

		public String[] getStringArray() {
			return stringArray;
		}

		public void modifyText(ModifyEvent e) {
			_updateOKButton();
		}

		protected Control createContents(Composite parent) {
			Composite composite = (Composite)super.createContents(parent);

			for (Text text : texts) {
				text.addModifyListener(this);
			}

			_updateOKButton();

			return composite;
		}

		protected Text createField(Composite composite, int index) {
			Label label = new Label(composite, SWT.LEFT);

			label.setText(labelsForTextField[index]);
			label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

			Text text = new Text(composite, SWT.SINGLE | SWT.BORDER);

			GridData data = new GridData(GridData.FILL_HORIZONTAL);

			data.widthHint = 100;

			text.setLayoutData(data);

			new Label(composite, SWT.NONE);

			return text;
		}

		protected void okPressed() {
			stringArray = callback.retrieveResultStrings(texts);
			super.okPressed();
		}

		protected void setWidthHint(int hint) {
			widthHint = hint;
		}

		protected String[] labelsForTextField;
		protected String[] stringArray;
		protected Text[] texts;
		protected int widthHint = 300;
		protected String windowTitle;

		private void _updateOKButton() {
			getButton(IDialogConstants.OK_ID).setEnabled(callback.validate(texts));
		}

	}

	/**
	 * Callback interface used by the Add/Edit-StringArrayDialog classes.
	 */
	public interface StringArrayDialogCallback {

		/**
		 * Retrieves the strings from the text fields of the dialog.
		 * <p>
		 * Implementers of the callback can use these method to do some preprocessing
		 * (like trimming) of the data in the text fields before using it. The returned
		 * values will be the actual data that will be put in the table viewer.
		 * </p>
		 *
		 * @param texts
		 *            reference to the text fields in the dialog
		 *
		 * @return the values retreived from the text fields
		 */
		public String[] retrieveResultStrings(Text[] texts);

		/**
		 * Validates the text fields.
		 * <p>
		 * Used to decide wheather to enable the OK button of the dialog. If the method
		 * returns <code>true</code> the OK button is enabled, otherwise the OK button
		 * is disabled.
		 * </p>
		 *
		 * @param reference
		 *            to the text fields in the dialog
		 *
		 * @return <code>true</code> if the values in the text fields are valid,
		 *         <code>false</code> otherwise.
		 */
		public boolean validate(Text[] texts);

	}

	protected void addButtonsToButtonComposite(
		Composite buttonCompo, String addButtonLabel, String editButtonLabel, String removeButtonLabel) {

		addButton = new Button(buttonCompo, SWT.PUSH);

		addButton.setText(addButtonLabel);
		addButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL));
		addButton.addSelectionListener(
			new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent event) {

					// Do nothing

				}

				public void widgetSelected(SelectionEvent event) {
					handleAddButtonSelected();
				}

			});

		if (editButtonLabel != null) {
			editButton = new Button(buttonCompo, SWT.PUSH);

			editButton.setText(editButtonLabel);
			editButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL));
			editButton.addSelectionListener(
				new SelectionListener() {

					public void widgetDefaultSelected(SelectionEvent event) {
					}

					public void widgetSelected(SelectionEvent event) {
						handleEditButtonSelected();
					}

				});
			editButton.setEnabled(false);
		}

		removeButton = new Button(buttonCompo, SWT.PUSH);

		removeButton.setText(removeButtonLabel);
		removeButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL));
		removeButton.addSelectionListener(
			new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent event) {

					// Do nothing

				}

				public void widgetSelected(SelectionEvent event) {
					handleRemoveButtonSelected();
				}

			});
		removeButton.setEnabled(false);
	}

	protected void handleAddButtonSelected() {
		AddStringArrayDialog dialog = new AddStringArrayDialog(getShell(), dialogTitle, fieldLabels);

		dialog.open();

		String[] stringArray = dialog.getStringArray();

		addStringArray(stringArray);
	}

	protected void handleEditButtonSelected() {
		ISelection s = viewer.getSelection();

		if (!(s instanceof IStructuredSelection)) {
			return;
		}

		IStructuredSelection selection = (IStructuredSelection)s;

		if (selection.size() != 1) {
			return;
		}

		Object selectedObj = selection.getFirstElement();

		String[] valuesForText = (String[])selectedObj;

		EditStringArrayDialog dialog = new EditStringArrayDialog(getShell(), dialogTitle, fieldLabels, valuesForText);

		dialog.open();

		String[] stringArray = dialog.getStringArray();

		editStringArray(valuesForText, stringArray);
	}

	protected void handleRemoveButtonSelected() {
		ISelection selection = viewer.getSelection();

		if (selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
			return;
		}

		List selectedObj = ((IStructuredSelection)selection).toList();

		removeStringArrays(selectedObj);
	}

	protected Button addButton;
	protected StringArrayDialogCallback callback;
	protected String dialogTitle;
	protected Button editButton;
	protected String[] fieldLabels;
	protected Image labelProviderImage;
	protected IDataModel model;
	protected String propertyName;
	protected Button removeButton;
	protected TableViewer viewer;

	protected class EditStringArrayDialog extends AddStringArrayDialog {

		/**
		 * CMPFieldDialog constructor comment.
		 */
		public EditStringArrayDialog(
			Shell shell, String windowTitle, String[] labelsForTextField, String[] valuesForTextField) {

			super(shell, windowTitle, labelsForTextField);

			this.valuesForTextField = valuesForTextField;
		}

		/**
		 * CMPFieldDialog constructor comment.
		 */
		public Control createDialogArea(Composite parent) {
			Composite composite = (Composite)super.createDialogArea(parent);

			int n = valuesForTextField.length;

			for (int i = 0; i < n; i++) {
				texts[i].setText(valuesForTextField[i]);
			}

			return composite;
		}

		protected String[] valuesForTextField;

	}

	/**
	 * Default adapter with basic implementation of the
	 * <code>StringArrayDialogCallback</code> interface.
	 */
	protected class StringArrayDialogCallbackAdapter implements StringArrayDialogCallback {

		/**
		 * Just retreives the unmodified values of the text fields as a string array.
		 */
		public String[] retrieveResultStrings(Text[] texts) {
			int n = texts.length;

			String[] result = new String[n];

			for (int i = 0; i < n; i++) {
				result[i] = texts[i].getText();
			}

			return result;
		}

		/**
		 * Returns always <code>true</code>.
		 */
		public boolean validate(Text[] texts) {
			return true;
		}

	}

	protected class StringArrayListContentProvider implements IStructuredContentProvider {

		public void dispose() {

			// Default nothing

		}

		public Object[] getElements(Object element) {
			if (element instanceof List) {
				return ((List)element).toArray();
			}

			return new Object[0];
		}

		public void inputChanged(Viewer aViewer, Object oldInput, Object newInput) {

			// Default nothing

		}

		public boolean isDeleted(Object element) {
			return false;
		}

	}

	protected class StringArrayListLabelProvider extends LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				return labelProviderImage;
			}

			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String[] array = (String[])element;

			return array[columnIndex];
		}

		@Override
		public Image getImage(Object element) {
			return labelProviderImage;
		}

		@Override
		public String getText(Object element) {
			String[] array = (String[])element;

			if (ListUtil.isNotEmpty(array)) {
				return array[0];
			}
			else {
				return super.getText(element);
			}
		}

	}

}