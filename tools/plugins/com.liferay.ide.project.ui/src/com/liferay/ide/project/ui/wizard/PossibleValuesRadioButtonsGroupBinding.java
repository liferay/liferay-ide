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

import com.liferay.ide.project.ui.ProjectUI;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.LocalizableText;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Text;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.util.MiscUtil;
import org.eclipse.sapphire.services.ValueImageService;
import org.eclipse.sapphire.services.ValueLabelService;
import org.eclipse.sapphire.ui.forms.PropertyEditorDef;
import org.eclipse.sapphire.ui.forms.swt.AbstractBinding;
import org.eclipse.sapphire.ui.forms.swt.PropertyEditorPresentation;
import org.eclipse.sapphire.ui.forms.swt.RadioButtonsGroup;
import org.eclipse.sapphire.ui.forms.swt.SwtResourceCache;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin
 *         Komissarchik</a>
 */
public final class PossibleValuesRadioButtonsGroupBinding<T> extends AbstractBinding {

	public PossibleValuesRadioButtonsGroupBinding(
		final PropertyEditorPresentation propertyEditorPresentation, final RadioButtonsGroup buttonsGroup) {

		super(propertyEditorPresentation, buttonsGroup);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Value<T> property() {
		return (Value<T>)super.property();
	}

	@Override
	protected final void doUpdateModel() {
		final int index = _getSelectionIndex();

		if ((index >= 0) && (index < _possibleValues.size())) {
			property().write(_possibleValues.get(index));
			_removeMalformedItem();
		}
	}

	@Override
	protected final void doUpdateTarget() {
		final int existingSelection = _getSelectionIndex();
		final Value<T> value = property();

		int newSelection = _possibleValues.size();

		if (!value.malformed()) {
			final T newValue = value.content(true);

			for (int i = 0, n = _possibleValues.size(); i < n; i++) {
				if (_possibleValues.get(i).equals(newValue.toString())) {
					newSelection = i;

					break;
				}
			}
		}

		if (newSelection == _possibleValues.size()) {
			final String newValueString = value.text(true);

			final String label = newValueString == null ? _nullValueLabel.text() : newValueString;

			_createMalformedItem(label);
		}
		else {
			_removeMalformedItem();
		}

		if (existingSelection != newSelection) {
			_setSelectionIndex(newSelection);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void initialize(PropertyEditorPresentation propertyEditorPresentation, Control control) {
		super.initialize(propertyEditorPresentation, control);

		final PossibleValuesService possibleValuesService =
			propertyEditorPresentation.property().service(PossibleValuesService.class);

		_possibleValues = new ArrayList<>(possibleValuesService.values());

		_buttonsGroup = (RadioButtonsGroup)control;

		final Property property = propertyEditorPresentation.property();

		String auxTextProviderName =
			propertyEditorPresentation.part().getRenderingHint("possible.values.aux.text.provider", (String)null);
		PossibleValuesAuxTextProvider auxTextProvider = null;

		if (auxTextProviderName != null) {
			try {
				Bundle bundle = ProjectUI.getDefault().getBundle();

				Class<PossibleValuesAuxTextProvider> providerClass =
					(Class<PossibleValuesAuxTextProvider>)bundle.loadClass(auxTextProviderName);

				auxTextProvider = providerClass.newInstance();
			}
			catch (Exception e) {
			}
		}

		for (String possibleValue : _possibleValues) {
			final ValueLabelService labelService = property.service(ValueLabelService.class);

			final String possibleValueText = labelService.provide(possibleValue);

			String auxText = propertyEditorPresentation.part().getRenderingHint(
				PropertyEditorDef.HINT_AUX_TEXT + "." + possibleValue, null);

			if ((auxText == null) && (auxTextProvider != null)) {
				auxText = auxTextProvider.getAuxText(element(), property.definition(), possibleValue);
			}

			ValueImageService imageService = property.service(ValueImageService.class);

			ImageData imageData = imageService.provide(possibleValue);

			SwtResourceCache resources = presentation().resources();

			Image image = resources.image(imageData);

			final Button button = this._buttonsGroup.addRadioButton(possibleValueText, auxText, image);

			button.setData(possibleValue);
		}

		_buttonsGroup.addSelectionListener(
			new SelectionAdapter() {

				public void widgetSelected(final SelectionEvent event) {
					updateModel();
					updateTargetAttributes();
				}

			});
	}

	private void _createMalformedItem(String label) {
		if (_badValueButton == null) {
			_badValueButton = _buttonsGroup.addRadioButton(MiscUtil.EMPTY_STRING);
		}

		_badValueButton.setText(label);
		presentation().layout();
	}

	private int _getSelectionIndex() {
		return _buttonsGroup.getSelectionIndex();
	}

	private void _removeMalformedItem() {
		if (!this._buttonsGroup.isDisposed()) {
			if (_badValueButton != null) {
				_badValueButton.dispose();

				_badValueButton = null;

				presentation().layout();
			}
		}
	}

	private void _setSelectionIndex(final int index) {
		_buttonsGroup.setSelectionIndex(index);
	}

	@Text("<value not set>")
	private static LocalizableText _nullValueLabel;

	static {
		LocalizableText.init(PossibleValuesRadioButtonsGroupBinding.class);
	}

	private Button _badValueButton;
	private RadioButtonsGroup _buttonsGroup;
	private List<String> _possibleValues;

}