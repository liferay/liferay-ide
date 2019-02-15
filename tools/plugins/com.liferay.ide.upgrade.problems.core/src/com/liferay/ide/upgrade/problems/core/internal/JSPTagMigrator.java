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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrateException;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileSearchResult;
import com.liferay.ide.upgrade.problems.core.JSPFile;

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

	@Override
	public int correctProblems(File file, List<UpgradeProblem> upgradeProblems) throws AutoFileMigrateException {
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

		IFile jspFile = getJSPFile(file);

		if (ListUtil.isNotEmpty(autoCorrectTagOffsets)) {
			IDOMModel domModel = null;

			try {
				IModelManager modelManager = StructuredModelManager.getModelManager();

				domModel = (IDOMModel)modelManager.getModelForEdit(jspFile);

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
					else if (ListUtil.isNotEmpty(_newTagNames)) {
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

						if (newTagName.equals("")) {
							continue;
						}

						Document document = element.getOwnerDocument();

						Element newNode = document.createElement(newTagName);

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

					domModel.save();
				}
			}
			catch (Exception e) {
				throw new AutoFileMigrateException("Unable to auto-correct", e);
			}
			finally {
				if (domModel != null) {
					domModel.releaseFromEdit();
				}
			}

			File jsp = FileUtil.getFile(jspFile);

			if ((corrected > 0) && !jsp.equals(file)) {
				try (InputStream jspFileContent = jspFile.getContents()) {
					Files.copy(jspFileContent, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
				catch (Exception e) {
					throw new AutoFileMigrateException("Error writing corrected file", e);
				}
			}
		}

		return corrected;
	}

	protected IFile getJSPFile(File file) {
		JSPFile jspFileService = context.getService(context.getServiceReference(JSPFile.class));

		return jspFileService.getIFile(file);
	}

	@Override
	protected List<FileSearchResult> searchFile(File file, JSPFile jspFileChecker) {
		List<FileSearchResult> searchResults = new ArrayList<>();

		for (String tagName : _tagNames) {
			if (ListUtil.isNotEmpty(_tagNames) && ListUtil.isEmpty(_attrNames) && ListUtil.isEmpty(_attrValues)) {
				searchResults.addAll(jspFileChecker.findJSPTags(tagName));
			}
			else if (ListUtil.isNotEmpty(_tagNames) && ListUtil.isNotEmpty(_attrNames) &&
					 ListUtil.isEmpty(_attrValues)) {

				searchResults.addAll(jspFileChecker.findJSPTags(tagName, _attrNames));
			}
			else if (ListUtil.isNotEmpty(_tagNames) && ListUtil.isNotEmpty(_attrNames) &&
					 ListUtil.isNotEmpty(_attrValues)) {

				searchResults.addAll(jspFileChecker.findJSPTags(tagName, _attrNames, _attrValues));
			}
		}

		if (ListUtil.isNotEmpty(_newAttrNames) || ListUtil.isNotEmpty(_newAttrValues) ||
			ListUtil.isNotEmpty(_newTagNames)) {

			for (FileSearchResult searchResult : searchResults) {
				searchResult.autoCorrectContext = "jsptag:" + _class.getName();
			}
		}

		return searchResults;
	}

	private final String[] _attrNames;
	private final String[] _attrValues;
	private Class<? extends JSPTagMigrator> _class;
	private final String[] _newAttrNames;
	private final String[] _newAttrValues;
	private final String[] _newTagNames;
	private final String[] _tagNames;

}