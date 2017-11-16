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

package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.portlet.ui.PortletUIPlugin;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * @author Gregory Amerson
 */
public class BasicJavaElementHyperlink implements IHyperlink {

	public BasicJavaElementHyperlink(IRegion region, IJavaElement javaElement) {
		_region = region;
		_javaElement = javaElement;
	}

	public IRegion getHyperlinkRegion() {
		return _region;
	}

	public String getHyperlinkText() {
		String elementLabel = JavaElementLabels.getElementLabel(_javaElement, JavaElementLabels.ALL_POST_QUALIFIED);

		return "Open " + elementLabel;
	}

	public String getTypeLabel() {
		return null;
	}

	public void open() {
		try {
			JavaUI.openInEditor(_javaElement);
		}
		catch (Exception e) {
			PortletUIPlugin.logError("Unable to open java editor for element " + _javaElement, e);
		}
	}

	private IJavaElement _javaElement;
	private IRegion _region;

}