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

import com.liferay.ide.core.util.ListUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Gregory Amerson
 */
public abstract class SharedPartWithButtons extends SharedPart {

	public SharedPartWithButtons(String[] buttonLabels) {
		_fButtonLabels = buttonLabels;
	}

	/**
	 * @see SharedPart#createControl(Composite, FormWidgetFactory)
	 */
	public void createControl(Composite parent, int style, int span, FormToolkit toolkit) {
		createMainLabel(parent, span, toolkit);
		createMainControl(parent, style, span - 1, toolkit);
		createButtons(parent, toolkit);
	}

	/**
	 * @param index
	 * @return
	 */
	public Button getButton(int index) {

		//
		if ((_fButtons == null) || (index < 0) || (index >= _fButtons.length)) {
			return null;
		}
		//

		return _fButtons[index];
	}

	public void setButtonEnabled(int index, boolean enabled) {
		if ((_fButtons != null) && (index >= 0) && (_fButtons.length > index)) {
			_fButtons[index].setEnabled(enabled);
		}
	}

	/**
	 * Set the specified button's visibility. Fix for defect 190717.
	 *
	 * @param index
	 *            The index of the button to be changed
	 * @param visible
	 *            true if the button is to be shown, false if hidden
	 */
	public void setButtonVisible(int index, boolean visible) {
		if ((_fButtons != null) && (index >= 0) && (_fButtons.length > index)) {
			_fButtons[index].setVisible(visible);
		}
	}

	protected abstract void buttonSelected(Button button, int index);

	protected Button createButton(Composite parent, String label, int index, FormToolkit toolkit) {
		Button button;

		if (toolkit != null) {
			button = toolkit.createButton(parent, label, SWT.PUSH);
		}
		else {
			button = new Button(parent, SWT.PUSH);

			button.setText(label);
		}

		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);

		button.setLayoutData(gd);

		button.setData(Integer.valueOf(index));

		return button;
	}

	protected void createButtons(Composite parent, FormToolkit toolkit) {
		if (ListUtil.isNotEmpty(_fButtonLabels)) {
			fButtonContainer = createComposite(parent, toolkit);
			GridData gd = new GridData(GridData.FILL_VERTICAL);

			fButtonContainer.setLayoutData(gd);

			fButtonContainer.setLayout(createButtonsLayout());
			_fButtons = new Button[_fButtonLabels.length];

			SelectionHandler listener = new SelectionHandler();

			for (int i = 0; i < _fButtonLabels.length; i++) {
				String label = _fButtonLabels[i];

				if (label != null) {
					Button button = createButton(fButtonContainer, label, i, toolkit);

					button.addSelectionListener(listener);

					_fButtons[i] = button;
				}
				else {
					createEmptySpace(fButtonContainer, 1, toolkit);
				}
			}
		}
	}

	protected GridLayout createButtonsLayout() {
		GridLayout layout = new GridLayout();

		layout.marginWidth = layout.marginHeight = 0;

		return layout;
	}

	protected abstract void createMainControl(Composite parent, int style, int span, FormToolkit toolkit);

	protected void createMainLabel(Composite parent, int span, FormToolkit toolkit) {
	}

	protected void updateEnabledState() {
		for (Button button : _fButtons) {
			button.setEnabled(isEnabled());
		}
	}

	protected Composite fButtonContainer;

	private String[] _fButtonLabels;
	private Button[] _fButtons;

	private class SelectionHandler implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
			_buttonSelected(e);
		}

		public void widgetSelected(SelectionEvent e) {
			_buttonSelected(e);
		}

		private void _buttonSelected(SelectionEvent e) {
			Integer index = (Integer)e.widget.getData();

			SharedPartWithButtons.this.buttonSelected((Button)e.widget, index.intValue());
		}

	}

}