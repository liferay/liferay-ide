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

package com.liferay.ide.layouttpl.ui.parts;

import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

/**
 * @author Gregory Amerson
 */
public class LayoutTplEditPartFactory implements EditPartFactory {

	public LayoutTplEditPartFactory() {
	}

	public EditPart createEditPart(EditPart context, Object modelElement) {
		EditPart part = getPartForElement(modelElement);

		part.setModel(modelElement);

		return part;
	}

	protected EditPart getPartForElement(Object modelElement) {
		if (modelElement instanceof LayoutTplElement) {
			return new LayoutTplDiagramEditPart();
		}

		if (modelElement instanceof PortletLayoutElement) {
			return new PortletLayoutEditPart();
		}

		if (modelElement instanceof PortletColumnElement) {
			return new PortletColumnEditPart();
		}

		Class<?> modelElementClass = modelElement.getClass();

		String message = (modelElement != null) ? modelElementClass.getName() : "null";

		throw new RuntimeException("Can not create part for model element: " + message);
	}

}