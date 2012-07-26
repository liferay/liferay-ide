/*******************************************************************************
 *  Copyright (c) 2000, 2008 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.ui.form;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * The helper class for creating entry fields with label and text. Optionally,
 * a button can be added after the text. The attached listener reacts to all
 * the events. Entring new text makes the entry 'dirty', but only when 'commit'
 * is called is 'valueChanged' method called (and only if 'dirty' flag is set).
 * This allows delayed commit.
 */
public class FormEntry {
	private Control fLabel;
	private Text fText;
	private Button fBrowse;
	private String fValue = ""; //$NON-NLS-1$
	private boolean fDirty;
	boolean fIgnoreModify = false;
	private IFormEntryListener fListener;

	public static final int F_DEFAULT_TEXT_WIDTH_HINT = 100;

	/**
	 * The default constructor. Call 'createControl' to make it.
	 *  
	 */
	public FormEntry(Composite parent, FormToolkit toolkit, String labelText, int style) {
		createControl(parent, toolkit, labelText, style, null, false, 0, 0);
	}

	/**
	 * This constructor create all the controls right away.
	 * 
	 * @param parent
	 * @param toolkit
	 * @param labelText
	 * @param browseText
	 * @param linkLabel
	 */
	public FormEntry(Composite parent, FormToolkit toolkit, String labelText, String browseText, boolean linkLabel) {
		this(parent, toolkit, labelText, browseText, linkLabel, 0);
	}

	public FormEntry(Composite parent, FormToolkit toolkit, String labelText, String browseText, boolean linkLabel, int indent) {
		createControl(parent, toolkit, labelText, SWT.SINGLE, browseText, linkLabel, indent, 0);
	}

	public FormEntry(Composite parent, FormToolkit toolkit, String labelText, int indent, int tcolspan) {
		createControl(parent, toolkit, labelText, SWT.SINGLE, null, false, indent, tcolspan);
	}

