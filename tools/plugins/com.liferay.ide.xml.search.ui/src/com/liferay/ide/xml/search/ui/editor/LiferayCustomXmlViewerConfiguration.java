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

package com.liferay.ide.xml.search.ui.editor;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.sse.core.text.IStructuredPartitions;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.taginfo.AnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.ProblemAnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.TextHoverManager;
import org.eclipse.wst.xml.core.text.IXMLPartitions;
import org.eclipse.wst.xml.search.editor.XMLReferencesStructuredTextViewerConfiguration;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class LiferayCustomXmlViewerConfiguration extends XMLReferencesStructuredTextViewerConfiguration {

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		ITextHover textHover = null;

		/*
		 * Returns a default problem, annotation, and best match hover depending
		 * on stateMask
		 */
		SSEUIPlugin plugin = SSEUIPlugin.getDefault();

		TextHoverManager hoverManager = plugin.getTextHoverManager();

		TextHoverManager.TextHoverDescriptor[] hoverDescs = hoverManager.getTextHovers();

		int i = 0;

		while ((i < hoverDescs.length) && (textHover == null)) {
			if (hoverDescs[i].isEnabled() && (computeStateMask(hoverDescs[i].getModifierString()) == stateMask)) {
				String hoverType = hoverDescs[i].getId();

				if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType)) {
					textHover = new ProblemAnnotationHoverProcessor();
				}
				else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType)) {
					textHover = new AnnotationHoverProcessor();
				}
				else if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType)) {
					textHover = createDocumentationHover(contentType);
				}
				else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType)) {
					textHover = createDocumentationHover(contentType);
				}
			}

			i++;
		}

		return textHover;
	}

	@Override
	protected ITextHover createDocumentationHover(String partitionType) {
		if ((partitionType == IStructuredPartitions.DEFAULT_PARTITION) ||
			(partitionType == IXMLPartitions.XML_DEFAULT)) {

			return new LiferayCustomXmlHover();
		}

		return super.createDocumentationHover(partitionType);
	}

}