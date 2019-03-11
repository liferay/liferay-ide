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

package com.liferay.ide.upgrade.plan.ui.internal.tasks;

import com.liferay.ide.upgrade.plan.ui.Disposable;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * @author Gregory Amerson
 */
public interface UpgradeItem extends Disposable, ISelectionProvider {

	public default ImageHyperlink createImageHyperlink(
		FormToolkit formToolkit, Composite parentComposite, Image image, Object data, String linkText) {

		ImageHyperlink imageHyperlink = formToolkit.createImageHyperlink(parentComposite, SWT.NULL);

		imageHyperlink.setData(data);
		imageHyperlink.setImage(image);
		imageHyperlink.setText(linkText);
		imageHyperlink.setToolTipText(linkText);

		return imageHyperlink;
	}

}