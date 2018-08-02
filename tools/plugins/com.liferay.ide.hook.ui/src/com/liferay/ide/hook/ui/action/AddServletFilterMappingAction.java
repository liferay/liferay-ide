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

package com.liferay.ide.hook.ui.action;

import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.model.ServletFilterMapping;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphirePart;

/**
 * @author Gregory Amerson
 */
public class AddServletFilterMappingAction extends SapphireActionHandler {

	public AddServletFilterMappingAction() {
	}

	@Override
	protected Object run(Presentation context) {
		SapphirePart part = context.part();

		Element element = part.getLocalModelElement();

		Hook hook = element.nearest(Hook.class);

		ElementList<ServletFilterMapping> elementList = hook.getServletFilterMappings();

		return elementList.insert();
	}

}