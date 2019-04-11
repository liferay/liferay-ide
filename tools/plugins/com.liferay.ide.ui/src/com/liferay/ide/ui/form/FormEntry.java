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
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * The helper class for creating entry fields with label and text. Optionally, a
 * button can be added after the text. The attached listener reacts to all the
 * events. Entring new text makes the entry 'dirty', but only when 'commit' is
 * called is 'valueChanged' method called (and only if 'dirty' flag is set).
 * This allows delayed commit.
 * @author Gregory Amerson
 */
public class FormEntry {

	public static final int F_DEFAULT_TEXT_WIDTH_HINT = 100;

	/**
	 * The default constructor. Call 'createControl' to make it.
	 */
	public FormEntry(Composite parent, FormToolkit toolkit, String labelText, int style) {
		_createControl(parent, toolkit, labelText, style, null, false, 0, 0);
	}

	public FormEntry(Composite parent, FormToolkit toolkit, String labelText, int style, int indent, int tcolspan) {
		_createControl(parent, toolkit, labelText, style, null, false, indent, tcolspan);
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
	public FormEntry(
		Composite parent, FormToolkit toolkit, String labelText, String browseText, int style, boolean linkLabel) {

		this(parent, toolkit, labelText, browseText, style, linkLabel, 0);
	}

	public FormEntry(
		Composite parent, FormToolkit toolkit, String labelText, String browseText, int style, boolean linkLabel,
		int indent) {

		_createControl(parent, toolkit, labelText, style, browseText, linkLabel, indent, 0);
	}

	public void cancelEdit() {
		_fDirty = false;
	}

	/**
	 * If dirty, commits the text in the widget to the value and notifies the
	 * listener. This call clears the 'dirty' flag.
	 */
	public void commit() {
		if (_fDirty) {
			_fValue = _fText.getText();

			// if (value.length()==0)
			// value = null;
			// notify

			if (_fListener != null) {
				_fListener.textValueChanged(this);
			}
		}

		_fDirty = false;
	}

	/**
	 * Returns the browse button control.
	 *
	 * @return
	 */
	public Button getButton() {
		return _fBrowse;
	}

	public Control getLabel() {
		return _fLabel;
	}

	/**
	 * Returns the text control.
	 *
	 * @return
	 */
	public Text getText() {
		return _fText;
	}

	/**
	 * Returns the current entry value. If the entry is dirty and was not
	 * commited, the value may be different from the text in the widget.
	 *
	 * @return
	 */
	public String getValue() {
		return _fValue.trim();
	}

	/**
	 * Returns true if the text has been modified.
	 *
	 * @return
	 */
	public boolean isDirty() {
		return _fDirty;
	}

	public void setEditable(boolean editable) {
		_fText.setEditable(editable);

		if (_fLabel instanceof Hyperlink) {
			((Hyperlink)_fLabel).setUnderlined(editable);
		}

		if (_fBrowse != null) {
			_fBrowse.setEnabled(editable);
		}
	}

	/**
	 * Attaches the listener for the entry.
	 *
	 * @param listener
	 */
	public void setFormEntryListener(IFormEntryListener listener) {
		if ((_fLabel != null) && _fLabel instanceof Hyperlink) {
			if (_fListener != null) {
				((Hyperlink)_fLabel).removeHyperlinkListener(_fListener);
			}

			if (listener != null) {
				((Hyperlink)_fLabel).addHyperlinkListener(listener);
			}
		}

		_fListener = listener;
	}

	/**
	 * If GridData was used, set the width hint. If TableWrapData was used set
	 * the max width. If no layout data was specified, this method does nothing.
	 *
	 * @param width
	 */
	public void setTextWidthHint(int width) {
		Object data = getText().getLayoutData();

		if (data == null) {
			return;
		}
		else if (data instanceof GridData) {
			((GridData)data).widthHint = width;
		}
		else if (data instanceof TableWrapData) {
			((TableWrapData)data).maxWidth = width;
		}
	}

	/**
	 * Sets the value of this entry.
	 *
	 * @param value
	 */
	public void setValue(String value) {
		if (_fText != null) {
			_fText.setText((value != null) ? value : "");
		}

		_fValue = (value != null) ? value : "";
	}

	/**
	 * Sets the value of this entry with the possibility to turn the
	 * notification off.
	 *
	 * @param value
	 * @param blockNotification
	 */
	public void setValue(String value, boolean blockNotification) {
		_fIgnoreModify = blockNotification;
		setValue(value);
		_fIgnoreModify = false;
	}

	public void setVisible(boolean visible) {
		if (_fLabel != null) {
			_fLabel.setVisible(visible);
		}

		if (_fText != null) {
			_fText.setVisible(visible);
		}

		if (_fBrowse != null) {
			_fBrowse.setVisible(visible);
		}
	}

	private void _addListeners() {
		_fText.addKeyListener(
			new KeyAdapter() {

				public void keyReleased(KeyEvent e) {
					if (e.keyCode != SWT.CR) {
						_keyReleaseOccured(e);
					}
				}

			});

		_fText.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					_editOccured();
				}

			});

		_fText.addFocusListener(
			new FocusAdapter() {

				public void focusGained(FocusEvent e) {
					if (_fListener != null) {
						_fListener.focusGained(FormEntry.this);
					}
				}

				public void focusLost(FocusEvent e) {
					if (_fDirty) {
						commit();
					}
				}

			});
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
	private void _createControl(
		Composite parent, FormToolkit toolkit, String labelText, int style, String browseText, boolean linkLabel,
		int indent, int tcolspan) {

		if (linkLabel) {
			Hyperlink link = toolkit.createHyperlink(parent, labelText, SWT.NULL);

			_fLabel = link;
		}
		else {
			if (labelText != null) {
				_fLabel = toolkit.createLabel(parent, labelText);

				FormColors colors = toolkit.getColors();

				_fLabel.setForeground(colors.getColor(IFormColors.TITLE));
			}
		}

		_fText = toolkit.createText(parent, "", style);

		_addListeners();

		if (browseText != null) {
			_fBrowse = toolkit.createButton(parent, browseText, SWT.PUSH);

			_fBrowse.addSelectionListener(
				new SelectionAdapter() {

					public void widgetSelected(SelectionEvent e) {
						if (_fListener != null) {
							_fListener.browseButtonSelected(FormEntry.this);
						}
					}

				});
		}

		_fillIntoGrid(parent, indent, tcolspan);

		// Set the default text width hint and let clients modify accordingly
		// after the fact

		setTextWidthHint(F_DEFAULT_TEXT_WIDTH_HINT);
	}

	private void _editOccured() {
		if (_fIgnoreModify) {
			return;
		}

		_fDirty = true;

		if (_fListener != null) {
			_fListener.textDirty(this);
		}
	}

	private void _fillIntoGrid(Composite parent, int indent, int tcolspan) {
		Layout layout = parent.getLayout();
		int tspan;

		if (layout instanceof GridLayout) {
			int span = ((GridLayout)layout).numColumns;

			if (tcolspan > 0) {
				tspan = tcolspan;
			}
			else {
				tspan = (_fBrowse != null) ? span - 2 : span - 1;
			}

			GridData gd;

			if (_fLabel != null) {
				gd = new GridData(GridData.VERTICAL_ALIGN_CENTER);

				gd.horizontalIndent = indent;
				_fLabel.setLayoutData(gd);
			}

			gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);

			gd.horizontalSpan = tspan;

			if (_fLabel != null) {
				gd.horizontalIndent = FormLayoutFactory.CONTROL_HORIZONTAL_INDENT;
			}

			gd.grabExcessHorizontalSpace = tspan == 1;
			gd.widthHint = 10;
			_fText.setLayoutData(gd);

			if (_fBrowse != null) {
				gd = new GridData(GridData.VERTICAL_ALIGN_CENTER);

				_fBrowse.setLayoutData(gd);
			}
		}
		else if (layout instanceof TableWrapLayout) {
			int span = ((TableWrapLayout)layout).numColumns;

			if (tcolspan > 0) {
				tspan = tcolspan;
			}
			else {
				tspan = (_fBrowse != null) ? span - 2 : span - 1;
			}

			TableWrapData td;

			if (_fLabel != null) {
				td = new TableWrapData();

				td.valign = TableWrapData.MIDDLE;
				td.indent = indent;
				_fLabel.setLayoutData(td);
			}

			td = new TableWrapData(TableWrapData.FILL);

			td.colspan = tspan;

			if (_fLabel != null) {
				td.indent = FormLayoutFactory.CONTROL_HORIZONTAL_INDENT;
			}

			td.grabHorizontal = tspan == 1;
			td.valign = TableWrapData.MIDDLE;
			_fText.setLayoutData(td);

			if (_fBrowse != null) {
				td = new TableWrapData(TableWrapData.FILL);

				td.valign = TableWrapData.MIDDLE;
				_fBrowse.setLayoutData(td);
			}
		}
	}

	private void _keyReleaseOccured(KeyEvent e) {
		if (e.character == '\r') {

			// commit value

			if (_fDirty) {
				commit();
			}
		}
		else if (e.character == '\u001b') {
			if (!_fValue.equals(_fText.getText())) {

				// restore old

				_fText.setText((_fValue != null) ? _fValue : "");
			}

			_fDirty = false;
		}

		if (_fListener != null) {
			_fListener.selectionChanged(FormEntry.this);
		}
	}

	private Button _fBrowse;
	private boolean _fDirty;
	private boolean _fIgnoreModify = false;
	private Control _fLabel;
	private IFormEntryListener _fListener;
	private Text _fText;
	private String _fValue = "";

}