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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.project.ui.ProjectUI;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import org.osgi.framework.Bundle;

/**
 * @author Simon Jiang
 */
public abstract class PageAction {

	public PageAction(String pageActionName) {
		images = new Image[5];
		this.pageActionName = pageActionName;
	}

	public Rectangle drawImage(GC gc, Image image, int cX, int cY) {
		Rectangle bounds = image.getBounds();

		cX -= bounds.width / 2;
		cY -= bounds.height / 2;

		gc.drawImage(image, cX, cY);

		return new Rectangle(cX, cY, bounds.width, bounds.height);
	}

	public Image getBageImage() {
		return images[4];
	}

	public Image[] getImages() {
		return images;
	}

	public final String getPageActionName() {
		return pageActionName;
	}

	public Point getSize() {
		Rectangle bounds = images[2].getBounds();

		return new Point(bounds.width, bounds.height);
	}

	public final Image loadImage(String name) {
		URL url = null;

		try {
			ProjectUI projectUI = ProjectUI.getDefault();

			Bundle bundle = projectUI.getBundle();

			url = bundle.getEntry("images/" + name);
		}
		catch (Exception e) {
		}

		ImageDescriptor imagedesc = ImageDescriptor.createFromURL(url);

		Image image = imagedesc.createImage();

		return image;
	}

	protected Image[] images;
	protected String pageActionName;

}