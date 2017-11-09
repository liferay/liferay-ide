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

package com.liferay.ide.alloy.core.jsp;

import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslation;
import org.eclipse.wst.jsdt.web.core.javascript.IJsTranslator;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslation;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class AlloyJsTranslation extends JsTranslation {

	public AlloyJsTranslation() {
	}

	public AlloyJsTranslation(IStructuredDocument doc, IJavaScriptProject project, boolean listen) {
		super(doc, project, listen);
	}

	@Override
	public IJsTranslation getInstance(IStructuredDocument doc, IJavaScriptProject project, boolean listen) {
		return new AlloyJsTranslation(doc, project, listen);
	}

	public IJsTranslator getTranslator() {
		if (fTranslator != null) {
			return fTranslator;
		}

		fTranslator = new AlloyJsTranslator(fHtmlDocument, fModelBaseLocation, listenForChanges);

		return fTranslator;
	}

}