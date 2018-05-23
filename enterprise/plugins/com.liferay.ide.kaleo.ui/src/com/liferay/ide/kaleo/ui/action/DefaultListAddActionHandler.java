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

package com.liferay.ide.kaleo.ui.action;

import com.liferay.ide.kaleo.core.model.Node;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.forms.PropertyEditorActionHandler;

/**
 * @author Gregory Amerson
 */
public abstract class DefaultListAddActionHandler extends PropertyEditorActionHandler {

	public static String getDefaultName(String initialName, Node newNode, Node[] nodes) {
		String newName = initialName;
		int count = 1;
		boolean newNameIsValid = false;

		do {
			newNameIsValid = true;

			for (Node node : nodes) {
				if (newName.equals(node.getName().content())) {
					newNameIsValid = false;

					break;
				}
			}

			if (!newNameIsValid) {
				newName = newName.replace(Integer.toString(count), "") + (++count);
			}
		}
		while (!newNameIsValid);

		return newName;
	}

	public DefaultListAddActionHandler(ElementType type, ListProperty property) {
		_type = type;
		_property = property;
	}

	public ElementList<Element> getList() {
		Element modelElement = getModelElement();

		if (modelElement != null) {
			return modelElement.property(_property);
		}

		return null;
	}

	@Override
	public void init(SapphireAction action, ActionHandlerDef def) {
		super.init(action, def);

		ImageData typeSpecificAddImage = _type.image();

		if (typeSpecificAddImage != null) {
			addImage(typeSpecificAddImage);
		}

		setLabel(_type.getLabel(false, CapitalizationType.TITLE_STYLE, false));
	}

	@Override
	protected Object run(Presentation context) {
		return getList().insert(_type);
	}

	private final ListProperty _property;
	private final ElementType _type;

}