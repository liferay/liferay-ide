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

package com.liferay.blade.upgrade.liferay70;

import com.liferay.blade.api.AutoMigrateException;
import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.JSPFile;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.SearchResult;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public abstract class JSPTagMigrator extends AbstractFileMigrator<JSPFile> implements AutoMigrator {

	private final String[] _attrNames;
	private final String[] _newAttrNames;
	private final String[] _attrValues;
	private final String[] _newAttrValues;
	private final String[] _tagNames;
	private final String[] _newTagNames;

	public JSPTagMigrator(
		String[] attrNames, String[] newAttrNames, String[] attrValues, String[] newAttrValues, String[] tagNames,
		String[] newTagNames) {

		super(JSPFile.class);

		_attrNames = attrNames;
		_newAttrNames = newAttrNames;
		_attrValues = attrValues;
		_newAttrValues = newAttrValues;
		_tagNames = tagNames;
		_newTagNames = newTagNames;
	}

	@Override
	public int correctProblems(File file, List<Problem> problems) throws AutoMigrateException {
		int corrected = 0;

		List<Integer> autoCorrectTagOffsets = new ArrayList<>();

		Stream<Problem> stream = problems.stream();

		final String autoCorrectContext = "jsptag:" + getClass().getName();

		stream.filter(
			p -> p.autoCorrectContext.equals(autoCorrectContext)
		).map(
			p -> p.getStartOffset()
		).sorted();

		for (Problem problem : problems) {
			if (problem.autoCorrectContext.equals("jsptag:" + getClass().getName())) {
				autoCorrectTagOffsets.add(problem.getStartOffset());
			}
		}

		Collections.sort(autoCorrectTagOffsets, new Comparator<Integer>() {

			@Override
			public int compare(Integer i1, Integer i2) {
				return i2.compareTo(i1);
			}
		});

		IFile jspFile = getJSPFile(file);

		if (autoCorrectTagOffsets.size() > 0) {
			IDOMModel domModel = null;

			try {
				domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit(jspFile);

				List<IDOMElement> elementsToCorrect = new ArrayList<>();

				for (int startOffset : autoCorrectTagOffsets) {
					IndexedRegion region = domModel.getIndexedRegion(startOffset);

					if (region instanceof IDOMElement) {
						IDOMElement element = (IDOMElement) region;

						elementsToCorrect.add(element);
					}
				}

				for (IDOMElement element : elementsToCorrect) {
					domModel.aboutToChangeModel();

					if (_newAttrValues.length == 1) {
						element.setAttribute(_attrNames[0], _newAttrValues[0]);

						corrected++;
					}
					else if (_newAttrNames.length == 1) {
						String value = element.getAttribute(_attrNames[0]);

						element.removeAttribute(_attrNames[0]);

						element.setAttribute(_newAttrNames[0], value);

						corrected++;
					}
					else if (_newTagNames.length > 0) {
						String tagName = element.getTagName();
						NamedNodeMap attributes = element.getAttributes();
						NodeList childNodes = element.getChildNodes();
						String nodeValue = element.getNodeValue();

						String newTagName = "";

						for (int i = 0; i < _tagNames.length; i++) {
							if (_tagNames[i].equals(tagName)) {
								newTagName = _newTagNames[i];

								break;
							}
						}

						Element newNode = element.getOwnerDocument().createElement(newTagName);

						if (nodeValue != null) {
							newNode.setNodeValue(nodeValue);
						}

						for (int i = 0; i < attributes.getLength(); i++) {
							Node attribute = attributes.item(i);

							newNode.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
						}

						for (int i = 0; i < childNodes.getLength(); i++) {
							Node childNode = childNodes.item(i);

							newNode.appendChild(childNode.cloneNode(true));
						}

						element.getParentNode().replaceChild(newNode, element);

						corrected++;
					}

					domModel.changedModel();

					domModel.save();
				}
			}
			catch (Exception e) {
				throw new AutoMigrateException("Unable to auto-correct", e);
			}
			finally {
				if (domModel != null) {
					domModel.releaseFromEdit();
				}
			}

			if (corrected > 0 && !jspFile.getLocation().toFile().equals(file)) {
				try (InputStream jspFileContent = jspFile.getContents()) {
					Files.copy(jspFileContent, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
				catch (Exception e) {
					throw new AutoMigrateException("Error writing corrected file.", e);
				}
			}
		}

		return corrected;
	}

	@Override
	protected List<SearchResult> searchFile(File file, JSPFile jspFileChecker) {
		List<SearchResult> searchResults = new ArrayList<SearchResult>();

		for (String tagName : _tagNames) {
			List<SearchResult> jspTagResults = new ArrayList<SearchResult>();

			if ((_tagNames.length > 0) && (_attrNames.length == 0) && (_attrValues.length == 0)) {
				jspTagResults = jspFileChecker.findJSPTags(tagName);
			}
			else if ((_tagNames.length > 0) && (_attrNames.length > 0) && (_attrValues.length == 0)) {
				jspTagResults = jspFileChecker.findJSPTags(tagName, _attrNames);
			}
			else if ((_tagNames.length > 0) && (_attrNames.length > 0) && (_attrValues.length > 0)) {
				jspTagResults = jspFileChecker.findJSPTags(tagName, _attrNames, _attrValues);
			}

			if (!jspTagResults.isEmpty()) {
				searchResults.addAll(jspTagResults);
			}
		}

		if (_newAttrNames.length > 0 || _newAttrValues.length > 0 || _newTagNames.length > 0) {
			for (SearchResult searchResult : searchResults) {
				searchResult.autoCorrectContext = "jsptag:" + getClass().getName();
			}
		}

		return searchResults;
	}

	protected IFile getJSPFile(File file) {
		final JSPFile jspFileService = _context.getService(_context.getServiceReference(JSPFile.class));

		return jspFileService.getIFile(file);
	}

}
