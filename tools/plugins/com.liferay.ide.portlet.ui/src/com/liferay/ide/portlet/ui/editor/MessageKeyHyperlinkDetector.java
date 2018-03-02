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

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.portlet.ui.util.MessageKey;
import com.liferay.ide.portlet.ui.util.NodeUtils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.search.core.util.DOMUtils;

import org.w3c.dom.Node;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class MessageKeyHyperlinkDetector extends AbstractHyperlinkDetector {

	public MessageKeyHyperlinkDetector() {
	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		IHyperlink[] retval = null;

		if (_shouldDetectHyperlinks(textViewer, region)) {
			IDocument document = textViewer.getDocument();
			int offset = region.getOffset();

			IDOMNode currentNode = DOMUtils.getNodeByOffset(document, offset);

			Node keyNode = NodeUtils.getMessageKey(currentNode);

			if (keyNode != null) {
				IRegion nodeRegion = new Region(
					currentNode.getStartOffset(), currentNode.getEndOffset() - currentNode.getStartOffset());
				long modStamp = ((IDocumentExtension4)document).getModificationStamp();
				IFile file = DOMUtils.getFile(document);

				MessageKey[] messageKeys = null;

				if (file.equals(_lastFile) && (modStamp == _lastModStamp) && nodeRegion.equals(_lastNodeRegion)) {
					messageKeys = _lastMessageKeys;
				}
				else {
					String key = keyNode.getNodeValue();

					// search for message key in content/Langauge.properties

					messageKeys = NodeUtils.findMessageKeys(document, key, false);

					_lastModStamp = modStamp;
					_lastFile = file;
					_lastNodeRegion = nodeRegion;
					_lastMessageKeys = messageKeys;
				}

				if (ListUtil.isNotEmpty(messageKeys)) {
					List<IHyperlink> links = new ArrayList<>();

					for (MessageKey messageKey : messageKeys) {
						links.add(
							new MessageKeyHyperlink(
								nodeRegion, messageKey.file, messageKey.key, messageKey.offset, messageKey.length));
					}

					if (ListUtil.isNotEmpty(links)) {
						if (canShowMultipleHyperlinks) {
							retval = links.toArray(new IHyperlink[0]);
						}
						else {
							retval = new IHyperlink[] {links.get(0)};
						}
					}
				}
			}
		}

		return retval;
	}

	private boolean _shouldDetectHyperlinks(ITextViewer textViewer, IRegion region) {
		if ((region != null) && (textViewer != null)) {
			return true;
		}

		return false;
	}

	private IFile _lastFile;
	private MessageKey[] _lastMessageKeys;
	private long _lastModStamp;
	private IRegion _lastNodeRegion;

}