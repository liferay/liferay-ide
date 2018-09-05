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

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.osgi.util.TextProcessor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class ComponentPropertyCompletionProposal extends JavaCompletionProposal {

	public ComponentPropertyCompletionProposal(
		JavaContentAssistInvocationContext jdt, String replacementString, int replacementOffset, int replacementLength,
		Image image, String displayString, int relevance, int replacementStart, int replacementEnd,
		String addtionalString, String source) {

		super(replacementString, replacementOffset, replacementLength, image, displayString, relevance);

		_replacementStart = replacementStart;
		_replacementEnd = replacementEnd;
		_addtionalString = addtionalString;
		_source = source;
	}

	@Override
	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		String info = _addtionalString;

		return info;
	}

	@Override
	public IInformationControlCreator getInformationControlCreator() {
		return new AbstractReusableInformationControlCreator() {

			@Override
			protected IInformationControl doCreateInformationControl(final Shell parent) {
				return new DefaultInformationControl(parent, true);
			}

		};
	}

	public int getReplaceEnd() {
		return _replacementEnd;
	}

	public final int getReplacementLength() {
		if (!_fReplacementLengthComputed) {
			setReplacementLength(getReplaceEnd() - getReplaceStart());
		}

		if (_source.contains("\"") && _source.endsWith("\"")) {
			return super.getReplacementLength() + 1;
		}

		return super.getReplacementLength();
	}

	/**
	 * Gets the replacement offset.
	 *
	 * @return Returns a int
	 */
	@Override
	public final int getReplacementOffset() {
		if (!_fReplacementOffsetComputed) {
			setReplacementOffset(getReplaceStart());
		}

		return super.getReplacementOffset();
	}

	public int getReplaceStart() {
		if (_source.contains("\"")) {
			return _replacementStart - 1;
		}

		return _replacementStart; // default overridden by concrete implementation
	}

	public int setReplaceEnd(final int replacementEnd) {
		return _replacementEnd = replacementEnd;
	}

	/**
	 * Sets the replacement length.
	 *
	 * @param replacementLength
	 *            The replacementLength to set
	 */
	public void setReplacementLength(int replacementLength) {
		Assert.isTrue(replacementLength >= 0);
		super.setReplacementLength(replacementLength);
	}

	@Override
	public boolean validate(IDocument document, int offset, DocumentEvent event) {
		if (!isOffsetValid(offset)) {
			return _fIsValidated = false;
		}

		String prefix = getPrefix(document, offset);

		String removeQuotPrefix = prefix.replace("\"", "");

		_fIsValidated = isValidPrefix(removeQuotPrefix);

		if (_fIsValidated) {
			setReplaceEnd(getReplaceEnd());
		}

		if (_fIsValidated && (event != null)) {
			int delta = (event.fText == null ? 0 : event.fText.length()) - event.fLength;

			final int newLength = Math.max(getReplacementLength() + delta, 0);

			setReplacementLength(newLength);

			setReplaceEnd(getReplaceEnd() + delta);
		}

		return _fIsValidated;
	}

	protected boolean isOffsetValid(int offset) {
		if (getReplacementOffset() <= offset) {
			return true;
		}

		return false;
	}

	protected boolean isValidPrefix(String prefix) {
		String word = TextProcessor.deprocess(getDisplayString());

		return isPrefix(prefix, word);
	}

	private String _addtionalString;
	private boolean _fIsValidated = true;
	private boolean _fReplacementLengthComputed;
	private boolean _fReplacementOffsetComputed;
	private int _replacementEnd;
	private int _replacementStart;
	private String _source;

}