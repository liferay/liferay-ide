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

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.AutoFileMigratorException;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JSPFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public abstract class JSPTagMigrator extends AbstractFileMigrator<JSPFile> implements AutoFileMigrator {

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
		_class = getClass();
	}

	public JSPTagMigrator(
		String[] attrNames, String[] newAttrNames, String[] attrValues, String[] newAttrValues, String[] tagNames,
		String[] newTagNames, String[] tagContents) {

		this(attrNames, newAttrNames, attrValues, newAttrValues, tagNames, newTagNames);

		_tagContents = tagContents;
	}

	@Override
	@SuppressWarnings("deprecation")
	public int correctProblems(File file, Collection<UpgradeProblem> upgradeProblems) throws AutoFileMigratorException {
		int corrected = 0;

		List<Integer> autoCorrectTagOffsets = new ArrayList<>();

		Stream<UpgradeProblem> stream = upgradeProblems.stream();

		Class<? extends JSPTagMigrator> class1 = getClass();

		String autoCorrectContext = "jsptag:" + class1.getName();

		stream.filter(
			p -> autoCorrectContext.equals(p.getAutoCorrectContext())
		).map(
			p -> p.getStartOffset()
		).sorted();

		for (UpgradeProblem upgradeProblem : upgradeProblems) {
			String upgradeProblemAutoCorrectContext = upgradeProblem.getAutoCorrectContext();

			if ((upgradeProblemAutoCorrectContext != null) &&
				upgradeProblemAutoCorrectContext.equals("jsptag:" + class1.getName())) {

				autoCorrectTagOffsets.add(upgradeProblem.getStartOffset());
			}
		}

		Collections.sort(
			autoCorrectTagOffsets,
			new Comparator<Integer>() {

				@Override
				public int compare(Integer i1, Integer i2) {
					return i2.compareTo(i1);
				}

			});

		if (_isNotEmpty(autoCorrectTagOffsets)) {
			IDOMModel domModel = null;

			try {
				IModelManager modelManager = StructuredModelManager.getModelManager();

				try (InputStream input = Files.newInputStream(Paths.get(file.toURI()), StandardOpenOption.READ)) {
					domModel = (IDOMModel)modelManager.getModelForEdit(file.getAbsolutePath(), input, null);
				}

				List<IDOMElement> elementsToCorrect = new ArrayList<>();

				for (int startOffset : autoCorrectTagOffsets) {
					IndexedRegion region = domModel.getIndexedRegion(startOffset);

					if (region instanceof IDOMElement) {
						IDOMElement element = (IDOMElement)region;

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
					else if (_isNotEmpty(_newTagNames)) {
						String tagName = element.getTagName();

						String newTagName = "";

						for (int i = 0; i < _tagNames.length; i++) {
							if (_tagNames[i].equals(tagName)) {
								newTagName = _newTagNames[i];

								break;
							}
						}

						if (Objects.equals(newTagName, "")) {
							continue;
						}

						NamedNodeMap attributes = element.getAttributes();

						NodeList childNodes = element.getChildNodes();

						Document document = element.getOwnerDocument();

						Element newNode = document.createElement(newTagName);

						String nodeValue = element.getNodeValue();

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

						Node parentNode = element.getParentNode();

						parentNode.replaceChild(newNode, element);

						corrected++;
					}

					domModel.changedModel();
				}

				if (corrected > 0) {
					try (OutputStream output = Files.newOutputStream(
							Paths.get(file.toURI()), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
							StandardOpenOption.DSYNC)) {

						domModel.save(output);
					}
				}
			}
			catch (Exception e) {
				throw new AutoFileMigratorException("Unable to auto-correct", e);
			}
			finally {
				if (domModel != null) {
					domModel.releaseFromEdit();
				}
			}
		}

		return corrected;
	}

	@Override
	public int reportProblems(File file, Collection<UpgradeProblem> upgradeProblems) {
		Path path = new Path(file.getAbsolutePath());

		JSPFile jspFile = createFileService(type, file, path.getFileExtension());

		jspFile.setFile(file);

		return upgradeProblems.stream(
		).map(
			problem -> {
				try {
					jspFile.appendComment(problem.getLineNumber(), problem.getTicket());

					return 1;
				}
				catch (IOException ioe) {
					ioe.printStackTrace(System.err);
				}

				return 0;
			}
		).reduce(
			0, Integer::sum
		);
	}

	@Override
	protected List<FileSearchResult> searchFile(File file, JSPFile jspFileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		for (String tagName : _tagNames) {
			if (_isNotEmpty(_tagNames) && _isEmpty(_attrNames) && _isEmpty(_attrValues) && _isNotEmpty(_tagContents)) {
				for (String tagContent : _tagContents) {
					searchResults.addAll(jspFileChecker.findJSPTags(tagName, tagContent));
				}
			}
			else if (_isNotEmpty(_tagNames) && _isEmpty(_attrNames) && _isEmpty(_attrValues)) {
				searchResults.addAll(jspFileChecker.findJSPTags(tagName));
			}
			else if (_isNotEmpty(_tagNames) && _isNotEmpty(_attrNames) && _isEmpty(_attrValues)) {
				searchResults.addAll(jspFileChecker.findJSPTags(tagName, _attrNames));
			}
			else if (_isNotEmpty(_tagNames) && _isNotEmpty(_attrNames) && _isNotEmpty(_attrValues)) {
				searchResults.addAll(jspFileChecker.findJSPTags(tagName, _attrNames, _attrValues));
			}
		}

		if (_isNotEmpty(_newAttrNames) || _isNotEmpty(_newAttrValues) || _isNotEmpty(_newTagNames)) {
			for (FileSearchResult searchResult : searchResults) {
				searchResult.autoCorrectContext = "jsptag:" + _class.getName();
			}
		}

		return searchResults;
	}

	private boolean _isEmpty(String[] names) {
		if ((names == null) || (names.length == 0)) {
			return true;
		}

		return false;
	}

	private boolean _isNotEmpty(Collection<?> items) {
		if ((items != null) && !items.isEmpty()) {
			return true;
		}

		return false;
	}

	private boolean _isNotEmpty(String[] names) {
		if ((names != null) && (names.length > 0)) {
			return true;
		}

		return false;
	}

	private final String[] _attrNames;
	private final String[] _attrValues;
	private Class<? extends JSPTagMigrator> _class;
	private final String[] _newAttrNames;
	private final String[] _newAttrValues;
	private final String[] _newTagNames;
	private String[] _tagContents;
	private final String[] _tagNames;

}