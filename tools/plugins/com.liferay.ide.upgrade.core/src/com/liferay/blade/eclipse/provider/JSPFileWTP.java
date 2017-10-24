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

import com.liferay.blade.api.CUCache;
import com.liferay.blade.api.JSPFile;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extension=jsp"
	},
	service = {
		JavaFile.class,
		JSPFile.class
	}
)
@SuppressWarnings({ "rawtypes", "restriction" })
public class JSPFileWTP extends JavaFileJDT implements JSPFile {

	public JSPFileWTP() {
	}

	public JSPFileWTP(File file) {
		super(file);
	}

	@Override
	protected SearchResult createSearchResult(String searchContext, int startOffset, int endOffset,
			int startLine, int endLine, boolean fullMatch) {

		IDOMModel jspModel = null;

		try {
			final int jspStartOffset = _translation.getJspOffset(startOffset);
			final int jspEndOffset = _translation.getJspOffset(endOffset);

			jspModel = (IDOMModel) StructuredModelManager.getModelManager()
					.getModelForRead(_translation.getJspFile());
			final IDOMDocument domDocument = jspModel.getDocument();

			final IStructuredDocument structuredDocument = domDocument
					.getStructuredDocument();
			final int jspStartLine = structuredDocument
					.getLineOfOffset(jspStartOffset) + 1;
			final int jspEndLine = structuredDocument
					.getLineOfOffset(jspEndOffset) + 1;

			return super.createSearchResult(searchContext, jspStartOffset, jspEndOffset,
					jspStartLine, jspEndLine, fullMatch);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jspModel != null) {
				jspModel.releaseFromRead();
			}
		}

		return super.createSearchResult(searchContext, startOffset, endOffset, startLine,
				endLine, fullMatch);
	}

	@Override
	protected char[] getJavaSource() {
		return _translation.getJavaText().toCharArray();
	}

	@Override
	public List<SearchResult> findJSPTags(String tagName) {
		if (tagName == null || tagName.isEmpty()) {
			throw new IllegalArgumentException("tagName can not be null or empty");
		}

		final List<SearchResult> searchResults = new ArrayList<>();

		final NodeList nodeList = getTagNodes(tagName);

		for (int i = 0; i < nodeList.getLength(); i++) {
			final IDOMNode domNode = (IDOMNode) nodeList.item(i);

			int startOffset = domNode.getStartOffset();
			int endOffset = domNode.getEndOffset();
			int jspStartLine = getJspLine(startOffset);
			int jspEndLine = getJspLine(endOffset);

			searchResults.add(super.createSearchResult(null, startOffset, endOffset, jspStartLine, jspEndLine, true));
		}

		return searchResults;
	}

	@Override
	public List<SearchResult> findJSPTags(String tagName, String[] attrNames) {
		if (tagName == null || tagName.isEmpty() || attrNames == null || attrNames.length == 0) {
			throw new IllegalArgumentException("tagName can not be null or empty");
		}

		final List<SearchResult> searchResults = new ArrayList<>();

		final NodeList nodeList = getTagNodes(tagName);

		for (int i = 0; i < nodeList.getLength(); i++) {
			final IDOMNode domNode = (IDOMNode) nodeList.item(i);

			for (String attrName : attrNames) {
				final IDOMNode attrNode = (IDOMNode) domNode.getAttributes().getNamedItem(attrName);

				if (attrNode != null) {
					int startOffset = attrNode.getStartOffset();
					int endOffset = startOffset + attrName.length();
					int jspStartLine = getJspLine(startOffset);
					int jspEndLine = getJspLine(endOffset);

					searchResults.add(
						super.createSearchResult(null, startOffset, endOffset, jspStartLine, jspEndLine, true));
				}
			}
		}

		return searchResults;
	}

	@Override
	public List<SearchResult> findJSPTags(String tagName, String[] attrNames, String[] attrValues) {
		if (tagName == null || tagName.isEmpty() || attrNames == null || attrNames.length == 0 || attrValues == null
				|| attrValues.length == 0) {
			throw new IllegalArgumentException("tagName can not be null or empty");
		}

		if (attrNames.length != attrValues.length) {
			throw new IllegalArgumentException("If attrValues is specified it must match the attrNames array in lengh");
		}

		final List<SearchResult> searchResults = new ArrayList<>();

		final NodeList nodeList = getTagNodes(tagName);

		for (int i = 0; i < nodeList.getLength(); i++) {
			final IDOMNode domNode = (IDOMNode) nodeList.item(i);

			for (int j = 0; j < attrNames.length; j++) {
				final IDOMNode attrNode = (IDOMNode) domNode.getAttributes().getNamedItem(attrNames[j]);

				if (attrNode != null) {
					if (attrValues != null && !(attrValues[j].equals(attrNode.getNodeValue()))) {
						continue;
					}

					int startOffset = attrNode.getStartOffset() + attrNames[j].length() + 2;
					int endOffset = startOffset + attrValues[j].length();
					int jspStartLine = getJspLine(startOffset);
					int jspEndLine = getJspLine(endOffset);

					SearchResult searchResult =
						super.createSearchResult(null, startOffset, endOffset, jspStartLine, jspEndLine, true);

					searchResults.add(searchResult);
				}
			}
		}

		return searchResults;
	}

	private int getJspLine(int offset) {
		final IFile jspFile = _translation.getJspFile();

		IDOMModel jspModel = null;
		IDOMDocument domDocument = null;

		try {
			jspModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead(jspFile);

			domDocument = jspModel.getDocument();

			return domDocument.getStructuredDocument().getLineOfOffset(offset) + 1;
		}
		catch (IOException | CoreException e) {
			e.printStackTrace();
		}
		finally {
			if (jspModel != null) {
				jspModel.releaseFromRead();
			}
		}

		return 0;
	}

	private NodeList getTagNodes(String tagName) {
		final IFile jspFile = _translation.getJspFile();

		IDOMModel jspModel = null;
		IDOMDocument domDocument = null;

		try {
			jspModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead(jspFile);

			domDocument = jspModel.getDocument();

			return domDocument.getElementsByTagName(tagName);
		}
		catch (IOException | CoreException e) {
			e.printStackTrace();
		}
		finally {
			if (jspModel != null) {
				jspModel.releaseFromRead();
			}
		}

		return null;
	}

	@Override
	public void setFile(File file) {
		try {
			final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

			final Collection<ServiceReference<CUCache>> sr = context.getServiceReferences(CUCache.class, "(type=jsp)");

			ServiceReference<CUCache> ref = sr.iterator().next();

			@SuppressWarnings("unchecked")
			CUCache<JSPTranslationPrime> cache = context.getService(ref);

			_translation = cache.getCU(file, null);
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		super.setFile(file);
	}

	private JSPTranslationPrime _translation;

}
