/*
 * JBoss by Red Hat
 * Copyright 2006-2009, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ide.eclipse.freemarker.outline;


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.jboss.ide.eclipse.freemarker.ImageManager;
import org.jboss.ide.eclipse.freemarker.model.Item;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class OutlineLabelProvider
	extends LabelProvider {

	public OutlineLabelProvider() {
		super();
	}

	public Image getImage(Object anElement) {
		Image result = null;
		if (anElement instanceof Item) {
			String imageName = ((Item) anElement).getTreeImage();
			if(imageName != null) {
				result = ImageManager.getImage(imageName);
			}
		}
		return result;
	}

	public String getText(Object anElement) {
		String text = null;
		if (anElement instanceof Item) {
			text = ((Item) anElement).getTreeDisplay();
		}
		return text;
	}

	public void dispose() {
	}	
}
