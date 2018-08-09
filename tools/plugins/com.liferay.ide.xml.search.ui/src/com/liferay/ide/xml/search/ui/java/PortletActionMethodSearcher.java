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

package com.liferay.ide.xml.search.ui.java;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.xml.search.editor.contentassist.IContentAssistProposalRecorder;
import org.eclipse.wst.xml.search.editor.searchers.javamethod.XMLSearcherForJavaMethod;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class PortletActionMethodSearcher extends XMLSearcherForJavaMethod {

	public PortletActionMethodSearcher() {
		_javaElementImageProvider = new JavaElementImageProvider();
	}

	@Override
	protected void createMethodProposal(
		IContentAssistProposalRecorder recorder, IMethod method, String methodNameToUse) {

		try {
			IJavaElement element = method.getParent();

			String displayText = methodNameToUse + " - " + element.getElementName();

			Image image = _javaElementImageProvider.getImageLabel(method, method.getFlags() | 2);

			recorder.recordProposal(image, 10, displayText, methodNameToUse, null);
		}
		catch (JavaModelException jme) {
		}
	}

	private final JavaElementImageProvider _javaElementImageProvider;

}