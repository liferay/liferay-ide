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

package com.liferay.ide.ui.editor;

import com.liferay.ide.ui.LiferayUIPlugin;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;

/**
 * @author Gregory Amerson
 */
public class PropertyCompletionProposal
	implements ICompletionProposal, ICompletionProposalExtension3, ICompletionProposalExtension5 {

	public PropertyCompletionProposal(String key, String info, int offset, int rewindOffset) {
		_key = key;
		_info = info;
		_offset = offset;
		_rewindOffset = rewindOffset;
	}

	public void apply(IDocument document) {
		try {
			document.replace(_rewindOffset, _offset - _rewindOffset, _key);
		}
		catch (BadLocationException ble) {
			LiferayUIPlugin.logError("Unable to apply proposal", ble);
		}
	}

	public String getAdditionalProposalInfo() {
		return _info;
	}

	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		return _info;
	}

	public IContextInformation getContextInformation() {
		return null;
	}

	public String getDisplayString() {
		return _key;
	}

	public Image getImage() {
		ISharedImages sharedImages = UIUtil.getSharedImages();

		return sharedImages.getImage(ISharedImages.IMG_OBJ_ELEMENT);
	}

	public IInformationControlCreator getInformationControlCreator() {
		return new AbstractReusableInformationControlCreator() {

			@Override
			protected IInformationControl doCreateInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, true);
			}

		};
	}

	public int getPrefixCompletionStart(IDocument document, int completionOffset) {
		return _rewindOffset;
	}

	public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
		try {
			return document.get(_rewindOffset, completionOffset);
		}
		catch (BadLocationException ble) {
		}

		return null;
	}

	public Point getSelection(IDocument document) {
		int point = _rewindOffset + _key.length();

		return new Point(point, 0);
	}

	private final String _info;
	private final String _key;
	private final int _offset;
	private final int _rewindOffset;

}