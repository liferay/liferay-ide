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

package com.liferay.ide.portlet.ui.util;

import com.liferay.ide.core.properties.PropertiesFileLookup;
import com.liferay.ide.core.properties.PropertiesFileLookup.KeyInfo;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.search.core.util.DOMUtils;

import org.w3c.dom.Node;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class NodeUtils {

	public static MessageKey[] findMessageKeys(IDocument document, String key, boolean loadValues) {
		MessageKey[] retval = null;

		IFile file = DOMUtils.getFile(document);

		if ((file != null) && file.exists()) {
			IJavaProject project = JavaCore.create(file.getProject());

			if ((project != null) && project.exists()) {
				List<MessageKey> keys = new ArrayList<>();

				for (IFolder src : CoreUtil.getSourceFolders(project)) {
					if (src.exists()) {
						IFile[] props = PropertiesUtil.visitPropertiesFiles(src, ".*");

						for (IFile prop : props) {
							try {
								KeyInfo info = new PropertiesFileLookup(
									prop.getContents(), key, loadValues).getKeyInfo(key);

								if ((info != null) && (info.offset >= 0)) {
									keys.add(new MessageKey(prop, key, info.offset, info.length, info.value));
								}
							}
							catch (CoreException ce) {
							}
						}
					}
				}

				retval = keys.toArray(new MessageKey[0]);
			}
		}

		return retval;
	}

	public static Node getMessageKey(IDOMNode currentNode) {
		Node retval = null;

		boolean messageNode = false;

		if ((currentNode != null) && (currentNode.getNodeName() != null) &&
			(currentNode.getNodeType() == Node.ELEMENT_NODE) &&
			(currentNode.getNodeName().endsWith("message") || currentNode.getNodeName().endsWith("error") ||
			 _isAuiLabel(currentNode))) {

			messageNode = true;
		}

		if (messageNode) {
			Node key = currentNode.getAttributes().getNamedItem("key");

			if ((key != null) && !CoreUtil.isNullOrEmpty(key.getNodeValue())) {
				retval = key;
			}
			else {
				Node label = currentNode.getAttributes().getNamedItem("label");

				if ((label != null) && !CoreUtil.isNullOrEmpty(label.getNodeValue())) {
					retval = label;
				}
			}
		}

		return retval;
	}

	private static boolean _isAuiLabel(IDOMNode currentNode) {
		if (currentNode.getNodeName().startsWith("aui:") &&
			(currentNode.getAttributes().getNamedItem("label") != null)) {

			return true;
		}

		return false;
	}

}