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

package com.liferay.ide.ui.form;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Gregory Amerson
 */
public abstract class TableSection extends StructuredViewerSection {

	/**
	 * Constructor for TableSection.
	 *
	 * @param formPage
	 */
	public TableSection(IDEFormPage formPage, Composite parent, int style, boolean titleBar, String[] buttonLabels) {
		super(formPage, parent, style, titleBar, buttonLabels);
	}

	/**
	 * Constructor for TableSection.
	 *
	 * @param formPage
	 */
	public TableSection(IDEFormPage formPage, Composite parent, int style, String[] buttonLabels) {
		this(formPage, parent, style, true, buttonLabels);
	}

	public class PartAdapter extends EditableTablePart {

		public PartAdapter(String[] buttonLabels) {
			super(buttonLabels);
		}

		public void buttonSelected(Button button, int index) {
			TableSection.this.buttonSelected(index);

			if (fHandleDefaultButton) {
				Shell shell = button.getShell();

				shell.setDefaultButton(null);
			}
		}

		public void entryModified(Object entry, String value) {
			TableSection.this.entryModified(entry, value);
		}

		public void handleDoubleClick(IStructuredSelection selection) {
			TableSection.this.handleDoubleClick(selection);
		}

		public void selectionChanged(IStructuredSelection selection) {
			getManagedForm().fireSelectionChanged(TableSection.this, selection);
			TableSection.this.selectionChanged(selection);
		}

		protected void createButtons(Composite parent, FormToolkit toolkit) {
			super.createButtons(parent, toolkit);

			enableButtons();

			if (createCount()) {
				Composite comp = toolkit.createComposite(fButtonContainer);

				comp.setLayout(createButtonsLayout());
				comp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END | GridData.FILL_BOTH));

				_fCount = toolkit.createLabel(comp, "");

				FormColors colors = toolkit.getColors();

				_fCount.setForeground(colors.getColor(IFormColors.TITLE));

				_fCount.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				EditableTablePart tablePart = getTablePart();

				TableViewer tableViewer = tablePart.getTableViewer();

				Table table = tableViewer.getTable();

				table.addPaintListener(
					new PaintListener() {

						public void paintControl(PaintEvent e) {
							updateLabel();
						}

					});
			}
		}

		protected void updateLabel() {
			if ((_fCount != null) && !_fCount.isDisposed()) {
				TableViewer tableViewer = getTableViewer();

				Table table = tableViewer.getTable();

				_fCount.setText(NLS.bind(Msgs.totalLabel, String.valueOf(table.getItemCount())));
			}
		}

		private Label _fCount;

	}

	protected boolean createCount() {
		return false;
	}

	protected StructuredViewerPart createViewerPart(String[] buttonLabels) {
		return new PartAdapter(buttonLabels);
	}

	protected void enableButtons() {
	}

	protected void entryModified(Object entry, String value) {
	}

	protected IAction getRenameAction() {
		return getTablePart().getRenameAction();
	}

	protected EditableTablePart getTablePart() {
		return (EditableTablePart)fViewerPart;
	}

	protected void handleDoubleClick(IStructuredSelection selection) {
	}

	protected void selectionChanged(IStructuredSelection selection) {
	}

	protected boolean fHandleDefaultButton = true;

	private static class Msgs extends NLS {

		public static String totalLabel;

		static {
			initializeMessages(TableSection.class.getName(), Msgs.class);
		}

	}

}