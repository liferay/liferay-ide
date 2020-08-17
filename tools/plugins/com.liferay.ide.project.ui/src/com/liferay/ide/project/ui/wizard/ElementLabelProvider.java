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

import com.liferay.ide.project.ui.wizard.AbstractCheckboxCustomPart.CheckboxElement;
import com.liferay.ide.ui.navigator.AbstractLabelProvider;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Simon Jiang
 */
public abstract class ElementLabelProvider
	extends AbstractLabelProvider implements IColorProvider, IStyledLabelProvider {

	@Override
	public Color getBackground(Object element) {
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		return null;
	}

	@Override
	public abstract Image getImage(Object element);

	@Override
	public abstract StyledString getStyledText(Object element);

	@Override
	public String getText(Object element) {
		if (element instanceof CheckboxElement) {
			CheckboxElement checkboxElement = (CheckboxElement)element;

			return checkboxElement.context;
		}

		return super.getText(element);
	}

	@Override
	protected abstract void initalizeImageRegistry(ImageRegistry imageRegistry);

}