	/**
	 * Create all the controls in the provided parent.
	 * 
	 * @param parent
	 * @param toolkit
	 * @param labelText
	 * @param span
	 * @param browseText
	 * @param linkLabel
	 */
	private void createControl(Composite parent, FormToolkit toolkit, String labelText, int style, String browseText, boolean linkLabel, int indent, int tcolspan) {
		if (linkLabel) {
			Hyperlink link = toolkit.createHyperlink(parent, labelText, SWT.NULL);
			fLabel = link;
		} else {
			if (labelText != null) {
				fLabel = toolkit.createLabel(parent, labelText);
				fLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
			}
		}
		fText = toolkit.createText(parent, "", style); //$NON-NLS-1$
		addListeners();
		if (browseText != null) {
			fBrowse = toolkit.createButton(parent, browseText, SWT.PUSH);
			fBrowse.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (fListener != null)
						fListener.browseButtonSelected(FormEntry.this);
				}
			});
		}
		fillIntoGrid(parent, indent, tcolspan);
		// Set the default text width hint and let clients modify accordingly
		// after the fact
		setTextWidthHint(F_DEFAULT_TEXT_WIDTH_HINT);
	}

	public void setEditable(boolean editable) {
		fText.setEditable(editable);
		if (fLabel instanceof Hyperlink)
			((Hyperlink) fLabel).setUnderlined(editable);

		if (fBrowse != null)
			fBrowse.setEnabled(editable);
	}

	private void fillIntoGrid(Composite parent, int indent, int tcolspan) {
		Layout layout = parent.getLayout();
		int tspan;
		if (layout instanceof GridLayout) {
			int span = ((GridLayout) layout).numColumns;
			if (tcolspan > 0)
				tspan = tcolspan;
			else
				tspan = fBrowse != null ? span - 2 : span - 1;
			GridData gd;
			if (fLabel != null) {
				gd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
				gd.horizontalIndent = indent;
				fLabel.setLayoutData(gd);
			}
			gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			gd.horizontalSpan = tspan;
			if (fLabel != null) {
				gd.horizontalIndent = FormLayoutFactory.CONTROL_HORIZONTAL_INDENT;
			}
			gd.grabExcessHorizontalSpace = (tspan == 1);
			gd.widthHint = 10;
			fText.setLayoutData(gd);
			if (fBrowse != null) {
				gd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
				fBrowse.setLayoutData(gd);
			}
		} else if (layout instanceof TableWrapLayout) {
			int span = ((TableWrapLayout) layout).numColumns;
			if (tcolspan > 0)
				tspan = tcolspan;
			else
				tspan = fBrowse != null ? span - 2 : span - 1;
			TableWrapData td;
			if (fLabel != null) {
				td = new TableWrapData();
				td.valign = TableWrapData.MIDDLE;
				td.indent = indent;
				fLabel.setLayoutData(td);
			}
			td = new TableWrapData(TableWrapData.FILL);
			td.colspan = tspan;
			if (fLabel != null) {
				td.indent = FormLayoutFactory.CONTROL_HORIZONTAL_INDENT;
			}
			td.grabHorizontal = (tspan == 1);
			td.valign = TableWrapData.MIDDLE;
			fText.setLayoutData(td);
			if (fBrowse != null) {
				td = new TableWrapData(TableWrapData.FILL);
				td.valign = TableWrapData.MIDDLE;
				fBrowse.setLayoutData(td);
			}
		}
	}

	/**
	 * Attaches the listener for the entry.
	 * 
	 * @param listener
	 */
	public void setFormEntryListener(IFormEntryListener listener) {
		if (fLabel != null && fLabel instanceof Hyperlink) {
			if (this.fListener != null)
				((Hyperlink) fLabel).removeHyperlinkListener(this.fListener);
			if (listener != null)
				((Hyperlink) fLabel).addHyperlinkListener(listener);
		}
		this.fListener = listener;
	}

	private void addListeners() {
		fText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				keyReleaseOccured(e);
			}
		});
		fText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				editOccured(e);
			}
		});
		fText.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if (fListener != null)
					fListener.focusGained(FormEntry.this);
			}

			public void focusLost(FocusEvent e) {
				if (fDirty)
					commit();
			}
		});
	}

	/**
	 * If dirty, commits the text in the widget to the value and notifies the
	 * listener. This call clears the 'dirty' flag.
	 *  
	 */
	public void commit() {
		if (fDirty) {
			fValue = fText.getText();
			//if (value.length()==0)
			//value = null;
			//notify
			if (fListener != null)
				fListener.textValueChanged(this);
		}
		fDirty = false;
	}

	public void cancelEdit() {
		fDirty = false;
	}

	private void editOccured(ModifyEvent e) {
		if (fIgnoreModify)
			return;
		fDirty = true;
		if (fListener != null)
			fListener.textDirty(this);
	}

	/**
	 * Returns the text control.
	 * 
	 * @return
	 */
	public Text getText() {
		return fText;
	}

	public Control getLabel() {
		return fLabel;
	}

	/**
	 * Returns the browse button control.
	 * @return
	 */
	public Button getButton() {
		return fBrowse;
	}

	/**
	 * Returns the current entry value. If the entry is dirty and was not
	 * commited, the value may be different from the text in the widget.
	 * 
	 * @return
	 */
	public String getValue() {
		return fValue.trim();
	}

	/**
	 * Returns true if the text has been modified.
	 * 
	 * @return
	 */
	public boolean isDirty() {
		return fDirty;
	}

	private void keyReleaseOccured(KeyEvent e) {
		if (e.character == '\r') {
			// commit value
			if (fDirty)
				commit();
		} else if (e.character == '\u001b') { // Escape character
			if (!fValue.equals(fText.getText()))
				fText.setText(fValue != null ? fValue : ""); // restore old //$NON-NLS-1$
			fDirty = false;
		}
		if (fListener != null)
			fListener.selectionChanged(FormEntry.this);
	}

	/**
	 * Sets the value of this entry.
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		if (fText != null)
			fText.setText(value != null ? value : ""); //$NON-NLS-1$
		this.fValue = (value != null) ? value : ""; //$NON-NLS-1$
	}

	/**
	 * Sets the value of this entry with the possibility to turn the
	 * notification off.
	 * 
	 * @param value
	 * @param blockNotification
	 */
	public void setValue(String value, boolean blockNotification) {
		fIgnoreModify = blockNotification;
		setValue(value);
		fIgnoreModify = false;
	}

	public void setVisible(boolean visible) {
		if (fLabel != null)
			fLabel.setVisible(visible);
		if (fText != null)
			fText.setVisible(visible);
		if (fBrowse != null)
			fBrowse.setVisible(visible);
	}

	/**
	 * If GridData was used, set the width hint.  If TableWrapData was used
	 * set the max width.  If no layout data was specified, this method does
	 * nothing.
	 * @param width
	 */
	public void setTextWidthHint(int width) {
		Object data = getText().getLayoutData();
		if (data == null) {
			return;
		} else if (data instanceof GridData) {
			((GridData) data).widthHint = width;
		} else if (data instanceof TableWrapData) {
			((TableWrapData) data).maxWidth = width;
		}
	}
}
