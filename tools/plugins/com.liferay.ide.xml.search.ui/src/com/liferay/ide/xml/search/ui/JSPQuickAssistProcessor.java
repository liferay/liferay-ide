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

package com.liferay.ide.xml.search.ui;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.xml.search.ui.validators.LiferayBaseValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.quickassist.IQuickFixableAnnotation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IMarkerHelpRegistry;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.sse.ui.internal.reconcile.TemporaryAnnotation;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class JSPQuickAssistProcessor implements IQuickAssistProcessor {

	@Override
	public boolean canAssist(IQuickAssistInvocationContext invocationContext) {
		return true;
	}

	@Override
	public boolean canFix(Annotation annotation) {
		if (annotation instanceof TemporaryAnnotation) {
			TemporaryAnnotation temp = (TemporaryAnnotation)annotation;

			Map attributes = temp.getAttributes();

			if ((attributes != null) && (attributes.get(LiferayBaseValidator.MARKER_QUERY_ID) != null)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public ICompletionProposal[] computeQuickAssistProposals(IQuickAssistInvocationContext context) {
		ICompletionProposal[] retval = null;

		List<ICompletionProposal> proposals = new ArrayList<>();

		ISourceViewer sourceViewer = context.getSourceViewer();

		IAnnotationModel annotationModel = sourceViewer.getAnnotationModel();

		Iterator<Annotation> annotations = annotationModel.getAnnotationIterator();

		while (annotations.hasNext()) {
			Annotation annotation = annotations.next();

			Position position = annotationModel.getPosition(annotation);

			try {
				IMarker marker = _createTempMarker(annotation);

				IDocument document = sourceViewer.getDocument();

				int lineNum = document.getLineOfOffset(position.getOffset()) + 1;
				int currentLineNum = document.getLineOfOffset(context.getOffset()) + 1;

				if ((marker != null) && (currentLineNum == lineNum) &&
					(marker.getAttribute(LiferayBaseValidator.MARKER_QUERY_ID, null) != null)) {

					ICompletionProposal[] resolutions = _createFromMarkerResolutions(marker);

					if (ListUtil.isNotEmpty(resolutions)) {
						Collections.addAll(proposals, resolutions);

						if (annotation instanceof IQuickFixableAnnotation) {
							IQuickFixableAnnotation quick = (IQuickFixableAnnotation)annotation;

							quick.setQuickFixable(true);
						}
					}
				}
			}
			catch (BadLocationException ble) {
				LiferayXMLSearchUI.logError("Error finding quick assists", ble);
			}
		}

		if (ListUtil.isNotEmpty(proposals)) {
			retval = proposals.toArray(new ICompletionProposal[0]);
		}

		return retval;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	private ICompletionProposal[] _createFromMarkerResolutions(IMarker marker) {
		List<ICompletionProposal> retval = new ArrayList<>();

		IMarkerHelpRegistry markerHelpRegistry = IDE.getMarkerHelpRegistry();

		if (markerHelpRegistry.hasResolutions(marker)) {
			IMarkerResolution[] resolutions = markerHelpRegistry.getResolutions(marker);

			for (IMarkerResolution resolution : resolutions) {
				if (resolution instanceof CommonWorkbenchMarkerResolution) {
					retval.add(new MarkerResolutionProposal(resolution, marker));
				}
			}
		}

		return retval.toArray(new ICompletionProposal[0]);
	}

	private IMarker _createTempMarker(Annotation annotation) {
		if (annotation instanceof TemporaryAnnotation) {
			TemporaryAnnotation temp = (TemporaryAnnotation)annotation;

			if (temp.getAttributes() != null) {
				return new TempMarker((TemporaryAnnotation)annotation);
			}
		}

		return null;
	}

}