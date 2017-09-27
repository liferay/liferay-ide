/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.eclipse.provider;

import com.liferay.blade.api.SearchResult;
import com.liferay.blade.api.XMLFile;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.core.StructuredModelManager;
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
		List<SearchResult> _results = new ArrayList<>();

		IFile xmlFile = getIFile(_file);
		IDOMModel domModel = null;

		try {
			domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead(xmlFile);

			IDOMDocument document = domModel.getDocument();

			NodeList elements = document.getElementsByTagName(tagName);

			if (elements != null) {
				for (int i = 0; i < elements.getLength(); i++) {
					IDOMElement element = (IDOMElement) elements.item(i);

					String textContent = element.getTextContent();

					if (textContent != null && value.trim().equals(textContent.trim())) {

						int startOffset = element.getStartOffset();
						int endOffset = element.getEndOffset();
						int startLine = document.getStructuredDocument().getLineOfOffset(startOffset) + 1;
						int endLine = document.getStructuredDocument().getLineOfOffset(endOffset) + 1;

						SearchResult result = new SearchResult(_file, "startOffset:" + startOffset, startOffset,
								endOffset, startLine, endLine, true);

						_results.add(result);
					}
				}
			}

		} catch (Exception e) {
		}
		finally {
			if (domModel != null) {
				domModel.releaseFromRead();
			}
		}

		return _results;
	}


}