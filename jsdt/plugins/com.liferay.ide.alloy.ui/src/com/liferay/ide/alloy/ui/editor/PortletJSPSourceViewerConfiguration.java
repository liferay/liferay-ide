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

package com.liferay.ide.alloy.ui.editor;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.xml.search.ui.editor.LiferayCustomXmlHover;

import java.util.List;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.jsdt.web.support.jsp.JSDTStructuredTextViewerConfigurationJSP;
import org.eclipse.wst.sse.ui.internal.ExtendedConfigurationBuilder;
import org.eclipse.wst.sse.ui.internal.SSEUIPlugin;
import org.eclipse.wst.sse.ui.internal.taginfo.AnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.ProblemAnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.internal.taginfo.TextHoverManager;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class PortletJSPSourceViewerConfiguration extends JSDTStructuredTextViewerConfigurationJSP {

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		ITextHover textHover = null;

		SSEUIPlugin plugin = SSEUIPlugin.getDefault();

		TextHoverManager textHoverManager = plugin.getTextHoverManager();

		TextHoverManager.TextHoverDescriptor[] hoverDescs = textHoverManager.getTextHovers();

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
					textHover = (ITextHover)new LiferayCustomXmlHover();
				}
				else if (TextHoverManager.DOCUMENTATION_HOVER.equalsIgnoreCase(hoverType)) {
					ITextHover[] hovers = createDocumentationHovers(contentType);

					if (ListUtil.isNotEmpty(hovers)) {
						textHover = hovers[0];
					}
				}
			}

			i++;
		}

		return textHover;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	protected ITextHover[] createDocumentationHovers(String partitionType) {
		ExtendedConfigurationBuilder instance = ExtendedConfigurationBuilder.getInstance();

		List extendedTextHover = instance.getConfigurations(
			ExtendedConfigurationBuilder.DOCUMENTATIONTEXTHOVER, partitionType);

		return (ITextHover[])extendedTextHover.toArray(new ITextHover[0]);
	}

}