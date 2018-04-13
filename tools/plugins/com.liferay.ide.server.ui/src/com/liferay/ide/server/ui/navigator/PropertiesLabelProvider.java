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

package com.liferay.ide.server.ui.navigator;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Gregory Amerson
 */
public class PropertiesLabelProvider extends LabelProvider implements ILightweightLabelDecorator {

	public PropertiesLabelProvider() {
	}

	public void decorate(Object element, IDecoration decoration) {
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof PropertiesFile) {
			IWorkbench workbench = PlatformUI.getWorkbench();

			ISharedImages sharedImage = workbench.getSharedImages();

			return sharedImage.getImage(ISharedImages.IMG_OBJ_FILE);
		}

		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PropertiesFile) {
			PropertiesFile file = (PropertiesFile)element;

			return file.getName();
		}

		return null;
	}

}