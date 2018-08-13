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

package com.liferay.blade.eclipse.provider;

import com.liferay.blade.api.SearchResult;
import com.liferay.blade.api.XMLFile;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

import org.osgi.service.component.annotations.Component;

import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 */
@Component(property = "file.extension=xml")
@SuppressWarnings("restriction")
public class SSEXMLFile extends WorkspaceFile implements XMLFile {

	@Override
	public List<SearchResult> findElement(String tagName, String value) {
		List<SearchResult> results = new ArrayList<>();

		IFile xmlFile = getIFile(file);
		IDOMModel domModel = null;

		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();

			domModel = (IDOMModel)modelManager.getModelForRead(xmlFile);

			IDOMDocument document = domModel.getDocument();

			NodeList elements = document.getElementsByTagName(tagName);

			if (elements != null) {
				for (int i = 0; i < elements.getLength(); i++) {
					IDOMElement element = (IDOMElement)elements.item(i);

					String textContent = element.getTextContent();

					value = value.trim();

					if ((textContent != null) && value.equals(textContent.trim())) {
						IStructuredDocument structuredDocument = document.getStructuredDocument();

						int startOffset = element.getStartOffset();
						int endOffset = element.getEndOffset();
						int startLine = structuredDocument.getLineOfOffset(startOffset) + 1;
						int endLine = structuredDocument.getLineOfOffset(endOffset) + 1;

						SearchResult result = new SearchResult(
							file, "startOffset:" + startOffset, startOffset, endOffset, startLine, endLine, true);

						results.add(result);
					}
				}
			}
		}
		catch (Exception e) {
		}
		finally {
			if (domModel != null) {
				domModel.releaseFromRead();
			}
		}

		return results;
	}

}