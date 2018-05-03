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

package com.liferay.ide.project.ui.jdt;

import com.liferay.ide.project.ui.ProjectUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

/**
 * @author Simon Jiang
 */
public class ComponentPropertiesCompletionProposalComputer implements IJavaCompletionProposalComputer {

	@Override
	public List<ICompletionProposal> computeCompletionProposals(
		ContentAssistInvocationContext context, IProgressMonitor monitor) {

		List<ICompletionProposal> propsoalList = new ArrayList<>();

		try {
			JavaContentAssistInvocationContext jdtContext = (JavaContentAssistInvocationContext)context;

			int invocationOffset = jdtContext.getInvocationOffset();
			IDocument document = jdtContext.getDocument();

			IRegion lineRegion = document.getLineInformationOfOffset(invocationOffset);

			String source = "";
			String wholeLine = document.get(lineRegion.getOffset(), lineRegion.getLength());
			int wholeLineEndPos = lineRegion.getOffset() + lineRegion.getLength();
			int quotPos = wholeLine.indexOf("\"");
			boolean hasQuot = false;
			int replaceEndPos = invocationOffset;

			while (((quotPos + lineRegion.getOffset()) < wholeLineEndPos) && (quotPos != -1)) {
				int lastQuotPos = quotPos;

				quotPos = wholeLine.indexOf("\"", lastQuotPos + 1);

				if ((quotPos + lineRegion.getOffset()) >= invocationOffset) {
					hasQuot = true;
					int prefixStartPos = lineRegion.getOffset() + lastQuotPos + 1;

					if ((invocationOffset - prefixStartPos) < 0) {
						hasQuot = false;

						break;
					}

					source = document.get(prefixStartPos, invocationOffset - prefixStartPos).trim();

					if ((quotPos + lineRegion.getOffset()) > wholeLineEndPos) {
						replaceEndPos = wholeLineEndPos;
					}
					else {
						replaceEndPos = quotPos + lineRegion.getOffset();
					}

					break;
				}
			}

			String candidate = source.replace("\"", "");

			if ((candidate != null) && hasQuot) {
				int replaceStartPos = invocationOffset - candidate.trim().length();

				for (int i = 0; i < LiferayComponentProperties.CODE_ASSISTANT_RESOURCE.length - 1; i++) {
					final String[] propertyAssist = LiferayComponentProperties.CODE_ASSISTANT_RESOURCE[i];

					final String propertyKey = propertyAssist[0];

					final String propertyComment = propertyAssist[2];

					if ((candidate.length() > 0) && !propertyKey.startsWith(candidate)) {
						continue;
					}

					Image propertiesImage = ProjectUI.getPluginImageRegistry().get(ProjectUI.PROPERTIES_IMAGE_ID);
					final String replaceString = propertyKey + "=";

					propsoalList.add(
						new ComponentPropertyCompletionProposal(
							jdtContext, replaceString, jdtContext.getInvocationOffset(), replaceString.length(),
							propertiesImage, propertyKey, 0, replaceStartPos, replaceEndPos, propertyComment, source));
				}
			}
		}
		catch (BadLocationException ble) {
			ProjectUI.logError(ble);
		}

		return propsoalList;
	}

	@Override
	public List<IContextInformation> computeContextInformation(
		ContentAssistInvocationContext context, IProgressMonitor monitor) {

		return Collections.emptyList();
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public void sessionEnded() {
	}

	@Override
	public void sessionStarted() {
	}

}