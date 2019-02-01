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

package com.liferay.ide.upgrade.problems.core.internal;

import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.XMLFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocumentType;
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
	public FileSearchResult findDocumentTypeDeclaration(String name, Pattern idPattern) {
		FileSearchResult result = null;

		IFile xmlFile = getIFile(file);
		IDOMModel domModel = null;

		try {
			IModelManager modelManager = StructuredModelManager.getModelManager();

			domModel = (IDOMModel)modelManager.getModelForRead(xmlFile);

			IDOMDocument document = domModel.getDocument();

			IDOMDocumentType docType = (IDOMDocumentType)document.getDoctype();

			if (docType != null) {
				String docTypeName = docType.getName();

				String docTypePublicId = docType.getPublicId();

				Matcher m = idPattern.matcher(docTypePublicId);

				if (docTypeName.equals(name) && !m.matches()) {
					IStructuredDocument structuredDocument = document.getStructuredDocument();

					int startOffset = docType.getStartOffset();
					int endOffset = docType.getEndOffset();
					int startLine = structuredDocument.getLineOfOffset(startOffset) + 1;
					int endLine = structuredDocument.getLineOfOffset(endOffset) + 1;

					result = new FileSearchResult(
						file, "startOffset:" + startOffset, startOffset, endOffset, startLine, endLine, true);

					result.autoCorrectContext = "descriptor:dtd-version";
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

		return result;
	}

	@Override
	public List<FileSearchResult> findElement(String tagName, String value) {
		List<FileSearchResult> results = new ArrayList<>();

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

						FileSearchResult result = new FileSearchResult(
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