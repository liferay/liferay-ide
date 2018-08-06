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

package com.liferay.ide.service.ui.actions;

import com.liferay.ide.service.core.operation.ServiceBuilderDescriptorHelper;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphirePart;

/**
 * @author Kuo Zhang
 */
public class AddDefaultEntityAction extends SapphireActionHandler {

	public AddDefaultEntityAction() {
	}

	@Override
	protected Object run(Presentation context) {
		SapphirePart sapphirePart = context.part();

		Element localModelElement = sapphirePart.getLocalModelElement();

		IFile serviceXML = localModelElement.adapt(IFile.class);

		new ServiceBuilderDescriptorHelper(serviceXML.getProject()).addDefaultEntity();

		return null;
	}

}