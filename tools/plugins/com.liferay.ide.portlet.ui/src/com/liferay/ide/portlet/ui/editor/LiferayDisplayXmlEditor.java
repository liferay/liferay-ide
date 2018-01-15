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

import com.liferay.ide.portlet.core.display.model.Display6xx;

import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.ui.PartInitException;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
public class LiferayDisplayXmlEditor extends SapphireEditorForXml {

	public LiferayDisplayXmlEditor() {
		super(Display6xx.TYPE, null);
	}

	@Override
	protected void createFormPages() throws PartInitException {
		addDeferredPage(1, "Overview", "DetailsPage");
	}

}